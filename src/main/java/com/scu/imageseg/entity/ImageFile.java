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
 * @since 2025-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ImageFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String filePath;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long diagnosisRequestId;


}
