package com.bclould.tea.topperchat;

import android.content.Context;
import com.bclould.tea.history.DBPublicManage;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.PublicInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.utils.UtilTool;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by GIjia on 2018/6/25.
 */
public class RoomMemberManage {
    private static RoomMemberManage mInstance;
    /** 每次只执行一个任务的线程池 */
    private static ExecutorService mSingleThreadExecutor = null;
    private Context context;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    private DBPublicManage mDBPublicManage;
    private boolean isLoadMember=false;
    private boolean isLoadManage=false;
    private boolean isLoadPublic=false;
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
        mDBPublicManage=new DBPublicManage(context);
    }

   public synchronized void addRoomMember(final List<GroupInfo.DataBean> dataBean){
        if(isLoadMember){
            UtilTool.Log("fengjian","已經在加載群成員，暫不讓繼續加載");
            return;
        }
       UtilTool.Log("fengjian","加載成員");
       mSingleThreadExecutor.execute(new Runnable() {
           @Override
           public void run() {
               isLoadMember=true;
               for(int j=0;j<dataBean.size();j++) {
                   GroupInfo.DataBean data =dataBean.get(j);
                   mDBRoomMember.addRoomMember(data);
                   ArrayList<RoomMemberInfo> roomManageInfos=mDBRoomMember.queryAllOldRequest(data.getId()+"");
                   mDBRoomMember.deleteOldRoomMember(roomManageInfos,data.getId()+"");
                   mDBRoomMember.updateIsRefresh(data.getUsers(),data.getId()+"");
               }
               isLoadMember=false;
           }
       });
   }

    public synchronized void addRoomManage(final List<GroupInfo.DataBean> baseInfo){
       if (isLoadManage){
           UtilTool.Log("fengjian","已經在加載房間信息，暫不讓繼續加載");
           return;
       }
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

    public synchronized void addPublicManage(final List<PublicInfo.DataBean> baseInfo){
        if (isLoadPublic){
            UtilTool.Log("fengjian","已經在加載公眾號信息，暫不讓繼續加載");
            return;
        }
        UtilTool.Log("fengjian","加載房間信息");
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                isLoadPublic=true;
                for(PublicInfo.DataBean dataBean1:baseInfo){
                    mDBPublicManage.addPublic(dataBean1);
                }
                mDBPublicManage.deleteOldPublic();
                mDBPublicManage.updateIsRefresh(baseInfo);
                isLoadPublic=false;
            }
        });
    }
}
