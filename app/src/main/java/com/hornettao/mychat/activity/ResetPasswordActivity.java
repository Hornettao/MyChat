package com.hornettao.mychat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hornettao.mychat.R;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.NetUtils;
import com.hornettao.mychat.utils.T;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    public static final String SMS_CODE = "SMS_CODE";

    private EditText newPwdEditText;
    private EditText confirmPwdEditText;
    private Button finishButton;

    private String smsCode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        showBackAndHidelogo();
        getData();
        setUpView();
    }

    private void setUpView() {
        newPwdEditText = (EditText) this.findViewById(R.id.edit_text_new_password);
        confirmPwdEditText = (EditText) this.findViewById(R.id.edit_text_new_password_confirm);
        finishButton = (Button) this.findViewById(R.id.button_finish);
        finishButton.setOnClickListener(this);
    }

    private void getData() {
        smsCode = getIntent().getStringExtra(SMS_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_finish:
                if (!NetUtils.isConnected(this))
                    T.showShort(this, "无网络连接");
                resetPassword();
                break;
            default:
                break;
        }
    }

    private void resetPassword() {
        String newPassword = newPwdEditText.getText().toString();
        String confirmNewPassword = confirmPwdEditText.getText().toString();
        if (TextUtils.isEmpty(newPassword)) {
            T.showShort(this, R.string.toast_error_password_null);
            return;
        }
        if (TextUtils.isEmpty(confirmNewPassword)) {
            T.showShort(this, R.string.toast_error_password_null);
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            T.showShort(this, R.string.toast_error_password_twice_not_match);
            return;
        }
        BmobUser.resetPasswordBySMSCode(this, smsCode, newPassword, new ResetPasswordByCodeListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    L.i("密码重置成功");
                } else {
                    L.i("重置失败：code =" + e.getErrorCode() + ",msg = " + e.getLocalizedMessage());
                }
            }
        });
    }
}
