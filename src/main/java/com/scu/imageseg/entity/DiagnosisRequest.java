package com.scu.imageseg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wlt
 * @since 2025-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DiagnosisRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private LocalDate birthDate;

    private String phone;

    private String symptoms;

    private LocalDate startTime;

    private String duration;

    private String medicalHistory;

    private String familyHistory;

    private String otherExams;

    private String currentTreatment;

    private Boolean emergencyDiagnosis;

    private String specialAttention;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long patientId;

    private Long doctorId;

    private String status;


}
