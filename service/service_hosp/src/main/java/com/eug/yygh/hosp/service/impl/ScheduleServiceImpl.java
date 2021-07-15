package com.eug.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.eug.yygh.hosp.repository.ScheduleRepository;
import com.eug.yygh.hosp.service.DepartmentService;
import com.eug.yygh.hosp.service.HospitalService;
import com.eug.yygh.hosp.service.ScheduleService;
import com.eug.yygh.model.hosp.Schedule;
import com.eug.yygh.vo.hosp.BookingScheduleRuleVo;
import com.eug.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;


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


    /**
     * 根据医院编号 和 科室编号，查询排班规则数据
     *
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    @Override
    public Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode) {

        // 根据医院编号和科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // 根据工作日分组
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),// 匹配条件
                Aggregation.group("workDate")// 分组字段
                        .first("workDate").as("workDate")

                        // 统计号源量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.DEFAULT_DIRECTION.DESC, "workDate"),

                /// 实现分页
                Aggregation.skip((page - 1) * limit), Aggregation.limit(limit)
        );

        // 最终执行
        AggregationResults<BookingScheduleRuleVo> results = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        // 结果集
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = results.getMappedResults();

        // 分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResult = mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResult.getMappedResults().size();

        // 日期对应星期
        for (BookingScheduleRuleVo bookingScheduleRuleVo :
                bookingScheduleRuleVoList) {
            String dayOfWeek = this.getDayOfWeek(new DateTime(bookingScheduleRuleVo.getWorkDate()));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        // 设置最终返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);

        // 获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        // 其他基础数据
        Map<Object, Object> baseMap = new HashMap<>();
        baseMap.put("hosname", hosName);
        result.put("baseMap", baseMap);

        return result;
    }

    /**
     * 根据医院编号、科室编号和工作日期，查询排班详细信息
     *
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {

        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());

        // 设置其他值
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return scheduleList;
    }





    /**
     * 设置医院名称、科室名称、日期星期
     *
     * @param schedule
     */
    private void packageSchedule(Schedule schedule) {
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        schedule.getParam().put("depcode", departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * 根据日期获取周几数据
     *
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}
