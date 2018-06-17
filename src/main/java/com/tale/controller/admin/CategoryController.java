package com.tale.controller.admin;

import com.tale.constant.TaleConst;
import com.tale.controller.BaseController;
import com.tale.dto.MetaDto;
import com.tale.dto.Types;
import com.tale.service.MetasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hpxl on 18/4/18.
 */
@Controller
@RequestMapping("admin/category")
public class CategoryController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private MetasService metasService;

    @RequestMapping("")
    public String index(HttpServletRequest request) {
        List<MetaDto> categories = metasService.getMetaList(Types.CATEGORY, null, TaleConst.MAX_POSTS);
        List<MetaDto> tags = metasService.getMetaList(Types.TAG, null, TaleConst.MAX_POSTS);
        request.setAttribute("categories", categories);
        request.setAttribute("tags", tags);
        return "admin/category";
    }

}
