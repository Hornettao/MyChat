package com.hornettao.mychat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hornettao.mychat.R;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText registerPhoneNumberEditText;
    private EditText smsCodeEditText;
    private Button sendSmsCodeButton;
    private Button nextStepButton;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_send_sms_code:
                sendSmsCode();
                break;
            case R.id.button_next_step:
                verifySmsCode();
                break;
            default:
                break;
        }
    }
}
