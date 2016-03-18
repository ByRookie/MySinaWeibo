package com.huliang.weibo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huliang.weibo.R;
import com.huliang.weibo.api.BoreWeiboApi;
import com.huliang.weibo.api.SimpleRequestListener;
import com.huliang.weibo.constants.AccessTokenKeeper;
import com.huliang.weibo.constants.Constants;
import com.huliang.weibo.entity.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;


public class SplashActivity extends Activity implements View.OnClickListener {

    private Button btn_login;
    private ImageView iv_slogan;
    private WeiboAuth mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;
    private SharedPreferences sharedPreferences;
    private Oauth2AccessToken accessToken;
    private Handler handler = new Handler() {
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        long startTime = System.currentTimeMillis();
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        btn_login = (Button) findViewById(R.id.btn_login);
        iv_slogan = (ImageView) findViewById(R.id.iv_slogan);
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(SplashActivity.this, mAuthInfo);
        //为图片设置一个透明度动画
        AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
        animation.setDuration(1000);
        iv_slogan.setAnimation(animation);
        //设置登录按钮的可见性
        if (mAccessToken.isSessionValid()) {
            btn_login.setVisibility(View.INVISIBLE);
            BoreWeiboApi api = new BoreWeiboApi(SplashActivity.this);
            mAccessToken = AccessTokenKeeper.readAccessToken(this);
            synchronized (api) {
                api.usersShow(mAccessToken.getUid(), null, new SimpleRequestListener(SplashActivity.this, null) {
                    @Override
                    public void onComplete(String response) {
                        sharedPreferences.edit().putString("user", response).commit();
                    }
                });
            }
            long endTime = System.currentTimeMillis();
            //设置显示时间2s
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000 - endTime + startTime);
        } else {
            btn_login.setVisibility(View.VISIBLE);
            btn_login.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        mSsoHandler.authorize(new AuthListener());
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(SplashActivity.this, mAccessToken);
                Toast.makeText(SplashActivity.this, "auth_success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "auth_failed";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(SplashActivity.this, "cancel auth", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(SplashActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
