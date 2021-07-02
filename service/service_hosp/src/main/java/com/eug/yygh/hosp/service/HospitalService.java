package com.eug.yygh.hosp.service;

import com.eug.yygh.model.hosp.Hospital;
import com.eug.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HospitalService {


    void save(Map<String, Object> requestMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
}
