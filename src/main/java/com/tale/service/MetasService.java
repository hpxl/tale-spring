package com.tale.service;

import com.tale.dto.MetaDto;
import com.tale.model.entity.Metas;

import java.util.List;

/**
 * Created by hpxl on 14/3/18.
 */
public interface MetasService {
    /**
     * 获取分类列表
     * @param type
     * @return
     */
    public List<Metas> getMetas(String type);

    public MetaDto getMetas(String type, String keyword);

    /**
     * 根据类型查询项目列表，带项目下面的文章数
     * @return
     */
    List<MetaDto> getMetaList(String type, String orderby, int limit);

    /**
     * 保存多个项目
     * @param cid
     * @param names
     * @param type
     */
    public void saveMetas(Integer cid, String names, String type);

    public void saveMeta(String name, String type, Integer mid);

    public void saveMetas(Metas metas);

    public void update(Metas metas);

    public void delete(int mid);
}
