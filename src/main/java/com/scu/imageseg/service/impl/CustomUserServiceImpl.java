package com.scu.imageseg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scu.imageseg.entity.CustomUser;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.mapper.CustomUserMapper;
import com.scu.imageseg.service.ICustomUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        queryWrapper.eq("username", customUser.getUsername());
        CustomUser customUserData = customUserMapper.selectOne(queryWrapper); // 根据用户名在数据库中寻找对应用户
        if(customUserData == null){ return null; } // 用户不存在
        if(!new BCryptPasswordEncoder().matches(customUser.getPassword(), customUserData.getPassword())){  // 用户名或密码错误！
            log.error("用户名或密码错误！");
            throw new ServiceException(204, "用户名或密码错误！");
        }
        log.info("用户名和密码正确，验证成功！");
        return customUserData;
    }
}
