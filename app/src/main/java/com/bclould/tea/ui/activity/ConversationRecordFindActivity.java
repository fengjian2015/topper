package com.bclould.tea.ui.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.adapter.MessageRecordAdapter;
import com.bclould.tea.ui.adapter.MessageRecordSelectAdapter;
import com.bclould.tea.ui.adapter.MessageRecordViedoAdapter;
import com.bclould.tea.utils.FullyGridLayoutManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_RED_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_TRANSFER_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_RED_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_TRANSFER_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_VIDEO_MSG;

public class ConversationRecordFindActivity extends BaseActivity implements MessageRecordSelectAdapter.OnItemClickListener {

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
    @Bind(R.id.recycler_view_video)
    RecyclerView recyclerViewVideo;
    @Bind(R.id.refresh_layout_video)
    SmartRefreshLayout refreshLayoutVideo;

    private MessageRecordSelectAdapter selectAdapter;
    private List<Integer> selectList = new ArrayList<>();//以後可以通過後台獲取數據

    private List<MessageInfo> messageInfoList = new ArrayList<>();//消息集合
    private MessageRecordAdapter recordAdapter;

    private MessageRecordViedoAdapter recordViedoAdapter;

    public static final int DATE_MSG = 1;//時間賽選
    public static final int IMAGE_MSG = 2;//圖片賽選
    public static final int VIDEO_MSG = 3;//視頻賽選
    public static final int TRADE_MSG = 4;//交易賽選
    public static final int TEXT_MSG = 5;//文本賽選
    public static final int TEXT_SELECT = 6;//文本輸入

    private DBManager mMdb;
    private DBRoomMember mDBRoomMember;
    private String user;
    private int mOffset = 1;
    private int type=TEXT_SELECT;
    private boolean changeIntupType = false;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_record_find);
        ButterKnife.bind(this);
        setTitle(getString(R.string.chat_content));
        init();
        pullToRefresh();
    }

    private void init() {
        roomId=getIntent().getStringExtra("roomId");
        user = getIntent().getStringExtra("user");
        mMdb = new DBManager(this);
        mDBRoomMember=new DBRoomMember(this);
//        selectList.add(DATE_MSG);
        selectList.add(IMAGE_MSG);
        selectList.add(VIDEO_MSG);
        selectList.add(TRADE_MSG);
        //初始化選擇項
        selectAdapter = new MessageRecordSelectAdapter(this, selectList);
        recyclerSelect.setAdapter(selectAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerSelect.setLayoutManager(gridLayoutManager);
        selectAdapter.addOnItemClickListener(this);

        //初始化結果適配器
        recordAdapter = new MessageRecordAdapter(this, messageInfoList, mMdb,mDBRoomMember,roomId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recordAdapter);

        //初始化圖片和視頻適配器
        recordViedoAdapter=new MessageRecordViedoAdapter(this,messageInfoList,mMdb);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        recyclerViewVideo.setLayoutManager(manager);
        recyclerViewVideo.addItemDecoration(new SpaceItemDecoration(20));
        recyclerViewVideo.setAdapter(recordViedoAdapter);

        listenerEditText();
    }

    //动态设置条目间隔
    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
                outRect.top = mSpace;
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
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
        refreshLayoutVideo.setOnLoadMoreListener(new OnLoadMoreListener() {
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
                if (changeIntupType) {
                    changeIntupType = false;
                } else {
                    searchType(TEXT_SELECT);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @OnClick({R.id.bark, R.id.iv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_delete:
                etSearch.setText("");
                changeLayoutVisible(true);
                break;
        }
    }

    public void searchType(int type) {
        if(this.type!=type){
            this.type = type;
            mOffset = 1;
        }
        if (type != TEXT_SELECT) {
            changeIntupType = true;
        }else{
            mOffset = 1;
        }
        messageInfoList.clear();
        recyclerView.setBackgroundResource(R.drawable.bg_pay_record_shape);
        if (type == DATE_MSG) {
            // TODO: 2018/5/15  跳轉到聊天界面
        } else if (type == IMAGE_MSG) {
            messageInfoList.addAll(mMdb.queryTypeMessage(user, FROM_IMG_MSG, TO_IMG_MSG, mOffset));
            notifyDataRecordVideo();
        } else if (type == VIDEO_MSG) {
            messageInfoList.addAll(mMdb.queryTypeMessage(user, FROM_VIDEO_MSG, TO_VIDEO_MSG, mOffset));
            notifyDataRecordVideo();
        } else if (type == TRADE_MSG) {
            recyclerView.setBackground(null);
            messageInfoList.addAll(mMdb.queryTypeMessage(user, FROM_TRANSFER_MSG, TO_TRANSFER_MSG, FROM_RED_MSG, TO_RED_MSG, mOffset));
            notifyDataRecord();
        } else if (type == TEXT_MSG) {
            messageInfoList.addAll(mMdb.queryTextMessage(user, etSearch.getText().toString(), mOffset));
            notifyDataRecord();
        } else if (type == TEXT_SELECT) {
            inputText();
            notifyDataRecord();
        }

    }
    private void notifyDataRecordVideo(){
        recordViedoAdapter.setType(type);
        recordViedoAdapter.notifyDataSetChanged();
    }

    private void notifyDataRecord(){
        recordAdapter.setType(type);
        recordAdapter.notifyDataSetChanged();
    }

    private void inputText() {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessage(getString(R.string.click_the_search) + etSearch.getText().toString());
        messageInfoList.add(messageInfo);
    }

    private void changeLayoutVisible(boolean isShow) {
        if (isShow) {
            refreshLayout.setVisibility(View.GONE);
            refreshLayoutVideo.setVisibility(View.GONE);
            recyclerSelect.setVisibility(View.VISIBLE);
            mOffset = 1;
            messageInfoList.clear();
            recordAdapter.notifyDataSetChanged();
            recordViedoAdapter.notifyDataSetChanged();
        } else {
            if (type == VIDEO_MSG||type==IMAGE_MSG) {
                refreshLayout.setVisibility(View.GONE);
                refreshLayoutVideo.setVisibility(View.VISIBLE);
            } else if (type == TRADE_MSG||type == TEXT_SELECT||type == TEXT_MSG) {
                refreshLayout.setVisibility(View.VISIBLE);
                refreshLayoutVideo.setVisibility(View.GONE);
            }
            recyclerSelect.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemClick(View view, int postion) {
        changeLayoutVisible(false);
        int msg = selectList.get(postion);
        String msgcontent = "";
        if (msg == DATE_MSG) {
            msgcontent = getString(R.string.date);
        } else if (msg == IMAGE_MSG) {
            msgcontent = getString(R.string.image);
        } else if (msg == VIDEO_MSG) {
            msgcontent = getString(R.string.video);
        } else if (msg == TRADE_MSG) {
            msgcontent = getString(R.string.trade);
        }
        searchType(msg);
        etSearch.setText(msgcontent);

    }
}
