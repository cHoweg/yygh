package com.eug.yygh.user.controller;


import com.eug.yygh.common.result.Result;
import com.eug.yygh.user.service.UserInfoService;
import com.eug.yygh.vo.user.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(value = "登录接口")
@RestController
@RequestMapping("/api/user")
public class UserInfoAPIController {

    @Autowired
    private UserInfoService userInfoService;

    // 用户手机号登录
    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        Map<String, Object> map = userInfoService.loginUser(loginVo);
        return Result.ok(map);
    }
}