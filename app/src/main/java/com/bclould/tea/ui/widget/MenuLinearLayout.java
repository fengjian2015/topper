package com.bclould.tea.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bclould.tea.R;
import com.bclould.tea.model.PublicMenuInfo;
import com.bclould.tea.ui.activity.HTMLActivity;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;

import java.util.List;

/**
 * Created by GIjia on 2018/7/28.
 */

public class MenuLinearLayout extends LinearLayout{
    private PublicMenuInfo mPublicMenuInfo;
    private Context mContext;

    public MenuLinearLayout(Context context) {
        super(context);
        mContext=context;
    }

    public MenuLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public MenuLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }

    public void setMenuData(String menu){
        if(StringUtils.isEmpty(menu)){
            return;
        }
        mPublicMenuInfo= JSON.parseObject(menu,PublicMenuInfo.class);
        if(mPublicMenuInfo.getButton().size()==0){
            return;
        }
        ImageView imageView=new ImageView(mContext);
        LayoutParams layoutParams=new LayoutParams(getResources().getDimensionPixelSize(R.dimen.x100),getResources().getDimensionPixelSize(R.dimen.y100));
        imageView.setLayoutParams(layoutParams);
        imageView.setPadding(getResources().getDimensionPixelSize(R.dimen.x20),0,getResources().getDimensionPixelSize(R.dimen.x20),0);
        imageView.setImageResource(R.mipmap.public_keyboard);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onKeyBodard!=null)
                    onKeyBodard.onClick();
            }
        });

        addView(imageView);

        for(final PublicMenuInfo.ButtonBean buttonBean:mPublicMenuInfo.getButton()){
            View view=View.inflate(getContext(), R.layout.public_list_item,null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,1.0f));
            RelativeLayout relativeLayout=view.findViewById(R.id.rl);
            TextView textView=view.findViewById(R.id.tv_name);
            textView.setText(buttonBean.getName());
            relativeLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(buttonBean.getSub_button()==null||buttonBean.getSub_button().size()<=0){
                        click(buttonBean);
                    }else {
                        showMenuDialog(view, buttonBean.getSub_button());
                    }
                }
            });
            addView(view);
        }
    }

    private void showMenuDialog(View view, final List<PublicMenuInfo.ButtonBean.SubButtonBean> sub_button) {
        new MenuListPopWindow1(getContext(),view,sub_button).setListOnClick(new MenuListPopWindow1.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                click(sub_button.get(position));
            }
        });
    }

    private void click(PublicMenuInfo.ButtonBean buttonBean){
        if("click".equals(buttonBean.getType())){
            ToastShow.showToast2((Activity) getContext(),buttonBean.getKey());
        }else if("view".equals(buttonBean.getType())){
            Intent intent=new Intent(getContext(), HTMLActivity.class);
            intent.putExtra("html5Url",buttonBean.getUrl());
            getContext().startActivity(intent);
        }
    }

    private void click(PublicMenuInfo.ButtonBean.SubButtonBean subButtonBean) {
        if("click".equals(subButtonBean.getType())){
            ToastShow.showToast2((Activity) getContext(),subButtonBean.getKey()+"");
        }else if("view".equals(subButtonBean.getType())){
            Intent intent=new Intent(getContext(), HTMLActivity.class);
            intent.putExtra("html5Url",subButtonBean.getUrl());
            getContext().startActivity(intent);
        }
    }

    private OnKeyBodard onKeyBodard;
    public void setOnKeyBodard(OnKeyBodard onKeyBodard){
        this.onKeyBodard=onKeyBodard;
    }

    public interface OnKeyBodard{
        void onClick();
    }
}
