package com.hornettao.mychat.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.Task;
import com.hornettao.mychat.bean.User;
import com.hornettao.mychat.config.Consts;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.PhotoUtil;
import com.hornettao.mychat.utils.T;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class AddTaskActivity extends Base2Activity implements View.OnClickListener, View.OnTouchListener {

    private LinearLayout layout_all;
    private EditText titleEditText;
    private EditText startTimeButton;
    private EditText endTimeButton;
    private EditText totalEditText;
    private EditText positionEditText;
    private EditText contentEditText;
    private ImageView imageView;
    private Button addTaskButton;

    private RelativeLayout layout_choose;
    private RelativeLayout layout_photo;
    private PopupWindow avatorPop;

    private String path = "";
    private  String filePath = "";

    Bitmap newBitmap;
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;


    String title = "";
    String startTime = "";
    String endTime = "";
    Integer total = 0;
    String position = "";
    String content = "";
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        showBackAndHidelogo();
        setUpView();
    }

    private void setUpView() {
        layout_all = (LinearLayout) this.findViewById(R.id.layout_all);
        titleEditText = (EditText) this.findViewById(R.id.text_view_title);
        startTimeButton = (EditText) this.findViewById(R.id.text_view_start_time);
        endTimeButton = (EditText) this.findViewById(R.id.text_view_end_time);
        totalEditText = (EditText) this.findViewById(R.id.text_view_total);
        positionEditText = (EditText) this.findViewById(R.id.text_view_position);
        contentEditText = (EditText) this.findViewById(R.id.text_view_content);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        addTaskButton = (Button) this.findViewById(R.id.button_add_task);
        startTimeButton.setOnTouchListener(this);
        endTimeButton.setOnTouchListener(this);
        addTaskButton.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.activity_add_task_new, null);
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);
            builder.setView(view);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(Calendar.MINUTE);

            if (v.getId() == R.id.text_view_start_time) {
                final int inType = startTimeButton.getInputType();
                startTimeButton.setInputType(InputType.TYPE_NULL);
                startTimeButton.onTouchEvent(event);
                startTimeButton.setInputType(inType);
                startTimeButton.setSelection(startTimeButton.getText().length());

                builder.setTitle("选取起始时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        sb.append("  ");
                        sb.append(timePicker.getCurrentHour())
                                .append(":").append(timePicker.getCurrentMinute());

                        startTimeButton.setText(sb);
//                        endTimeButton.requestFocus();

                        dialog.cancel();
                    }
                });

            } else if (v.getId() == R.id.text_view_end_time) {
                int inType = endTimeButton.getInputType();
                endTimeButton.setInputType(InputType.TYPE_NULL);
                endTimeButton.onTouchEvent(event);
                endTimeButton.setInputType(inType);
                endTimeButton.setSelection(endTimeButton.getText().length());

                builder.setTitle("选取结束时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        sb.append("  ");
                        sb.append(timePicker.getCurrentHour())
                                .append(":").append(timePicker.getCurrentMinute());
                        endTimeButton.setText(sb);

                        dialog.cancel();
                    }
                });
            }

            Dialog dialog = builder.create();
            dialog.show();
        }

        return true;
    }

    private void addTask() {
        title = titleEditText.getText().toString();
        startTime = startTimeButton.getText().toString();
        endTime = endTimeButton.getText().toString();
        total = Integer.parseInt(totalEditText.getText().toString());
        position = positionEditText.getText().toString();
        content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(title)) {
            T.showShort(this, "标题不能为空");
            return;
        }
        if (TextUtils.isEmpty(startTime)) {
            T.showShort(this, "开始时间不能为空");
            return;
        }
        if (TextUtils.isEmpty(endTime)) {
            T.showShort(this, "结束时间不能为空");
            return;
        }
        if (total == 0) {
            T.showShort(this, "总人数不能为0");
            return;
        }
        if (TextUtils.isEmpty(position)) {
            T.showShort(this, "地点不能为空");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            T.showShort(this, "详细内容不能为空");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            T.showShort(this, "图片不能为空");
            return;
        }
        final Task task = new Task(title, startTime, endTime, total, position, content);
        task.setAuthor(BmobUser.getCurrentUser(this, User.class));
        task.setImagePath(url);
        task.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                T.showShort(AddTaskActivity.this, "发布成功");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                T.showShort(AddTaskActivity.this, "失败");
            }
        });
    }

    private void showAvatarPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
                null);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatorPop.dismiss();
            }
        });
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
                File dir = new File(Consts.MyTaskDir);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Consts.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        T.showShort(AddTaskActivity.this, "SD不可用");
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
                        T.showShort(AddTaskActivity.this, "SD不可用");
                        return;
                    }
                    isFromCamera = false;
                    uri = data.getData();
                    startImageAction(uri, 200, 200,
                            Consts.REQUESTCODE_UPLOADAVATAR_CROP, true);
                } else {
                    T.showShort(AddTaskActivity.this, "照片获取失败");
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
                uploadTaskImage();
                break;
            default:
                break;

        }
    }

    private void uploadTaskImage() {
        L.i("path==" + path);
        L.i("file==" + filePath);
        BTPFileResponse response = BmobProFile.getInstance(this).upload(path, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                url = bmobFile.getUrl();
                L.i("success" + url);
//                saveTask(url);
            }

            @Override
            public void onProgress(int i) {
                L.i("progress");
            }

            @Override
            public void onError(int i, String s) {
                L.i("error" + s);
            }
        });
    }

//    private void saveTask(final String url) {
//        final Task task = new Task(title, startTime, endTime, total, position, content);
//        task.setAuthor(BmobUser.getCurrentUser(this, User.class));
//        task.setImagePath(url);
////        for (int i = 0; i < 20; i++) {
////            task.setTitle("No."+i);
//        task.save(this, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                T.showShort(AddTaskActivity.this, "发布成功");
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                T.showShort(AddTaskActivity.this, "失败");
//            }
//        });
//    }

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
                imageView.setImageBitmap(bitmap);
                // 保存图片
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date())+".png";
                path = Consts.MyTaskDir + filename;
                PhotoUtil.saveBitmap(Consts.MyTaskDir, filename,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_task:
                addTask();
                break;
            case R.id.imageView:
                showAvatarPop();
            default:
                break;
        }
    }
}
