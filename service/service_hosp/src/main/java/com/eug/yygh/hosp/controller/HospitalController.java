package com.eug.yygh.hosp.controller;

import com.eug.yygh.common.result.Result;
import com.eug.yygh.hosp.service.HospitalService;
import com.eug.yygh.model.hosp.Hospital;
import com.eug.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
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
}
