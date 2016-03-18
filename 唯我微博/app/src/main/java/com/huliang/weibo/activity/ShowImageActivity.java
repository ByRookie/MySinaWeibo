package com.huliang.weibo.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.huliang.weibo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;

import cn.kejin.android.views.XImageView;

/**
 * Created by huliang on 16/3/17.
 */
public class ShowImageActivity extends Activity {
    private static final String TAG = "ShowImageActivity";
    private XImageView xImageView;
    private AVLoadingIndicatorView onLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        xImageView = (XImageView) findViewById(R.id.iv_show_image);
        onLoad = (AVLoadingIndicatorView) findViewById(R.id.loading_item);
        onLoad.setVisibility(View.INVISIBLE);
        xImageView.setVisibility(View.VISIBLE);
        String imageUrl = getIntent().getStringExtra("ImageURL");

        File cacheFile = ImageLoader.getInstance().getDiscCache().get(imageUrl);
        if (cacheFile != null && cacheFile.exists()) {
            xImageView.setImage(cacheFile, Bitmap.Config.ARGB_8888);
        } else {
            xImageView.setVisibility(View.INVISIBLE);
            onLoad.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    xImageView.setImage(loadedImage);
                    onLoad.setVisibility(View.INVISIBLE);
                    xImageView.setVisibility(View.VISIBLE);
                }
            });
        }

        xImageView.setActionListener(new XImageView.SimpleActionListener() {
            @Override
            public void onSingleTapped(XImageView view, MotionEvent event, boolean onImage) {
                finish();
            }
        });
    }
}