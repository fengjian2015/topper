package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.CollectPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.CollectInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.adapter.CollectRVAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_TEXT_MSG;

/**
 * Created by GA on 2018/7/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CollectActivity extends BaseActivity {

    List<CollectInfo.DataBean> mDataList = new ArrayList<>();
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_add)
    TextView mTvAdd;
    @Bind(R.id.tv_edit)
    TextView mTvEdit;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;
    @Bind(R.id.tv_cancel)
    TextView mTvCancel;
    @Bind(R.id.rl_edit)
    RelativeLayout mRlEdit;
    private CollectRVAdapter mCollectRVAdapter;
    private CollectPresenter mCollectPresenter;
    private ItemTouchHelper mItemTouchHelper;
    private List<CollectInfo.DataBean> mDataList2 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mCollectPresenter = new CollectPresenter(this);
        initRecyclerView();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.add_collect))) {
            initData();
        }
    }

    private void initRecyclerView() {
        mCollectRVAdapter = new CollectRVAdapter(this, mDataList, mCollectPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCollectRVAdapter);
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mDataList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mDataList, i, i - 1);
                    }
                }
                mCollectRVAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
            }

        });
        mCollectRVAdapter.setOnItemLongClickListener(new CollectRVAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = mRecyclerView.getChildAdapterPosition(view);
                if (!isEdit) {
                    Intent intent = new Intent(CollectActivity.this, SelectConversationActivity.class);
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setMessage(mDataList.get(position).getUrl());
                    intent.putExtra("type", 2);
                    intent.putExtra("msgType", TO_TEXT_MSG);
                    intent.putExtra("messageInfo", messageInfo);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void initData() {
        mCollectPresenter.getCollectList(new CollectPresenter.CallBack() {
            @Override
            public void send(List<CollectInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(CollectActivity.this)) {
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.GONE);
                        mDataList.clear();
                        mDataList.addAll(data);
                        mCollectRVAdapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void error() {
                mRecyclerView.setVisibility(View.GONE);
                mLlNoData.setVisibility(View.GONE);
                mLlError.setVisibility(View.VISIBLE);
            }

            @Override
            public void finishRefresh() {
            }
        });
    }

    boolean isEdit = false;

    @OnClick({R.id.bark, R.id.tv_add, R.id.tv_edit, R.id.tv_cancel, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_cancel:
                mTvEdit.setVisibility(View.VISIBLE);
                mRlEdit.setVisibility(View.GONE);
                mItemTouchHelper.attachToRecyclerView(null);
                ToastShow.showToast2(CollectActivity.this, getString(R.string.long_click_share));
                mDataList.clear();
                mDataList.addAll(mDataList2);
                mCollectRVAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_confirm:
                saveSequence();
                break;
            case R.id.tv_edit:
                mDataList2.clear();
                mDataList2.addAll(mDataList);
                mTvEdit.setVisibility(View.GONE);
                mRlEdit.setVisibility(View.VISIBLE);
                mItemTouchHelper.attachToRecyclerView(mRecyclerView);
                ToastShow.showToast2(CollectActivity.this, getString(R.string.long_click_haul));
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, AddCollectActivity.class));
                break;
        }
    }

    private void saveSequence() {
        String sequenceStr = "";
        for (int i = 0; i < mDataList.size(); i++) {
            if (i == 0) {
                sequenceStr += mDataList.get(i).getId();
            } else {
                sequenceStr += "," + mDataList.get(i).getId();
            }
        }
        UtilTool.Log("順序", sequenceStr);
        mCollectPresenter.saveSequence(sequenceStr, new CollectPresenter.CallBack2() {
            @Override
            public void send() {
                mRlEdit.setVisibility(View.GONE);
                mTvEdit.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }
}
