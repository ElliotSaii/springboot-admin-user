package com.techguy.vo;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppTokenVo implements Serializable {
    private Integer memberId;
    private String account;
}
