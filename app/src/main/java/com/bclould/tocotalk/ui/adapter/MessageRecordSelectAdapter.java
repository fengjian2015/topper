package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.DATE_MSG;
import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.IMAGE_MSG;
import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.TRADE_MSG;
import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.VIDEO_MSG;

/**
 * Created by GIjia on 2018/5/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MessageRecordSelectAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Integer> selectList;
    private OnItemClickListener onItemClickListener;

    public MessageRecordSelectAdapter(Context context, List<Integer> selectList) {
        this.context = context;
        this.selectList = selectList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BodyViewHolder(LayoutInflater.from(context).inflate(R.layout.record_select_item, null));
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int msg=selectList.get(position);
        String msgcontent = "";
        if(msg== DATE_MSG){
            msgcontent=context.getString(R.string.date);
        }else if(msg==IMAGE_MSG){
            msgcontent=context.getString(R.string.image);
        }else if(msg==VIDEO_MSG){
            msgcontent=context.getString(R.string.video);
        }else if(msg==TRADE_MSG){
            msgcontent=context.getString(R.string.trade);
        }
        ((BodyViewHolder) holder).getTvContent().setText(msgcontent);
        ((BodyViewHolder) holder).getTvContent().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(((BodyViewHolder) holder).getTvContent(),position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectList.size();
    }

    /**
     * 给GridView中的条目用的ViewHolder
     */
    public class BodyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_content)
        TextView tvContent;
        public BodyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public TextView getTvContent() {
            return tvContent;
        }
    }

    public void addOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public interface OnItemClickListener{
         void onItemClick(View view, int postion);
    }
}
