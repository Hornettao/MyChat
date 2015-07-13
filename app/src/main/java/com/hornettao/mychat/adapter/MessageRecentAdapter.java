package com.hornettao.mychat.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.hornettao.mychat.R;
import com.hornettao.mychat.adapter.base.ViewHolder;
import com.hornettao.mychat.utils.FaceTextUtils;
import com.hornettao.mychat.utils.ImageLoadOptions;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.FindListener;

/**
 * 会话适配器
 * Created by hornettao on 15/7/12.
 */
public class MessageRecentAdapter extends ArrayAdapter<BmobRecent> implements Filterable {

    private LayoutInflater inflater;
    private List<BmobRecent> mData;
    private Context mContext;
    private BmobUserManager bmobUserManager;

    public MessageRecentAdapter(Context context, int textViewResourceId, List<BmobRecent> objects) {
        super(context, textViewResourceId, objects);
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        mData = objects;
        bmobUserManager = BmobUserManager.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final BmobRecent item = mData.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_conversation, parent, false);
        }
        final ImageView iv_recent_avatar = ViewHolder.get(convertView, R.id.iv_recent_avatar);
        TextView tv_recent_name = ViewHolder.get(convertView, R.id.tv_recent_name);
        TextView tv_recent_msg = ViewHolder.get(convertView, R.id.tv_recent_msg);
        TextView tv_recent_time = ViewHolder.get(convertView, R.id.tv_recent_time);
        TextView tv_recent_unread = ViewHolder.get(convertView, R.id.tv_recent_unread);

        bmobUserManager.queryUserById(item.getTargetid(), new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                String url = list.get(0).getAvatar();
                L.i("recent adapter", url);
                String avatar = url;
                if(avatar!=null&& !avatar.equals("")){
                    ImageLoader.getInstance().displayImage(avatar, iv_recent_avatar, ImageLoadOptions.getOptions());
                }else{
                    iv_recent_avatar.setImageResource(R.mipmap.ic_launcher);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });




        tv_recent_name.setText(item.getUserName());
        tv_recent_time.setText(TimeUtil.getChatTime(item.getTime()));
        //显示内容
        if(item.getType()==BmobConfig.TYPE_TEXT){
            SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, item.getMessage());
            tv_recent_msg.setText(spannableString);
        }else if(item.getType()==BmobConfig.TYPE_IMAGE){
            tv_recent_msg.setText("[图片]");
        }else if(item.getType()==BmobConfig.TYPE_LOCATION){
            String all =item.getMessage();
            if(TextUtils.isEmpty(all)){//位置类型的信息组装格式：地理位置&维度&经度
                String address = all.split("&")[0];
                tv_recent_msg.setText("[位置]"+address);
            }
        }else if(item.getType()==BmobConfig.TYPE_VOICE){
            tv_recent_msg.setText("[语音]");
        }

        int num = BmobDB.create(mContext).getUnreadCount(item.getTargetid());
        if (num > 0) {
            tv_recent_unread.setVisibility(View.VISIBLE);
            tv_recent_unread.setText(num + "");
        } else {
            tv_recent_unread.setVisibility(View.GONE);
        }
        return convertView;
    }

}

