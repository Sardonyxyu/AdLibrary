package com.yingyongduoduo.ad.interfaceimpl;


import com.yingyongduoduo.ad.bean.ADBean;

public interface SelfKPAdListener {
    void onAdPresent(ADBean bean);

    void onAdDismissed(ADBean bean);

    void onAdFailed(ADBean bean);

    void onAdClick(ADBean bean);
}
