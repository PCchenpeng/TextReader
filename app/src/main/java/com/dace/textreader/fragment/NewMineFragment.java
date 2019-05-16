package com.dace.textreader.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.dace.textreader.activity.AbilityAnalysisActivity;
import com.dace.textreader.activity.BoughtLessonActivity;
import com.dace.textreader.activity.ContactActivity;
import com.dace.textreader.activity.ExcerptActivity;
import com.dace.textreader.activity.GlossaryActivity;
import com.dace.textreader.activity.InviteActivity;
import com.dace.textreader.activity.InviteCodeActivity;
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.MaterialListActivity;
import com.dace.textreader.activity.MemberCentreActivity;
import com.dace.textreader.activity.MyCompositionActivity;
import com.dace.textreader.activity.MyNoteListActivity;
import com.dace.textreader.activity.NewCollectionActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.NewSettingsActivity;
import com.dace.textreader.activity.NewWeekRankActivity;
import com.dace.textreader.activity.NewsActivity;
import com.dace.textreader.activity.NotesActivity;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.activity.WalletActivity;
import com.dace.textreader.activity.WritingWorkActivity;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.activity
 * Created by Administrator.
 * Created time 2018/3/2 0002 下午 5:50.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class NewMineFragment extends Fragment implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/system/message/notify";
    private static final String eventsUrl = HttpUrlPre.HTTP_URL + "/select/invite/friend/activity/detail";
    private static final String memberUrl = HttpUrlPre.HTTP_URL + "/card/multifunction/situation";

    private View view;

    private LinearLayout ll_root;
    private RelativeLayout rl_news_count;
    private TextView tv_news_count;

    private RelativeLayout rl_user_info;
    private TextView tv_user;
    private ImageView iv_user;
    private TextView tv_ability;

    private RelativeLayout rl_member_centre;
    private ImageView iv_member_centre;

    private RelativeLayout rl_wallet,rl_contact;
    private LinearLayout ll_writing;
    private LinearLayout ll_course;

    private LinearLayout ll_content;

    private LinearLayout ll_word;
    private LinearLayout ll_excerpt;
    private LinearLayout ll_idea;
    private LinearLayout ll_note;

    private LinearLayout ll_collection;
    private LinearLayout ll_weekRank;
    private LinearLayout ll_analysis;
    private RelativeLayout rl_teacher;
    private RelativeLayout rl_invite_code;
    private LinearLayout ll_settings;

    private String memberCardId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_mine_layout, container, false);

        initView();
        initEvents();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    private void initData() {
        updateUi();
        getMessageCount();
        new GetEventsData(this).execute(eventsUrl);
        new GetMemberData(this).execute(memberUrl, String.valueOf(NewMainActivity.STUDENT_ID));
    }

    public void getMessageCount() {
        new GetData(this).execute(url);
    }

    private void updateUi() {
        if (NewMainActivity.NEWS_COUNT == 0) {
            tv_news_count.setVisibility(View.GONE);
        } else {
            if (NewMainActivity.NEWS_COUNT > 99) {
                tv_news_count.setText("99+");
            } else {
                tv_news_count.setText(String.valueOf(NewMainActivity.NEWS_COUNT));
            }
            tv_news_count.setVisibility(View.VISIBLE);
        }
        GlideUtils.loadUserImage(getActivity(),
                HttpUrlPre.FILE_URL + NewMainActivity.USERIMG, iv_user);
        tv_user.setText(NewMainActivity.USERNAME);
        String ability;
        if (NewMainActivity.GRADE_ID == -1 || NewMainActivity.PY_SCORE.equals("") ||
                NewMainActivity.PY_SCORE.equals("null")) {
            ability = "";
        } else {
            ability = DataUtil.gradeCode2Chinese(NewMainActivity.GRADE_ID) + "    阅读能力" +
                    NewMainActivity.PY_SCORE + "PY";
        }
        tv_ability.setText(ability);
    }

    private void initEvents() {
        rl_news_count.setOnClickListener(this);
        rl_user_info.setOnClickListener(this);

        rl_member_centre.setOnClickListener(this);

        rl_wallet.setOnClickListener(this);
        ll_writing.setOnClickListener(this);
        ll_course.setOnClickListener(this);

        ll_word.setOnClickListener(this);
        ll_excerpt.setOnClickListener(this);
        ll_idea.setOnClickListener(this);
        ll_note.setOnClickListener(this);

        ll_collection.setOnClickListener(this);
        ll_weekRank.setOnClickListener(this);
        ll_analysis.setOnClickListener(this);
        rl_teacher.setOnClickListener(this);
        rl_invite_code.setOnClickListener(this);
        ll_settings.setOnClickListener(this);
        rl_contact.setOnClickListener(this);
    }

    private void initView() {
        ll_root = view.findViewById(R.id.ll_root_new_mine);
        rl_news_count = view.findViewById(R.id.rl_news_count_new_mine);
        tv_news_count = view.findViewById(R.id.tv_news_count_new_mine);

        rl_user_info = view.findViewById(R.id.rl_user_info_new_mine);
        iv_user = view.findViewById(R.id.iv_user_image_new_mine);
        tv_user = view.findViewById(R.id.tv_user_name_new_mine);
        tv_ability = view.findViewById(R.id.tv_user_ability_new_mine);

        rl_member_centre = view.findViewById(R.id.rl_member_centre_new_mine);
        iv_member_centre = view.findViewById(R.id.iv_member_centre_new_mine);

        rl_wallet = view.findViewById(R.id.rl_wallet);
        ll_writing = view.findViewById(R.id.ll_new_writing_new_mine);
        ll_course = view.findViewById(R.id.ll_new_course_new_mine);

        ll_content = view.findViewById(R.id.ll_content_new_mine);

        ll_word = view.findViewById(R.id.ll_new_words_new_mine);
        ll_excerpt = view.findViewById(R.id.ll_new_excerpt_new_mine);
        ll_idea = view.findViewById(R.id.ll_new_idea_new_mine);
        ll_note = view.findViewById(R.id.ll_new_note_new_mine);


        ll_collection = view.findViewById(R.id.ll_new_connection_new_mine);
        ll_weekRank = view.findViewById(R.id.ll_week_rank_new_mine);
        ll_analysis = view.findViewById(R.id.ll_new_analysis_new_mine);
        rl_teacher = view.findViewById(R.id.rl_teacher);
        rl_invite_code = view.findViewById(R.id.rl_invite_code);
        ll_settings = view.findViewById(R.id.ll_new_settings_new_mine);
        rl_contact = view.findViewById(R.id.rl_contact);

        if (getContext() == null) {
            return;
        }
        int width = DensityUtil.getScreenWidth(getContext());
        ViewGroup.LayoutParams layoutParams = iv_member_centre.getLayoutParams();
        int w_size = width * 345 / 375;
        int h_size = width * 70 / 375;
        layoutParams.width = w_size;
        layoutParams.height = h_size;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_news_count_new_mine:
                turnToNewsView();
                break;
            case R.id.rl_user_info_new_mine:
                turnToChangeUserInfo();
                break;
            case R.id.rl_member_centre_new_mine:
                turnToMemberCentre();
                break;
            case R.id.rl_wallet:
                turnToWalletView();
                break;
            case R.id.ll_new_writing_new_mine:
                turnToWritingList();
                break;
            case R.id.ll_new_course_new_mine:
                turnToCourseList();
                break;
            case R.id.ll_new_words_new_mine:
                turnToWordsList();
                break;
            case R.id.ll_new_excerpt_new_mine:
                turnToExcerptList();
                break;
            case R.id.ll_new_idea_new_mine:
                turnToIdeaList();
                break;
            case R.id.ll_new_note_new_mine:
                turnToNoteList();
                break;
            case R.id.ll_new_connection_new_mine:
                turnToCollectionList();
                break;
            case R.id.ll_week_rank_new_mine:
                turnToWeekRankView();
                break;
            case R.id.ll_new_analysis_new_mine:
                turnToAnalysisView();
                break;
            case R.id.rl_teacher:
                turnToBindTeacherView();
                break;
            case R.id.rl_invite_code:
                turnToInviteCodeView();
                break;
            case R.id.ll_new_settings_new_mine:
                turnToSettingsView();
                break;
            case R.id.rl_contact:
                startActivity(new Intent(getContext(), ContactActivity.class));
                break;
        }
    }

    /**
     * 前往会员中心
     */
    private void turnToMemberCentre() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            Intent intent = new Intent(getActivity(), MemberCentreActivity.class);
            intent.putExtra("id", memberCardId);
            intent.putExtra("code", "");
            startActivity(intent);
        }
    }

    /**
     * 前往新消息界面
     */
    private void turnToNewsView() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), NewsActivity.class));
        }
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    /**
     * 前往改变用户信息界面
     */
    private void turnToChangeUserInfo() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            Intent intent = new Intent(getContext(), UserHomepageActivity.class);
            intent.putExtra("userId", NewMainActivity.STUDENT_ID);
            startActivity(intent);
        }
    }

    /**
     * 前往钱包界面
     */
    private void turnToWalletView() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getContext(), WalletActivity.class));
        }
    }

    /**
     * 前往已购课程列表
     */
    private void turnToCourseList() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), BoughtLessonActivity.class));
        }
    }

    /**
     * 前往收藏列表
     */
    private void turnToCollectionList() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), NewCollectionActivity.class));
        }
    }

    /**
     * 前往生词列表
     */
    private void turnToWordsList() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), GlossaryActivity.class));
        }
    }

    /**
     * 前往摘抄列表
     */
    private void turnToExcerptList() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), ExcerptActivity.class));
        }
    }

    /**
     * 前往想法列表
     */
    private void turnToIdeaList() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            Intent intent_notes = new Intent(getActivity(), NotesActivity.class);
            intent_notes.putExtra("isAllNotes", true);
            startActivity(intent_notes);
        }
    }

    /**
     * 前往阅读排行榜页面
     */
    private void turnToWeekRankView() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), NewWeekRankActivity.class));
        }
    }

    /**
     * 前往阅读能力分析页面
     */
    private void turnToAnalysisView() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), AbilityAnalysisActivity.class));
        }
    }

    /**
     * 前往全文笔记列表
     */
    private void turnToNoteList() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            Intent intent = new Intent(getActivity(), MyNoteListActivity.class);
            intent.putExtra("isWriting", false);
            intent.putExtra("isCorrection", false);
            startActivity(intent);
        }
    }

    /**
     * 前往我的作文界面
     */
    private void turnToWritingList() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            Intent intent = new Intent(getActivity(), MyCompositionActivity.class);
            intent.putExtra("index", 0);
            startActivity(intent);
        }
    }

    /**
     * 跳转到绑定老师界面
     */
    private void turnToBindTeacherView() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), WritingWorkActivity.class));
        }
    }

    /**
     * 跳转到邀请码界面
     */
    private void turnToInviteCodeView() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getActivity(), InviteCodeActivity.class));
        }
    }

    /**
     * 前往设置页面
     */
    private void turnToSettingsView() {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            startActivity(new Intent(getContext(), NewSettingsActivity.class));
        }
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, NewMineFragment> {

        protected GetData(NewMineFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NewMineFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
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
        protected void onPostExecute(NewMineFragment fragment, String s) {
            if (s != null) {
                fragment.analyzeData(s);
            }
        }
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
                JSONObject object = jsonObject.getJSONObject("data");
                NewMainActivity.NEWS_COUNT = object.optInt("total", 0);
                if (NewMainActivity.NEWS_COUNT == 0) {
                    tv_news_count.setVisibility(View.GONE);
                } else {
                    if (NewMainActivity.NEWS_COUNT > 99) {
                        tv_news_count.setText("99+");
                    } else {
                        tv_news_count.setText(String.valueOf(NewMainActivity.NEWS_COUNT));
                    }
                    tv_news_count.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取活动数据
     */
    private static class GetEventsData
            extends WeakAsyncTask<String, Void, String, NewMineFragment> {

        protected GetEventsData(NewMineFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NewMineFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
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
        protected void onPostExecute(NewMineFragment fragment, String s) {
            if (s != null) {
                fragment.analyzeEventsData(s);
            }
        }
    }

    /**
     * 分析活动数据
     *
     * @param s
     */
    private void analyzeEventsData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                int status = object.optInt("status", -1);
                if (status == 1) {
                    final String id = object.getString("id");
                    final String title = object.getString("title");
                    final String content = object.getString("content");
                    String image = object.getString("indexImage");

                    ImageView imageView = new ImageView(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (getContext() != null && getActivity() != null) {
                        Glide.with(getContext()).load(image).into(imageView);
                    }
                    imageView.setLayoutParams(layoutParams);
                    ll_content.removeAllViews();
                    ll_content.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), InviteActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("title", title);
                            intent.putExtra("content", content);
                            intent.putExtra("url", "");
                            startActivity(intent);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取会员数据
     */
    private static class GetMemberData
            extends WeakAsyncTask<String, Void, String, NewMineFragment> {

        protected GetMemberData(NewMineFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NewMineFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
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
        protected void onPostExecute(NewMineFragment fragment, String s) {
            if (s != null) {
                fragment.analyzeMembersData(s);
            }
        }
    }

    /**
     * 分析会员数据结果
     *
     * @param s
     */
    private void analyzeMembersData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    rl_member_centre.setVisibility(View.GONE);
                    return;
                }
                JSONObject object = array.getJSONObject(0);
                memberCardId = object.getString("cardId");
                String tips = object.getString("tips");
                JSONObject object_tips = new JSONObject(tips);
                String imagePath = object_tips.getString("entranceImg");
                loadMemberImage(imagePath);
            } else if (400 == jsonObject.optInt("status", -1)) {
                rl_member_centre.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载会员图片
     *
     * @param imagePath
     */
    private void loadMemberImage(String imagePath) {
        if (getContext() == null) {
            return;
        }
        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(getContext())
                .asBitmap()
                .load(imagePath)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        rl_member_centre.setVisibility(View.GONE);
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
                        iv_member_centre.setImageBitmap(resource);
                        rl_member_centre.setVisibility(View.VISIBLE);
                    }
                });
    }

}
