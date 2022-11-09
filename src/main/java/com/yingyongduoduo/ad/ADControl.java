package com.yingyongduoduo.ad;

import static com.bytedance.sdk.openadsdk.TTAdLoadType.PRELOAD;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.yingyongduoduo.ad.bean.ADBean;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.dialog.GDTMuBanTuiPingDialog;
import com.yingyongduoduo.ad.dialog.SelfCPDialog;
import com.yingyongduoduo.ad.dialog.SelfTuiPingDialog;
import com.yingyongduoduo.ad.dialog.UpdateDialog;
import com.yingyongduoduo.ad.interfaceimpl.ADListener;
import com.yingyongduoduo.ad.interfaceimpl.SelfBannerAdListener;
import com.yingyongduoduo.ad.interfaceimpl.SelfBannerView;
import com.yingyongduoduo.ad.interfaceimpl.SelfKPAdListener;
import com.yingyongduoduo.ad.interfaceimpl.SelfKPView;
import com.yingyongduoduo.ad.utils.ScreenUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class ADControl {

    private static int score = 0;

    public static long lastshowadTime = 0;

    public static Boolean isonshow = false;
    private static boolean ISGiveHaoping = false;
    private static HashMap<String, String> giveHaoping = new HashMap<String, String>();

    //展示5分好评广告，首次进来不展示，和插屏广告戳开，隔间10秒
    private static long divideTime = 8L * 1000L;
    private static long showadTimeDuration = 120 * 1000;
    private static long lastshowHaopingTime = System.currentTimeMillis();

    public static String oldADVersition = "";

    public void ChangeTVAddrVersion(Context context, String newVersion) {
        SharedPreferences mSettings = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        Editor editor = mSettings.edit();
        editor.putString("addrversion", newVersion);
        editor.apply();
        ADControl.oldADVersition = newVersion;
    }

    private void ShowCSJKP(final Activity context, final RelativeLayout adsParent, final View skip_view, final ADListener kpAdListener, String appid, String adplaceid) {
        //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adplaceid)
                .setImageAcceptedSize(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context) - ScreenUtils.getVirtualBarHeigh(context))
                .setAdLoadType(PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
//                .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP) // 应用每次下载都需要触发弹窗披露应用信息（不含跳转商店的场景），该配置优先级高于下载网络弹窗配置；
                .build();

        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.CSJSplashAdListener() {
            @Override
            public void onSplashLoadSuccess() {

            }

            @Override
            public void onSplashLoadFail(CSJAdError csjAdError) {
                //开发者处理跳转到APP主页面逻辑
//                kpAdListener.onAdFailed(csjAdError.getMsg() + "");
//                csjAdError.getCode() == 23：超时
                showGdtKp(csjAdError, context, kpAdListener, adsParent, skip_view);
            }

            @Override
            public void onSplashRenderSuccess(CSJSplashAd ad) {
                if (ad == null) {
                    kpAdListener.onAdDismissed();
                    return;
                }
                if (adsParent != null && !context.isFinishing()) {
                    adsParent.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width =屏幕宽；height >=75%屏幕高
                    ad.showSplashView(adsParent);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                } else {
                    //开发者处理跳转到APP主页面逻辑
                    kpAdListener.onAdDismissed();
                }

                //设置SplashView的交互监听器
                ad.setSplashAdListener(new CSJSplashAd.SplashAdListener() {
                    @Override
                    public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                        kpAdListener.onAdPresent();
                    }

                    @Override
                    public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                        kpAdListener.onAdClick();
                    }

                    @Override
                    public void onSplashAdClose(CSJSplashAd csjSplashAd, int i) {
                        kpAdListener.onAdDismissed();
                    }
                });
            }

            @Override
            public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
                //开发者处理跳转到APP主页面逻辑
//                kpAdListener.onAdFailed(csjAdError.getMsg() + "");
//                csjAdError.getCode() == 23：超时
                showGdtKp(csjAdError, context, kpAdListener, adsParent, skip_view);
            }
        }, 4000);
    }

    private void showGdtKp(final CSJAdError csjAdError, final Activity context, final ADListener kpAdListener, final RelativeLayout adsParent, final View skip_view) {
        if (context == null || context.isFinishing() || context.isDestroyed()) {
            kpAdListener.onAdFailed(csjAdError.getMsg() + "");
            return;
        }
        if (AppConfig.getKPType().equals("csj")) {
            String id_String = AppConfig.configBean.ad_kp_idMap.get("gdt");
            if (!TextUtils.isEmpty(id_String) && id_String.split(",").length == 2) {
                String[] a = id_String.split(",");
                String appid = a[0];
                String adplaceid = a[1];
                ShowGDTKP(context, adsParent, skip_view, kpAdListener, appid, adplaceid);
            } else {
                kpAdListener.onAdFailed("");
            }
        } else {
            kpAdListener.onAdFailed(csjAdError.getMsg() + "");
        }
    }


    private void ShowGDTKP(final Activity context, final RelativeLayout adsParent, final View skip_view, final ADListener kpAdListener, String appid, String adplaceid) {
        SplashADListener listener = new SplashADListener() {

            @Override
            public void onADDismissed() {
                kpAdListener.onAdDismissed();
            }

            @Override
            public void onNoAD(AdError adError) {
                if (context == null || context.isFinishing() || context.isDestroyed()) {
                    kpAdListener.onAdFailed("");
                    return;
                }
                if (AppConfig.getKPType().equals("gdt")) {
                    String id_String = AppConfig.configBean.ad_kp_idMap.get("csj");
                    if (!TextUtils.isEmpty(id_String) && id_String.split(",").length == 2) {
                        String[] a = id_String.split(",");
                        String appid = a[0];
                        String adplaceid = a[1];
                        ShowCSJKP(context, adsParent, skip_view, kpAdListener, appid, adplaceid);
                    } else {
                        kpAdListener.onAdFailed(adError != null ? adError.getErrorMsg() : "");
                    }
                } else {
                    kpAdListener.onAdFailed(adError != null ? adError.getErrorMsg() : "");
                }
            }

            @Override
            public void onADPresent() {
                kpAdListener.onAdPresent();
            }

            @Override
            public void onADClicked() {
                kpAdListener.onAdClick();
            }

            @Override
            public void onADTick(long l) {
                kpAdListener.onAdTick(l);
            }

            @Override
            public void onADExposure() {

            }

            @Override
            public void onADLoaded(long l) {

            }
        };
        SplashAD splashAD = new SplashAD((Activity) context, adplaceid, listener, 0);
        splashAD.fetchAndShowIn(adsParent);
    }

    private void ShowSelfKP(final Context context, RelativeLayout adsParent, final ADListener kpAdListener) {

        SelfKPAdListener listener = new SelfKPAdListener() {
            @Override
            public void onAdDismissed(ADBean bean) {//广告展示完毕
                kpAdListener.onAdDismissed();
            }

            @Override
            public void onAdFailed(ADBean bean) {//广告获取失败
                kpAdListener.onAdFailed("");
            }

            @Override
            public void onAdPresent(ADBean bean) {//广告开始展示
                kpAdListener.onAdPresent();
//                if (bean != null && !TextUtils.isEmpty(bean.getAd_name())) {
//                    Map<String, String> map_ekv = new HashMap<String, String>();
//                    map_ekv.put("show", bean.getAd_name());
//                }
            }

            @Override
            public void onAdClick(ADBean bean) {//广告被点击
                kpAdListener.onAdClick();
//                if (bean != null && !TextUtils.isEmpty(bean.getAd_name())) {
//                    Map<String, String> map_ekv = new HashMap<String, String>();
//                    map_ekv.put("click", bean.getAd_name());
//                }
            }
        };
        SelfKPView selfKPView = new SelfKPView(context);
        selfKPView.setADListener(listener);
        adsParent.removeAllViews();
        adsParent.addView(selfKPView);
    }

    //初始化广点通退屏广告
    public static Boolean InitGDTMuBanTP(Context context) {
        if (AppConfig.isShowTP())//展示退屏广告
        {
            String kpType = AppConfig.getTPType();//获取开屏广告类型，baidu，gdt，google
            String kp_String = AppConfig.configBean.ad_tp_idMap.get(kpType);
            if (!TextUtils.isEmpty(kp_String)) {
                String[] a = kp_String.split(",");
                if (a.length == 2) {
                    String appid = a[0];
                    String adplaceid = a[1];
                    if ("gdtmb".equals(kpType)) {
                        GDTMuBanTuiPingDialog.Init(context, appid, adplaceid);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else//不展示开屏广告
        {
            return false;
        }

    }

    //初始化广点通退屏广告
    public Boolean ShowTPAD(Context context) {
        if (AppConfig.isShowTP())//展示开屏广告
        {
            String kpType = AppConfig.getTPType();//获取开屏广告类型，baidu，gdt，google
            String kp_String = AppConfig.configBean.ad_tp_idMap.get(kpType);
            if (!TextUtils.isEmpty(kpType) && "self".equals(kpType)) {//退屏类型为自家的
                SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context);
                sfCP.show();
                return false;
            } else if (!TextUtils.isEmpty(kp_String)) {//并非自家的，来自广点通，百度等，目前只有广点通
                String[] a = kp_String.split(",");
                if (a.length == 2) {
                    String appid = a[0];
                    String adplaceid = a[1];
                    if ("gdtmb".equals(kpType)) {
                        GDTMuBanTuiPingDialog sfCP = new GDTMuBanTuiPingDialog(context);
                        sfCP.show();
                        return true;
                    } else {//有两个id，但是又不是广点通
                        SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context, null);
                        sfCP.show();
                        return false;
                    }
                } else {//id没有两个，则暂时表示配置有问题，如果以后某个平台id只有一个，则重新写该方法
                    SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context, null);
                    sfCP.show();
                    return false;
                }
            } else {//如果返回的id为空，又不展示自家广告，这种情况可能是后台配置错误，则不展示广告
                SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context, null);
                sfCP.show();
                return true;
            }
        } else {//不展示退屏广告
            SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context, null);
            sfCP.show();
            return false;
        }

    }

    public void showKp(Activity context, RelativeLayout adsParent, View skipView, final ADListener kpAdListener) {
        if (AppConfig.isShowKP()) {//展示开屏广告
            String kpType = AppConfig.getKPType();//获取开屏广告类型，baidu，gdt，google
            String kp_String = AppConfig.configBean.ad_kp_idMap.get(kpType);
            if (!TextUtils.isEmpty(kp_String)) {
                String[] a = kp_String.split(",");
                if (a.length == 2) {
                    String appid = a[0];
                    String adplaceid = a[1];
                    if ("baidu".equals(kpType)) {
                        ShowSelfKP(context, adsParent, kpAdListener);
                    } else if ("csj".equals(kpType)) {
                        ShowCSJKP(context, adsParent, skipView, kpAdListener, appid, adplaceid);
                    } else if ("gdt".equals(kpType)) {
                        ShowGDTKP(context, adsParent, skipView, kpAdListener, appid, adplaceid);
                    } else {
                        kpAdListener.onAdFailed("其他不支持广告类型" + kp_String);
                    }
                }
            } else {
                kpAdListener.onAdFailed("没有获取到广告类型");
            }
        } else {//不展示开屏广告
            kpAdListener.onAdFailed("后台不展示开屏广告");
        }

    }

    private UnifiedInterstitialAD interAd;
    private TTNativeExpressAd mInterAd;

    private void showCSJNewCp(final Activity context, final String appid, final String adplaceid) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adplaceid)
                //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                .setExpressViewAcceptedSize(500,500)
                .setSupportDeepLink(true)
                .setOrientation(TTAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setAdLoadType(PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();


        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            //请求广告失败
            @Override
            public void onError(int code, String message) {
                Log.e("ADControl", "Callback --> onError: " + code + ", " + message);
                if (context == null || context.isFinishing() || context.isDestroyed()) return;
                if (AppConfig.getCPType().equals("csj2")) {
                    String id_String = AppConfig.configBean.ad_cp_idMap.get("gdt2");
                    if (!TextUtils.isEmpty(id_String) && id_String.split(",").length == 2) {
                        String[] a = id_String.split(",");
                        String appid = a[0];
                        String adplaceid = a[1];
                        ShowGDTCP2(context, appid, adplaceid);
                    }
                } else {
                    ShowSelfCP(context);
                }
            }

            //广告物料加载完成的回调
            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {

                // 广告交互监听器
                ad.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                    }

                    @Override
                    public void onAdVideoBarClick() {
                    }

                    @Override
                    public void onAdClose() {
                    }

                    @Override
                    public void onVideoComplete() {
                    }

                    @Override
                    public void onSkippedVideo() {
                    }

                });
            }

            //广告视频/图片加载完成的回调，接入方可以在这个回调后展示广告
            @Override
            public void onFullScreenVideoCached() {

            }

            @Override
            public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {
                if (ttFullScreenVideoAd != null) {
                    //展示广告，并传入广告展示的场景
                    ttFullScreenVideoAd.showFullScreenVideoAd(context, TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
                }
            }
        });
    }


    private void ShowCSJCP(final Activity context, String appid, String adplaceid) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);

        int i = new Random(100).nextInt();

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adplaceid) //广告位id
                .setAdCount(1) //请求广告数量为1到3条
//                .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP) // 应用每次下载都需要触发弹窗披露应用信息（不含跳转商店的场景），该配置优先级高于下载网络弹窗配置；
                // 300 * 300，300 * 450，450 * 300
//                .setExpressViewAcceptedSize(i > 50 ? 300 : 450, i > 50 ? 300 : 450) //期望模板广告view的size,单位dp
                .setExpressViewAcceptedSize(300, 300) //期望模板广告view的size,单位dp
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e("ADControl", "loadInteractionExpressAd onError : " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mInterAd = ads.get(0);
                if (mInterAd == null)
                    return;

                mInterAd.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
                    @Override
                    public void onAdDismiss() {

                    }

                    @Override
                    public void onAdClicked(View view, int type) {
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                    }

                    @Override
                    public void onRenderFail(View view, String msg, int code) {
                    }

                    @Override
                    public void onRenderSuccess(View view, float width, float height) {
                        if (context != null && !context.isFinishing() && mInterAd != null)
                            mInterAd.showInteractionExpressAd(context);
                    }
                });
                mInterAd.render();
            }
        });
    }

    private void ShowGDTCP2(final Activity context, String appid, String adplaceid) {
        interAd = getIAD(context, appid, adplaceid, new UnifiedInterstitialADListener() {
            @Override
            public void onADReceive() {
                if (context == null || context.isFinishing() || interAd == null)
                    return;
                interAd.show();
            }

            @Override
            public void onVideoCached() {

            }

            @Override
            public void onNoAD(AdError adError) {
                if (context == null || context.isFinishing() || context.isDestroyed()) return;
                if (AppConfig.getCPType().equals("gdt2")) {
                    String id_String = AppConfig.configBean.ad_cp_idMap.get("csj2");
                    if (!TextUtils.isEmpty(id_String) && id_String.split(",").length == 2) {
                        String[] a = id_String.split(",");
                        String appid = a[0];
                        String adplaceid = a[1];
                        showCSJNewCp(context, appid, adplaceid);
                    }
                } else {
                    ShowSelfCP(context);
                }
            }


            @Override
            public void onADOpened() {

            }

            @Override
            public void onADExposure() {

            }

            @Override
            public void onADClicked() {

            }

            @Override
            public void onADLeftApplication() {

            }

            @Override
            public void onADClosed() {

            }

            @Override
            public void onRenderSuccess() {

            }

            @Override
            public void onRenderFail() {

            }
        });

        interAd.loadAD();
    }

    private UnifiedInterstitialAD getIAD(Activity context, String appid, String posId, UnifiedInterstitialADListener listener) {
        if (interAd != null && interAd.isValid()) {
            interAd.close();
            interAd.destroy();
            interAd = null;
        }
        interAd = new UnifiedInterstitialAD(context, posId, listener);
        return interAd;
    }

    private void ShowSelfCP(final Context context) {
        SelfCPDialog sfCP = new SelfCPDialog(context);
        sfCP.setADListener(new SelfBannerAdListener() {
            @Override
            public void onAdClick(ADBean adBean) {
            }

            @Override
            public void onAdFailed() {

            }

            @Override
            public void onADReceiv(ADBean adBean) {
            }
        });
        sfCP.show();
    }

    public void ShowCp(Activity context) {
        if (AppConfig.isShowCP()) {//展示开屏广告
            if (System.currentTimeMillis() - lastshowadTime < showadTimeDuration) {
                System.out.println("广告时间没到" + (System.currentTimeMillis() - lastshowadTime));
                return;
            }
            lastshowadTime = System.currentTimeMillis();
            showCpContent(context);
        }
    }

    public void showCpContent(Activity context) {
        if (AppConfig.isShowCP()) {
            String cpType = AppConfig.getCPType();//获取开屏广告类型，baidu，gdt，google
            String kp_String = AppConfig.configBean.ad_cp_idMap.get(cpType);

            if (!TextUtils.isEmpty(kp_String)) {
                String[] a = kp_String.split(",");
                if (a.length == 2) {
                    String appid = a[0];
                    String adplaceid = a[1];
                    if ("csj2".equals(cpType)) {
                        showCSJNewCp(context, appid, adplaceid);
                    } else if ("csj".equals(cpType)) {
                        ShowCSJCP(context, appid, adplaceid);
                    } else if ("gdt2".equals(cpType)) {
                        ShowGDTCP2(context, appid, adplaceid);
                    } else if ("self".equals(cpType)) {
                        ShowSelfCP(context);
                    } else {
                        // kpAdListener.onAdFailed("其他不支持广告类型" + kp_String);
                    }
                }
            } else {
                ShowSelfCP(context);
            }
        }
    }

    private void addCSJBanner(final LinearLayout lyt, final Activity context, final String appid, final String adplaceid) {
        if (mTTAd != null) {
            if (lyt != null)
                lyt.removeAllViews();
            mTTAd.destroy();
            mTTAd = null;
        }
        try {

            TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);

            //step4:创建广告请求参数AdSlot,具体参数含义参考文档
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(adplaceid) //广告位id

                    .setAdCount(1) //请求广告数量为1到3条
                    .setExpressViewAcceptedSize(ScreenUtils.getScreenWidth(context), 60) //期望模板广告view的size,单位dp
//                    .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP) // 应用每次下载都需要触发弹窗披露应用信息（不含跳转商店的场景），该配置优先级高于下载网络弹窗配置；
                    .build();
            //step5:请求广告，对请求回调的广告作渲染处理
            mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
                @Override
                public void onError(int code, String message) {
                    if (context == null || context.isFinishing() || context.isDestroyed()) return;
                    if (AppConfig.getBannerType().equals("csj")) {
                        String id_String = AppConfig.configBean.ad_banner_idMap.get("gdt2");
                        if (!TextUtils.isEmpty(id_String) && id_String.split(",").length == 2) {
                            String[] a = id_String.split(",");
                            String appid = a[0];
                            String adplaceid = a[1];
                            addGDTBanner2(lyt, context, appid, adplaceid);
                        }
                    } else {
                        addSelfBanner(lyt, context);
                    }
                }

                @Override
                public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                    if (ads == null || ads.size() == 0) {
                        return;
                    }
                    mTTAd = ads.get(0);
                    if (mTTAd == null) {
                        return;
                    }
                    mTTAd.setSlideIntervalTime(30 * 1000);
                    mTTAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                        }

                        @Override
                        public void onRenderFail(View view, String msg, int code) {
                        }

                        @Override
                        public void onRenderSuccess(View view, float width, float height) {
                            //返回view的宽高 单位 dp
                            if (lyt != null) {
                                lyt.removeAllViews();
                                lyt.addView(view);
                            }
                        }
                    });
                    mTTAd.render();

                    //使用默认模板中默认dislike弹出样式
                    mTTAd.setDislikeCallback(context, new TTAdDislike.DislikeInteractionCallback() {
                        @Override
                        public void onShow() {

                        }

                        @Override
                        public void onSelected(int position, String value, boolean enforce) {
                            if (lyt != null)
                                lyt.removeAllViews();
                            AppConfig.isCanShowBanner = false;
                        }

                        @Override
                        public void onCancel() {
                        }

                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addGDTBanner2(final LinearLayout lyt, final Activity context, String appid, String adplaceid) {
        if (unifiedBannerView != null) {
            lyt.removeAllViews();
            unifiedBannerView.destroy();
            unifiedBannerView = null;
        }
        try {
            unifiedBannerView = new UnifiedBannerView(context, adplaceid, new UnifiedBannerADListener() {
                @Override
                public void onNoAD(AdError adError) {
                    if (context == null || context.isFinishing() || context.isDestroyed()) return;
                    if (AppConfig.getBannerType().equals("gdt2")) {
                        String id_String = AppConfig.configBean.ad_banner_idMap.get("csj");
                        if (!TextUtils.isEmpty(id_String) && id_String.split(",").length == 2) {
                            String[] a = id_String.split(",");
                            String appid = a[0];
                            String adplaceid = a[1];
                            addCSJBanner(lyt, context, appid, adplaceid);
                        }
                    } else {
                        addSelfBanner(lyt, context);
                    }
                }

                @Override
                public void onADReceive() {

                }

                @Override
                public void onADExposure() {

                }

                @Override
                public void onADClosed() {

                }

                @Override
                public void onADClicked() {
                    System.out.println("广点通广告被点击");
                }

                @Override
                public void onADLeftApplication() {

                }

            });
            lyt.addView(unifiedBannerView, getUnifiedBannerLayoutParams(context));
            // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
            unifiedBannerView.loadAD();

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    /**
     * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
     *
     * @return
     */
    private LinearLayout.LayoutParams getUnifiedBannerLayoutParams(Activity context) {
        Point screenSize = new Point();
        context.getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new LinearLayout.LayoutParams(screenSize.x, Math.round(screenSize.x / 6.4F));
    }

    public void addGoogleBanner(final LinearLayout lyt, final Activity context, String appid, String adplaceid) {
        lyt.removeAllViews();
    }

    public void addSelfBanner(LinearLayout lyt, final Activity context) {
        lyt.removeAllViews();
        try {
            SelfBannerView bv = new SelfBannerView(context);
            bv.setADListener(new SelfBannerAdListener() {
                @Override
                public void onAdClick(ADBean adBean) {
                }

                @Override
                public void onAdFailed() {

                }

                @Override
                public void onADReceiv(ADBean adBean) {
//                    if (adBean != null && !TextUtils.isEmpty(adBean.getAd_name())) {
//                        Map<String, String> map_ekv = new HashMap<String, String>();
//                        map_ekv.put("show", adBean.getAd_name());
//                        System.out.println("广告被展示:"+adBean.getAd_name());
//                    }
                }
            });
            lyt.addView(bv);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private UnifiedBannerView unifiedBannerView;
    private TTNativeExpressAd mTTAd;

    /**
     * 显示广告
     *
     * @param lyt     banner广告位置
     * @param context context
     */
    public void showAd(LinearLayout lyt, Activity context) {
        ShowCp(context);
        homeGet5Score(context);
        if (AppConfig.isShowBanner() && lyt != null) {//展示广告条广告
            String bannerType = AppConfig.getBannerType();//获取开屏广告类型，baidu，gdt，google
            String banner_String = AppConfig.configBean.ad_banner_idMap.get(bannerType);

            if (!TextUtils.isEmpty(banner_String)) {
                String[] a = banner_String.split(",");
                if (a.length == 2) {
                    String appid = a[0];
                    String adplaceid = a[1];
                    if ("csj".equals(bannerType)) {
                        addCSJBanner(lyt, context, appid, adplaceid);
                    } else if ("gdt2".equals(bannerType)) {
                        addGDTBanner2(lyt, context, appid, adplaceid);
                    } else if ("google".equals(bannerType)) {
                        addGoogleBanner(lyt, context, appid, adplaceid);
                    } else if ("self".equals(bannerType)) {
                        addSelfBanner(lyt, context);
                    } else {
//                        kpAdListener.onAdFailed("其他不支持广告类型" + kp_String);
                    }
                }
            } else {
                addSelfBanner(lyt, context);
            }
        }
    }

    public void setISGiveHaoping(Context context, Boolean isgivehaoping) {
        ISGiveHaoping = isgivehaoping;
        SharedPreferences mSettings = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE); //
        Editor editor = mSettings.edit();
        editor.putBoolean("ISGiveHaoping", true);
        editor.apply();
    }

    public boolean getIsGiveHaoping(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        ISGiveHaoping = mSettings.getBoolean("ISGiveHaoping", false);
        return ISGiveHaoping;
    }

    public void setScore(Context context, int score) {
        ADControl.score = score;
        SharedPreferences mSettings = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        Editor editor = mSettings.edit();
        editor.putInt("score", ADControl.score);
        editor.apply();
    }

    public static int getScore(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        return mSettings.getInt("score", -1);
    }

    private void homeGet5Score(final Activity context) {

        if (getIsGiveHaoping(context)) {
            return;
        }
        if (!AppConfig.isShowHaoPing()) {
            return;
        }

        if (isonshow)
            return;

        if (System.currentTimeMillis() - lastshowHaopingTime < showadTimeDuration) {
            Log.i("广告时间没到", (System.currentTimeMillis() - lastshowHaopingTime) + "");
            return;
        }

        lastshowHaopingTime = System.currentTimeMillis();
        isonshow = true;

        new AlertDialog.Builder(context).setTitle("申请开放超级GPS")
                .setMessage("\t\t在市场给5星好评,24小时内审核即可自动切换到超级GPS模式！")
                .setPositiveButton("给个好评", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setISGiveHaoping(context, true);
                        goodPinglun(context);
                        isonshow = false;
                    }
                }).setNeutralButton("以后再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                isonshow = false;
            }
        }).setCancelable(false).show();
    }


    /*
     * 加入QQ群的代码
     * */
    public boolean joinQQGroup(Context context) {
        try {
            String key = AppConfig.publicConfigBean.qqKey;
            if (key == null || "".equals(key)) {
                Toast.makeText(context, "请手工添加QQ群:286239217", Toast.LENGTH_SHORT).show();
                return false;
            }
            Intent intent = new Intent();
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "请手工添加QQ群:286239217", Toast.LENGTH_SHORT).show();
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public static boolean update(Context context) {
        if (AppConfig.isShowUpdate()) {
            int currentVersion = 0;
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                currentVersion = pi.versionCode;
                if (currentVersion < AppConfig.configBean.updatemsg.versioncode) {
                    UpdateDialog dg = new UpdateDialog(context);
                    dg.show();
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;

        } else
            return false;
    }

    public void goodPinglun(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "异常", Toast.LENGTH_SHORT).show();
        }

    }

    public void destroyView() {
        if (interAd != null && interAd.isValid()) {
            interAd.close();
            interAd.destroy();
            interAd = null;
        }
        if (unifiedBannerView != null) {
            unifiedBannerView.destroy();
            unifiedBannerView = null;
        }
        if (mTTAd != null) {
            mTTAd.destroy();
            mTTAd = null;
        }
        if (mInterAd != null) {
            mInterAd.destroy();
            mInterAd = null;
        }
    }


}
