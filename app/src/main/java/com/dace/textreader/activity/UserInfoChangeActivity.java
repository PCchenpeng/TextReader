package com.dace.textreader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideCircleTransform;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 修改资料
 */
public class UserInfoChangeActivity extends BaseActivity {

    //上传头像
    private static final String imageUrl = HttpUrlPre.UPLOAD_URL + "/file/synchronize/uploadFile";
    //修改用户名
    private static final String updateUserNameUrl = HttpUrlPre.HTTP_URL + "/me/nickName?";
    //更换背景图
    private static final String updateBackgroundUrl = HttpUrlPre.HTTP_URL + "/upload/user/surface";

    private RelativeLayout rl_back;
    private TextView tv_title;

    private RelativeLayout rl_head;
    private ImageView iv_head;

    private RelativeLayout rl_background;

    private RelativeLayout rl_name;
    private TextView tv_name;

    private RelativeLayout rl_introduction;
    private TextView tv_introduction;

    private RelativeLayout rl_grade;
    private TextView tv_grade;

    private LinearLayout ll_loading;

    private UserInfoChangeActivity mContext;

    //用户头像文件路径
    private String filename = "";
    RequestOptions options;
    //背景图文件路径
    private String backgroundFilePath = "";

    private ImagePicker imagePicker;//图片选择框架

    private boolean isChooseUserHeadImg = true;  //是否是选择用户头像
    private CropImageView.CropShape crop_type;  //裁剪类型
    private int crop_w;  //裁剪宽高比
    private int crop_h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_change);

        mContext = this;

        initData();
        initView();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlideUtils.loadUserImage(mContext,
                HttpUrlPre.FILE_URL + NewMainActivity.USERIMG, iv_head);
        tv_name.setText(NewMainActivity.USERNAME);
        tv_grade.setText(DataUtil.gradeCode2Chinese(NewMainActivity.GRADE_ID));
        if (!NewMainActivity.DESCRIPTION.equals("") && !NewMainActivity.DESCRIPTION.equals("null")) {
            tv_introduction.setText(NewMainActivity.DESCRIPTION);
        }
    }

    private void initData() {
        options = new RequestOptions()
                .placeholder(R.drawable.image_student)
                .transform(new GlideCircleTransform(this))
                .error(R.drawable.image_student);
        filename = getIntent().getStringExtra("filename");
        imagePicker = new ImagePicker();
    }

    private void initEvents() {
        rl_back.setOnClickListener(onClickListener);
        rl_head.setOnClickListener(onClickListener);
        rl_background.setOnClickListener(onClickListener);
        rl_name.setOnClickListener(onClickListener);
        rl_introduction.setOnClickListener(onClickListener);
        rl_grade.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_page_back_top_layout:
                    finish();
                    break;
                case R.id.rl_change_head:
                    updateUserHead();
                    break;
                case R.id.rl_change_name:
                    updateUserName();
                    break;
                case R.id.rl_change_background:
                    updateBackground();
                    break;
                case R.id.rl_change_introduction:
                    turnToUpdateIntroduction();
                    break;
                case R.id.rl_change_grade:
                    turnToChangeGrade();
                    break;
            }
        }
    };

    /**
     * 前往更改年级
     */
    private void turnToChangeGrade() {
        Intent intent = new Intent(mContext, UpdateGradeActivity.class);
        startActivity(intent);
    }

    /**
     * 前往更改简介
     */
    private void turnToUpdateIntroduction() {
        Intent intent = new Intent(mContext, UpdateIntroductionActivity.class);
        startActivity(intent);
    }

    /**
     * 更改背景
     */
    private void updateBackground() {
        isChooseUserHeadImg = false;
        crop_type = CropImageView.CropShape.RECTANGLE;
        crop_w = 16;
        crop_h = 9;
        // 设置标题
        imagePicker.setTitle("选择图片");
        // 设置是否裁剪图片
        imagePicker.setCropImage(true);
        imagePicker.startChooser(mContext, imagePickerCallback);
    }

    //更换用户头像
    private void updateUserHead() {
        getImagePath();
    }

    //获取头像路径
    private void getImagePath() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_update_user_image_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_file = holder.getView(R.id.tv_file_update_user_image);
                        TextView tv_camera = holder.getView(R.id.tv_camera_update_user_image);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_update_user_image);
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
     * 前往拍照
     */
    private void toCamera() {
        isChooseUserHeadImg = true;
        crop_type = CropImageView.CropShape.OVAL;
        crop_w = 1;
        crop_h = 1;
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
        isChooseUserHeadImg = true;
        crop_type = CropImageView.CropShape.OVAL;
        crop_w = 1;
        crop_h = 1;
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
     * 更改背景图
     *
     * @param imageUri
     */
    private void uploadBackground(Uri imageUri) {
        backgroundFilePath = ImageUtils.getImageAbsolutePath(mContext, imageUri);
        new UploadBackground(mContext).execute(updateBackgroundUrl, backgroundFilePath);
    }

    /**
     * 上传用户头像
     *
     * @param imageUri
     */
    private void uploadUserImage(Uri imageUri) {
        filename = ImageUtils.getImageAbsolutePath(mContext, imageUri);
        if (!isDestroyed()) {
            Glide.with(mContext)
                    .load(new File(filename))
                    .apply(options)
                    .into(iv_head);
        }
        new UploadUserImg(mContext).execute(imageUrl, filename);
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
     * 上传头像
     */
    private static class UploadUserImg
            extends WeakAsyncTask<String, Integer, String, UserInfoChangeActivity> {

        protected UploadUserImg(UserInfoChangeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UserInfoChangeActivity activity, String[] strings) {
            try {
                String imagePath = strings[1];
                File image = new File(imagePath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("formData", activity.filename,
                                RequestBody.create(MediaType.parse("image/*"), image))
                        .addFormDataPart("path", activity.filename)
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
        protected void onPostExecute(UserInfoChangeActivity activity, String s) {
            if (s == null) {
                activity.uploadImgFailed();
            } else {
                activity.analyzeUploadImg(s);
            }
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
        GlideUtils.loadUserImage(mContext, imageUrl, iv_head);
        NewMainActivity.USERIMG = imageUrl;
        ll_loading.setVisibility(View.INVISIBLE);
    }

    /**
     * 上传头像失败
     */
    private void uploadImgFailed() {
        GlideUtils.loadUserImage(mContext,
                HttpUrlPre.FILE_URL + NewMainActivity.USERIMG, iv_head);
        MyToastUtil.showToast(mContext, "上传头像失败，请稍后重试！");
        ll_loading.setVisibility(View.INVISIBLE);
    }

    //更换用户昵称
    private void updateUserName() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_name_layout, null);
        final EditText editText = view.findViewById(R.id.et_mine_dialog_name);
        final TextView tv_number = view.findViewById(R.id.tv_mine_username_number);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_number.setText("" + (12 - editText.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setText(NewMainActivity.USERNAME);
        editText.setSelection(editText.getText().length());
        dialog.setView(view);
        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(str)) {
                    NewMainActivity.USERNAME = editText.getText().toString();
                    tv_name.setText(NewMainActivity.USERNAME);
                    new UpdateUserName().execute(updateUserNameUrl + "studentId=" + NewMainActivity.STUDENT_ID
                            + "&nickName=" + NewMainActivity.USERNAME);
                } else {
                    MyToastUtil.showToast(UserInfoChangeActivity.this, "昵称不能为空");
                }
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    //更新用户名字
    private class UpdateUserName extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            //获取数据之前
        }

        @Override
        protected String doInBackground(String... params) {
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //获取数据之后
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("个人资料");

        rl_head = findViewById(R.id.rl_change_head);
        iv_head = findViewById(R.id.iv_change_head);
        ll_loading = findViewById(R.id.ll_loading_head);

        rl_background = findViewById(R.id.rl_change_background);

        rl_name = findViewById(R.id.rl_change_name);
        tv_name = findViewById(R.id.tv_change_name);

        rl_introduction = findViewById(R.id.rl_change_introduction);
        tv_introduction = findViewById(R.id.tv_change_introduction);

        rl_grade = findViewById(R.id.rl_change_grade);
        tv_grade = findViewById(R.id.tv_change_grade);

    }

    /**
     * 上传背景图
     */
    private static class UploadBackground
            extends WeakAsyncTask<String, Integer, String, UserInfoChangeActivity> {

        protected UploadBackground(UserInfoChangeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UserInfoChangeActivity activity, String[] strings) {
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
        protected void onPostExecute(UserInfoChangeActivity activity, String s) {
            if (s == null) {
                activity.uploadBackgroundFailed("上传背景图失败，请检查您的网络状态是否连接");
            } else {
                activity.analyzeBackgroundData(s);
            }
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
                String path = data.getString("path");
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
     * 上传背景图失败
     */
    private void uploadBackgroundFailed(String tips) {
        showTips(tips);
    }

    /**
     * 显示吐丝
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

}
