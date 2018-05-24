package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.history.DBRoomManage;
import com.bclould.tocotalk.history.DBRoomMember;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GIjia on 2018/5/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RoomManage {
    public final static String ROOM_TYPE_SINGLE = "single";
    public final static String ROOM_TYPE_MULTI = "multi";
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
            if(getRoom(getRoomId(roomId))!=null){
                return getRoom(getRoomId(roomId));
            }
            SingleManage singleManage =new SingleManage(mMgr,roomId,context,roomName);
            roomHashMap.put(getRoomId(roomId), singleManage);
            return singleManage;
        }
        return null;
    }

    public synchronized Room addMultiMessageManage(String roomId, String roomName){
        if(roomHashMap!=null){
            if(getRoom(getRoomId(roomId))!=null){
                return getRoom(getRoomId(roomId));
            }
            MultiManage multiManage =new MultiManage(mMgr,dbRoomMember,dbRoomManage,roomId,context,roomName);
            roomHashMap.put(getRoomId(roomId), multiManage);
            return multiManage;
        }
        return null;
    }

    public synchronized void removeRoom(String roomId){
        if(roomHashMap!=null){
             roomHashMap.remove(getRoomId(roomId));
        }
    }

    public synchronized Room getRoom(String roomId){
        if(roomHashMap!=null){
          return roomHashMap.get(getRoomId(roomId));
        }
        return null;
    }

    /***
     *
     * 获得房间号
     *
     */
    public String getRoomId(String roomId) {
        return UtilTool.getJid() + "&" + roomId;
    }
}
