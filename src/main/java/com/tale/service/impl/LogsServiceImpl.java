package com.tale.service.impl;

import com.tale.dao.LogsDao;
import com.tale.model.entity.Logs;
import com.tale.service.LogsService;
import com.tale.utils.DateKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 22/3/18.
 */
@Service("logsService")
public class LogsServiceImpl implements LogsService {

    @Autowired
    private LogsDao logsDao;

    /**
     * 插入一条日志
     * @param action
     * @param data
     * @param ip
     * @param authorId
     */
    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {
        Logs logs = new Logs();
        logs.setAction(action);
        logs.setData(data);
        logs.setIp(ip);
        logs.setAuthorId(authorId);
        logs.setCreated(DateKit.getCurrentUnixTime());
        logsDao.save(logs);
    }

    @Override
    public List<Logs> getLogs(int page, int limit) {
        int offset = (page > 1) ? (page - 1) * limit : 0;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", offset);
        map.put("limit", limit);
        return logsDao.queryList(map);
    }

}
