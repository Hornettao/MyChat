package com.hornettao.mychat.activity;

import android.app.ActionBar;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.hornettao.mychat.MyChatApplication;
import com.hornettao.mychat.bean.User;
import com.hornettao.mychat.utils.CollectionUtils;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.ScreenUtils;
import com.hornettao.mychat.view.dialog.DialogTips;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 *提供了更新用户地理位置、更新用户好友列表的方法
 */
public class BaseActivity extends FragmentActivity {

	BmobUserManager userManager;
	BmobChatManager manager;
	
	MyChatApplication mApplication;

	protected int mScreenWidth;
	protected int mScreenHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		mApplication = MyChatApplication.getInstance();
		mScreenWidth = ScreenUtils.getScreenWidth(this);
		mScreenHeight = ScreenUtils.getScreenWidth(this);
	}

	public void showBackAndHidelogo() {
		ActionBar actionBar = getActionBar();
		if (actionBar == null)
			return;
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	/** 显示下线的对话框
	  * showOfflineDialog
	  * @return void
	  * @throws
	  */
	public void showOfflineDialog(final Context context) {
		DialogTips dialog = new DialogTips(this,"您的账号已在其他设备上登录!", "重新登录");
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				MyChatApplication.getInstance().logout();
				startActivity(new Intent(context, WelcomeActivity.class));
				finish();
				dialogInterface.dismiss();
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
	}
	
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	/** 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	  * @Title: updateUserInfos
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void updateUserInfos(){
		//更新地理位置信息
		updateUserLocation();
		//查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		//这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					L.e(arg1);
				} else {
					L.e("查询好友列表失败：" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				// 保存到application中方便比较
				MyChatApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
			}
		});
	}
	/** 更新用户的经纬度信息
	  * @Title: uploadLocation
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void updateUserLocation(){
		if(MyChatApplication.lastPoint!=null){
			String saveLatitude  = mApplication.getLatitude();
			String saveLongtitude = mApplication.getLongtitude();
			String newLat = String.valueOf(MyChatApplication.lastPoint.getLatitude());
			String newLong = String.valueOf(MyChatApplication.lastPoint.getLongitude());
			if(!saveLatitude.equals(newLat)|| !saveLongtitude.equals(newLong)){//只有位置有变化就更新当前位置，达到实时更新的目的
				final User user = (User) userManager.getCurrentUser(User.class);
				user.setLocation(MyChatApplication.lastPoint);
				user.update(this, new UpdateListener() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						MyChatApplication.getInstance().setLatitude(String.valueOf(user.getLocation().getLatitude()));
						MyChatApplication.getInstance().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
					}
					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
					}
				});
			}else{
				L.i("用户位置未发生变化");
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		L.i("item selected==" + item.getItemId());
		if (item.getItemId() == android.R.id.home) {
			L.i("R.id.home");
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();
			} else {
				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
