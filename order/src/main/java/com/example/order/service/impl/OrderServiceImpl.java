package com.example.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.constant.OrderMq;
import com.example.common.to.mq.OrderTo;
import com.example.common.to.mq.SeckillOrderTo;
import com.example.common.utils.R;
import com.example.common.vo.MemberResVo;
import com.example.order.constant.OrderConstant;
import com.example.order.entity.OrderItemEntity;
import com.example.order.entity.PaymentInfoEntity;
import com.example.order.entity.SpuInfoEntity;
import com.example.order.enume.OrderStatusEnum;
import com.example.order.feign.CartFeignService;
import com.example.order.feign.MemberFeignService;
import com.example.order.feign.ProductFeignService;
import com.example.order.feign.WareFeignService;
import com.example.order.interceptor.LoginInterceptor;
import com.example.order.service.OrderItemService;
import com.example.order.service.PaymentInfoService;
import com.example.order.to.OrderCreateTo;
import com.example.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.order.dao.OrderDao;
import com.example.order.entity.OrderEntity;
import com.example.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
	private ThreadLocal<OrderSubmitVo> threadLocal = new ThreadLocal<>();
	@Resource
	MemberFeignService memberFeignService;

	@Resource
	CartFeignService cartFeignService;

	@Resource
	ThreadPoolExecutor executor;

	@Resource
	WareFeignService wareFeignService;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	ProductFeignService productFeignService;

	@Resource
	OrderItemService orderItemService;

	@Resource
	RabbitTemplate rabbitTemplate;

	@Resource
	PaymentInfoService paymentInfoService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<OrderEntity> page = this.page(
				new Query<OrderEntity>().getPage(params),
				new QueryWrapper<OrderEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
		OrderConfirmVo confirmVo = new OrderConfirmVo();
		MemberResVo member = LoginInterceptor.threadLocal.get();
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
			// 1.远程查询所有收获地址列表
			RequestContextHolder.setRequestAttributes(attributes);
			List<MemberAddressVo> address = memberFeignService.getMemberAddress(member.getId());
			confirmVo.setAddress(address);
		}, executor);

		CompletableFuture<Void> getCartFuture = CompletableFuture.runAsync(() -> {
			// 2.远程查询购物车所有选中的购物项
			RequestContextHolder.setRequestAttributes(attributes);
			List<OrderItemVo> cartItems = cartFeignService.getCartItems();
			confirmVo.setItems(cartItems);
		}, executor).thenRunAsync(() -> {
			// query stock
			List<OrderItemVo> items = confirmVo.getItems();
			List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
			R r = wareFeignService.hasStock(collect);
			List<SkuHasStockTo> data = r.getData(new TypeReference<List<SkuHasStockTo>>() {
			});
			Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
			confirmVo.setStocks(map);
		}, executor);


		CompletableFuture.allOf(getAddressFuture, getCartFuture).get();
		// 3.查询用户积分
		Integer integration = member.getIntegration();
		confirmVo.setIntegration(integration);

		// 4.金额自动计算

		// 5.防重令牌
		String token = UUID.randomUUID().toString().replaceAll("-", "");
		stringRedisTemplate.opsForValue().set(OrderConstant.ORDER_TOKEN_PREFIX + member.getId(), token, 30, TimeUnit.MINUTES);
		confirmVo.setOrderToken(token);

		return confirmVo;
	}

	@Transactional // 本地事务
	@Override
	public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
		SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
		threadLocal.set(vo);
		responseVo.setCode(0);
		MemberResVo memberResVo = LoginInterceptor.threadLocal.get();
		String tokenKey = OrderConstant.ORDER_TOKEN_PREFIX + memberResVo.getId();
		// 1.验证令牌
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		String orderToken = vo.getOrderToken();
		// 原子验证令牌和删除令牌
		Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Collections.singletonList(tokenKey), orderToken);
		if (result == 0L) {
			// 验证失败
			responseVo.setCode(1);
			return responseVo;
		} else {
			// 2.验价
			OrderCreateTo order = createOrder();
			BigDecimal payAmount = order.getOrder().getPayAmount();
			BigDecimal payPrice = vo.getPayPrice();
			if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
				// 3.保存订单
				saveOrder(order);

				// 4.锁库存，只要有异常，回滚订单数据
				WareSkuLockVo lockVo = new WareSkuLockVo();
				lockVo.setOrderSn(order.getOrder().getOrderSn());
				List<OrderItemVo> locks = order.getItems().stream().map(item -> {
					OrderItemVo orderItemVo = new OrderItemVo();
					orderItemVo.setSkuId(item.getSkuId());
					orderItemVo.setCount(item.getSkuQuantity());
					orderItemVo.setTitle(item.getSkuName());
					return orderItemVo;
				}).collect(Collectors.toList());
				lockVo.setLocks(locks);
				// TODO 调用远程服务锁定库存
				R r = wareFeignService.orderLockStock(lockVo);
				if (r.getCode() == 0) {
					// 锁成功
					responseVo.setOrder(order.getOrder());
					// int i = 10/0;
					// 订单创建成功发送消息给mq
					OrderTo orderTo = new OrderTo();
					BeanUtils.copyProperties(order.getOrder(), orderTo);
					rabbitTemplate.convertAndSend(OrderMq.ORDER_EVENT_EXCHANGE, OrderMq.ORDER_CREATE_KEY, orderTo);
					return responseVo;
				} else {
					// responseVo.setCode(3);
					// return responseVo;
					String msg = (String) r.get("msg");
					// todo
					// throw new NoStockException(msg);
					throw new RuntimeException(msg);
				}
			} else {
				responseVo.setCode(2);
				return responseVo;
			}
		}
	}

	@Override
	public OrderEntity getOrderByOrderSn(String orderSn) {
		return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
	}

	@Override
	public void closeOrder(OrderTo orderEntity) {
		OrderEntity entity = this.getById(orderEntity.getId());
		if (Objects.equals(entity.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
			// 关闭订单
			OrderEntity update = new OrderEntity();
			update.setId(entity.getId());
			update.setStatus(OrderStatusEnum.CANCLED.getCode());
			this.updateById(update);
			// 发给mq一个解锁库存消息
			OrderTo orderTo = new OrderTo();
			BeanUtils.copyProperties(entity, orderTo);
			try {
				// TODO 每一个消息都保存到数据库，定期扫描数据库将失败的消息再发送一遍
				rabbitTemplate.convertAndSend(OrderMq.ORDER_EVENT_EXCHANGE, OrderMq.ORDER_RELEASE_OTHER_KEY, orderTo);
			} catch (Exception e) {

			}
		}
	}

	@Override
	public PayVo getPayOrder(String orderSn) {
		PayVo payVo = new PayVo();
		OrderEntity order = this.getOrderByOrderSn(orderSn);
		BigDecimal payAmount = order.getPayAmount().setScale(2, RoundingMode.UP);
		payVo.setTotal_amount(payAmount.toString());
		payVo.setOut_trade_no(order.getOrderSn());

		List<OrderItemEntity> list = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
		OrderItemEntity orderItemEntity = list.get(0);
		payVo.setSubject(orderItemEntity.getSkuName());
		payVo.setBody(orderItemEntity.getSkuName());
		return payVo;
	}

	@Override
	public PageUtils queryPageWithItem(Map<String, Object> params) {
		MemberResVo member = LoginInterceptor.threadLocal.get();
		IPage<OrderEntity> page = this.page(
				new Query<OrderEntity>().getPage(params),
				new QueryWrapper<OrderEntity>().eq("member_id", member.getId()).orderByDesc("id")
		);
		List<OrderEntity> orderEntities = page.getRecords().stream().peek(order -> {
			List<OrderItemEntity> itemList = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
			order.setOrderItemEntityList(itemList);
		}).collect(Collectors.toList());
		page.setRecords(orderEntities);
		return new PageUtils(page);
	}

	@Override
	public String handleAlipayResult(PayAsyncVo vo) {

		// 记录支付流水
		PaymentInfoEntity infoEntity = new PaymentInfoEntity();
		infoEntity.setOrderSn(vo.getOut_trade_no());
		infoEntity.setAlipayTradeNo(vo.getTrade_no());
		infoEntity.setCallbackTime(vo.getNotify_time());
		infoEntity.setPaymentStatus(vo.getTrade_status());

		paymentInfoService.save(infoEntity);
		if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
			String orderSn = vo.getOut_trade_no();
			this.updateOrderStatus(orderSn, OrderStatusEnum.PAYED.getCode());
		}
		return "success";
	}

	@Override
	public void seckillOrder(SeckillOrderTo order) {
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setOrderSn(order.getOrderSn());
		BigDecimal multiply = order.getSeckillPrice().multiply(new BigDecimal(order.getNum()));
		orderEntity.setPayAmount(multiply);
		orderEntity.setMemberId(order.getMemberId());
		orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
		this.save(orderEntity);

		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setOrderSn(order.getOrderSn());
		orderItem.setRealAmount(multiply);
		orderItem.setSkuQuantity(order.getNum());
		orderItemService.save(orderItem);

	}

	private void updateOrderStatus(String orderSn, Integer code) {
		this.baseMapper.updateOrderStatus(orderSn, code);
	}


	private void saveOrder(OrderCreateTo order) {
		OrderEntity orderEntity = order.getOrder();
		orderEntity.setModifyTime(new Date());
		this.save(orderEntity);

		List<OrderItemEntity> items = order.getItems();
		orderItemService.saveBatch(items);
	}

	private OrderCreateTo createOrder() {
		OrderCreateTo orderCreateTo = new OrderCreateTo();
		String orderSn = IdWorker.getTimeId();
		OrderEntity order = buildOrder(orderSn);
		List<OrderItemEntity> items = buildOrderItems(orderSn);

		// compute price and amount
		computePrice(order, items);

		orderCreateTo.setOrder(order);
		orderCreateTo.setItems(items);
		return orderCreateTo;

	}

	private void computePrice(OrderEntity order, List<OrderItemEntity> items) {
		BigDecimal total = new BigDecimal("0");
		BigDecimal gift = new BigDecimal("0");
		BigDecimal growth = new BigDecimal("0");
		for (OrderItemEntity item : items) {
			total = total.add(item.getRealAmount());
			gift = gift.add(new BigDecimal(item.getGiftIntegration().toString()));
			growth = gift.add(new BigDecimal(item.getGiftGrowth().toString()));
		}
		order.setTotalAmount(total);
		order.setPayAmount(total.add(order.getFreightAmount()));

		order.setGrowth(growth.intValue());
		order.setIntegration(gift.intValue());
	}

	private OrderEntity buildOrder(String orderSn) {
		MemberResVo member = LoginInterceptor.threadLocal.get();
		OrderEntity order = new OrderEntity();
		order.setMemberId(member.getId());
		order.setOrderSn(orderSn);
		// 远程获取收货地址和运费
		OrderSubmitVo vo = threadLocal.get();
		R r = wareFeignService.getFare(vo.getAddrId());
		FareVo fareVo = r.getData(new TypeReference<FareVo>() {
		});
		order.setFreightAmount(fareVo.getFare());
		MemberAddressVo address = fareVo.getAddress();
		order.setReceiverName(address.getName());
		order.setReceiverPhone(address.getPhone());
		order.setReceiverProvince(address.getProvince());
		order.setReceiverCity(address.getCity());
		order.setReceiverRegion(address.getRegion());
		order.setReceiverDetailAddress(address.getDetailAddress());
		order.setReceiverPostCode(address.getPostCode());

		order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
		order.setAutoConfirmDay(7);
		order.setDeleteStatus(0);
		return order;
	}

	private List<OrderItemEntity> buildOrderItems(String orderSn) {
		List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
		List<OrderItemVo> cartItems = cartFeignService.getCartItems();
		if (cartItems != null && cartItems.size() > 0) {
			orderItemEntityList = cartItems.stream().map(item -> {
				OrderItemEntity orderItemEntity = buildOrderItem(item);
				orderItemEntity.setOrderSn(orderSn);
				return orderItemEntity;
			}).collect(Collectors.toList());
		}
		return orderItemEntityList;
	}

	private OrderItemEntity buildOrderItem(OrderItemVo item) {
		OrderItemEntity orderItem = new OrderItemEntity();
		// set order info
		// set spu info
		R r = productFeignService.getSpuBySkuId(item.getSkuId());
		SpuInfoEntity spu = r.getData(new TypeReference<SpuInfoEntity>() {
		});
		orderItem.setSpuId(spu.getId());
		orderItem.setSpuBrand(spu.getBrandId().toString());
		orderItem.setSpuName(spu.getSpuName());
		orderItem.setCategoryId(spu.getCatalogId());
		// set sku info
		orderItem.setSkuId(item.getSkuId());
		orderItem.setSkuName(item.getTitle());
		orderItem.setSkuPic(item.getImage());
		orderItem.setSkuPrice(item.getPrice());
		orderItem.setSkuQuantity(item.getCount());
		String s = StringUtils.collectionToDelimitedString(item.getSkuAttr(), ";");
		orderItem.setSkuAttrsVals(s);
		// 积分信息
		BigDecimal growth = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
		orderItem.setGiftGrowth(growth.intValue());
		orderItem.setGiftIntegration(growth.intValue());
		// real amount
		BigDecimal origin = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuQuantity().toString()));
		orderItem.setRealAmount(origin);

		return orderItem;
	}


}