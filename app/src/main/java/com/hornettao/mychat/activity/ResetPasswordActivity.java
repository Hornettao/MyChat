package com.hornettao.mychat.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.hornettao.mychat.R;

public class ResetPasswordActivity extends BaseActivity {

    private EditText newPwdEditText;
    private EditText confirmPwdEditText;
    private Button finishButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        showBackAndHidelogo();
        setUpView();
    }

    private void setUpView() {

    }

}
