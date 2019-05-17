package com.dace.textreader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.App;
import com.dace.textreader.R;
import com.dace.textreader.adapter.VersionContentAdapter;
import com.dace.textreader.audioUtils.AppHelper;
import com.dace.textreader.audioUtils.PlayService;
import com.dace.textreader.service.DownloadService;
import com.dace.textreader.util.ActivityUtils;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.NetWorkUtils;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.VersionInfoUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 基础Activity
 * 包括后台登录、版本更新、检查邀请码、老师码、其他设备登录检测
 */
public class BaseActivity extends AppCompatActivity {

    //TOKEN登录接口
    public static String TOKEN_URL = HttpUrlPre.HTTP_URL + "/tokenLogin";
    //查询最新的应用版本
    private final String versionUrl = HttpUrlPre.HTTP_URL +
            "/manager/application/select/new?apikey=8a801df7917547a5ad91aff6ab133f15";
    //查询学生
    private static final String studentUrl = HttpUrlPre.HTTP_URL + "/select/is/not/invite";
    //查询老师编码
    private static final String teacherUrl = HttpUrlPre.HTTP_URL + "/select/code/relation/student";
    //绑定老师
    private static final String teacherBindUrl = HttpUrlPre.HTTP_URL + "/regularRelation/setup";
    //查询会员卡活动
    private static final String memberUrl = HttpUrlPre.HTTP_URL + "/card/vip/code/identify";

    private App application;

    private BaseActivity mContext;

    private IntentFilter intentFilter;

    private boolean isResume = false;

    private boolean isNeedCheckCode = true;

    //下载相关
    private TextView tv_apply;
    private LinearLayout ll_progress;

    //应用版本号
    private String app_version = "";
    //App更新时的更新内容
    private String appUpdateContent = "";
    private List<String> mList_version = new ArrayList<>();
    private String mDownloadUrl = "";

    private BaseNiceDialog dialog_system_upgrade = null;

    //是否需要检查版本更新
    public static boolean isNeedCheckVersion = false;
    private boolean hasBindService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
        if (application == null) {
            application = (App) getApplication();
        }

        mContext = this;

        application.addActivity(mContext);

        intentFilter = new IntentFilter();
        intentFilter.addAction(HttpUrlPre.ACTION_BROADCAST_OTHER_DEVICE);
        intentFilter.addAction(HttpUrlPre.ACTION_BROADCAST_SYSTEM_UPGRADE);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, intentFilter);

        if (NewMainActivity.STUDENT_ID == -1
                && !ActivityUtils.isExsitMianActivity(mContext, NewMainActivity.class)) {
            //当没有从主界面进入APP，而是在启动页进入APP二级页面时，需要用到
            hideLogin();
        }

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    /**
     * 后台登录
     */
    private void hideLogin() {
        if (NewMainActivity.STUDENT_ID != -1) {
            return;
        }
        if (NewMainActivity.TOKEN.equals("")) {
            //获取存储在SharedPreferences中的TOKEN
            SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
            NewMainActivity.TOKEN = sharedPreferences.getString("token", "");
        }
        if (NewMainActivity.TOKEN.equals("")) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                String data = "";
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("token", NewMainActivity.TOKEN);
                    RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                    Request request = new Request.Builder()
                            .url(TOKEN_URL)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    data = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    NewMainActivity.TOKEN = "";
                    NewMainActivity.STUDENT_ID = -1;
                }
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = data;
                mHandler.sendMessage(msg);
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
                    String data = (String) msg.obj;
                    analyzeLoginData(data);
                    break;
            }
        }
    };

    /**
     * 分析数据
     *
     * @param data 数据
     */
    private void analyzeLoginData(String data) {
        try {
            JSONObject json = new JSONObject(data);
            int status = json.optInt("status", -1);
            if (status == 200) {
                JSONObject student = json.getJSONObject("data");
                NewMainActivity.STUDENT_ID = student.optLong("studentid", -1);
                NewMainActivity.TOKEN = student.getString("token");
                NewMainActivity.USERNAME = student.getString("username");
                NewMainActivity.USERIMG = student.getString("userimg");
                NewMainActivity.GRADE = student.optInt("level", -1);
                NewMainActivity.GRADE_ID = student.optInt("gradeid", 110);
                NewMainActivity.PY_SCORE = student.getString("score");
                NewMainActivity.LEVEL = student.optInt("level", -1);
                NewMainActivity.PHONENUMBER = student.getString("phonenum");
                NewMainActivity.DESCRIPTION = student.getString("description");

                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString("token", NewMainActivity.TOKEN);
                editor.apply();

            } else {
                clearUser();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            clearUser();
        }
    }

    public void setNeedCheckCode(boolean needCheckCode) {
        isNeedCheckCode = needCheckCode;
    }

    @Override
    protected void onResume() {
        super.onResume();

        isResume = true;
        if (isNeedCheckVersion) {
            checkVersion();
        } else {
            if (isNeedCheckCode) {
                if (teacherCode.equals("")) {
                    getClipContent();
                } else {
                    checkTeacher(teacherCode);
                }
            }
        }

    }

    public boolean isResume() {
        return isResume;
    }

    public void backToMainActivity() {
        application.removeAllActivity();
    }

    public void removeMainActivity(NewMainActivity activity) {
        application.removeMainActivity(activity);
    }

    private String studentCode = "";  //学生邀请码
    private String teacherCode = "";  //老师邀请码
    private String cardId = "";  //会员卡ID
    private String couponCode = "";  //会员优惠码

    /**
     * 获取剪切板内容
     */
    public void getClipContent() {
        String content = DataUtil.getClipboardContent(mContext);
        if (content == null || content.equals("")) {
            return;
        }

        if (content.contains("&friendInvitation")) {  //学生邀请码
            String code = content.substring(0, content.indexOf("&friendInvitation"));
            if (!code.equals("")) {
                studentCode = code;
                checkStudent(studentCode);
            } else {
                studentCode = "";
            }
            DataUtil.copyEmptyContentNoTips(mContext, "");
        } else if (content.contains("&teacher")) {
            String code = content.substring(0, content.indexOf("&teacher"));
            if (!code.equals("")) {
                teacherCode = code;
                checkTeacher(teacherCode);
            } else {
                teacherCode = "";
            }
            DataUtil.copyEmptyContentNoTips(mContext, "");
        } else if (content.contains("studyCard?")) {
            if (content.contains("agentCode=") && content.contains("cardId=")) {
                couponCode = content.substring(20, content.indexOf("cardId=") - 1);
                cardId = content.substring(content.indexOf("cardId=") + 7);
                checkCouponCode();
            } else {
                cardId = "";
                couponCode = "";
            }
            if (NewMainActivity.STUDENT_ID != -1) {
                DataUtil.copyEmptyContentNoTips(mContext, "");
            }
        }
    }

    /**
     * 检查学生邀请码
     *
     * @param studentCode
     */
    public void checkStudent(String studentCode) {
        new GetStudentData(mContext).execute(studentUrl, studentCode,
                String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 检查老师邀请码
     *
     * @param teacherCode
     */
    public void checkTeacher(String teacherCode) {
        new GetTeacherData(mContext).execute(teacherUrl, teacherCode,
                String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 检查会员优惠码
     */
    private void checkCouponCode() {
        if (cardId.equals("") || couponCode.equals("")) {
            return;
        }
        new GetMemberData(mContext).execute(memberUrl, String.valueOf(NewMainActivity.STUDENT_ID)
                , cardId, couponCode);
    }

    /**
     * 显示优惠码对话框
     */
    private void showCouponDialog(final String title, final String content,
                                  final String image, final String button) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_invite_coupon_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        final ImageView imageView = holder.getView(R.id.iv_pic_invite_coupon_dialog);
                        TextView tv_name = holder.getView(R.id.tv_name_invite_coupon_dialog);
                        TextView tv_code = holder.getView(R.id.tv_code_invite_coupon_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_invite_coupon_dialog);
                        RelativeLayout rl_submit = holder.getView(R.id.rl_submit_invite_coupon_dialog);
                        TextView tv_submit = holder.getView(R.id.tv_submit_invite_coupon_dialog);
                        ImageView iv_close = holder.getView(R.id.iv_close_invite_coupon_layout);

                        if (isDestroyed()) {
                            return;
                        }
                        RequestOptions options = new RequestOptions()
                                .centerCrop();
                        Glide.with(mContext)
                                .asBitmap()
                                .load(image)
                                .apply(options)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        dialog.dismiss();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        imageView.setImageBitmap(resource);
                                    }
                                });
                        tv_name.setText(title);
                        String code = "序号：" + couponCode;
                        tv_code.setText(code);
                        tv_content.setText(content);
                        tv_submit.setText(button);

                        rl_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NewMainActivity.STUDENT_ID == -1) {
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                } else {
                                    Intent intent = new Intent(mContext, MemberCentreActivity.class);
                                    intent.putExtra("id", cardId);
                                    intent.putExtra("code", couponCode);
                                    startActivity(intent);
                                }
                                dialog.dismiss();
                            }
                        });
                        iv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setWidth(310)
                .setHeight(405)
                .show(getSupportFragmentManager());
    }

    /**
     * 广播接收器
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case HttpUrlPre.ACTION_BROADCAST_OTHER_DEVICE:
                    if (isResume && mDialog == null) {
                        showExistDialog();
                    }
                    break;
                case HttpUrlPre.ACTION_BROADCAST_SYSTEM_UPGRADE:
                    if (isResume) {
                        checkVersion();
                    } else {
                        isNeedCheckVersion = true;
                    }
                    break;
            }
        }
    };

    /**
     * 检查版本
     */
    private void checkVersion() {
        isNeedCheckVersion = false;
        bindDownloadService();
        new GetAppVersionData(mContext).execute(versionUrl +
                "&studentId=" + NewMainActivity.STUDENT_ID +
                "&appVersion=" + VersionInfoUtil.getVersionName(mContext));
    }

    public static boolean isShowSystemUpgradeDialog = false;

    /**
     * 显示系统维护弹窗
     */
    private void showSystemUpgradeDialog() {
        if (isShowSystemUpgradeDialog) {
            return;
        }
        isShowSystemUpgradeDialog = true;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_system_upgrade_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        dialog_system_upgrade = dialog;
                        ImageView imageView = holder.getView(R.id.iv_state_system_upgrade_dialog);
                        GlideUtils.loadImageWithNoOptions(mContext,
                                R.drawable.image_system_state_fail, imageView);
                    }
                })
                .setShowBottom(false)
                .setOutCancel(false)
                .setMargin(56)
                .show(getSupportFragmentManager());
    }

    /**
     * 隐藏系统维护弹窗
     */
    private void hideSystemUpgradeDialog() {
        isNeedCheckVersion = false;
        isShowSystemUpgradeDialog = false;
        if (dialog_system_upgrade != null) {
            dialog_system_upgrade.dismiss();
            dialog_system_upgrade = null;
        }
    }

    private BaseNiceDialog mDialog = null;

    /**
     * 账号在别处登录
     */
    private void showExistDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_single_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        mDialog = dialog;
                        TextView tv_title = holder.getView(R.id.tv_title_single_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_single_choose_dialog);
                        TextView tv_sure = holder.getView(R.id.tv_sure_single_choose_dialog);
                        tv_title.setText("下线通知");
                        String content = "您的账号在另一设备登录，您被迫下线了。" +
                                "如果这不是您本人的操作，那么您的密码可能已经泄漏，" +
                                "建议您修改密码。";
                        tv_content.setText(content);
                        tv_sure.setText("重新登录");
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clearUser();
                                stopLessonPlayService();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.putExtra("hideBack", true);
                                startActivity(intent);
                            }
                        });
                    }
                })
                .setMargin(60)
                .setShowBottom(false)
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    public void hideDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 停止微课播放
     */
    private void stopLessonPlayService() {
        if (getPlayService() != null) {
            getPlayService().pause();
            getPlayService().stop();
            getPlayService().hideFloatView();
        }
    }

    /**
     * 获取到播放音乐的服务
     *
     * @return PlayService对象
     */
    public PlayService getPlayService() {
        PlayService playService = AppHelper.get().getPlayService();
        return playService;
    }

    /**
     * 退出登录，清除用户信息
     */
    private void clearUser() {
        NewMainActivity.TOKEN = "";
        NewMainActivity.STUDENT_ID = -1;
        NewMainActivity.USERNAME = "";
        NewMainActivity.GRADE = -1;
        NewMainActivity.LEVEL = -1;
        NewMainActivity.PY_SCORE = "";
        NewMainActivity.USERIMG = "";
        NewMainActivity.NEWS_COUNT = 0;
        NewMainActivity.PHONENUMBER = "";
        NewMainActivity.DESCRIPTION = "";

        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString("token", "");
        editor.apply();
    }

    /**
     * 获取学生数据
     */
    private static class GetStudentData
            extends WeakAsyncTask<String, Void, String, BaseActivity> {

        protected GetStudentData(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BaseActivity activity, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("code", strings[1]);
                object.put("studentId", strings[2]);
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BaseActivity activity, String s) {
            if (s != null) {
                activity.analyzeStudentData(s);
            }
        }
    }

    /**
     * 分析学生数据
     *
     * @param s
     */
    private void analyzeStudentData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            int status = jsonObject.optInt("status", -1);
            JSONObject object = jsonObject.getJSONObject("data");
            String userImg = object.getString("userImg");
            String username = object.getString("userName");
            showStudentDialog(status, userImg, username, studentCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示新人礼包
     *
     * @param status
     * @param userImg
     * @param username
     */
    private void showStudentDialog(final int status, final String userImg, final String username,
                                   final String code) {
        studentCode = "";
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_invite_student_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        RelativeLayout rl_user = holder.getView(R.id.rl_user_invite_student_dialog);
                        ImageView iv_user = holder.getView(R.id.iv_user_invite_student_dialog);
                        TextView tv_user = holder.getView(R.id.tv_user_invite_student_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_invite_student_dialog);
                        RelativeLayout rl_submit = holder.getView(R.id.rl_submit_invite_student_dialog);
                        TextView tv_submit = holder.getView(R.id.tv_submit_invite_student_dialog);
                        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());

                        String content = "";
                        if (status == 700) {
                            GlideUtils.loadUserImage(mContext, userImg, iv_user);
                            tv_user.setText(username);
                            content = "邀请你一起来派知语文学习，\n" +
                                    "并为你送上一份新人“荐”面礼，\n" +
                                    "马上去注册查看新人大礼包吧！";
                            tv_submit.setText("查看新人礼包");
                        } else if (status == 200 || status == 400) {
                            rl_user.setVisibility(View.GONE);
                            iv_user.setVisibility(View.GONE);
                            tv_user.setVisibility(View.GONE);
                            content = "你已注册领取过新人大礼包！\n" +
                                    "暂不能参加应邀！";
                            tv_submit.setText("查看我的礼包");
                            rl_submit.setVisibility(View.GONE);
                        }
                        tv_content.setText(content);

                        rl_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (status == 200) {
                                    if (NewMainActivity.STUDENT_ID == -1) {
                                        startActivity(new Intent(mContext, LoginActivity.class));
                                    } else {
                                        startActivity(new Intent(mContext, MyselfNewsActivity.class));
                                    }
                                } else if (status == 400) {
                                    startActivity(new Intent(mContext, MyselfNewsActivity.class));
                                } else if (status == 700) {
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setWidth(310)
                .setHeight(461)
                .setShowBottom(false)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 获取老师数据
     */
    private static class GetTeacherData
            extends WeakAsyncTask<String, Void, String, BaseActivity> {

        protected GetTeacherData(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BaseActivity activity, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("teacherCode", strings[1]);
                object.put("studentId", strings[2]);
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BaseActivity activity, String s) {
            if (s != null) {
                activity.analyzeTeacherData(s);
            }
        }
    }

    /**
     * 分析老师数据
     *
     * @param s
     */
    private void analyzeTeacherData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            int status = jsonObject.optInt("status", -1);
            JSONObject object = jsonObject.getJSONObject("data");
            String teacherName = object.getString("username");
            long teacherId = object.optLong("userId", -1);
            showTeacherDialog(status, teacherName, teacherCode, String.valueOf(teacherId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示老师邀请对话框
     *
     * @param status
     * @param teacherName
     * @param code
     * @param teacherId
     */
    private void showTeacherDialog(final int status, final String teacherName,
                                   final String code, final String teacherId) {
        teacherCode = "";
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_invite_teacher_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_content = holder.getView(R.id.tv_content_invite_teacher_dialog);
                        RelativeLayout rl_submit = holder.getView(R.id.rl_submit_invite_teacher_dialog);
                        TextView tv_submit = holder.getView(R.id.tv_submit_invite_teacher_dialog);
                        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());

                        String content = "";
                        if (status == 200 || status == 700) {
                            content = "你正在绑定" + teacherName
                                    + "\n（编码：" + code
                                    + "）\n点击下方按钮完成绑定申请。";
                            tv_submit.setText("申请绑定");
                        } else if (status == 400) {
                            content = "你已提交绑定" + teacherName
                                    + "，\n（编码：" + code + "）";
                            tv_submit.setText("查看详情");
                        }
                        tv_content.setText(content);

                        rl_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (status == 200) {
                                    bindTeacher(code, teacherId);
                                } else if (status == 400) {
                                    startActivity(new Intent(mContext, WritingWorkActivity.class));
                                } else if (status == 700) {
                                    teacherCode = code;
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setWidth(310)
                .setHeight(350)
                .setShowBottom(false)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 绑定老师
     *
     * @param teacherCode
     * @param teacherId
     */
    private void bindTeacher(String teacherCode, String teacherId) {
        showTips("正在提交申请，请稍候...");
        new BindTeacher(mContext).execute(teacherBindUrl, teacherCode, teacherId,
                String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 显示吐丝
     *
     * @param tips
     */
    protected void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 绑定老师
     */
    private static class BindTeacher
            extends WeakAsyncTask<String, Void, String, BaseActivity> {

        protected BindTeacher(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BaseActivity activity, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("teacherCode", strings[1]);
                object.put("teacherId", strings[2]);
                object.put("studentId", strings[3]);
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BaseActivity activity, String s) {
            if (s != null) {
                activity.analyzeBindData(s);
            }
        }
    }

    /**
     * 分析绑定老师数据
     *
     * @param s
     */
    private void analyzeBindData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                showBindTeacherSuccessDialog();
            } else if (600 == jsonObject.optInt("status", -1)) {
                showTips("关系不合法");
            } else if (700 == jsonObject.optInt("status", -1)) {
                showTips("已提交等待老师确认，请勿再次发送");
            } else {
                showTips("绑定失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("绑定失败");
        }
    }

    /**
     * 显示绑定老师成功对话框
     */
    private void showBindTeacherSuccessDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_invite_teacher_success_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        ImageView imageView =
                                holder.getView(R.id.iv_check_invite_teacher_success_dialog);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(mContext, WritingWorkActivity.class));
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setWidth(238)
                .setShowBottom(false)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 获取会员活动数据
     */
    private static class GetMemberData
            extends WeakAsyncTask<String, Void, String, BaseActivity> {

        protected GetMemberData(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BaseActivity activity, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("cardId", strings[2]);
                object.put("code", strings[3]);
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BaseActivity activity, String s) {
            if (s != null) {
                activity.analyzeMemberData(s);
            }
        }
    }

    /**
     * 分析会员卡数据
     *
     * @param s
     */
    private void analyzeMemberData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject json = jsonObject.getJSONObject("data");
                JSONObject object = json.getJSONObject("card");
                String title = object.getString("title");
                String tips = object.getString("tips");
                JSONObject object_tips = new JSONObject(tips);
                String content = object_tips.getString("activeNotice");
                String image = object_tips.getString("activeImg");
                String button = object_tips.getString("buttonDisplay");
                showCouponDialog(title, content, image, button);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            cardId = "";
            couponCode = "";
        }
    }

    /**
     * 获取应用的版本数据
     */
    private static class GetAppVersionData
            extends WeakAsyncTask<String, Integer, String, BaseActivity> {

        protected GetAppVersionData(BaseActivity mainActivity) {
            super(mainActivity);
        }

        @Override
        protected String doInBackground(BaseActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BaseActivity activity, String s) {
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int status = jsonObject.getInt("status");


                    if (status!=200){
                        JSONObject data = jsonObject.getJSONObject("data");
                        activity.app_version = data.getString("version");
                        activity.appUpdateContent = data.getString("content");
                        activity.mDownloadUrl = data.getString("downloadAddress");
                    }



                        //检查新版本
                        activity.checkVersionData(status);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查新版本数据
     */
    private void checkVersionData(int status) {
        if (!isResume) {
            isNeedCheckVersion = true;
            return;
        }


        if (status!=200){
            String[] appContentString = appUpdateContent.split("\n");
            for (int i = 0; i < appContentString.length; i++) {
                mList_version.add(appContentString[i]);
            }
        }
        if(status==700){
            showAppUpdateDialog(false);
        }
        if (status==800) {
            showAppUpdateDialog(true);
        }

        if (status==600) {
            showSystemUpgradeDialog();
        }

        if (status == 200){
            hideSystemUpgradeDialog();
        }
    }

    public static boolean isShowAppUpdateDialog = false;

    /**
     * 显示App更新提示
     */
    private void showAppUpdateDialog(final boolean hasForce) {
        if (SystemClock.currentThreadTimeMillis() - (long)PreferencesUtil.getData(getApplicationContext(),"lastCloseDialogTime",0) < 24 * 60 * 60 * 1000){
            return;
        }

        if (isShowAppUpdateDialog) {
            return;
        }
        isShowAppUpdateDialog = true;
        boolean hasExists = false;
        final String fileName = mDownloadUrl.substring(mDownloadUrl.lastIndexOf("/"));

        final File file = new File(DataUtil.getDownloadFileName(fileName, app_version));
        if (file.exists()) {
            hasExists = true;
        }

        final View view = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_forced_app_update_layout, null);
        ImageView iv_bg = view.findViewById(R.id.iv_bg_version_update);
        tv_apply = view.findViewById(R.id.tv_apply_version_update);
        ImageView iv_close = view.findViewById(R.id.iv_close_version_update);
        TextView tv_version = view.findViewById(R.id.tv_version_update);
        TextView tv_tips = view.findViewById(R.id.tv_wifi_tips_version_update);
        ll_progress = view.findViewById(R.id.ll_download_info_version_update);
        final ProgressBar progressBar = view.findViewById(R.id.progress_bar_version_update);
        final TextView tv_progress = view.findViewById(R.id.tv_progress_version_update);

        if (hasExists) {
            tv_tips.setVisibility(View.VISIBLE);
            tv_apply.setText("安装");
        } else {
            tv_tips.setVisibility(View.GONE);
            tv_apply.setText("更新");
        }
        if (hasForce) {
            iv_close.setVisibility(View.GONE);
        }
        if (app_version.contains("f")) {
            app_version = app_version.split("f")[0];
        }
        tv_version.setText(app_version);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_content_version_update);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final VersionContentAdapter adapter =
                new VersionContentAdapter(mContext, mList_version);
        recyclerView.setAdapter(adapter);
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_version_update, iv_bg);
        tv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMainActivity.isInitiativeClose = false;
                File file = new File(DataUtil.getDownloadFileName(fileName, app_version));
                if (file.exists()) {
                    installationAPK(fileName);
                } else {
                    //WRITE_EXTERNAL_STORAGE的权限申请
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        goToDownload();
                        if (!hasForce) {
                            ((ViewGroup) getWindow().getDecorView()).removeView(view);
                            isShowAppUpdateDialog = false;
                        }
                    } else {
                        if (mDownloadBinder != null) {
                            tv_apply.setVisibility(View.GONE);
                            ll_progress.setVisibility(View.VISIBLE);
                            mDownloadBinder.setProgress(progressBar, tv_progress);
                            startDownloadAPK();
                        } else {
                            goToDownload();
                            if (!hasForce) {
                                ((ViewGroup) getWindow().getDecorView()).removeView(view);
                                isShowAppUpdateDialog = false;
                            }
                        }
                    }
                }
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasForce) {
                    if (ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {  //先判断是否有写入文件的权限
                        Log.d("111","==================>WRITE_EXTERNAL_STORAGE");
                        if (NetWorkUtils.isWifiConnected(mContext) && !file.exists()) {
                            NewMainActivity.isInitiativeClose = true;
                            startDownloadAPK();
                        }
                    } else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            if (ContextCompat.checkSelfPermission(BaseActivity.this,
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                    != PackageManager.PERMISSION_GRANTED) {
//                                requestPermissions(new String[]{
//                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
//                            }
//                        }
                    }
                    ((ViewGroup) getWindow().getDecorView()).removeView(view);
                    isShowAppUpdateDialog = false;
                    PreferencesUtil.saveData(getApplicationContext(),"lastCloseDialogTime",SystemClock.currentThreadTimeMillis());
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        ((ViewGroup) getWindow().getDecorView()).addView(view, layoutParams);
    }

    /**
     * 前往应用宝下载
     */
    private void goToDownload() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(HttpUrlPre.APP_DOWNLOAD_URL);
        intent.setData(content_url);
        startActivity(intent);
    }

    private String apkName = "";

    /**
     * 安装APK
     */
    private void installationAPK(String name) {

        apkName = name;

        if (Build.VERSION.SDK_INT >= 26) {
            boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                //跳转至“安装未知应用”权限界面，引导用户开启权限
                Uri selfPackageUri = Uri.parse("package:com.dace.textreader");
                Intent intent_request = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, selfPackageUri);
                startActivityForResult(intent_request, 110);
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(DataUtil.getDownloadFileName(name, app_version));
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(mContext, "com.dace.textreader.provider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(DataUtil.getDownloadFileName(name, app_version))),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            installationAPK(apkName);
        }
        if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 111) {
                startDownloadAPK();
            }
        }
    }

    /**
     * 开始下载安装包
     */
    private void startDownloadAPK() {
        if (mDownloadBinder != null) {
            Log.d("111","==================>startDownloadAPK");
            mDownloadBinder.startDownload(mDownloadUrl, app_version);
        }
    }

    /**
     * 绑定下载服务
     */
    private void bindDownloadService() {
        if (hasBindService) {
            return;
        }
        hasBindService = true;
        Intent intent = new Intent();
        intent.setClass(this, DownloadService.class);
        mDownloadServiceConnection = new DownloadServiceConnection();
        bindService(intent, mDownloadServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 下载服务相关
     */
    private DownloadService.DownloadBinder mDownloadBinder;

    private DownloadServiceConnection mDownloadServiceConnection;

    /**
     * 下载服务
     */
    private class DownloadServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取到DownloadBinder的实例，用这个实例在活动中调用服务提供的各种方法
            mDownloadBinder = (DownloadService.DownloadBinder) service;
            mDownloadBinder.getService().setOnDownLoadSuccessListen(new DownloadService.OnDownLoadSuccess() {
                @Override
                public void onSuccess() {
                    if (tv_apply != null && ll_progress != null) {
                        ll_progress.setVisibility(View.GONE);
                        tv_apply.setVisibility(View.VISIBLE);
                        tv_apply.setText("安装");
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    @Override
    protected void onStop() {
        isResume = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mDownloadServiceConnection != null) {
            mDownloadBinder.pauseDownload();
            unbindService(mDownloadServiceConnection);
        }
        if (intentFilter != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);
            intentFilter = null;
        }
        application.removeActivity(mContext);
        super.onDestroy();
    }

    protected void showLoading(FrameLayout frameLayout){
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.view_loading, null);

        ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
        frameLayout.removeAllViews();
        frameLayout.addView(view);
        frameLayout.setVisibility(View.VISIBLE);
    }

    protected void showDefaultView(FrameLayout frameLayout, int imageResource, String tipsText, boolean isGif, boolean isButton, String buttonText, final OnButtonClick onButtonClick){
        View view = LayoutInflater.from(this)
                .inflate(R.layout.list_default_layout, null);

        ImageView imageView = view.findViewById(R.id.iv_state);
        if (isGif) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = DensityUtil.dip2px(this,110);
            layoutParams.height = DensityUtil.dip2px(this,110);
            imageView.setLayoutParams(layoutParams);
            GlideUtils.loadGIFImageWithNoOptions(this, R.drawable.image_loading, imageView);
        } else {
            GlideUtils.loadImageWithNoOptions(this, imageResource, imageView);
        }

        TextView tv_tips = view.findViewById(R.id.tv_tips);
        TextView tv_operation = view.findViewById(R.id.tv_operation);

        tv_tips.setText(tipsText);
        tv_operation.setText(buttonText);
        if (isButton) {
            tv_operation.setVisibility(View.VISIBLE);
            if (onButtonClick != null) {
                tv_operation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClick.onButtonClick();
                    }
                });
            }
        } else {
            tv_operation.setVisibility(View.GONE);
        }

        frameLayout.removeAllViews();
        frameLayout.addView(view);
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    protected void showNetFailView(FrameLayout frameLayout,final OnButtonClick onButtonClick){
        showDefaultView(frameLayout, R.drawable.image_state_netfail, "加载数据失败，请重试～", false, true, "重新加载", onButtonClick);
    }
    protected void showLoadingView(FrameLayout frameLayout){
        showDefaultView(frameLayout, R.drawable.image_loading, "", true, false, "", null);
    }

    protected void showEmptyView(FrameLayout frameLayout){
        showDefaultView(frameLayout, R.drawable.image_state_empty, "暂无内容", false, false, "", null);
    }

    public interface OnButtonClick{
        void onButtonClick();
    }
    protected boolean isLogin(){
        Object studeenObj = PreferencesUtil.getData(this,"studentId","-1");
        if(studeenObj == null)
            return false;
        String studentId = studeenObj.toString();

        return !studentId.equals("-1");
    }

    protected void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
