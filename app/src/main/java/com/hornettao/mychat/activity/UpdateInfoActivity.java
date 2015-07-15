package com.hornettao.mychat.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.User;
import com.hornettao.mychat.utils.T;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 设置昵称和性别
 * 
 * @ClassName: SetNickAndSexActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-7 下午4:03:40
 */
public class UpdateInfoActivity extends Base2Activity {

	EditText edit_nick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_updateinfo);
		showBackAndHidelogo();
		initView();
	}

	private void initView() {
		edit_nick = (EditText) findViewById(R.id.edit_nick);
	}

	/** 修改资料
	  * updateInfo
	  * @Title: updateInfo
	  * @return void
	  * @throws
	  */
	private void updateInfo(String nick) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
		u.setNick(nick);
//		u.setHight(110);
		u.setObjectId(user.getObjectId());
		u.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				final User c = userManager.getCurrentUser(User.class);
				T.showShort(UpdateInfoActivity.this, "修改成功:" + c.getNick());
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				T.showShort(UpdateInfoActivity.this, "onFailure:" + arg1);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_update_info, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_update_nick:
				String nick = edit_nick.getText().toString();
				if (nick.equals("")) {
					T.showShort(UpdateInfoActivity.this, "请填写昵称!");
					break;
				}
				updateInfo(nick);
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
