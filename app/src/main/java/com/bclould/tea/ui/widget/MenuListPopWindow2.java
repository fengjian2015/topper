package com.bclould.tea.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bclould.tea.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjian on 2019/1/10.
 */

public class MenuListPopWindow2 extends PopupWindow{

    private List<HashMap> pop_menu_list=new ArrayList<>();
    private Activity activity;
    private  int popupWidth;

    public static final String CONTENT="content";
    public static final String IMAGE="image";
    private ListOnClick listOnClick;

    public MenuListPopWindow2(Activity activity,int hight,List<HashMap> pop_menu_list){
        this.activity=activity;
        this.pop_menu_list=pop_menu_list;
        final View view = View.inflate(activity, R.layout.pop_menu_list, null);
        popupWidth = view.getMeasuredWidth();
        setData(view);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(hight);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

    }

    private void setData(View view){
        for(int i=0;i<pop_menu_list.size();i++){
            HashMap hashMap=pop_menu_list.get(i);
            TextView textView=new TextView(activity);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,0, 1.0f);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(activity.getResources().getColor(R.color.app_bg_color));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            textView.setText(hashMap.get(CONTENT)+"");
            Drawable drawable = (Drawable)hashMap.get(IMAGE);
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            textView.setCompoundDrawablePadding(activity.getResources().getDimensionPixelSize(R.dimen.x30));
            ((LinearLayout)view).addView(textView);
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listOnClick!=null){
                        listOnClick.onclickitem(finalI);
                    }
                }
            });
        }

    }

    public int getPopupWidth(){
        return popupWidth;
    }

    public static HashMap setHashMapData(Context context, int content, int image){
        HashMap hashMap=new HashMap();
        hashMap.put(CONTENT,context.getResources().getString(content));
        hashMap.put(IMAGE,context.getResources().getDrawable(image));
        return hashMap;
    }

    public void setListOnClick(ListOnClick listOnClick){
        this.listOnClick = listOnClick;
    }

    public interface ListOnClick{
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         * @param position
         */
        public void onclickitem(int position);
    }

}
