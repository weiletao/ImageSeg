package com.scu.imageseg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scu.imageseg.entity.UserMessage;
import com.scu.imageseg.mapper.UserMessageMapper;
import com.scu.imageseg.service.IUserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wlt
 * @since 2025-03-22
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements IUserMessageService {
    @Autowired
    UserMessageMapper userMessageMapper;
    @Override
    public List<UserMessage> getUserMessageList(Long userId) {
        QueryWrapper<UserMessage> userMessageQueryWrapper = new QueryWrapper<>();
        userMessageQueryWrapper.eq("user_id", userId);
        return userMessageMapper.selectList(userMessageQueryWrapper);
    }
}
