package com.eug.yygh.hosp.controller.api;

import com.eug.yygh.common.exception.YyghException;
import com.eug.yygh.common.helper.HttpRequestHelper;
import com.eug.yygh.common.result.Result;
import com.eug.yygh.common.result.ResultCodeEnum;
import com.eug.yygh.common.utils.MD5;
import com.eug.yygh.hosp.service.DepartmentService;
import com.eug.yygh.hosp.service.HospitalService;
import com.eug.yygh.hosp.service.HospitalSetService;
import com.eug.yygh.hosp.service.ScheduleService;
import com.eug.yygh.model.hosp.Department;
import com.eug.yygh.model.hosp.Hospital;
import com.eug.yygh.model.hosp.Schedule;
import com.eug.yygh.vo.hosp.DepartmentQueryVo;
import com.eug.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院接口设置管理")
@RestController
@RequestMapping("/api/hosp")
@CrossOrigin
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 验证签名方法
     *
     * @param requestMap
     */
    public void eqSign(Map requestMap) {
        String hospSign = (String) requestMap.get("sign");
        String hoscode = (String) requestMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String encrypt = MD5.encrypt(signKey);

        if (!hospSign.equals(encrypt)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    /**
     * 获取传递的参数信息
     *
     * @param httpServletRequest
     * @return
     */
    public Map<String, Object> getRequestMap(HttpServletRequest httpServletRequest){
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> requestMap = HttpRequestHelper.switchMap(parameterMap);
        return requestMap;
    }


    @ApiOperation(value = "查询科室信息")
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest httpServletRequest) {

        Map<String, Object> requestMap = getRequestMap(httpServletRequest);

        // 签名校验
        eqSign(requestMap);

        // 医院编号
        String hoscode = (String) requestMap.get("hoscode");

        Integer page = StringUtils.isEmpty(requestMap.get("page")) ? 1 : Integer.parseInt((String) requestMap.get("page"));  //当前页
        Integer limit = StringUtils.isEmpty(requestMap.get("limit")) ? 1 : Integer.parseInt((String) requestMap.get("limit"));  //每页记录数

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "上传科室信息")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest httpServletRequest) {

        Map<String, Object> requestMap = getRequestMap(httpServletRequest);

        eqSign(requestMap);

        departmentService.save(requestMap);
        return Result.ok();
    }

    @ApiOperation(value = "查询医院")
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest httpServletRequest) {

        Map<String, Object> requestMap = getRequestMap(httpServletRequest);

        String hoscode = (String) requestMap.get("hoscode");
        eqSign(requestMap);

        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }


    @ApiOperation(value = "上传医院接口")
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest httpServletRequest) {

        // 获取传递的医院信息
        Map<String, Object> requestMap = getRequestMap(httpServletRequest);

        // 获取签名并比对
        eqSign(requestMap);

        // 图片转换Base64 " "转换为"+"
        String logoData = (String) requestMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        requestMap.put("logoData", logoData);

        // 存储
        hospitalService.save(requestMap);
        return Result.ok();
    }

    @ApiOperation(value = "删除科室信息接口")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest httpServletRequest) {

        Map<String, Object> requestMap = getRequestMap(httpServletRequest);

        eqSign(requestMap);

        departmentService.remove((String) requestMap.get("hoscode"), (String) requestMap.get("depcode"));
        return Result.ok();
    }


    @ApiOperation(value = "上传排班接口")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest httpServletRequest) {

        Map<String, Object> requestMap = getRequestMap(httpServletRequest);
        eqSign(requestMap);

        scheduleService.save(requestMap);
        return Result.ok();
    }


    @ApiOperation(value = "查询排班接口")
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest httpServletRequest){

        Map<String, Object> requestMap = getRequestMap(httpServletRequest);
        eqSign(requestMap);

        String hoscode = (String) requestMap.get("hoscode");
        String dopcode  = (String) requestMap.get("dopcode");

        Integer page = StringUtils.isEmpty(requestMap.get("page")) ? 1 : Integer.parseInt((String) requestMap.get("page"));  //当前页
        Integer limit = StringUtils.isEmpty(requestMap.get("limit")) ? 1 : Integer.parseInt((String) requestMap.get("limit"));  //每页记录数

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(dopcode);

        Page<Schedule> pageModel = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);

        return Result.ok(pageModel);
    }


    @ApiOperation(value = "删除排班接口")
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest httpServletRequest){

        Map<String, Object> requestMap = getRequestMap(httpServletRequest);
        eqSign(requestMap);

        String hoscode = (String) requestMap.get("hoscode");
        String hosScheduleId = (String) requestMap.get("hosScheduleId");

        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }
}