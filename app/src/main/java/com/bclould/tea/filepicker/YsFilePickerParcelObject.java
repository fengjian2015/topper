package com.bclould.tea.filepicker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class YsFilePickerParcelObject implements Parcelable {

    public String path = "";
    public ArrayList<String> names = new ArrayList<String>();
    public int count = 0;

    public YsFilePickerParcelObject(String path, ArrayList<String> names, int count) {
        this.path = path;
        this.names = names;
        this.count = count;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(path);
        parcel.writeStringList(names);
        parcel.writeInt(count);
    }

    public static final Parcelable.Creator<YsFilePickerParcelObject> CREATOR = new Parcelable.Creator<YsFilePickerParcelObject>() {
        public YsFilePickerParcelObject createFromParcel(Parcel in) {
            return new YsFilePickerParcelObject(in);
        }

        public YsFilePickerParcelObject[] newArray(int size) {
            return new YsFilePickerParcelObject[size];
        }
    };

    private YsFilePickerParcelObject(Parcel parcel) {
        path = parcel.readString();
        parcel.readStringList(names);
        count = parcel.readInt();
    }

}
