package com.bclould.tea.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.DynamicListInfo;
import com.bclould.tea.ui.activity.IndividualDetailsActivity;
import com.bclould.tea.utils.CircleMovementMethod;
import com.bclould.tea.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by GA on 2018/6/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CommentsView extends LinearLayout {

    private Context mContext;
    private List<DynamicListInfo.DataBean.ReviewListBean> mDatas;
    private onItemClickListener listener;
    private int mDynamicId;

    public CommentsView(Context context) {
        this(context, null);
    }

    public CommentsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        this.mContext = context;
    }

    /**
     * 设置评论列表信息
     *
     * @param list
     * @param dynamicId
     */
    public void setList(List<DynamicListInfo.DataBean.ReviewListBean> list, int dynamicId) {
        mDatas = list;
        mDynamicId = dynamicId;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public void notifyDataSetChanged() {
        removeAllViews();
        if (mDatas == null || mDatas.size() <= 0) {
            return;
        }
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);
        for (int i = 0; i < mDatas.size(); i++) {
            View view = getView(i);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }
            addView(view, i, layoutParams);
        }
    }

    private View getView(final int position) {
        final DynamicListInfo.DataBean.ReviewListBean item = mDatas.get(position);
        DynamicListInfo.DataBean.ReviewListBean.UsersBean replyUser = item.getReplyUser();
        String replyName = replyUser.getUser_name();
        boolean hasReply = false;   // 是否有回复
        if (!replyName.isEmpty()) {
            hasReply = true;
        }
        TextView textView = new TextView(mContext);
        textView.setTextSize(15);
        textView.setTextColor(0xff686868);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        DynamicListInfo.DataBean.ReviewListBean.UsersBean commentsUser = item.getCommentsUser();
        String commentsName = commentsUser.getUser_name();
        if (hasReply) {
            builder.append(setClickableSpan(commentsName, commentsUser.getToco_id()));
            builder.append(setClickableSpanContent(" " + mContext.getString(R.string.reply) + " ", position));
            builder.append(setClickableSpan(replyName, replyUser.getToco_id()));
        } else {
            builder.append(setClickableSpan(commentsName, commentsUser.getToco_id()));
        }
        builder.append(" : ");
        builder.append(setClickableSpanContent(item.getContent(), position));
        textView.setText(builder);
        // 设置点击背景色
        textView.setHighlightColor(getResources().getColor(android.R.color.transparent));
//        textView.setHighlightColor(0xff000000);

        textView.setMovementMethod(new CircleMovementMethod(0xffcccccc, 0xffcccccc));

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position, item);
                }
            }
        });

        return textView;
    }

    /**
     * 设置评论内容点击事件
     *
     * @param item
     * @param position
     * @return
     */
    public SpannableString setClickableSpanContent(final String item, final int position) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.comment));
                messageEvent.setId(mDatas.get(position).getId() + "");
                messageEvent.setState(mDynamicId + "");
                messageEvent.setType(true);
                messageEvent.setCoinName(mDatas.get(position).getCommentsUser().getUser_name());
                EventBus.getDefault().post(messageEvent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的内容文本颜色
                ds.setColor(0xff000000);
                ds.setTextSize(mContext.getResources().getDimension(R.dimen.x30));
                ds.setUnderlineText(false);
            }
        };
        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    /**
     * 设置评论用户名字点击事件
     *
     * @param item
     * @param toco_id
     * @return
     */
    public SpannableString setClickableSpan(final String item, final String toco_id) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                intent.putExtra("roomId", toco_id);
                intent.putExtra("name", item);
                intent.putExtra("user", toco_id);
                mContext.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的用户名文本颜色
                ds.setColor(0xff60798e);
                ds.setTextSize(mContext.getResources().getDimension(R.dimen.x32));
                ds.setUnderlineText(false);
            }
        };

        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    /**
     * 定义一个用于回调的接口
     */
    public interface onItemClickListener {
        void onItemClick(int position, DynamicListInfo.DataBean.ReviewListBean bean);
    }
}
