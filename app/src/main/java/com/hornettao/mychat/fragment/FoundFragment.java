package com.hornettao.mychat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hornettao.mychat.R;
import com.hornettao.mychat.activity.FriendCircleActivity;
import com.hornettao.mychat.activity.TaskActivity;
import com.zxing.activity.CaptureActivity;

/**
 * 发现Fragment的界面
 */
public class FoundFragment extends BaseFragment implements View.OnClickListener {

	private RelativeLayout friendRelativeLayout;
	private RelativeLayout taskRelativeLayout;
	private RelativeLayout scanRelativeLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_found, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setUpView();
	}

	private void setUpView() {
		friendRelativeLayout = (RelativeLayout) findViewById(R.id.layout_friend_circle);
		friendRelativeLayout.setOnClickListener(this);
		taskRelativeLayout = (RelativeLayout) findViewById(R.id.layout_task);
		taskRelativeLayout.setOnClickListener(this);
		scanRelativeLayout = (RelativeLayout) findViewById(R.id.layout_scan);
		scanRelativeLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.layout_friend_circle:
				startAnimActivity(FriendCircleActivity.class);
				break;
			case R.id.layout_task:
				startAnimActivity(TaskActivity.class);
				break;
			case R.id.layout_scan:
				startAnimActivity(CaptureActivity.class);
				break;
			default:
				break;
		}
	}
}
