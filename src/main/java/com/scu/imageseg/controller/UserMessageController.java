package com.scu.imageseg.controller;


import com.scu.imageseg.entity.JSONResult;
import com.scu.imageseg.entity.UserMessage;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.IUserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wlt
 * @since 2025-03-22
 */
@Slf4j
@RestController
public class UserMessageController {
    @Autowired
    IUserMessageService iUserMessageService;

    @GetMapping("/jwtFilter/myMessage")
    public JSONResult<Object> myMessage(HttpServletRequest httpServletRequest){
        try {
            return new JSONResult<>(iUserMessageService.getUserMessageList(Long.valueOf((Integer)httpServletRequest.getAttribute("id"))));
        }catch (Exception e){
            log.error("获取我的消息列表失败！" + e.getMessage());
            throw new ServiceException(232, "获取我的消息列表失败！");
        }
    }

    @PutMapping("/jwtFilter/readMessage")
    public JSONResult<Object> readMessage(@RequestBody UserMessage userMessage){
        log.info("接收到前端返回数据：" + userMessage.toString());
        try {
            userMessage.setIsRead(true);
            if(!iUserMessageService.updateById(userMessage)){
                throw new Exception();
            }
            return new JSONResult<>(200, "操作成功！");
        }catch (Exception e){
            log.error("更新已读消息失败！");
            throw new ServiceException(233, "更新已读消息失败！");
        }
    }

    @PutMapping("/jwtFilter/deleteMessage")
    public JSONResult<Object> deleteMessage(@RequestBody Map<String, Long> messageId){
        log.info("接受到前端返回消息id：" + messageId);
        try {
            if(!iUserMessageService.removeById(messageId.get("messageId"))){
                throw new Exception();
            }
            return new JSONResult<>(200, "操作成功！");
        }catch (Exception e){
            log.error("删除指定消息失败！" + e.getMessage());
            throw new ServiceException(234, "删除指定消息失败！");
        }
    }
}
