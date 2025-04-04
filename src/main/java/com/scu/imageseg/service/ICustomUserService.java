package com.scu.imageseg.service;

import com.scu.imageseg.entity.CustomUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
    CustomUser getUserProfile(String username); // 根据用户名获取用户信息
    CustomUser updateUserProfile(Integer id, CustomUser userData); // 根据id修改用户信息
    List<CustomUser> getUsersListByRole(String role); // 获取某个角色的用户列表

}
