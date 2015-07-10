package com.hornettao.mychat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hornettao.mychat.R;
import com.hornettao.mychat.config.Consts;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private Button registerButton;
    private Button loginButton;

    private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setUpView();
        //注册退出广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.ACTION_LOGIN_SUCCESS_FINISH);
        registerReceiver(receiver, filter);
    }

    private void setUpView() {
        registerButton = (Button) this.findViewById(R.id.button_register);
        loginButton = (Button) this.findViewById(R.id.button_login);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register:
                register();
                break;
            case R.id.button_login:
                login();
                break;
            default:
                break;
        }
    }

    private void register() {
        startActivity(new Intent(this, VerifySMSCodeActivity.class));
    }

    private void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && Consts.ACTION_LOGIN_SUCCESS_FINISH.equals(intent.getAction())) {
                finish();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
