package com.tale.model.entity;

/**
 * Created by hpxl on 22/3/18.
 */
public class Logs {
    private Integer id;
    private String action;
    private String data;
    private Integer authorId;
    private String ip;
    private Integer created;

    public Logs() {
        super();
    }

    public Logs(String action, String data, Integer authorId, String ip) {
        this.action = action;
        this.data = data;
        this.authorId = authorId;
        this.ip = ip;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Logs{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", data='" + data + '\'' +
                ", authorId=" + authorId +
                ", ip='" + ip + '\'' +
                ", created=" + created +
                '}';
    }
}
