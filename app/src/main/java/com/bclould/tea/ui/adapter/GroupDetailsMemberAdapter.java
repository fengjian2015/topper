package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.ui.activity.CreateGroupRoomActivity;
import com.bclould.tea.ui.activity.IndividualDetailsActivity;
import com.bclould.tea.ui.activity.SelectGroupMemberActivity;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

/**
 * Created by GIjia on 2018/6/20.
 */
public class GroupDetailsMemberAdapter extends BaseAdapter{
    private Context context;
    private List<RoomMemberInfo> list;
    private  String roomId;
    private DBManager mgr;
    private DBRoomManage mDBRoomManage;
    private DBRoomMember mDBRoomMember;
    public GroupDetailsMemberAdapter(Context context, List<RoomMemberInfo> list, String roomId, DBManager mgr, DBRoomManage mDBRoomManage, DBRoomMember mDBRoomMember) {
        this.context=context;
        this.list=list;
        this.roomId=roomId;
        this.mgr=mgr;
        this.mDBRoomManage=mDBRoomManage;
        this.mDBRoomMember=mDBRoomMember;
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
            viewHolder.tvName=view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        if(isOwner()){
            if (i ==1) {
                viewHolder.group_touxiang.setImageResource(R.mipmap.delete_member);
                viewHolder.tvName.setText(context.getString(R.string.delete_group_member));
            } else if(i ==0){
                viewHolder.group_touxiang.setImageResource(R.mipmap.add_member);
                viewHolder.tvName.setText(context.getString(R.string.add_group_member));
            }else {
                UtilTool.getImage(context, viewHolder.group_touxiang,mDBRoomMember,mgr,list.get(i).getJid());
                String remark = mgr.queryRemark(list.get(i).getJid());
                if (!StringUtils.isEmpty(remark)) {
                    viewHolder.tvName.setText(remark);
                } else {
                    viewHolder.tvName.setText(list.get(i).getName());
                }
            }
        }else {
            if (i == 0) {
                viewHolder.group_touxiang.setImageResource(R.mipmap.add_member);
                viewHolder.tvName.setText(context.getString(R.string.add_group_member));
            } else {
                UtilTool.getImage(context, viewHolder.group_touxiang,mDBRoomMember,mgr, list.get(i).getJid());
                String remark = mgr.queryRemark(list.get(i).getJid());
                if(list.get(i).getJid().equals(UtilTool.getTocoId())){
                    remark="";
                }
                if (!StringUtils.isEmpty(remark)) {
                    viewHolder.tvName.setText(remark);
                } else {
                    viewHolder.tvName.setText(list.get(i).getName());
                }
            }
        }

        viewHolder.group_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOwner()){
                    if(i==1){
                        //刪除
                        deleteIntent();
                    }else if(i==0){
                        addIntent();
                    }else{
                        goIndividual(i);
                    }
                }else{
                    if(i==0){
                        addIntent();
                    }else{
                        goIndividual(i);
                    }
                }
            }
        });
        return view;
    }

    private void deleteIntent(){
        Intent intent = new Intent(context, SelectGroupMemberActivity.class);
        intent.putExtra("type",2);
        intent.putExtra("roomId",roomId);
        context.startActivity(intent);
    }

    private void addIntent(){
        if(list.size()-1>=mDBRoomManage.findRoomNumber(roomId)){
            ToastShow.showToast2((Activity) context,context.getString(R.string.group_member_maxuser));
            return;
        }
        //添加
        Intent intent = new Intent(context, CreateGroupRoomActivity.class);
        intent.putExtra("type",1);
        intent.putExtra("roomId",roomId);
        context.startActivity(intent);
    }

    private void goIndividual(int i){
        //查看好友詳情
        Intent intent = new Intent(context, IndividualDetailsActivity.class);
        intent.putExtra("user", list.get(i).getJid());
        intent.putExtra("name", list.get(i).getName());
        intent.putExtra("roomId", list.get(i).getJid());
        context.startActivity(intent);
    }

    private boolean isOwner(){
        if(UtilTool.getTocoId().equals(mDBRoomManage.findRoomOwner(roomId))){
            return true;
        }else{
            return false;
        }
    }

    class ViewHolder{
        ImageView group_touxiang;
        TextView tvName;
    }
}
