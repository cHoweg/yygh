package com.eug.yygh.hosp.service;

import com.eug.yygh.model.hosp.Schedule;
import com.eug.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {

    void save(Map<String, Object> requestMap);

    Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);
}
