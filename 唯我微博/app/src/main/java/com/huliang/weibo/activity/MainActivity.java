package com.huliang.weibo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huliang.weibo.R;
import com.huliang.weibo.entity.User;
import com.huliang.weibo.fragment.HomeFragment;
import com.huliang.weibo.fragment.HotWeiboFragment;
import com.huliang.weibo.fragment.MyLoveFragment;
import com.huliang.weibo.fragment.SettingFragment;
import com.huliang.weibo.utils.ImageOptHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String[] mPlanetTitles;
    private int[] images = new int[]{R.drawable.ic_view_day_grey600_24dp, R.drawable.hot_weibo_gray,
            R.drawable.ic_book_grey600_24dp, R.drawable.application_setting};
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Toolbar toolbar;
    private View drawer_layout_slider;
    private long firstTime;
    private FrameLayout fl_content;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static MenuItem sMenuItem;
    private Fragment fragment = null;
    private SharedPreferences sharedPreferences;
    private String response;
    private User user;
    private ImageView iv_icon;
    private TextView tv_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader(this);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        response = sharedPreferences.getString("user", null);

        //侧滑整体
        drawer_layout_slider = findViewById(R.id.drawer_layout_slider);
        //设置顶部的ToolBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("微博首页");
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.light_toolbar));
        showMenuOnToolBar(R.menu.main_time_line_menu);
        mPlanetTitles = getResources().getStringArray(R.array.sliding_menu_item);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_name = (TextView) findViewById(R.id.tv_name);
        //FrameLayout 主体内容
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).replace(R.id.fl_content, new HomeFragment()).commit();
        mDrawerList.setAdapter(new MyAdapter());
        //设置监听事件，打开对应的Fragment
        mDrawerList.setOnItemClickListener(new MyListener());
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (response != null) {
            user = new Gson().fromJson(response, User.class);
            setUserInfo();
        }
    }

    private void setUserInfo() {
        ImageLoader.getInstance().displayImage(user.getAvatar_hd(), iv_icon);
        iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(drawer_layout_slider)) {
                    //关闭抽屉
                    mDrawerLayout.closeDrawer(drawer_layout_slider);
                }
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                intent.putExtra("uid", user.getId());
                Log.i(TAG, "userID:" + user.getId());
                startActivity(intent);
            }
        });
        tv_name.setText(user.getScreen_name());
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(drawer_layout_slider)) {
                    //关闭抽屉
                    mDrawerLayout.closeDrawer(drawer_layout_slider);
                }
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                intent.putExtra("uid", user.getId());
                startActivity(intent);
            }
        });
        Log.i(TAG, "昵称：" + user.getScreen_name());
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mPlanetTitles.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(MainActivity.this, R.layout.sliding_menu_item, null);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.sliding_menu_item_iv);
            TextView textView = (TextView) view.findViewById(R.id.sliding_menu_item_tv);
            textView.setText(mPlanetTitles[position]);
            imageView.setImageResource(images[position]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private class MyListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            toolbar.setTitle(mPlanetTitles[position]);
            //关闭抽屉
            mDrawerLayout.closeDrawer(drawer_layout_slider);
            //根据不同内容打开不同的Fragment
            selectItem(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(drawer_layout_slider)) {
            //关闭抽屉
            mDrawerLayout.closeDrawer(drawer_layout_slider);
        } else {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Snackbar sb = Snackbar.make(fl_content, "再按一次退出", Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(getResources().getColor(R.color.light_toolbar));
                sb.show();
                firstTime = secondTime;
            } else {
                finish();
            }
        }
    }

    public void showMenuOnToolBar(final Toolbar toolbar, final int menuRes) {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(menuRes);
                sMenuItem = toolbar.getMenu().findItem(R.id.edit_query);
            }
        }, 200);
    }

    public void showMenuOnToolBar(int menu) {
        showMenuOnToolBar(toolbar, menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                int id = arg0.getItemId();
                switch (id) {
                    case R.id.search_menu: {
                        //点击搜索按钮
                        Intent intent = new Intent(MainActivity.this, SeachActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.edit_query: {
                        //点击通知按钮
                        Intent intent = new Intent(MainActivity.this, WriteStatusActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default:
                        break;
                }
                return false;
            }
        });
    }

    // 初始化图片处理
    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(ImageOptHelper.getImgOptions())
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new HotWeiboFragment();
                break;
            case 2:
                fragment = new MyLoveFragment();
                break;
            case 3:
                fragment = new SettingFragment();
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).replace(R.id.fl_content, fragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        boolean sendWeiboSuccess = data.getBooleanExtra("sendWeiboSuccess", false);
//        if (sendWeiboSuccess) {
//            //重新载入数据
//            HomeFragment homeFragment = (HomeFragment) fragment;
//            homeFragment.loadData(1);
//        }
    }
}