package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/20.
 */

public class SelectConversationAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ConversationInfo> mConversationList;
    private final DBManager mMgr;
    private OnItemListener onItemListener;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;

    public SelectConversationAdapter(Context context, List<ConversationInfo> ConversationList, DBManager mgr, DBRoomMember mDBRoomMember, DBRoomManage mDBRoomManage) {
        mContext = context;
        mConversationList = ConversationList;
        mMgr = mgr;
        this.mDBRoomMember=mDBRoomMember;
        this.mDBRoomManage=mDBRoomManage;
    }

    public void addOnItemListener(OnItemListener onItemListener){
        this.onItemListener=onItemListener;
    }

    public interface OnItemListener{
        void onItemClick(String remark,String name,String user,String chatType,String url);
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url="";
                    if(RoomManage.ROOM_TYPE_MULTI.equals(mConversationInfo.getChatType())){
                        url=mDBRoomManage.findRoomUrl(mConversationInfo.getUser());
                    }else{
                        url = mDBRoomMember.findMemberUrl(mConversationInfo.getUser());
                        if (StringUtils.isEmpty(url) && mMgr.findUser(mConversationInfo.getUser())) {
                            UserInfo info = mMgr.queryUser(mConversationInfo.getUser());
                            if (!StringUtils.isEmpty(info.getPath())) {
                                url = info.getPath();
                            }
                        }
                        if (StringUtils.isEmpty(url)) {
                            url = mMgr.findStrangerPath(mConversationInfo.getUser());
                        }
                        if (StringUtils.isEmpty(url)) {
                            url = mMgr.findUserPath(mConversationInfo.getUser());
                        }
                    }
                    onItemListener.onItemClick(mTab1ItemName.getText().toString(),mConversationInfo.getFriend(),mConversationInfo.getUser(),mConversationInfo.getChatType(),url );
                }
            });

        }
        private void setNameAndUrl(ImageView mIvTouxiang, String user){
            String url=mDBRoomMember.findMemberUrl(user);
            if(StringUtils.isEmpty(url)&&mMgr.findUser(user)){
                UserInfo info = mMgr.queryUser(user);
                if (!StringUtils.isEmpty(info.getPath())) {
                    url=info.getPath();
                }
            }
            UtilTool.getImage(mContext, mIvTouxiang,mDBRoomMember,mMgr,mConversationInfo.getUser());
        }


        public void setData(ConversationInfo conversationInfo) {
            mConversationInfo = conversationInfo;
            if(RoomManage.ROOM_TYPE_MULTI.equals(conversationInfo.getChatType())){
                UtilTool.getGroupImage(mDBRoomManage,conversationInfo.getUser(), (Activity) mContext,mTab1ItemImg);
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
        }
    }

}