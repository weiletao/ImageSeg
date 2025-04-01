package com.scu.imageseg.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DiagnosisReport {
    private DiagnosisRequest diagnosisRequest;
    private DiagnosisSubmit diagnosisSubmit;
}
