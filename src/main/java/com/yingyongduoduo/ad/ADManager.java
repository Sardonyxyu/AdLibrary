package com.yingyongduoduo.ad;


import android.app.Activity;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingyongduoduo on 2018/8/9.
 */

public class ADManager {
    private static class SingletonHolder {
        private static final ADManager instance = new ADManager();
    }

    public static long initTime = 0L;

    private ADManager() {

    }

    public static ADManager getInsatance() {
        return SingletonHolder.instance;
    }

    public void initPosAD(Activity context, String appid, String sid) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(sid) //广告位id
                .setAdCount(3) //请求广告数量为1到3条
//                    .setExpressViewAcceptedSize(ScreenUtils.getScreenWidth(context), 60) //期望模板广告view的size,单位dp
                .setExpressViewAcceptedSize(300, 200) //期望模板广告view的size,单位dp:宽度不能小于350
//                .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP) // 应用每次下载都需要触发弹窗披露应用信息（不含跳转商店的场景），该配置优先级高于下载网络弹窗配置；
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e("ADManager", "onError : " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                initTime = System.currentTimeMillis();
                if (ads == null || ads.size() == 0) {
                    return;
                }
                Log.e("ADManager", "加载广告成功，数量 = " + ads.size() + "个");
                csjADList.addAll(ads);
            }
        });
    }

    public void initPosAD2(Activity context, String appid, String sid) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(sid) //广告位id
                .setAdCount(3) //请求广告数量为1到3条
                .setSupportDeepLink(true)
                .setAdLoadType(TTAdLoadType.PRELOAD)
//                    .setExpressViewAcceptedSize(ScreenUtils.getScreenWidth(context), 60) //期望模板广告view的size,单位dp
                .setExpressViewAcceptedSize(350, 0) //期望模板广告view的size,单位dp:宽度不能小于350
//                .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP) // 应用每次下载都需要触发弹窗披露应用信息（不含跳转商店的场景），该配置优先级高于下载网络弹窗配置；
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e("ADManager", "onError : " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                initTime = System.currentTimeMillis();
                if (ads == null || ads.size() == 0) {
                    return;
                }
                Log.e("ADManager", "加载广告成功，数量 = " + ads.size() + "个");
                csjADList.addAll(ads);
            }
        });
    }

    private List<TTNativeExpressAd> csjADList = new ArrayList<>();

    public List<TTNativeExpressAd> getCsjADList() {
        return csjADList;
    }

    public ADManager setCsjADList(List<TTNativeExpressAd> csjADList) {
        this.csjADList = csjADList;
        return this;
    }

}
