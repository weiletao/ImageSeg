package com.scu.imageseg.service;

import com.scu.imageseg.entity.DiagnosisRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scu.imageseg.entity.DiagnosisRequestData;
import com.scu.imageseg.entity.DiagnosisRequestWithImage;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wlt
 * @since 2025-03-22
 */
public interface IDiagnosisRequestService extends IService<DiagnosisRequest> {
    Map<String, Object> saveDiagnosisRequestReturnId(Long patientId, DiagnosisRequestData diagnosisRequestData); // 从DiagnosisRequestData中获取信息 保存诊断申请记录并返回对应id和doctorId
    List<DiagnosisRequestWithImage> getUserDiagnosisRequestsWithImages(Long userId); // 获取对应用户Id的所有诊断申请列表
}
