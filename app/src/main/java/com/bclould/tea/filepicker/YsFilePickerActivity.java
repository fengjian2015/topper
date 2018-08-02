package com.bclould.tea.filepicker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.widget.PoppyViewHelper;
import com.bclould.tea.ui.widget.TextViewDoubleClickable;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.bclould.tea.filepicker.YsFilePicker.MAX_UPLOAD_SIZE;

@RequiresApi(api = Build.VERSION_CODES.N)
public class YsFilePickerActivity extends BaseActivity implements OnLongClickListener {

    final private String[] mVideoExtensions = {"avi", "mp4", "3gp", "mov"};
    final private String[] mImagesExtensions = {"jpeg", "jpg", "png", "gif", "bmp", "wbmp"};
    private int MaxNumber=10;
    //可能要做多选
//    final private List<CheckBox> checkBoxList = new ArrayList<>();

    final private boolean DEBUG = true;
    final private String TAG = "YsFilePicker";

    private boolean mOptOnlyOneItem = false;
    private List<String> mOptFilterExclude;
    private List<String> mOptFilterListed;
    private int mOptChoiceType;
    private int mOptSortType;

    private LruCache<String, Bitmap> mBitmapsCache;

    private AbsListView mAbsListView;
    private View mEmptyView;
    private ArrayList<File> mFilesList = new ArrayList<File>();
    private ArrayList<String> mSelected = new ArrayList<String>();
    private long currentFileSizeSum = 0;
    private HashMap<String, Integer> mListPositioins = new HashMap<String, Integer>();
    private File mCurrentDirectory;
    private boolean mIsMultiChoice = false;
    private TextView mHeaderTitle;
    private PoppyViewHelper mPoppyViewHelper;
    private TextView poppyView;
    private TextView mHeaderTitle2;
    private Button btnSend;
    private TextView tvSizeSum;
    private View flFooterBar;


    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.toco_filepicker_main_activity);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mBitmapsCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return getBitmapSize(bitmap) / 1024;
            }
        };

        Intent intent = getIntent();
        mOptOnlyOneItem = intent.getBooleanExtra(YsFilePicker.SET_ONLY_ONE_ITEM, false);
        if (intent.hasExtra(YsFilePicker.SET_FILTER_EXCLUDE)) {
            mOptFilterExclude = Arrays.asList(intent.getStringArrayExtra(YsFilePicker.SET_FILTER_EXCLUDE));
        }
        if (intent.hasExtra(YsFilePicker.SET_FILTER_LISTED)) {
            mOptFilterListed = Arrays.asList(intent.getStringArrayExtra(YsFilePicker.SET_FILTER_LISTED));
        }
        mOptChoiceType = intent.getIntExtra(YsFilePicker.SET_CHOICE_TYPE, YsFilePicker.CHOICE_TYPE_ALL);

        mOptSortType = intent.getIntExtra(YsFilePicker.SET_SORT_TYPE, YsFilePicker.SORT_NAME_ASC);

        mEmptyView = getLayoutInflater().inflate(R.layout.ys_filepicker_empty, null);
        addContentView(mEmptyView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        setAbsListView();
        showSecondHeader(false);

        File path = null;
        if (intent.hasExtra(YsFilePicker.SET_START_DIRECTORY)) {
            String startPath = intent.getStringExtra(YsFilePicker.SET_START_DIRECTORY);
            if (startPath != null && startPath.length() > 0) {
                File tmp = new File(startPath);
                if (tmp.exists() && tmp.isDirectory()) path = tmp;
            }
        }
        if (path == null) {
            path = new File("/");
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                path = Environment.getExternalStorageDirectory();
        }
        readDirectory(path);

        mHeaderTitle = (TextView) findViewById(R.id.title);
        mHeaderTitle2 = (TextView) findViewById(R.id.title2);
        updateTitle();

        ImageButton sort2 = (ImageButton) findViewById(R.id.menu_sort2);
        if (!intent.getBooleanExtra(YsFilePicker.DISABLE_SORT_BUTTON, false)) {
            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(YsFilePickerActivity.this);
                    alert.show();
                    alert.setTitle(R.string.date);
                    alert.setItems(R.array.ys__sorting_types, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    mOptSortType = YsFilePicker.SORT_NAME_ASC;
                                    break;
                                case 1:
                                    mOptSortType = YsFilePicker.SORT_NAME_DESC;
                                    break;
                                case 2:
                                    mOptSortType = YsFilePicker.SORT_SIZE_ASC;
                                    break;
                                case 3:
                                    mOptSortType = YsFilePicker.SORT_SIZE_DESC;
                                    break;
                                case 4:
                                    mOptSortType = YsFilePicker.SORT_DATE_ASC;
                                    break;
                                case 5:
                                    mOptSortType = YsFilePicker.SORT_DATE_DESC;
                                    break;

                            }
                            sort();
                        }
                    });

                }
            };
            sort2.setOnClickListener(listener);
            sort2.setOnLongClickListener(this);
        } else {
            sort2.setVisibility(ImageButton.GONE);
        }


//        ImageButton cancel1 = (ImageButton) findViewById(R.id.menu_cancel1);
        View cancel1 = findViewById(R.id.menu_cancel1);
        if (intent.getBooleanExtra(YsFilePicker.ENABLE_QUIT_BUTTON, false)) {
            cancel1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    complete(null);
                }
            });
//            cancel1.setOnLongClickListener(this);
        } else cancel1.setVisibility(ImageButton.GONE);

        View cancel2 = findViewById(R.id.menu_cancel2);
        cancel2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //原来取消回到非多选界面
//                disableMultiChoice();
//                showSecondHeader(false);
                //现在直接关闭不发送
                mSelected.clear();
                tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                        getString(R.string.im_file_select_size_sum, getHumanFileSize(0)));
                complete(null);
            }
        });
//        cancel2.setOnLongClickListener(this);

//        if (mOptOnlyOneItem && mOptChoiceType == YsFilePicker.CHOICE_TYPE_DIRECTORIES) {
//            ok1.setVisibility(ImageButton.VISIBLE);
//            ok1_delimiter.setVisibility(ImageButton.VISIBLE);
//            ok1.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ArrayList<String> list = new ArrayList<String>();
//                    String parent;
//                    File parentFile = mCurrentDirectory.getParentFile();
//                    if (parentFile == null) {
//                        parent = "";
//                        list.add("/");
//                    } else {
//                        parent = parentFile.getAbsolutePath();
//                        if (!parent.endsWith("/")) parent += "/";
//                        list.add(mCurrentDirectory.getName());
//                    }
//                    YsFilePickerParcelObject object = new YsFilePickerParcelObject(parent, list, 1);
//                    complete(object);
//                }
//            });
//            ok1.setOnLongClickListener(this);
//        } else {
//            ok1.setVisibility(ImageButton.GONE);
//            ok1_delimiter.setVisibility(ImageButton.GONE);
//        }

        ImageButton select_all = (ImageButton) findViewById(R.id.menu_select_all);
        ImageButton deselect = (ImageButton) findViewById(R.id.menu_deselect);
        ImageButton invert = (ImageButton) findViewById(R.id.menu_invert);
        //20160518 不需要全选 反选 全不选
        select_all.setVisibility(View.GONE);
        deselect.setVisibility(View.GONE);
        invert.setVisibility(View.GONE);
        if (mOptOnlyOneItem) {
            select_all.setVisibility(ImageButton.GONE);
            deselect.setVisibility(ImageButton.GONE);
            invert.setVisibility(ImageButton.GONE);
        } else {
            select_all.setOnClickListener(new OnClickListener() {//此功能根据UI图没给出,已屏蔽掉了
                @Override
                public void onClick(View v) {
                    selectMax();
                }
            });
            select_all.setOnLongClickListener(this);

            deselect.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelected.clear();
                    ((BaseAdapter) mAbsListView.getAdapter()).notifyDataSetChanged();
                }
            });
            deselect.setOnLongClickListener(this);

            invert.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> tmp = new ArrayList<String>();
                    for (int i = 0; i < mFilesList.size(); i++) {
                        String filename = mFilesList.get(i).getName();
                        if (!mSelected.contains(filename)) tmp.add(filename);
                    }
                    mSelected = tmp;
                    ((BaseAdapter) mAbsListView.getAdapter()).notifyDataSetChanged();
                }
            });
            invert.setOnLongClickListener(this);
        }
        View toPre1 = findViewById(R.id.menu_to_pre1);
        toPre1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toPreDir();
            }
        });
        toPre1.setOnLongClickListener(this);

//        ImageButton ok2 = (ImageButton) findViewById(R.id.menu_ok2);
        View toPre = findViewById(R.id.menu_to_pre2);
        toPre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mSelected.size() > 0) {
//                    complete(null);
//                } else {
//                    disableMultiChoice();
//                }
                if (mIsMultiChoice) {
                    disableMultiChoice();
                } else {
                    toPreDir();
                }
            }
        });
        toPre.setOnLongClickListener(this);
        //底部bar绑定事件
        flFooterBar = findViewById(R.id.fl_footer_bar);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelected.size() > 0) {
                    complete(null);
                } else {
//                    disableMultiChoice();
                    ToastShow.showToast2(YsFilePickerActivity.this, "请选择文件");
                }
            }
        });
        btnSend.setText("发送");
        tvSizeSum = (TextView) findViewById(R.id.tv_size_sum);
        addToTvSizeSumText(new File(""));
        tvSizeSum.setText(YsFilePickerActivity.this.getResources().getString(R.string.im_file_select_size_sum, mSelected.size()));

        //双击的时候全选MaxNumber个
        TextViewDoubleClickable textViewDoubleClickable
                = (TextViewDoubleClickable) findViewById(R.id.tv_choosemode);
        textViewDoubleClickable.setOnDoubleClickListener(new TextViewDoubleClickable.DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
//                if ("00".equalsIgnoreCase(AbstractHttp.kMode)) {//生产环境
                    selectMax();
//                } else {//测试环境
//                    selectAll();
//                }
            }
        });
        //反选
//        textViewDoubleClickable.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                return false;
//            }
//        });
    }

    private void selectAll() {
        mSelected.clear();
        tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                getString(R.string.im_file_select_size_sum, getHumanFileSize(0)));
        for (int i = 0; i < mFilesList.size(); i++) {
            File file = mFilesList.get(i);
            mSelected.add(file.getName());
            addToTvSizeSumText(file);
        }
        ToastShow.showToast2(YsFilePickerActivity.this, "已为您选择前" + mFilesList.size() + "个文件");
        ((BaseAdapter) mAbsListView.getAdapter()).notifyDataSetChanged();
    }

    private void selectMax() {
        mSelected.clear();
        tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                getString(R.string.im_file_select_size_sum, getHumanFileSize(0)));
        if (mFilesList.size() >= MaxNumber) {
            int count = 0;
            for (int i = 0; i < MaxNumber; i++) {
                File file = mFilesList.get(i);
                if (file.length() <= MAX_UPLOAD_SIZE) {
                    if (mSelected.add(file.getName())) {
                        count++;
                    }
                }
                addToTvSizeSumText(file);
            }
            ToastShow.showToast2(YsFilePickerActivity.this, "已为您选择前" + count + "个文件");
        } else {
            for (int i = 0; i < mFilesList.size(); i++) {
                File file = mFilesList.get(i);
                mSelected.add(file.getName());
                addToTvSizeSumText(file);
            }
            ToastShow.showToast2(YsFilePickerActivity.this, "已为您选择前" + mFilesList.size() + "个文件");
        }

        ((BaseAdapter) mAbsListView.getAdapter()).notifyDataSetChanged();
    }

    @SuppressLint("StringFormatMatches")
    private void addToTvSizeSumText(File file) {
        if (file.exists()) {
            currentFileSizeSum += file.length();
            tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                    getString(R.string.im_file_select_size_sum, getHumanFileSize(currentFileSizeSum)));
        } else {
            //文件不存在
            if (mSelected.size() == 0) {
                btnSend.setText("发送");
                btnSend.setEnabled(false);
            }
        }
        if (mSelected.size() <= 0) {
            btnSend.setText("发送");
            btnSend.setEnabled(false);
        } else {
            btnSend.setEnabled(true);
            btnSend.setText(YsFilePickerActivity.this.getResources().
                    getString(R.string.send_complete, mSelected.size()));
        }
    }

    @SuppressLint("StringFormatMatches")
    private void removeFromTvSizeSumText(File file) {
        if (file.exists()) {
            currentFileSizeSum -= file.length();
            tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                    getString(R.string.im_file_select_size_sum, getHumanFileSize(currentFileSizeSum)));
        } else {
            //文件不存在
        }
        if (mSelected.size() == 0) {
            btnSend.setText("发送");
            btnSend.setEnabled(false);
        } else {
            btnSend.setEnabled(true);
            btnSend.setText(YsFilePickerActivity.this.getResources().
                    getString(R.string.send_complete, mSelected.size()));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Toast toast = Toast.makeText(YsFilePickerActivity.this, v.getContentDescription(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.LEFT | Gravity.TOP, v.getLeft(), v.getBottom() + v.getBottom() / 2);
        toast.show();
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (mIsMultiChoice) {
                    disableMultiChoice();
                } else {
                    toPreDir();
                }
            } else if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getFlags() & KeyEvent.FLAG_LONG_PRESS) == KeyEvent.FLAG_LONG_PRESS) {
                mSelected.clear();
                tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                        getString(R.string.im_file_select_size_sum, getHumanFileSize(0)));
                complete(null);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void toPreDir() {
        File parentFile = mCurrentDirectory.getParentFile();
        if (parentFile == null) {
            complete(null);
        } else {
            //如果父目录是linux根目录，直接退出
            if ("/".equals(parentFile.getAbsolutePath())) {
                complete(null);
                return;
            }
            readDirectory(parentFile);

            String path = mCurrentDirectory.getAbsolutePath();
            if (mListPositioins.containsKey(path)) {
                mAbsListView.setSelection(mListPositioins.get(path));
                mListPositioins.remove(path);
            }

            updateTitle();
        }
    }

    private void disableMultiChoice() {
        showSecondHeader(false);
        mIsMultiChoice = false;
        hideOrShowPopNoticeByColorChange(true);
        //隐藏底部bar
        flFooterBar.setVisibility(View.GONE);
        if (poppyView != null) poppyView.setVisibility(View.VISIBLE);
        mSelected.clear();
        tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                getString(R.string.im_file_select_size_sum, getHumanFileSize(0)));
        mHeaderTitle2.setText(YsFilePickerActivity.this.getResources().
                getString(R.string.im_file_select_count, mSelected.size()));
        if (mOptChoiceType == YsFilePicker.CHOICE_TYPE_FILES && !mOptOnlyOneItem) {
            readDirectory(mCurrentDirectory);
        }
        ((BaseAdapter) mAbsListView.getAdapter()).notifyDataSetChanged();
    }

    private void showSecondHeader(boolean show) {
        if (show) {
            findViewById(R.id.header1).setVisibility(View.GONE);
            findViewById(R.id.header2).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.header1).setVisibility(View.VISIBLE);
            findViewById(R.id.header2).setVisibility(View.GONE);
        }
    }

    private void updateTitle() {
//        String name = mCurrentDirectory.getName();
        String absolutePath = mCurrentDirectory.getAbsolutePath();
        String finalPath = absolutePath;
        if (absolutePath.length() > 30) {
            finalPath = absolutePath.substring(0, 30) + "...";
        }
        mHeaderTitle.setText("当前所在位置:\n" + finalPath);
    }

    private void complete(YsFilePickerParcelObject object) {
        if (object == null) {
            String path = mCurrentDirectory.getAbsolutePath();
            if (!path.endsWith("/")) path += "/";
            object = new YsFilePickerParcelObject(path, mSelected, mSelected.size());
        }
        Intent intent = new Intent();
        intent.putExtra(YsFilePickerParcelObject.class.getCanonicalName(), object);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void readDirectory(File path) {
        mCurrentDirectory = path;
        mFilesList.clear();
        File[] files = path.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (mOptChoiceType == YsFilePicker.CHOICE_TYPE_DIRECTORIES && !files[i].isDirectory())
                    continue;
                if (files[i].isFile()) {
                    String extension = getFileExtension(files[i]);
                    if (TextUtils.isEmpty(extension.trim())) continue;//如果是那种没有后缀名字的文件
                    //if (mOptFilterListed != null && !mOptFilterListed.contains(extension)) continue;
                    if (mOptFilterExclude != null && mOptFilterExclude.contains(extension))
                        continue;
                }else{
                    String fileName=files[i].getName();
                    int index = fileName.lastIndexOf(".");
                    if (index == 0) continue;
                    if(fileName.startsWith(".")){
                        continue;
                    }
                }
                mFilesList.add(files[i]);
            }
        }

        sort();
    }

    private void sort() {
        Collections.sort(mFilesList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                boolean isDirectory1 = file1.isDirectory();
                boolean isDirectory2 = file2.isDirectory();
                if (isDirectory1 && !isDirectory2) return -1;
                if (!isDirectory1 && isDirectory2) return 1;
                switch (mOptSortType) {
                    case YsFilePicker.SORT_NAME_DESC:
                        return file2.getName().toLowerCase(Locale.getDefault()).compareTo(file1.getName().toLowerCase(Locale.getDefault()));
                    case YsFilePicker.SORT_SIZE_ASC:
                        return Long.valueOf(file1.length()).compareTo(Long.valueOf(file2.length()));
                    case YsFilePicker.SORT_SIZE_DESC:
                        return Long.valueOf(file2.length()).compareTo(Long.valueOf(file1.length()));
                    case YsFilePicker.SORT_DATE_ASC:
                        return Long.valueOf(file1.lastModified()).compareTo(Long.valueOf(file2.lastModified()));
                    case YsFilePicker.SORT_DATE_DESC:
                        return Long.valueOf(file2.lastModified()).compareTo(Long.valueOf(file1.lastModified()));
                }
                // Default, YsFilePicker.SORT_NAME_ASC
                return file1.getName().toLowerCase(Locale.getDefault()).compareTo(file2.getName().toLowerCase(Locale.getDefault()));
            }
        });
        ((BaseAdapter) mAbsListView.getAdapter()).notifyDataSetChanged();
    }

    private final String mPopNoticeWords = "长按其中一项可以多选文件哦";
    private final int mPopNoticeTvBgColor = R.color.bright_foreground_disabled_material_dark;

    private void setAbsListView() {
        int curView, nextView;//没有出UI的时候做的两种选择文件布局,根据UI图只使用了listview
        if (mAbsListView == null || mAbsListView.getId() == R.id.gridview) {
            curView = R.id.gridview;
            nextView = R.id.listview;
        } else {
            curView = R.id.listview;
            nextView = R.id.gridview;
        }
        //20160518加入长按多选文件提示↓
        mPoppyViewHelper = new PoppyViewHelper(this);
        poppyView = (TextView) mPoppyViewHelper.createPoppyViewOnListView(nextView, R.layout.poppyview, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

        });
        poppyView.setFocusable(false);
        poppyView.setClickable(false);
        poppyView.setFocusableInTouchMode(false);

//        poppyView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(YsFilePickerActivity.this, "Click me!", Toast.LENGTH_SHORT).show();
//            }
//        });
        //20160518加入长按多选文件提示↑
        mAbsListView = (AbsListView) findViewById(nextView);
        mAbsListView.setEmptyView(mEmptyView);
        FilesListAdapter adapter = new FilesListAdapter(this, (nextView == R.id.listview) ? R.layout.ys_filepicker_list_item : R.layout.ys_filepicker_grid_item);
        if (nextView == R.id.listview) ((ListView) mAbsListView).setAdapter(adapter);
        else ((GridView) mAbsListView).setAdapter(adapter);
        mAbsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < mFilesList.size()) {
                    File file = mFilesList.get(position);
                    if (mIsMultiChoice) {
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                            setItemBackground(view, false);
                            mSelected.remove(file.getName());
                            removeFromTvSizeSumText(file);
                            mHeaderTitle2.setText(YsFilePickerActivity.this.getResources().
                                    getString(R.string.im_file_select_count, mSelected.size()));
                        } else {
                            if (mOptOnlyOneItem) {
                                mSelected.clear();
                                tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                                        getString(R.string.im_file_select_size_sum, getHumanFileSize(0)));
                                mHeaderTitle2.setText(YsFilePickerActivity.this.getResources().
                                        getString(R.string.im_file_select_count, mSelected.size()));
                                ((BaseAdapter) mAbsListView.getAdapter()).notifyDataSetChanged();
                            }
                            if (allowSelected(file)) {
                                setItemBackground(view, true);
                                mSelected.add(file.getName());
                                addToTvSizeSumText(file);
                                mHeaderTitle2.setText(YsFilePickerActivity.this.getResources().
                                        getString(R.string.im_file_select_count, mSelected.size()));
                                checkBox.setChecked(true);
                            } else {
                                checkBox.setChecked(false);
                            }
                        }
                    } else {
                        if (file.isDirectory()) {
                            int currentPosition = mAbsListView.getFirstVisiblePosition();
                            mListPositioins.put(mCurrentDirectory.getAbsolutePath(), currentPosition);
                            readDirectory(file);
                            updateTitle();
                            mAbsListView.setSelection(0);
                        } else {
                            if (allowSelected(file)) {
                                mSelected.add(file.getName());
                                addToTvSizeSumText(file);
                                mHeaderTitle2.setText(YsFilePickerActivity.this.getResources().
                                        getString(R.string.im_file_select_count, mSelected.size()));
                                complete(null);
                            }
                        }
                    }
                }
            }
        });

        if (mOptChoiceType != YsFilePicker.CHOICE_TYPE_FILES || !mOptOnlyOneItem) {
            mAbsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                                                        @Override
                                                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                            if (!mIsMultiChoice) {
                                                                //-----------初始化--------------------
                                                                //清空
                                                                currentFileSizeSum = 0;
                                                                mSelected.clear();
                                                                //左下角文件选择置0 kb
                                                                tvSizeSum.setText(YsFilePickerActivity.this.getResources().
                                                                        getString(R.string.im_file_select_size_sum, getHumanFileSize(currentFileSizeSum)));
                                                                //右下角文件发送文件置为"发送"
                                                                btnSend.setText("发送");
                                                                btnSend.setEnabled(false);
                                                                //-----------初始化--------------------
                                                                flFooterBar.setVisibility(View.VISIBLE);
                                                                mIsMultiChoice = true;
                                                                File file1 = mFilesList.get(position);
                                                                if (file1.isFile()) {
                                                                    if (position < mFilesList.size()) {
                                                                        if (mOptChoiceType != YsFilePicker.CHOICE_TYPE_FILES || file1.isFile()) {
                                                                            if (allowSelected(file1)) {
                                                                                mSelected.add(file1.getName());
                                                                                addToTvSizeSumText(file1);
                                                                            }
                                                                        }
                                                                    }
                                                                    if (mOptChoiceType == YsFilePicker.CHOICE_TYPE_FILES && !mOptOnlyOneItem) {
                                                                        ArrayList<File> tmpList = new ArrayList<File>();
                                                                        for (int i = 0; i < mFilesList.size(); i++) {
                                                                            File file = mFilesList.get(i);
                                                                            if (file.isFile()) tmpList.add(file);
                                                                        }
                                                                        mFilesList = tmpList;
                                                                    }
                                                                } else {//长按的是个文件夹的话
                                                                    mCurrentDirectory = file1;
                                                                    if(file1.listFiles()==null){
                                                                        ArrayList<File> tmpList = new ArrayList<File>();
                                                                        mFilesList = tmpList;
                                                                    }else if (mOptChoiceType == YsFilePicker.CHOICE_TYPE_FILES && !mOptOnlyOneItem) {
                                                                        ArrayList<File> tmpList = new ArrayList<File>();
                                                                        File[] files = file1.listFiles();
                                                                        for (int i = 0; i < files.length; i++) {
                                                                            File file = files[i];
                                                                            if (file.isFile()&&!(Arrays.asList(mImagesExtensions).contains(getFileExtension(file)))) tmpList.add(file);
                                                                        }
                                                                        mFilesList = tmpList;
                                                                    }
                                                                }
                                                                ((BaseAdapter) mAbsListView.getAdapter()).notifyDataSetChanged();
                                                                hideOrShowPopNoticeByColorChange(false);
                                                                showSecondHeader(true);
                                                                return true;
                                                            }
                                                            return false;
                                                        }
                                                    }

            );
        }
        findViewById(curView).setVisibility(View.GONE);
        mAbsListView.setVisibility(View.VISIBLE);
    }

    private void hideOrShowPopNoticeByColorChange(boolean falseForHideTrueForShow) {
        if (falseForHideTrueForShow) {//不隐藏
            if (poppyView != null) poppyView.setVisibility(View.VISIBLE);
            poppyView.setText(mPopNoticeWords);
            poppyView.setBackgroundColor(getResources().getColor(mPopNoticeTvBgColor));
        } else {//隐藏
            if (poppyView != null) poppyView.setVisibility(View.GONE);//空的时候不显示
            poppyView.setText("");
            poppyView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

    }

    /**
     * 判断文件最多数量 以及 文件 的大小
     *
     * @param file
     * @return
     */

    private boolean allowSelected(File file) {
        if (mSelected.size() >= MaxNumber) {
//            ToastShow.showToast2(YsFilePickerActivity.this, "最多只能选择MaxNumber个文件!");
            ToastShow.showToastShowCenter(YsFilePickerActivity.this, "最多只能选择"+MaxNumber+"个文件");
            return false;
        } else {
            long length = 0;
            if (file != null && file.exists()) {
                length = file.length();
                if (length > MAX_UPLOAD_SIZE) {
                    ToastShow.showToast2(YsFilePickerActivity.this,
                            "请选择" + UtilTool.FormetFileSize(MAX_UPLOAD_SIZE) + "及以下的文件");
                    return false;
                } else {
                    //允许发送
                    return true;
                }
            } else {
                //后期可以换成文件监听者来刷新界面
                ToastShow.showToast2(YsFilePickerActivity.this, "文件可能已经被删除，请重新选择文件进入界面");
                return false;
            }
        }
    }

    private void setItemBackground(View view, boolean state) {
        view.setBackgroundResource(state ? attrToResId(R.attr.ys__selected_item_background) : 0);
    }

    int attrToResId(int attr) {
        TypedArray a = getTheme().obtainStyledAttributes(new int[]{attr});
        return a.getResourceId(0, 0);
    }

    @SuppressLint("DefaultLocale")
    private String getFileExtension(File file) {
        String fileName=file.getName();
        int index = fileName.lastIndexOf(".");
        if (index == -1) return "";
        if(index==0&&!file.isDirectory()) return "";
        if(fileName.startsWith("."))return "";
        return fileName.substring(index + 1, fileName.length()).toLowerCase(Locale.getDefault());
    }

    private int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= 12) {
            return new OldApiHelper().getBtimapSize(bitmap);
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    private void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            mBitmapsCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromCache(String key) {
        return mBitmapsCache.get(key);
    }

    class FilesListAdapter extends BaseAdapter {
        private Context mContext;
        private int mResource;

        public FilesListAdapter(Context context, int resource) {
            mContext = context;
            mResource = resource;
        }

        @Override
        public int getCount() {
            return mFilesList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFilesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            File file = mFilesList.get(position);

            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);

            CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            if (mSelected.contains(file.getName())) {
                checkbox.setChecked(true);
                setItemBackground(convertView, true);
            } else {
                checkbox.setChecked(false);
                setItemBackground(convertView, false);
            }
            if (mIsMultiChoice) checkbox.setVisibility(View.VISIBLE);

            if (file.isDirectory()) {
                thumbnail.setImageResource(R.mipmap.ys__ic_folder);
            } else {
                //调用内容提供者查询图标逻辑↓
//				if (Build.VERSION.SDK_INT >= 5 && (Arrays.asList(mVideoExtensions).contains(getFileExtension(file.getName())) || Arrays.asList(mImagesExtensions).contains(getFileExtension(file.getName())))) {
//					Bitmap bitmap = getBitmapFromCache(file.getAbsolutePath());
//					if (bitmap == null) new ThumbnailLoader(thumbnail).execute(file);
//					else thumbnail.setImageBitmap(bitmap);
//				} else thumbnail.setImageResource(R.drawable.ys__ic_file);
                //调用内容提供者查询图标逻辑↑
                //由于现在只显示文档类型文件
                //所以只有几种图片不需要使用缓存
                if (file.getName().endsWith(".doc")) {
                    thumbnail.setImageResource(R.mipmap.type_doc);
                } else if (file.getName().endsWith(".docx")) {
                    thumbnail.setImageResource(R.mipmap.type_doc);
                } else if (file.getName().endsWith(".ppt")) {
                    thumbnail.setImageResource(R.mipmap.type_ppt);
                } else if (file.getName().endsWith(".pptx")) {
                    thumbnail.setImageResource(R.mipmap.type_ppt);
                } else if (file.getName().endsWith(".xls")) {
                    thumbnail.setImageResource(R.mipmap.type_xls);
                } else if (file.getName().endsWith(".xlsx")) {
                    thumbnail.setImageResource(R.mipmap.type_xls);
                } else if (file.getName().endsWith(".pdf")) {
                    thumbnail.setImageResource(R.mipmap.type_pdf);
                } else if (file.getName().endsWith(".txt")) {
                    thumbnail.setImageResource(R.mipmap.type_txt);
                } else if (file.getName().endsWith(".zip")) {
                    thumbnail.setImageResource(R.mipmap.type_zip);
                } else if (file.getName().endsWith(".rar")) {
                    thumbnail.setImageResource(R.mipmap.type_rar);
                } else if (file.getName().endsWith(".log")) {
                    thumbnail.setImageResource(R.mipmap.type_txt);
                } else if (file.getName().endsWith(".rtf")) {
                    thumbnail.setImageResource(R.mipmap.type_txt);
                } else {
                    thumbnail.setImageResource(R.mipmap.type_unknown);
                }
            }

            TextView filename = (TextView) convertView.findViewById(R.id.filename);
            filename.setText(file.getName());

            TextView filesize = (TextView) convertView.findViewById(R.id.filesize);
            if (filesize != null) {
                if (file.isFile()) filesize.setText(getHumanFileSize(file.length()));
                else filesize.setText("[文件夹]");
            }

            TextView filedate = (TextView) convertView.findViewById(R.id.filedate);
            if (filedate != null) {
                filedate.setText(getHumanFileDate(file));
            }

            return convertView;
        }

        CharSequence getHumanFileDate(File file) {
            CharSequence dateStr = "";
            dateStr = DateFormat.format("yyyy-MM-dd", file.lastModified());
            if (TextUtils.isEmpty(dateStr)) dateStr = "1970-01-01";
            return dateStr;
        }

        class ThumbnailLoader extends AsyncTask<File, Void, Bitmap> {
            private final WeakReference<ImageView> imageViewReference;

            public ThumbnailLoader(ImageView imageView) {
                imageViewReference = new WeakReference<ImageView>(imageView);
            }

            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected Bitmap doInBackground(File... arg0) {
                Bitmap thumbnailBitmap = null;
                File file = arg0[0];
                if (DEBUG) UtilTool.Log("fengjian","Loading thumbnail");
                if (file != null) {
                    if (DEBUG) UtilTool.Log("fengjian",file.getAbsolutePath());
                    try {
                        ContentResolver crThumb = getContentResolver();
                        if (Arrays.asList(mVideoExtensions).contains(getFileExtension(file))) {
                            if (DEBUG) UtilTool.Log("fengjian","Video");
                            Cursor cursor = crThumb.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "='" + file.getAbsolutePath() + "'", null, null);
                            if (cursor != null) {
                                if (DEBUG) UtilTool.Log("fengjian","Cursor is not null");
                                if (cursor.getCount() > 0) {
                                    if (DEBUG) UtilTool.Log("fengjian","Cursor has data");
                                    cursor.moveToFirst();
                                    thumbnailBitmap = MediaStore.Video.Thumbnails.getThumbnail(crThumb, cursor.getInt(0), MediaStore.Video.Thumbnails.MICRO_KIND, null);
                                }
                                cursor.close();
                            }
                        } else if (Arrays.asList(mImagesExtensions).contains(getFileExtension(file))) {
                            if (DEBUG) UtilTool.Log("fengjian","Image");
                            Cursor cursor = crThumb.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "='" + file.getAbsolutePath() + "'", null, null);
                            if (cursor != null) {
                                if (DEBUG) UtilTool.Log("fengjian","Cursor is not null");
                                if (cursor.getCount() > 0) {
                                    if (DEBUG) UtilTool.Log("fengjian","Cursor has data");
                                    cursor.moveToFirst();
                                    thumbnailBitmap = MediaStore.Images.Thumbnails.getThumbnail(crThumb, cursor.getInt(0), MediaStore.Images.Thumbnails.MINI_KIND, null);
                                }
                                cursor.close();
                            }
                        }
                        if (DEBUG) UtilTool.Log("fengjian","Finished");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (Error e) {
                        e.printStackTrace();
                    }
                }
                if (DEBUG)
                    UtilTool.Log("fengjian","Thumbnail: " + (thumbnailBitmap == null ? "null" : "Ok"));
                if (thumbnailBitmap != null)
                    addBitmapToCache(file.getAbsolutePath(), thumbnailBitmap);
                return thumbnailBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (imageViewReference != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        if (bitmap == null) imageView.setImageResource(R.mipmap.ys__ic_file);
                        else imageView.setImageBitmap(bitmap);
                    }
                }
            }

        }

    }

    // Need for backward compatibility to Android 1.6
    private class OldApiHelper {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        private int getBtimapSize(Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    }

    String getHumanFileSize(long size) {
        String[] units = getResources().getStringArray(R.array.ys__size_units);
        for (int i = units.length - 1; i >= 0; i--) {
            if (size >= Math.pow(1024, i)) {
                return Math.round((size / Math.pow(1024, i))) + " " + units[i];
            }
        }
        return size + " " + units[0];
    }

}
