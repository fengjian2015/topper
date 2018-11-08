package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.FinancialInfo;

import java.util.List;

/**
 * Created by GIjia on 2018/11/2.
 */

public class FinancialGridAdapter extends BaseAdapter{
    private Context context;
    private List<FinancialInfo.DataBean.IncomeListsBean> income_lists;
    public FinancialGridAdapter(Context context, List<FinancialInfo.DataBean.IncomeListsBean> income_lists) {
        this.context=context;
        this.income_lists=income_lists;
    }

    @Override
    public int getCount() {
        return income_lists.size();
    }

    @Override
    public Object getItem(int i) {
        return income_lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            view=View.inflate(context, R.layout.item_financial_grid,null);
            holder=new ViewHolder();
            holder.tv_add_current=view.findViewById(R.id.tv_add_current);
            holder.tv_current=view.findViewById(R.id.tv_current);
            holder.tv_title=view.findViewById(R.id.tv_title);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        holder.tv_title.setText(income_lists.get(i).getTitle());
        holder.tv_current.setText(income_lists.get(i).getNumber());
        holder.tv_add_current.setText(income_lists.get(i).getIncome());
        return view;
    }

    class ViewHolder{
        TextView tv_title;
        TextView tv_current;
        TextView tv_add_current;
    }
}
