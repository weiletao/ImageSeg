package com.scu.imageseg.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 诊断申请数据类(封装)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisRequestData {
    private String name;
    private String birthDate;
    private String phone;
    private String symptoms;
    private String start_time;
    private String duration;
    private String imageDescription;
    private String medical_history;
    private String family_history;
    private String other_exams;
    private String current_treatment;
    private Boolean emergency_diagnosis;
    private String special_attention;
    private List<MultipartFile> ultrasoundImages;
    private String doctor_email;
    private Long patient_id;
    private String status;
}
