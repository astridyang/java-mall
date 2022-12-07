package com.example.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConfig;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.order.config.AlipayTemplate;
import com.example.order.service.OrderService;
import com.example.order.vo.PayAsyncVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author sally
 * @date 2022-11-07 16:33
 */
@RestController
public class OrderPayLister {

	@Resource
	OrderService orderService;

	@Resource
	AlipayTemplate alipayTemplate;

	@PostMapping("/payed/notify")
	public String handleAlipayed(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException {
		// Map<String, String[]> map = request.getParameterMap();
		System.out.println("支付宝回调通知：");
		// System.out.println("vo = " + vo);
		// for (String key : map.keySet()) {
		// 	System.out.println("map.get(key) = " + Arrays.toString(map.get(key)));
		// }

		// 验签

		// batlgg6147@sandbox.com
		// return "success";

		//获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
				alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名

		if (signVerified) {
			System.out.println("验签成功");
			return orderService.handleAlipayResult(vo);
		} else {
			System.out.println("验签失败");
			return "error";
		}


	}
}
