package com.bclould.tea.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fengjian on 2019/1/17.
 */

public class PublicItemView extends LinearLayout{
    private Context mContext;
    private List<View> subItemList=new ArrayList<>();
    private View title;
    public PublicItemView(Context context) {
        super(context);
        mContext=context;
    }

    public PublicItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public PublicItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }

    public void setView(List<Map> menuList){
        if(menuList==null) {
            menuList=new ArrayList<>();
        }
        //当传入的list比subItems要小时，不需要创建item，只要赋值和隐藏
        if(menuList.size()<=subItemList.size()){
            for(int i = 0;i < subItemList.size(); i++){
                View view=subItemList.get(i);
                if(i==0){
                    setTop(menuList.get(i),menuList.size()<=1);
                    continue;
                }
                if(i<menuList.size()){
                    setData(view,menuList.get(i),i==menuList.size()-1);
                }else{
                    view.setVisibility(View.GONE);
                }
            }
            return;
        }
        for(int i=0;i<menuList.size();i++){
            Map contentMap=menuList.get(i);
            View view;
            if(i<subItemList.size()){
                if(i==0){
                    setTop(menuList.get(i),menuList.size()<=1);
                    continue;
                }
                view=subItemList.get(i);
                setData(view,contentMap,i==menuList.size()-1);
            }else{
                view=View.inflate(mContext, R.layout.item_public_list,null);
                subItemList.add(view);
                if(i==0){
                    setTop(menuList.get(i),menuList.size()<=1);
                    continue;
                }
                addView(view);
                setData(view,contentMap,i==menuList.size()-1);
            }
        }
    }

    private void setData(View view, final Map contentMap, boolean isLast){
        view.setVisibility(VISIBLE);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(contentMap);
            }
        });
        TextView content=view.findViewById(R.id.tv_content);
        ImageView imageView=view.findViewById(R.id.iv_image);
        View line=view.findViewById(R.id.view_line);
        content.setText(contentMap.get("content")+"");
        UtilTool.setImage(contentMap.get("url")+"",mContext,imageView,-1);
        if(isLast)
            line.setVisibility(View.GONE);
        else
            line.setVisibility(View.VISIBLE);
    }

    private void setTop(Map map, boolean isOne){
        if(title==null){
            title=View.inflate(mContext, R.layout.item_public_list_top,null);
            addView(title);
        }
        ImageView imageView=title.findViewById(R.id.iv_image);
        TextView tv_content1=title.findViewById(R.id.tv_content1);
        TextView tv_content=title.findViewById(R.id.tv_content);
        if(isOne){
            tv_content1.setVisibility(GONE);
            tv_content.setVisibility(VISIBLE);
            tv_content.setText(map.get("content")+"");
        }else{
            tv_content1.setVisibility(VISIBLE);
            tv_content.setVisibility(GONE);
            tv_content1.setText(map.get("content")+"");
        }
        UtilTool.setImage(map.get("url")+"",mContext,imageView,-1);
        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTopClick();
            }
        });
    }

    private void onTopClick(){
        ToastShow.showToast2(mContext,"头部图片");
    }

    private void onItemClick(Map contentMap){
        ToastShow.showToast2(mContext,contentMap.get("content")+"");
    }
}

