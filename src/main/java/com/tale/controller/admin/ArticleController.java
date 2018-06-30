package com.tale.controller.admin;

import com.tale.constant.TaleConst;
import com.tale.controller.BaseController;
import com.tale.model.entity.Contents;
import com.tale.utils.Query;
import com.tale.dto.LogActions;
import com.tale.exception.TipException;
import com.tale.model.entity.Metas;
import com.tale.dto.Types;
import com.tale.model.entity.Users;
import com.tale.model.Bo.RestResponseBo;
import com.tale.service.ContentsService;
import com.tale.service.LogsService;
import com.tale.service.MetasService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 4/3/18.
 */
@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = TipException.class)
public class ArticleController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ContentsService contentsService;

    @Autowired
    private MetasService metasService;

    @Autowired
    private LogsService logsService;

    /**
     * 文章管理首页
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "15") int limit,
            HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", page);
        params.put("limit", limit);
        params.put("type", Types.ARTICLE);
        Query query = new Query(params);
        List<Contents> articles = contentsService.queryList(query);
        request.setAttribute("articles", articles);
        return "admin/article_list";
    }

    @RequestMapping(value = "/{cid}", method = RequestMethod.GET)
    public String editArticle(@PathVariable String cid, HttpServletRequest request) {
        // 获取文章内容
        Contents contents = contentsService.getContents(cid);
        request.setAttribute("contents", contents);

        // 获取文章分类
        List<Metas> categories = metasService.getMetas(Types.CATEGORY);
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");

        return "admin/article_edit";
    }

    /**
     * 发布文章
     * @param request
     * @return
     */
    @RequestMapping(value = "/publish")
    public String newArticle(HttpServletRequest request) {
        // 获取文章分类
        List<Metas> categories = metasService.getMetas(Types.CATEGORY);
        request.setAttribute("categories", categories);
        return "admin/article_edit";
    }

    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponseBo publish(Contents contents, HttpServletRequest request) {
        Users users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE);
        if (StringUtils.isBlank(contents.getCategories())) {
            contents.setCategories("默认分类");
        }
        String result = contentsService.publish(contents);
        if (!TaleConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }

    /**
     * 编辑文章
     * @param contents
     * @param request
     * @return
     */
    @PostMapping(value = "/modify")
    @ResponseBody
    public RestResponseBo modifyArticle(Contents contents, HttpServletRequest request) {
        try {
            if (null == contents || null == contents.getCid()) {
                return RestResponseBo.fail("缺少参数，请重试");
            }
            Users users = this.user(request);
            contents.setAuthorId(users.getUid());
            contents.setType(Types.ARTICLE);
            String result = contentsService.updateArticle(contents);
            if (!TaleConst.SUCCESS_RESULT.equals(result)) {
                return RestResponseBo.fail(result);
            }
            return RestResponseBo.ok();
        } catch (Exception e) {
            String msg = "文章编辑失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
    }

    /**
     * 删除文章
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam int cid, HttpServletRequest request) {
        String result = contentsService.deleteByCid(cid);
        logsService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid + "",
                request.getRemoteAddr(), this.getUid(request));
        if (!TaleConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }
}
