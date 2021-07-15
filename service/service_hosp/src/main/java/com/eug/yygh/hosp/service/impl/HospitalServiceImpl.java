package com.eug.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.eug.cmn.client.DictFeignClient;
import com.eug.yygh.hosp.repository.HospitalRepository;
import com.eug.yygh.hosp.service.HospitalService;
import com.eug.yygh.model.hosp.Hospital;
import com.eug.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;


    /**
     * 上传医院接口
     *
     * @param requestMap
     */
    @Override
    public void save(Map<String, Object> requestMap) {

        // 将Map集合转换成Hospital对象
        String json = JSONObject.toJSONString(requestMap);
        Hospital hospital = JSONObject.parseObject(json, Hospital.class);

        // 判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        if (hospitalExist != null) {
            // 存在，修改
            hospital.setId(hospitalExist.getId());
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            // 不存在，添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }

    }

    /**
     * 查询医院
     *
     * @param hoscode
     * @return
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }


    /**
     * 医院列表
     *
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {

        // 创建Pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);

        // 创建条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);


        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        // 创建对象
        Example<Hospital> matcherExample = Example.of(hospital, exampleMatcher);

        Page<Hospital> all = hospitalRepository.findAll(matcherExample, pageable);

        // 查询list集合，遍历进行医院等级封装
        all.getContent().stream().forEach(hos -> {

            String name = dictFeignClient.getName("Hostype", hos.getHostype());
            hos.getParam().put("hostypeString", name);

            hos.getParam().put("fullAddress",
                    dictFeignClient.getName(hos.getProvinceCode())
                            + dictFeignClient.getName(hos.getCityCode())
                            + dictFeignClient.getName(hos.getDistrictCode()));

        });

        return all;
    }


    /**
     * 更新医院上线状态
     *
     * @param id
     * @param status
     */
    @Override
    public void updateHosStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setUpdateTime(new Date());
        hospital.setStatus(status);
        hospitalRepository.save(hospital);
    }

    /**
     * 医院信息接口
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> getHospById(String id) {

        Hospital hos = hospitalRepository.findById(id).get();

        String name = dictFeignClient.getName("Hostype", hos.getHostype());
        hos.getParam().put("hostypeString", name);

        hos.getParam().put("fullAddress",
                dictFeignClient.getName(hos.getProvinceCode())
                        + dictFeignClient.getName(hos.getCityCode())
                        + dictFeignClient.getName(hos.getDistrictCode()));

        Map<String, Object> result = new HashMap<>();
        result.put("hospital", hos);
        result.put("bookingRule", hos.getBookingRule());

        return result;
    }

    /**
     * 获取医院名称
     *
     * @param hoscode
     */
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hoscode != null) {
            return hospital.getHosname();
        }
        return null;
    }


    /**
     * 根据医院名称查询
     *
     * @param hosname
     * @return
     */
    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.getHospitalByHosnameLike(hosname);
    }


    /**
     * 根据医院编号获取医院预约挂号详细信息
     *
     * @param hoscode
     * @return
     */
    @Override
    public Map<String, Object> item(String hoscode) {

        Hospital hos = hospitalRepository.getHospitalByHoscode(hoscode);
        String name = dictFeignClient.getName("Hostype", hos.getHostype());
        hos.getParam().put("hostypeString", name);
        hos.getParam().put("fullAddress",
                dictFeignClient.getName(hos.getProvinceCode())
                        + dictFeignClient.getName(hos.getCityCode())
                        + dictFeignClient.getName(hos.getDistrictCode()));

        Map<String, Object> result = new HashMap<>();
        result.put("hospital", hos);
        result.put("bookingRule", hos.getBookingRule());
        return result;
    }
}
