package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 阅读能力分析
 */
public class AbilityAnalysisActivity extends BaseActivity {

    private final String url = HttpUrlPre.HTTP_URL + "/statistics/reading/ability?studentId=";

    private RelativeLayout rl_back;  //返回按钮
    private TextView tv_title;
    private ImageView iv_head;
    private LinearLayout ll_grade;
    private TextView tv_grade;
    private TextView tv_today;
    private TextView tv_total;
    private WebView webView_time;
    private WebView webView_total;
    private ImageView iv_time;
    private ImageView iv_total;

    private String today = "";
    private String total = "";
    private String time = "";
    private String read = "";
    private String read_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ability_analysis);

        initView();
        initData();
        initEvents();


    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToDoubtView();
            }
        });
    }

    /**
     * 前往疑问解释视图
     */
    private void turnToDoubtView() {
        Intent intent = new Intent(this, EventsActivity.class);
        intent.putExtra("pageName", "py_activity");
        startActivity(intent);
    }

    private void initData() {
        new GetData(AbilityAnalysisActivity.this)
                .execute(url + NewMainActivity.STUDENT_ID);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, AbilityAnalysisActivity> {

        protected GetData(AbilityAnalysisActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AbilityAnalysisActivity activity, String[] params) {
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
        protected void onPostExecute(AbilityAnalysisActivity activity, String s) {
            if (s == null) {
                activity.noContentInfo();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 分析数据
     *
     * @param s 获取到的数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                if (jsonObject.getString("data").equals("0")) {
                    noContentInfo();
                } else {
                    JSONObject data = jsonObject.getJSONObject("data");
                    today = data.getString("dailyNum");
                    total = data.getString("totalDuration");
                    time = data.getString("weekNum");
                    read = data.getString("typeNum");
                    read_type = data.getString("read_type");
                    updateUI();
                }
            } else {
                noContentInfo();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noContentInfo();
        }
    }

    /**
     * 无信息
     */
    private void noContentInfo() {
        today = "";
        total = "";
        time = "";
        read = "";
        GlideUtils.loadImageWithNoOptions(this, R.drawable.image_state_404, iv_time);
        GlideUtils.loadImageWithNoOptions(this, R.drawable.image_state_404, iv_total);
    }

    //更新UI
    private void updateUI() {
        if (today.equals("")) {
            tv_today.setText(String.valueOf(0));
        } else {
            tv_today.setText(today.split("\\.")[0]);
        }
        if (total.equals("")) {
            tv_total.setText(String.valueOf(0));
        } else {
            showTime(total.split("\\.")[0]);
        }
        if (webView_time != null) {
            if (time.equals("") || time.equals("null")) {
                GlideUtils.loadImageWithNoOptions(this, R.drawable.image_state_empty, iv_time);
                webView_time.setVisibility(View.GONE);
            } else {
                iv_time.setVisibility(View.GONE);
                webView_time.loadDataWithBaseURL("about:blank",
                        getTimeHtml(time), "text/html", "utf-8", null);
            }
        }
        if (webView_total != null) {
            if (read.equals("") || read.equals("null")) {
                GlideUtils.loadImageWithNoOptions(this, R.drawable.image_state_empty, iv_total);
                webView_total.setVisibility(View.GONE);
            } else {
                iv_total.setVisibility(View.GONE);
                webView_total.loadDataWithBaseURL("about:blank",
                        getTotalHtml(read), "text/html", "utf-8", null);
            }
        }
    }

    /**
     * 显示时间（转为时、分单位）
     *
     * @param str 总的时长（单位：分钟）
     */
    private void showTime(String str) {
        int data = Integer.valueOf(str);
        int hour = data / 60;
        int min = data % 60;
        String time = hour + "时" + min;
        int length = String.valueOf(hour).length();
        SpannableString spanString = new SpannableString(time);
        //设置字体大小
        spanString.setSpan(new AbsoluteSizeSpan(14, true), length, length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体颜色
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")),
                length, length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_total.setText(spanString);
    }

    //获取时间统计图的完整代码
    private String getTimeHtml(String time) {
        String head = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"Generator\" content=\"EditPlus®\">\n" +
                "  <meta name=\"Author\" content=\"\">\n" +
                "  <meta name=\"Keywords\" content=\"\">\n" +
                "  <meta name=\"Description\" content=\"\">\n" +
                "  <title>Document</title>\n" +
                "  <script src=\"file:///android_asset/js/echarts.min.js\"></script>\n" +
                "\n" +
                " </head>\n" +
                " <body>\n" +
                "    <div id=\"main\" style=\"width: 100%;height:600px;\"></div>\n" +
                "<script type=\"text/javascript\">\n" +
                "        var myChart = echarts.init(document.getElementById('main'));";
        String over = "myChart.setOption(option);\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
        return head + time + over;
    }

    //获取分类阅读统计图的完整代码
    private String getTotalHtml(String time) {
        String head_1 = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"Generator\" content=\"EditPlus®\">\n" +
                "  <meta name=\"Author\" content=\"\">\n" +
                "  <meta name=\"Keywords\" content=\"\">\n" +
                "  <meta name=\"Description\" content=\"\">\n" +
                "  <title>Document</title>\n" +
                "  <script src=\"file:///android_asset/js/echarts.min.js\"></script>\n" +
                "\n" +
                " </head>\n" +
                " <body>\n" +
                "<div id=\"main\" style=\"width: 100%;height:600px;\"></div>\n" +
                "<script type=\"text/javascript\">\n" +
                " \n" +
                "        var myChart = echarts.init(document.getElementById('main'));\n" +
                "option = {\n" +
                "calculable : true,\n" +
                "    tooltip: {\n" +
                "        trigger: 'item',\n" +
                "        formatter: \"{b}： {c} 篇\",\n" +
                "textStyle: {\n" +
                "fontSize:40,\n" +
                "fontFamily:'微软雅黑',\n" +
                "fontWeight:'bold'\n" +
                "}\n" +
                "    },\n" +
                "    legend: {\n" +
                "        orient: 'vertical',\n" +
                "        x: 'left',\n" +
                "        data:";
        String head_2 = ",textStyle: {\n" +
                "fontSize:40,\n" +
                "fontFamily:'微软雅黑'\n" +
                "},\n" +
                "        padding: [\n" +
                "    120,  \n" +
                "    10, \n" +
                "    0,  \n" +
                "    720 \n" +
                "]\n" +
                "    },\n" +
                "\n" +
                "    series : [\n" +
                "        {\n" +
                "            type:'pie',\n" +
                "radius: ['50%', '90%'],\n" +
                "center : ['35%', '50%'],\n" +
                "            data:";
        String over = ",\n" +
                "           itemStyle: {\n" +
                "           normal:{\n" +
                "\n" +
                "            label:{\n" +
                "            show:false,\n" +
                "            formatter: '{b} : {c}',\n" +
                "textStyle: {\n" +
                "fontSize:20 // 用 legend.textStyle.fontSize 更改示例大小\n" +
                "}\n" +
                "            },\n" +
                "            labelLine:{\n" +
                "            show:true\n" +
                "            }\n" +
                "            }\n" +
                "           }\n" +
                "       }\n" +
                "   ]\n" +
                "};\n" +
                "        myChart.setOption(option);\n" +
                "    </script>\n" +
                " </body>\n" +
                "</html>\n";
        return head_1 + read_type + head_2 + time + over;
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("阅读能力详情");

        iv_head = findViewById(R.id.iv_head_ability);
        ll_grade = findViewById(R.id.ll_grade_ability);
        tv_grade = findViewById(R.id.tv_grade_ability);
        tv_today = findViewById(R.id.tv_today_reader_time);
        tv_total = findViewById(R.id.tv_total_reader_time);
        webView_time = findViewById(R.id.web_view_ability_time);
        webView_total = findViewById(R.id.web_view_ability_total);
        iv_time = findViewById(R.id.iv_ability_time);
        iv_total = findViewById(R.id.iv_ability_total);

        GlideUtils.loadUserImage(this,
//                HttpUrlPre.FILE_URL +
                        NewMainActivity.USERIMG,
                iv_head);
        String grade = "阅读能力 " + NewMainActivity.PY_SCORE + "PY";
        tv_grade.setText(grade);

        GlideUtils.loadGIFImageWithNoOptions(this, R.drawable.image_loading, iv_time);
        GlideUtils.loadGIFImageWithNoOptions(this, R.drawable.image_loading, iv_total);

        webView_time.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView_time.getSettings().setJavaScriptEnabled(true);
        webView_time.getSettings().setUseWideViewPort(true);
        webView_time.getSettings().setLoadWithOverviewMode(true);
        webView_time.getSettings().setDefaultTextEncodingName("utf-8");
        webView_time.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView_total.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView_total.getSettings().setJavaScriptEnabled(true);
        webView_total.getSettings().setUseWideViewPort(true);
        webView_total.getSettings().setLoadWithOverviewMode(true);
        webView_total.getSettings().setDefaultTextEncodingName("utf-8");
        webView_total.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDestroy() {
        if (webView_time != null) {
            webView_time.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView_time.clearHistory();

            ((ViewGroup) webView_time.getParent()).removeView(webView_time);
            webView_time.destroy();
            webView_time = null;
        }
        if (webView_total != null) {
            webView_total.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView_total.clearHistory();

            ((ViewGroup) webView_total.getParent()).removeView(webView_total);
            webView_total.destroy();
            webView_total = null;
        }
        super.onDestroy();
    }
}
