package com.yuan.blog.config;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 通过过滤器 实现登录添加参数
 * com.yuan.blog.config.SecurityConfig 里要把过滤器加入HttpSecurity
 */
public class SecurityFilterConfig extends GenericFilterBean {

    private static final String AUTH_TYPE_PARAM_NAME = "auth_type";

    private static final String OAUTH_TOKEN_URL = "/login";
    private RequestMatcher requestMatcher;

    SecurityFilterConfig() {
        this.requestMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher(OAUTH_TOKEN_URL, "POST")
//                new AntPathRequestMatcher(OAUTH_TOKEN_URL, "GET"),
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {// 只对是登录的请求做处理
            if (servletRequest != null && requestMatcher.matches(request)) {
                HttpServletRequest r = (HttpServletRequest) servletRequest;
                String loginType = r.getParameter(AUTH_TYPE_PARAM_NAME);
                IntegrationAuthentication integrationAuthentication = new IntegrationAuthentication();
                integrationAuthentication.setAuthType(loginType);
                IntegrationAuthenticationContext.set(integrationAuthentication);
                System.out.println("end of dofilter");
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            IntegrationAuthenticationContext.clear();
        }
    }

}