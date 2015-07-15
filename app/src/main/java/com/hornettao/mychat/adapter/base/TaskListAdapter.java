package com.hornettao.mychat.adapter.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.Task;
import com.hornettao.mychat.utils.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 查看任务
  */
public class TaskListAdapter extends BaseListAdapter<Task> {

	public TaskListAdapter(Context context, List<Task> list) {
		super(context, list);
		// TODO Auto-generated constructor stub

	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final ScaleAnimation animation_open =new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f);
		final ScaleAnimation animation_closed =new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f);
		animation_closed.setDuration(500);
		animation_open.setDuration(500);
		animation_closed.setFillAfter(true);
		animation_open.setFillAfter(true);
		animation_closed.setFillAfter(false);
		Task task = getList().get(arg0);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_tasklist, null);
		}
		ImageView imageView = ViewHolder.get(convertView, R.id.imageView);
		TextView titleTextView = ViewHolder.get(convertView, R.id.text_view_title);
        TextView positionTextView = ViewHolder.get(convertView, R.id.text_view_position);
        TextView attendTextView = ViewHolder.get(convertView, R.id.text_view_attend);
        TextView totalTextView = ViewHolder.get(convertView, R.id.text_view_total);
		final Button endTimeButton = ViewHolder.get(convertView, R.id.button_end_time);
		final Button endTimeTextView = ViewHolder.get(convertView, R.id.text_view_end_time);

		titleTextView.setText(task.getTitle());
		positionTextView.setText(task.getPosition());
		totalTextView.setText("/" + task.getTotal());
		endTimeTextView.setText(task.getEndTime());
		endTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				endTimeTextView.setVisibility(View.VISIBLE);
				endTimeTextView.startAnimation(animation_open);
				endTimeButton.setVisibility(View.INVISIBLE);
			}
		});
		endTimeTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				endTimeTextView.startAnimation(animation_closed);
				endTimeTextView.setVisibility(View.INVISIBLE);
			}
		});
		animation_closed.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				endTimeButton.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
//		TextView tv_friend_name = ViewHolder.get(convertView, R.id.tv_friend_name);
//		ImageView iv_avatar = ViewHolder.get(convertView, R.id.img_friend_avatar);
		String imagePath = task.getImagePath();
		if (imagePath != null && !imagePath.equals("")) {
			ImageLoader.getInstance().displayImage(imagePath, imageView, ImageLoadOptions.getOptions());
		} else {
			imageView.setImageResource(R.mipmap.ic_launcher);
		}
//		tv_friend_name.setText(contract.getUsername());
		return convertView;
	}

}
