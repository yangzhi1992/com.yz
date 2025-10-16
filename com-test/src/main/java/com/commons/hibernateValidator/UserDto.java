package com.commons.hibernateValidator;

import com.commons.hibernateValidator.group.CreateGroup;
import com.commons.hibernateValidator.group.UpdateGroup;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.Data;

@Data
public class UserDto {
    @PhoneNumber
    private String phone;

    @Null(groups = CreateGroup.class)
    @NotNull(groups = UpdateGroup.class)
    private Long id;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String username;
}
