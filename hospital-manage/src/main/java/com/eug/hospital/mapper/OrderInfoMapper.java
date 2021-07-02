package com.eug.hospital.mapper;

import com.eug.hospital.model.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

}
