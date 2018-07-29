package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.PublicMenuInfo;

import java.util.List;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by xingyun on 2016/7/19.
 */
public class MenuListPopWindow1 extends PopupWindow {

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
    private ListOnClick listOnClick;
    private List<PublicMenuInfo.ButtonBean.SubButtonBean> menunames;
    private Context context;
    private int color = 0;

    public MenuListPopWindow1(Context context, final View mView, List<PublicMenuInfo.ButtonBean.SubButtonBean> menunames) {
        final View view = View.inflate(context, R.layout.pop_public_list, null);
        this.menunames = menunames;
        this.context = context;
        int popupWidth = mView.getMeasuredWidth();
        int popupHeight = mView.getMeasuredHeight();

        lv_menu = (ListView) view.findViewById(R.id.lv_menu);
        lv_menu.setAdapter(new MyMenuAdapter());
        setContentView(view);
        setWidth(popupWidth);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        final int[] location = new int[2];
        mView.getLocationOnScreen(location);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int windowWidth  = dm.widthPixels;
        int x=windowWidth-location[0]-popupWidth;
        view.setPadding(context.getResources().getDimensionPixelSize(R.dimen.x20),0,context.getResources().getDimensionPixelSize(R.dimen.x20),popupHeight);
        showAtLocation(mView,Gravity.BOTTOM|Gravity.RIGHT,x,popupHeight);

        RelativeLayout relativeLayout=view.findViewById(R.id.rl_data);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
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
            return menunames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.public_menulist_item, null);
            View line=view.findViewById(R.id.view);
            if(position==menunames.size()-1){
                line.setVisibility(View.GONE);
            }else {
                line.setVisibility(View.VISIBLE);
            }
            TextView textView = view.findViewById(R.id.text);
            textView.setText(menunames.get(position).getName());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listOnClick != null) {
                        dismiss();
                        listOnClick.onclickitem(position );
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
