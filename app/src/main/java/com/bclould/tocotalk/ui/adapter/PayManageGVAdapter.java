package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bclould.tocotalk.R;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/20.
 */

public class PayManageGVAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<String> mFiltrateArr;
    private final Map<String, Integer> mMap;
    private final CallBack mCallBack;

    public PayManageGVAdapter(Context context, List<String> filtrateArr, Map<String, Integer> map, CallBack callBack) {
        mContext = context;
        mFiltrateArr = filtrateArr;
        mMap = map;
        mCallBack = callBack;
    }

    @Override
    public int getCount() {
        if (mFiltrateArr.size() != 0)
            return mFiltrateArr.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (mFiltrateArr.get(i) != null)
            return mFiltrateArr.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_pay_manage, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.setData(i);
        return view;
    }


    //定义接口
    public interface CallBack {
        void send(int position, String typeName);
    }

    class ViewHolder {
        @Bind(R.id.textview)
        TextView mTextview;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setData(final int i) {
            mTextview.setText(mFiltrateArr.get(i));
            if (mMap.get(mContext.getString(R.string.filtrate)) == i) {
                mTextview.setSelected(true);
            } else {
                mTextview.setSelected(false);
            }
            mTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTextview.setSelected(true);
                    mCallBack.send(i, mTextview.getText().toString());
                }
            });
        }
    }
}
