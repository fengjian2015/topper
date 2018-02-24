package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.history.DBManager;
import com.dashiji.biyun.model.UserInfo;
import com.dashiji.biyun.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/27.
 */

public class SearchActivity extends AppCompatActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.searchView)
    SearchView mSearchView;
    @Bind(R.id.listView)
    ListView mListView;
    List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initListView();

    }

    private void initListView() {
        DBManager dbManager = new DBManager(this);
        List<UserInfo> userInfos = dbManager.queryAllUser();
        for (UserInfo info : userInfos){
            String user = info.getUser();
            String name = user.substring(0, user.indexOf("@"));
            mList.add(name);
        }
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                Intent intent = new Intent(SearchActivity.this, ConversationActivity.class);
                intent.putExtra("name", textView.getText());
                intent.putExtra("user", textView.getText() + "@" + Constants.DOMAINNAME);
                startActivity(intent);
            }
        });
        mListView.setTextFilterEnabled(true);
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    mListView.setFilterText(newText);
                } else {
                    mListView.clearTextFilter();
                }
                return false;
            }
        });

    }
}
