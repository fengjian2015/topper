package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.ReceiptPaymentPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.ProvinceBean;
import com.bclould.tocotalk.model.TransferListInfo;
import com.bclould.tocotalk.ui.adapter.PayManageGVAdapter;
import com.bclould.tocotalk.ui.adapter.PayRecordRVAdapter;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/3/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayRecordActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_filtrate)
    TextView mTvFiltrate;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.rl_date_selector)
    RelativeLayout mRlDateSelector;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private PayRecordRVAdapter mPayRecordRVAdapter;
    private Dialog mBottomDialog;
    String mPage = "1";
    String mPageSize = "1000";
    String mType = "";
    String mDate = "";
    private Map<String, Integer> mMap = new HashMap<>();
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;
    private List<String> mFiltrateList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record);
        ButterKnife.bind(this);
        mType = getResources().getString(R.string.all);
        mFiltrateList.add(getString(R.string.all));
        mFiltrateList.add(getString(R.string.red_package));
        mFiltrateList.add(getString(R.string.transfer));
        mFiltrateList.add(getString(R.string.receipt_payment));
        mFiltrateList.add(getString(R.string.in_coin));
        mFiltrateList.add(getString(R.string.out_coin));
        mFiltrateList.add(getString(R.string.bank_card));
        mFiltrateList.add(getString(R.string.ru_zhang));
        mFiltrateList.add(getString(R.string.qi_ta));
        getOptionData();
        mTvDate.setText(mDate);
        mReceiptPaymentPresenter = new ReceiptPaymentPresenter(this);
        initRecycler();
        initMap();
        initData();
    }

    private void initMap() {
        mMap.put(getString(R.string.filtrate), 0);
    }

    List<TransferListInfo.DataBean> mDataBeanList = new ArrayList<>();

    private void initData() {
        mDataBeanList.clear();
        mReceiptPaymentPresenter.transRecord(mPage, mPageSize, mType, mDate, new ReceiptPaymentPresenter.CallBack4() {
            @Override
            public void send(List<TransferListInfo.DataBean> data) {
                mDataBeanList.addAll(data);
                mPayRecordRVAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPayRecordRVAdapter = new PayRecordRVAdapter(this, mDataBeanList);
        mRecyclerView.setAdapter(mPayRecordRVAdapter);
    }

    @OnClick({R.id.bark, R.id.rl_date_selector, R.id.tv_filtrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_date_selector:
                initOptionPicker();
                break;
            case R.id.tv_filtrate:
                showFiltrateDialog();
                break;
        }
    }

    private OptionsPickerView pvOptions;
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private String[] mDateArr = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    private void initOptionPicker() {
        if (pvOptions == null) {
            pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    mDate = options1Items.get(options1).getPickerViewText()
                            + "-" + options2Items.get(options1).get(options2);
                    mTvDate.setText(mDate);
                    initData();
                }
            })
                    .setContentTextSize(20)//设置滚轮文字大小
                    .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                    .setSelectOptions(0, 0)//默认选中项
                    .setBgColor(getColor(R.color.white))
                    .setTitleBgColor(getColor(R.color.gray2))
                    .setCancelColor(Color.BLACK)
                    .setSubmitColor(getColor(R.color.blue2))
                    .setTextColorCenter(Color.BLACK)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setLabels(getString(R.string.year), getString(R.string.month), getString(R.string.day))
                    .setBackgroundId(0x00000000) //设置外部遮罩颜色
                    .build();
        }
        pvOptions.show();
        pvOptions.setPicker(options1Items, options2Items);//二级选择器
    }

    private void getOptionData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = new Date(System.currentTimeMillis());
        mDate = simpleDateFormat.format(date);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM");
        Date date2 = new Date(System.currentTimeMillis());
        int month = Integer.parseInt(simpleDateFormat2.format(date2));
        //选项1
        options1Items.add(new ProvinceBean(0, "2018", "描述部分", "其他数据"));
        options1Items.add(new ProvinceBean(1, "2017", "描述部分", "其他数据"));
        options1Items.add(new ProvinceBean(2, "2016", "描述部分", "其他数据"));

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        for (String s : mDateArr) {
            if (month >= Integer.parseInt(s)) {
                options2Items_01.add(s);
            }
        }
        Collections.reverse(options2Items_01);
        ArrayList<String> options2Items_02 = new ArrayList<>();
        options2Items_02.add("12");
        options2Items_02.add("11");
        options2Items_02.add("10");
        options2Items_02.add("09");
        options2Items_02.add("08");
        options2Items_02.add("07");
        options2Items_02.add("06");
        options2Items_02.add("05");
        options2Items_02.add("04");
        options2Items_02.add("03");
        options2Items_02.add("02");
        options2Items_02.add("01");
        ArrayList<String> options2Items_03 = new ArrayList<>();
        options2Items_02.add("12");
        options2Items_02.add("11");
        options2Items_02.add("10");
        options2Items_02.add("09");
        options2Items_02.add("08");
        options2Items_02.add("07");
        options2Items_02.add("06");
        options2Items_02.add("05");
        options2Items_02.add("04");
        options2Items_02.add("03");
        options2Items_02.add("02");
        options2Items_02.add("01");
        options2Items.add(options2Items_01);
        options2Items.add(options2Items_02);
        options2Items.add(options2Items_03);

        /*--------数据源添加完毕---------*/
    }

    //显示账单筛选dialog


    private void showFiltrateDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bill, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        GridView gridView = (GridView) mBottomDialog.findViewById(R.id.grid_view);
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        gridView.setAdapter(new PayManageGVAdapter(this, mFiltrateList, mMap, new PayManageGVAdapter.CallBack() {
            //接口回调
            @Override
            public void send(int position, String typeName) {
                mType = typeName;
                initData();
                mMap.put(getString(R.string.filtrate), position);
                mBottomDialog.dismiss();
            }
        }));
    }
}
