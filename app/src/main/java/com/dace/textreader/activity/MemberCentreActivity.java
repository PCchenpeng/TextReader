package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
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
import com.dace.textreader.adapter.MemberCentreAdapter;
import com.dace.textreader.bean.MemberCardBean;
import com.dace.textreader.bean.MemberCardRecordBean;
import com.dace.textreader.bean.PayResult;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.TipsUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 会员中心
 */
public class MemberCentreActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/card/vip";
    private static final String priceUrl = HttpUrlPre.HTTP_URL + "/card/vip/code/identify";
    private static final String wxUrl = HttpUrlPre.HTTP_URL + "/card/vip/purchase/wxpay";
    private static final String aliUrl = HttpUrlPre.HTTP_URL + "/card/vip/purchase/alipay";
    private static final String liveShowUrl = HttpUrlPre.HTTP_URL + "/card/broadcast/live/query";
    private static final String answerUrl = HttpUrlPre.HTTP_URL + "/card/QA/live/query";

    private static final int REQUEST_CODE_LIVE_SHOW_LESSON = 0;
    private static final int REQUEST_CODE_LOGIN = 1;

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private RelativeLayout rl_card;
    private TextView tv_activated;
    private ImageView iv_card;
    private TextView tv_card;
    private TextView tv_card_valid;
    private LinearLayout ll_code;
    private EditText et_code;
    private ImageView iv_clear;
    private TextView tv_code;
    private TextView tv_welfare;
    private TextView tv_welfare_detail;
    private RecyclerView recyclerView;
    private LinearLayout ll_price;
    private TextView tv_original_price;
    private TextView tv_price;

    private MemberCentreActivity mContext;

    private String cardId = "";  //卡ID
    private String code = "";  //优惠码

    private boolean activated;
    private boolean isDiscount;
    private String card_image;
    private String card_name;
    private String card_valid;
    private String card_welfare;
    private String card_welfare_detail;
    private String card_welfare_url;
    private double card_post_price;
    private double card_original_price;
    private double card_price;
    private List<MemberCardBean> mList = new ArrayList<>();
    private MemberCentreAdapter adapter;

    private int pay_way = 0;  //支付方式，默认为0微信，1为支付宝
    private boolean isTurnToWx = false;

    private String timeStamp;
    private String out_trade_no;
    private String paySign;
    private String nonceStr;
    private String prepay_id;

    private int mPosition = -1;  //当前选择的前往选择微课的item的索引

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_centre);

        mContext = this;

        cardId = getIntent().getStringExtra("id");
        code = getIntent().getStringExtra("code");

        initView();
        initData();
        initEvents();

        setNeedCheckCode(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTurnToWx) {
            isTurnToWx = false;
            checkResult();
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (string.length() == 0) {
                    iv_clear.setVisibility(View.GONE);
                } else {
                    iv_clear.setVisibility(View.VISIBLE);
                }
                if (string.contains("studyCard?")
                        && string.contains("agentCode=")
                        && string.contains("cardId=")) {
                    string = string.substring(20, string.indexOf("cardId=") - 1);
                    et_code.setText(string);
                    et_code.setSelection(string.length());
                }
            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_code.setText("");
            }
        });
        tv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    useCode();
                }
            }
        });
        tv_welfare_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventsActivity.class);
                intent.putExtra("pageName", card_welfare_url);
                startActivity(intent);
            }
        });
        ll_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    showPayWayDialog();
                }
            }
        });
        adapter.setOnItemClickListen(new MemberCentreAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (pos < 0 || pos >= mList.size()) {
                    return;
                }
                int type = mList.get(pos).getCardType();
                if (type == -1) {
                    return;
                }
                long cardId = mList.get(pos).getCardId();
                long cardRecordId = mList.get(pos).getCardRecordId();
                if (activated) {
                    mPosition = pos;
                    if (type == 1) {
                        showCorrectionDialog();
                    } else if (type == 2) {
                        turnToMicro(cardId);
                    } else if (type == 3) {
                        turnToLiveShowLessonChoose(cardId, cardRecordId);
                    } else if (type == 5) {
                        requestAnswerData(cardId);
                    } else {
                        showTips("功能正在开发中~");
                    }
                } else {
                    if (type == 2) {
                        turnToMicro(cardId);
                    } else if (type == 3) {
                        turnToLiveShowLessonChoose(cardId, cardRecordId);
                    }
                }
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    /**
     * 显示作文批改对话框
     */
    private void showCorrectionDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_correction_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout ll_write = holder.getView(R.id.ll_write_composition_correction_dialog);
                        LinearLayout ll_choose = holder.getView(R.id.ll_choose_composition_correction_dialog);
                        RelativeLayout rl_close = holder.getView(R.id.rl_close_writing_correction_dialog);

                        ll_write.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(mContext, WritingActivity.class);
                                intent.putExtra("id", "");
                                intent.putExtra("taskId", "");
                                intent.putExtra("area", 5);
                                intent.putExtra("type", 5);
                                intent.putExtra("isCorrection", true);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        ll_choose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, MyCompositionActivity.class);
                                intent.putExtra("index", 0);
                                intent.putExtra("isCorrection", true);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        rl_close.setOnClickListener(new View.OnClickListener() {
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
     * 显示微课列表
     */
    private void turnToMicro(long cardId) {
        Intent intent = new Intent(mContext, BoughtLessonActivity.class);
        intent.putExtra("isMemberContent", true);
        intent.putExtra("cardId", cardId);
        intent.putExtra("activated", activated);
        startActivity(intent);
    }

    /**
     * 使用优惠码
     */
    private void useCode() {
        String text = et_code.getText().toString();
        if (text.trim().isEmpty()) {
            showTips("请输入优惠码");
            return;
        }
        code = text;
        et_code.setText("");
        hideInputMethod();
        getPrice();
    }

    /**
     * 获取优惠价格
     */
    private void getPrice() {
        showLoadingView(true);
        new GetPriceData(mContext).execute(priceUrl,
                String.valueOf(NewMainActivity.STUDENT_ID), cardId, code);
    }

    /**
     * 隐藏软键盘
     */
    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) et_code.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(et_code.getApplicationWindowToken(), 0);
        }
    }

    private void initData() {
        showLoadingView(true);
        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID), cardId, code);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("学霸之路");

        frameLayout = findViewById(R.id.frame_member_centre);
        rl_card = findViewById(R.id.rl_card_member_centre);
        tv_activated = findViewById(R.id.tv_activated_member_centre);
        iv_card = findViewById(R.id.iv_card_member_centre);
        tv_card = findViewById(R.id.tv_card_name_member_centre);
        tv_card_valid = findViewById(R.id.tv_card_valid_member_centre);
        ll_code = findViewById(R.id.ll_code_member_centre);
        et_code = findViewById(R.id.et_code_member_centre);
        iv_clear = findViewById(R.id.iv_clear_member_centre);
        tv_code = findViewById(R.id.tv_code_member_centre);
        tv_welfare = findViewById(R.id.tv_welfare_member_centre);
        tv_welfare_detail = findViewById(R.id.tv_welfare_detail_member_centre);
        recyclerView = findViewById(R.id.recycler_view_member_centre);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MemberCentreAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        ll_price = findViewById(R.id.ll_price_member_centre);
        tv_original_price = findViewById(R.id.tv_original_price_member_centre);
        tv_price = findViewById(R.id.tv_price_member_centre);
        tv_original_price.setPaintFlags(Paint.ANTI_ALIAS_FLAG);

        if (!code.equals("")) {
            et_code.setText(code);
            et_code.setSelection(code.length());
        }

        int width = DensityUtil.getScreenWidth(mContext);
        int height = width * 364 / 750;
        ViewGroup.LayoutParams layoutParams = iv_card.getLayoutParams();
        layoutParams.height = height;

        int size = width * 30 / 375;
        int top_size = width * 28 / 375;
        RelativeLayout.LayoutParams layoutParams_parent = (RelativeLayout.LayoutParams)
                rl_card.getLayoutParams();
        layoutParams_parent.leftMargin = size;
        layoutParams_parent.topMargin = top_size;
        layoutParams_parent.rightMargin = size;
    }

    /**
     * 选择支付方式
     */
    private void showPayWayDialog() {
        pay_way = 0;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_pay_way_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout ll_wechat = holder.getView(R.id.ll_wechat_pay_way_dialog);
                        final ImageView iv_wechat = holder.getView(R.id.iv_wechat_pay_way_dialog);
                        LinearLayout ll_ali = holder.getView(R.id.ll_ali_pay_way_dialog);
                        final ImageView iv_ali = holder.getView(R.id.iv_ali_pay_way_dialog);
                        LinearLayout ll_pay = holder.getView(R.id.ll_price_pay_way_dialog);
                        TextView tv_o_price_dialog = holder.getView(R.id.tv_original_price_pay_way_dialog);
                        TextView tv_price_dialog = holder.getView(R.id.tv_price_pay_way_dialog);

                        if (isDiscount) {
                            String str1 = "原价：" + DataUtil.double2String(card_original_price) + "元";
                            tv_o_price_dialog.setText(str1);
                            tv_o_price_dialog.setVisibility(View.VISIBLE);
                            String str2 = "优惠支付" + DataUtil.double2String(card_price) + "元";
                            tv_price_dialog.setText(str2);
                        } else {
                            String string = "确认支付" + DataUtil.double2String(card_original_price) + "元";
                            tv_o_price_dialog.setVisibility(View.GONE);
                            tv_price_dialog.setText(string);
                        }


                        ll_wechat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pay_way != 0) {
                                    pay_way = 0;
                                    iv_ali.setImageResource(R.drawable.icon_edit_unselected);
                                    iv_wechat.setImageResource(R.drawable.icon_edit_selected);
                                }
                            }
                        });
                        ll_ali.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pay_way != 1) {
                                    pay_way = 1;
                                    iv_wechat.setImageResource(R.drawable.icon_edit_unselected);
                                    iv_ali.setImageResource(R.drawable.icon_edit_selected);
                                }
                            }
                        });
                        ll_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NewMainActivity.STUDENT_ID == -1) {
                                    turnToLogin();
                                } else {
                                    if (pay_way == 0) {
                                        getWechatPayInfo();
                                    } else if (pay_way == 1) {
                                        getAliPayInfo();
                                    }
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 获取支付宝支付信息
     */
    private void getAliPayInfo() {
        showLoadingView(true);
        new AliUnifiedOrder(mContext).execute(aliUrl, code,
                String.valueOf(NewMainActivity.STUDENT_ID), cardId);
    }

    private long fucCardId = 0;
    private long fucCardRecordId = 0;

    /**
     * 前往选择课程
     *
     * @param cardId
     * @param cardRecordId
     */
    private void turnToLiveShowLessonChoose(long cardId, long cardRecordId) {
        fucCardId = cardId;
        fucCardRecordId = cardRecordId;
        Intent intent = new Intent(mContext, LiveShowLessonChooseActivity.class);
        intent.putExtra("id", cardId);
        intent.putExtra("activated", activated);
        intent.putExtra("recordId", cardRecordId);
        startActivity(intent);
    }

    /**
     * 获取直播功能内容
     */
    private void requestLiveShowData(String lessonId) {
        showTips("正在获取直播内容");
        new GetLiveShowData(mContext).execute(liveShowUrl,
                String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(fucCardId),
                String.valueOf(fucCardRecordId), lessonId);
    }

    /**
     * 请求答疑数据
     *
     * @param cardId
     */
    private void requestAnswerData(long cardId) {
        showTips("正在获取答疑内容");
        new GetAnswerData(mContext).execute(answerUrl, String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(cardId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN) {
            initData();
        }
    }

    /**
     * 显示前往微信公众号对话框
     *
     * @param type    0为直播，1为答疑
     * @param code
     * @param content
     */
    private void showWxPublicNumberDialog(final int type, final String code, final String content) {
        final int dp_size = DensityUtil.px2dip(mContext, DensityUtil.getScreenWidth(mContext));
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_wx_public_number_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        RelativeLayout rl_content = holder.getView(R.id.rl_content_wx_public_number_dialog);
                        int padding_content_top = dp_size * 36 / 375;
                        rl_content.setPadding(0, DensityUtil.dip2px(mContext, padding_content_top), 0, 0);
                        ViewGroup.LayoutParams layoutParams_content = rl_content.getLayoutParams();
                        layoutParams_content.width = DensityUtil.dip2px(mContext, dp_size);
                        layoutParams_content.height = DensityUtil.dip2px(mContext, dp_size + padding_content_top);

                        RelativeLayout rl_title = holder.getView(R.id.rl_title_wx_public_number_dialog);
                        ViewGroup.LayoutParams layoutParams_title = rl_title.getLayoutParams();
                        int width_title = dp_size * 213 / 375;
                        int height_title = dp_size * 81 / 375;
                        layoutParams_title.width = DensityUtil.dip2px(mContext, width_title);
                        layoutParams_title.height = DensityUtil.dip2px(mContext, height_title);

                        TextView tv_title = holder.getView(R.id.tv_title_wx_public_number_dialog);
                        int padding_left = dp_size * 74 / 375;
                        tv_title.setPadding(DensityUtil.dip2px(mContext, padding_left), 0, 0, 0);

                        ScrollView ll_code = holder.getView(R.id.ll_code_wx_public_number_dialog);
                        int padding_top = dp_size * 64 / 375;
                        int padding_bottom = dp_size * 64 / 375;
                        ll_code.setPadding(0, DensityUtil.dip2px(mContext, padding_top),
                                0, DensityUtil.dip2px(mContext, padding_bottom));

                        TextView tv_code = holder.getView(R.id.tv_code_wx_public_number_dialog);
                        TextView tv_copy = holder.getView(R.id.tv_copy_wx_public_number_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_wx_public_number_dialog);
                        ImageView iv_close = holder.getView(R.id.iv_close_wx_public_number_dialog);

                        String title;
                        String code_text;
                        String copy;
                        if (type == 0) {
                            title = "直播课程";
                            code_text = "直播课程兑换码为：" + code;
                            copy = "复制兑换码";
                        } else {
                            title = "一对一答疑";
                            code_text = "答疑兑换码为：" + code;
                            copy = "复制兑换码";
                        }
                        tv_title.setText(title);
                        tv_code.setText(code_text);
                        tv_copy.setText(copy);
                        tv_content.setText(content);

                        tv_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataUtil.copyContent(mContext, code);
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
                .setWidth(dp_size)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 获取微信支付信息
     */
    private void getWechatPayInfo() {
        showLoadingView(true);
        new WXUnifiedOrder(mContext).execute(wxUrl, code,
                String.valueOf(NewMainActivity.STUDENT_ID), cardId, "APP", "");
    }

    /**
     * 显示正在加载
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_author_loading, null);
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
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                activated = object.optBoolean("actived", false);
                isDiscount = object.optBoolean("discount", false);
                JSONObject card = object.getJSONObject("card");
                card_image = card.getString("img");
                card_name = card.getString("title");
                card_post_price = card.optDouble("postPrice", 0);
                card_original_price = card.optDouble("price", 0);
                card_price = card.optDouble("discountPrice", 0);
                String tips = card.getString("tips");
                JSONObject object_tip = new JSONObject(tips);
                card_welfare = object_tip.getString("displayBanner");
                card_welfare_detail = object_tip.getString("bannerGuide");
                card_welfare_url = object_tip.getString("guideUrl");

                JSONArray array = object.getJSONArray("functions");
                mList.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject fuc = array.getJSONObject(i);
                    MemberCardBean memberCardBean = new MemberCardBean();
                    memberCardBean.setCardId(fuc.optLong("id", -1));
                    memberCardBean.setCardImage(fuc.getString("img"));
                    memberCardBean.setCount(fuc.optInt("times", -1));
                    memberCardBean.setCardName(fuc.getString("name"));
                    memberCardBean.setCardDescription(fuc.getString("description"));
                    memberCardBean.setCardType(fuc.optInt("category", -1));
                    mList.add(memberCardBean);
                }

                if (activated) {
                    JSONObject card_detail = object.getJSONObject("cardRecord");
                    card_valid = DateUtil.timedate(card_detail.getString("stopTime"));
                    List<MemberCardRecordBean> list = new ArrayList<>();
                    JSONArray array_record = object.getJSONArray("functionRecord");
                    for (int i = 0; i < array_record.length(); i++) {
                        MemberCardRecordBean memberCardRecordBean = new MemberCardRecordBean();
                        JSONObject object_record = array_record.getJSONObject(i);
                        memberCardRecordBean.setCardId(object_record.optLong("cardId", 0));
                        memberCardRecordBean.setCount(object_record.optInt("times", -1));
                        memberCardRecordBean.setRecordId(object_record.optLong("id", -1));
                        list.add(memberCardRecordBean);
                    }
                    updateData(list);
                }

                updateUi();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 更新激活后的数据
     *
     * @param list
     */
    private void updateData(List<MemberCardRecordBean> list) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < mList.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                MemberCardRecordBean recordBean = list.get(j);
                if (mList.get(i).getCardId() == recordBean.getCardId()) {
                    mList.get(i).setCount(recordBean.getCount());
                    mList.get(i).setCardRecordId(recordBean.getRecordId());
                }
            }
        }
    }

    /**
     * 更新界面
     */
    private void updateUi() {
        if (activated) {
            tv_activated.setVisibility(View.GONE);
            ll_code.setVisibility(View.GONE);
            ll_price.setVisibility(View.GONE);
            String valid = "有效期至：" + card_valid;
            tv_card_valid.setText(valid);
        } else {
            tv_activated.setVisibility(View.VISIBLE);
            if (isDiscount) {
                ll_code.setVisibility(View.GONE);
            } else {
                ll_code.setVisibility(View.VISIBLE);
            }
            ll_price.setVisibility(View.VISIBLE);
        }
        GlideUtils.loadImage(mContext, card_image, iv_card);

        if (isDestroyed()) {
            return;
        }
        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(mContext)
                .asBitmap()
                .load(card_image)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        iv_card.setImageResource(R.drawable.image_placeholder_rectangle);
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
                        iv_card.setImageBitmap(resource);
                    }
                });

        tv_card.setText(card_name);
        tv_welfare.setText(card_welfare);
        tv_welfare_detail.setText(card_welfare_detail);
        adapter.setActivated(activated);
        adapter.notifyDataSetChanged();
        updatePrice();
        hideInputMethod();
    }

    /**
     * 更新价格
     */
    private void updatePrice() {
        if (isDiscount) {
            String str1 = "原价：" + DataUtil.double2String(card_original_price) + "元";
            tv_original_price.setText(str1);
            tv_original_price.setVisibility(View.VISIBLE);
            String str2 = "优惠支付" + DataUtil.double2String(card_price) + "元";
            tv_price.setText(str2);
        } else {
            String post = " 原价" + DataUtil.double2String(card_post_price) + " ";
            String string = post + "    限时特惠¥" + DataUtil.double2String(card_original_price)
                    + "立即开通";
            tv_original_price.setVisibility(View.GONE);
            SpannableString ss = new SpannableString(string);
            ss.setSpan(new RelativeSizeSpan(0.9f), 0, post.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new StrikethroughSpan(), 0, post.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new StyleSpan(Typeface.NORMAL), 0, post.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_price.setText(ss);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (isDestroyed()) {
            return;
        }
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        tv_tips.setText("获取数据失败，请稍后重试~");
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
     * 分析价格数据
     *
     * @param s
     */
    private void analyzePriceData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                card_original_price = object.optDouble("price", 0);
                card_price = object.optDouble("discountPrice", 0);
                isDiscount = object.optBoolean("discount", false);
                updatePrice();
                showTips("兑换成功");
                ll_code.setVisibility(View.GONE);
                showPayWayDialog();
            } else {
                errorPrice("序列码无效");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorPrice("获取优惠失败，请稍后重试~");
        }
    }

    /**
     * 获取价格失败
     */
    private void errorPrice(String errorTips) {
        code = "";
        showTips(errorTips);
    }

    /**
     * 分析微信统一下单数据
     *
     * @param s
     */
    private void analyzeWXUnifiedOrderData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject data = jsonObject.getJSONObject("data");
                timeStamp = data.getString("timeStamp");
                out_trade_no = data.getString("out_trade_no");
                paySign = data.getString("paySign");
                nonceStr = data.getString("nonceStr");
                prepay_id = data.getString("prepay_id");
                wxRecharge();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 调起微信支付
     */
    private void wxRecharge() {
        isTurnToWx = true;
        PayReq req = new PayReq();
        req.appId = App.APP_ID;
        req.partnerId = App.WX_MCH_ID;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.sign = paySign;
        App.api.sendReq(req);
    }

    /**
     * 支付宝支付统一下单数据
     *
     * @param s
     */
    private void analyzeAliUnifiedOrderData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                String result = object.getString("result");
                out_trade_no = object.getString("orderNum");
                aliPay(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 调起支付宝支付
     */
    private void aliPay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(mContext);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        checkResult();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        errorPay();
                    }
                    break;
                }
            }
        }
    };

    /**
     * 支付失败
     */
    private void errorPay() {
        showLoadingView(false);
        TipsUtil tipsUtil = new TipsUtil(mContext);
        tipsUtil.showPayFailedView(frameLayout, "");
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        showTips("获取充值数据失败，请连接网络后重试");
    }

    /**
     * 检查支付结果
     */
    private void checkResult() {
        initData();
    }

    /**
     * 分析直播内容数据
     *
     * @param s
     */
    private void analyzeLiveShowData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    showTips("获取直播内容失败，请稍后重试~");
                    return;
                }
                JSONObject object = array.getJSONObject(0);
                String code = object.getString("code");
                JSONObject object_tips = object.getJSONObject("tips");
                String content = object_tips.getString("exchangeInstruction");
                showWxPublicNumberDialog(0, code, content);

                if (mPosition != -1 && mPosition < mList.size()) {
                    int count = mList.get(mPosition).getCount();
                    if (count > 0) {
                        mList.get(mPosition).setCount(count - 1);
                        adapter.notifyItemChanged(mPosition, "count");
                    }
                }

            } else {
                showTips("获取直播内容失败，请稍后重试~");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("获取直播内容失败，请稍后重试~");
        }
    }

    /**
     * 分析答疑数据
     *
     * @param s
     */
    private void analyzeAnswerData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                String code = object.getString("code");
                JSONObject object_tips = object.getJSONObject("tips");
                String content = object_tips.getString("exchangeInstruction");
                showWxPublicNumberDialog(1, code, content);

                if (mPosition != -1 && mPosition < mList.size()) {
                    int count = mList.get(mPosition).getCount();
                    if (count > 0) {
                        mList.get(mPosition).setCount(count - 1);
                        adapter.notifyItemChanged(mPosition, "count");
                    }
                }

            } else {
                showTips("获取直播内容失败，请稍后重试~");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("获取直播内容失败，请稍后重试~");
        }
    }


    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, MemberCentreActivity> {

        protected GetData(MemberCentreActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MemberCentreActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("cardId", strings[2]);
                if (!strings[3].equals("") && !strings[3].equals("null")) {
                    object.put("code", strings[3]);
                }
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
        protected void onPostExecute(MemberCentreActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 获取价格
     */
    private static class GetPriceData
            extends WeakAsyncTask<String, Void, String, MemberCentreActivity> {

        protected GetPriceData(MemberCentreActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MemberCentreActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("cardId", strings[2]);
                object.put("code", strings[3]);
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
        protected void onPostExecute(MemberCentreActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorPrice("网络错误，请连接网络后重试~");
            } else {
                activity.analyzePriceData(s);
            }
        }
    }

    /**
     * 微信统一下单
     */
    private static class WXUnifiedOrder
            extends WeakAsyncTask<String, Void, String, MemberCentreActivity> {

        public WXUnifiedOrder(MemberCentreActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MemberCentreActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                if (!strings[1].equals("")) {
                    object.put("code", strings[1]);
                }
                object.put("studentId", strings[2]);
                object.put("cardId", strings[3]);
                object.put("trade_type", strings[4]);
                object.put("spbill_create_ip", strings[5]);
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
        protected void onPostExecute(MemberCentreActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeWXUnifiedOrderData(s);
            }
        }
    }

    /**
     * 支付宝统一下单
     */
    private static class AliUnifiedOrder
            extends WeakAsyncTask<String, Void, String, MemberCentreActivity> {

        public AliUnifiedOrder(MemberCentreActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MemberCentreActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                if (!strings[1].equals("")) {
                    object.put("code", strings[1]);
                }
                object.put("studentId", strings[2]);
                object.put("cardId", strings[3]);
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
        protected void onPostExecute(MemberCentreActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeAliUnifiedOrderData(s);
            }
        }
    }

    /**
     * 获取直播内容
     */
    private static class GetLiveShowData
            extends WeakAsyncTask<String, Void, String, MemberCentreActivity> {

        protected GetLiveShowData(MemberCentreActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MemberCentreActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("functionCardId", strings[2]);
                object.put("functionCardRecordId", strings[3]);
                object.put("items", strings[4]);
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
        protected void onPostExecute(MemberCentreActivity activity, String s) {
            if (s == null) {
                activity.showTips("获取直播内容失败，请检查网络是否可用");
            } else {
                activity.analyzeLiveShowData(s);
            }
        }
    }

    /**
     * 获取答疑内容
     */
    private static class GetAnswerData
            extends WeakAsyncTask<String, Void, String, MemberCentreActivity> {

        protected GetAnswerData(MemberCentreActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MemberCentreActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("itemId", strings[2]);
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
        protected void onPostExecute(MemberCentreActivity activity, String s) {
            if (s == null) {
                activity.showTips("获取答疑内容失败，请检查网络是否可用");
            } else {
                activity.analyzeAnswerData(s);
            }
        }
    }

}
