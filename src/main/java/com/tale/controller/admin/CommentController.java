package com.tale.controller.admin;

import com.tale.controller.BaseController;
import com.tale.model.Bo.RestResponseBo;
import com.tale.model.entity.Comments;
import com.tale.model.entity.Users;
import com.tale.service.CommentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 1/5/18.
 */
@Controller
@RequestMapping("admin/comments")
public class CommentController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentsService commentsService;

    @RequestMapping(value="", method = RequestMethod.GET)
    public String index(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                        @RequestParam(value = "limit", required = false, defaultValue = "15") int limit,
                        HttpServletRequest request) {

        Users users = this.user(request);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", page);
        map.put("limit", limit);
        List<Comments> comments = commentsService.queryList(map);
        request.setAttribute("comments", comments);
        return "admin/comment_list";
    }

    /**
     * 删除一条评论
     *
     * @param coid
     * @param request
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public RestResponseBo delete(@RequestParam int coid, HttpServletRequest request) {
        try {
            Comments comments = commentsService.getCommentById(coid);
            if (null == comments) {
                return RestResponseBo.fail("不存在该评论");
            }
            commentsService.delete(comments.getCoid(), comments.getCid());
        } catch (Exception e) {
            String msg = "评论删除失败";
            LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    /**
     * 更新评论状态
     * @param coid
     * @param status
     * @param request
     * @return
     */
    @RequestMapping(value = "status", method = RequestMethod.POST)
    @ResponseBody
    public RestResponseBo status(@RequestParam int coid, @RequestParam String status, HttpServletRequest request) {
        try {
            Comments comments = commentsService.getCommentById(coid);
            if (null != comments) {
                comments.setCoid(coid);
                comments.setStatus(status);
                commentsService.update(comments);
            } else {
                return RestResponseBo.fail("操作失败");
            }
        } catch (Exception e) {
            String msg = "操作失败";
            LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }
}
