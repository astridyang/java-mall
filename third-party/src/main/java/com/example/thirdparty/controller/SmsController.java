package com.example.thirdparty.controller;

import com.example.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sally
 * @date 2022-10-10 9:31
 */
@Slf4j
@RestController
@RequestMapping("/sms")
public class SmsController {

	@GetMapping("/sendCode")
	public R sentCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
		log.info("send code: {} to phone success: {}", code, phone);
		return R.ok();
	}
}
