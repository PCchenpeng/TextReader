package com.dace.textreader.wxapi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.dace.textreader.App;
import com.dace.textreader.R;
import com.dace.textreader.activity.BaseActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.WXBindNumberActivity;
import com.dace.textreader.util.HttpUrlPre;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private final int RETURN_MSG_TYPE_LOGIN = 1;
    private final String url = "https://api.weixin.qq.com/sns/oauth2/access_token?";
    private final String checkUrl = HttpUrlPre.HTTP_URL + "/thirdpartyLogin/weixinCheck?openid=";
    private final String getUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=";

    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;
    private String nickName = "";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);

        api = WXAPIFactory.createWXAPI(this, App.APP_ID, true);
        //如果没回调onResp，八成是这句没有写
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp.getType() == RETURN_MSG_TYPE_LOGIN) {
                    //拿到了微信返回的code,立马再去请求access_token
                    String code = ((SendAuth.Resp) baseResp).code;
                    sendCode(code);
                } else {
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                finish();
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    //通过code获取access_token
    private void sendCode(final String code) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url + "appid=" + App.APP_ID +
                                    "&secret=" + App.APP_SECRET +
                                    "&code=" + code +
                                    "&grant_type=" + "authorization_code")
                            .build();
                    Response response = client.newCall(request).execute();
                    String body = response.body().string();
                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = body;
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String body = (String) msg.obj;
                    WxLogin(body);
                    break;
                case 1:
                    getWxUser();
                    break;
                case 2:
                    //微信用户不是第一次登录，那就直接token登录
                    broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_JIGUANG_LOGIN);
                    if (NewMainActivity.isLoginHideBack) {
                        broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_USER_EXIT);
                        backToMainActivity();
                    }
                    finish();
                    break;
                case 3:
                    turnToBindPhoneNumber();
                    break;
            }
        }
    };

    /**
     * 发送广播
     *
     * @param action 广播的Action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * 获取微信用户信息后跳转到绑定号码界面
     */
    private void turnToBindPhoneNumber() {
        Intent intent = new Intent(WXEntryActivity.this, WXBindNumberActivity.class);
        intent.putExtra("access_token", access_token);
        intent.putExtra("openid", openid);
        intent.putExtra("name", nickName);
        intent.putExtra("unionId", unionid);
        startActivity(intent);
        finish();
    }

    /**
     * 微信登录
     *
     * @param body 返回的数据
     */
    private void WxLogin(String body) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            access_token = jsonObject.getString("access_token");
            expires_in = jsonObject.getString("expires_in");
            refresh_token = jsonObject.getString("refresh_token");
            openid = jsonObject.getString("openid");
            scope = jsonObject.getString("scope");
            unionid = jsonObject.getString("unionid");
            checkWX();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(WXEntryActivity.this, "授权失败,请重试！", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //检查微信用户是否第一次登录
    private void checkWX() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(checkUrl + openid + "&unionid=" + unionid)
                            .build();
                    Response response = client.newCall(request).execute();
                    String body = response.body().string();
                    JSONObject jsonObject = new JSONObject(body);
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        JSONObject json = jsonObject.getJSONObject("data");
                        String token = json.getString("token");

                        NewMainActivity.STUDENT_ID = json.getInt("studentid");
                        NewMainActivity.USERNAME = json.getString("username");
                        NewMainActivity.USERIMG = json.getString("userimg");
                        NewMainActivity.GRADE = json.getInt("level");
                        NewMainActivity.GRADE_ID = json.getInt("gradeid");
                        NewMainActivity.PY_SCORE = json.getString("score");
                        NewMainActivity.LEVEL = json.getInt("level");
                        NewMainActivity.PHONENUMBER = json.getString("phonenum");
                        NewMainActivity.DESCRIPTION = json.getString("description");

                        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                        editor.putString("token", token);
                        editor.putString("phoneNum", NewMainActivity.PHONENUMBER);
                        editor.apply();

                        Message msg = Message.obtain();
                        msg.what = 2;
                        msg.obj = token;
                        mHandler.sendMessage(msg);
                    } else if (status == 400) {
                        mHandler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        }.start();
    }

    /**
     * 获取微信用户信息
     */
    private void getWxUser() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(getUserInfoUrl + access_token + "&openid=" + openid)
                            .build();
                    Response response = client.newCall(request).execute();
                    String body = response.body().string();
                    JSONObject jsonObject = new JSONObject(body);
                    nickName = jsonObject.getString("nickname");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(3);
            }
        }.start();
    }
}