package com.bclould.tea.xmpp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GIjia on 2018/5/22.
 */

public class RoomManage {
    public final static String ROOM_TYPE_SINGLE = "single";
    public final static String ROOM_TYPE_MULTI = "multi";
    public final static int ROOM_MAX_NUMBER=200;
    private Map<String, Room> roomMap=new HashMap<>();
    public static RoomManage roomManage;
    public Context context;
    private DBManager mMgr;
    private DBRoomMember dbRoomMember;
    private DBRoomManage dbRoomManage;
    public HashMap<String,Room> roomHashMap=new HashMap<>();
    public static RoomManage getInstance(){
        if(roomManage==null){
            roomManage=new RoomManage();
        }
        return roomManage;
    }

    public void setContext(Context context){
        this.context=context;
        mMgr=new DBManager(context);
        dbRoomMember=new DBRoomMember(context);
        dbRoomManage=new DBRoomManage(context);
    }


    public synchronized Room addSingleMessageManage(String roomId, String roomName){
        if(roomHashMap!=null){
            if(getRoom(roomId)!=null){
                return getRoom(roomId);
            }
            SingleManage singleManage =new SingleManage(mMgr,roomId,context,roomName);
            roomHashMap.put(roomId, singleManage);
            return singleManage;
        }
        return null;
    }

    public synchronized Room addMultiMessageManage(String roomId, String roomName){
        if(roomHashMap!=null){
            if(getRoom(roomId)!=null){
                return getRoom(roomId);
            }
            MultiManage multiManage =new MultiManage(mMgr,dbRoomMember,dbRoomManage,roomId,context,roomName);
            roomHashMap.put(roomId, multiManage);
            return multiManage;
        }
        return null;
    }

    public synchronized void removeRoom(String roomId){
        if(roomHashMap!=null){
             roomHashMap.remove(roomId);
        }
        mMgr.deleteMessage(roomId,0);
        mMgr.deleteConversation(roomId);
        dbRoomManage.deleteRoom(roomId);
        dbRoomMember.deleteRoom(roomId);
        MessageEvent messageEvent = new MessageEvent(EventBusUtil.quit_group);
        messageEvent.setId(roomId);
        EventBus.getDefault().post(messageEvent);
    }

    public synchronized Room getRoom(String roomId){
        if(roomHashMap!=null){
          return roomHashMap.get(roomId);
        }
        return null;
    }

    public synchronized void reoveAllRoom(){
        if(roomHashMap!=null){
            roomHashMap.clear();
        }
    }
}
