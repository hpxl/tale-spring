package com.tale.interceptor;

import com.tale.utils.Commons;
import com.tale.dto.Types;
import com.tale.constant.TaleConst;
import com.tale.controller.BaseController;
import com.tale.service.OptionsService;
import com.tale.controller.admin.AttachController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationListener;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by hpxl on 16/6/18.
 */
@Component
public class InitConfiguration implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitConfiguration.class);

    @Autowired
    private OptionsService optionsService;

    @Value("${app.version:}")
    String appVersion;

    @Value("${app.enableCdn:}")
    String appEnableCdn;

    @Value("${app.max-file-size:}")
    String appMaxFileSize;

    @Value("${app.salt:}")
    String appSalt;

    public InitConfiguration() {
        super();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            // 获取网站设置
            TaleConst.initConfig = optionsService.getOptions(null);

            if (StringUtils.isNotBlank(appVersion)) {
                TaleConst.APP_VERSION = appVersion;
            }

            if (StringUtils.isNotBlank(appEnableCdn)) {
                TaleConst.ENABLED_CDN = Boolean.valueOf(appEnableCdn);
            }

            if (StringUtils.isNotBlank(appMaxFileSize)) {
                TaleConst.MAX_FILE_SIZE = Integer.valueOf(appMaxFileSize);
            }

            if (StringUtils.isNotBlank(appSalt)) {
                TaleConst.AES_SALT = appSalt;
            }

            String ips = TaleConst.initConfig.get(Types.BLOCK_IPS);
            if (StringUtils.isNotBlank(ips)) {
                TaleConst.BLOCK_IPS.addAll(Arrays.asList(ips.split(",")));
            }
            if (Files.exists(Paths.get(AttachController.CLASSPATH + "install.lock"))) {
                TaleConst.INSTALLED = Boolean.TRUE;
            }

            BaseController.THEME = "themes/" + Commons.site_option("site_theme");
        }
    }
}
