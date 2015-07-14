package com.hornettao.mychat.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.User;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.T;
import com.zxing.encoding.EncodingHandler;

import cn.bmob.v3.BmobUser;

public class MyQRCodeActivity extends Base2Activity {

    private String userName;

    private ImageView QRCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);
        showBackAndHidelogo();
        User user = BmobUser.getCurrentUser(this, User.class);
        userName = user.getUsername();
        L.i(user.getObjectId());
        setUpView();
        generateQRCode();
    }

    private void setUpView() {
        QRCodeImageView = (ImageView) this.findViewById(R.id.image_view_qr_code);
    }

    private void generateQRCode() {
        try {
            if (!userName.equals("")) {
                //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                Bitmap qrCodeBitmap = EncodingHandler.createQRCode(userName, 350);
                QRCodeImageView.setImageBitmap(qrCodeBitmap);
            }else {
                T.showShort(MyQRCodeActivity.this, "Text can not be empty");
            }

        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
