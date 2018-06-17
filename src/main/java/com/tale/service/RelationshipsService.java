package com.tale.service;

import com.tale.model.entity.Metas;
import com.tale.model.entity.Relationships;

import java.util.List;

/**
 * Created by hpxl on 14/3/18.
 */
public interface RelationshipsService {
    /**
     * 按主键删除
     * @param cid
     * @param mid
     */
    public int deleteById(Integer cid, Integer mid);

    public int countById(Integer cid, Integer mid);

    public void insert(Relationships relationships);

    public List<Relationships> getRelationshipById(Integer cid, Integer mid);
}
