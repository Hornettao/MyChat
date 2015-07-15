package com.hornettao.mychat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hornettao.mychat.R;
import com.hornettao.mychat.activity.FriendCircleActivity;

/**
 * 发现Fragment的界面
 */
public class FoundFragment extends BaseFragment implements View.OnClickListener {

	private Button button;
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
		button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button:
				startAnimActivity(FriendCircleActivity.class);
				break;
			default:
				break;
		}
	}
}
