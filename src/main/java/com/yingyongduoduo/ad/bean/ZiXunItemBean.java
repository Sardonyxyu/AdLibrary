package com.yingyongduoduo.ad.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class ZiXunItemBean implements Parcelable {
    private List<ZiXunListItemBean> list;
    private String tabName;
    private String icon;
    private String selIcon;

    public ZiXunItemBean(){}

    protected ZiXunItemBean(Parcel in) {
        tabName = in.readString();
        icon = in.readString();
        selIcon = in.readString();
    }

    public String getSelIcon() {
        return selIcon;
    }

    public ZiXunItemBean setSelIcon(String selIcon) {
        this.selIcon = selIcon;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public ZiXunItemBean setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getTabName() {
        return tabName;
    }

    public ZiXunItemBean setTabName(String tabName) {
        this.tabName = tabName;
        return this;
    }

    public List<ZiXunListItemBean> getList() {
        return list;
    }

    public ZiXunItemBean setList(List<ZiXunListItemBean> list) {
        this.list = list;
        return this;
    }

    public static final Creator<ZiXunItemBean> CREATOR = new Creator<ZiXunItemBean>() {
        @Override
        public ZiXunItemBean createFromParcel(Parcel in) {
            return new ZiXunItemBean(in);
        }

        @Override
        public ZiXunItemBean[] newArray(int size) {
            return new ZiXunItemBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tabName);
        dest.writeString(selIcon);
        dest.writeString(icon);
    }
}
