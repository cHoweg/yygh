package com.eug.yygh.hosp.controller;

import com.eug.yygh.common.result.Result;
import com.eug.yygh.hosp.service.HospitalService;
import com.eug.yygh.model.hosp.Hospital;
import com.eug.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Api("医院管理接口")
@RestController
@RequestMapping("admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;


    /**
     * 医院列表
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "医院列表")
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page, @PathVariable Integer limit, HospitalQueryVo hospitalQueryVo) {

        Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "更新医院上线状态")
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHosStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateHosStatus(id,status);
        return Result.ok();
    }


    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        return Result.ok(hospitalService.getHospById(id));
    }
}
