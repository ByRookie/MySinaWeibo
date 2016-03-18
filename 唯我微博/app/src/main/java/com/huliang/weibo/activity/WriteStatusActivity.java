package com.huliang.weibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huliang.weibo.R;
import com.huliang.weibo.adapter.EmotionGvAdapter;
import com.huliang.weibo.adapter.EmotionPagerAdapter;
import com.huliang.weibo.adapter.WriteStatusGridImgsAdapter;
import com.huliang.weibo.api.BoreWeiboApi;
import com.huliang.weibo.api.SimpleRequestListener;
import com.huliang.weibo.entity.Emotion;
import com.huliang.weibo.entity.Status;
import com.huliang.weibo.utils.DisplayUtils;
import com.huliang.weibo.utils.ImageUtils;
import com.huliang.weibo.utils.StringUtils;
import com.huliang.weibo.utils.TitleBuilder;
import com.huliang.weibo.utils.ToastUtils;
import com.huliang.weibo.widget.WrapHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class WriteStatusActivity extends Activity implements OnClickListener, OnItemClickListener {

    // 输入框
    private EditText et_write_status;
    // 添加的九宫格图片
    private WrapHeightGridView gv_write_status;
    // 转发微博内容
    private View include_retweeted_status_card;
    private ImageView iv_rstatus_img;
    private TextView tv_rstatus_username;
    private TextView tv_rstatus_content;
    // 底部添加栏
    private ImageView iv_image;
    private ImageView iv_at;
    private ImageView iv_topic;
    private ImageView iv_emoji;
    // 表情选择面板
    private LinearLayout ll_emotion_dashboard;
    private ViewPager vp_emotion_dashboard;
    // 进度框
    private ProgressDialog progressDialog;

    private WriteStatusGridImgsAdapter statusImgsAdapter;
    private ArrayList<Uri> imgUris = new ArrayList<Uri>();
    private EmotionPagerAdapter emotionPagerGvAdapter;

    private Status retweeted_status;
    private Status cardStatus;
    private BoreWeiboApi weiboApi;
    private ImageLoader imageLoader;
    private int topicCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_write_status);
        weiboApi = new BoreWeiboApi(this);
        retweeted_status = (Status) getIntent().getSerializableExtra("status");
        imageLoader = ImageLoader.getInstance();
        initView();
    }

    private void initView() {
        // 标题栏
        new TitleBuilder(this).setTitleText("发微博").setLeftImage(R.drawable.navigationbar_back_sel).setLeftOnClickListener(this).setRightText("发送")
                .setRightOnClickListener(this).build();
        // 输入框
        et_write_status = (EditText) findViewById(R.id.et_write_status);
        et_write_status.setHint("微博内容...");
        // 添加的九宫格图片
        gv_write_status = (WrapHeightGridView) findViewById(R.id.gv_write_status);
        // 转发微博内容
        include_retweeted_status_card = findViewById(R.id.include_retweeted_status_card);
        iv_rstatus_img = (ImageView) findViewById(R.id.iv_rstatus_img);
        tv_rstatus_username = (TextView) findViewById(R.id.tv_rstatus_username);
        tv_rstatus_content = (TextView) findViewById(R.id.tv_rstatus_content);
        // 底部添加栏
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_at = (ImageView) findViewById(R.id.iv_at);
        iv_topic = (ImageView) findViewById(R.id.iv_topic);
        iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
        // 表情选择面板
        ll_emotion_dashboard = (LinearLayout) findViewById(R.id.ll_emotion_dashboard);
        vp_emotion_dashboard = (ViewPager) findViewById(R.id.vp_emotion_dashboard);

        statusImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUris, gv_write_status);
        gv_write_status.setAdapter(statusImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        iv_image.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        iv_topic.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);

        initRetweetedStatus();
        initEmotion();
    }

    /**
     * 发送微博
     */
    private void sendStatus() {
        String statusContent = et_write_status.getText().toString();
        if (TextUtils.isEmpty(statusContent)) {
            ToastUtils.showToast(WriteStatusActivity.this, "微博内容不能为空", Toast.LENGTH_SHORT);
            return;
        }

        String imgFilePath = null;
        if (imgUris.size() > 0) {
            Uri uri = imgUris.get(0);
            imgFilePath = ImageUtils.getImageAbsolutePath19(this, uri);
        }

        // 开启上传对话框
        final Dialog dialog = new Dialog(WriteStatusActivity.this, R.style.DialogCommon);
        View contentView = View.inflate(WriteStatusActivity.this, R.layout.dialog_loading, null);
        dialog.setContentView(contentView);
        dialog.show();
        dialog.setCancelable(false);


        long retweetedStatusId = cardStatus == null ? -1 : cardStatus.getId();
        weiboApi.statusesSend(statusContent, imgFilePath, retweetedStatusId, new SimpleRequestListener(this, null) {

            @Override
            public void onComplete(String response) {
                super.onComplete(response);
                ToastUtils.showToast(WriteStatusActivity.this, "微博发送成功", Toast.LENGTH_SHORT);
                dialog.dismiss();
                WriteStatusActivity.this.finish();
            }

        });

    }

    /**
     * 初始化引用微博内容
     */
    private void initRetweetedStatus() {
        if (retweeted_status != null) {
            Status rrStatus = retweeted_status.getRetweeted_status();
            if (rrStatus != null) {
                String content = "//@" + retweeted_status.getUser().getName() + ":" + retweeted_status.getText();
                et_write_status.setText(StringUtils.getWeiboContent(this, et_write_status, content));
                cardStatus = rrStatus;
            } else {
                et_write_status.setText("转发微博");
                cardStatus = retweeted_status;
            }

            String imgUrl = cardStatus.getThumbnail_pic();
            if (TextUtils.isEmpty(imgUrl)) {
                iv_rstatus_img.setVisibility(View.GONE);
            } else {
                iv_rstatus_img.setVisibility(View.VISIBLE);
                imageLoader.displayImage(imgUrl, iv_rstatus_img);
            }

            tv_rstatus_username.setText("@" + cardStatus.getUser().getName());
            tv_rstatus_content.setText(cardStatus.getText());
            include_retweeted_status_card.setVisibility(View.VISIBLE);
            iv_image.setVisibility(View.GONE);
        } else {
            include_retweeted_status_card.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化表情面板内容
     */
    private void initEmotion() {
        int screenWidth = DisplayUtils.getScreenWidthPixels(this);
        int spacing = DisplayUtils.dp2px(this, 8);

        int itemWidth = (screenWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<String> emotionNames = new ArrayList<String>();
        for (String emojiName : Emotion.emojiMap.keySet()) {
            emotionNames.add(emojiName);

            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);

                emotionNames = new ArrayList<String>();
            }
        }

        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

        emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        vp_emotion_dashboard.setLayoutParams(params);
        vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth,
                                           int gvHeight) {
        GridView gv = new GridView(this);
        gv.setBackgroundResource(R.color.bg_gray);
        gv.setSelector(R.color.transparent);
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);

        LayoutParams params = new LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);

        EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);

        return gv;
    }

    /**
     * 更新图片显示
     */
    private void updateImgs() {
        if (imgUris.size() > 0) {
            gv_write_status.setVisibility(View.VISIBLE);
            statusImgsAdapter.notifyDataSetChanged();
        } else {
            gv_write_status.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_iv_left:
                finish();
                break;
            case R.id.titlebar_tv_right:
                sendStatus();
                break;
            case R.id.iv_image:
                ImageUtils.showImagePickDialog(this);
                break;
            case R.id.iv_at:
                //@用户
                break;
            case R.id.iv_topic:
                //话题
                et_write_status.setText("##" + et_write_status.getText());
                et_write_status.setSelection(++topicCount);
                break;
            case R.id.iv_emoji:
                if (ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
                    ll_emotion_dashboard.setVisibility(View.GONE);
                    iv_emoji.setImageResource(R.drawable.btn_insert_emotion);
                } else {
                    ll_emotion_dashboard.setVisibility(View.VISIBLE);
                    iv_emoji.setImageResource(R.drawable.btn_insert_keyboard);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdapter = parent.getAdapter();
        if (itemAdapter instanceof WriteStatusGridImgsAdapter) {
            if (position == statusImgsAdapter.getCount() - 1) {
                ImageUtils.showImagePickDialog(this);
            }
        } else if (itemAdapter instanceof EmotionGvAdapter) {
            EmotionGvAdapter emotionAdapter = (EmotionGvAdapter) itemAdapter;

            if (position == emotionAdapter.getCount() - 1) {
                et_write_status.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                String emotionName = emotionAdapter.getItem(position);

                int curPosition = et_write_status.getSelectionStart();
                StringBuilder sb = new StringBuilder(et_write_status.getText().toString());
                sb.insert(curPosition, emotionName);

                SpannableString weiboContent = StringUtils.getWeiboContent(this, et_write_status, sb.toString());
                et_write_status.setText(weiboContent);

                et_write_status.setSelection(curPosition + emotionName.length());
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                Uri imageUri = data.getData();

                imgUris.add(imageUri);
                updateImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                } else {
                    Uri imageUriCamera = ImageUtils.imageUriFromCamera;

                    imgUris.add(imageUriCamera);
                    updateImgs();
                }
                break;

            default:
                break;
        }
    }

}
