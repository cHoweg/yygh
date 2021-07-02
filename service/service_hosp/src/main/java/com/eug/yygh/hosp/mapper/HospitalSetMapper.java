package com.eug.yygh.hosp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eug.yygh.model.hosp.HospitalSet;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {

}
