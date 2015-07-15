package com.hornettao.mychat.view;

import android.content.Context;
import android.content.Intent;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

import com.hornettao.mychat.R;
import com.hornettao.mychat.activity.AddTaskActivity;
import com.hornettao.mychat.utils.L;
import com.zxing.activity.CaptureActivity;

public class PlusActionProvider extends ActionProvider {

	private Context context;

	public PlusActionProvider(Context context) {
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
		subMenu.add(context.getString(R.string.plus_group_chat))
				.setIcon(R.mipmap.ofm_group_chat_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return true;
					}
				});
		subMenu.add(context.getString(R.string.plus_add_friend))
				.setIcon(R.mipmap.ofm_add_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});
		subMenu.add(context.getString(R.string.plus_video_chat))
				.setIcon(R.mipmap.ofm_video_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});
		subMenu.add(context.getString(R.string.plus_scan))
				.setIcon(R.mipmap.ofm_qrcode_icon)
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
				.setIcon(R.mipmap.ofm_camera_icon)
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