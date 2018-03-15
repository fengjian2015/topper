package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.adapter.AddFriendAdapter;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.impl.JidCreate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/25.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddFriendActivity extends BaseActivity {

    List<String> mRowList = new ArrayList<>();
    @Bind(R.id.imageview)
    ImageView mImageview;
    @Bind(R.id.tv_cancel)
    TextView mTvCancel;
    @Bind(R.id.tv_search)
    TextView mTvSearch;
    @Bind(R.id.et_search)
    EditText mEtSearch;
    @Bind(R.id.clear_away)
    TextView mClearAway;
    @Bind(R.id.ll_hot)
    LinearLayout mLlHot;
    @Bind(R.id.listView)
    ListView mListView;
    private AddFriendAdapter mAddFriendAdapter;
    private LoadingProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        listenerEditText();
        initListView();
    }

    private void initListView() {
        mAddFriendAdapter = new AddFriendAdapter(this, mRowList);
        mListView.setAdapter(mAddFriendAdapter);
    }

    //监听输入框
    private void listenerEditText() {

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = mEtSearch.getText().toString();
                if (!content.isEmpty()) {
                    mTvCancel.setVisibility(View.GONE);
                    mTvSearch.setVisibility(View.VISIBLE);
                } else {
                    mTvCancel.setVisibility(View.VISIBLE);
                    mTvSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @OnClick({R.id.tv_cancel, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_search:
                search();
                break;
        }
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("搜索中...");
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideDialog();
            mAddFriendAdapter.notifyDataSetChanged();
        }
    };

    private void search() {
        try {
            String text = mEtSearch.getText().toString();
            UserSearchManager search = new UserSearchManager(XmppConnection.getInstance().getConnection());
            Form searchForm = search.getSearchForm(JidCreate.domainBareFrom("search.xmpp.bclould.com"));
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", text.trim());
            ReportedData data = search.getSearchResults(answerForm, JidCreate.domainBareFrom("search.xmpp.bclould.com"));
            List<ReportedData.Row> rowList = data.getRows();
            if (rowList != null) {
                mLlHot.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mRowList.removeAll(mRowList);
                for (int i = 0; i < rowList.size(); i++) {
                    mRowList.add(rowList.get(i).getValues("Username").get(0));
                }

            }
        } catch (Exception e) {
            hideDialog();
            UtilTool.Log("fsdafa", e.getMessage());
        }
    }
}
