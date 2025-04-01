package com.scu.imageseg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scu.imageseg.entity.DiagnosedPdf;
import com.scu.imageseg.mapper.DiagnosedPdfMapper;
import com.scu.imageseg.service.IDiagnosedPdfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wlt
 * @since 2025-03-28
 */
@Service
public class DiagnosedPdfServiceImpl extends ServiceImpl<DiagnosedPdfMapper, DiagnosedPdf> implements IDiagnosedPdfService {
    @Autowired
    DiagnosedPdfMapper diagnosedPdfMapper;

    @Override
    public DiagnosedPdf getDiagnosedPdfByDiagnosisRequestId(Long diagnosisRequestId) {
        QueryWrapper<DiagnosedPdf> diagnosedPdfQueryWrapper = new QueryWrapper<>();
        diagnosedPdfQueryWrapper.eq("diagnosis_request_id", diagnosisRequestId);
        return diagnosedPdfMapper.selectOne(diagnosedPdfQueryWrapper);
    }
}
