package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.PersonalDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AddRequestInfo;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewFriendRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<AddRequestInfo> mAddRequestInfos;
    private final DBManager mMgr;
    private final List<String> mImageList;

    public NewFriendRVAdapter(Context context, List<AddRequestInfo> addRequestInfos, DBManager mgr, List<String> imageList) {
        mContext = context;
        mAddRequestInfos = addRequestInfos;
        mMgr = mgr;
        mImageList = imageList;
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
        if (mAddRequestInfos.size() != 0 && mImageList.size() != 0)
            viewHolder.setData(mAddRequestInfos.get(position), mImageList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mAddRequestInfos.size() != 0) {
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
                    try {
                        new PersonalDetailsPresenter(mContext).confirmAddFriend(mAddRequestInfo.getUser(), 1, new PersonalDetailsPresenter.CallBack() {
                            @Override
                            public void send() {
                                mMgr.updateRequest(mAddRequestInfo.getId(), 1);
                                EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.new_friend)));
                                mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                                mBtnConsent.setText(mContext.getString(R.string.agrd_agreed));
                                mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.gray));
                                mBtnConsent.setEnabled(false);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void setData(AddRequestInfo addRequestInfo, String url) {
            mBtnConsent.setVisibility(View.VISIBLE);
            mAddRequestInfo = addRequestInfo;
            if (url.isEmpty()) {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, url, mIvTouxiang);
            }
            mName.setText(addRequestInfo.getUserName());
            if (addRequestInfo.getType() == 1) {
                mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                mBtnConsent.setText(mContext.getString(R.string.agrd_agreed));
                mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.gray));
                mBtnConsent.setEnabled(false);
            }else if(addRequestInfo.getType() == 2){
                mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                mBtnConsent.setText(mContext.getString(R.string.denied));
                mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.gray));
                mBtnConsent.setEnabled(false);
            }
        }
    }
}
