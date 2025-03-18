package com.scu.imageseg.service;

import java.awt.image.BufferedImage;

/**
 * <p>
 *  验证码服务类 主要负责验证码的生成和校验
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
public interface IVerifyCodeService {
    // 接口不需要声明public
    BufferedImage generateVerifyCode(String sessionId); // 生成验证码
    Boolean validateVerifyCode(String sessionId, String verifyCode); // 验证码验证
    Boolean deleteVerifyCode(String sessionId); //销毁验证码
}
