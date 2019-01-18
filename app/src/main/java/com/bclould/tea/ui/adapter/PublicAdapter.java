package com.bclould.tea.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.widget.PublicItemView;
import com.bclould.tea.utils.ChatTimeUtil;
import com.bclould.tea.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/9.
 */

public class PublicAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<MessageInfo> mMessageList;


    public PublicAdapter(Context context, List<MessageInfo> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.item_public, parent, false);
        holder = new PublicContent(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PublicContent publicContent = (PublicContent) holder;
        publicContent.setData(mMessageList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mMessageList.size() != 0) {
            return mMessageList.size();
        }
        return 0;
    }

    private void setCreatetime(View view, String currentShowTime) {
        TextView createtime = (TextView) view.findViewById(R.id.chat_createtime);
        if (StringUtils.isEmpty(currentShowTime)) {
            createtime.setVisibility(View.GONE);
        } else {
            createtime.setText(ChatTimeUtil.createChatShowTime(currentShowTime));
            createtime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageList.get(position).getMsgType();
    }

    class PublicContent extends RecyclerView.ViewHolder {
        @Bind(R.id.chat_createtime)
        View tvCreateTime;
        @Bind(R.id.ll_order_msg)
        PublicItemView mLlOrderMsg;
        public PublicContent(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());

            int min=0;
            int max=3;
            Random random = new Random();
            int num = random.nextInt(max)%(max-min+1) + min;
            HashMap map=new HashMap();
            map.put("url","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547730089751&di=6664d92cf712aaec2cca161fcf1ffb13&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Ff7246b600c338744acb2ecf35c0fd9f9d62aa0ce.jpg");

            List<Map> list=new ArrayList<>();
            for(int i=0;i<num;i++){
                Map map1=new HashMap();
                map1.put("url","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547730089751&di=6664d92cf712aaec2cca161fcf1ffb13&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Ff7246b600c338744acb2ecf35c0fd9f9d62aa0ce.jpg");
                map1.put("content","我是测试数据"+i);
                list.add(map1);
            }
            mLlOrderMsg.setView(map,list);
        }
    }
}

