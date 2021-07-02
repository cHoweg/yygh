package com.eug.yygh.cmn.serivce;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eug.yygh.cmn.DictListener;
import com.eug.yygh.cmn.mapper.DictMapper;
import com.eug.yygh.model.cmn.Dict;
import com.eug.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private DictMapper dictMapper;


    /**
     * 根据数据ID查询子数据列表
     *
     * @param parentId
     * @return
     */
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    @Override
    public List<Dict> findChildData(Long parentId) {
        List<Dict> list = dictMapper.selectList(new QueryWrapper<Dict>().eq("parent_id", parentId));
        // 向list集合每个dict对象设置hasChildren
        for (Dict dict : list) {
            Long dictId = dict.getId();
            boolean isChildren = this.isChildren(dictId);
            dict.setHasChildren(isChildren);
        }
        return list;
    }


    /**
     * 判断是否有子节点
     *
     * @param id
     * @return
     */
    public boolean isChildren(Long id) {
        Integer count = dictMapper.selectCount(new QueryWrapper<Dict>().eq("parent_id", id));
        return count > 0;
    }


    /**
     * 导出数据字典
     *
     * @param httpServletResponse
     */
    @Override
    public void exportDictData(HttpServletResponse httpServletResponse) {

        // 设置下载信息
        httpServletResponse.setContentType("application/vnd.ms-excel");
        httpServletResponse.setCharacterEncoding("UTF-8");
        String fileName = null;
        try {
            fileName = URLEncoder.encode("数据字典", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        List<Dict> dictList = dictMapper.selectList(null);
        // dict---dictEeVo
        List<DictEeVo> list = new ArrayList<>(dictList.size());
        for (Dict dict :
                dictList) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);
            list.add(dictEeVo);
        }

        // 写操作
        try {
            EasyExcel.write(httpServletResponse.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入数据字典
     *
     * @param file
     */
    @CacheEvict(value = "dict", allEntries = true)
    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(dictMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据条件查询数据字典名称
     *
     * @param dictCode
     * @param value
     * @return
     */
    @Override
    public String getDictName(String dictCode, String value) {

        // 如果value能唯一定位数据字典，parentDictCode可以传空，例如：省市区的value值能够唯一确定
        if (StringUtils.isEmpty(dictCode)) {
            Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("value", value));
            if (null != dict) {
                return dict.getName();
            }
        } else {
            Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code", dictCode));
            System.out.println(dict);
            if (null != dict) {
                return dictMapper.selectOne(new QueryWrapper<Dict>()
                        .eq("parent_id", dict.getId())
                        .eq("value", value))
                        .getName();
            }
        }
        return "";
    }

}