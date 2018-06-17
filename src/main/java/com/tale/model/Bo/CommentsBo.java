package com.tale.model.Bo;

import com.tale.model.entity.Comments;

import java.util.List;

/**
 * Created by hpxl on 17/5/18.
 */
public class CommentsBo extends Comments {
    private int levels;
    private List<Comments> children;

    public CommentsBo(Comments comments) {
        setAuthor(comments.getAuthor());
        setMail(comments.getMail());
        setCoid(comments.getCoid());
        setAuthorId(comments.getAuthorId());
        setUrl(comments.getUrl());
        setCreated(comments.getCreated());
        setAgent(comments.getAgent());
        setIp(comments.getIp());
        setContent(comments.getContent());
        setOwnerId(comments.getOwnerId());
        setCid(comments.getCid());
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public List<Comments> getChildren() {
        return children;
    }

    public void setChildren(List<Comments> children) {
        this.children = children;
    }
}
