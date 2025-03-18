package com.scu.imageseg.config;

import com.scu.imageseg.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.FilterRegistration;

/**
 * <p>
 *  过滤器Filters配置类（由Spring容器注入管理）
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
@Configuration
public class FilterConfig {
    @Autowired
    private JwtFilter jwtFilter;
    /**
     * 注入JwtFilter
     * @return
     */
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterBean(){
        FilterRegistrationBean<JwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(jwtFilter);
        filterRegistrationBean.addUrlPatterns("/jwtFilter/*");
        filterRegistrationBean.setName("JwtFilter");
        // 设置优先级别
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }


}
