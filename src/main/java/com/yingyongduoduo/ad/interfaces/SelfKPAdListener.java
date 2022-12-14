package com.yingyongduoduo.ad.interfaces;


import com.yingyongduoduo.ad.bean.ADBean;

public interface SelfKPAdListener {
    void onAdPresent(ADBean bean);

    void onAdDismissed(ADBean bean);

    void onAdFailed(ADBean bean);

    void onAdClick(ADBean bean);
}
