package com.scu.imageseg.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class DiagnosisRequestWithImage extends DiagnosisRequest{
    private List<String> file_path;
}
