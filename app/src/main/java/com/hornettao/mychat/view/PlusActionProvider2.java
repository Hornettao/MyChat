package com.hornettao.mychat.view;

import android.content.Context;
import android.content.Intent;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

import com.hornettao.mychat.R;
import com.hornettao.mychat.activity.AddFriendActivity;
import com.hornettao.mychat.activity.AddTaskActivity;
import com.hornettao.mychat.utils.L;
import com.zxing.activity.CaptureActivity;

public class PlusActionProvider2 extends ActionProvider {

	private Context context;

	public PlusActionProvider2(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View onCreateActionView() {
		return null;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();
//		subMenu.add(context.getString(R.string.plus_group_chat))
//				.setIcon(R.mipmap.ofm_group_chat_icon)
//				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						return true;
//					}
//				});
		subMenu.add("发状态")
				.setIcon(R.mipmap.add_friend_white)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						context.startActivity(new Intent(context, AddFriendActivity.class));
						return false;
					}
				});
//		subMenu.add(context.getString(R.string.plus_video_chat))
//				.setIcon(R.mipmap.ofm_video_icon)
//				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						return false;
//					}
//				});
		subMenu.add(context.getString(R.string.plus_scan))
				.setIcon(R.mipmap.scan_white)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						context.startActivity(new Intent(context, CaptureActivity.class));
						L.v("scan");
						return false;
					}
				});
//		subMenu.add(context.getString(R.string.plus_take_photo))
//				.setIcon(R.mipmap.ofm_camera_icon)
//				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						return false;
//					}
//				});
		subMenu.add(context.getString(R.string.plus_publish_task))
				.setIcon(R.mipmap.task)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        context.startActivity(new Intent(context, AddTaskActivity.class));
                        return false;
                    }
                });
	}

	@Override
	public boolean hasSubMenu() {
		return true;
	}

}