package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBPublicManage;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.ui.activity.ConversationServerActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ChatTimeUtil;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.bclould.tea.utils.Constants.ADMINISTRATOR_NAME;

/**
 * Created by GA on 2017/12/20.
 */

public class ConversationAdapter extends RecyclerView.Adapter {

    private final Activity mContext;
    private final List<ConversationInfo> mConversationList;
    private final DBManager mMgr;
    private RelativeLayout mRlTitle;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    private DBPublicManage mDBPublicManage;

    public ConversationAdapter(Activity context, List<ConversationInfo> ConversationList, DBManager mgr, RelativeLayout mRlTitle, DBRoomMember mDBRoomMember, DBRoomManage mDBRoomManage, DBPublicManage dBPublicManage) {
        mContext = context;
        mConversationList = ConversationList;
        mMgr = mgr;
        this.mRlTitle=mRlTitle;
        this.mDBRoomMember=mDBRoomMember;
        this.mDBRoomManage=mDBRoomManage;
        this.mDBPublicManage=dBPublicManage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_conversation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mConversationList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mConversationList != null) {
            return mConversationList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tab1_item_img)
        ImageView mTab1ItemImg;
        @Bind(R.id.tab1_item_name)
        TextView mTab1ItemName;
        @Bind(R.id.tab1_item_text)
        TextView mTab1ItemText;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.number)
        TextView mNumber;
        @Bind(R.id.rl_item)
        RelativeLayout mRlItem;
        ConversationInfo mConversationInfo;
        private byte[] mDatas;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ADMINISTRATOR_NAME.equals(mConversationInfo.getUser())){
                        Intent intent = new Intent();
                        intent.setClass(mContext, ConversationServerActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", mConversationInfo.getFriend());
                        bundle.putString("roomId", mConversationInfo.getUser());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }else {
                        Intent intent = new Intent();
                        intent.setClass(mContext, ConversationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", mConversationInfo.getFriend());
                        bundle.putString("user", mConversationInfo.getUser());
                        bundle.putString("chatType", mConversationInfo.getChatType());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                    mNumber.setVisibility(View.GONE);
                    mMgr.updateConversation(mConversationInfo.getUser(), 0, mConversationInfo.getMessage(), mConversationInfo.getTime(),mConversationInfo.getCreateTime());
                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
                }
            });
            mRlItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDialog(mConversationInfo);
                    return false;
                }
            });
        }


        public void setData(ConversationInfo conversationInfo) {
            if ("true".equals(conversationInfo.getIstop())) {
                mRlItem.setBackgroundColor(mContext.getResources().getColor(R.color.boundary_color));
            } else {
                mRlItem.setBackgroundColor(mContext.getResources().getColor(R.color.app_bg_color));
            }
            mConversationInfo = conversationInfo;

           /* Bitmap bitmap = UtilTool.getImage(mMgr, conversationInfo.getUser(), mContext);
            mTab1ItemImg.setImageBitmap(bitmap);*/
           if(RoomManage.ROOM_TYPE_MULTI.equals(conversationInfo.getChatType())){
               UtilTool.getGroupImage(mDBRoomManage,conversationInfo.getUser(), (Activity) mContext,mTab1ItemImg);
               String roomName = mDBRoomManage.findRoomName(conversationInfo.getUser());
               if (StringUtils.isEmpty(roomName)) {
                   mTab1ItemName.setText(conversationInfo.getFriend());
               } else {
                   mTab1ItemName.setText(roomName);
               }
           }else if(RoomManage.ROOM_TYPE_SINGLE.equals(conversationInfo.getChatType())){
               setNameAndUrl(mTab1ItemImg,conversationInfo.getUser());
               String remark = mMgr.queryRemark(conversationInfo.getUser());
               if (!StringUtils.isEmpty(remark)) {
                   mTab1ItemName.setText(remark);
               } if(!StringUtils.isEmpty(conversationInfo.getFriend())){
                   mTab1ItemName.setText(conversationInfo.getFriend());
               }else{
                   mTab1ItemName.setText(conversationInfo.getUser());
               }
           }

            String draft=mMgr.findConversationDraft(conversationInfo.getUser());
            String atme=mMgr.findConversationAtme(conversationInfo.getUser());
            if(StringUtils.isEmpty(draft)){
                if(StringUtils.isEmpty(atme)){
                    mTab1ItemText.setText(conversationInfo.getMessage());
                }else{
                    UtilTool.changeTextColor(mTab1ItemText,atme+conversationInfo.getMessage(),0,atme.length(),Color.RED);
                }
            }else{
                mTab1ItemText.setText(mContext.getString(R.string.draft)+draft);
            }

            //兼容老版本
            if((conversationInfo.getCreateTime()+"").length()<11){
                long time=mMgr.findLastMessageConversationCreateTime(conversationInfo.getUser(),0);
                if(time<=0){
                    time=UtilTool.createChatCreatTime();
                }
                mMgr.updateConversationCreateTime(conversationInfo.getUser(),time);
            }
            mTime.setText(ChatTimeUtil.getConversation(conversationInfo.getCreateTime()));
            if (conversationInfo.getNumber() != 0) {
                mNumber.setVisibility(View.VISIBLE);
                if(conversationInfo.getNumber()>=100){
                    mNumber.setText("99+");
                }else {
                    mNumber.setText(conversationInfo.getNumber() + "");
                }
            }else{
                mNumber.setVisibility(View.GONE);
            }
        }
    }

    private void setNameAndUrl(ImageView mIvTouxiang, String user){
        UtilTool.getImage(mContext, mIvTouxiang,mDBRoomMember,mMgr,mDBPublicManage,user);
    }

    private void showDeleteDialog(final String user) {
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.confirm_delete_and_message));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mMgr.deleteConversation(user);
                    mMgr.deleteMessage(user,0);
                    setMessageTop(user,0);
                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialog(final ConversationInfo conversationInfo){
        final String istop=conversationInfo.getIstop();
        List<String> list = new ArrayList<>();
        list.add(mContext.getString(R.string.delete));
        if("true".equals(istop)){
            list.add(mContext.getString(R.string.cancel_the_top));
        }else{
            list.add(mContext.getString(R.string.stick));
        }
        final MenuListPopWindow menu = new MenuListPopWindow(mContext, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position){
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        showDeleteDialog(conversationInfo.getUser());
                        break;
                    case 2:
                        menu.dismiss();
                        if("true".equals(istop)){
                            mMgr.updateConversationIstop(conversationInfo.getUser(),"false");
                            setMessageTop(conversationInfo.getUser(),0);
                        }else{
                            mMgr.updateConversationIstop(conversationInfo.getUser(),"true");
                            setMessageTop(conversationInfo.getUser(),1);
                        }
                        EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.message_top_change)));
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    private void setMessageTop(final String roomId, final int status){
        new GroupPresenter(mContext).setTopMessage(roomId, status, false, new GroupPresenter.CallBack() {
            @Override
            public void send() {
            }
        });
    }

}