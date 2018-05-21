package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.LikeInfo;
import com.bclould.tocotalk.model.ReviewListInfo;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DynamicDetailRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ReviewListInfo.DataBean.ListBean> mDataList;
    private final DBManager mMgr;
    private final DynamicPresenter mDynamicPresenter;

    public DynamicDetailRVAdapter(Context context, List<ReviewListInfo.DataBean.ListBean> dataList, DBManager mgr, DynamicPresenter dynamicPresenter) {
        mContext = context;
        mDataList = dataList;
        mMgr = mgr;
        mDynamicPresenter = dynamicPresenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_detail, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time_tv)
        ImageView mTimeTv;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.comment_text)
        TextView mCommentText;
        @Bind(R.id.tv_zan_count)
        TextView mTvZanCount;
        @Bind(R.id.ll_zan)
        LinearLayout mLlZan;
        @Bind(R.id.dynamic_content)
        RelativeLayout mDynamicContent;
        private ReviewListInfo.DataBean.ListBean mListBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mLlZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.reviewLike(mListBean.getId() + "", new DynamicPresenter.CallBack4() {
                        @Override
                        public void send(LikeInfo data) {
                            mTvZanCount.setText(data.getLikeCounts() + "");
                            if (data.getStatus() == 1) {
                                mLlZan.setSelected(true);
                            } else {
                                mLlZan.setSelected(false);
                            }
                        }
                    });
                }
            });
        }

        public void setData(ReviewListInfo.DataBean.ListBean listBean) {
            mListBean = listBean;
            mCommentText.setText(listBean.getContent());
            mName.setText(listBean.getUser_name());
            mTime.setText(listBean.getCreated_at());
            mTvZanCount.setText(listBean.getLike_count() + "");
            if (listBean.getIs_like() == 1) {
                mLlZan.setSelected(true);
            } else {
                mLlZan.setSelected(false);
            }
            String user = listBean.getUser_name() + "@" + Constants.DOMAINNAME;
//            mTouxiang.setImageBitmap(UtilTool.getImage(mMgr, user, mContext));
            UtilTool.getImage(mMgr, user, mContext, mTouxiang);
        }
    }
}
