package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.adapter.ChatAdapter;
import com.bclould.tea.ui.adapter.SearchImageRVAdapter;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.litesuits.android.async.AsyncExecutor.handler;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SerchImageActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.et_content)
    EditText mEtContent;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;


    private ArrayList<String> imageList = new ArrayList<>();
    private int page = 0;
    private GridLayoutManager mLayoutManager;
    private SearchImageRVAdapter mSearchImageRVAdapter;
    String content;
    private LoadingProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_image);
        MyApp.getInstance().addActivity(this);
        ButterKnife.bind(this);
        init();
        initAdapter();
    }

    private void init() {
        mEtContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    content = mEtContent.getText().toString();
                    page=0;
                    imageList.clear();
                    showDialog();
                    search();
                }
                return false;
            }
        });
    }

    //初始化适配器
    private void initAdapter() {
        WindowManager wm1 = this.getWindowManager();
        float width = wm1.getDefaultDisplay().getWidth();
        mLayoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSearchImageRVAdapter = new SearchImageRVAdapter(this, imageList,width/3,mHandler);
        mRecyclerView.setAdapter(mSearchImageRVAdapter);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page++;
                search();
            }
        });
    }

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
        }
    }

    private void search() {
        if (StringUtils.isEmpty(content)) {
            ToastShow.showToast2(this, getString(R.string.search_content_notnull));
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    String html5Url2 = "http://image.baidu.com/search/avatarjson?tn=resultjsonavatarnew&ie=utf-8&word=" + content + "&cg=star&pn=" + page * 30 + "&rn=30&itg=0&z=0&fr=&width=&height=&lm=-1&ic=0&s=0&st=-1&gsm=" + Integer.toHexString(page * 30);
                    Document document = Jsoup.connect(html5Url2).data().get();
                    String doco=document.body().text().replace(" ","");
                    //下面的操作是百度的一個坑，fromPageTitle裡面包含""""這樣的，無法解析
                    String[] split =doco.split("fromPageTitle\":\"");
                    StringBuffer stringBuffer=new StringBuffer();
                    for(int i=0;i<split.length;i++){
                        String spl = split[i];
                        if(i==0){
                           stringBuffer.append(spl.substring(0,spl.lastIndexOf(",")));
                       }else if(i==split.length-1){
                            stringBuffer.append(spl.substring(spl.indexOf("bdSourceName")-2,spl.length()));
                       }else{
                          String spl1=spl.substring(spl.indexOf("bdSourceName")-2,spl.length());
                          stringBuffer.append(spl1.substring(0,spl1.lastIndexOf(",")));
                       }
                    }

                    JSONObject jsonObject = JSON.parseObject(stringBuffer.toString());
                    JSONArray jsonArray = (JSONArray) jsonObject.get("imgs");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        imageList.add((String) object.get("objURL"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    hideDialog();
                    mRefreshLayout.finishLoadMore();
                    mSearchImageRVAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    String url= (String) msg.obj;
                    if(StringUtils.isEmpty(url)){
                       ToastShow.showToast2(SerchImageActivity.this,getString(R.string.picture_is_not_loaded));
                        return;
                    }
                    Intent intent=new Intent(SerchImageActivity.this,CropImageActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage(this.getString(R.string.loading));
        }
        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
