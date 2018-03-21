package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.OtcActivity;
import com.bclould.tocotalk.ui.activity.PayManageActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by GA on 2018/3/16.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BottomDialogRVAdapter4 extends RecyclerView.Adapter {

    private final String[] mCoinArr;
    private final Context mContext;

    public BottomDialogRVAdapter4(Context context, String[] coinArr) {
        mContext = context;
        mCoinArr = coinArr;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_bottom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mCoinArr[position]);
    }

    @Override
    public int getItemCount() {
        if (mCoinArr == null)
            return 0;
        return mCoinArr.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView mTvName;
        private String mName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mContext instanceof OtcActivity) {
                        OtcActivity activity = (OtcActivity) mContext;
                        activity.hideDialog(mName);
                    }else if(mContext instanceof PayManageActivity){
                        PayManageActivity activity = (PayManageActivity) mContext;
                        activity.hideDialog(mName);
                    }
                }
            });
        }

        public void setData(String name) {
            mName = name;
            mTvName.setText(name);
        }
    }
}
