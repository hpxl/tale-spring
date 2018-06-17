package com.tale.service.impl;

import com.tale.dao.RelationshipsDao;
import com.tale.model.entity.Relationships;
import com.tale.service.RelationshipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by hpxl on 20/3/18.
 */
@Service("relationshipsService")
public class RelationshipsServiceImpl implements RelationshipsService {
    @Autowired
    private RelationshipsDao relationshipsDao;

    public int deleteById(Integer cid, Integer mid) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (cid != null) map.put("cid", cid);
        if (mid != null) map.put("mid", mid);
        return relationshipsDao.delete(map);
    }

    public int countById(Integer cid, Integer mid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cid", cid);
        map.put("mid", mid);
        return relationshipsDao.queryTotal(map);
    }

    public void insert(Relationships relationships) {
        relationshipsDao.save(relationships);
    }

    public List<Relationships> getRelationshipById(Integer cid, Integer mid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cid", cid);
        map.put("mid", mid);
        return relationshipsDao.queryList(map);
    }
}
