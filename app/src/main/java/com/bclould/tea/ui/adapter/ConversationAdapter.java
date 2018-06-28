package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.ui.activity.ConversationServerActivity;
import com.bclould.tea.ui.fragment.ConversationFragment;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ConversationInfo> mConversationList;
    private final DBManager mMgr;
    private RelativeLayout mRlTitle;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;

    public ConversationAdapter(Context context, List<ConversationInfo> ConversationList, DBManager mgr, RelativeLayout mRlTitle, DBRoomMember mDBRoomMember, DBRoomManage mDBRoomManage) {
        mContext = context;
        mConversationList = ConversationList;
        mMgr = mgr;
        this.mRlTitle=mRlTitle;
        this.mDBRoomMember=mDBRoomMember;
        this.mDBRoomManage=mDBRoomManage;
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
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
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
                mRlItem.setBackgroundColor(mContext.getResources().getColor(R.color.gray2));
            } else {
                mRlItem.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
            mConversationInfo = conversationInfo;

           /* Bitmap bitmap = UtilTool.getImage(mMgr, conversationInfo.getUser(), mContext);
            mTab1ItemImg.setImageBitmap(bitmap);*/
           if(RoomManage.ROOM_TYPE_MULTI.equals(conversationInfo.getChatType())){
               UtilTool.getGroupImage(mDBRoomManage,conversationInfo.getUser(),mContext,mTab1ItemImg);
           }else {
               setNameAndUrl(mTab1ItemImg,conversationInfo.getUser());
           }
            String remark = mMgr.queryRemark(conversationInfo.getUser());
            if (!StringUtils.isEmpty(remark)) {
                mTab1ItemName.setText(remark);
            } else {
                mTab1ItemName.setText(conversationInfo.getFriend());
            }
            mTab1ItemText.setText(conversationInfo.getMessage());
            mTime.setText(conversationInfo.getTime());
            if (conversationInfo.getNumber() != 0) {
                mNumber.setVisibility(View.VISIBLE);
                mNumber.setText(conversationInfo.getNumber() + "");
            }else{
                mNumber.setVisibility(View.GONE);
            }
        }
    }

    private void setNameAndUrl(ImageView mIvTouxiang, String user){
        UtilTool.getImage(mContext, mIvTouxiang,mDBRoomMember,mMgr,user);
    }

    private void showDeleteDialog(final String user) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.confirm_delete));
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
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
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
                        }else{
                            mMgr.updateConversationIstop(conversationInfo.getUser(),"true");
                        }
                        EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.message_top_change)));
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

}