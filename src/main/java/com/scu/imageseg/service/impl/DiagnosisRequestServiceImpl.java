package com.scu.imageseg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scu.imageseg.entity.CustomUser;
import com.scu.imageseg.entity.DiagnosisRequest;
import com.scu.imageseg.entity.DiagnosisRequestData;
import com.scu.imageseg.mapper.DiagnosisRequestMapper;
import com.scu.imageseg.service.ICustomUserService;
import com.scu.imageseg.service.IDiagnosisRequestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wlt
 * @since 2025-03-22
 */
@Slf4j
@Service
public class DiagnosisRequestServiceImpl extends ServiceImpl<DiagnosisRequestMapper, DiagnosisRequest> implements IDiagnosisRequestService {

    @Autowired
    DiagnosisRequestMapper diagnosisRequestMapper;
    @Autowired
    ICustomUserService iCustomUserService;

    /**
     * 从DiagnosisRequestData中获取信息 保存诊断申请记录并返回对应id
     * @param patientId
     * @param diagnosisRequestData
     * @return
     */
    @Override
    public Map<String,Object> saveDiagnosisRequestReturnId(Long patientId, DiagnosisRequestData diagnosisRequestData) {
//        log.info("patientId: " + patientId.toString());
        /** 根据DiagnosisRequestData构造DiagnosisRequest实体类 */
        // 根据doctor email查询相关的doctor id
        QueryWrapper<CustomUser> doctorQueryWrapper = new QueryWrapper<>();
        doctorQueryWrapper.eq("email", diagnosisRequestData.getDoctor_email());
        Long doctorId = iCustomUserService.getOne(doctorQueryWrapper).getId();
        // 构造DiagnosisRequest实体类
        log.info("初始化DiagnosisRequest实体类...");
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        diagnosisRequest.setName(diagnosisRequestData.getName());
        diagnosisRequest.setBirthDate(LocalDate.parse(diagnosisRequestData.getBirthDate()));
        diagnosisRequest.setPhone(diagnosisRequestData.getPhone());
        diagnosisRequest.setSymptoms(diagnosisRequestData.getSymptoms());
        diagnosisRequest.setStartTime(LocalDate.parse(diagnosisRequestData.getStart_time()));
        diagnosisRequest.setDuration(diagnosisRequestData.getDuration());
        diagnosisRequest.setMedicalHistory(diagnosisRequestData.getMedical_history());
        diagnosisRequest.setFamilyHistory(diagnosisRequestData.getFamily_history());
        diagnosisRequest.setOtherExams(diagnosisRequestData.getOther_exams());
        diagnosisRequest.setCurrentTreatment(diagnosisRequestData.getCurrent_treatment());
        diagnosisRequest.setEmergencyDiagnosis(diagnosisRequestData.getEmergency_diagnosis());
        diagnosisRequest.setSpecialAttention(diagnosisRequestData.getSpecial_attention());
        diagnosisRequest.setCreatedAt(LocalDateTime.now());
        diagnosisRequest.setUpdatedAt(LocalDateTime.now());
        diagnosisRequest.setPatientId(patientId);
        diagnosisRequest.setDoctorId(doctorId);
        diagnosisRequest.setStatus(diagnosisRequestData.getStatus());
        /** 将诊断申请记录写入数据库 */
        log.info("开始写入数据库...");
        Map<String, Object> re = new HashMap<>();
        re.put("diagnosisRequestId", (long) diagnosisRequestMapper.insert(diagnosisRequest)); // 利用Mapper插入记录并返回记录id
        re.put("doctorId", doctorId);
        return re;
    }
}
