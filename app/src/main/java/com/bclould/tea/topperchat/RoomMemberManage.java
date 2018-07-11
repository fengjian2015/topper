package com.bclould.tea.topperchat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.crypto.otr.OtrChatListenerManager;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bclould.tea.topperchat.WsContans.MSG_GROUP;
import static com.bclould.tea.topperchat.WsContans.MSG_SINGLER;
import static com.bclould.tea.topperchat.WsContans.MSG_STEANGER;

/**
 * Created by GIjia on 2018/6/25.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class RoomMemberManage {
    private static RoomMemberManage mInstance;
    /** 每次只执行一个任务的线程池 */
    private static ExecutorService mSingleThreadExecutor = null;
    private Context context;
    private DBRoomMember mDBRoomMember;
    public static RoomMemberManage getInstance(){
        if(mInstance == null){
            synchronized (RoomMemberManage.class){
                if(mInstance == null){
                    mInstance = new RoomMemberManage();
                    mSingleThreadExecutor = Executors.newSingleThreadExecutor();// 每次只执行一个线程任务的线程池
                }
            }
        }
        return mInstance;
    }

    public void setContext(Context context){
        this.context=context;
        mDBRoomMember=new DBRoomMember(context);
    }

   public synchronized void addRoomMember(final List<GroupInfo.DataBean> dataBean){
       mSingleThreadExecutor.execute(new Runnable() {
           @Override
           public void run() {
               for(int j=0;j<dataBean.size();j++) {
                   GroupInfo.DataBean data =dataBean.get(j);
                   for (int i = 0; i < data.getUsers().size(); i++) {
                       GroupInfo.DataBean.UsersBean usersBean = data.getUsers().get(i);
                       RoomMemberInfo roomMemberInfo = new RoomMemberInfo();
                       roomMemberInfo.setRoomId(data.getId() + "");
                       roomMemberInfo.setJid(usersBean.getToco_id());
                       roomMemberInfo.setImage_url(usersBean.getAvatar());
                       roomMemberInfo.setName(usersBean.getName());
                       mDBRoomMember.addRoomMember(roomMemberInfo);
                   }
               }
           }
       });
   }
}
