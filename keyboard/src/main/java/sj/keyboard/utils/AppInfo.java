package sj.keyboard.utils;

/**
 * Created by GA on 2017/12/27.
 */

public class AppInfo {
    private int id;
    private int icon;
    private String funcName;

    public int getIcon() {
        return icon;
    }

    public String getFuncName() {
        return funcName;
    }

    public int getId() {
        return id;
    }

    public AppInfo(int icon, String funcName){
        this.icon = icon;
        this.funcName = funcName;
    }
}
