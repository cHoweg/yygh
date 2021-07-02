package com.eug.yygh.hosp.repository;

import com.eug.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    // 查询排班信息
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
