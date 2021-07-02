package com.eug.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.eug.yygh.hosp.repository.ScheduleRepository;
import com.eug.yygh.hosp.service.ScheduleService;
import com.eug.yygh.model.hosp.Schedule;
import com.eug.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;


    /**
     * 上传排班信息
     *
     * @param requestMap
     */
    @Override
    public void save(Map<String, Object> requestMap) {

        Schedule schedule = JSONObject.parseObject(
                JSONObject.toJSONString(requestMap), Schedule.class);

        // 判断是否存在
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (scheduleExist != null) {
            String id = scheduleExist.getId();
            BeanUtils.copyProperties(schedule, scheduleExist, Schedule.class);
            scheduleExist.setId(id);
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }


    /**
     * 查询排班信息
     *
     * @param page
     * @param limit
     * @param scheduleQueryVo
     * @return
     */
    @Override
    public Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {
        // 创建Pageable 设置当前页和每页记录数
        Pageable pageable = PageRequest.of(page - 1, limit);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);

        // 创建Example对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, exampleMatcher);

        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
        return all;
    }


    /**
     * 删除排班信息
     *
     * @param hoscode
     * @param hosScheduleId
     */
    @Override
    public void remove(String hoscode, String hosScheduleId) {

        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);

        if (scheduleExist != null) {
            scheduleRepository.deleteById(scheduleExist.getId());
        }
    }

}
