package com.eug.yygh.cmn.serivce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eug.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void exportDictData(HttpServletResponse httpServletResponse);

    void importDictData(MultipartFile file);

    String getDictName(String dictCode, String value);
}
