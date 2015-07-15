package com.hornettao.mychat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.hornettao.mychat.R;
import com.hornettao.mychat.adapter.base.TaskListAdapter;
import com.hornettao.mychat.bean.Task;
import com.hornettao.mychat.utils.CollectionUtils;
import com.hornettao.mychat.utils.L;
import com.hornettao.mychat.utils.T;
import com.hornettao.mychat.view.xlist.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class TaskActivity extends Base2Activity implements AdapterView.OnItemClickListener, XListView.IXListViewListener {

    private XListView taskListView;
    private TaskListAdapter taskListAdapter;
    private List<Task> taskList = new ArrayList<>();

    private int pageLimit = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        showBackAndHidelogo();
        setUpView();
        initXListView();
        initSearchList(false);
    }

    private void setUpView() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        L.i("on item click == " + position);
        Task task = (Task) taskListAdapter.getItem(position - 1);
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("task", task);
        startAnimActivity(intent);
    }

    private void initXListView() {
        taskListView = (XListView) this.findViewById(R.id.list_view_task);

        // 首先不允许加载更多
        taskListView.setPullLoadEnable(false);
        // 允许下拉
        taskListView.setPullRefreshEnable(true);
        // 设置监听器
        taskListView.setXListViewListener(this);
        //
        taskListView.pullRefreshing();

        taskListAdapter = new TaskListAdapter(this, taskList);
        taskListView.setAdapter(taskListAdapter);

        taskListView.setOnItemClickListener(this);

    }

    int curPage = 0;
    ProgressDialog progress ;
    private void initSearchList(final boolean isUpdate){
        if(!isUpdate){
            progress = new ProgressDialog(TaskActivity.this);
            progress.setMessage("正在搜索...");
            progress.setCanceledOnTouchOutside(true);
            progress.show();
        }

        BmobQuery<Task> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(pageLimit);
        query.findObjects(this, new FindListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                if (CollectionUtils.isNotNull(list)) {
                    if (isUpdate) {
                        taskList.clear();
                    }
                    taskListAdapter.addAll(list);
                    if (list.size() < pageLimit) {
                        taskListView.setPullLoadEnable(false);
                        T.showShort(TaskActivity.this, "任务搜索完成!");
                    } else {
                        ++curPage;
                        taskListView.setPullLoadEnable(true);
                    }
                } else {
                    L.i("查询成功:无返回值");
                    if (taskList != null) {
                        taskList.clear();
                    }
                    taskListAdapter.notifyDataSetChanged();
                    T.showShort(TaskActivity.this, "用户不存在");
                }
                if (!isUpdate) {
                    progress.dismiss();
                } else {
                    refreshPull();
                }
//                这样能保证每次查询都是从头开始
                curPage = 0;

            }

            @Override
            public void onError(int i, String s) {
                T.showShort(TaskActivity.this, "查询失败:" + s);
                if (taskList != null) {
                    taskList.clear();
                }
                taskListAdapter.notifyDataSetChanged();
                taskListView.setPullLoadEnable(false);
                refreshPull();
                //这样能保证每次查询都是从头开始
                curPage = 0;
            }
        });
    }

    /** 查询更多
     * @Title: queryMoreNearList
     * @Description: TODO
     * @param @param page
     * @return void
     * @throws
     */
    private void queryMoreSearchList(int page){
        BmobQuery<Task> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(pageLimit);
        query.setSkip(page * pageLimit);

        query.findObjects(this, new FindListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                if (CollectionUtils.isNotNull(list)) {
                    taskListAdapter.addAll(list);
                }
                refreshLoad();
            }

            @Override
            public void onError(int i, String s) {
                L.e("搜索更多任务出错:" + s);
                taskListView.setPullLoadEnable(false);
                refreshLoad();
            }
        });
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        initSearchList(true);

    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        BmobQuery<Task> query = new BmobQuery<>();
        query.count(this, Task.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                if (i > taskList.size()) {
                    ++curPage;
                    queryMoreSearchList(curPage);
                } else {
                    T.showShort(TaskActivity.this, "数据加载完成");
                    taskListView.setPullLoadEnable(false);
                    refreshLoad();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("查询任务总数失败:" + s);
                refreshLoad();
            }
        });
    }

    private void refreshLoad(){
        if (taskListView.getPullLoading()) {
            taskListView.stopLoadMore();
        }
    }

    private void refreshPull(){
        if (taskListView.getPullRefreshing()) {
            taskListView.stopRefresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_task) {
            startAnimActivity(AddTaskActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
