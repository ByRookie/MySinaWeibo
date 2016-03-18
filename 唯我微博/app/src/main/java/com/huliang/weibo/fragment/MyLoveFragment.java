package com.huliang.weibo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.huliang.weibo.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.huliang.weibo.adapter.StatusAdapter;
import com.huliang.weibo.api.BoreWeiboApi;
import com.huliang.weibo.api.SimpleRequestListener;
import com.huliang.weibo.entity.Status;
import com.huliang.weibo.entity.response.Favorites;
import com.huliang.weibo.entity.response.FavoritesResponse;
import com.huliang.weibo.entity.response.StatusTimeLineResponse;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wang.avi.AVLoadingIndicatorView;

public class MyLoveFragment extends BaseFragment {

    private static final String TAG = "MyLoveFragment";
    private View view;

    private PullToRefreshListView plv_home;
    private View footView;
    private StatusAdapter adapter;
    private List<Status> statuses = new ArrayList<Status>();
    private int curPage = 1;
    private AVLoadingIndicatorView loading_item;
    private BoreWeiboApi api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        loadData(1);
        return view;
    }

    private void initView() {
        view = View.inflate(activity, R.layout.frag_home, null);
        plv_home = (PullToRefreshListView) view.findViewById(R.id.lv_home);
        loading_item = (AVLoadingIndicatorView) view.findViewById(R.id.loading_item);
        // 为View设置滑动监听事件
        adapter = new StatusAdapter(activity, statuses);
        plv_home.setAdapter(adapter);
        plv_home.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
            }
        });
        plv_home.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                loadData(curPage + 1);
            }
        });
        footView = View.inflate(activity, R.layout.footview_loading, null);

        api = new BoreWeiboApi(activity);
    }

    public void loadData(final int page) {
        api.favroits(new SimpleRequestListener(activity, null) {

            @Override
            public void onComplete(String response) {
                super.onComplete(response);
                if (page == 1) {
                    statuses.clear();
                }
                curPage = page;
                addData(new Gson().fromJson(response, FavoritesResponse.class));
            }

            @Override
            public void onAllDone() {
                super.onAllDone();
                plv_home.onRefreshComplete();
            }
        });
    }

    private void addData(FavoritesResponse resBean) {
        int statusCount = statuses.size();
        Status status;
        for (Favorites favorites : resBean.getFavorites()) {
            status = favorites.getStatus();
            Log.e(TAG, favorites.getFavorited_time());
            if (!statuses.contains(status) && status != null) {
                statuses.add(status);
                Log.e(TAG, status.toString());
            }
        }
        if (statusCount == statuses.size()) {
            removeFootView(plv_home, footView);
            addLastView(plv_home);
        }
        Log.e(TAG, statuses.size() + "  " + curPage + "   " + resBean.getTotal_number());
        loading_item.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
        if (curPage < resBean.getTotal_number()) {
            addFootView(plv_home, footView);
        } else {
            removeFootView(plv_home, footView);
        }
    }

    private void addFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    private void removeFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }

    private void addLastView(PullToRefreshListView plv) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() == 1) {
            TextView textView = new TextView(activity);
            textView.setText("没有更多收藏了");
            textView.setTextSize(16);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            lv.addFooterView(textView);
        }
    }
}