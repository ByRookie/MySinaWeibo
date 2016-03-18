package com.huliang.weibo.entity.response;

import java.util.List;

/**
 * Created by huliang on 16/3/17.
 */
public class FavoritesResponse {
    private List<Favorites> favorites;
    private int total_number;

    public List<Favorites> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorites> favorites) {
        this.favorites = favorites;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }
}