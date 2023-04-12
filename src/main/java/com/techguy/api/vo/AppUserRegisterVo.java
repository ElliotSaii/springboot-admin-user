package com.techguy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRegisterVo {


    @NotNull(message= "Name can't be null")
    @NotEmpty(message= "Name can't be empty")
    @NotBlank(message= "Name can't be blank")
    private String name;

    @NotNull(message= "email can't be null")
    @NotEmpty(message= "email can't be empty")
    @NotBlank(message= "email can't be blank")
    private String email;
}
