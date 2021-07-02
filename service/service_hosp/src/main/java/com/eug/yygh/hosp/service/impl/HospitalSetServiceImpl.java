package com.eug.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eug.yygh.hosp.mapper.HospitalSetMapper;
import com.eug.yygh.hosp.service.HospitalSetService;
import com.eug.yygh.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    /*
        根据医院编码查询签名
     */
    @Override
    public String getSignKey(String hoscode) {
        return hospitalSetMapper.selectOne(new QueryWrapper<HospitalSet>().eq("hoscode", hoscode)).getSignKey();
    }
}
