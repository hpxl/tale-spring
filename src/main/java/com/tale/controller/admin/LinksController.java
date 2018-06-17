package com.tale.controller.admin;

import com.tale.controller.BaseController;
import com.tale.model.Bo.RestResponseBo;
import com.tale.dto.Types;
import com.tale.model.entity.Metas;
import com.tale.service.MetasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hpxl on 12/4/18.
 */
@Controller
@RequestMapping("/admin/links")
public class LinksController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinksController.class);

    @Autowired
    private MetasService metasService;

    /**
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                        @RequestParam(value = "limit", required = false, defaultValue = "15") int limit,
                        HttpServletRequest request) {
        List<Metas> metas = metasService.getMetas(Types.LINK);
        request.setAttribute("links", metas);
        return "admin/links";
    }

    /**
     * 保存
     * @return
     */
    @PostMapping(value = "save")
    @ResponseBody
    public RestResponseBo saveLink(@RequestParam String title, @RequestParam String url,
                                   @RequestParam String logo, @RequestParam Integer mid,
                                   @RequestParam(value = "sort", defaultValue = "0") int sort) {
        try {
            Metas metas = new Metas();
            metas.setName(title);
            metas.setSlug(url);
            metas.setDescription(logo);
            metas.setSort(sort);
            metas.setType(Types.LINK);
            if (null != mid) {
                metas.setMid(mid);
                metasService.update(metas);
            } else {
                metasService.saveMetas(metas);
            }
        } catch (Exception e) {
            String msg = "友链保存失败";
            LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @PostMapping(value = "delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam int mid) {
        try {
            metasService.delete(mid);
        } catch (Exception e) {
            String msg = "友链删除失败";
            LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }
}
