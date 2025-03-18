package com.scu.imageseg.service.impl;

import com.scu.imageseg.entity.CustomUser;
import com.scu.imageseg.service.IJwtRedisService;
import com.scu.imageseg.utils.JwtUtil;
import com.scu.imageseg.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtRedisServiceImpl implements IJwtRedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${globalVar.jwt_token_redis_timeout}")
    private long tokenTimeOut;

    /**
     * 生成Jwt Token并存储到Redis数据库
     * @param customUser
     * @return
     */
    @Override
    public String generateRedisJwtToken(CustomUser customUser) {
        try{
            String token = JwtUtil.generateToken(customUser);
            RedisUtil.set(redisTemplate, customUser.getId().toString(), token, tokenTimeOut);
            return token;
        }catch (Exception e){
            log.error("生成Jwt Token失败！");
            return null;
        }
    }

    /**
     * 验证Redis数据库中是否存在id token对
     * @param id
     * @param token
     * @return
     */
    @Override
    public Boolean validateRedisJwtToken(String id, String token) {
        try{
            log.info("正在验证Jwt Token...."); // 验证Redis中是否存在token token是否一致
            return RedisUtil.get(redisTemplate, id).equals(token);
        }catch (Exception e){
            log.error("验证Jwt Token失败！原因：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除Redis数据库中对应用户的JwtToken 以满足修改密码 退出登录功能
     * @param id
     * @return
     */
    @Override
    public Boolean deleteRedisJwtToken(String id) {
        try{
            log.info("尝试删除Jwt Token...");
            RedisUtil.delete(redisTemplate, id);
            return true;
        }catch (Exception e){
            log.error("删除Jwt Token失败！");
            return false;
        }
    }
}
