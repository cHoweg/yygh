package com.eug.yygh.cmn.controller;

import com.eug.yygh.cmn.serivce.DictService;
import com.eug.yygh.common.result.Result;
import com.eug.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典接口")
@RestController
@RequestMapping("admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("根据数据ID查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }

    @ApiOperation("根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

    @ApiOperation("导出数据字典")
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse httpServletResponse) {
        dictService.exportDictData(httpServletResponse);
    }

    @ApiOperation("导入数据字典")
    @PostMapping("importData")
    public void importDict(MultipartFile file) {
        dictService.importDictData(file);
    }

    @ApiOperation("查询数据字典名称")
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode, @PathVariable String value) {
        return dictService.getDictName(dictCode, value);
    }

    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value) {
        return dictService.getDictName("", value);
    }
}
