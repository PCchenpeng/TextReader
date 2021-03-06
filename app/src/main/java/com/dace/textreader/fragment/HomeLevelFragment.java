package com.dace.textreader.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.dace.textreader.R;
import com.dace.textreader.activity.EventsActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.HomeLevelAdapter;
import com.dace.textreader.adapter.LevelFragmentRecyclerViewAdapter;
import com.dace.textreader.bean.LevelFragmentBean;
import com.dace.textreader.bean.ReaderLevelBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeLevelFragment extends BaseFragment implements PullListener {
    private View view;
    private PullRecyclerView mRecycleView;
    private FrameLayout framelayout;
    private View line_pupop_window;
    private String url = HttpUrlPre.HTTP_URL_ + "/select/reading/py/list";
    private String level_url = HttpUrlPre.HTTP_URL_ + "/select/article/level/list";
    private int pageNum = 1;
    private boolean isRefresh = true;
    private String grade = "-1";
    private HomeLevelAdapter homeLevelAdapter;
    private List<ReaderLevelBean.DataBean.ArticleListBean> mData = new ArrayList<>();
    private boolean isVisibleToUser = false;
    private List<LevelFragmentBean.DataBean> levelBeanList = new ArrayList<>();
    private PopupWindow popupWindow;
    private boolean popupWindowShowing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_reader_level, container, false);

        initView();
        loadLevelData();

        return view;
    }


    private void loadData() {
//        if (pageNum == 1) {
//            showLoadingView(framelayout);
//        }
        JSONObject params = new JSONObject();
        try {
            params.put("studentId",PreferencesUtil.getData(getContext(),"studentId","-1"));
            params.put("gradeId",PreferencesUtil.getData(getContext(),"gradeId","-1"));
            params.put("grade",grade);
            params.put("pageNum",String.valueOf(pageNum));
            params.put("width",DensityUtil.getScreenWidth(getContext()));
            params.put("height",DensityUtil.getScreenWidth(getContext())*2/3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {

                    @Override
                    public void onReqSuccess(Object result) {
                        framelayout.setVisibility(View.GONE);
                        Log.d("111","result.toString() " + result.toString());
                        ReaderLevelBean readerLevelBean = GsonUtil.GsonToBean(result.toString(),ReaderLevelBean.class);
                        if (readerLevelBean.getStatus() == 200) {
                            List<ReaderLevelBean.DataBean.ArticleListBean> data = readerLevelBean.getData().getArticleList();
                            if (isRefresh) {
//                            Toast.makeText(getContext(),"hahhaha",Toast.LENGTH_SHORT).show();
                                if (mData != null) {
                                    mData.clear();
                                    mData.addAll(data);
                                    mRecycleView.scrollToPosition(0);
                                }
                                mRecycleView.onPullComplete();
                            } else {
                                if (mData != null)
                                    mData.addAll(data);
                            }
                            homeLevelAdapter.setData(mData);

                        } else if (readerLevelBean != null && readerLevelBean.getStatus() == 400 && homeLevelAdapter.getmData().size() == 0) {//如果所有分页没有数据
                            showDefaultView(framelayout, R.drawable.image_state_empty, "暂无内容～", false, false, "", null);
                        } else {
                            if (pageNum == 1) {//如果是第一页
                                showNetFailView(framelayout, new OnButtonClick() {
                                    @Override
                                    public void onButtonClick() {
                                        framelayout.setVisibility(View.GONE);
                                        mRecycleView.onRefresh();
                                    }
                                });
                            } else {

                            }
                        }

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        showNetFailView(framelayout, new OnButtonClick() {
                            @Override
                            public void onButtonClick() {
                                framelayout.setVisibility(View.GONE);
                                mRecycleView.onRefresh();
                            }
                        });
                        mRecycleView.onPullComplete();
                    }
                });
    }

    private void initView() {

        NewHomeFragment newHomeFragment = (NewHomeFragment) getParentFragment();
        newHomeFragment.setOnTabLevelClickListener(new NewHomeFragment.OnTabLevelClickListener() {
            @Override
            public void onClick() {
                if(isVisibleToUser) {
                    if (!popupWindowShowing){
                        Log.d("111","111");
                            showLevelView(line_pupop_window);
                    } else {
                        Log.d("111","222");
                        popupWindow.dismiss();
                    }
                }
            }
        });
        mRecycleView = view.findViewById(R.id.rlv_reader_level);
        framelayout = view.findViewById(R.id.framelayout);
        line_pupop_window = view.findViewById(R.id.line_pupop_window);
        homeLevelAdapter = new HomeLevelAdapter(mData,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecycleView.setHeadRefreshView(new SimpleRefreshHeadView(getContext()))
                .setUseLoadMore(true)
                .setUseRefresh(true)
                .setPullLayoutManager(layoutManager)
                .setPullListener(this)
                .setPullItemAnimator(null)
                .build(homeLevelAdapter);

        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int childCount = recyclerView.getChildCount();
                    int itemCount = recyclerView.getLayoutManager().getItemCount();
                    int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (firstVisibleItem + childCount == (itemCount+1)) {
                        isRefresh = false;
                        pageNum++;
                        loadData();
                    }
                }
            }
        });
        setOnScrollListener(mRecycleView);
    }

    private void loadLevelData() {
        JSONObject params = new JSONObject();
//        try {
//            params.put("studentId","7826");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        OkHttpManager.getInstance(getContext()).requestAsyn(level_url, OkHttpManager.TYPE_GET, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        LevelFragmentBean readerLevelBean = GsonUtil.GsonToBean(result.toString(),LevelFragmentBean.class);
                        if (readerLevelBean.getStatus() == 200) {
                            List<LevelFragmentBean.DataBean> data = readerLevelBean.getData();
                            if (levelBeanList != null) {
                                levelBeanList.addAll(data);
                                if (levelBeanList.size() > 0) {
                                    grade = levelBeanList.get(0).getGrade() + "";
                                    pageNum = 1;
                                    loadData();
                                    updateLevelState(0);
                                }
                            }
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        showNetFailView(framelayout, new OnButtonClick() {
                            @Override
                            public void onButtonClick() {
                                framelayout.setVisibility(View.GONE);
                                mRecycleView.onRefresh();
                            }
                        });
                        mRecycleView.onPullComplete();
                    }
                });
    }



    /**
     * 在分级界面顶部下面
     */
    public void showLevelView(View view) {
        if (getContext() instanceof Activity && ((Activity) getContext()).isDestroyed()){
            return;
        }
        final View popupView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_level_choose_layout, null);
        LinearLayout ll_doubt = popupView.findViewById(R.id.ll_doubt_level_dialog);
        final RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view_level_dialog);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),
                3);
        recyclerView.setLayoutManager(gridLayoutManager);
        LevelFragmentRecyclerViewAdapter adapter =
                new LevelFragmentRecyclerViewAdapter(getContext(), levelBeanList);
        recyclerView.setAdapter(adapter);
        ll_doubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToDoubtView();
                popupWindow.dismiss();
            }
        });
        adapter.setOnItemClickListener(new LevelFragmentRecyclerViewAdapter.OnLevelItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (levelBeanList.size() > 0) {
                    if (pos != getLevelSelectedState()) {
                        updateLevelState(pos);
                        grade = levelBeanList.get(pos).getGrade() + "";
                        isRefresh = true;
                        pageNum = 1;
                        loadData();
                    }

                }
                popupWindow.dismiss();
            }
        });



//        PopupWindow popupWindow = new PopupWindow(DensityUtil.dip2px(getContext(), 130),DensityUtil.dip2px(getContext(), 80));
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(popupView);
        popupWindow.setOutsideTouchable(false);
        int offsetX = view.getWidth() / 2 - DensityUtil.dip2px(getContext(), 130) / 2;
        Log.d("111","popupWindow.isShowing() " + popupWindow.isShowing());
        popupWindow.showAsDropDown(view, offsetX, 0);
        popupWindowShowing = true;
        Animation animation=AnimationUtils.loadAnimation(getContext(), R.anim.level_enter_anim);
        ((NewMainActivity)getActivity()).view_cover.startAnimation(animation);
        ((NewMainActivity)getActivity()).view_cover.setVisibility(View.VISIBLE);
        ((NewMainActivity)getActivity()).view_cover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            // 在dismiss中恢复透明度
            public void onDismiss() {
                Log.d("111","333");
                popupWindowShowing = false;
                Animation animation=AnimationUtils.loadAnimation(getContext(), R.anim.level_exit_anim);
                ((NewMainActivity)getActivity()).view_cover.startAnimation(animation);
                ((NewMainActivity)getActivity()).view_cover.setVisibility(View.GONE);
            }
        });
//        this.popupWindow = popupWindow;
    }

    /**
     * 获取当前选择的等级
     */
    private int getLevelSelectedState() {
        int pos = -1;
        for (int i = 0; i < levelBeanList.size(); i++) {
            if (levelBeanList.get(i).isSelected()) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    /**
     * 更新等级选择视图
     *
     * @param pos
     */
    private void updateLevelState(int pos) {
        if (levelBeanList.size() != 0) {
            for (int i = 0; i < levelBeanList.size(); i++) {
                levelBeanList.get(i).setSelected(false);
            }
            levelBeanList.get(pos).setSelected(true);
        }
    }


    /**
     * 前往疑问解释视图
     */
    private void turnToDoubtView() {
        Intent intent = new Intent(getContext(), EventsActivity.class);
        intent.putExtra("pageName", "py_activity");
        startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            //相当于OnResume(),可以做相关逻辑
            this.isVisibleToUser = true;
        }else {
            //相当于OnPause()
            this.isVisibleToUser = false;
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        pageNum = 1;
        loadData();
    }

    @Override
    public void onLoadMore() {

    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }
}
