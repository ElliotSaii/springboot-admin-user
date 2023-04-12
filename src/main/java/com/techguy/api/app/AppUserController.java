package com.techguy.api.app;

import com.techguy.api.vo.AppUserRegisterVo;
import com.techguy.api.vo.ResultVo;
import com.techguy.common.annotation.AccessLimit;
import com.techguy.type.AccessLimitType;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/app", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AppUserController {

    @PostMapping("/register")
    @AccessLimit(type = AccessLimitType.IP, key = "u-register", limit = 5,blockWhenExceed = true,blockMinutes = 2)
    public ResultVo register(@Validated @RequestBody AppUserRegisterVo reqVo) {
//        MyCaptchaUtils.validateCaptcha(reqVo.getCoderef(), reqVo.getCaptcha());
//        MemberLoginRespVo register = authService.register(reqVo);
        return ResultVo.success(reqVo);
    }
}
