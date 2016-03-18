package com.huliang.weibo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.huliang.weibo.activity.UserInfoActivity;
import com.huliang.weibo.adapter.StatusAdapter;
import com.huliang.weibo.api.BoreWeiboApi;
import com.huliang.weibo.api.SimpleRequestListener;
import com.huliang.weibo.entity.Status;
import com.huliang.weibo.entity.response.StatusTimeLineResponse;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wang.avi.AVLoadingIndicatorView;

public class WeiboFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private View view;
    private PullToRefreshListView plv_home;
    private View footView;
    private StatusAdapter adapter;
    private List<Status> statuses = new ArrayList<Status>();
    private int curPage = 1;
    private AVLoadingIndicatorView loading_item;
    private UserInfoActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (UserInfoActivity) getActivity();
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
    }

    public void loadData(final int page) {
        BoreWeiboApi api = new BoreWeiboApi(activity);
        api.statusesUser_timeline(UserInfoActivity.uid, UserInfoActivity.name, page,
                new SimpleRequestListener(activity, null) {

                    @Override
                    public void onComplete(String response) {
                        super.onComplete(response);
                        if (page == 1) {
                            statuses.clear();
                        }
                        curPage = page;
                        addData(new Gson().fromJson(response, StatusTimeLineResponse.class));
                    }

                    @Override
                    public void onAllDone() {
                        super.onAllDone();
                        plv_home.onRefreshComplete();
                    }
                });
    }

    private void addData(StatusTimeLineResponse resBean) {
        try {
            int statusCount = statuses.size();
            for (Status status : resBean.getStatuses()) {
                if (!statuses.contains(status)) {
                    statuses.add(status);
                }
            }
            Log.e(TAG, statuses.size() + "  " + curPage + "   " + resBean.getTotal_number());
            if (statusCount == statuses.size()) {
                removeFootView(plv_home, footView);
                addLastView(plv_home);
            }
            adapter.notifyDataSetChanged();
            if (curPage < resBean.getTotal_number()) {
                addFootView(plv_home, footView);
            } else {
                removeFootView(plv_home, footView);
            }
        } catch (Exception e) {
            ListView lv = plv_home.getRefreshableView();
            if (lv.getFooterViewsCount() == 1) {
                TextView textView = new TextView(activity);
                textView.setText("由于系统API限制，只能获取当前授权用户的五条微博以及关注、粉丝信息");
                textView.setTextSize(16);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setGravity(Gravity.CENTER);
                lv.addFooterView(textView);
            }
            return;
        }finally {
            loading_item.setVisibility(View.INVISIBLE);
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
            textView.setText("由于系统API限制，只能获取最新的五条微博");
            textView.setTextSize(16);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            lv.addFooterView(textView);
        }
    }
}