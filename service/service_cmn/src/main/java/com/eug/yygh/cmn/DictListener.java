package com.eug.yygh.cmn;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.eug.yygh.cmn.mapper.DictMapper;
import com.eug.yygh.model.cmn.Dict;
import com.eug.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    /**
     * 逐行读取
     * @param dictEeVo
     * @param analysisContext
     */
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
