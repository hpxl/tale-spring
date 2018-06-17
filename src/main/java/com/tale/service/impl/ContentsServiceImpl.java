package com.tale.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tale.constant.TaleConst;
import com.tale.dao.ContentsDao;
import com.tale.dao.MetasDao;
import com.tale.model.entity.Contents;
import com.tale.dto.Types;
import com.tale.service.ContentsService;
import com.tale.service.MetasService;
import com.tale.service.RelationshipsService;
import com.tale.utils.DateKit;
import com.tale.utils.TaleUtils;
import com.tale.utils.Tools;
import org.apache.commons.lang3.StringUtils;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 4/3/18.
 */
@Service("contentsService")
public class ContentsServiceImpl implements ContentsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentsServiceImpl.class);

    @Autowired
    private ContentsDao contentsDao;

    @Autowired
    private MetasDao metasDao;

    @Autowired
    private MetasService metasService;

    @Autowired
    private RelationshipsService relationshipsService;

    @Override
    public List<Contents> queryList(Map<String, Object> map){
        return contentsDao.queryList(map);
    }

    /**
     * 获取文章ID
     */
    public Contents getContents(String id) {
        if (StringUtils.isNotBlank(id)) {
            if (Tools.isNumber(id)) {
                return contentsDao.queryObject(id);
            } else {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("slug", id);
                return contentsDao.queryByWhere(map);
            }
        }
        return null;
    }

    @Override
    public PageInfo<Contents> getContents(Integer p, Integer limit) {
        LOGGER.debug("Enter getContents method");
        PageHelper.startPage(p, limit);
        Map<String, Object> map = new HashMap<String, Object>();
        int offset = (p > 1) ? (p - 1) * limit : 0;
        map.put("offset", offset);
        map.put("limit", limit);
        map.put("order", "created desc");
        map.put("type", Types.ARTICLE);
        map.put("status", Types.PUBLISH);
        List<Contents> data = contentsDao.queryList(map);
        PageInfo<Contents> pageInfo = new PageInfo<Contents>(data);
        LOGGER.debug("Exit getContents method");
        return pageInfo;
    }

    @Override
    public PageInfo<Contents> getArticles(String keyword, Integer page, Integer limit) {
        LOGGER.debug("Enter getArticles method");
        PageHelper.startPage(page, limit);
        Map<String, Object> map = new HashMap<String, Object>();
        int offset = (page > 1) ? (page - 1) * limit : 0;
        map.put("offset", offset);
        map.put("limit", limit);
        map.put("order", "created desc");
        map.put("type", Types.ARTICLE);
        map.put("status", Types.PUBLISH);
        map.put("keyword", "%" + keyword + "%");
        List<Contents> data = contentsDao.queryList(map);
        PageInfo<Contents> pageInfo = new PageInfo<Contents>(data);
        LOGGER.debug("Exit getArticles method");
        return pageInfo;
    }

    @Override
    public PageInfo<Contents> getArticles(Integer mid, int page, int limit) {
        int total = metasDao.countWithSql(mid);
        PageHelper.startPage(page, limit);
        List<Contents> list = contentsDao.findByCatalog(mid);
        PageInfo<Contents> paginator = new PageInfo<Contents>(list);
        paginator.setTotal(total);
        return paginator;
    }

    /**
     * 删除文章
     * @param cid
     * @return
     */
    public String deleteByCid(Integer cid) {
        Contents contents = contentsDao.queryObject(cid);
        if (contents != null) {
            contentsDao.delete(cid);
            relationshipsService.deleteById(cid, null);
            return TaleConst.SUCCESS_RESULT;
        }
        return "数据为空";
    }

    @Transactional
    public String publish(Contents contents) {
        if (null == contents) {
            return "文章对象为空";
        }
        if (StringUtils.isBlank(contents.getTitle())) {
            return "文章标题不能为空";
        }
        if (StringUtils.isBlank(contents.getContent())) {
            return "文章内容不能为空";
        }
        int titleLength = contents.getTitle().length();
        if (titleLength > TaleConst.MAX_TITLE_COUNT) {
            return "文章标题过长";
        }
        int contentLength = contents.getContent().length();
        if (contentLength > TaleConst.MAX_TEXT_COUNT) {
            return "文章内容过长";
        }
        if (null == contents.getAuthorId()) {
            return "请登录后发布文章";
        }
        if (StringUtils.isNotBlank(contents.getSlug())) {
            if (contents.getSlug().length() < 5) {
                return "路径太短了";
            }
            if (!TaleUtils.isPath(contents.getSlug())) return "您输入的路径不合法";
            // 判断路径是否已存在
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", Types.ARTICLE);
            map.put("slug", contents.getSlug());
            int count = contentsDao.queryTotal(map);
            if (count > 0) return "该路径已经存在，请重新输入";
        } else {
            contents.setSlug(null);
        }

        int time = DateKit.getCurrentUnixTime();
        contents.setCreated(time);
        contents.setModified(time);
        contents.setHits(0);
        contents.setCommentsNum(0);
        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));
        contentsDao.save(contents);

        Integer cid = contents.getCid();
        metasService.saveMetas(cid, contents.getTags(), Types.TAG);
        metasService.saveMetas(cid, contents.getCategories(), Types.CATEGORY);

        return TaleConst.SUCCESS_RESULT;
    }

    @Override
    @Transactional
    public void updateCategories(String original, String newCatefory) {
        Contents contents = new Contents();
        contents.setCategories(newCatefory);
        contentsDao.updateByCondition(contents);
    }

    /**
     * 更新文章信息
     * @param contents
     */
    @Transactional
    public String updateArticle(Contents contents) {
        if (null == contents) {
            return "文章对象为空";
        }
        if (StringUtils.isBlank(contents.getTitle())) {
            return "文章标题不能为空";
        }
        if (StringUtils.isBlank(contents.getContent())) {
            return "文章内容不能为空";
        }
        int titleLength = contents.getTitle().length();
        if (titleLength > TaleConst.MAX_TITLE_COUNT) {
            return "文章标题过长";
        }
        int contentLength = contents.getContent().length();
        if (contentLength > TaleConst.MAX_TEXT_COUNT) {
            return "文章内容过长";
        }
        if (null == contents.getAuthorId()) {
            return "请登录后发布文章";
        }
        if (StringUtils.isBlank(contents.getSlug())) {
            contents.setSlug(null);
        }

        int time = DateKit.getCurrentUnixTime();
        contents.setModified(time);
        Integer cid = contents.getCid();
        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));
        contentsDao.update(contents);
        relationshipsService.deleteById(cid, null);
        metasService.saveMetas(cid, contents.getTags(), Types.TAG);
        metasService.saveMetas(cid, contents.getCategories(), Types.CATEGORY);

        return TaleConst.SUCCESS_RESULT;
    }

    @Override
    public void updateContentByCid(Contents contents) {
        if (null != contents && null != contents.getCid()) {
            contentsDao.update(contents);
        }
    }
}
