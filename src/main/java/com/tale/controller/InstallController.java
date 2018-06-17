package com.tale.controller;

import com.tale.constant.TaleConst;
import com.tale.controller.admin.AttachController;
import com.tale.exception.TipException;
import com.tale.model.Bo.RestResponseBo;
import com.tale.model.entity.Users;
import com.tale.service.OptionsService;
import com.tale.service.SiteService;
import com.tale.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by hpxl on 16/6/18.
 */
@Controller
@RequestMapping("/install")
public class InstallController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallController.class);

    @Autowired
    private SiteService siteService;

    @Autowired
    private OptionsService optionsService;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public String index(HttpServletRequest request) {
        if (optionsService.checkReInstall() == 1) {
            request.setAttribute("is_install", false);
        } else {
            boolean existInstall = Files.exists(Paths.get(AttachController.CLASSPATH + "install.lock"));
            LOGGER.info(AttachController.CLASSPATH + "install.lock");
            request.setAttribute("is_install", existInstall);
        }
        return "install";
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseBody
    public RestResponseBo doInstall(@RequestParam String site_title, @RequestParam String site_url,
                                    @RequestParam String admin_user, @RequestParam String admin_email,
                                    @RequestParam String admin_pwd) {
        if (Files.exists(Paths.get(AttachController.CLASSPATH + "install.lock"))
                && optionsService.checkReInstall() != 1) {
            return RestResponseBo.fail("请勿重新安装");
        }

        try {
            if (StringUtils.isBlank(site_title) ||
                    StringUtils.isBlank(site_url) ||
                    StringUtils.isBlank(admin_user) ||
                    StringUtils.isBlank(admin_pwd)) {
                return RestResponseBo.fail("请确认网站信息输入完整");
            }

            if (admin_pwd.length() < 6 || admin_pwd.length() > 14) {
                return RestResponseBo.fail("请输入6-14位密码");
            }

            if (StringUtils.isNotBlank(admin_email) && !TaleUtils.isEmail(admin_email)) {
                return RestResponseBo.fail("邮箱格式不正确");
            }

            Users temp = new Users();
            temp.setUsername(admin_user);
            temp.setPassword(admin_pwd);
            temp.setEmail(admin_email);
            siteService.initSite(temp);

            if (site_url.endsWith("/")) {
                site_url = site_url.substring(0, site_url.length() - 1);
            }
            if (!site_url.startsWith("http")) {
                site_url = "http://".concat(site_url);
            }
            optionsService.saveOptions("site_title", site_title);
            optionsService.saveOptions("site_url", site_url);
            TaleConst.initConfig = optionsService.getOptions(null);
        } catch (Exception e) {
            String msg = "安装失败";
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
