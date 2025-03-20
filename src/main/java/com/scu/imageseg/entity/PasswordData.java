package com.scu.imageseg.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改密码的数据类(封装)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordData {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
