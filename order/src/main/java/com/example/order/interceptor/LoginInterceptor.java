package com.example.order.interceptor;

import com.example.common.constant.AuthServerConstant;
import com.example.common.vo.MemberResVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author sally
 * @date 2022-10-14 14:14
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
	public static ThreadLocal<MemberResVo> threadLocal = new ThreadLocal<>();
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// 远程调用放行
		String uri = request.getRequestURI();
		AntPathMatcher matcher = new AntPathMatcher();
		boolean match = matcher.match("/order/order/info/**", uri);
		boolean match1 = matcher.match("/payed/notify", uri);
		// System.out.println("match1 = " + match1);
		if (match || match1){
			return true;
		}


		HttpSession session = request.getSession();
		MemberResVo member = (MemberResVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
		if (member != null) {
			threadLocal.set(member);
			return true;
		} else {
			request.getSession().setAttribute("msg", "please login first");
			response.sendRedirect("//auth.gmall.com/login.html");
			return false;
		}
	}
}
