package com.huliang.weibo.utils;

import android.graphics.Bitmap;

import com.huliang.weibo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageOptHelper {

    public static DisplayImageOptions getImgOptions() {
        DisplayImageOptions imgOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc()
                .cacheInMemory()
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .showStubImage(R.drawable.timeline_image_loading)
                .showImageForEmptyUri(R.drawable.timeline_image_loading)
                .showImageOnFail(R.drawable.timeline_image_failure)
                .build();
        return imgOptions;
    }

    public static DisplayImageOptions getAvatarOptions() {
        DisplayImageOptions avatarOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc()
                .cacheInMemory()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.drawable.avatar_default)
                .showImageForEmptyUri(R.drawable.avatar_default)
                .showImageOnFail(R.drawable.avatar_default)
                .displayer(new RoundedBitmapDisplayer(999))
                .build();
        return avatarOptions;
    }

    public static DisplayImageOptions getCornerOptions(int cornerRadiusPixels) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisc()
                .cacheInMemory()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.drawable.timeline_image_loading)
                .showImageForEmptyUri(R.drawable.timeline_image_loading)
                .showImageOnFail(R.drawable.timeline_image_loading)
                .displayer(new RoundedBitmapDisplayer(cornerRadiusPixels)).build();
        return options;
    }
}
