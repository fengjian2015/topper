package com.bclould.tea.ui.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.LocationInfo;

import java.util.List;

/**
 * Created by xingyun on 2016/7/19.
 */
public class ScreenListPopWindow extends PopupWindow {

    public interface ListOnClick{
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         * @param position
         */
        void onclickitem(int position);
    }

    private ListView lv_menu;
    private LinearLayout relativeLayout;
    /**
     * 取消按钮
     */
    private ListOnClick listOnClick;
    private List<LocationInfo.DataBean> menunames;
    private Context context;
    public MyMenuAdapter myMenuAdapter;


    public ScreenListPopWindow(Context context, List<LocationInfo.DataBean> menunames) {
        final View view = View.inflate(context, R.layout.pop_screen_list, null);
        this.menunames = menunames;
        this.context = context;
//        int popupWidth = mView.getMeasuredWidth();
//        int popupHeight = mView.getMeasuredHeight();

        lv_menu = (ListView) view.findViewById(R.id.lv_menu);

        myMenuAdapter=new MyMenuAdapter();
        lv_menu.setAdapter(myMenuAdapter);

        setContentView(view);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(600);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

//        final int[] location = new int[2];
//        mView.getLocationOnScreen(location);
//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        int windowWidth  = dm.widthPixels;
//        int x=windowWidth-location[0]-popupWidth;
//        view.setPadding(context.getResources().getDimensionPixelSize(R.dimen.x20),0,context.getResources().getDimensionPixelSize(R.dimen.x20),popupHeight);
//        showAtLocation(mView,Gravity.BOTTOM|Gravity.RIGHT,x,popupHeight);

        relativeLayout=view.findViewById(R.id.rl_data);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setList(List<LocationInfo.DataBean> menuname){
        menunames.clear();
        menunames.addAll(menuname);
        notifyDataSetChanged();
    }


    public void notifyDataSetChanged(){
        myMenuAdapter.notifyDataSetChanged();
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
            final ViewHolder holder;
            if(convertView==null) {
                convertView= View.inflate(context, R.layout.pop_location_menulist_item, null);
                holder=new ViewHolder();
                convertView.setTag(holder);
                holder.textView=convertView.findViewById(R.id.title);
                holder.textContent=convertView.findViewById(R.id.content);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(menunames.get(position).getTitle());
            holder.textContent.setText(menunames.get(position).getAddress());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listOnClick != null) {
                        listOnClick.onclickitem(position);
                    }
                }
            });
            return convertView;
        }
        class ViewHolder{
            TextView textView;
            TextView textContent;
        }
    }



    public void setListOnClick(ListOnClick listOnClick){
        this.listOnClick = listOnClick;
    }


}
