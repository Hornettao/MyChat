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
import com.hornettao.mychat.utils.NetUtils;
import com.hornettao.mychat.utils.T;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText registerPhoneNumberEditText;
    private EditText smsCodeEditText;
    private Button sendSmsCodeButton;
    private Button nextStepButton;

    private String smsCode = "";
    private String phoneNumber = "";

    private MyCountTimer countTimer;
    private final int resendSMSCodeDelay = 60000;
    private final int countUnit = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        showBackAndHidelogo();
        setUpView();
    }

    private void setUpView() {
        registerPhoneNumberEditText = (EditText) this.findViewById(R.id.edit_text_register_phone_number);
        smsCodeEditText = (EditText) this.findViewById(R.id.edit_text_sms_code);
        sendSmsCodeButton = (Button) this.findViewById(R.id.button_send_sms_code);
        nextStepButton = (Button) this.findViewById(R.id.button_next_step);
        sendSmsCodeButton.setOnClickListener(this);
        nextStepButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_send_sms_code:
                if (!NetUtils.isConnected(this))
                    T.showShort(this, "无网络连接");
                sendSmsCode();
                break;
            case R.id.button_next_step:
                verifySmsCode();
                break;
            default:
                break;
        }
    }

    private void verifySmsCode() {
        phoneNumber = registerPhoneNumberEditText.getText().toString();
        smsCode = smsCodeEditText.getText().toString();
        if (TextUtils.isEmpty(smsCode)) {
            T.showShort(this, R.string.toast_error_sms_code_null);
            return;
        }
        Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
        intent.putExtra(ResetPasswordActivity.SMS_CODE, smsCode);
        startActivity(intent);
        finish();
//        BmobSMS.verifySmsCode(this, phoneNumber, smsCode, new VerifySMSCodeListener() {
//            @Override
//            public void done(BmobException e) {
//                if (e == null) {
//                    L.i("验证通过");
//                    Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
//                    intent.putExtra(ResetPasswordActivity.SMS_CODE, phoneNumber);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    T.showShort(ForgetPasswordActivity.this, "验证码错误");
//                    L.i("验证失败：code =" + e.getErrorCode() + ",msg = " + e.getLocalizedMessage());
//                }
//            }
//        });
    }

    private void sendSmsCode() {
        phoneNumber = registerPhoneNumberEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            T.showShort(this, R.string.toast_error_phone_number_null);
            return;
        }
        countTimer = new MyCountTimer(resendSMSCodeDelay, countUnit);
        countTimer.start();
        sendSmsCodeButton.setClickable(false);
        BmobSMS.requestSMSCode(this, phoneNumber, "注册验证码模板", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    L.i("短信id:" + integer);
                } else {
                    L.e(e.getErrorCode() + "==" +e.getMessage());
                    countTimer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendSmsCodeButton.setClickable(true);
                            sendSmsCodeButton.setText("发送失败，点击重发");
                        }
                    });
                }
            }
        });
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendSmsCodeButton.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            sendSmsCodeButton.setClickable(true);
            sendSmsCodeButton.setText("重新发送验证码");
        }
    }
}
