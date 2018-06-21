package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.ui.activity.CreateGroupRoomActivity;
import com.bclould.tea.ui.activity.IndividualDetailsActivity;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

/**
 * Created by GIjia on 2018/6/20.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class GroupDetailsMemberAdapter extends BaseAdapter{
    private Context context;
    private List<RoomMemberInfo> list;
    private  String roomId;
    public GroupDetailsMemberAdapter(Context context, List<RoomMemberInfo> list, String roomId) {
        this.context=context;
        this.list=list;
        this.roomId=roomId;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
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

        viewHolder.group_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i==list.size()-1){
                    //添加
                    Intent intent = new Intent(context, CreateGroupRoomActivity.class);
                    intent.putExtra("type",1);
                    intent.putExtra("roomId",roomId);
                    context.startActivity(intent);
                }else{
                    //查看好友詳情
                    Intent intent = new Intent(context, IndividualDetailsActivity.class);
                    intent.putExtra("user", list.get(i).getJid());
                    intent.putExtra("name", list.get(i).getName());
                    intent.putExtra("roomId", list.get(i).getJid());
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }

    class ViewHolder{
        ImageView group_touxiang;
    }
}
