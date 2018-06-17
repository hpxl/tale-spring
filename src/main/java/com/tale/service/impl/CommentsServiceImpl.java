package com.tale.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tale.constant.TaleConst;
import com.tale.dao.CommentsDao;
import com.tale.model.Bo.CommentsBo;
import com.tale.model.entity.Comments;
import com.tale.model.entity.Contents;
import com.tale.service.CommentsService;
import com.tale.exception.TipException;
import com.tale.service.ContentsService;
import com.tale.utils.DateKit;
import com.tale.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 1/5/18.
 */
@Service("commentsService")
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private CommentsDao commentsDao;

    @Autowired
    private ContentsService contentsService;

    @Override
    public List<Comments> queryList(Map<String, Object> map) {
        return commentsDao.queryList(map);
    }

    @Override
    public Comments getCommentById(Integer coid) {
        if (null != coid) {
            return commentsDao.queryObject(coid);
        }
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer coid, Integer cid) {
        if (null == coid) {
            throw new TipException("主键为空");
        }

        commentsDao.delete(coid);
        Contents contents = contentsService.getContents(cid + "");
        if (null != contents && contents.getCommentsNum() > 0) {
            contents.setCommentsNum(contents.getCommentsNum() - 1);
            contentsService.updateArticle(contents);
        }
    }

    @Override
    @Transactional
    public void update(Comments comments) {
        if (null != comments && null != comments.getCoid()) {
            commentsDao.update(comments);
        }
    }

    @Override
    @Transactional
    public String insertComment(Comments comments) {
        if (null == comments) {
            return "评论对象为空";
        }
        if (StringUtils.isBlank(comments.getAuthor())) {
            comments.setAuthor("热心网友");
        }
        if (StringUtils.isNotBlank(comments.getMail()) && !TaleUtils.isEmail(comments.getMail())) {
            return "请输入正确的邮箱格式";
        }
        if (StringUtils.isBlank(comments.getContent())) {
            return "评论内容不能为空";
        }
        if (comments.getContent().length() < 5 || comments.getContent().length() > 2000) {
            return "评论字数在5-2000个字符";
        }
        if (null == comments.getCid()) {
            return "评论文章不能为空";
        }
        Contents contents = contentsService.getContents(String.valueOf(comments.getCid()));
        if (null == contents) {
            return "不存在的文章";
        }
        comments.setOwnerId(contents.getAuthorId());
        comments.setStatus("not_audit");
        comments.setCreated(DateKit.getCurrentUnixTime());
        commentsDao.save(comments);

        Contents temp = new Contents();
        temp.setCid(contents.getCid());
        temp.setCommentsNum(contents.getCommentsNum() + 1);
        contentsService.updateArticle(temp);

        return TaleConst.SUCCESS_RESULT;
    }

    @Override
    public PageInfo<CommentsBo> getComments(Integer cid, int page, int limit) {
        if (null != cid) {
            PageHelper.startPage(page, limit);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cid", cid);
            map.put("parent", 0);
            map.put("status", "approved");
            List<Comments> parents = commentsDao.queryList(map);
            PageInfo<Comments> commentPaginator = new PageInfo<Comments>(parents);
            PageInfo<CommentsBo> returnBo = copyPageInfo(commentPaginator);
            if (parents.size() != 0) {
                List<CommentsBo> comments = new ArrayList<CommentsBo>(parents.size());
                for (Comments parent : parents) {
                    CommentsBo comment = new CommentsBo(parent);
                    comments.add(comment);
                }
                returnBo.setList(comments);
            }
            return returnBo;
        }
        return null;
    }

    /**
     * copy原有的分页信息，除数据
     *
     * @param ordinal
     * @param <T>
     * @return
     */
    private <T> PageInfo<T> copyPageInfo(PageInfo ordinal) {
        PageInfo<T> returnBo = new PageInfo<T>();
        returnBo.setPageSize(ordinal.getPageSize());
        returnBo.setPageNum(ordinal.getPageNum());
        returnBo.setEndRow(ordinal.getEndRow());
        returnBo.setTotal(ordinal.getTotal());
        returnBo.setHasNextPage(ordinal.isHasNextPage());
        returnBo.setHasPreviousPage(ordinal.isHasPreviousPage());
        returnBo.setIsFirstPage(ordinal.isIsFirstPage());
        returnBo.setIsLastPage(ordinal.isIsLastPage());
        returnBo.setNavigateFirstPage(ordinal.getNavigateFirstPage());
        returnBo.setNavigateLastPage(ordinal.getNavigateLastPage());
        returnBo.setNavigatepageNums(ordinal.getNavigatepageNums());
        returnBo.setSize(ordinal.getSize());
        returnBo.setPrePage(ordinal.getPrePage());
        returnBo.setNextPage(ordinal.getNextPage());
        return returnBo;
    }
}
