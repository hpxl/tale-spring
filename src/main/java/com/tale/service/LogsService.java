package com.tale.service;

import com.tale.model.entity.Logs;

import java.util.List;

/**
 * Created by hpxl on 22/3/18.
 */
public interface LogsService {
    /**
     * 插入一条日志
     * @param action
     * @param data
     * @param ip
     * @param authorId
     */
    public void insertLog(String action, String data, String ip, Integer authorId);

    /**
     * 获取日志分页
     * @param page 当前页
     * @param limit 每页条数
     * @return 日志
     */
    List<Logs> getLogs(int page, int limit);
}
