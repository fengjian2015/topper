package com.bclould.tocotalk.ui.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.ui.adapter.MessageRecordAdapter;
import com.bclould.tocotalk.ui.adapter.MessageRecordSelectAdapter;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_VIDEO_MSG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationRecordFindActivity extends AppCompatActivity implements MessageRecordSelectAdapter.OnItemClickListener{

    @Bind(R.id.bark)
    ImageView bark;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.iv_delete)
    ImageView ivDelete;
    @Bind(R.id.recycler_select)
    RecyclerView recyclerSelect;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private MessageRecordSelectAdapter selectAdapter;
    private List<Integer> selectList=new ArrayList<>();//以後可以通過後台獲取數據

    private List<MessageInfo> messageInfoList=new ArrayList<>();//消息集合
    private MessageRecordAdapter recordAdapter;

    public static final int DATE_MSG = 1;//時間賽選
    public static final int IMAGE_MSG = 2;//圖片賽選
    public static final int VIDEO_MSG = 3;//視頻賽選
    public static final int TRADE_MSG = 4;//交易賽選
    public static final int TEXT_MSG = 5;//文本賽選

    private DBManager mMdb;
    private String user;
    private String name;
    private int mOffset=1;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_record_find);
        ButterKnife.bind(this);
        init();
        pullToRefresh();
    }

    private void init() {
        user=getIntent().getStringExtra("user");
        name=getIntent().getStringExtra("name");

        mMdb=new DBManager(this);
        selectList.add(DATE_MSG);
        selectList.add(IMAGE_MSG);
        selectList.add(VIDEO_MSG);
        selectList.add(TRADE_MSG);
        //初始化選擇項
        selectAdapter=new MessageRecordSelectAdapter(this,selectList);
        recyclerSelect.setAdapter(selectAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerSelect.setLayoutManager(gridLayoutManager);
        selectAdapter.addOnItemClickListener(this);
        listenerEditText();

        //初始化結果適配器
        recordAdapter=new MessageRecordAdapter(this,messageInfoList,mMdb);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recordAdapter);
    }


    private void pullToRefresh() {
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                mOffset++;
                searchType(type);
            }
        });
    }

    //监听输入框
    private void listenerEditText() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etSearch.getText().toString();
                if (!content.isEmpty()) {
                    changeLayoutVisible(false);
                } else {
                    changeLayoutVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @OnClick({R.id.bark,R.id.iv_delete,R.id.iv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_delete:
                etSearch.setText("");
                changeLayoutVisible(true);
                break;
            case R.id.iv_search:
                searchType(TEXT_MSG);
                break;
        }
    }

    private void searchType(int type){
        if(this.type==type){
            messageInfoList.clear();
        }else{
            this.type=type;
            mOffset=1;
        }

        if(type== DATE_MSG){
            // TODO: 2018/5/15  跳轉到聊天界面
//            messageInfoList.addAll(mMdb.queryTypeMessage(user,FROM_TEXT_MSG,TO_TEXT_MSG,mOffset));
        }else if(type==IMAGE_MSG){
            messageInfoList.addAll(mMdb.queryTypeMessage(user,FROM_IMG_MSG,TO_IMG_MSG,mOffset));
        }else if(type==VIDEO_MSG){
            messageInfoList.addAll(mMdb.queryTypeMessage(user,FROM_VIDEO_MSG,TO_VIDEO_MSG,mOffset));
        }else if(type==TRADE_MSG){
            messageInfoList.addAll(mMdb.queryTypeMessage(user,FROM_TRANSFER_MSG,TO_TRANSFER_MSG,FROM_RED_MSG,TO_RED_MSG,mOffset));
        }else if(type==TEXT_MSG){
            messageInfoList.addAll(mMdb.queryTextMessage(user,etSearch.getText().toString(),mOffset));
        }
        recordAdapter.setType(type);
        recordAdapter.notifyDataSetChanged();
    }

    private void changeLayoutVisible(boolean isShow){
        if(isShow){
            refreshLayout.setVisibility(View.GONE);
            recyclerSelect.setVisibility(View.VISIBLE);
            mOffset=1;
            messageInfoList.clear();
            recordAdapter.notifyDataSetChanged();
        }else {
            refreshLayout.setVisibility(View.VISIBLE);
            recyclerSelect.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemClick(View view, int postion) {
        changeLayoutVisible(false);
        int msg=selectList.get(postion);
        String msgcontent = "";
        if(msg== DATE_MSG){
            msgcontent=getString(R.string.date);
        }else if(msg==IMAGE_MSG){
            msgcontent=getString(R.string.image);
        }else if(msg==VIDEO_MSG){
            msgcontent=getString(R.string.video);
        }else if(msg==TRADE_MSG){
            msgcontent=getString(R.string.trade);
        }
        etSearch.setText(msgcontent);
        searchType(msg);
    }
}
