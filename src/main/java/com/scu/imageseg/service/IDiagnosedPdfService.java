package com.scu.imageseg.service;

import com.scu.imageseg.entity.DiagnosedPdf;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wlt
 * @since 2025-03-28
 */
public interface IDiagnosedPdfService extends IService<DiagnosedPdf> {
    DiagnosedPdf getDiagnosedPdfByDiagnosisRequestId(Long diagnosisRequestId);
}
