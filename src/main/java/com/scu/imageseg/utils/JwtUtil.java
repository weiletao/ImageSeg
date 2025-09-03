package com.scu.imageseg.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.scu.imageseg.entity.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Jwt工具类 生成JWT和认证JWT 工具类只能完成初步认证JWT JwtRedisService引入了额外的验证逻辑并采用Redis缓存，以满足修改密码、退出登录的需求
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */

@Slf4j
public class JwtUtil {

    //    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class); // 日志（改为使用@Slf4j）
    private static final String SECRET_KEY = "my_secret_key"; // 密钥
    private static final long EXPIRATION_TIME = 30 * 60 * 1000; // 过期时间：30分钟


    public static String generateToken(CustomUser customUser) {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        return JWT.create()
                .withHeader(header) // 添加头部
                // 添加基本信息（用户id 用户名 用户权限等）
                .withClaim("id", customUser.getId())
                .withClaim("username", customUser.getUsername())
                .withClaim("role", customUser.getRole())

                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 添加过期时间
                .withIssuedAt(new Date()) // 签发时间
                .sign(Algorithm.HMAC256(SECRET_KEY)); // 私钥加密
    }

    public static Map<String, Claim> validateToken(String token) {
        DecodedJWT decodedJWT = null;
        try {
            /** JWT验证器 验证token */
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build(); // 初始化验证器
            decodedJWT = verifier.verify(token); // 验证token

        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("token解码异常");
            // 若解码异常则返回空值
            return null;
        }
        // 返回token中的claims（基本信息）
        return decodedJWT.getClaims();
    }
}