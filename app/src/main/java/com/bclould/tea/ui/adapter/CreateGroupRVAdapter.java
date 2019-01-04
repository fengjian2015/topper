package com.bclould.tea.ui.adapter;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.activity.CreateGroupRoomActivity;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/5.
 */
public class CreateGroupRVAdapter extends RecyclerView.Adapter {

    private final List<UserInfo> mUserInfos;
    private final CreateGroupRoomActivity mActivity;
    private DBManager mgr;
    private String roomId;
    private DBRoomMember mDBRoomMember;
    private List<UserInfo> userInfoList;
    private String tocoId;

    public CreateGroupRVAdapter(CreateGroupRoomActivity activity, List<UserInfo> userInfos, DBManager mgr, String roomId, DBRoomMember mDBRoomMember, List<UserInfo> userInfoList, String tocoId) {
        mUserInfos = userInfos;
        mActivity = activity;
        this.mgr=mgr;
        this.roomId=roomId;
        this.mDBRoomMember=mDBRoomMember;
        this.userInfoList=userInfoList;
        this.tocoId=tocoId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_create_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mUserInfos.get(position));
    }

    @Override
    public int getItemCount() {
        if (mUserInfos != null) {
            return mUserInfos.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.check_box)
        CheckBox mCheckBox;
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.rl)
        RelativeLayout rl;
        private String mUser;
        private String mName;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final UserInfo userInfo) {
            String remark = userInfo.getRemark();
            mUser = userInfo.getUser();
            mName= userInfo.getUserName();
            if (!StringUtils.isEmpty(remark)) {
                mTvName.setText(remark);
            } else
                mTvName.setText(mName);
            UtilTool.getImage(mgr, userInfo.getUser(),mActivity , mIvTouxiang);
            mIvTouxiang.setImageBitmap(BitmapFactory.decodeFile(userInfo.getPath()));
            if(roomId!=null&&mDBRoomMember.findMember(roomId,mUser)){
                rl.setOnClickListener(null);
                mCheckBox.setOnClickListener(null);
                mCheckBox.setChecked(true);
                mCheckBox.setEnabled(false);
            }else if(userInfo.getUser().equals(tocoId)){
                rl.setOnClickListener(null);
                mCheckBox.setOnClickListener(null);
                mCheckBox.setChecked(true);
                mCheckBox.setEnabled(false);
            }else {
                if(userInfoList.contains(userInfo)) {
                    mCheckBox.setChecked(true);
                }else{
                    mCheckBox.setChecked(false);
                }
                mCheckBox.setEnabled(true);
                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mCheckBox.isChecked()) {
                            mCheckBox.setChecked(false);
                        } else {
                            mCheckBox.setChecked(true);
                        }
                        mActivity.setData(userInfo, mCheckBox.isChecked());
                    }
                });
                mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.setData(userInfo, mCheckBox.isChecked());
                    }
                });
            }
        }
    }
}
