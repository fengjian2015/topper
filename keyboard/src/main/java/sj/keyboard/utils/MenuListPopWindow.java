package sj.keyboard.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.keyboard.view.R;

import java.util.ArrayList;
import java.util.List;

import sj.keyboard.XhsEmoticonsKeyBoard;
import sj.keyboard.widget.EmoticonsEditText;

import static com.keyboard.view.R.style.BottomDialog;

/**
 * 用於會話功能盤彈出，隨時可能刪除
 */
public class MenuListPopWindow extends Dialog {

    public interface ListOnClick{
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         * @param position
         */
        public void onclickitem(String name);
    }

    private ListView lv_menu;
    /**
     * 取消按钮
     */
    private Button button_common3;
    private ListOnClick listOnClick;
    private LinearLayout quit_popupwindows_bg;
    private List<String> menunames=new ArrayList<>();
    private Context context;
    private int color = 0;
    private String roomType;

    public MenuListPopWindow(Context context, EmoticonsEditText etChat, String roomType) {
        super(context, R.style.BottomDialog2);
        View view = View.inflate(context, R.layout.menulist_popwindow, null);
        this.context = context;
        this.roomType=roomType;
        lv_menu = (ListView) view.findViewById(R.id.lv_menu);
        setList();
        lv_menu.setAdapter(new MyMenuAdapter());
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
        menunames.add(context.getString(R.string.image));
        menunames.add(context.getString(R.string.shooting));
        if(!XhsEmoticonsKeyBoard.ROOM_TYPE_MULTI.equals(roomType)) {
            menunames.add(context.getString(R.string.transfer));
        }
        menunames.add(context.getString(R.string.red_package));
        menunames.add(context.getString(R.string.location));
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
                        listOnClick.onclickitem(menunames.get(position));
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
