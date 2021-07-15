package com.eug.yygh.hosp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eug.yygh.common.result.Result;
import com.eug.yygh.common.utils.MD5;
import com.eug.yygh.hosp.service.HospitalSetService;
import com.eug.yygh.model.hosp.HospitalSet;
import com.eug.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Random;


@Api(tags = "医院设置管理")
@RestController
@RequestMapping("admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAllHospitalSet")
    public Result findAllHospitalSet() {
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else return Result.fail();
    }

    @ApiOperation(value = "条件查询带分页")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current, @PathVariable long limit, @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> page = new Page<>(current, limit);

        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hoscode = hospitalSetQueryVo.getHoscode();
        String hosname = hospitalSetQueryVo.getHosname();

        if (!StringUtils.isEmpty(hosname)) wrapper.like("hosname", hosname);
        if (!StringUtils.isEmpty(hoscode)) wrapper.eq("hoscode", hoscode);

        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);

        return Result.ok(hospitalSetPage);
    }

    @ApiOperation(value = "添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        // 状态 1使用 0不可用
        hospitalSet.setStatus(1);

        // 设置密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));

        boolean save = hospitalSetService.save(hospitalSet);
        if (save) return Result.ok();
        else return Result.fail();

    }

    @ApiOperation(value = "根据ID获取医院设置")
    @GetMapping("getHospitalSet/{id}")
    public Result getHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    @ApiOperation(value = "修改医院设置")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b) return Result.ok();
        else return Result.fail();
    }

    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemoveHospitalSet")
    public Result batchRemoveHospitalSet(@RequestBody List<String> idList) {
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    @ApiOperation(value = "医院锁定和解锁设置")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id, @PathVariable Integer status) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    @ApiOperation("发送签名密钥")
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hosCode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }
}
