package com.tale.controller.admin;

import com.tale.constant.TaleConst;
import com.tale.controller.BaseController;
import com.tale.dto.LogActions;
import com.tale.exception.TipException;
import com.tale.model.Bo.RestResponseBo;
import com.tale.service.LogsService;
import com.tale.service.UserService;
import com.tale.model.entity.Users;
import com.tale.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by hpxl on 6/5/18.
 */
@Controller
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class AuthController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LogsService logsService;

    @GetMapping(value = "/login")
    public String login(HttpServletRequest request) {
        return "admin/login";
    }

    @PostMapping(value = "login")
    @ResponseBody
    public RestResponseBo doLogin(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam(required = false) String remember_me,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        Integer error_count = cache.get("login_error_count");
        try {
            Users users = userService.login(username, password);
            request.getSession().setAttribute(TaleConst.LOGIN_SESSION_KEY, users);
            if (StringUtils.isNotBlank(remember_me)) {
                TaleUtils.setCookie(response, users.getUid());
            }
            logsService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), users.getUid());
        } catch (Exception e) {
            error_count = null == error_count ? 1 : error_count + 1;
            if (error_count > 3) {
                return RestResponseBo.fail("您输入密码已经错误超过3次，请10分钟后尝试");
            }
            cache.set("login_error_count", error_count, 10 * 60);
            String msg = "登录失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    /**
     * 注销
     * @param session
     * @param request
     * @param response
     */
    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        session.removeAttribute(TaleConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie(TaleConst.USER_IN_COOKIE, "");
        cookie.setValue(null);
        cookie.setMaxAge(0); // 立即销毁cookie
        cookie.setPath("/");
        response.addCookie(cookie);
        try {
            response.sendRedirect("/admin/login");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }
    }
}
