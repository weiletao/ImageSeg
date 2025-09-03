package com.scu.imageseg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scu.imageseg.entity.CustomUser;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.mapper.CustomUserMapper;
import com.scu.imageseg.service.ICustomUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
@Slf4j
@Service
public class CustomUserServiceImpl extends ServiceImpl<CustomUserMapper, CustomUser> implements ICustomUserService {

    @Autowired
    private CustomUserMapper customUserMapper;

    /**
     * 验证用户名和密码 验证成功返回对应用户实体类 验证失败返回空值
     * 与MD5加密不同，Bcrypt对密码加密方式具有随机性，所以需要调用该类的match方法进行比较，因此该方法能够分辨两种情况，用户不存在和用户名或密码错误
     * @param customUser
     * @return
     */
    @Override
    public CustomUser verifyUsernamePassword(CustomUser customUser) {
        QueryWrapper<CustomUser> queryWrapper = new QueryWrapper<>();
//        log.info(customUser.toString());
        queryWrapper.eq("username", customUser.getUsername());
        CustomUser customUserData = customUserMapper.selectOne(queryWrapper); // 根据用户名在数据库中寻找对应用户
        if(customUserData == null){ return null; } // 用户不存在
//        log.info(customUser.getPassword());
//        log.info(customUserData.getPassword());
        if(!new BCryptPasswordEncoder().matches(customUser.getPassword(), customUserData.getPassword())){  // 用户名或密码错误！
            log.error("用户名或密码错误！");
            throw new ServiceException(204, "用户名或密码错误！");
        }
        log.info("用户名和密码正确，验证成功！");
        return customUserData;
    }

    /**
     * 根据用户名获取用户信息类
     * @param username
     * @return
     */
    @Override
    public CustomUser getUserProfile(String username) {
        QueryWrapper<CustomUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        CustomUser customUser = customUserMapper.selectOne(queryWrapper); // 根据用户名在数据库中寻找对应用户
        if(customUser == null){ return null; } // 用户不存在
        // 清除不能返回的数据 确保安全
        customUser.setPassword(null);
        customUser.setId(null);
        return customUser;
    }

    /**
     * 根据id修改用户信息
     * @param id
     * @param userData
     * @return
     */
    @Override
    public CustomUser updateUserProfile(Integer id, CustomUser userData) {
        /** 指定要修改的字段 */
        UpdateWrapper<CustomUser> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", id);
        /**错误使用，如果要修改的话应该用set方法*/
//        userUpdateWrapper.eq("full_name", userData.getFullName()); // 修改姓名
//        userUpdateWrapper.eq("birth_date", userData.getBirthDate()); // 修改出生日期
//        userUpdateWrapper.eq("phone", userData.getPhone()); // 修改电话
//        userUpdateWrapper.eq("bio", userData.getBio()); // 修改个人简介
        log.info("执行用户信息修改...." + userData);
        customUserMapper.update(userData, userUpdateWrapper); // 参数分别为：为了方便快速赋值而存在的实体类 + updateWrapper既可以指定要修改的具体值set，又可以指定条件where
        return userData; // 实体类经过update函数后会发生变化
    }

    /**
     * 获取某个角色的用户列表
     * @param role
     * @return
     */
    @Override
    public List<CustomUser> getUsersListByRole(String role) {
        // 判断角色名是否合法
        if(!(role.equals("patient") || role.equals("doctor") || role.equals("admin"))){
            log.info("角色名不合法！");
            throw new ServiceException(212, "角色名不合法！");
        }
        // 进行数据库操作
        log.info("执行角色列表查找...." + role);
        QueryWrapper<CustomUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("role", role);
        return customUserMapper.selectList(userQueryWrapper);
    }

}
