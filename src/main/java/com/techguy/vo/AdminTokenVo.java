package com.techguy.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTokenVo implements Serializable {
    private Integer adminId;
    private String uname;

}