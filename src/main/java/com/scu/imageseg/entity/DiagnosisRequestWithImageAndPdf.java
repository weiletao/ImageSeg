package com.scu.imageseg.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DiagnosisRequestWithImageAndPdf extends DiagnosisRequestWithImage{
    private String pdfPath;
}
