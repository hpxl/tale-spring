package com.tale.controller;

import com.tale.model.entity.Users;
import com.tale.utils.TaleUtils;
import com.tale.utils.MapCache;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by hpxl on 4/3/18.
 */
public abstract class BaseController {

    public static String THEME = "themes/default";

    protected MapCache cache = MapCache.single();

    /**
     * 主页的页面主题
     * @param viewName
     * @return
     */
    public String render(String viewName) {
        return THEME + "/" + viewName;
    }

    /**
     * 获取请求绑定的登录对象
     * @param request
     * @return
     */
    public Users user(HttpServletRequest request) {
        return TaleUtils.getLoginUser(request);
    }

    public Integer getUid(HttpServletRequest request){
        return this.user(request).getUid();
    }

    public BaseController title(HttpServletRequest request, String title) {
        request.setAttribute("title", title);
        return this;
    }

    public BaseController keywords(HttpServletRequest request, String keywords) {
        request.setAttribute("keywords", keywords);
        return this;
    }

    public String render_404() {
        return "comm/error_404";
    }
}
