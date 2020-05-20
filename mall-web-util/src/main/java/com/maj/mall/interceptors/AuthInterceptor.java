package com.maj.mall.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.maj.mall.annotations.LoginRequired;
import com.maj.mall.util.CookieUtil;
import com.maj.mall.util.HttpclientUtil;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private final String PASSPORT_URL = "http://passport.gmall.com:7085";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 拦截器代码
		
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

		// 判断方法是否有注解
		HandlerMethod method = (HandlerMethod) handler;

        // *************************没有注解，不需要验证*******************
		if(!method.hasMethodAnnotation(LoginRequired.class)) {
			return true;
		} 
		
		// **************************有注解，就需要验证*********************
		String token = "";
		// 如果从来没有登陆过，oldToken就是空的
		String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        // 没有登陆过，token就是空
		String newToken = request.getParameter("token");
		if(!StringUtils.isEmpty(oldToken)) {
			token = oldToken;
		}
		if(!StringUtils.isEmpty(newToken)) {
			token = newToken;
		}
		
        // 验证是否成功
		boolean isVerifySuccess = false;
		Map<String, String> successMap = null;
		if(!StringUtils.isEmpty(token)) {
            String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
            if(StringUtils.isBlank(ip)){
                ip = request.getRemoteAddr();// 从request中获取ip
                System.out.println(request.getRequestURL());
                if(StringUtils.isBlank(ip)){
                    ip = "127.0.0.1";
                }
            }
			
            // 验证token是否正确
            String successJson = HttpclientUtil.doGet(PASSPORT_URL + "/verify?token=" + token
                    + "&currentIp=" + ip);
			
			successMap = JSON.parseObject(successJson, Map.class);
            String success = successMap.get("status");
			if("success".equals(success)) {
				isVerifySuccess = true;
			}	
		}
		
        // 如果验证通过，设置用户信息，覆盖cookie，用户信息设置到request中
		if(isVerifySuccess) {
			request.setAttribute("memberId", successMap.get("memberId"));
			request.setAttribute("nickname", successMap.get("nickname"));
			// 保存两个小时
            CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
			return true;
		}
		
        // *******************有需要验证注解，但验证没通过；根据注解判断是否需要必须验证通过
        LoginRequired loginRequired = method.getMethodAnnotation(LoginRequired.class);
		// **************有注解，是否必须值：为false，直接通过*******************
		boolean loginSuccess = loginRequired.loginSuccess();
		// 如果不需要登录，也可以访问
		if(!loginSuccess) {
			return true;
		}
		
        // 验证失败；重定向到passport，进行登录
        response.sendRedirect(PASSPORT_URL + "/index?ReturnUrl=" + request.getRequestURL());

        return false;
		
	}
}
