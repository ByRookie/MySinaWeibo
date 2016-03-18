package com.huliang.weibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huliang.weibo.R;

public class SettingFragment extends BaseFragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        return view;
    }

    private void initView() {
        view = View.inflate(activity, R.layout.frag_setting, null);
    }
}
