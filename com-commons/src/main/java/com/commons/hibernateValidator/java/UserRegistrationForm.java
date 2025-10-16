package com.commons.hibernateValidator.java;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationForm {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度4-20")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码至少8位")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
             message = "密码必须包含大小写字母和数字")
    private String password;

    @Email(message = "无效邮箱格式")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "无效电话号码")
    private String phone;

    @Min(value = 18, message = "年龄必须≥18岁")
    @Max(value = 120, message = "年龄必须≤120岁")
    private Integer age;

    @AssertTrue(message = "必须接受条款")
    private boolean termsAccepted;

    @Valid
    private CheckValidatorExample.Address address;
}
