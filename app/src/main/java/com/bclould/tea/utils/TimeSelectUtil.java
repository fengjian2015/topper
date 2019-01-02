package com.bclould.tea.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.bclould.tea.R;
import com.bclould.tea.model.ProvinceBean;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by fengjian on 2019/1/2.
 */

public class TimeSelectUtil {
    //时间选择
    private OptionsPickerView pvOptions;
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private String[] mDateArr = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private SimpleDateFormat mSimpleDateFormat;

    private Context mContext;
    private String mDate;
    private OnTimeReturnListener mOnTimeReturnListener;

    /**
     *
     * @param context
     * @param number 显示几个年份，不能为最少为1
     */
    public TimeSelectUtil(Context context,int number){
        mContext=context;
        getOptionData(number);
    }

    public void setOnTimeReturnListener(OnTimeReturnListener onTimeReturnListener){
        mOnTimeReturnListener=onTimeReturnListener;
    }

    public void initOptionPicker() {
        if (pvOptions == null) {
            pvOptions = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    if (options2 >= 0) {
                        mDate = options1Items.get(options1).getPickerViewText()
                                + "-" + options2Items.get(options1).get(options2);
                        if(mOnTimeReturnListener==null)return;
                        mOnTimeReturnListener.getTime(mDate);
                    }
                }
            })
                    .setContentTextSize(20)//设置滚轮文字大小
                    .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                    .setSelectOptions(0, 0)//默认选中项
                    .setBgColor(mContext.getResources().getColor(R.color.white))
                    .setTitleBgColor(mContext.getResources().getColor(R.color.gray2))
                    .setCancelColor(Color.BLACK)
                    .setSubmitColor(mContext.getResources().getColor(R.color.blue2))
                    .setTextColorCenter(Color.BLACK)
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setLabels(mContext.getResources().getString(R.string.year), mContext.getResources().getString(R.string.month), mContext.getResources().getString(R.string.day))
                    .setBackgroundId(0x00000000) //设置外部遮罩颜色
                    .build();
            pvOptions.setPicker(options1Items, options2Items);//二级选择器
        }
        pvOptions.show();
    }

    public String getDate(){
        return mDate;
    }

    private void getOptionData(int number) {
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = new Date(System.currentTimeMillis());
        mDate = mSimpleDateFormat.format(date);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM");
        Date date2 = new Date(System.currentTimeMillis());
        int month = Integer.parseInt(simpleDateFormat2.format(date2));

        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        int year = calendar.get(Calendar.YEAR);
        //选项1
        for (int i = 0; i < number; i++) {
            options1Items.add(new ProvinceBean(i, year - i + "", "描述部分", "其他数据"));
        }
        //选项2
        for (int j = 0; j < number; j++) {
            ArrayList<String> options2Items_01 = new ArrayList<>();
            for (String s : mDateArr) {
                if (j != 0) {
                    options2Items_01.add(s);
                } else {
                    if (month >= Integer.parseInt(s)) {
                        options2Items_01.add(s);
                    }
                }
            }
            Collections.reverse(options2Items_01);
            options2Items.add(options2Items_01);
        }
    }

    public interface OnTimeReturnListener{
        void getTime(String time);
    }

    public void dismiss(){
        if (pvOptions != null)
            pvOptions.dismiss();
    }
}
