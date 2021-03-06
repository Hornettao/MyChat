package com.hornettao.mychat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;

import com.hornettao.mychat.MyChatApplication;
import com.hornettao.mychat.MyMessageReceiver;
import com.hornettao.mychat.R;
import com.hornettao.mychat.fragment.ContactsFragment;
import com.hornettao.mychat.fragment.FoundFragment;
import com.hornettao.mychat.fragment.RecentFragment;
import com.hornettao.mychat.fragment.SettingsFragment;
import com.hornettao.mychat.utils.DensityUtils;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.T;
import com.hornettao.mychat.view.PagerSlidingTabStrip;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

/**
 * Created by hornettao on 15/7/9.
 */
public class HomeActivity extends Base2Activity implements EventListener {

    /**
     * 聊天界面的Fragment
     */
    private RecentFragment recentFragment;

    /**
     * 通讯录界面的Fragment
     */
    private ContactsFragment contactsFragment;

    /**
     * 发现界面的Fragment
     */
    private FoundFragment foundFragment;

    /**
     * 我的界面的Fragment
     */
    private SettingsFragment settingsFragment;

    /**
     * PagerSlidingTabStrip的实例
     */
    private PagerSlidingTabStrip tabs;

    private ViewPager pager;

    private int currentPosition = 0;

//    //消息提示
//    private ImageView iv_recent_tips;
//    private ImageView iv_contact_tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
        BmobChat.getInstance(this).startPollService(30);
        //开启广播接收器
        initNewMessageBroadCast();
        initTagMessageBroadCast();
        setUpView();
        L.i("oncreate");
    }

    private void setUpView() {
        setOverflowShowingAlways();
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //小圆点提示
//        if (BmobDB.create(this).hasUnReadMsg()) {
//            iv_recent_tips.setVisibility(View.VISIBLE);
//        } else {
//            iv_recent_tips.setVisibility(View.GONE);
//        }
//        if (BmobDB.create(this).hasNewInvite()) {
//            iv_contact_tips.setVisibility(View.VISIBLE);
//        } else {
//            iv_contact_tips.setVisibility(View.GONE);
//        }
        tabs.setChatBadgeViewCount(BmobDB.create(this).getAllUnReadCount());
        tabs.setContactsBadgeViewCount(getNewInviteCount());
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        //清空
        MyMessageReceiver.mNewNum=0;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    }

    @Override
    public void onMessage(BmobMsg message) {
        // TODO Auto-generated method stub
        refreshNewMsg(message);
    }

    /**
     * 获取添加好友数目
     */
    private int getNewInviteCount() {
        if (!BmobDB.create(this).hasNewInvite())
            return 0;
        return 1;
    }

    /**
     * 刷新界面
     * @Title: refreshNewMsg
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshNewMsg(BmobMsg message){
        // 声音提示
        boolean isAllow = MyChatApplication.getInstance().getSpUtil().isAllowVoice();
        if(isAllow){
            MyChatApplication.getInstance().getMediaPlayer().start();
        }
//        iv_recent_tips.setVisibility(View.VISIBLE);
        tabs.setChatBadgeViewCount(BmobDB.create(this).getAllUnReadCount());
        //也要存储起来
        if(message != null){
            BmobChatManager.getInstance(HomeActivity.this).saveReceiveMessage(true,message);
        }
        if(currentPosition == 0){
            //当前页面如果为会话页面，刷新此页面
            if(recentFragment != null){
                recentFragment.refresh();
            }
        }
    }

    NewBroadcastReceiver  newReceiver;

    private void initNewMessageBroadCast(){
        // 注册接收消息广播
        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }

    /**
     * 新消息广播接收者
     *
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新界面
            refreshNewMsg(null);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    TagBroadcastReceiver  userReceiver;

    private void initTagMessageBroadCast(){
        // 注册接收消息广播
        userReceiver = new TagBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        //优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(userReceiver, intentFilter);
    }

    /**
     * 标签消息广播接收者
     */
    private class TagBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if(isNetConnected){
            T.showShort(this, R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        // TODO Auto-generated method stub
        refreshInvite(message);
    }

    /** 刷新好友请求
     * @Title: notifyAddUser
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshInvite(BmobInvitation message){
        boolean isAllow = MyChatApplication.getInstance().getSpUtil().isAllowVoice();
        if(isAllow){
            MyChatApplication.getInstance().getMediaPlayer().start();
        }
//        iv_contact_tips.setVisibility(View.VISIBLE);
        tabs.setContactsBadgeViewCount(getNewInviteCount());
        if (currentPosition == 1) {
            if (contactsFragment != null) {
                contactsFragment.refresh();
            }
        } else {
            //同时提醒通知
            String tickerText = message.getFromname()+"请求添加好友";
            boolean isAllowVibrate = MyChatApplication.getInstance().getSpUtil().isAllowVibrate();
            BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.mipmap.ic_launcher
                    , tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);
        }
    }

    @Override
    public void onOffline() {
        // TODO Auto-generated method stub
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // TODO Auto-generated method stub
    }


    private static long firstTime;
    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (firstTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            T.showShort(this, "再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            unregisterReceiver(newReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(userReceiver);
        } catch (Exception e) {
        }
        //取消定时检测服务
        BmobChat.getInstance(this).stopPollService();
    }


    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
//        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        tabs.setUnderlineHeight(DensityUtils.getDP(this, 1));
        // 设置Tab Indicator的高度
//        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        tabs.setIndicatorHeight(DensityUtils.getDP(this, 4));
        // 设置Tab标题文字的大小
//        tabs.setTextSize((int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        tabs.setTextSize(DensityUtils.getSP(this, 15));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#45c01a"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "聊天", "通讯录", "发现", "我"};

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (recentFragment == null) {
                        recentFragment = new RecentFragment();
                    }
                    return recentFragment;
                case 1:
                    if (contactsFragment == null) {
                        contactsFragment = new ContactsFragment();
                    }
                    return contactsFragment;
                case 2:
                    if (foundFragment == null) {
                        foundFragment = new FoundFragment();
                    }
                    return foundFragment;
                case 3:
                    if (settingsFragment == null) {
                        settingsFragment = new SettingsFragment();
                    }
                    return settingsFragment;
                default:
                    return null;
            }
        }

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_recent, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.i("hihi， 回来了");
    }
}
