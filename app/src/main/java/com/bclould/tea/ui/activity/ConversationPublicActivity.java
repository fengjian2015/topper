package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBPublicManage;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.adapter.PublicAdapter;
import com.bclould.tea.ui.widget.MenuLinearLayout;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.meitu.scanimageview.ScanPhotoView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationPublicActivity extends BaseActivity {


    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.iv_image)
    ScanPhotoView mIvImage;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv_keyboard)
    ImageView mIvKeyboard;
    @Bind(R.id.ll_menu)
    MenuLinearLayout mLlMenu;
    @Bind(R.id.rl_bottom)
    RelativeLayout mRlBottom;
    private String roomId;
    private DBPublicManage mDBPublicManage;

    private DBManager mMgr;
    private List<MessageInfo> mMessageList = new ArrayList<>();
    private PublicAdapter mPublicAdapter;
    private LinearLayoutManager mLayoutManager;
    private int currentPosition;//記錄刷新位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_public);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setTitle("", R.mipmap.icon_nav_more_selected);
        EventBus.getDefault().register(this);//初始化EventBus
        mMgr = new DBManager(this);//初始化数据库管理类
        mMgr.updateNumber(roomId, 0);//更新未读消息条数
        EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));//发送更新未读消息通知
        mDBPublicManage = new DBPublicManage(this);
        initGetintent();
        initData();
    }

    private void initGetintent() {
        roomId = getIntent().getStringExtra("roomId");
//        roomId="TOPPERCHAT";
        setImage();
    }

    private void setImage() {
        if ("3".equals(roomId)) {
            mIvImage.setVisibility(View.VISIBLE);
            Resources r = getResources();
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + r.getResourcePackageName(R.mipmap.public_background) + "/"
                    + r.getResourceTypeName(R.mipmap.public_background) + "/"
                    + r.getResourceEntryName(R.mipmap.public_background));
            mIvImage.setImageURI(uri);
        } else {
            mIvImage.setVisibility(View.GONE);
        }
    }

    //初始化适配器
    private void initAdapter() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPublicAdapter = new PublicAdapter(this, mMessageList);
        mRecyclerView.setAdapter(mPublicAdapter);
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


    private void initData() {
        mTvTitleTop.setText(mDBPublicManage.findPublicName(roomId));
        initAdapter();
        initData(null);
        setMenu();
    }

    private void initData(String msgId) {
        if (StringUtils.isEmpty(msgId)) {
            List<MessageInfo> messageInfos = mMgr.queryMessage(roomId, 0);
            mMessageList.clear();
            mMessageList.addAll(messageInfos);
            mPublicAdapter.notifyDataSetChanged();
            mLayoutManager.scrollToPositionWithOffset(mPublicAdapter.getItemCount() - 1, 0);
        } else {
            MessageInfo messageInfo = mMgr.queryMessageMsg(msgId);
            if (roomId.equals(messageInfo.getUsername())) {
                mMessageList.add(messageInfo);
                mPublicAdapter.notifyItemInserted(mMessageList.indexOf(messageInfo));
                mLayoutManager.scrollToPositionWithOffset(mPublicAdapter.getItemCount() - 1, 0);
            }
        }
    }

    private void setMenu() {
        String menu = mDBPublicManage.findPublicMenu(roomId);
        mLlMenu.setMenuData(menu);
    }


    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.update_public_number))) {
            //更新公眾號
        } else if (msg.equals(EventBusUtil.msg_database_update)) {
            initData(event.getId());
            mMgr.updateNumber(roomId, 0);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//
    }

    @OnClick({R.id.bark, R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_more:
                goPublicDetail();
                break;
        }
    }

    private void goPublicDetail() {
        Intent intent = new Intent(this, PublicDetailsActivity.class);
        intent.putExtra("id", roomId);
        startActivity(intent);
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
                    currentPosition = mMessageList.size();
                    int position = mLayoutManager.findFirstVisibleItemPosition();
                    View view = mRecyclerView.getChildAt(position);
                    int top = 0;
                    if (view != null) {
                        top = view.getTop();
                    }
                    List<MessageInfo> messageInfos = mMgr.queryRefreshMessage(roomId, mMessageList.get(0).getCreateTime(), 0);
                    mMessageList.addAll(0, messageInfos);
                    currentPosition = mMessageList.size() - currentPosition;
                    mPublicAdapter.notifyDataSetChanged();
                    mLayoutManager.scrollToPositionWithOffset(currentPosition, top);
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
                        messageInfos1 = mMgr.queryLoadMessage(roomId, messageInfo.getCreateTime(), isFist, 0);
                    } else {
                        if (mMessageList.size() == 0) {
                            messageInfos1 = mMgr.queryLoadMessage(roomId, mMessageList.get(0).getCreateTime(), isFist, 0);
                        } else {
                            messageInfos1 = mMgr.queryLoadMessage(roomId, mMessageList.get(mMessageList.size() - 1).getCreateTime(), isFist, 0);
                        }
                    }
                    if (messageInfos1.size() <= 0) {
                        mRefreshLayout.setEnableLoadMore(false);
                    }
                    List<MessageInfo> MessageList3 = new ArrayList<>();
                    MessageList3.addAll(mMessageList);
                    MessageList3.addAll(messageInfos1);
                    mMessageList.clear();
                    mMessageList.addAll(MessageList3);
                    mPublicAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
}
