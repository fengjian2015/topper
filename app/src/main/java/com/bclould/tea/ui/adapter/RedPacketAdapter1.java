package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RedPacketAdapter1 extends BaseAdapter {

    private final List<GrabRedInfo.DataBean.LogBean> mLogBeanList;
    private final Context mContext;
    private final DBManager mMgr;
    private String coin;
    private boolean isShowBestLuck;

    public RedPacketAdapter1(Context context, List<GrabRedInfo.DataBean.LogBean> logBeanList, DBManager mgr, String coin, boolean isShowBestLuck) {
        mLogBeanList = logBeanList;
        mContext = context;
        mMgr = mgr;
        this.coin=coin;
        this.isShowBestLuck=isShowBestLuck;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_red_packet, viewGroup, false);
            viewHolder=new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.setData(mLogBeanList.get(i));
        return view;
    }


    class ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_best_luck)
        TextView tvBestLuck;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setData(GrabRedInfo.DataBean.LogBean logBean) {
            mTvName.setText(logBean.getName());
            mTvTime.setText(logBean.getTime());
            mTvMoney.setText(logBean.getMoney()+coin);
            UtilTool.setCircleImg(mContext,logBean.getAvatar(), mIvTouxiang);
            //            mIvTouxiang.setImageBitmap(UtilTool.getImage(mMgr, jid, mContext));
            if(isShowBestLuck&&logBean.getIs_good()==1){
                tvBestLuck.setVisibility(View.VISIBLE);
            }else{
                tvBestLuck.setVisibility(View.GONE);
            }
        }
    }
}
