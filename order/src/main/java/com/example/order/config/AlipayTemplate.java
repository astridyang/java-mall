package com.example.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

	//在支付宝创建的应用的id
	private String app_id = "2021000121684809";

	// 商户私钥，您的PKCS8格式RSA2私钥
	private String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCVXSpLDChz1ywx1caAttAT50KjZUMHOcxMrgR3Bc/SIy3NMRj8IehbBve3YbKM9MPBIwIfdglGJfk2scZT4TqkUObcfONuaxTWz9ocWz1aPE87cBST6IzrgprRg8Yb+1FbI9X/MiPfuH3sfpxU1n9Z22lC514rMYV/D5XecbLhM0r83ZQAEABZuDV4+vtaqyTRq2c6vYbgk9GwaUjj0gSQ1hY63pogfqTl2qlJbZZ7N0efaeXuCX7qCbeUSxXsfH8AkLpb0ssmFuGQ4sHcLGykkftrIuwxhNGm/q6O2jmicX7qKSXp4MPUTpI60FWEhIpDoMSdKxM9KbmnA5TIEfz9AgMBAAECggEAaa7g9PbmTIisIZrT+FDhy6NvGDPXCG7R6lOanyjCkjoH907NEeFgCcBVWDDNaETrmWWO4/ndt/+4ZQXGgaU0m/0OTGYlegRUW2X4OuYn9AgGO9UjCkCaLS1dI++sJjJdrzIdAZNVpL9iggpXzQN0Wn0P7qHMlJ1Wir/ZV+p1GjfVfP98e/yOGVEwEdOSHiFQf7OBgYfs7xzxNcNvvY2AGZ7xKLNyvr8YCR6IMPoF1HPqgW4XjqaC9Nq/OzmnhA+Twew+LSMzPZCKO79IGdHeDjn6hyFbtgQUMNtRthGQ2H5o3y29ghT5AxnX6U9G/6DWYxG7MCRqPLur5cumAZ/WwQKBgQD+TLsA0PyoiLRph+m1HGVLcyAFHRnWcglUpEiiO0CTqhTlEtkxp2b3gwuiUe+xM00PvsXOGptNdpf8UOp5KeT6DWIBlWId7gYF0Vj4Gfl2FzVSlGBuFANNK4LcN4Pvh3FSQKreWLGNqSdnN8fMzfVPGU+Blqv8bNohpoW71+xYMQKBgQCWXNKLWPBRvSfDBpKTJ1Zm9Vpp4K7ds4XPhzPIbyKSJXVcax8tHQwAkhF2JSnMzK71mu71kN9i1khonPIbKG56wYeG7qgDkzEM+BbgMbH0zTbONTbA9pd7swTeo51sj02+Yg1Zmq1nLkzzmY3QGJ/b/p7ZI6MIQTy3DDbrLUuKjQKBgCMwoQwa32u/ByA3Y5rhZA8NzleG0GWBgFMLLNXuVyBX/+43LoEVhkUoCDsdFYsy6y4LmznLKsSMbbnHxLqhloMXyQEpF9vGbWTutF4Y0hGKYqGb0MBK2q4aIFCzCfnih0b3le2Q3nkY+E/rQyXPNmwMt5jaLdCPi5vufEEjPxxxAoGBAISkwf+3p9kTuv3q0GcMLsaI7wez888UTZjztpS8pYyzisLKpSgRFcNzGABXbHaEolt8IA11jfsM+bd/rHyUwFF+ZGZ+Jq1wrba5LMT+UfSZDB4BB5pWbu/qzwLw5zEbggPPT+OZ9hqt16iV72qCLpPOHKeFLo9brYn1NoVk4d0JAoGBAMQ8kAukXpJSKXnSmvQnq0662Yj5Ed7mS8haFmYeL5tXO/2lcPcm6rSsGSoJiUUaLJJilzx8acUv9llMzrIoHKpNk+BbbO6lnB1fI7FQhvd7axyPp8s5YEIPgRMcX3gp+3b8YqTc0fBKUxkAeYhZlnHkfZ95ksvqJVJQO8cZ2BmR";
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
	private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4P8y6USEAABVTvpt91kK/UKVDhcUXNdxyqZqTBe5OJ5G/ze6keCsTceEimBBjO25L6ZjbyIUDcCiQMoRELnAJpR7iDVFKjG/4J8DVClkPk2/xnsLqFIQICuKNTnXWqZjC5huk3hvhQu9naA9iaDZINkXS8I/FV/fQ5WLTFF+5vN/AfD++WzzA0ce/C1EQgozfEbGLK5+5LbJBZN2bUpC1VLxrdSLw26qfVA++ncRgsxq1BXPXNmsaZC6AaK+tpr8RIqcteOCBq+ZTxBEMPIap1IzvGZlmQeCMHjpwbEHLyv1JKXk/hv9UA9msTLjLMcD8lbFtSLKHO646OoPe1oFKwIDAQAB";
	// 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	// 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
	private String notify_url = "http://120efc83.nat123.fun/payed/notify";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	//同步通知，支付成功，一般跳转到成功页
	private String return_url = "http://member.gmall.com/memberOrder.html";

	// 签名方式
	private String sign_type = "RSA2";

	// 字符编码格式
	private String charset = "utf-8";

	// 支付宝网关； https://openapi.alipaydev.com/gateway.do
	private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

	public String pay(PayVo vo) throws AlipayApiException {

		//AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
		//1、根据支付宝的配置生成一个支付客户端
		AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
				app_id, merchant_private_key, "json",
				charset, alipay_public_key, sign_type);

		//2、创建一个支付请求 //设置请求参数
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(return_url);
		alipayRequest.setNotifyUrl(notify_url);

		//商户订单号，商户网站订单系统中唯一订单号，必填
		String out_trade_no = vo.getOut_trade_no();
		//付款金额，必填
		String total_amount = vo.getTotal_amount();
		//订单名称，必填
		String subject = vo.getSubject();
		//商品描述，可空
		String body = vo.getBody();

		// 支付超时收单时间
		String timeout = "30m";

		alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
				+ "\"total_amount\":\"" + total_amount + "\","
				+ "\"subject\":\"" + subject + "\","
				+ "\"body\":\"" + body + "\","
				+ "\"timeout_express\":\"" + timeout + "\","
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

		String result = alipayClient.pageExecute(alipayRequest).getBody();

		//会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
		System.out.println("支付宝的响应：" + result);

		return result;

	}
}
