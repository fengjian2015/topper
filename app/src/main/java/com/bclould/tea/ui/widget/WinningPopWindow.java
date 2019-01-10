package com.bclould.tea.ui.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bclould.tea.R;

/**
 * Created by xingyun on 2016/7/19.
 */
public class WinningPopWindow extends PopupWindow {


    private TextView tv_content;
    private ImageView mImageView;

    public WinningPopWindow(Context context, String content) {
        View view = View.inflate(context, R.layout.title_winning_notice, null);
        tv_content=view.findViewById(R.id.tv_content);
        mImageView=view.findViewById(R.id.iv_shut_down);
        tv_content.setText(content);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(context.getResources().getDimensionPixelSize(R.dimen.y100));
        setFocusable(false);
        setOutsideTouchable(false);
        setBackgroundDrawable(new BitmapDrawable());
        //将设置好的属性set回去
        setContentView(view);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show(final View mView){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showAsDropDown(mView, 0, 2);
            }
        },500);

    }
}
