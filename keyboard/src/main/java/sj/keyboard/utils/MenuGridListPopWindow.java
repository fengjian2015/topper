package sj.keyboard.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.keyboard.view.R;
import java.util.ArrayList;
import sj.keyboard.XhsEmoticonsKeyBoard;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.MyGridView;

import static com.keyboard.view.R.style.BottomDialog;

/**
 * 用於會話功能盤彈出，隨時可能刪除
 */
public class MenuGridListPopWindow extends Dialog {

    public interface ListOnClick{
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         * @param position
         */
        public void onclickitem(String name);
    }

    private MyGridView gv_menu;
    /**
     * 取消按钮
     */
    private Button button_common3;
    private ListOnClick listOnClick;
    private LinearLayout quit_popupwindows_bg;
    ArrayList<AppInfo> mAppBeanList = new ArrayList<>();
    private Context context;
    private int color = 0;
    private String roomType;

    public MenuGridListPopWindow(Context context, EmoticonsEditText etChat, String roomType) {
        super(context, R.style.BottomDialog2);
        View view = View.inflate(context, R.layout.menugridlist_popwindow, null);
        this.context = context;
        this.roomType=roomType;
        gv_menu = (MyGridView) view.findViewById(R.id.gv_menu);
        setList();
        gv_menu.setAdapter(new MyMenuAdapter());
        button_common3 = (Button) view.findViewById(R.id.button_common3);
        quit_popupwindows_bg = (LinearLayout) view.findViewById(R.id.quit_popupwindows_bg);

        button_common3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOnClick != null) {
                    listOnClick.onclickitem(null);
                    dismiss();
                }
            }
        });

        quit_popupwindows_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOnClick != null) {
                    listOnClick.onclickitem(null);
                    dismiss();
                }
            }
        });

        //获得dialog的window窗口
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        setContentView(view);

    }

    private void setList() {
        mAppBeanList.add(new AppInfo(R.drawable.icon_tail_photo, context.getString(R.string.image)));
        mAppBeanList.add(new AppInfo(R.drawable.icon_tail_camera, context.getString(R.string.shooting)));
        if(!XhsEmoticonsKeyBoard.ROOM_TYPE_MULTI.equals(roomType)) {
            mAppBeanList.add(new AppInfo(R.drawable.icon_tail_transfer, context.getString(R.string.transfer)));
        }
        mAppBeanList.add(new AppInfo(R.drawable.icon_tail_red_envelope, context.getString(R.string.red_package)));
        mAppBeanList.add(new AppInfo(R.drawable.icon_tail_position, context.getString(R.string.location)));
        mAppBeanList.add(new AppInfo(R.drawable.icon_tail_file,context.getString(R.string.file)));
        mAppBeanList.add(new AppInfo(R.drawable.icon_collect,context.getString(R.string.collect)));
    }

    public void showAtLocation(){
        show();
    }

    public void setColor(int color) {
        this.color = color;
    }

    private class MyMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAppBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.item_app, null);
            TextView textView= (TextView) view.findViewById(R.id.tv_name);
            ImageView imageView= (ImageView) view.findViewById(R.id.iv_icon);
            textView.setText(mAppBeanList.get(position).getFuncName());
            imageView.setImageResource(mAppBeanList.get(position).getIcon());
            if (color!=0){
                textView.setTextColor(color);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listOnClick != null) {
                        listOnClick.onclickitem(mAppBeanList.get(position).getFuncName());
                    }
                    dismiss();
                }
            });
            return view;
        }
    }

    public void setListOnClick(ListOnClick listOnClick){
        this.listOnClick = listOnClick;
    }


}
