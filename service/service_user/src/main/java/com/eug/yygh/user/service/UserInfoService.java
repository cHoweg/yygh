package com.eug.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eug.yygh.model.user.UserInfo;
import com.eug.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> loginUser(LoginVo loginVo);
}
