package com.bclould.tea.ui.widget;

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
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.FinanciaProductInfo;

import java.util.List;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by xingyun on 2016/7/19.
 */
public class MenuGridPopWindow extends Dialog {

    public interface ListOnClick {
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         *
         * @param position
         */
        public void onclickitem(int position);
    }

    private GridView lv_menu;
    /**
     * 取消按钮
     */
    private Button btn_cancel;
    private ListOnClick listOnClick;
    private List<FinanciaProductInfo.DataBean> menunames;
    private Context context;
    private int color = 0;

    public MenuGridPopWindow(Context context, List<FinanciaProductInfo.DataBean> menunames) {
        super(context, R.style.BottomDialog2);
        View view = View.inflate(context, R.layout.dialog_bill, null);
        this.menunames = menunames;
        this.context = context;
        lv_menu = (GridView) view.findViewById(R.id.grid_view);
        lv_menu.setAdapter(new MyMenuAdapter());
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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

    public void showAtLocation() {
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
            View view = View.inflate(context, R.layout.item_pay_manage, null);
            final TextView mTextview = view.findViewById(R.id.textview);

            mTextview.setText(menunames.get(position).getText());
            mTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTextview.setSelected(true);
                    if (listOnClick != null) {
                        listOnClick.onclickitem(position);
                    }
                   dismiss();
                }
            });
            return view;
        }
    }

    public void setListOnClick(ListOnClick listOnClick) {
        this.listOnClick = listOnClick;
    }


}
