package com.eug.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.eug.yygh.hosp.repository.DepartmentRepository;
import com.eug.yygh.hosp.service.DepartmentService;
import com.eug.yygh.model.hosp.Department;
import com.eug.yygh.vo.hosp.DepartmentQueryVo;
import com.eug.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            BeanUtils.copyProperties(department, departmentExist, Department.class);
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
        BeanUtils.copyProperties(departmentQueryVo, department);
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

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        // 根据医院编号查询科室列表
        List<DepartmentVo> result = new ArrayList<>();

        Department department = new Department();
        department.setHoscode(hoscode);

        Example<Department> example = Example.of(department);

        List<Department> departmentList = departmentRepository.findAll(example);

        // 根据大科室编号，bigcode分组，获取每个大科室下的子科室
        Map<String, List<Department>> departmentMap = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {

            // 大科室编号
            String bigCode = entry.getKey();
            // 大科室全部数据
            List<Department> departments = entry.getValue();

            // 封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(departments.get(0).getBigname());

            // 封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department dpm : departments) {
                DepartmentVo vo = new DepartmentVo();
                vo.setDepcode(dpm.getDepcode());
                vo.setDepname(dpm.getDepname());
                children.add(vo);
            }

            // 小科室存到大科室中
            departmentVo.setChildren(children);

            // 存放到集合中返回
            result.add(departmentVo);
        }
        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {

        // 根据医院编号、科室编号查询科室名字
        Department departmentByHoscodeAndDepcode = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (departmentByHoscodeAndDepcode != null) {
            return departmentByHoscodeAndDepcode.getDepname();
        }
        return null;
    }

}
