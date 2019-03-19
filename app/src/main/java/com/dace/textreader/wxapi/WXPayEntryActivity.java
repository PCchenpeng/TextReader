package com.dace.textreader.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.dace.textreader.App;
import com.dace.textreader.activity.BaseActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.RechargeActivity;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, App.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int code = baseResp.errCode;
            switch (code) {
                case 0:
                    RechargeActivity.isPaymentSuccessful = true;
                    break;
                case -1:
                    RechargeActivity.isPaymentSuccessful = false;
                    break;
                case -2:
                    RechargeActivity.isPaymentSuccessful = false;
                    break;
                default:
                    RechargeActivity.isPaymentSuccessful = false;
                    break;
            }
            finish();
        }
    }

}
