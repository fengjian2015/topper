package com.bclould.tea.topperchat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import org.greenrobot.eventbus.EventBus;
import static com.bclould.tea.ui.activity.my.systemxet.SystemSetActivity.INFORM;
import static com.bclould.tea.ui.fragment.FriendListFragment.NEWFRIEND;
import static com.bclould.tea.utils.MySharedPreferences.SETTING;

/**
 * Created by GA on 2018/6/20.
 */

//广播接收器
public class AddFriendReceiver extends BroadcastReceiver {

    private String response;
    private String acceptAdd;
    private String tocoId;
    private String alertSubName;
    private DBManager mMgr;
    private int mNewFriend;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mMgr = new DBManager(context);
        //接收传递的字符串response
        Bundle bundle = intent.getExtras();
        response = bundle.getString("response");
        UtilTool.Log("fsdafa", "广播收到" + response);
        if (response == null) {
            //获取传递的字符串及发送方JID
            acceptAdd = bundle.getString("acceptAdd");
            tocoId = bundle.getString("fromName");
            alertSubName = bundle.getString("alertSubName");
            if (acceptAdd.equals(context.getString(R.string.receive_add_request))) {
                //弹出一个对话框，包含同意和拒绝按钮
                SharedPreferences sp = context.getSharedPreferences(SETTING, 0);
                if (sp.contains(INFORM)) {
                    if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                        UtilTool.playHint(context);
                    }
                } else {
                    UtilTool.playHint(context);
                }

                if (ActivityUtil.isActivityOnTop((Activity) MyApp.getInstance().mActivityList.lastElement())) {
                    final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, (Activity) MyApp.getInstance().mActivityList.lastElement(), R.style.dialog);
                    deleteCacheDialog.show();
                    deleteCacheDialog.setTitle(context.getString(R.string.user) + alertSubName + context.getString(R.string.request_add));
                    Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
                    cancel.setTextColor(context.getResources().getColor(R.color.red));
                    cancel.setText(context.getString(R.string.reject));
                    Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
                    confirm.setText(context.getString(R.string.consent));
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //拒絕
                            new PersonalDetailsPresenter((Activity) MyApp.getInstance().mActivityList.lastElement()).confirmAddFriend(tocoId, 2, new PersonalDetailsPresenter.CallBack() {
                                @Override
                                public void send() {
                                    deleteCacheDialog.dismiss();
                                    mMgr.updateRequest(tocoId, 2);
                                    mNewFriend = MySharedPreferences.getInstance().getInteger(NEWFRIEND);
                                    mNewFriend--;
                                    MySharedPreferences.getInstance().setInteger(NEWFRIEND, mNewFriend);
                                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.receive_add_request));
                                }
                            });
                        }
                    });
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new PersonalDetailsPresenter((Activity) MyApp.getInstance().mActivityList.lastElement()).confirmAddFriend(tocoId, 1, new PersonalDetailsPresenter.CallBack() {
                                @Override
                                public void send() {
                                    deleteCacheDialog.dismiss();
                                    mMgr.updateRequest(tocoId, 1);
                                    mNewFriend = MySharedPreferences.getInstance().getInteger(NEWFRIEND);
                                    mNewFriend--;
                                    MySharedPreferences.getInstance().setInteger(NEWFRIEND, mNewFriend);
                                    UserInfo userInfo=new UserInfo();
                                    userInfo.setUser(tocoId);
                                    userInfo.setUserName(alertSubName);
                                    userInfo.setRemark(alertSubName);
                                    userInfo.setPath("");
                                    mMgr.addUser(userInfo);
                                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.receive_add_request));
                                    //同意
                                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.new_friend));
                                    RoomManage.getInstance().addSingleMessageManage(tocoId,alertSubName).sendMessage(context.getString(R.string.we_already_friends_come_chat_together));
                                }
                            });
                        }
                    });
                }
            }
        }
    }
}
