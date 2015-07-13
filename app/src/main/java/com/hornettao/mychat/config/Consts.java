package com.hornettao.mychat.config;

import android.os.Environment;

/**
 * Created by hornettao on 15/7/10.
 */
public class Consts {


    /**
     * 存放发送图片的目录
     */
    public static String MYCHAT_PICTURE_PATH = Environment.getExternalStorageDirectory()	+ "/mychat/image/";

    /**
     * 我的头像保存目录
     */
    public static String MyAvatarDir = "/sdcard/mychat/avatar/";
    /**
     * 拍照回调
     */
    public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像

    public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
    public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
    public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置
    public static final String EXTRA_STRING = "extra_string";

    public static final String ACTION_LOGIN_SUCCESS_FINISH = "ACTION_LOGIN_SUCCESS_FINISH";
}
