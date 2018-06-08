package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.QuestionInfo;
import com.bclould.tocotalk.ui.activity.WebViewActivity;
import com.bclould.tocotalk.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AllProblemRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<QuestionInfo.DataBean> mDataBeanList;

    public AllProblemRVAdapter(Context context, List<QuestionInfo.DataBean> dataBeanList) {
        mDataBeanList = dataBeanList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_all_problem, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataBeanList.size() != 0) {
            return mDataBeanList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.problem_content)
        TextView mProblemContent;
        @Bind(R.id.cv)
        CardView mCV;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final QuestionInfo.DataBean dataBean) {
            mProblemContent.setText(dataBean.getTitle());
            mCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("url", Constants.BASE_URL + Constants.QUES_WEB_URL + dataBean.getId());
                    intent.putExtra("title", mContext.getString(R.string.question_details));
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
