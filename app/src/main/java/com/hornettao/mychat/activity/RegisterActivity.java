package com.hornettao.mychat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.User;
import com.hornettao.mychat.config.Consts;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.T;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    public static final String PHONE_NUMBER = "PHONE_NUMBER";

    private Button registerButton;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        showBackAndHidelogo();
        getData();
        setUpView();
    }

    private void setUpView() {
        registerButton = (Button) this.findViewById(R.id.button_register);
        passwordEditText = (EditText) this.findViewById(R.id.edit_text_password);
        confirmPasswordEditText = (EditText) this.findViewById(R.id.edit_text_sms_code);
        registerButton.setOnClickListener(this);
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent == null)
            return;
        phoneNumber = intent.getStringExtra(PHONE_NUMBER);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register:
                register();
                break;
            default:
                break;
        }
    }

    private void register() {
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            T.showShort(this, R.string.toast_error_phone_number_null);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            T.showShort(this, R.string.toast_error_password_null);
            return;
        }
        if (!password.equals(confirmPassword)) {
            T.showShort(this, R.string.toast_error_password_twice_not_match);
            return;
        }
        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User user = new User();
        user.setUsername(phoneNumber);
        user.setPassword(password);
        user.setSex(true);
        user.setNick(phoneNumber);
        user.setMobilePhoneNumber(phoneNumber);
        user.setMobilePhoneNumberVerified(true);
        //将user和设备id进行绑定
        user.setDeviceType("android");
        user.setInstallId(BmobInstallation.getInstallationId(this));
        user.signUp(RegisterActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                progress.dismiss();
                T.showShort(RegisterActivity.this, "注册成功");
                // 将设备与username进行绑定
                userManager.bindInstallationForRegister(user.getUsername());
                //更新地理位置信息
                updateUserLocation();
                //发广播通知登陆页面退出
                sendBroadcast(new Intent(Consts.ACTION_LOGIN_SUCCESS_FINISH));
                // 启动主页
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                L.e(arg1);
                T.showShort(RegisterActivity.this, "注册失败:" + arg1);
                progress.dismiss();
            }
        });
    }
}
