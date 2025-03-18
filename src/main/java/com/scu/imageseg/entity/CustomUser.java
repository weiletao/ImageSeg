package com.scu.imageseg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String fullName;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private String role;

    private Boolean isActive;

    private String bio;

    private Boolean isStaff;

    private Boolean isSuperuser;

    private LocalDateTime dateJoined;


}
