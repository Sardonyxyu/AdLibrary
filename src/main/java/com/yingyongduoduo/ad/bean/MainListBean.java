package com.yingyongduoduo.ad.bean;

import java.io.Serializable;

public class MainListBean extends ADBean implements Serializable {
    private static final long serialVersionUID = 168652213201L;
    //壁纸地址
    private String hoverURL;
    private String thumbURL;
    private String middleURL;

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getMiddleURL() {
        return middleURL;
    }

    public void setMiddleURL(String middleURL) {
        this.middleURL = middleURL;
    }

    public String getHoverURL() {
        return hoverURL;
    }

    public void setHoverURL(String hoverURL) {
        this.hoverURL = hoverURL;
    }
}
