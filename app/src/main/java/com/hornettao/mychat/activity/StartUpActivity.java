package com.hornettao.mychat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.hornettao.mychat.MyChatApplication;
import com.hornettao.mychat.R;
import com.hornettao.mychat.config.Config;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.WeakReferenceHandler;

import cn.bmob.im.BmobChat;

/**
 * 默认启动Activity，需要在style里面设置图片避免黑屏，后期加
 * Created by hornettao on 15/7/9.
 */
public class StartUpActivity extends BaseActivity {

    private static final int GO_HOME = 100;
    private static final int GO_WELCOME = 200;

    private static final int ANIMATION_TIME = 1000;

    // 定位获取当前用户的地理位置
    private LocationClient mLocationClient;

    // 注册广播接收器，用于监听网络以及验证key
    private BaiduReceiver mReceiver;

    private Handler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        BmobChat.getInstance(this).init(Config.applicationId);
        // 开启定位
        initLocClient();
        // 注册地图 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new BaiduReceiver();
        registerReceiver(mReceiver, iFilter);

        if (userManager.getCurrentUser() != null) {
            // 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
            updateUserInfos();
            mHandler.sendEmptyMessageDelayed(GO_HOME, ANIMATION_TIME);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_WELCOME, ANIMATION_TIME);
        }
    }

    /**
     * 开启定位，更新当前用户的经纬度坐标
     * @Title: initLocClient
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initLocClient() {
        mLocationClient = MyChatApplication.getInstance().mLocationClient;
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
        option.setCoorType("bd09ll"); // 设置坐标类型:百度经纬度
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
        option.setIsNeedAddress(false);// 不需要包含地址信息
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private static class MyHandler extends WeakReferenceHandler<StartUpActivity> {

        public MyHandler(StartUpActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(StartUpActivity reference, Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    reference.startAnimActivity(HomeActivity.class);
                    reference.finish();
                    break;
                case GO_WELCOME:
                    reference.startAnimActivity(WelcomeActivity.class);
                    reference.finish();
                    break;
            }
        }
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                L.e("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s
                    .equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                L.e("当前网络连接不稳定，请检查您的网络设置!");
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
