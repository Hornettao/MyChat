package com.hornettao.mychat.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hornettao.mychat.R;
import com.hornettao.mychat.adapter.NewFriendAdapter;
import com.hornettao.mychat.view.dialog.DialogTips;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

/**
 * 新的朋友
 */
public class NewFriendActivity extends Base2Activity implements AdapterView.OnItemLongClickListener {

    private ListView listview;

    private NewFriendAdapter adapter;

    private String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        from = getIntent().getStringExtra("from");
        initView();
    }

    private void initView() {
        listview = (ListView)findViewById(R.id.list_newfriend);
        listview.setOnItemLongClickListener(this);
        adapter = new NewFriendAdapter(this, BmobDB.create(this).queryBmobInviteList());
        listview.setAdapter(adapter);
        if(from==null){//若来自通知栏的点击，则定位到最后一条
            listview.setSelection(adapter.getCount());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
                                   long arg3) {
        // TODO Auto-generated method stub
        BmobInvitation invite = (BmobInvitation) adapter.getItem(position);
        showDeleteDialog(position,invite);
        return true;
    }

    public void showDeleteDialog(final int position,final BmobInvitation invite) {
        DialogTips dialog = new DialogTips(this,invite.getFromname(),"删除好友请求", "确定",true,true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                deleteInvite(position,invite);
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    /**
     * 删除请求
     * deleteRecent
     * @param @param recent
     * @return void
     * @throws
     */
    private void deleteInvite(int position, BmobInvitation invite){
        adapter.remove(position);
        BmobDB.create(this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
    }
}
