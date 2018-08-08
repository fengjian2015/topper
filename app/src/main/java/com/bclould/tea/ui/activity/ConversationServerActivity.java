package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.adapter.ChatServerAdapter;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationServerActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title_name)
    TextView mTitleName;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private DBManager mMgr;
    private String mName;
    private String roomId;
    private LinearLayoutManager mLayoutManager;
    private List<MessageInfo> mMessageList = new ArrayList<>();
    private ChatServerAdapter mChatAdapter;
    private int currentPosition;//記錄刷新位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_server);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);//初始化数据库管理类
        EventBus.getDefault().register(this);//初始化EventBus
        mMgr.updateNumber(roomId, 0);//更新未读消息条数
        EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));//发送更新未读消息通知
        initIntent();
        initAdapter();
        initData(null);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("MessageInfo");
        if (messageInfo == null) {
            return;
        }
        mRefreshLayout.setEnableLoadMore(true);
        //通過傳遞過來的消息，查找
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFist", true);
        bundle.putSerializable("MessageInfo", (Serializable) messageInfo);
        mMessageList.clear();
        Message message = new Message();
        message.obj = bundle;
        message.what = 4;
        handler.sendMessage(message);
    }

    private void initIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mName = bundle.getString("name");
        roomId = bundle.getString("roomId");
        clearNotification();
        setTitleName();
    }

    private void setTitleName() {
        mTitleName.setText(mName);
    }

    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.msg_database_update)) {
            initData(event.getId());
            mMgr.updateNumber(roomId, 0);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
        }
    }


    //初始化数据
    private void initData(String msgId) {
        if(StringUtils.isEmpty(msgId)){
            List<MessageInfo> messageInfos = mMgr.queryMessage(roomId,0);
            mMessageList.clear();
            mMessageList.addAll(messageInfos);
            mChatAdapter.notifyDataSetChanged();
            mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
        }else{
            MessageInfo messageInfo=mMgr.queryMessageMsg(msgId);
            if(roomId.equals(messageInfo.getUsername())) {
                mMessageList.add(messageInfo);
                mChatAdapter.notifyItemInserted(mMessageList.indexOf(messageInfo));
                mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
            }
        }
    }

    //初始化适配器
    private void initAdapter() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mChatAdapter = new ChatServerAdapter(this, mMessageList);
        mRecyclerView.setAdapter(mChatAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                handler.sendEmptyMessage(0);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFist", false);
                Message message = new Message();
                message.obj = bundle;
                message.what = 4;
                handler.sendMessage(message);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mRefreshLayout.finishRefresh();
                    //下拉查询历史消息
                    if (mMessageList.size() == 0) return;
                    currentPosition=mMessageList.size();
                    int position = mLayoutManager.findFirstVisibleItemPosition();
                    View view = mRecyclerView.getChildAt(position);
                    int top = 0;
                    if (view != null) {
                        top = view.getTop();
                    }
                    List<MessageInfo> messageInfos = mMgr.queryRefreshMessage(roomId, mMessageList.get(0).getCreateTime(),0);
                    mMessageList.addAll(0,messageInfos);
                    currentPosition=mMessageList.size()-currentPosition;
                    mChatAdapter.notifyDataSetChanged();
                    mLayoutManager.scrollToPositionWithOffset(currentPosition,top);
                    break;
                case 4:
                    mRefreshLayout.finishLoadMore();
                    //上啦加載
//                    if (mMessageList.size() == 0) return;
                    Bundle bundle3 = (Bundle) msg.obj;
                    boolean isFist = bundle3.getBoolean("isFist");
                    List<MessageInfo> messageInfos1 = null;
                    if (isFist) {
                        MessageInfo messageInfo = (MessageInfo) bundle3.getSerializable("MessageInfo");
                        messageInfos1 = mMgr.queryLoadMessage(roomId, messageInfo.getCreateTime(), isFist,0);
                    } else {
                        if (mMessageList.size() == 0) {
                            messageInfos1 = mMgr.queryLoadMessage(roomId, mMessageList.get(0).getCreateTime(), isFist,0);
                        } else {
                            messageInfos1 = mMgr.queryLoadMessage(roomId, mMessageList.get(mMessageList.size() - 1).getCreateTime(), isFist,0);
                        }
                    }
                    if(messageInfos1.size()<=0){
                        mRefreshLayout.setEnableLoadMore(false);
                    }
                    List<MessageInfo> MessageList3 = new ArrayList<>();
                    MessageList3.addAll(mMessageList);
                    MessageList3.addAll(messageInfos1);
                    mMessageList.clear();
                    mMessageList.addAll(MessageList3);
                    mChatAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
        }
    }
}
