package com.tale.service.impl;

import com.tale.dao.OptionsDao;
import com.tale.model.entity.Options;
import com.tale.service.OptionsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 1/5/18.
 */
@Service("optionsService")
public class OptionsServiceImpl implements OptionsService {

    @Autowired
    private OptionsDao optionsDao;

    @Override
    public List<Options> getOptions() {
        Map<String, Object> map = new HashMap<String, Object>();
        return optionsDao.queryList(map);
    }

    /**
     * 根据key前缀查找配置项
     *
     * @param key 配置key
     */
    @Override
    public Map<String, String> getOptions(String key) {
        Map<String, String> options = new HashMap<String, String>();

        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(key)) {
            map.put("keyword", key + "%");
        }
        List<Options> optionsList = optionsDao.queryList(map);
        if (null != optionsList) {
            for (Options option : optionsList) {
                options.put(option.getName(), option.getValue());
            }
        }
        return options;
    }

    @Override
    public void saveOptions(Map<String, String> options) {
        if (null != options && !options.isEmpty()) {
            for (Map.Entry<String, String> entry : options.entrySet()) {
                insertOption(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void saveOptions(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            Options options;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", key);
            long count = optionsDao.queryTotal(map);
            if (count == 0) {
                options = new Options();
                options.setName(key);
                options.setValue(value);
                optionsDao.save(options);
            } else {
                options = new Options();
                options.setValue(value);
                optionsDao.update(options);
            }
        }
    }

    @Transactional
    public void insertOption(String name, String value) {
        Options options = new Options();
        options.setName(name);
        options.setValue(value);
        if (optionsDao.queryObject(name) == null) {
            optionsDao.save(options);
        } else {
            optionsDao.update(options);
        }
    }

    @Override
    public Options getOptionByName(String name) {
        return optionsDao.queryObject(name);
    }

    @Override
    public int checkReInstall() {
        int allow_reinstall = 0;
        Options ov = this.getOptionByName("allow_install");
        if (null != ov && ov.getValue().equals("1")) {
            allow_reinstall = 1;
        }
        return allow_reinstall;
    }
}
