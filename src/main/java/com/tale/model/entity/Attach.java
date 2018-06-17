package com.tale.model.entity;

/**
 * Created by hpxl on 2/4/18.
 */
public class Attach {
    /**
     * 附件ID
     */
    private Integer id;

    /**
     * 附件名称
     */
    private String fname;
    /**
     * 附件类型
     */
    private String ftype;

    private String fkey;

    /**
     * 作者ID
     */
    private Integer authorId;
    /**
     * 创建时间
     */
    private Integer created;

    public Attach() {
        super();
    }

    public Attach(String fname, String ftype, String fkey, Integer authorId, Integer created) {
        this.fname = fname;
        this.ftype = ftype;
        this.fkey = fkey;
        this.authorId = authorId;
        this.created = created;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFtype() {
        return ftype;
    }

    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    public String getFkey() {
        return fkey;
    }

    public void setFkey(String fkey) {
        this.fkey = fkey;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Attach{" +
                "id=" + id +
                ", fname='" + fname + '\'' +
                ", ftype='" + ftype + '\'' +
                ", fkey='" + fkey + '\'' +
                ", authorId=" + authorId +
                ", created=" + created +
                '}';
    }
}
