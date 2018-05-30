package com.bclould.tocotalk.ui.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bclould.tocotalk.R;

import java.util.List;

/**
 * Created by xingyun on 2016/7/19.
 */
public class MenuListPopWindow extends PopupWindow {

    public interface ListOnClick{
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         * @param position
         */
        public void onclickitem(int position);
    }

    private ListView lv_menu;
    /**
     * 取消按钮
     */
    private Button button_common3;
    private ListOnClick listOnClick;
    private LinearLayout quit_popupwindows_bg;
    private List<String> menunames;
    private Context context;
    private int color = 0;

    public MenuListPopWindow(Context context, List<String> menunames){
        View view = View.inflate(context, R.layout.menulist_popwindow, null);
        view.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_ins));
        this.menunames = menunames;
        this.context = context;

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);

        lv_menu = (ListView) view.findViewById(R.id.lv_menu);
        lv_menu.setAdapter(new MyMenuAdapter());
        button_common3 = (Button) view.findViewById(R.id.button_common3);
        quit_popupwindows_bg = (LinearLayout) view.findViewById(R.id.quit_popupwindows_bg);

        button_common3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOnClick != null) {
                    listOnClick.onclickitem(0);
                }
            }
        });

        quit_popupwindows_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOnClick != null) {
                    listOnClick.onclickitem(0);
                }
            }
        });

    }

    public void setBgBackgroundColor(int color,float alpha){
        quit_popupwindows_bg.setBackgroundColor(color);
        quit_popupwindows_bg.setAlpha(alpha);
    }

    public void setColor(int color) {
        this.color = color;
    }

    private class MyMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menunames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.menulist_item, null);
            Button button = (Button) view.findViewById(R.id.button_common2);
            button.setText(menunames.get(position));
            if (color!=0){
                button.setTextColor(color);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listOnClick != null) {
                        listOnClick.onclickitem(position + 1);
                    }
                }
            });

            return view;
        }
    }

    public void setListOnClick(ListOnClick listOnClick){
        this.listOnClick = listOnClick;
    }


}
