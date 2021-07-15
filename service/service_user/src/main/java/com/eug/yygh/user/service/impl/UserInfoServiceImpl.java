package com.eug.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eug.yygh.common.exception.YyghException;
import com.eug.yygh.common.helper.JwtHelper;
import com.eug.yygh.common.result.ResultCodeEnum;
import com.eug.yygh.model.user.UserInfo;
import com.eug.yygh.user.mapper.UserInfoMapper;
import com.eug.yygh.user.service.UserInfoService;
import com.eug.yygh.vo.user.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    // 手机号登录接口
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {

        // 获取手机号的验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 判空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //TODO 判断验证码是否一致

        // 判断是否第一次登录
        // 查询数据库是否有数据
        UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("phone", phone));
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            userInfoMapper.insert(userInfo);
        }

        //校验是否被禁用
        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        // 返回登录信息
        // 返回登录用户名
        // 返回token信息
        // 返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        // token生成
        map.put("token", JwtHelper.createToken(userInfo.getId(), name));
        return map;
    }
}
