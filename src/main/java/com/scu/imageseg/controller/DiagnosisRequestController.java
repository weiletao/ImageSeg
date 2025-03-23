package com.scu.imageseg.controller;



import com.scu.imageseg.entity.DiagnosisRequestData;
import com.scu.imageseg.entity.ImageFile;
import com.scu.imageseg.entity.JSONResult;
import com.scu.imageseg.entity.UserMessage;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.IDiagnosisRequestService;
import com.scu.imageseg.service.IImageFileService;
import com.scu.imageseg.service.IUserMessageService;
import com.scu.imageseg.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
public class DiagnosisRequestController {

    @Autowired
    IDiagnosisRequestService iDiagnosisRequestService;
    @Autowired
    IImageFileService iImageFileService;
    @Autowired
    IUserMessageService iUserMessageService;


    /**
     * 新建诊断申请接口 实现患者新建用户诊断申请功能 同时在本地存储相关的images
     * 涉及到3个表的操作：诊断申请表 诊断图像表 用户消息表
     * 可能抛出3个异常
     * 215，写入诊断申请表异常！
     * 216，诊断图像保存失败！
     * 217，生成用户消息失败！
     */
    @PostMapping("/jwtFilter/newDiagnosis")
    public JSONResult<Object> newDiagnosis(HttpServletRequest httpServletRequest, DiagnosisRequestData diagnosisRequestData) {
        log.info("接收到前端诊断申请数据：" + diagnosisRequestData.toString());
        Map<String, Object> map;
        /** 将数据写入诊断申请表 并得到诊断申请记录id */
        try {
            map = iDiagnosisRequestService.saveDiagnosisRequestReturnId(Long.valueOf((Integer) httpServletRequest.getAttribute("id")), diagnosisRequestData);
        }catch (Exception e){
            log.error("写入诊断申请表异常！" + e.getMessage());
            throw new ServiceException(215, "写入诊断申请表异常！");
        }
        /** FileUtil将检查图片保存并写入诊断图像表 */
        try {
            for(MultipartFile imageFile : diagnosisRequestData.getUltrasoundImages()){ // 增强型for循环会报空指针异常，但不会报空集合异常
                if(!imageFile.isEmpty()){
                    // 保存图片到recourse/img目录下
                    FileUtil fileUtil = new FileUtil(imageFile);
                    String filePath = fileUtil.saveFile();
                    // 写入ImageFile表
                    ImageFile image = new ImageFile();
                    image.setDiagnosisRequestId((Long) map.get("diagnosisRequestId"));
                    image.setDescription(diagnosisRequestData.getImageDescription());
                    image.setFilePath(filePath);
                    image.setCreatedAt(LocalDateTime.now());
                    image.setUpdatedAt(LocalDateTime.now());
                    if(!iImageFileService.save(image)) throw new Exception();
                }
            }
        }catch (Exception e){
            log.error("诊断图像保存失败！");
            throw new ServiceException(216, "诊断图像保存失败！");
        }
        /** 写入用户消息表 */
        try {
            log.info("构造用户信息表实体类...");
            UserMessage userMessage = new UserMessage();
            userMessage.setMessageType("info");
            userMessage.setTitle("您有一条新的诊断申请");
            userMessage.setSummary("患者 " + diagnosisRequestData.getName() + " 向您提交了一条诊断申请。");
            // 设置紧急程度
            userMessage.setIsUrgent(diagnosisRequestData.getEmergency_diagnosis());
            // 设置消息详情
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("患者 ").append(diagnosisRequestData.getName()).append(" 向您提交了一条诊断申请。\n");
            stringBuilder.append(diagnosisRequestData.getSymptoms() + "\n");
            if(diagnosisRequestData.getEmergency_diagnosis()){
                // 如果紧急
                stringBuilder.append("要求急诊！");
            }else {
                stringBuilder.append("不要求急诊。");
            }
            userMessage.setDetails(stringBuilder.toString());
            userMessage.setTimestamp(LocalDateTime.now()); // 设置时间戳
            userMessage.setUserId((Long) map.get("doctorId")); // 设置消息所属用户id
            userMessage.setIsRead(false); // 设置是否已读
            log.info("写入用户信息表实体类...");
            if(!iUserMessageService.save(userMessage)) throw new Exception();
        }catch (Exception e){
            log.error("生成用户消息失败！");
            throw new ServiceException(217, "生成用户消息失败！");
        }

        return new JSONResult<>(200, "操作成功！");

    }

}
