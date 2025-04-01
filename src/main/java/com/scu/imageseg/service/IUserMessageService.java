package com.scu.imageseg.service;

import com.scu.imageseg.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wlt
 * @since 2025-03-22
 */
public interface IUserMessageService extends IService<UserMessage> {
    List<UserMessage> getUserMessageList(Long userId);
}
