package com.tale.model.Bo;

import com.tale.model.entity.Contents;

import java.util.List;
import java.io.Serializable;

/**
 * Created by hpxl on 27/5/18.
 */
public class ArchiveBo implements Serializable {

    private String date;
    private String count;
    private List<Contents> articles;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<Contents> getArticles() {
        return articles;
    }

    public void setArticles(List<Contents> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "ArchiveBo{" +
                "date='" + date + '\'' +
                ", count='" + count + '\'' +
                ", articles=" + articles +
                '}';
    }
}
