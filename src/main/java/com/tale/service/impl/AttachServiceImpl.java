package com.tale.service.impl;

import com.tale.dao.AttachDao;
import com.tale.model.entity.Attach;
import com.tale.service.AttachService;
import com.tale.utils.DateKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 2/4/18.
 */
@Service("attachService")
public class AttachServiceImpl implements AttachService {

    @Autowired
    private AttachDao attachDao;

    /**
     * 保存附件
     * @param fname
     * @param ftype
     * @param fkey
     * @param authorId
     */
    @Override
    @Transactional
    public void save(String fname, String ftype, String fkey, Integer authorId) {
        Attach attach = new Attach();
        attach.setFname(fname);
        attach.setFtype(ftype);
        attach.setFkey(fkey);
        attach.setAuthorId(authorId);
        attach.setCreated(DateKit.getCurrentUnixTime());
        attachDao.save(attach);
    }

    @Override
    public List<Attach> getAttachs(Integer page, Integer limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", page);
        map.put("limit", limit);
        List<Attach> attaches = attachDao.queryList(map);
        return attaches;
    }

    @Override
    public Attach selectById(Integer id) {
        Attach attach = attachDao.queryObject(id);
        return attach;
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (id != null) {
            attachDao.delete(id);
        }
    }
}
