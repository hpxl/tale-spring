package com.tale.interceptor;

import com.tale.model.entity.Options;
import com.tale.model.entity.Users;
import com.tale.service.OptionsService;
import com.tale.service.UserService;
import com.tale.utils.TaleUtils;
import com.tale.utils.IPKit;
import com.tale.utils.MapCache;
import com.tale.utils.Commons;
import com.tale.utils.AdminCommons;
import com.tale.utils.UUID;
import com.tale.dto.Types;
import com.tale.constant.TaleConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hpxl on 13/3/18.
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseInterceptor.class);
    private static final String USER_AGENT = "user-agent";

    @Autowired
    private Commons commons;

    @Autowired
    private AdminCommons adminCommons;

    @Autowired
    private UserService userService;

    @Autowired
    private OptionsService optionsService;

    private MapCache cache = MapCache.single();

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();

        // 禁止该ip访问
        String ip = IPKit.getIpAddrByRequest(request);
        if (TaleConst.BLOCK_IPS.contains(ip)) {
            response.getOutputStream().write("You have been banned, brother".getBytes());
            LOGGER.info("You have been banned, ip: {}", ip);
            return false;
        }

        if (!TaleConst.INSTALLED && !uri.startsWith(TaleConst.INSTALL_URI)) {
           response.sendRedirect(request.getContextPath() + TaleConst.INSTALL_URI);
           return false;
        }

        LOGGER.info("UserAgent: {}", request.getHeader(USER_AGENT));
        LOGGER.info("用户访问地址：{}，来路地址：{}", uri, ip);

        if (uri.startsWith(TaleConst.STATIC_URI)) {
            return true;
        }

        //请求拦截处理
        Users user = TaleUtils.getLoginUser(request);
        if (null == user) {
            Integer uid = TaleUtils.getCookieUid(request);
            if (null != uid) {
                //这里还是有安全隐患,cookie是可以伪造的
                user = userService.queryUserById(uid);
                // set session
                request.getSession().setAttribute(TaleConst.LOGIN_SESSION_KEY, user);
            }
        }

        if (uri.startsWith(TaleConst.ADMIN_URI) && !uri.startsWith(TaleConst.LOGIN_URI) && null == user) {
            response.sendRedirect(request.getContextPath() + TaleConst.LOGIN_URI);
            return false;
        }
        //设置get请求的token
        if (request.getMethod().equals("GET")) {
            String csrf_token = UUID.UU64();
            // 默认存储30分钟
            cache.hset(Types.CSRF_TOKEN, csrf_token, uri, 30 * 60);
            request.setAttribute("_csrf_token", csrf_token);
        }

        LOGGER.info("Exit preHandle method");
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
        if (request.getRequestURI().startsWith(TaleConst.STATIC_URI)) {
            return;
        }

        Options ov = optionsService.getOptionByName("site_record");
        request.setAttribute("commons", commons);//一些工具类和公共方法
        request.setAttribute("option", ov);
        request.setAttribute("adminCommons", adminCommons);
        request.setAttribute("version", TaleConst.APP_VERSION);
    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object  o, Exception e) throws Exception {

    }

}
