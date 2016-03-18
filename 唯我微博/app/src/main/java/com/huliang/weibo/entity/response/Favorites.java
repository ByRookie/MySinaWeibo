package com.huliang.weibo.entity.response;

import com.huliang.weibo.entity.BaseEntity;
import com.huliang.weibo.entity.Status;

import java.util.List;

/**
 * Created by huliang on 16/3/17.
 */
public class Favorites extends BaseEntity {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private String favorited_time;

    public String getFavorited_time() {
        return favorited_time;
    }

    public void setFavorited_time(String favorited_time) {
        this.favorited_time = favorited_time;
    }

}
