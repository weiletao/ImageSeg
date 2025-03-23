package com.scu.imageseg.service;

import com.scu.imageseg.entity.DiagnosisRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scu.imageseg.entity.DiagnosisRequestData;

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
    Map<String, Object> saveDiagnosisRequestReturnId(Long patientId, DiagnosisRequestData diagnosisRequestData); // 从DiagnosisRequestData中获取信息 保存诊断申请记录并返回对应id
}
