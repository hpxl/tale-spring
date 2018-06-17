package com.tale.controller.admin;

import com.tale.constant.TaleConst;
import com.tale.controller.BaseController;
import com.tale.dto.LogActions;
import com.tale.exception.TipException;
import com.tale.model.Bo.BackResponseBo;
import com.tale.model.Bo.RestResponseBo;
import com.tale.model.entity.Options;
import com.tale.service.LogsService;
import com.tale.service.OptionsService;
import com.tale.service.SiteService;
import com.tale.utils.GsonUtils;
import org.apache.commons.lang3.StringUtils;
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
 * Created by hpxl on 1/5/18.
 */
@Controller
@RequestMapping("admin/setting")
public class SettingController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingController.class);

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private LogsService logsService;

    @Autowired
    private SiteService siteService;

    @RequestMapping(value="")
    public String index(HttpServletRequest request) {
        List<Options> optionsList = optionsService.getOptions();
        Map<String, String> options = new HashMap<String, String>();
        for (Options o : optionsList) {
            options.put(o.getName(), o.getValue());
        }
        if (options.get("site_record") == null) {
            options.put("site_record", "");
        }
        request.setAttribute("options", options);
        return "admin/setting";
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public RestResponseBo saveSetting(@RequestParam(required = false) String site_theme, HttpServletRequest request) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String, String> querys = new HashMap<String, String>();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                querys.put(entry.getKey(), join(entry.getValue()));
            }
            optionsService.saveOptions(querys);
            TaleConst.initConfig = querys;
            if (StringUtils.isNotBlank(site_theme)) {
                BaseController.THEME = "themes/" + site_theme;
            }
            logsService.insertLog(LogActions.SYS_SETTING.getAction(), GsonUtils.toJsonString(querys),
                    request.getRemoteAddr(), this.getUid(request));
            return RestResponseBo.ok();
        } catch (Exception e) {
            String msg = "保存设置失败";
            LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
    }

    /**
     * 系统备份
     * @param bk_type
     * @param bk_path
     * @param request
     * @return
     */
    @PostMapping(value = "backup")
    @ResponseBody
    public RestResponseBo backup(@RequestParam String bk_type, @RequestParam String bk_path,
                                 HttpServletRequest request) {
        if (StringUtils.isBlank(bk_type)) {
            return RestResponseBo.fail("请确认信息输入完整");
        }
        try {
            BackResponseBo backResponseBo = siteService.backup(bk_type, bk_path, "yyyyMMddHHmm");
            logsService.insertLog(LogActions.SYS_BACKUP.getAction(), null, request.getRemoteAddr(), this.getUid(request));
            return RestResponseBo.ok(backResponseBo);
        } catch (Exception e) {
            String msg = "备份失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
    }

    private String join(String[] arr) {
        StringBuilder ret = new StringBuilder();
        String[] var3 = arr;
        int var4 = arr.length;
        for (int var5 = 0; var5 < var4; ++var5) {
            ret.append(",").append(var3[var5]);
        }
        return ret.length() > 0 ? ret.substring(1) : ret.toString();
    }
}
