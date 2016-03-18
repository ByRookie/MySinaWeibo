package com.huliang.weibo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huliang.weibo.R;
import com.huliang.weibo.api.BoreWeiboApi;
import com.huliang.weibo.api.SimpleRequestListener;
import com.huliang.weibo.entity.User;
import com.huliang.weibo.fragment.WeiboFragment;
import com.huliang.weibo.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by huliang on 16/3/18.
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView circleImageView;
    private TextView tv_name;
    private TextView tv_position;
    private TextView tv_voice;
    private RadioButton rb_weibo;
    private RadioButton rb_guanzhu;
    private RadioButton rb_fensi;
    private FrameLayout frameLayout;
    private User user;
    private TextView tv_title;
    private ImageView iv_back;
    private AVLoadingIndicatorView loading;
    private LinearLayout loading_content;
    public static long uid;
    private Fragment fragment;
    public static String name;
    private String uidString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();

        uid = getIntent().getLongExtra("uid", 0);
        if (uid == 0) {
            uidString = null;
        } else {
            uidString = Long.toString(uid);
        }
        name = getIntent().getStringExtra("name");
        BoreWeiboApi api = new BoreWeiboApi(UserInfoActivity.this);
        api.usersShow(uidString, name, new SimpleRequestListener(UserInfoActivity.this, null) {
            @Override
            public void onComplete(String response) {
                user = new Gson().fromJson(response, User.class);
                setUserInfo();
            }
        });

    }

    private void setUserInfo() {
        ImageLoader.getInstance().displayImage(user.getAvatar_hd(), circleImageView);
        tv_name.setText(user.getScreen_name());
        tv_title.setText(user.getScreen_name());
        tv_position.setText(user.getLocation());
        if (user.getDescription().length() == 0) {
            tv_voice.setText("用户暂无说明");
        } else {
            tv_voice.setText(user.getDescription());
        }
        rb_weibo.setText(user.getStatuses_count() + "\n微博数");
        rb_guanzhu.setText(user.getFriends_count() + "\n关注数");
        rb_fensi.setText(user.getFollowers_count() + "\n粉丝数");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new WeiboFragment()).commit();
        loading.setVisibility(View.GONE);
        loading_content.setVisibility(View.VISIBLE);
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        circleImageView = (CircleImageView) findViewById(R.id.civ_icon);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_position = (TextView) findViewById(R.id.tv_position);
        tv_voice = (TextView) findViewById(R.id.tv_voice);
        rb_weibo = (RadioButton) findViewById(R.id.rb_weibo);
        rb_guanzhu = (RadioButton) findViewById(R.id.rb_guanzhu);
        rb_fensi = (RadioButton) findViewById(R.id.rb_fensi);
        frameLayout = (FrameLayout) findViewById(R.id.fragment);
        loading = (AVLoadingIndicatorView) findViewById(R.id.loading_item);
        loading_content = (LinearLayout) findViewById(R.id.loading_content);
        loading.setVisibility(View.VISIBLE);
        loading_content.setVisibility(View.GONE);
        iv_back.setOnClickListener(this);
        rb_weibo.setOnClickListener(this);
        rb_guanzhu.setOnClickListener(this);
        rb_fensi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                //点击上方返回键
                finish();
                break;
            case R.id.rb_weibo:
                //点击微博数键，切换Fragment
                fragment = new WeiboFragment();
                break;
            case R.id.rb_guanzhu:
                //点击关注数键，切换Fragment
                break;
            case R.id.rb_fensi:
                //点击粉丝数键，切换Fragment
                break;
            default:
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).replace(R.id.fragment, fragment).commit();
        }
    }
}
