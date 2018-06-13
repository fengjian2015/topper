package com.bclould.tocotalk.ui.adapter;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.CreateGroupRoomActivity;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/5.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class CreateGroupRVAdapter extends RecyclerView.Adapter {

    private final List<UserInfo> mUserInfos;
    private final CreateGroupRoomActivity mActivity;
    private DBManager mgr;

    public CreateGroupRVAdapter(CreateGroupRoomActivity activity, List<UserInfo> userInfos, DBManager mgr) {
        mUserInfos = userInfos;
        mActivity = activity;
        this.mgr=mgr;
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
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCheckBox.isChecked()){
                        mCheckBox.setChecked(false);
                    }else{
                        mCheckBox.setChecked(true);
                    }
                }
            });
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        mActivity.setData(userInfo, b);
                    } else {
                        mActivity.setData(userInfo, !b);
                    }
                }
            });
        }
    }
}
