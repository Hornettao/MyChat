package com.hornettao.mychat.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hornettao.mychat.R;
import com.hornettao.mychat.bean.Task;
import com.hornettao.mychat.bean.User;
import com.hornettao.mychat.utils.ImageLoadOptions;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.T;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class TaskDetailActivity extends Base2Activity implements View.OnClickListener {

    private ImageView imageView;
    private TextView titleTextView;
    private TextView attendTextView;
    private TextView totalTextView;
    private TextView positionTextView;
    private EditText startTimeEditText;
    private EditText endTimeEditText;
    private EditText contentEditText;
    private Button inButton;
    private Button outButton;

    private Task task;
    private User user;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        showBackAndHidelogo();
        user = BmobUser.getCurrentUser(this, User.class);
        task = (Task) getIntent().getSerializableExtra("task");
        setUpView();
    }

    private void setUpView() {
        imageView = (ImageView) this.findViewById(R.id.imageView);
        titleTextView = (TextView) this.findViewById(R.id.text_view_title);
        attendTextView = (TextView) this.findViewById(R.id.text_view_attend);
        totalTextView = (TextView) this.findViewById(R.id.text_view_total);
        positionTextView = (TextView) this.findViewById(R.id.text_view_position);
        startTimeEditText = (EditText) this.findViewById(R.id.edit_text_start_time);
        endTimeEditText = (EditText) this.findViewById(R.id.edit_text_end_time);
        contentEditText = (EditText) this.findViewById(R.id.edit_text_content);
        inButton = (Button) this.findViewById(R.id.button_in);
        outButton = (Button) this.findViewById(R.id.button_out);
        inButton.setOnClickListener(this);
        outButton.setOnClickListener(this);
        queryAddUsers();
        setData();
    }

    private void setData() {
        String imagePath = task.getImagePath();
        if (imagePath != null && !imagePath.equals("")) {
            ImageLoader.getInstance().displayImage(imagePath, imageView, ImageLoadOptions.getOptions());
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
        titleTextView.setText(task.getTitle());
        totalTextView.setText("/" + task.getTotal());
        positionTextView.setText(task.getPosition());
        startTimeEditText.setText(task.getStartTime());
        endTimeEditText.setText(task.getEndTime());
        contentEditText.setText(task.getContent());
        L.i("user" + user.getObjectId());
        L.i("authot" + task.getAuthor().getObjectId());
//        if (!user.getObjectId().equals(task.getAuthor().getObjectId())) {
//            inButton.setVisibility(View.VISIBLE);
//        }
    }

    private void in() {
        L.i(task.getObjectId());
        Task newTask = new Task();
        newTask.setObjectId(task.getObjectId());
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        task.setMember(relation);
        task.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                T.showShort(TaskDetailActivity.this, "成功参加");
                inButton.setVisibility(View.GONE);
                queryAddUsers();
            }

            @Override
            public void onFailure(int i, String s) {
                T.showShort(TaskDetailActivity.this, "参加失败");
                L.e(s);
            }
        });
    }

    private void queryAddUsers() {
        BmobQuery<User> query = new BmobQuery<>();
        Task newTask2 = new Task();
        newTask2.setObjectId(task.getObjectId());
        query.addWhereRelatedTo("member", new BmobPointer(newTask2));
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                L.i("个数" + list.size());
                userList = list;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        attendTextView.setText(userList.size() + "");
                        Boolean flag = false;
                        for (User u : userList) {
                            if (u.getObjectId().equals(user.getObjectId())) {
                                L.i("contains");
                                inButton.setVisibility(View.GONE);
                                flag = true;
                                return;
                            }
                        }
                        if (!flag && !user.getObjectId().equals(task.getAuthor().getObjectId()) && userList.size() <= task.getTotal()) {
                            inButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_in:
                in();
                break;
            case R.id.button_out:
                break;
            default:
                break;
        }
    }
}
