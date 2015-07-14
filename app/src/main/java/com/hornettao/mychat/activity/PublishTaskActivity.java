package com.hornettao.mychat.activity;

import android.os.Bundle;

import com.hornettao.mychat.R;

public class PublishTaskActivity extends Base2Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_task);
        showBackAndHidelogo();
    }

}
