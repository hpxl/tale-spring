package com.tale.controller.admin;

import com.tale.constant.TaleConst;
import com.tale.controller.BaseController;
import com.tale.dto.LogActions;
import com.tale.exception.TipException;
import com.tale.model.Bo.RestResponseBo;
import com.tale.model.Bo.StatisticsBo;
import com.tale.model.entity.Comments;
import com.tale.model.entity.Contents;
import com.tale.model.entity.Logs;
import com.tale.model.entity.Users;
import com.tale.service.LogsService;
import com.tale.service.SiteService;
import com.tale.service.UserService;
import com.tale.utils.GsonUtils;
import com.tale.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by hpxl on 1/5/18.
 */
@Controller("adminIndexController")
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class IndexController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private SiteService siteService;

    @Autowired
    private LogsService logsService;

    @Autowired
    private UserService userService;

    @GetMapping(value = {"","/index"})
    public String index(HttpServletRequest request) {
        LOGGER.info("Enter admin index method");
        List<Comments> comments = siteService.recentComments(10);
        List<Contents> contents = siteService.recentContents(10);

        StatisticsBo statistics = siteService.getStatistics();

        // 取最新的20条日志
        List<Logs> logs = logsService.getLogs(1, 5);

        request.setAttribute("comments", comments);
        request.setAttribute("articles", contents);
        request.setAttribute("statistics", statistics);
        request.setAttribute("logs", logs);
        LOGGER.info("Exit admin index method");
        return "admin/index";
    }

    /**
     * 个人设置页面
     */
    @GetMapping(value = "profile")
    public String profile() {
        return "admin/profile";
    }

    /**
     * 保存个人信息
     * @param screenName
     * @param email
     * @param request
     * @param httpSession
     * @return
     */
    @PostMapping(value = "profile")
    @ResponseBody
    public RestResponseBo saveProfile(@RequestParam String screenName, @RequestParam String email,
                                      HttpServletRequest request, HttpSession httpSession) {
        Users users = this.user(request);
        if (StringUtils.isNotBlank(screenName) && StringUtils.isNotBlank(email)) {
            Users temp = new Users();
            temp.setUid(users.getUid());
            temp.setScreenName(screenName);
            temp.setEmail(email);
            userService.updateByUid(temp);
            logsService.insertLog(LogActions.UP_INFO.toString(), GsonUtils.toJsonString(temp), request.getRemoteAddr(), this.getUid(request));

            //更新session中的数据
            Users original = (Users) httpSession.getAttribute(TaleConst.LOGIN_SESSION_KEY);
            original.setScreenName(screenName);
            original.setEmail(email);
            httpSession.setAttribute(TaleConst.LOGIN_SESSION_KEY, original);
        }

        return RestResponseBo.ok();
    }

    @PostMapping(value = "password")
    @ResponseBody
    public RestResponseBo upPwd(@RequestParam String oldPassword, @RequestParam String password,
                                HttpServletRequest request, HttpSession session) {
        Users users = this.user(request);
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)) {
            return RestResponseBo.fail("请确认信息输入完整");
        }

        if (!users.getPassword().equals(TaleUtils.MD5encode(users.getUsername() + oldPassword))) {
            return RestResponseBo.fail("旧密码错误");
        }

        if (password.length() < 6 || password.length() > 14) {
            return RestResponseBo.fail("请输入6-14位密码");
        }

        try {
            Users temp = new Users();
            String pwd = TaleUtils.MD5encode(users.getUsername() + password);
            temp.setPassword(pwd);
            temp.setUid(users.getUid());
            userService.updateByUid(users);
            logsService.insertLog(LogActions.UP_PWD.toString(), null, request.getRemoteAddr(), this.getUid(request));

            // update session
            Users original = (Users)session.getAttribute(TaleConst.LOGIN_SESSION_KEY);
            original.setPassword(pwd);
            session.setAttribute(TaleConst.LOGIN_SESSION_KEY, original);
        } catch (Exception e) {
            String msg = "修改密码失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }
}
