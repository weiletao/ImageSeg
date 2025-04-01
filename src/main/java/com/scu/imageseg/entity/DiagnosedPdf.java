package com.scu.imageseg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wlt
 * @since 2025-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DiagnosedPdf implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String pdfPath;

    private LocalDateTime updatedAt;

    private Long diagnosisRequestId;


}
