package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bclould.tocotalk.history.DBManager;
import java.util.HashMap;

/**
 * Created by GIjia on 2018/5/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RoomManage {
    public static RoomManage roomManage;
    public Context context;
    private DBManager mMgr;
    public HashMap<String,MessageManage> messageManageHashMap=new HashMap<>();
    public static RoomManage getInstance(){
        if(roomManage==null){
            roomManage=new RoomManage();
        }
        return roomManage;
    }

    public void setContext(Context context){
        this.context=context;
        mMgr=new DBManager(context);
    }

    public synchronized MessageManage addMessageManage(String roomId,String roomName){
        if(messageManageHashMap!=null){
            MessageManage messageManage=new MessageManage(mMgr,roomId,context,roomName);
            messageManageHashMap.put(roomId,messageManage);
            return messageManage;
        }
        return null;
    }

    public synchronized MessageManage getMessageManage(String roomId){
        if(messageManageHashMap!=null){
          return messageManageHashMap.get(roomId);
        }
        return null;
    }
}
