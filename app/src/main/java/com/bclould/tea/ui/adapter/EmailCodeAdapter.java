package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBUserCode;
import com.bclould.tea.ui.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/6/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class EmailCodeAdapter extends BaseAdapter implements Filterable {
    private List<String> mList;
    private ArrayFilter mFilter;
    private Context context;
    private ArrayList<String> mUnfilteredData;
    private DBUserCode DBUserCode;
    public EmailCodeAdapter(List<String> userCodeList, Context context, DBUserCode DBUserCode) {
        this.mList=userCodeList;
        this.context=context;
        this.DBUserCode=DBUserCode;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        final String pc = mList.get(position);
        if (convertView == null) {
            view = View.inflate(context, R.layout.emily_charge_item, null);

            holder = new ViewHolder();
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.clickDelete = (TextView) view.findViewById(R.id.btnClick);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_name.setText(pc);
        holder.clickDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pc != null && !"".equals(pc)) {
                    DBUserCode.deleteUser(pc);
                    mList.remove(pc);
                    notifyDataSetChanged();
                }
            }
        });
        return view;
    }

    static class ViewHolder {
        public TextView tv_name;
        private TextView clickDelete;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            mUnfilteredData = (ArrayList<String>) mList;
            if (prefix == null || prefix.length() == 0) {
                ArrayList<String> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<String> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();
                ArrayList<String> newValues = new ArrayList<String>(count);
                for (int i = 0; i < count; i++) {
                    String pc = unfilteredValues.get(i);
                    if (pc != null && pc.startsWith(prefixString)) {
                        newValues.add(pc);
                    } else if (pc != null && pc.startsWith(prefixString)) {
                        newValues.add(pc);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
                ((LoginActivity)context).updateEditAdapter(mList.size());
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mList = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
