package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.dace.textreader.R;
import com.dace.textreader.adapter.HomeRecommendationAdapter;
import com.dace.textreader.bean.HomeRecommendationBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 用户主页
 */
public class UserHomepageActivity extends BaseActivity {

    private static final String userUrl = HttpUrlPre.HTTP_URL + "/follow/stat";
    private static final String compositionUrl = HttpUrlPre.HTTP_URL + "/select/personal/composition/release/list";
    //上传头像
    private static final String imageUrl = HttpUrlPre.UPLOAD_URL + "/file/synchronize/uploadFile";
    //更换背景图
    private static final String updateBackgroundUrl = HttpUrlPre.HTTP_URL + "/upload/user/surface";
    private static final String userFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/setup";
    private static final String userUnFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/cancel";

    private FrameLayout frameLayout;
    private NestedScrollView scrollView;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private RelativeLayout rl_back;
    private ImageView iv_back;
    private RelativeLayout rl_camera;
    private ImageView iv_camera;
    private ImageView iv_background;
    private ImageView iv_user;
    private ImageView iv_change_head;
    private RelativeLayout rl_operate;
    private ImageView iv_operate;
    private TextView tv_operate;
    private TextView tv_name;
    private TextView tv_grade;
    private TextView tv_description;
    private TextView tv_fans;
    private TextView tv_composition;
    private TextView tv_follow;
    private RecyclerView recyclerView;
    private TextView tv_empty;

    private UserHomepageActivity mContext;

    private long userId = -1;
    private String userBackground = "";
    private String userImage = "";
    private String name = "";
    private int gradeId = 110;
    private String description = "";
    private String fans = "";
    private String composition = "";
    private String follow = "";
    private int followed = 0;

    private List<HomeRecommendationBean> mList = new ArrayList<>();
    private HomeRecommendationAdapter adapter;
    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    private boolean isUploadBackground = false;
    private boolean isUploadUserImage = false;

    private ImagePicker imagePicker;//图片选择框架
    //背景图文件路径
    private String backgroundFilePath = "";
    private boolean isChooseUserHeadImg = true;  //是否是选择用户头像
    private CropImageView.CropShape crop_type;  //裁剪类型
    private int crop_w;  //裁剪宽高比
    private int crop_h;

    private LinearLayoutManager layoutManager;

    private boolean isNeedRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        mContext = this;

        userId = getIntent().getLongExtra("userId", -1);

        initView();
        initData();
        initEvents();
        setImmerseLayout(toolbar);

        imagePicker = new ImagePicker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedRefresh) {
            isNeedRefresh = false;
            updateOperateUi();
            initData();
        }
    }

    // view为标题栏
    protected void setImmerseLayout(View view) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(getBaseContext());
        view.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -iv_background.getHeight() / 2) {
                    //修改状态栏为暗色主题
                    int flag = StatusBarUtil.StatusBarLightMode(mContext);
                    StatusBarUtil.StatusBarLightMode(mContext, flag);
                    iv_back.setImageResource(R.drawable.icon_back);
                    iv_camera.setImageResource(R.drawable.icon_camera_stroke);
                } else {
                    //修改状态栏为亮色主题
                    int flag = StatusBarUtil.StatusBarLightMode(mContext);
                    StatusBarUtil.StatusBarDarkMode(mContext, flag);
                    iv_back.setImageResource(R.drawable.icon_shadow_back);
                    iv_camera.setImageResource(R.drawable.icon_camera);
                }
            }
        });
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == userId && userId != -1) {
                    if (isUploadBackground) {
                        showTips("正在上传背景图，请稍等...");
                    } else {
                        isChooseUserHeadImg = false;
                        getImagePath();
                    }
                }
            }
        });
        iv_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == userId && userId != -1) {
                    if (isUploadBackground) {
                        showTips("正在上传背景图，请稍等...");
                    } else {
                        isChooseUserHeadImg = false;
                        getImagePath();
                    }
                }
            }
        });
        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == userId && userId != -1) {
                    if (isUploadUserImage) {
                        showTips("正在上传头像，请稍等...");
                    } else {
                        isChooseUserHeadImg = true;
                        getImagePath();
                    }
                }
            }
        });
        rl_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    if (userId == NewMainActivity.STUDENT_ID) {
                        turnToChangeUserInfo();
                    } else {
                        followOrNot();
                    }
                }
            }
        });
        tv_composition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToArticle();
            }
        });
        tv_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToFans();
            }
        });
        tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToFollow();
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (!refreshing && !isEnd) {
                    getMoreCompositionData();
                }
            }
        });
        adapter.setOnItemClickListen(new HomeRecommendationAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList.size()) {
                    int type = mList.get(pos).getType();
                    if (type == 0) {
                        String writingId = mList.get(pos).getCompositionId();
                        turnToWritingDetail(writingId);
                        //增加阅读数
                        String views = mList.get(pos).getViews();
                        mList.get(pos).setViews(DataUtil.increaseViews(views));
                        adapter.notifyItemChanged(pos);
                    }
                }
            }
        });
    }

    /**
     * 滚动到文章
     */
    private void scrollToArticle() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                appBarLayout.setExpanded(false);
                scrollView.smoothScrollTo(0, recyclerView.getTop());
            }
        });
    }

    //获取头像路径
    private void getImagePath() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_update_user_image_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_tips = holder.getView(R.id.tv_tips_update_user_image);
                        TextView tv_file = holder.getView(R.id.tv_file_update_user_image);
                        TextView tv_camera = holder.getView(R.id.tv_camera_update_user_image);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_update_user_image);

                        if (isChooseUserHeadImg) {
                            tv_tips.setText("请选择更换头像");
                        } else {
                            tv_tips.setText("请选择更换背景");
                        }

                        tv_file.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toChooseFile();
                                dialog.dismiss();
                            }
                        });
                        tv_camera.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toCamera();
                                dialog.dismiss();
                            }
                        });
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 前往关注列表
     */
    private void turnToFollow() {
        isNeedRefresh = true;
        Intent intent = new Intent(mContext, FansListActivity.class);
        intent.putExtra("type", "follow");
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    /**
     * 前往粉丝列表
     */
    private void turnToFans() {
        isNeedRefresh = true;
        Intent intent = new Intent(mContext, FansListActivity.class);
        intent.putExtra("type", "fans");
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        isNeedRefresh = true;
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 前往作文详情
     *
     * @param id
     */
    private void turnToWritingDetail(String id) {
        Intent intent = new Intent(mContext, CompositionDetailActivity.class);
        intent.putExtra("writingId", id);
        intent.putExtra("area", 0);
        intent.putExtra("orderNum", "");
        startActivity(intent);
    }

    private void initData() {
        if (!refreshing) {
            showLoadingView(true);
            refreshing = true;
            new GetUserInfo(mContext).execute(userUrl, String.valueOf(userId),
                    String.valueOf(NewMainActivity.STUDENT_ID));
        }
    }

    private void initCompositionData() {
        refreshing = true;
        isEnd = false;
        pageNum = 1;
        mList.clear();
        adapter.notifyDataSetChanged();
        new GetData(mContext).execute(compositionUrl, String.valueOf(userId),
                String.valueOf(pageNum));
    }

    /**
     * 获取更多数据
     */
    private void getMoreCompositionData() {
        if (layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            refreshing = true;
            pageNum = pageNum + 1;
            new GetData(mContext).execute(compositionUrl, String.valueOf(userId),
                    String.valueOf(pageNum));
        }
    }

    private void initView() {
        frameLayout = findViewById(R.id.frame_user_homepage);
        scrollView = findViewById(R.id.nested_scroll_user_homepage);
        appBarLayout = findViewById(R.id.app_bar_user_homepage);
        toolbar = findViewById(R.id.toolbar_user_homepage);
        rl_back = findViewById(R.id.rl_back_user_homepage);
        iv_back = findViewById(R.id.iv_back_user_homepage);
        rl_camera = findViewById(R.id.rl_camera_user_homepage);
        iv_camera = findViewById(R.id.iv_camera_user_homepage);

        iv_background = findViewById(R.id.iv_background_user_homepage);
        iv_user = findViewById(R.id.iv_user_homepage);
        iv_change_head = findViewById(R.id.iv_change_head_user_homepage);
        rl_operate = findViewById(R.id.rl_operate_homepage);
        iv_operate = findViewById(R.id.iv_follow_user_homepage);
        tv_operate = findViewById(R.id.tv_operate_user_homepage);
        tv_name = findViewById(R.id.tv_name_user_homepage);
        tv_grade = findViewById(R.id.tv_grade_user_homepage);
        tv_description = findViewById(R.id.tv_description_user_homepage);
        tv_fans = findViewById(R.id.tv_fans_user_homepage);
        tv_composition = findViewById(R.id.tv_composition_user_homepage);
        tv_follow = findViewById(R.id.tv_follow_user_homepage);
        tv_empty = findViewById(R.id.tv_empty_user_homepage);

        recyclerView = findViewById(R.id.recycler_view_user_homepage);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomeRecommendationAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        updateOperateUi();
    }

    /**
     * 显示加载视图
     *
     * @param show
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        refreshing = show;
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
            ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
            GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    /**
     * 关注或取消关注
     */
    private void followOrNot() {
        if (followed == 1) {
            unFollowUser();
        } else {
            followUser();
        }
    }

    /**
     * 关注用户
     */
    private void followUser() {
        new FollowUser(mContext).execute(userFollowUrl,
                String.valueOf(userId), String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 取消关注用户
     */
    private void unFollowUser() {
        new FollowUser(mContext).execute(userUnFollowUrl,
                String.valueOf(userId), String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 前往修改个人资料
     */
    private void turnToChangeUserInfo() {
        isNeedRefresh = true;
        startActivity(new Intent(mContext, UserInfoChangeActivity.class));
    }

    /**
     * 前往拍照
     */
    private void toCamera() {
        if (isChooseUserHeadImg) {
            crop_type = CropImageView.CropShape.OVAL;
            crop_w = 1;
            crop_h = 1;
        } else {
            crop_type = CropImageView.CropShape.RECTANGLE;
            crop_w = 16;
            crop_h = 9;
        }
        // 设置标题
        imagePicker.setTitle("拍照");
        // 设置是否裁剪图片
        imagePicker.setCropImage(true);
        imagePicker.startCamera(mContext, imagePickerCallback);
    }

    /**
     * 前往选择照片
     */
    private void toChooseFile() {
        if (isChooseUserHeadImg) {
            crop_type = CropImageView.CropShape.OVAL;
            crop_w = 1;
            crop_h = 1;
        } else {
            crop_type = CropImageView.CropShape.RECTANGLE;
            crop_w = 16;
            crop_h = 9;
        }

        // 设置标题
        imagePicker.setTitle("选择图片");
        // 设置是否裁剪图片
        imagePicker.setCropImage(true);
        imagePicker.startGallery(mContext, imagePickerCallback);
    }

    /**
     * 图片选择器回调
     */
    private ImagePicker.Callback imagePickerCallback = new ImagePicker.Callback() {
        @Override
        public void onPickImage(Uri imageUri) {

        }

        @Override
        public void onCropImage(Uri imageUri) {
            super.onCropImage(imageUri);
            if (isChooseUserHeadImg) {
                uploadUserImage(imageUri);
            } else {
                uploadBackground(imageUri);
            }
        }

        @Override
        public void cropConfig(CropImage.ActivityBuilder builder) {
            super.cropConfig(builder);
            builder.setMultiTouchEnabled(false)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(crop_type)
                    .setAspectRatio(crop_w, crop_h);
        }

        @Override
        public void onPermissionDenied(int requestCode, String[] permissions, int[] grantResults) {
            super.onPermissionDenied(requestCode, permissions, grantResults);
            showTips("没有选择图片或拍照的权限");
        }
    };

    /**
     * 上传用户头像
     *
     * @param imageUri
     */
    private void uploadUserImage(Uri imageUri) {
        isUploadUserImage = true;
        String filename = ImageUtils.getImageAbsolutePath(mContext, imageUri);
        new UploadUserImg(mContext).execute(imageUrl, filename);
    }

    /**
     * 更改背景图
     *
     * @param imageUri
     */
    private void uploadBackground(Uri imageUri) {
        isUploadBackground = true;
        backgroundFilePath = ImageUtils.getImageAbsolutePath(mContext, imageUri);
        new UploadBackground(mContext).execute(updateBackgroundUrl, backgroundFilePath);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 分析用户数据
     *
     * @param s
     */
    private void analyzeUserInfo(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                JSONObject user = object.getJSONObject("student");
                userId = user.optLong("studentid", -1);
                name = user.getString("username");
                userImage =
//                        HttpUrlPre.FILE_URL +
                                user.getString("userimg");
                gradeId = user.optInt("gradeid", 110);
                backgroundFilePath = user.getString("surface");
                description = user.getString("description");
                fans = object.getString("followerNum");
                composition = object.getString("compositionNum");
                follow = object.getString("followingNum");
                followed = object.optInt("followed", 0);
                updateUserUi();
                if (mList.size() == 0) {
                    initCompositionData();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                showAccountBanned();
            } else {
                showErrorView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorView();
        }
    }

    /**
     * 更新用户视图
     */
    private void updateUserUi() {
        if (isDestroyed()) {
            return;
        }
        updateOperateUi();
        updateBackgroundUi();
        GlideUtils.loadUserImage(mContext, userImage, iv_user);
        tv_name.setText(name);
        tv_grade.setText(DataUtil.gradeCode2Chinese(gradeId));
        if (description.equals("") || description.equals("null")) {
            tv_description.setText("该用户暂无介绍");
        } else {
            tv_description.setText(description);
        }
        String fansInfo = fans + "粉丝";
        tv_fans.setText(fansInfo);
        String compositionInfo = composition + "文章";
        tv_composition.setText(compositionInfo);
        String followInfo = follow + "关注";
        tv_follow.setText(followInfo);
    }

    /**
     * 显示账号被封禁s
     */
    private void showAccountBanned() {
        if (isDestroyed()) {
            return;
        }
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
        tv_tips.setText("该账号已被封禁~");
        tv_reload.setVisibility(View.GONE);
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示错误视图
     */
    private void showErrorView() {
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        tv_tips.setText("获取数据失败，请稍后重试~");
        tv_reload.setText("重新加载");
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                frameLayout.removeAllViews();
                initData();
            }
        });
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        HomeRecommendationBean bean = new HomeRecommendationBean();
                        JSONObject object = array.getJSONObject(i);
                        bean.setType(0);
                        bean.setCompositionId(object.getString("compositionId"));
                        bean.setCompositionArea(0);
                        bean.setCompositionScore(String.valueOf(object.optInt("mark", 0)));
                        bean.setCompositionPrize(object.getString("prize"));
                        bean.setCompositionAvgScore(object.getString("avgScore"));
                        bean.setTitle(object.getString("article"));
                        bean.setContent(object.getString("content"));
                        bean.setImage(object.getString("cover"));
                        if (object.getString("saveTime").equals("")
                                || object.getString("saveTime").equals("null")) {
                            bean.setDate("2018-01-01");
                        } else {
                            bean.setDate(DateUtil.time2Format(object.getString("saveTime")));
                        }
                        bean.setViews(object.getString("pv"));
                        bean.setUserId(object.optLong("studentId", -1));
                        bean.setUserName("");
                        bean.setUserImage("");
                        bean.setUserGrade(DataUtil.gradeCode2Chinese(
                                object.optInt("gradeid", 111)));
                        mList.add(bean);
                    }
                    adapter.notifyDataSetChanged();
                    refreshing = false;
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 数据为空
     */
    private void emptyData() {
        refreshing = false;
        isEnd = true;
        if (mList.size() == 0) {
            tv_empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        refreshing = false;
        if (mList.size() == 0) {
            tv_empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 分析上传背景图数据
     *
     * @param s
     */
    private void analyzeBackgroundData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject data = jsonObject.getJSONObject("data");
                backgroundFilePath = data.getString("path");
                updateBackgroundUi();
                showTips("上传背景图成功");
            } else if (300 == jsonObject.optInt("status", -1)) {
                uploadBackgroundFailed("上传背景图太大，请调整后重试~");
            } else {
                uploadBackgroundFailed("上传背景图失败，请稍后重试~");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            uploadBackgroundFailed("上传背景图失败，请稍后重试~");
        }
    }

    /**
     * 更新背景图
     */
    private void updateBackgroundUi() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.image_placeholder_user_background)
                .error(R.drawable.image_placeholder_user_background);
        if (!isDestroyed()) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(backgroundFilePath)
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            iv_background.setImageResource(R.drawable.image_placeholder_user_background);
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
                            iv_background.setImageBitmap(resource);
                        }
                    });
        }
    }

    /**
     * 上传背景图失败
     */
    private void uploadBackgroundFailed(String tips) {
        showTips(tips);
    }

    /**
     * 分析关注用户数据
     *
     * @param s
     */
    private void analyzeFollowUserData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                if (followed == 1) {
                    followed = 0;
                } else {
                    followed = 1;
                }
                updateOperateUi();
            } else {
                errorFollowUser();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorFollowUser();
        }
    }

    /**
     * 更新操作视图的UI
     */
    private void updateOperateUi() {
        if (userId == NewMainActivity.STUDENT_ID && NewMainActivity.STUDENT_ID != -1) {
            iv_change_head.setVisibility(View.VISIBLE);
            iv_operate.setVisibility(View.GONE);
            tv_operate.setText("编辑资料");
            rl_camera.setVisibility(View.VISIBLE);
        } else {
            iv_change_head.setVisibility(View.GONE);
            rl_camera.setVisibility(View.INVISIBLE);
            if (followed == 1) {
                iv_operate.setVisibility(View.GONE);
                tv_operate.setText("已关注");
            } else {
                iv_operate.setVisibility(View.VISIBLE);
                tv_operate.setText("关注");
            }
        }
    }

    /**
     * 关注用户失败
     */
    private void errorFollowUser() {
        if (followed == 1) {
            showTips("取消关注失败，请稍后重试~");
        } else {
            showTips("关注失败，请稍后重试~");
        }
    }

    /**
     * 获取用户信息
     */
    private static class GetUserInfo
            extends WeakAsyncTask<String, Integer, String, UserHomepageActivity> {

        protected GetUserInfo(UserHomepageActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UserHomepageActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("viewerId", strings[2]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserHomepageActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.showErrorView();
            } else {
                activity.analyzeUserInfo(s);
            }
            activity.refreshing = false;
        }
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, UserHomepageActivity> {

        protected GetData(UserHomepageActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UserHomepageActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("pageNum", strings[2]);
                object.put("pageSize", 10);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserHomepageActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 上传背景图
     */
    private static class UploadBackground
            extends WeakAsyncTask<String, Integer, String, UserHomepageActivity> {

        protected UploadBackground(UserHomepageActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UserHomepageActivity activity, String[] strings) {
            try {
                String imagePath = strings[1];
                File image = new File(imagePath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("formData", strings[1],
                                RequestBody.create(MediaType.parse("image/*"), image))
                        .addFormDataPart("path", strings[1])
                        .addFormDataPart("fileType", "image")
                        .addFormDataPart("studentId", String.valueOf(NewMainActivity.STUDENT_ID))
                        .build();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient.Builder().build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserHomepageActivity activity, String s) {
            if (s == null) {
                activity.uploadBackgroundFailed("上传背景图失败，请检查您的网络状态是否连接");
            } else {
                activity.analyzeBackgroundData(s);
            }
            activity.isUploadBackground = false;
        }
    }

    /**
     * 关注用户
     */
    private static class FollowUser
            extends WeakAsyncTask<String, Void, String, UserHomepageActivity> {

        protected FollowUser(UserHomepageActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UserHomepageActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("followingId", strings[1]);
                object.put("followerId", strings[2]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserHomepageActivity activity, String s) {
            if (s == null) {
                activity.errorFollowUser();
            } else {
                activity.analyzeFollowUserData(s);
            }
        }
    }

    /**
     * 上传头像
     */
    private static class UploadUserImg
            extends WeakAsyncTask<String, Integer, String, UserHomepageActivity> {

        protected UploadUserImg(UserHomepageActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UserHomepageActivity activity, String[] strings) {
            try {
                String imagePath = strings[1];
                File image = new File(imagePath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("formData", strings[1],
                                RequestBody.create(MediaType.parse("image/*"), image))
                        .addFormDataPart("path", strings[1])
                        .addFormDataPart("fileType", "image")
                        .addFormDataPart("studentId", String.valueOf(NewMainActivity.STUDENT_ID))
                        .build();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient.Builder().build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserHomepageActivity activity, String s) {
            if (s == null) {
                activity.uploadImgFailed();
            } else {
                activity.analyzeUploadImg(s);
            }
            activity.isChooseUserHeadImg = false;
            activity.isUploadUserImage = false;
        }
    }

    /**
     * 分析上传头像返回的数据
     *
     * @param s
     */
    private void analyzeUploadImg(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject data = jsonObject.getJSONObject("data");
                boolean up_flag = data.getBoolean("up_flag");
                if (up_flag) {
                    String imageUrl = data.getString("path");
                    uploadImgSuccess(imageUrl);
                } else {
                    uploadImgFailed();
                }
            } else {
                uploadImgFailed();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            uploadImgFailed();
        }
    }

    /**
     * 更新头像成功
     */
    private void uploadImgSuccess(String imageUrl) {
        GlideUtils.loadUserImage(mContext,
//                HttpUrlPre.FILE_URL +
                        imageUrl, iv_user);
        NewMainActivity.USERIMG = imageUrl;
    }

    /**
     * 上传头像失败
     */
    private void uploadImgFailed() {
        showTips("上传头像失败，请稍后重试~");
    }

}
