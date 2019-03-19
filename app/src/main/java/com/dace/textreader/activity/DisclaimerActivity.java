package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.StatusBarUtil;

import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * 免责声明
 */
public class DisclaimerActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_title;
    private JustifyTextView tv_disclaimer;

    private String disclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        initData();
        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        disclaimer = "1. 访问者在接受本应用服务之前，请务必仔细阅读本条款并同意本声明。" +
                "访问者访问本应用的行为以及通过各类方式利用本应用的行为，" +
                "都将被视作是对本声明全部内容的无异议的认可;如有异议，请立即跟本网站协商，" +
                "并取得本应用的书面同意。" + "\n\n" +
                "2. 本应用所收集的部分公开资料来源于互联网，" +
                "转载的目的在于传递更多信息及用于网络分享，并不代表本站赞同其观点和对其真实性负责，" +
                "也不构成任何其他建议。本应用部分作品是由网友自主投稿和发布、编辑整理上传，" +
                "对此类作品本站仅提供交流平台，不为其版权负责。" +
                "如果您发现应用上有侵犯您的知识产权的作品，请与我们取得联系，" +
                "我们会及时修改或删除。\n\n" +
                "3.本应用所提供的信息，只供参考之用。本应用不保证信息的准确性、有效性、" +
                "及时性和完整性。本应用及其雇员一概毋须以任何方式就任何信息传递或传送的失误、" +
                "不准确或错误，对用户或任何其他人士负任何直接或间接责任。在法律允许的范围内，" +
                "本应用在此声明，" +
                "不承担用户或任何人士就使用或未能使用本应用所提供的信息或任何链接所引致的任何直接、" +
                "间接、附带、从属、特殊、惩罚性或惩戒性的损害赔偿。\n";
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("免责声明");

        tv_disclaimer = findViewById(R.id.tv_disclaimer);
        tv_disclaimer.setText(disclaimer);
    }

}
