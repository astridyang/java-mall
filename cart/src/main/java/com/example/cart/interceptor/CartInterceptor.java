package com.example.cart.interceptor;

import com.example.cart.to.UserInfoTo;
import com.example.common.constant.AuthServerConstant;
import com.example.common.constant.CartConstant;
import com.example.common.constant.CommonConstant;
import com.example.common.vo.MemberResVo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.UUID;

/**
 * @author sally
 * @date 2022-10-12 10:30
 */
@Component
public class CartInterceptor implements HandlerInterceptor {
	// 同一个线程共享数据
	public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 封装userInfoTo
		UserInfoTo userInfoTo = new UserInfoTo();
		HttpSession session = request.getSession();
		MemberResVo member = (MemberResVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
		if (member != null) {
			// 用户已登录
			userInfoTo.setUserId(member.getId());
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (Objects.equals(cookie.getName(), CartConstant.TEMP_USER_COOKIE_NAME)) {
					userInfoTo.setUserKey(cookie.getValue());
					userInfoTo.setTempUser(false);
				}
			}
		}
		// 没有临时用户分配一个临时用户
		if(!StringUtils.hasLength(userInfoTo.getUserKey())){
			String uuid = UUID.randomUUID().toString();
			userInfoTo.setUserKey(uuid);
		}
		// 目标方法执行之前
		threadLocal.set(userInfoTo);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		UserInfoTo userInfoTo = threadLocal.get();
		if(userInfoTo.isTempUser()){
			Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserKey());
			cookie.setDomain(CommonConstant.DOMAIN);
			cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
			response.addCookie(cookie);
		}

	}
}
