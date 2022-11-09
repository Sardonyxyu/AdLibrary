package com.yingyongduoduo.ad.interfaceimpl;


import com.yingyongduoduo.ad.bean.ADBean;

public interface SelfBannerAdListener {
    void onAdClick(ADBean adBean);
    void onAdFailed();
    void onADReceiv(ADBean adBean);
}
