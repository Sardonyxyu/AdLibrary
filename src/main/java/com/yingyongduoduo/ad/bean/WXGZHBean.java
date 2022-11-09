package com.yingyongduoduo.ad.bean;

import java.io.Serializable;

public class WXGZHBean implements Serializable {
    public String displayName;//公众号名称
    public String introduction;//公众号简介
    public String url;//发送到微信朋友圈或者朋友打开可以关注的网页
    public String id;//公众号的id
    public String thumb;
    public String type;
    public boolean isPicExist=false;//本地图片是否存在
}
