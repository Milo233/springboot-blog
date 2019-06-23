package com.yuan.blog.config;

import com.google.common.util.concurrent.RateLimiter;
import com.yuan.blog.controller.MainController;
import com.yuan.blog.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过过滤器
 * 1. 实现登录添加参数
 * 2. 基于ip限流
 * com.yuan.blog.config.SecurityConfig 里要把过滤器加入HttpSecurity
 */
public class SecurityFilterConfig extends GenericFilterBean {

    private static final String AUTH_TYPE_PARAM_NAME = "auth_type";

    private static final String OAUTH_TOKEN_URL = "/login";
    private RequestMatcher requestMatcher;

    /**
     * The Limiter map.
     */
    static Map<String, RateLimiter> limiterMap = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    SecurityFilterConfig() {
        this.requestMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher(OAUTH_TOKEN_URL, "POST")
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //ip限流 开始
        String ip = NetUtil.getIpAddr(request);
        RateLimiter limiterIp = limiterMap.get(ip);
        if (limiterIp == null) {
            limiterIp = RateLimiter.create(50); // 每秒不超过50个请求被提交
            limiterMap.put(ip, limiterIp);
            log.info("IP个数：{}", limiterMap.size());
        } else {
            if (!limiterIp.tryAcquire()) {
                log.warn("接口超频：{}，rate:{}，ip地址：{}",request.getRequestURI(), limiterIp.getRate(), ip);
                // 不能抛异常，因为@ExceptionHandlerAdvice 捕获不到这里的异常
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "over rated！");
                return;
            }
        }
        // ip限流 结束

        try {// 只对是登录的请求做处理
            if (servletRequest != null && requestMatcher.matches(request)) {
                String loginType = request.getParameter(AUTH_TYPE_PARAM_NAME);
                IntegrationAuthentication integrationAuthentication = new IntegrationAuthentication();
                integrationAuthentication.setAuthType(loginType);
                IntegrationAuthenticationContext.set(integrationAuthentication);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            IntegrationAuthenticationContext.clear();
        }
    }
}