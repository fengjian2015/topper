package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

/**
 * Created by GIjia on 2018/6/20.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class GroupDetailsMemberAdapter extends BaseAdapter{
    private Context context;
    private List<RoomMemberInfo> list;
    public GroupDetailsMemberAdapter(Context context, List<RoomMemberInfo> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            view=View.inflate(context, R.layout.group_member_item,null);
            viewHolder=new ViewHolder();
            viewHolder.group_touxiang= (ImageView) view.findViewById(R.id.group_touxiang);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        if(i==list.size()-1){
            viewHolder.group_touxiang.setImageResource(R.mipmap.add_member);
        }else{
            UtilTool.getImage(context,viewHolder.group_touxiang,list.get(i).getImage_url());
        }
        return view;
    }

    class ViewHolder{
        ImageView group_touxiang;
    }
}
