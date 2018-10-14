package com.yuan.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Spring Security 配置类.
 */
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法安全设置
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String KEY = "milo.com";

    /**
     * 自定义配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/**", "/js/**", "/fonts/**", "/index").permitAll() // 都可以访问
                .antMatchers("/h2-console/**").permitAll() // 都可以访问
                // /admin/** 的页面都要登录
                .antMatchers("/admins/**").hasRole("ADMIN") // 需要相应的角色才能访问
                .and()
                .formLogin()   //基于 Form 表单登录验证
                .loginPage("/login").failureUrl("/login-error"); // 自定义登录界面
        // 关闭csrf 不然只能提交get请求。。可以传递csrf token来进行post请求，but 有点麻烦。请求都要改
        http.csrf().disable();

/*                .and().rememberMe().key(KEY) // 启用 remember me
                .and().exceptionHandling().accessDeniedPage("/403");  // 处理异常，拒绝访问就重定向到 403 页面
        http.csrf().ignoringAntMatchers("/h2-console/**"); // 禁用 H2 控制台的 CSRF 防护
        http.headers().frameOptions().sameOrigin(); // 允许来自同一来源的H2 控制台的请求*/
    }

    // any method annotated with @Autowired is a config method. It is called on bean instantiation after field injection
    // is done. The arguments of the method are injected into the method on calling.
    @Autowired // 类被实例化以后会被自动执行
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder()).withUser("milo")
                .password(new BCryptPasswordEncoder().encode("111")).roles("ADMIN");
//        auth.inMemoryAuthentication().withUser("milo") // 认证信息存储在内存中
//                .password("123456").roles("ADMIN");
    }

}
