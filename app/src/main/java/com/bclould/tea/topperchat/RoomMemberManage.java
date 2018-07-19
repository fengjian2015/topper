package com.bclould.tea.topperchat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.crypto.otr.OtrChatListenerManager;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.ArrayList;
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
    private DBRoomManage mDBRoomManage;
    private boolean isLoadMember=false;
    private boolean isLoadManage=false;
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
        mDBRoomManage=new DBRoomManage(context);
    }

   public synchronized void addRoomMember(final List<GroupInfo.DataBean> dataBean){
//        if(isLoadMember){
//            UtilTool.Log("fengjian","已經在加載群成員，暫不讓繼續加載");
//            return;
//        }
       UtilTool.Log("fengjian","加載成員");
       mSingleThreadExecutor.execute(new Runnable() {
           @Override
           public void run() {
               isLoadMember=true;
               for(int j=0;j<dataBean.size();j++) {
                   GroupInfo.DataBean data =dataBean.get(j);
                   for (int i = 0; i < data.getUsers().size(); i++) {
                       GroupInfo.DataBean.UsersBean usersBean = data.getUsers().get(i);
                       RoomMemberInfo roomMemberInfo = new RoomMemberInfo();
                       roomMemberInfo.setRoomId(data.getId() + "");
                       roomMemberInfo.setJid(usersBean.getToco_id());
                       roomMemberInfo.setImage_url(usersBean.getAvatar());
                       roomMemberInfo.setName(usersBean.getName());
                       roomMemberInfo.setIsRefresh(1);
                       mDBRoomMember.addRoomMember(roomMemberInfo);
                   }
                   ArrayList<RoomMemberInfo> roomManageInfos=mDBRoomMember.queryAllOldRequest(data.getId()+"");
                   mDBRoomMember.deleteOldRoomMember(roomManageInfos,data.getId()+"");
                   mDBRoomMember.updateIsRefresh(data.getUsers(),data.getId()+"");
               }
               isLoadMember=false;
           }
       });
   }

    public synchronized void addRoomManage(final List<GroupInfo.DataBean> baseInfo){
//       if (isLoadManage){
//           UtilTool.Log("fengjian","已經在加載房間信息，暫不讓繼續加載");
//           return;
//       }
        UtilTool.Log("fengjian","加載房間信息");
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                isLoadManage=true;
                for(int j=0;j<baseInfo.size();j++){
                    GroupInfo.DataBean dataBean = baseInfo.get(j);
                    RoomManageInfo roomManageInfo = new RoomManageInfo();
                    roomManageInfo.setRoomName(dataBean.getName());
                    roomManageInfo.setRoomId(dataBean.getId() + "");
                    roomManageInfo.setOwner(dataBean.getToco_id());
                    roomManageInfo.setRoomNumber(dataBean.getMax_people());
                    roomManageInfo.setRoomImage(dataBean.getLogo());
                    roomManageInfo.setDescription(dataBean.getDescription());
                    roomManageInfo.setIsRefresh(1);
                    roomManageInfo.setAllowModify(dataBean.getIs_allow_modify_data());
                    roomManageInfo.setIsReview(dataBean.getIs_review());
                    mDBRoomManage.addRoom(roomManageInfo);
                }
                mDBRoomManage.deleteOldRoom();
                mDBRoomManage.updateIsRefresh(baseInfo);
                isLoadManage=false;
            }
        });
    }

}
