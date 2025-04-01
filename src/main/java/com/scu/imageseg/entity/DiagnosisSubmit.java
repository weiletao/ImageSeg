package com.scu.imageseg.entity;

import lombok.*;

import java.util.List;

/**
 * 诊断意见数据类(封装)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiagnosisSubmit {
    private Long diagnosisId;
    private String opinion;
    private String suggestion;
    private List<List<Region>> regions; // 注意，一个图片有多个区域、有多张图片
    private List<ImageData> images;

    // 内部类用于存储图像数据
    @Setter
    @Getter
    public static class ImageData {
        // Getters 和 Setters
        private String originalImage;
        private String segmentedImage;
    }
    @Setter
    @Getter
    public static class Region {
        private Double x;
        private Double y;
        private Double width;
        private Double height;
    }

}
