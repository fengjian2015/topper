package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.FGCInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FGCAdapter extends BaseAdapter{

    private final Context mContext;
    private final List<FGCInfo.DataBean.RecordBean> mList;

    private OnclickListener mOnclickListener;

    public FGCAdapter(Context context, List<FGCInfo.DataBean.RecordBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            holder=new ViewHolder();
            view=View.inflate(mContext,R.layout.item_fgc_list,null);
            holder.mTvMoney=view.findViewById(R.id.tv_money);
            holder.mTvTime=view.findViewById(R.id.tv_time);
            holder.mTvIncome=view.findViewById(R.id.tv_income);
            holder.mTvRate=view.findViewById(R.id.tv_rate);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        FGCInfo.DataBean.RecordBean hashMap=mList.get(i);
        holder.mTvTime.setText(hashMap.getCreated_at());
        holder.mTvRate.setText(hashMap.getRate()+":1");
        holder.mTvMoney.setText(hashMap.getCoin_number()+"USDT");
        holder.mTvIncome.setText("+"+hashMap.getFgc_number()+"FGC");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnclickListener != null) {
                    mOnclickListener.onclick(i);
                }
            }
        });
        return view;
    }


    class ViewHolder  {
        TextView mTvMoney;
        TextView mTvTime;
        TextView mTvIncome;
        TextView mTvRate;
    }

    public void setOnClickListener(OnclickListener onClickListener) {
        this.mOnclickListener = onClickListener;
    }

    public interface OnclickListener {
        void onclick(int position);
    }
}
