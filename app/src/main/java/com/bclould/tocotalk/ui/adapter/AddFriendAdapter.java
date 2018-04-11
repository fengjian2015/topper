package com.bclould.tocotalk.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.AddFriendActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.impl.JidCreate;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddFriendAdapter extends BaseAdapter {

    private final List<String> mRowList;
    private final AddFriendActivity mAddFriendActivity;
    private ViewHolder mViewHolder;

    public AddFriendAdapter(AddFriendActivity addFriendActivity, List<String> rowList) {
        mAddFriendActivity = addFriendActivity;
        mRowList = rowList;
    }

    @Override
    public int getCount() {
        if (mRowList != null)
            return mRowList.size();
        return 0;
    }

    @Override
    public Object getItem(int itemId) {
        return mRowList.get(itemId);
    }

    @Override
    public long getItemId(int itemId) {
        return itemId;
    }

    @Override
    public View getView(final int itemId, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mAddFriendActivity).inflate(R.layout.item_search, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        String user = mRowList.get(itemId);
        byte[] userImage = UtilTool.getUserImage(XmppConnection.getInstance().getConnection(), user + "@" + Constants.DOMAINNAME);
        if (userImage != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(userImage, 0, userImage.length);
            mViewHolder.mIvTouxiang.setImageBitmap(bitmap);
        }
        mViewHolder.mTvName.setText(user);
        mViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Roster.getInstanceFor(XmppConnection.getInstance().getConnection()).createEntry(JidCreate.entityBareFrom(mRowList.get(itemId) + "@" + Constants.DOMAINNAME), null, new String[]{"Friends"});
                    mAddFriendActivity.finish();
                    Toast.makeText(mAddFriendActivity, "发送请求成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(mAddFriendActivity, "发送请求失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    static class ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.btn_add)
        Button mBtnAdd;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
