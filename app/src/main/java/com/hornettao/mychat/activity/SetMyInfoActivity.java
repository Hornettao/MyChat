package com.hornettao.mychat.activity;

/**
 * Created by hornettao on 15/7/11.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hornettao.mychat.MyChatApplication;
import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.User;
import com.hornettao.mychat.config.Consts;
import com.hornettao.mychat.utils.CollectionUtils;
import com.hornettao.mychat.utils.ImageLoadOptions;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.PhotoUtil;
import com.hornettao.mychat.utils.T;
import com.hornettao.mychat.view.dialog.DialogTips;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 个人资料页面
 *
 * @ClassName: SetMyInfoActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-10 下午2:55:19
 */
@SuppressLint("SimpleDateFormat")
public class SetMyInfoActivity extends Base2Activity implements View.OnClickListener {

    TextView tv_set_name, tv_set_nick, tv_set_gender;
    ImageView iv_set_avator, iv_arraw, iv_nickarraw;
    LinearLayout layout_all;

    Button btn_chat, btn_back, btn_add_friend;
    RelativeLayout layout_head, layout_nick, layout_gender, layout_black_tips;

    String from = "";
    String username = "";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 14) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(R.layout.activity_set_info);
        showBackAndHidelogo();
        from = getIntent().getStringExtra("from");//me add other
        username = getIntent().getStringExtra("username");
        initView();
    }

    private void initView() {
        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
        iv_arraw = (ImageView) findViewById(R.id.iv_arraw);
        iv_nickarraw = (ImageView) findViewById(R.id.iv_nickarraw);
        tv_set_name = (TextView) findViewById(R.id.tv_set_name);
        tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
        layout_head = (RelativeLayout) findViewById(R.id.layout_head);
        layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
        layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
        // 黑名单提示语Ø
        layout_black_tips = (RelativeLayout) findViewById(R.id.layout_black_tips);
        tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        btn_add_friend.setEnabled(false);
        btn_chat.setEnabled(false);
        btn_back.setEnabled(false);
        //来自我的，进入的是自己的信息页面
        if (from.equals("me")) {
            layout_head.setOnClickListener(this);
            layout_nick.setOnClickListener(this);
            layout_gender.setOnClickListener(this);
            iv_nickarraw.setVisibility(View.VISIBLE);
            iv_arraw.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
            btn_add_friend.setVisibility(View.GONE);
        } else {
            iv_nickarraw.setVisibility(View.INVISIBLE);
            iv_arraw.setVisibility(View.INVISIBLE);
            //不管对方是不是你的好友，均可以发送消息--BmobIM_V1.1.2修改
            btn_chat.setVisibility(View.VISIBLE);
            btn_chat.setOnClickListener(this);
            if (from.equals("add")) {// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
                if (mApplication.getContactList().containsKey(username)) {// 是好友
//					btn_chat.setVisibility(View.VISIBLE);
//					btn_chat.setOnClickListener(this);
                    btn_back.setVisibility(View.VISIBLE);
                    btn_back.setOnClickListener(this);
                } else {
//					btn_chat.setVisibility(View.GONE);
                    btn_back.setVisibility(View.GONE);
                    btn_add_friend.setVisibility(View.VISIBLE);
                    btn_add_friend.setOnClickListener(this);
                }
            } else {// 查看他人
//				btn_chat.setVisibility(View.VISIBLE);
//				btn_chat.setOnClickListener(this);
                btn_back.setVisibility(View.VISIBLE);
                btn_back.setOnClickListener(this);
            }
            initOtherData(username);
        }
    }

    private void initMeData() {
        User user = userManager.getCurrentUser(User.class);
        initOtherData(user.getUsername());
    }

    private void initOtherData(String name) {
        userManager.queryUser(name, new FindListener<User>() {

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                L.e("onError onError:" + arg1);
            }

            @Override
            public void onSuccess(List<User> arg0) {
                // TODO Auto-generated method stub
                if (arg0 != null && arg0.size() > 0) {
                    user = arg0.get(0);
                    btn_chat.setEnabled(true);
                    btn_back.setEnabled(true);
                    btn_add_friend.setEnabled(true);
                    updateUser(user);
                } else {
                    L.e("onSuccess 查无此人");
                }
            }
        });
    }

    private void updateUser(User user) {
        // 更改
        refreshAvatar(user.getAvatar());
        tv_set_name.setText(user.getUsername());
        tv_set_nick.setText(user.getNick());
        if (user.getSex() != null) {
            tv_set_gender.setText(user.getSex() ? "男" : "女");
        } else {
            tv_set_gender.setText("保密");
        }
        // 检测是否为黑名单用户
        if (from.equals("other")) {
            if (BmobDB.create(this).isBlackUser(user.getUsername())) {
                btn_back.setVisibility(View.GONE);
                layout_black_tips.setVisibility(View.VISIBLE);
            } else {
                btn_back.setVisibility(View.VISIBLE);
                layout_black_tips.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新头像 refreshAvatar
     *
     * @return void
     * @throws
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
                    ImageLoadOptions.getOptions());
        } else {
            iv_set_avator.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (from.equals("me")) {
            initMeData();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_chat:// 发起聊天
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("user", user);
                startAnimActivity(intent);
                finish();
                break;
            case R.id.layout_head:
                showAvatarPop();
                break;
            case R.id.layout_nick:
                startAnimActivity(UpdateInfoActivity.class);
                break;
            case R.id.layout_gender:// 性别
                showSexChooseDialog();
                break;
            case R.id.btn_back:// 黑名单
                showBlackDialog(user.getUsername());
                break;
            case R.id.btn_add_friend:// 添加好友
                addFriend();
                break;
        }
    }
    String[] sexs = new String[]{ "男", "女" };
    private void showSexChooseDialog() {
        new AlertDialog.Builder(this)
                .setTitle("单选框")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(sexs, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                L.i("点击的是"+sexs[which]);
                                updateInfo(which);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消", null)
                .show();
    }


    /** 修改资料
     * updateInfo
     * @Title: updateInfo
     * @return void
     * @throws
     */
    private void updateInfo(int which) {
        final User user = userManager.getCurrentUser(User.class);
        L.i("updateInfo 性别：" + user.getSex());
        if(which==0){
            user.setSex(true);
        }else{
            user.setSex(false);
        }
        user.update(this, new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                T.showShort(SetMyInfoActivity.this, "修改成功");
                final User u = userManager.getCurrentUser(User.class);
                L.i("修改成功后的sex = "+u.getSex());
                tv_set_gender.setText(user.getSex() == true ? "男" : "女");
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                T.showShort(SetMyInfoActivity.this, "onFailure:" + arg1);
            }
        });
    }
    /**
     * 添加好友请求
     *
     * @Title: addFriend
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void addFriend() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // 发送tag请求
        BmobChatManager.getInstance(this).sendTagMessage(MsgTag.ADD_CONTACT,
                user.getObjectId(), new PushListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        T.showShort(SetMyInfoActivity.this, "发送请求成功，等待对方验证！");
                    }

                    @Override
                    public void onFailure(int arg0, final String arg1) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        T.showShort(SetMyInfoActivity.this, "发送请求失败:" + arg1);
                    }
                });
    }

    /**
     * 显示黑名单提示框
     *
     * @Title: showBlackDialog
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void showBlackDialog(final String username) {
        DialogTips dialog = new DialogTips(this, "加入黑名单",
                "加入黑名单，你将不再收到对方的消息，确定要继续吗？", "确定", true, true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                // 添加到黑名单列表
                userManager.addBlack(username, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        T.showShort(SetMyInfoActivity.this, "黑名单添加成功!");
                        btn_back.setVisibility(View.GONE);
                        layout_black_tips.setVisibility(View.VISIBLE);
                        // 重新设置下内存中保存的好友列表
                        MyChatApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        T.showShort(SetMyInfoActivity.this, "黑名单添加失败:" + arg1);
                    }
                });
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;

    public String filePath = "";

    private void showAvatarPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                L.i("点击拍照");
                // TODO Auto-generated method stub
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(
                        R.mipmap.pop_bg_press));
                File dir = new File(Consts.MyAvatarDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 原图
                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date()));
                filePath = file.getAbsolutePath();// 获取相片的保存路径
                Uri imageUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,
                        Consts.REQUESTCODE_UPLOADAVATAR_CAMERA);
            }
        });
        layout_choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                L.i("点击相册");
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_choose.setBackgroundDrawable(getResources().getDrawable(
                        R.mipmap.pop_bg_press));
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,
                        Consts.REQUESTCODE_UPLOADAVATAR_LOCATION);
            }
        });

        avatorPop = new PopupWindow(view, mScreenWidth, 600);
        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }

    /**
     * @Title: startImageAction
     * @return void
     * @throws
     */
    private void startImageAction(Uri uri, int outputX, int outputY,
                                  int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    Bitmap newBitmap;
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Consts.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        T.showShort(SetMyInfoActivity.this, "SD不可用");
                        return;
                    }
                    isFromCamera = true;
                    File file = new File(filePath);
                    degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                    Log.i("life", "拍照后的角度：" + degree);
                    startImageAction(Uri.fromFile(file), 200, 200,
                            Consts.REQUESTCODE_UPLOADAVATAR_CROP, true);
                }
                break;
            case Consts.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                Uri uri = null;
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        T.showShort(SetMyInfoActivity.this, "SD不可用");
                        return;
                    }
                    isFromCamera = false;
                    uri = data.getData();
                    startImageAction(uri, 200, 200,
                            Consts.REQUESTCODE_UPLOADAVATAR_CROP, true);
                } else {
                    T.showShort(SetMyInfoActivity.this, "照片获取失败");
                }

                break;
            case Consts.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                // TODO sent to crop
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                if (data == null) {
                    // Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    saveCropAvator(data);
                }
                // 初始化文件路径
                filePath = "";
                // 上传头像
                uploadAvatar();
                break;
            default:
                break;

        }
    }

    private void uploadAvatar() {
        L.i("头像地址：" + path);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
                // 更新BmobUser对象
                updateUserAvatar(url);
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int arg0, String msg) {
                // TODO Auto-generated method stub
                T.showShort(SetMyInfoActivity.this, "头像上传失败：" + msg);
            }
        });
    }

    private void updateUserAvatar(final String url) {
        User user = (User) userManager.getCurrentUser(User.class);
        user.setAvatar(url);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                T.showShort(SetMyInfoActivity.this, "头像更新成功！");
                // 更新头像
                refreshAvatar(url);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                T.showShort(SetMyInfoActivity.this, "头像更新失败：" + msg);
            }
        });
    }

    String path;

    /**
     * 保存裁剪的头像
     *
     * @param data
     */
    private void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            L.i("avatar - bitmap = " + bitmap);
            if (bitmap != null) {
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                iv_set_avator.setImageBitmap(bitmap);
                // 保存图片
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date());
                path = Consts.MyAvatarDir + filename;
                PhotoUtil.saveBitmap(Consts.MyAvatarDir, filename,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
