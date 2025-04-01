package com.scu.imageseg.service;

import com.scu.imageseg.entity.DiagnosisReport;

/**
 * <p>
 *  诊断报告pdf服务类
 * </p>
 *
 * @author wlt
 * @since 2025-03-28
 */
public interface IPdfReportService {
    byte[] generatePdf(DiagnosisReport diagnosisReport); // 根据诊断报告生成pdf的bytearray
}
