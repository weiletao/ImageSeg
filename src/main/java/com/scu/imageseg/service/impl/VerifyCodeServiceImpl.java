package com.scu.imageseg.service.impl;

import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.IVerifyCodeService;
import com.scu.imageseg.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.scu.imageseg.utils.VerifyUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
/**
 * <p>
 *  验证码服务实现类 主要负责验证码的生成和校验
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
@Slf4j
@Service
public class VerifyCodeServiceImpl implements IVerifyCodeService {

    @Autowired // 此处需要使用springboot自动注入的方法来初始化Redis template，不能用Java传统的new方法，此外，该对象可以直接传到静态工具类RedisUtil中，实现封装。
    private StringRedisTemplate stringRedisTemplate;

    @Value("${globalVar.verifycode_timeout}") // 全局变量方法1：将变量写在yml全局配置文件中，利用@Value注释获取值
    private int VERIFYCODE_TIMEOUT; // 验证码有效时间

    /**
     * 验证码生成
     * @param sessionId
     * @return
     */
    @Override
    public BufferedImage generateVerifyCode(String sessionId) {
        /** 通过工具类VerifyUtil生成验证码 obj[0]代表String验证码，obj[1]代表图片 */
        Object[] objs = VerifyUtil.newBuilder().build().createImage();
        log.info("....成功生成验证码{}", objs[0]);

        /** session与验证码关联存入Redis */
        log.info("VERIFYCODE_TIMEOUT = " + VERIFYCODE_TIMEOUT);
        RedisUtil.set(stringRedisTemplate, "VERIFY_CODE_SESSIONID_" + sessionId, (String)objs[0], VERIFYCODE_TIMEOUT); // 将sesion与验证码关联存入数据库，并设置有效时间

        /** 返回验证码图片 */
        return (BufferedImage) objs[1];
    }

    /**
     * 验证码校验
     * @param sessionId
     * @param verifyCode
     * @return
     */
    @Override
    public Boolean validateVerifyCode(String sessionId, String verifyCode) {
        /** 访问Redis缓存中session对应验证码的值 */
        log.info("开始校验验证码，sessionId:{}", sessionId);
        String vCode = RedisUtil.get(stringRedisTemplate, "VERIFY_CODE_SESSIONID_" + sessionId);
        if(vCode == null) { throw new ServiceException(207, "验证码失效"); }; // 如果找不到Redis缓存，说明验证码失效了
        try {
            if(verifyCode.equalsIgnoreCase(vCode)){ // 在不失效的情况下校验验证码
                RedisUtil.delete(stringRedisTemplate, "VERIFY_CODE_SESSIONID_" + sessionId); // 销毁
                return true;
            }
            return false; // 查看Redis 中session对应的验证码 此处假设查不到会抛出异常（String.equals(null)会异常），证明验证码失效
        }catch (Exception e){
            log.error("验证码校验错误信息：" + e.getMessage());
            throw new ServiceException(208, "验证码校验服务错误");
        }
    }

    /**
     * 验证码销毁
     * @param sessionId
     * @return
     */
    @Override
    public Boolean deleteVerifyCode(String sessionId) {

        log.info("销毁验证码中....");
        try {
            RedisUtil.delete(stringRedisTemplate, "VERIFY_CODE_SESSIONID_" + sessionId); // 销毁验证码
            log.info("成功销毁！");
            return true;
        }catch (Exception e){
            log.error("销毁失败！");
            return false;
        }

    }
}
