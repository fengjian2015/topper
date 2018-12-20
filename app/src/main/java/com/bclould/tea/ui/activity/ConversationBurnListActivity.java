package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBConversationBurnManage;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.ui.adapter.ConversationBurnListAdapter;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationBurnListActivity extends BaseActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;

    private ConversationBurnListAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;

    private RefreshList mRefreshList;
    private DBManager mDBManager;
    private DBConversationBurnManage mDBConversationBurnManage;
    private List<ConversationInfo> showlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_burn_list);
        ButterKnife.bind(this);
        setTitle(getString(R.string.burn_after_reading),R.mipmap.icon_nav_add);
        EventBus.getDefault().register(this);//初始化EventBus
        mDBManager=new DBManager(this);
        mDBConversationBurnManage=new DBConversationBurnManage(this);
        initRecyclerView();
        initData();
    }

    private void initRecyclerView() {
        if (mRecyclerView == null) return;
        mRefreshList = new RefreshList();
        mAdapter = new ConversationBurnListAdapter(this,showlist,mDBConversationBurnManage,mDBManager);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        if (mRecyclerView != null) {
            mRefreshList.run();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    class RefreshList implements Runnable {
        @Override
        public void run() {
            synchronized (mRecyclerView) {
                List<ConversationInfo> conversationInfos = mDBConversationBurnManage.queryConversation();
                showlist.removeAll(showlist);
                showlist.addAll(conversationInfos);
                sort();
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.oneself_send_msg)) {
            initData();
        } else if (msg.equals(EventBusUtil.dispose_unread_msg)) {
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.refresh_msg_number));
            initData();
        } if (msg.equals(EventBusUtil.delete_friend)) {
            initData();
        } if (msg.equals(getString(R.string.refresh_the_interface))) {
            initData();
        }if (msg.equals(getString(R.string.home_msg_click_two))) {
            unNumbertopList();
        } else if (msg.equals(getString(R.string.refresh))) {
            mHandler.sendEmptyMessage(0);
        }
    }

    private void unNumbertopList() {
        A:for (int i = 0; i < showlist.size(); i++) {
            if (showlist.get(i).getNumber() > 0) {
                linearLayoutManager.scrollToPositionWithOffset(i, 0);
                break A;
            }
        }
    }

    private void sort() {
        //将showlist按时间排成倒序
        Collections.sort(showlist, new Comparator<ConversationInfo>() {
            @Override
            public int compare(ConversationInfo conversationInfo, ConversationInfo conversationInfo2) {
                long at = conversationInfo.getCreateTime();
                long bt = conversationInfo2.getCreateTime();
                if (bt > at) {
                    return 1;
                } else if (bt < at) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (showlist.size() == 0) {
                        mLlNoData.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mLlNoData.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                    break;

            }
        }
    };

    @OnClick({R.id.iv_more, R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_else:
                goSelectFriends();
                break;
        }
    }

    private void goSelectFriends() {
        Intent intent=new Intent(this,SelectFriendGetActivity.class);
        intent.putExtra("type",1);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//初始化EventBus
    }
}
