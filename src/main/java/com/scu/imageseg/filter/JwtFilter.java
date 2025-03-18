package com.scu.imageseg.filter;

import com.auth0.jwt.interfaces.Claim;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.IJwtRedisService;
import com.scu.imageseg.service.impl.JwtRedisServiceImpl;
import com.scu.imageseg.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Jwt过滤器 拦截需要验证JWT的请求 需要在启动类加上@ServletComponentScan
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
@Slf4j
@Component
//@WebFilter(filterName = "JwtFilter", urlPatterns = "/jwtFilter/*") // 所有/jwtFilter下的接口都需要经过该过滤器 todo 由于该项目主要希望由spring容器管理 所以不用webfilter 这是由sevlet容器管理的
public class JwtFilter implements Filter {

    @Autowired
    IJwtRedisService iJwtRedisService;

    private final ObjectMapper objectMapper = new ObjectMapper();  // 用于转换为JSON

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json"); // 设置响应类型为JSON

        //获取 header里的token
        final String token = request.getHeader("authorization");
        log.info("成功获取header中的token{}", token);

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
        }
        // Except OPTIONS, other request should be checked by JWT
        else {
            Map<String,Object> re = new HashMap<>();

            if (token == null) {
                re.put("code", 0);
                re.put("message", "没有token！");
                response.getWriter().write(objectMapper.writeValueAsString(re));
                return;
            }

            Map<String, Claim> userData = JwtUtil.validateToken(token);
            if (userData == null) {
                re.put("code", 1);
                re.put("message", "token不合法");
                response.getWriter().write(objectMapper.writeValueAsString(re));
                return;
            }
            Integer id = userData.get("id").asInt();
            String userName = userData.get("username").asString();
            String role = userData.get("role").asString();

            log.info("token初步校验成功，id:{}, username:{}, role:{}", id, userName, role);

            /** 验证Redis中的Token 为了满足退出登录 修改密码的需求 */
            if(!iJwtRedisService.validateRedisJwtToken(id.toString(), token)){
                re.put("code", 2);
                re.put("message", "token校验失败");
                response.getWriter().write(objectMapper.writeValueAsString(re));
                return;
            }

            //拦截器 拿到用户信息，放到request中
            request.setAttribute("id", id);
            request.setAttribute("username", userName);
            request.setAttribute("role", role);
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
