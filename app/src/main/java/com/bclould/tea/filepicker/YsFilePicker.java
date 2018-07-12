package com.bclould.tea.filepicker;

import android.content.Intent;

public class YsFilePicker {
    public final static String SET_ONLY_ONE_ITEM = "OnlyOneItem";
    public final static String SET_FILTER_EXCLUDE = "FilterExclude";
    public final static String SET_FILTER_LISTED = "FilterListed";
    public final static String SET_CHOICE_TYPE = "ViewType";
    public final static String SET_START_DIRECTORY = "StartDirectory";
    public final static String SET_SORT_TYPE = "SortType";
    public final static String DISABLE_NEW_FOLDER_BUTTON = "DisableNewFolderButton";
    public final static String DISABLE_SORT_BUTTON = "DisableSortButton";
    public final static String ENABLE_QUIT_BUTTON = "EnableCancelButton";
    public static final int MAX_UPLOAD_SIZE = 10 * 1024 * 1024/*限制为10兆*/;

    public final static int CHOICE_TYPE_ALL = 0;
    public final static int CHOICE_TYPE_FILES = 1;
    public final static int CHOICE_TYPE_DIRECTORIES = 2;

    public final static int SORT_NAME_ASC = 0;
    public final static int SORT_NAME_DESC = 1;
    public final static int SORT_SIZE_ASC = 2;
    public final static int SORT_SIZE_DESC = 3;
    public final static int SORT_DATE_ASC = 4;
    public final static int SORT_DATE_DESC = 5;

//    public static void initSingle(Intent intent) {
//        intent.putExtra(YsFilePicker.SET_ONLY_ONE_ITEM, true);//只选一个文件
//        intent.putExtra(YsFilePicker.SET_FILTER_LISTED, new String[]{
//                "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt", "log"});//只显示文档
//        intent.putExtra(YsFilePicker.SET_FILTER_EXCLUDE, new String[]{"jpg", "png", "bmp", "jbg", "jpeg"});//不显示其中格式文件，不显示图片
//        intent.putExtra(YsFilePicker.DISABLE_NEW_FOLDER_BUTTON, true);//不显示新建文件夹
//        intent.putExtra(YsFilePicker.DISABLE_SORT_BUTTON, true);//不显示排序按钮
//        intent.putExtra(YsFilePicker.ENABLE_QUIT_BUTTON, true);//显示取消按钮
//
////		int checkedChoiceRadio = ((RadioGroup) findViewById(R.id.choice_type)).getCheckedRadioButtonId();
//        intent.putExtra(YsFilePicker.SET_CHOICE_TYPE, YsFilePicker.CHOICE_TYPE_FILES);//只选文件
////		if (checkedChoiceRadio == R.id.choice_type_directories) intent.putExtra(YsFilePicker.SET_CHOICE_TYPE, YsFilePicker.CHOICE_TYPE_DIRECTORIES);
//    }

    public static void initMulti(Intent intent) {
//		intent.putExtra(YsFilePicker.SET_ONLY_ONE_ITEM, false);//只选一个文件
//        intent.putExtra(YsFilePicker.SET_FILTER_LISTED, new String[]{
//                "doc", "docx", "xls", "xlsx",
//                "ppt", "pptx", "pdf", "txt",
//                "log", "zip", "rar", "rtf",
//                "jpg", "jpeg", "png",
//                "3gp","apk"});//只这些文件
		intent.putExtra(YsFilePicker.SET_FILTER_EXCLUDE, new String[] { "jpg","png","bmp","jbg","jpeg","gif" });//不显示其中格式文件，不显示图片
        intent.putExtra(YsFilePicker.DISABLE_NEW_FOLDER_BUTTON, true);//显示新建文件夹
        intent.putExtra(YsFilePicker.DISABLE_SORT_BUTTON, true);//显示排序按钮
        intent.putExtra(YsFilePicker.ENABLE_QUIT_BUTTON, true);//显示取消按钮

//		int checkedChoiceRadio = ((RadioGroup) findViewById(R.id.choice_type)).getCheckedRadioButtonId();
        intent.putExtra(YsFilePicker.SET_CHOICE_TYPE, YsFilePicker.CHOICE_TYPE_FILES);//只选文件
//		if (checkedChoiceRadio == R.id.choice_type_directories) intent.putExtra(YsFilePicker.SET_CHOICE_TYPE, YsFilePicker.CHOICE_TYPE_DIRECTORIES);
    }
}
