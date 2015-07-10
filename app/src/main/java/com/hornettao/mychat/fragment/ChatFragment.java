package com.hornettao.mychat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hornettao.mychat.R;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.view.PagerSlidingTabStrip;

/**
 * 聊天Fragment的界面
 */
public class ChatFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		FrameLayout fl = new FrameLayout(getActivity());
//		fl.setLayoutParams(params);
//		DisplayMetrics dm = getResources().getDisplayMetrics();
//		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm);
//		TextView v = new TextView(getActivity());
//		params.setMargins(margin, margin, margin, margin);
//		v.setLayoutParams(params);
//		v.setLayoutParams(params);
//		v.setGravity(Gravity.CENTER);
//		v.setText("聊天界面");
//		v.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
//		fl.addView(v);
//		return fl;
		return inflater.inflate(R.layout.fragment_chat, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Button button = (Button) getActivity().findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				L.i("点击了 ");
				int i = ((PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs)).getChatBadgeViewCount();
				L.i("数目" + i);
				((PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs)).setChatBadgeViewCount(0);
			}
		});
	}
}
