package com.tale.controller.admin;

import com.tale.model.entity.Contents;
import com.tale.utils.Query;
import com.tale.constant.TaleConst;
import com.tale.controller.BaseController;
import com.tale.dto.LogActions;
import com.tale.model.Bo.RestResponseBo;
import com.tale.dto.Types;
import com.tale.model.entity.Users;
import com.tale.service.ContentsService;
import com.tale.service.LogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 17/4/18.
 */
@Controller
@RequestMapping("admin/page")
public class PageController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    @Autowired
    private ContentsService contentsService;

    @Autowired
    private LogsService logsService;

    @GetMapping("")
    public String index(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                        @RequestParam(value = "limit", required = false, defaultValue = "15") int limit,
                        HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", page);
        params.put("limit", limit);
        params.put("type", Types.PAGE);
        Query query = new Query(params);
        List<Contents> articles = contentsService.queryList(query);
        request.setAttribute("articles", articles);
        return "admin/page_list";
    }

    @GetMapping(value = "new")
    public String newPage(HttpServletRequest request) {
        return "admin/page_edit";
    }

    @PostMapping(value = "publish")
    @ResponseBody
    public RestResponseBo publish(@RequestParam String title, @RequestParam String content,
                                  @RequestParam String status, @RequestParam String slug,
                                  @RequestParam(required = false) Integer allowComment,
                                  @RequestParam(required = false) Integer allowPing,
                                  HttpServletRequest request) {
        Users users = this.user(request);
        Contents contents = new Contents();
        contents.setAuthorId(users.getUid());
        contents.setTitle(title);
        contents.setContent(content);
        contents.setStatus(status);
        contents.setSlug(slug);
        contents.setType(Types.PAGE);
        if (null != allowComment) {
            contents.setAllowComment(allowComment == 1);
        }
        if (null != allowPing) {
            contents.setAllowPing(allowPing == 1);
        }
        String result = contentsService.publish(contents);
        if (!TaleConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }

    @RequestMapping(value = "/{cid}", method = RequestMethod.GET)
    public String editArticle(@PathVariable String cid, HttpServletRequest request) {
        // 获取文章内容
        Contents contents = contentsService.getContents(cid);
        request.setAttribute("contents", contents);
        return "admin/page_edit";
    }

    @PostMapping(value = "modify")
    @ResponseBody
    public RestResponseBo modifyArticle(@RequestParam Integer cid, @RequestParam String title,
                                        @RequestParam String content, @RequestParam String status,
                                        @RequestParam String slug, @RequestParam(required = false) Integer allowComment,
                                        @RequestParam(required = false) Integer allowPing,
                                        HttpServletRequest request) {
        Users users = this.user(request);
        Contents contents = new Contents();
        contents.setCid(cid);
        contents.setAuthorId(users.getUid());
        contents.setTitle(title);
        contents.setContent(content);
        contents.setStatus(status);
        contents.setSlug(slug);
        contents.setType(Types.PAGE);
        if (null != allowComment) {
            contents.setAllowComment(allowComment == 1);
        }
        if (null != allowPing) {
            contents.setAllowPing(allowPing == 1);
        }
        String result = contentsService.updateArticle(contents);
        if (!TaleConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }

    /**
     * 删除文章
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam int cid, HttpServletRequest request) {
        String result = contentsService.deleteByCid(cid);
        logsService.insertLog(LogActions.DEL_PAGE.getAction(), cid + "",
                request.getRemoteAddr(), this.getUid(request));
        if (!TaleConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }
}
