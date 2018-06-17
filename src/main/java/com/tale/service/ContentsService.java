package com.tale.service;

import com.github.pagehelper.PageInfo;
import com.tale.model.entity.Contents;

import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 4/3/18.
 */
public interface ContentsService {
    /**
     * 查询文章列表
     */
    List<Contents> queryList(Map<String, Object> map);

    public Contents getContents(String id);

    public PageInfo<Contents> getContents(Integer p, Integer limit);

    public PageInfo<Contents> getArticles(String keyword, Integer page, Integer limit);

    public PageInfo<Contents> getArticles(Integer mid, int page, int limit);

    public String updateArticle(Contents contents);

    public void updateContentByCid(Contents contents);

    public void updateCategories(String original, String newCatefory);

    public String publish(Contents contents);

    public String deleteByCid(Integer cid);
}
