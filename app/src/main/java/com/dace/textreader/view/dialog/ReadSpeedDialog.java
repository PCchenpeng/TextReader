package com.dace.textreader.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.dace.textreader.R;

public class ReadSpeedDialog extends  Dialog {

    private ImageView iv_cancle;
    private ImageView iv_img;
    private Context mCotext;
    private String type;

    public ReadSpeedDialog(Context context,String type) {
        super(context, R.style.dialog);
        this.mCotext = context;
        this.type = type;
        setContentView(R.layout.dialog_readspeed);
        initData();

    }

    private void initData() {
        iv_img = findViewById(R.id.iv_img);
        iv_cancle = findViewById(R.id.iv_cancle);
        switch (type){
            case "200":
                iv_img.setImageResource(R.drawable.read_testspeed_good);
                break;
            case "100":
                iv_img.setImageResource(R.drawable.read_testspeed_bad);
                break;
            default:
                iv_img.setImageResource(R.drawable.read_testspeed_finished);
                break;
        }
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
