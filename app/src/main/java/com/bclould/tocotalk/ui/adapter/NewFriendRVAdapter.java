package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AddRequestInfo;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.impl.JidCreate;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/12.
 */

public class NewFriendRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<AddRequestInfo> mAddRequestInfos;
    private final DBManager mMgr;

    public NewFriendRVAdapter(Context context, List<AddRequestInfo> addRequestInfos, DBManager mgr) {
        mContext = context;
        mAddRequestInfos = addRequestInfos;
        mMgr = mgr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_new_friend, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mAddRequestInfos.get(position));
    }

    @Override
    public int getItemCount() {
        if (mAddRequestInfos != null) {
            return mAddRequestInfos.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.btn_consent)
        Button mBtnConsent;
        private AddRequestInfo mAddRequestInfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mBtnConsent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Presence presenceRes = new Presence(Presence.Type.subscribed);
                    presenceRes.setTo(mAddRequestInfo.getUser());
                    try {
                        XmppConnection.getInstance().getConnection().sendStanza(presenceRes);
                        Roster.getInstanceFor(XmppConnection.getInstance().getConnection()).createEntry(JidCreate.entityBareFrom(mAddRequestInfo.getUser()), null, new String[]{"Friends"});
                        mMgr.updateRequest(mAddRequestInfo.getId(), 1);
                        EventBus.getDefault().post("新的好友");
                        mBtnConsent.setBackgroundColor(mContext.getColor(R.color.white));
                        mBtnConsent.setText("已同意");
                        mBtnConsent.setTextColor(mContext.getColor(R.color.gray));
                        mBtnConsent.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void setData(AddRequestInfo addRequestInfo) {
            mAddRequestInfo = addRequestInfo;
            mName.setText(addRequestInfo.getUser().substring(0, addRequestInfo.getUser().indexOf("@")));
            if (addRequestInfo.getType() == 1) {
                mBtnConsent.setBackgroundColor(mContext.getColor(R.color.white));
                mBtnConsent.setText("已同意");
                mBtnConsent.setTextColor(mContext.getColor(R.color.gray));
                mBtnConsent.setEnabled(false);
            }
        }
    }
}
