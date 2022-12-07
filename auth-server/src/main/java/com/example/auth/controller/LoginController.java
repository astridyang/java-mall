package com.example.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.example.auth.feign.MemberFeignService;
import com.example.auth.feign.ThirdPartyFeignService;
import com.example.auth.vo.LoginVo;
import com.example.common.vo.MemberResVo;
import com.example.auth.vo.RegisterVo;
import com.example.common.constant.AuthServerConstant;
import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author sally
 * @date 2022-10-09 16:21
 */
@Controller
public class LoginController {

	@Resource
	ThirdPartyFeignService thirdPartyFeignService;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	MemberFeignService memberFeignService;

	@ResponseBody
	@GetMapping("/sms/sendCode")
	public R sendCode(@RequestParam("phone") String phone) {
		// TODO 接口防刷
		String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		if (ops.get(key) != null) {
			long sendTime = Long.parseLong(Objects.requireNonNull(ops.get(key)).split("_")[1]);
			// 60s 内只能发一次
			if (System.currentTimeMillis() - sendTime < 60000) {
				return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
			}
		}
		String code = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
		R r = thirdPartyFeignService.sentCode(phone, code);
		if (r.getCode() == 0) {
			ops.set(key, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);
			return R.ok();
		} else {
			return R.error(r.getCode(), "send code failed");
		}

	}

	@GetMapping("/login.html")
	public String loginPage(HttpSession session) {
		if (session.getAttribute(AuthServerConstant.LOGIN_USER) != null) {
			return "redirect://gmall.com";
		} else {
			return "login";
		}
	}

	@PostMapping("/login")
	public String login(LoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
		R r = memberFeignService.login(vo);
		if (r.getCode() == 0) {
			MemberResVo user = r.getData("data", new TypeReference<MemberResVo>() {
			});
			session.setAttribute(AuthServerConstant.LOGIN_USER, user);
			return "redirect://gmall.com";
		} else {
			HashMap<String, String> errors = new HashMap<>();
			errors.put("msg", r.getMsg());
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect://auth.gmall.com/login.html";
		}
	}

	@PostMapping("/register")
	public String register(@Valid RegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect://auth.gmall.com/reg.html";
		} else {
			// 检查验证码
			String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone();
			String s = stringRedisTemplate.opsForValue().get(key);
			if (StringUtils.hasLength(s)) {
				String code = s.split("_")[0];
				if (vo.getCode().equals(code)) {
					// 删除验证码
					stringRedisTemplate.delete(key);
					// 调用远程member接口注册
					R r = memberFeignService.register(vo);
					if (r.getCode() == 0) {
						return "redirect://auth.gmall.com/login.html";
					} else {
						HashMap<String, String> errors = new HashMap<>();
						errors.put("msg", r.getMsg());
						redirectAttributes.addFlashAttribute("errors", errors);
						return "redirect://auth.gmall.com/reg.html";
					}

				} else {
					HashMap<String, String> errors = new HashMap<>();
					errors.put("code", "incorrect code");
					redirectAttributes.addFlashAttribute("errors", errors);
					return "redirect://auth.gmall.com/reg.html";
				}
			} else {
				HashMap<String, String> errors = new HashMap<>();
				errors.put("code", "incorrect code");
				redirectAttributes.addFlashAttribute("errors", errors);
				return "redirect://auth.gmall.com/reg.html";
			}


		}
	}
}
