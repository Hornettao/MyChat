package com.hornettao.mychat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hornettao.mychat.R;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.T;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class VerifySMSCodeActivity extends BaseActivity implements View.OnClickListener {

    private EditText phoneNumberEditText;
    private EditText smsCodeEditText;
    private Button verifySMSCodeButton;
    private Button sendSMSCodeButton;

    private String phoneNumber = "";
    private String smsCode = "";

    private MyCountTimer countTimer;
    private final int resendSMSCodeDelay = 60000;
    private final int countUnit = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_sms_code);
        showBackAndHidelogo();
        setUpView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        phoneNumber = "";
        smsCode = "";
        phoneNumberEditText.setText("");
        smsCodeEditText.setText("");
        sendSMSCodeButton.setText(R.string.send_sms_code);
    }

    private void setUpView() {
        phoneNumberEditText = (EditText) this.findViewById(R.id.edit_text_phone_number);
        smsCodeEditText = (EditText) this.findViewById(R.id.edit_text_sms_code);
        verifySMSCodeButton = (Button) this.findViewById(R.id.button_verify_sms_code);
        sendSMSCodeButton = (Button) this.findViewById(R.id.button_send_sms_code);
        verifySMSCodeButton.setOnClickListener(this);
        sendSMSCodeButton.setOnClickListener(this);
    }

    private void verifySmsCode() {
        smsCode = smsCodeEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            T.showShort(this, R.string.toast_error_sms_code_null);
            return;
        }
        BmobSMS.verifySmsCode(this, phoneNumber, smsCode, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException e) {
                if (e == null ){
                    L.i("验证通过");
                    Intent intent = new Intent(VerifySMSCodeActivity.this, RegisterActivity.class);
                    intent.putExtra(RegisterActivity.PHONE_NUMBER, phoneNumber);
                    startActivity(intent);
                    finish();
                } else {
                    L.i("验证失败：code =" + e.getErrorCode() + ",msg = " + e.getLocalizedMessage());
                }
            }
        });
    }

    private void sendSMSCode() {
        phoneNumber = phoneNumberEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            T.showShort(this, R.string.toast_error_phone_number_null);
            return;
        }
        countTimer = new MyCountTimer(resendSMSCodeDelay, countUnit);
        countTimer.start();
        BmobSMS.requestSMSCode(this, phoneNumber, "注册验证码模板", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    L.i("taoyc", "短信id:" + integer);
                } else {
                    countTimer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendSMSCodeButton.setText("发送失败，点击重发");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_verify_sms_code:
                verifySmsCode();
                break;
            case R.id.button_send_sms_code:
                sendSMSCode();
        }
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendSMSCodeButton.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            sendSMSCodeButton.setText("重新发送验证码");
        }
    }
}
