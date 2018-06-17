package com.tale.service;

import com.tale.model.entity.Options;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 1/5/18.
 */
public interface OptionsService {

    public List<Options> getOptions();
    public Map<String, String> getOptions(String key);
    public void saveOptions(Map<String, String> options);
    public void saveOptions(String key, String value);

    public Options getOptionByName(String name);

    public int checkReInstall();
}
