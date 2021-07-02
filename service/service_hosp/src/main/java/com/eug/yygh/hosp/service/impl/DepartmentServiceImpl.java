package com.eug.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.eug.yygh.hosp.repository.DepartmentRepository;
import com.eug.yygh.hosp.service.DepartmentService;
import com.eug.yygh.model.hosp.Department;
import com.eug.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> requestMap) {
        Department department = JSONObject.parseObject(
                JSONObject.toJSONString(requestMap), Department.class);

        // 判断是否存在
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        if (departmentExist != null) {
            BeanUtils.copyProperties(department,departmentExist,Department.class);
            departmentExist.setId(department.getId());
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {

        // 创建Pageable 设置当前页和每页记录数
        Pageable pageable = PageRequest.of(page - 1, limit);

        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);

        // 创建Example对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase(true);
        Example<Department> example = Example.of(department, exampleMatcher);

        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {

        // 根据医院编号和科室编号查询
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);

        if (department != null) {
            departmentRepository.deleteById(department.getId());  //调用方法删除
        }
    }

}
