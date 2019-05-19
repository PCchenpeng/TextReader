package com.dace.textreader.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.dace.textreader.R;
import com.dace.textreader.activity.FeedbackActivity;
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.MyNoteListActivity;
import com.dace.textreader.util.PreferencesUtil;

public class ConfirmPopWindow extends PopupWindow implements View.OnClickListener {
    private Context context;
    private View ll_chat, ll_friend;
    private String word;
    public ConfirmPopWindow(Context context,String word) {
        super(context);
        this.context = context;
        this.word = word;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_worddetail_more, null);
        ll_chat = view.findViewById(R.id.ll_note);//发起群聊
        ll_friend = view.findViewById(R.id.ll_advice);//添加好友
        ll_chat.setOnClickListener(this);
        ll_friend.setOnClickListener(this);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        this.setWidth((int) (d.widthPixels * 0.35));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) context, 0.8f);//0.0-1.0
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((Activity) context, 1f);
            }
        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view) {
        //弹窗位置设置
        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 10);
        //showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_note:
                if(!isLogin()){
                    Intent loginIntent = new Intent(context,LoginActivity.class);
                    context.startActivity(loginIntent);
                    return;
                }
                Intent intent = new Intent(context,MyNoteListActivity.class);
                context.startActivity(intent);
                break;
            case R.id.ll_advice:
                if(!isLogin()){
                    Intent loginIntent = new Intent(context,LoginActivity.class);
                    context.startActivity(loginIntent);
                    return;
                }
                Intent intent1 = new Intent(context,FeedbackActivity.class);
                intent1.putExtra("type","词语反馈");
                intent1.putExtra("word",word);
                context.startActivity(intent1);
                break;
            default:
                break;
        }
    }


    private boolean isLogin(){
        Object studeenObj = PreferencesUtil.getData(context,"studentId","-1");
        if(studeenObj == null)
            return false;
        String studentId = studeenObj.toString();

        return !studentId.equals("-1");
    }

}
