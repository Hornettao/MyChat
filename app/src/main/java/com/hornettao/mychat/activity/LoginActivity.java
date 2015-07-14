package com.hornettao.mychat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hornettao.mychat.R;
import com.hornettao.mychat.config.Consts;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.NetUtils;
import com.hornettao.mychat.utils.T;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by hornettao on 15/7/10.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgetPwdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        showBackAndHidelogo();
        setUpView();
    }

    private void setUpView() {
        phoneNumberEditText = (EditText) findViewById(R.id.edit_text_phone_number);
        passwordEditText = (EditText) findViewById(R.id.edit_text_password);
        forgetPwdTextView = (TextView) findViewById(R.id.text_view_forget_pwd);
        loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);
        forgetPwdTextView.setOnClickListener(this);
    }


    private void login(){
        String name = phoneNumberEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(name)) {
            T.showShort(this, R.string.toast_error_username_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            T.showShort(this, R.string.toast_error_password_null);
            return;
        }

        final ProgressDialog progress = new ProgressDialog(
                LoginActivity.this);
        progress.setMessage("正在登陆...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        userManager.login(name, password, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        progress.setMessage("正在获取好友列表...");
                    }
                });
                //更新用户的地理位置以及好友的资料
                updateUserInfos();
                progress.dismiss();
                //发广播通知登陆页面退出
                sendBroadcast(new Intent(Consts.ACTION_LOGIN_SUCCESS_FINISH));
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int errorcode, String arg0) {
                // TODO Auto-generated method stub
                progress.dismiss();
                L.e(arg0);
                T.showShort(LoginActivity.this, arg0);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                boolean isNetConnected = NetUtils.isConnected(this);
                if(!isNetConnected){
                    T.showShort(this, R.string.network_tips);
                    return;
                }
                login();
                break;
            case R.id.text_view_forget_pwd:
                startAnimActivity(ForgetPasswordActivity.class);
                break;
            default:
                break;
        }
    }
}
