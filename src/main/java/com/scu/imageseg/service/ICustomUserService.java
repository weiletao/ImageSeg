package com.scu.imageseg.service;

import com.scu.imageseg.entity.CustomUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
public interface ICustomUserService extends IService<CustomUser> {
    CustomUser verifyUsernamePassword(CustomUser customUser); // 验证用户名和密码 验证成功返回对应用户实体类
}
