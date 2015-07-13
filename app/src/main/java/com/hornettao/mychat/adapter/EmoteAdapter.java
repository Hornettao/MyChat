package com.hornettao.mychat.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hornettao.mychat.R;
import com.hornettao.mychat.adapter.base.BaseArrayListAdapter;
import com.hornettao.mychat.bean.FaceText;
import com.hornettao.mychat.utils.L;

import java.util.List;

/**
 * Created by hornettao on 15/7/12.
 */
public class EmoteAdapter extends BaseArrayListAdapter {

    public EmoteAdapter(Context context, List<FaceText> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_face_text, null);
            holder = new ViewHolder();
            holder.mIvImage = (ImageView) convertView
                    .findViewById(R.id.v_face_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FaceText faceText = (FaceText) getItem(position);
        String key = faceText.text.substring(1);
        L.i("facetext key", key);
        Drawable drawable =mContext.getResources().getDrawable(mContext.getResources().getIdentifier(key, "mipmap", mContext.getPackageName()));
        holder.mIvImage.setImageDrawable(drawable);
        return convertView;
    }

    class ViewHolder {
        ImageView mIvImage;
    }
}
