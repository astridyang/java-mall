package com.example.order.web;

import com.alipay.api.AlipayApiException;
import com.example.order.config.AlipayTemplate;
import com.example.order.service.OrderService;
import com.example.order.vo.PayVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author sally
 * @date 2022-11-07 10:59
 */
@Controller
public class PayController {

    @Resource
    AlipayTemplate alipayTemplate;

    @Resource
    OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo vo = orderService.getPayOrder(orderSn);
        return alipayTemplate.pay(vo);
    }
}
