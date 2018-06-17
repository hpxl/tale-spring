package com.tale.service.impl;

import com.tale.constant.TaleConst;
import com.tale.dao.MetasDao;
import com.tale.dto.MetaDto;
import com.tale.exception.TipException;
import com.tale.dto.Types;
import com.tale.model.entity.Contents;
import com.tale.model.entity.Metas;
import com.tale.model.entity.Relationships;
import com.tale.service.ContentsService;
import com.tale.service.MetasService;
import com.tale.service.RelationshipsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 14/3/18.
 */
@Service("metasService")
public class MetasServiceImpl implements MetasService {

    @Autowired
    private MetasDao metasDao;

    @Autowired
    private ContentsService contentsService;

    @Autowired
    private RelationshipsService relationshipsService;

    /**
     * 获取分类列表
     * @param type
     * @return
     */
    public List<Metas> getMetas(String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        return metasDao.queryList(map);
    }

    public MetaDto getMetas(String type, String name) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", type);
            map.put("name", name);
            return metasDao.selectDtoByNameAndType(map);
        }
        return null;
    }

    @Override
    public List<MetaDto> getMetaList(String type, String orderby, int limit) {
        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.isBlank(orderby)) {
                orderby = "count desc, a.mid desc";
            }
            if (limit < 1 || limit > TaleConst.MAX_POSTS) {
                limit = 10;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", type);
            map.put("orderby", orderby);
            map.put("limit", limit);
            return metasDao.selectFromSql(map);
        }
        return null;
    }

    /**
     * 保存多个项目
     * @param cid 文章id
     * @param names 类型名称列表
     * @param type 类型，tag or category
     */
    @Override
    @Transactional
    public void saveMetas(Integer cid, String names, String type) {
        if (null == cid) {
            throw  new TipException("项目关联id不能为空");
        }
        if (StringUtils.isNotBlank(names) && StringUtils.isNotBlank(type)) {
            String[] nameArr = StringUtils.split(names, ",");
            for (String name : nameArr) {
                this.saveOrUpdate(cid, name, type);
            }
        }
    }

    @Override
    @Transactional
    public void saveMeta(String name, String type, Integer mid) {
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("type", type);
            List<Metas> metasList = metasDao.queryList(map);
            if (metasList.size() != 0) {
                throw new TipException("已经存在改项");
            } else {
                Metas metas = new Metas();
                metas.setName(name);
                if (null != mid) {
                    Metas original = metasDao.queryObject(mid);
                    metas.setMid(mid);
                    metasDao.update(metas);
                } else {
                    metas.setType(type);
                    metasDao.save(metas);
                }
            }
        }
    }

    /**
     * save or update
     * @param cid
     * @param name
     * @param type
     */
    private void saveOrUpdate(Integer cid, String name, String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("name", name);
        List<Metas> metasList = metasDao.queryList(map);

        int mid;
        Metas metas;
        if (metasList.size() == 1) {
            metas = metasList.get(0);
            mid = metas.getMid();
        } else if (metasList.size() > 1) {
            throw new TipException("查询到多条数据");
        } else {
            metas = new Metas();
            metas.setSlug(name);
            metas.setName(name);
            metas.setType(type);
            metasDao.save(metas);
            mid = metas.getMid();
        }

        if (mid != 0) {
            int count = relationshipsService.countById(cid, mid);
            if (count == 0) {
                Relationships relationships = new Relationships();
                relationships.setCid(cid);
                relationships.setMid(mid);
                relationshipsService.insert(relationships);
            }
        }
    }

    @Override
    @Transactional
    public void saveMetas(Metas metas) {
        if (null != metas) {
            metasDao.save(metas);
        }
    }

    @Override
    @Transactional
    public void update(Metas metas) {
        if (null != metas && null != metas.getMid()) {
            metasDao.update(metas);
        }
    }

    @Override
    @Transactional
    public void delete(int mid) {
        Metas metas = metasDao.queryObject(mid);
        if (null != metas) {
            metasDao.delete(mid);
            String name = metas.getName();
            String type = metas.getType();

            List<Relationships> rList = relationshipsService.getRelationshipById(null, metas.getMid());
            if (null != rList) {
                for (Relationships r : rList) {
                    Contents contents = contentsService.getContents(String.valueOf(r.getCid()));
                    if (null != contents) {
                        Contents temp = new Contents();
                        temp.setCid(r.getCid());
                        if (type.equals(Types.CATEGORY)) {
                            temp.setCategories(reMeta(name, contents.getCategories()));
                        }
                        if (type.equals(Types.TAG)) {
                            temp.setTags(reMeta(type, contents.getTags()));
                        }
                        contentsService.updateArticle(temp);
                    }
                }
            }
            relationshipsService.deleteById(null, mid);
        }
    }

    private String reMeta(String name, String metas) {
        String[] ms = StringUtils.split(metas, ",");
        StringBuilder sbuf = new StringBuilder();
        for (String m : ms) {
            if (!name.equals(m)) {
                sbuf.append(",").append(m);
            }
        }
        if (sbuf.length() > 0) {
            return sbuf.substring(1);
        }
        return "";
    }
}
