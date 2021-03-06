package com.eug.yygh.hosp.repository;

import com.eug.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    // 查询医院科室信息
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
