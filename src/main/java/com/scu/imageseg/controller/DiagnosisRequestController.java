package com.scu.imageseg.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scu.imageseg.entity.*;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.*;
import com.scu.imageseg.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
    private IDiagnosisRequestService iDiagnosisRequestService;
    @Autowired
    private IImageFileService iImageFileService;
    @Autowired
    private IUserMessageService iUserMessageService;
    @Autowired
    private IPdfReportService iPdfReportService;
    @Autowired
    private IDiagnosedPdfService iDiagnosedPdfService;

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
                    String filePath = fileUtil.saveFile((Long) map.get("diagnosisRequestId"));
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

    /**
     * 我的诊断申请接口
     * 可能抛出1个异常
     * 218, "获取用户诊断申请失败！"
     */
    @GetMapping("/jwtFilter/myDiagnosisRequest")
    public JSONResult<Object> myDiagnosisRequest(HttpServletRequest httpServletRequest){
        log.info("开始获取用户诊断申请...");
        try{
            List<DiagnosisRequestWithImage> diagnosisRequestWithImageList = iDiagnosisRequestService.getUserDiagnosisRequestsWithImages(Long.valueOf((Integer)httpServletRequest.getAttribute("id")));
            List<DiagnosisRequestWithImageAndPdf> diagnosisRequestWithImageAndPdfs = new ArrayList<>();
            for (DiagnosisRequestWithImage diagnosisRequestWithImage:diagnosisRequestWithImageList){
                DiagnosisRequestWithImageAndPdf diagnosisRequestWithImageAndPdf = new DiagnosisRequestWithImageAndPdf();
                BeanUtils.copyProperties(diagnosisRequestWithImage, diagnosisRequestWithImageAndPdf);
                if(diagnosisRequestWithImage.getStatus().equals("diagnosed")){
                    // 如果是已诊断的申请
                    String pdfPath = iDiagnosedPdfService.getDiagnosedPdfByDiagnosisRequestId(diagnosisRequestWithImage.getId()).getPdfPath();
                    diagnosisRequestWithImageAndPdf.setPdfPath(pdfPath);
                }
                diagnosisRequestWithImageAndPdfs.add(diagnosisRequestWithImageAndPdf);
            }
            return new JSONResult<>(diagnosisRequestWithImageAndPdfs);
        }catch (Exception e){
            log.error("获取用户诊断申请失败！");
            throw new ServiceException(218, "获取用户诊断申请失败！");
        }
    }

    /**
     * 获取我的诊断申请详情接口 根据id查询我申请诊断的历史记录
     * 可能抛出1个异常
     * 219, "获取我的诊断申请详情失败！"
     */
    @GetMapping("/jwtFilter/myDiagnosisRequest/detail")
    public JSONResult<Object> myDiagnosisRequestDetail(@RequestParam Long diagnosisRequestId){
        log.info("开始获取对应的诊断申请详情...");
        try {
            return new JSONResult<>(iDiagnosisRequestService.getDiagnosisRequestsWithImagesUsingId(diagnosisRequestId));
        }catch (Exception e){
            log.error("获取我的诊断申请详情失败！");
            throw new ServiceException(219, "获取我的诊断申请详情失败！");
        }
    }

    /**
     * 获取医生待处理诊断列表
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/jwtFilter/doctorDiagnosisRequest/pending")
    public JSONResult<Object> doctorPendingDiagnosisRequest(HttpServletRequest httpServletRequest){
        log.info("开始获取医生待处理诊断...");
        try{
            Long userId = Long.valueOf((Integer) httpServletRequest.getAttribute("id")); // 获取用户id
            String role = (String) httpServletRequest.getAttribute("role"); // 获取用户角色
            List<DiagnosisRequestWithImage> diagnosisRequestWithImageList
                    = iDiagnosisRequestService.getDiagnosisStatusRequestsWithImages(userId, role, "pending");
            return new JSONResult<>(diagnosisRequestWithImageList);
        }catch (Exception e){
            log.error("获取医生待处理诊断失败！" + e.getMessage());
            throw new ServiceException(223, "获取医生待处理诊断失败！");
        }
    }

    /**
     * 医生提交诊断接口
     * @param diagnosisSubmit
     * @return
     */
    @PostMapping("/jwtFilter/submitDiagnosis")
    public JSONResult<Object> submitDiagnosis(HttpServletRequest httpServletRequest, @RequestBody DiagnosisSubmit diagnosisSubmit){
        log.info("成功接收到医生诊断数据：" + diagnosisSubmit.toString());
        /** 构造DiagnosisReport类 */
        DiagnosisReport diagnosisReport = new DiagnosisReport();
        try {
            // 查询数据库得到DiagnosisRequest类，同时构造DiagnosisReport类
            diagnosisReport.setDiagnosisRequest(iDiagnosisRequestService.getById(diagnosisSubmit.getDiagnosisId()));
            diagnosisReport.setDiagnosisSubmit(diagnosisSubmit);
        }catch (Exception e){
            log.error("构造DiagnosisReport类失败！");
            throw new ServiceException(224, "构造DiagnosisReport类失败！");
        }
        /** 生成pdf诊断报告 */
        FileUtil fileUtil = new FileUtil();
        String pdfRootPath = fileUtil.getResourceSavePath().replace("img", "pdf");
        String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // 生成随机文件名
        try {
            byte[] pdfReport = iPdfReportService.generatePdf(diagnosisReport);
            fileUtil.saveToFile(pdfReport, pdfRootPath + "\\" + diagnosisSubmit.getDiagnosisId() +"\\" +  uuid + ".pdf");
        }catch (Exception e){
            log.error("生成pdf诊断报告失败！");
            e.printStackTrace();
            throw new ServiceException(225, "生成pdf诊断报告失败！");
        }
        /** 写入diagnosed_pdf数据表 */
        // 构造pdf路径
        String pdfPath = diagnosisSubmit.getDiagnosisId() + "/" + uuid + ".pdf";
        log.info("生成pdf诊断报告成功，路径为：{}", pdfPath);
        // 构造DiagnosedPdf实体类
        DiagnosedPdf diagnosedPdf = new DiagnosedPdf();
        diagnosedPdf.setPdfPath(pdfPath);
        diagnosedPdf.setDiagnosisRequestId(diagnosisSubmit.getDiagnosisId());
        diagnosedPdf.setUpdatedAt(LocalDateTime.now());
        // 调用service存储数据库
        try {
            if(!iDiagnosedPdfService.save(diagnosedPdf)){
                throw new Exception();
            }
        }catch (Exception e){
            log.error("调用service存储数据库失败！");
            throw new ServiceException(229, "调用service存储数据库失败！");
        }
        /** 消息通知模块 写入用户消息表 */
        try {
            log.info("构造用户信息表实体类...");
            UserMessage userMessage = new UserMessage();
            userMessage.setMessageType("info");
            userMessage.setTitle("您的诊断申请状态已更新");
            userMessage.setSummary("医生 " + httpServletRequest.getAttribute("username") + " 已处理您的诊断申请。");
            // 设置紧急程度
            userMessage.setIsUrgent(false);
            // 设置消息详情
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("医生 ").append(httpServletRequest.getAttribute("username")).append(" 已处理您诊断id为 ").append(diagnosisSubmit.getDiagnosisId()).append(" 的诊断申请。");
            userMessage.setDetails(stringBuilder.toString());
            userMessage.setTimestamp(LocalDateTime.now()); // 设置时间戳
            userMessage.setUserId(diagnosisReport.getDiagnosisRequest().getPatientId()); // 设置消息所属用户id
            userMessage.setIsRead(false); // 设置是否已读
            log.info("写入用户信息表实体类...");
            if(!iUserMessageService.save(userMessage)) throw new Exception();
        }catch (Exception e){
            log.error("生成用户消息失败！");
            throw new ServiceException(217, "生成用户消息失败！");
        }
        /** 修改对应的诊断申请表字段 状态为已诊断 */
        try {
            UpdateWrapper<DiagnosisRequest> diagnosisRequestUpdateWrapper = new UpdateWrapper<>();
            diagnosisRequestUpdateWrapper.set("status", "diagnosed"); // 修改status字段 状态为已诊断
            diagnosisRequestUpdateWrapper.eq("id", diagnosisReport.getDiagnosisRequest().getId());
            iDiagnosisRequestService.update(diagnosisRequestUpdateWrapper);
        }catch (Exception e){
            log.error("修改对应的诊断申请表字段失败！");
            throw new ServiceException(230, "修改对应的诊断申请表字段失败！");
        }

        return new JSONResult<>(200, "操作成功！");
    }

    /**
     * 获取医生已处理诊断列表
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/jwtFilter/doctorDiagnosisRequest/diagnosed")
    public JSONResult<Object> doctorDiagnosedDiagnosisRequest(HttpServletRequest httpServletRequest){
        log.info("开始获取医生已处理诊断...");
        try{
            Long userId = Long.valueOf((Integer) httpServletRequest.getAttribute("id")); // 获取用户id
            String role = (String) httpServletRequest.getAttribute("role"); // 获取用户角色
            List<DiagnosisRequestWithImage> diagnosisRequestWithImageList
                    = iDiagnosisRequestService.getDiagnosisStatusRequestsWithImages(userId, role, "diagnosed");
            // 继续构造DiagnosisRequestWithImageAndPdf返回类
            List<DiagnosisRequestWithImageAndPdf> diagnosisRequestWithImageAndPdfs = new ArrayList<>();
            for(DiagnosisRequestWithImage diagnosisRequestWithImage:diagnosisRequestWithImageList){
                DiagnosedPdf diagnosedPdf = iDiagnosedPdfService.getDiagnosedPdfByDiagnosisRequestId(diagnosisRequestWithImage.getId());
                DiagnosisRequestWithImageAndPdf diagnosisRequestWithImageAndPdf = new DiagnosisRequestWithImageAndPdf();
                BeanUtils.copyProperties(diagnosisRequestWithImage, diagnosisRequestWithImageAndPdf);
                diagnosisRequestWithImageAndPdf.setPdfPath(diagnosedPdf.getPdfPath());
                diagnosisRequestWithImageAndPdfs.add(diagnosisRequestWithImageAndPdf);
            }
            return new JSONResult<>(diagnosisRequestWithImageAndPdfs);
        }catch (Exception e){
            log.error("获取医生已处理诊断失败！" + e.getMessage());
            throw new ServiceException(231, "获取医生已处理诊断失败！");
        }
    }




}
