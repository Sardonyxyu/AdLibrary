package com.yingyongduoduo.ad.interfaces;

public interface ADListener {
    void onAdPresent();

    void onAdDismissed();

    void onAdFailed(String var1);

    void onAdClick();

    void onAdTick(long millisUntilFinished);
}
