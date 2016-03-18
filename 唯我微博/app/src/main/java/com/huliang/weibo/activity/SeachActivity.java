package com.huliang.weibo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.huliang.weibo.R;

/**
 * Created by huliang on 16/3/18.
 */
public class SeachActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;
    private EditText et_content;
    private RadioButton rb_content;
    private RadioButton rb_person;
    private FrameLayout fragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach);
        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.iv_back);
        et_content = (EditText) findViewById(R.id.et_content);
        rb_content = (RadioButton) findViewById(R.id.rb_content);
        rb_person = (RadioButton) findViewById(R.id.rb_person);
        fragmentLayout = (FrameLayout) findViewById(R.id.fragment);

        imageView.setOnClickListener(this);
        et_content.setOnClickListener(this);
        rb_content.setOnClickListener(this);
        rb_person.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.et_content:

                break;
            case R.id.rb_content:
                break;
            case R.id.rb_person:
                break;
            default:
                break;
        }
    }
}
