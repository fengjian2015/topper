package com.bclould.tocotalk.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.crypto.otr.OtrChatManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AuthStatusInfo;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.OtcOrderStatusInfo;
import com.bclould.tocotalk.model.QrcodeReceiptPayInfo;
import com.bclould.tocotalk.model.RedExpiredInfo;
import com.bclould.tocotalk.ui.activity.OrderCloseActivity;
import com.bclould.tocotalk.ui.activity.OrderDetailsActivity;
import com.bclould.tocotalk.ui.activity.PayDetailsActivity;
import com.bclould.tocotalk.ui.adapter.ConversationAdapter;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.IMLogin;
import com.bclould.tocotalk.xmpp.XMConnectionListener;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.bclould.tocotalk.xmpp.XmppListener;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.impl.JidCreate;
import org.xutils.common.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.bclould.tocotalk.Presenter.LoginPresenter.CURRENCY;
import static com.bclould.tocotalk.Presenter.LoginPresenter.STATE;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.ACCESSKEYID;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SECRETACCESSKEY;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SESSIONTOKEN;
import static com.bclould.tocotalk.ui.activity.SystemSetActivity.INFORM;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_AUTH_STATUS_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_IN_OUT_COIN_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_OTC_ORDER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_RECEIPT_PAY_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_RED_PACKET_EXPIRED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VOICE_MSG;
import static com.bclould.tocotalk.utils.Constants.ACCESS_KEY_ID;
import static com.bclould.tocotalk.utils.Constants.SECRET_ACCESS_KEY;
import static com.bclould.tocotalk.utils.Constants.SESSION_TOKEN;
import static com.bclould.tocotalk.utils.MySharedPreferences.SETTING;

/**
 * Created by GA on 2017/12/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.rl_ununited)
    RelativeLayout mRlUnunited;
    @Bind(R.id.iv_warning)
    ImageView mIvWarning;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private List<Map<String, Object>> list = new ArrayList<>();
    private List<ConversationInfo> showlist = new ArrayList<>();
    private DBManager mgr;
    public static ConversationFragment instance = null;
    private ConversationAdapter mConversationAdapter;
    private MyReceiver receiver;
    private LoadingProgressDialog mProgressDialog;

    public static ConversationFragment getInstance() {

        if (instance == null) {
            instance = new ConversationFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (receiver == null) {
            receiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter("XMPPConnectionListener");
            getActivity().registerReceiver(receiver, intentFilter);
        }
        ButterKnife.bind(this, view);
        pullToRefresh();
        mgr = new DBManager(getActivity());
        initRecyclerView();
        initData();
//        initAWS();
        return view;
    }




    private void pullToRefresh() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                if (XmppConnection.getInstance().getConnection() == null || !XmppConnection.getInstance().getConnection().isAuthenticated()) {
                    loginIM();

                }
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(getContext(), getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.setAction("XMPPConnectionListener");
                    intent.putExtra("type", true);
                    getContext().sendBroadcast(intent);
                    pingService();
                    break;
            }
        }
    };

    private Timer tExit;

    private void pingService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PingManager pingManager = PingManager.getInstanceFor(XmppConnection.getInstance().getConnection());
                pingManager.setPingInterval(60);
                try {
                    pingManager.pingMyServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pingManager.registerPingFailedListener(new PingFailedListener() {
                    @Override
                    public void pingFailed() {
                        if (tExit == null) {
                            tExit = new Timer();
                            tExit.schedule(new TimeTask(), 1000);
                        }
                    }
                });
            }
        }).start();
    }

    private class TimeTask extends TimerTask {
        @Override
        public void run() {

            if (UtilTool.getUser() != null && UtilTool.getpw() != null) {
                Log.i("XMConnectionListener", "尝试登录");
                // 连接服务器
                try {
                    if (!XmppConnection.getInstance().isAuthenticated()) {// 用户未登录
                        if (IMLogin.loginAction(getContext(),XmppConnection.getInstance().getConnection())) {
                            Log.i("XMConnectionListener", "登录成功");
                            EventBus.getDefault().post(new MessageEvent(getString(R.string.login_succeed)));
                            Intent intent = new Intent();
                            intent.setAction("XMPPConnectionListener");
                            intent.putExtra("type", true);
                            getContext().sendBroadcast(intent);
                        } else {
                            Log.i("XMConnectionListener", "重新登录");
                            tExit.schedule(new TimeTask(), 1000);
                        }
                    }
                } catch (Exception e) {
                    Log.i("XMConnectionListener", "尝试登录,出现异常!");
                    Log.i("XMConnectionListener", e.getMessage());
                }
            }
        }
    }

    //登录即时通讯
    private void loginIM() {
//        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //连接openfile
                    AbstractXMPPConnection connection = XmppConnection.getInstance().getConnection();
                    //判断是否连接
                    if (connection != null && connection.isConnected()) {
                        IMLogin.loginAction(getContext(),connection);
                        /*if (connection.isAuthenticated()) {//登录成功
                            PingManager.setDefaultPingInterval(10);
                            PingManager myPingManager = PingManager.getInstanceFor(connection);
                            myPingManager.registerPingFailedListener(new PingFailedListener() {
                                @Override
                                public void pingFailed() {
                                    Toast.makeText(MainActivity.this, "发送心跳包失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }*/
                        //登录成功发送通知
                        EventBus.getDefault().post(new MessageEvent(getString(R.string.login_succeed)));
                        UtilTool.Log("fsdafa", "登录成功");
                        android.os.Message message = new android.os.Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    } else if (!connection.isConnected()) {
                        XmppConnection.getInstance().getConnection().connect();
                    }
                } catch (Exception e) {
                    //发送登录失败通知
                    mHandler.removeMessages(3);
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.login_error)));
                    android.os.Message message = new android.os.Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                    UtilTool.Log("日志", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @OnClick(R.id.rl_ununited)
    public void onViewClicked() {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    //广播接收器
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean type = intent.getBooleanExtra("type", false);
            if (mRlUnunited != null) {
                if (type) {
                    mRlUnunited.setVisibility(View.GONE);
                } else {
                    mRlUnunited.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.login_succeed))) {
            initData();
        } else if (msg.equals(getString(R.string.oneself_send_msg))) {
            initData();
        } else if (msg.equals(getString(R.string.send_red_packet_le))) {
            initData();
        } else if (msg.equals(getString(R.string.dispose_unread_msg))) {
            initData();
        } else if (msg.equals(getString(R.string.new_friend))) {
            initData();
        } else if (msg.equals(getString(R.string.login_error))) {
            mRlUnunited.setVisibility(View.VISIBLE);
        } else if (msg.equals(getString(R.string.message_top_change))) {
            initData();
        }else if(msg.equals(getString(R.string.change_friend_remark))){
            initData();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        if (mRecyclerView != null) {
            List<ConversationInfo> conversationInfos = mgr.queryConversation();
            if (conversationInfos.size() == 0) {
                mLlNoData.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mLlNoData.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            showlist.removeAll(showlist);
            showlist.addAll(conversationInfos);
            sort();
            initRecyclerView();
        }
    }

    private void initRecyclerView() {
        mConversationAdapter = new ConversationAdapter(this, getActivity(), getSimpleData(), mgr);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mConversationAdapter);
    }

    private List<ConversationInfo> getSimpleData() {
        return showlist;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void downloadFile(final String key, final String fileName, final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                            ACCESS_KEY_ID,
                            SECRET_ACCESS_KEY,
                            SESSION_TOKEN);

                    AmazonS3Client s3Client = new AmazonS3Client(
                            sessionCredentials);
                    Regions regions = Regions.fromName("ap-northeast-2");
                    Region region = Region.getRegion(regions);
                    s3Client.setRegion(region);
                    GetObjectRequest gor = new GetObjectRequest(Constants.BUCKET_NAME, key);
                    File file = new File(Environment.getExternalStorageDirectory().getPath(), fileName);
                    ObjectMetadata object = s3Client.getObject(gor, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void sort() {
        //将showlist按时间排成倒序
        Collections.sort(showlist, new Comparator<ConversationInfo>() {
            @Override
            public int compare(ConversationInfo conversationInfo, ConversationInfo conversationInfo2) {
                String at = conversationInfo.getTime();
                String bt = conversationInfo2.getTime();
                String atop = conversationInfo.getIstop();
                String btop = conversationInfo2.getIstop();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if ("true".equals(btop) && !"true".equals(atop)) {
                        return 1;
                    } else if (!"true".equals(btop) && "true".equals(atop)) {
                        return -1;
                    } else if (formatter.parse(bt).getTime() > formatter.parse(at).getTime()) {
                        return 1;
                    } else if (formatter.parse(bt).getTime() < formatter.parse(at).getTime()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
