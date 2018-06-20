package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;

/**
 * Created by GA on 2018/1/5.
 */

public class GroupChatActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_group_chat);
    }
}
