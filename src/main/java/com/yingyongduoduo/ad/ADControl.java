package com.yingyongduoduo.ad;

import static com.bytedance.sdk.openadsdk.TTAdLoadType.PRELOAD;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
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
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.yingyongduoduo.ad.bean.ADBean;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.dialog.DialogTextViewBuilderAD;
import com.yingyongduoduo.ad.dialog.GDTMuBanTuiPingDialog;
import com.yingyongduoduo.ad.dialog.SelfCPDialog;
import com.yingyongduoduo.ad.dialog.SelfTuiPingDialog;
import com.yingyongduoduo.ad.dialog.UpdateDialog;
import com.yingyongduoduo.ad.interfaces.ADListener;
import com.yingyongduoduo.ad.interfaces.AdRewardVideoListener;
import com.yingyongduoduo.ad.interfaces.SelfBannerAdListener;
import com.yingyongduoduo.ad.interfaces.SelfBannerView;
import com.yingyongduoduo.ad.interfaces.SelfKPAdListener;
import com.yingyongduoduo.ad.interfaces.SelfKPView;
import com.yingyongduoduo.ad.utils.Logger;
import com.yingyongduoduo.ad.utils.ScreenUtils;

import java.util.List;
import java.util.Map;


public class ADControl {

    private static int score = 0;

    public static long lastshowadTime = 0;

    public static Boolean isonshow = false;
    private static boolean ISGiveHaoping = false;

    //展示5分好评广告，首次进来不展示，和插屏广告戳开，隔间10秒
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

    /**
     * 显示激励视频
     * @param activity
     * @param listener
     */
    public void showShiping(final Activity activity, final AdRewardVideoListener listener) {
        if (AppConfig.isShowShiping()) { //展示激励视频
            String spType = AppConfig.getShipingType();//获取开屏广告类型，baidu，gdt，google
            String sp_String = AppConfig.configBean.ad_shiping_idMap.get(spType);

            if (!TextUtils.isEmpty(sp_String)) {
                String[] a = sp_String.split(",");
                if (a.length == 2) {
                    String appid = a[0];
                    String adplaceid = a[1];
                    if ("csj".equals(spType)) {
                        showCsjVideo(activity, appid, adplaceid, listener);
                    } else if ("gdt".equals(spType)) {
                        showGdtVideoAd(activity, appid, adplaceid, listener);
                    } else {
                        listener.onError(-100, "没有支持的广告类型");
                    }
                }
            }
        }
    }

    /**
     * 显示穿山甲激励视频
     * @param activity
     * @param listener
     */
    public void showCsjVideo(final Activity activity, String appid, String codeId, final AdRewardVideoListener listener){

        final String TAG = "showCsjVideo";
        //step3:创建TTAdNative对象,用于调用广告请求接口
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                //此次加载广告的用途是实时加载，当用来作为缓存时，请使用：TTAdLoadType.PRELOAD
                .setAdLoadType(TTAdLoadType.LOAD)
                .build();

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Logger.error(TAG, "Callback --> onError: " + code + ", " + message);
                showCsjVideoError(code, message, activity, listener);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Logger.error(TAG, "Callback --> onRewardVideoCached");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ad) {
                Logger.error(TAG, "Callback --> onRewardVideoCached");
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Logger.error(TAG, "Callback --> onRewardVideoAdLoad");

                TTRewardVideoAd mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        // 广告的展示回调
                        Logger.error(TAG, "Callback --> rewardVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        // 广告下载bar点击回调
                        Logger.error(TAG, "Callback --> rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        // 广告关闭回调
                        Logger.error(TAG, "Callback --> rewardVideoAd close");
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        // 视频播放完成回调
                        Logger.error(TAG, "Callback --> rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        // 视频播放异常回调
                        Logger.error(TAG, "Callback -->  rewardPlayAgain error");
                        showCsjVideoError(-100, "视频播放异常，请重试", activity, listener);
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        Logger.error(TAG, "Callback --> " + logString);
                        if (rewardVerify) {
                            // 领取奖励
                            listener.onReceiveAward();
                        } else {
                            showCsjVideoError(errorCode, errorMsg, activity, listener);
                        }
                    }

                    @Override
                    public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                        Logger.error(TAG, "Callback --> rewardVideoAd has onRewardArrived " +
                                "\n奖励是否有效：" + isRewardValid +
                                "\n奖励类型：" + rewardType);
                    }

                    @Override
                    public void onSkippedVideo() {
                        // 跳过视频播放回调
                        Logger.error(TAG, "Callback --> rewardVideoAd has onSkippedVideo");
                    }
                });

                if (mttRewardVideoAd != null) {
                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                    //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

                    //展示广告，并传入广告展示的场景
                    mttRewardVideoAd.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                    mttRewardVideoAd = null;
                }
            }
        });
    }

    private void showCsjVideoError(int code, String message, Activity activity, AdRewardVideoListener listener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            listener.onError(-100, "Activity已关闭");
            return;
        }
        if (AppConfig.getShipingType().equals("csj")) {
            String id_String = AppConfig.configBean.ad_shiping_idMap.get("gdt");
            if (!TextUtils.isEmpty(id_String) && id_String.split(",").length == 2) {
                String[] a = id_String.split(",");
                String appid = a[0];
                String adplaceid = a[1];
                showGdtVideoAd(activity, appid, adplaceid, listener);
            } else {
                listener.onError(code, message);
            }
        } else {
            listener.onError(code, message);
        }
    }

    private RewardVideoAD rewardVideoAD;

    /**
     * 激励视频广告
     *
     * @param activity
     * @param appid
     * @param adplaceid
     */
    private void showGdtVideoAd(final Activity activity, String appid, String adplaceid, final AdRewardVideoListener listener) {
        if (rewardVideoAD == null || rewardVideoAD.hasShown()) {
            rewardVideoAD = new RewardVideoAD(activity.getApplicationContext(), adplaceid, new RewardVideoADListener() {
                @Override
                public void onADLoad() {
                    boolean isShow = false;
                    // 3. 展示激励视频广告
                    if (rewardVideoAD != null) {//广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
                        if (!rewardVideoAD.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
                            long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
                            //广告展示检查3：展示广告前判断广告数据未过期
//                            if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
                            isShow = true;
                            rewardVideoAD.showAD(activity);
//                            }
//                        else {
//                            Toast.makeText(this, "激励视频广告已过期，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG).show();
//                        }
//                            rewardVideoAD.showAD(activity);
                        } else {
//                        Toast.makeText(this, "此条广告已经展示过，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (!isShow) {
                        showGdtVideoError(-100, "显示广点通视频失败", activity, listener);
                    }

                }

                @Override
                public void onVideoCached() {

                }

                @Override
                public void onADShow() {

                }

                @Override
                public void onADExpose() {
                    Log.e("ADControl", "showRewardVideoAd onADExpose");
                }

                @Override
                public void onReward(Map<String, Object> map) {
                    if (listener != null)
                        listener.onReceiveAward();
                }

                @Override
                public void onADClick() {

                }

                @Override
                public void onVideoComplete() {

                }

                @Override
                public void onADClose() {

                }

                @Override
                public void onError(AdError adError) {
                    rewardVideoAD = null;
                    showGdtVideoError(adError.getErrorCode(), adError.getErrorMsg(), activity, listener);
                }
            }, true);
            // 2. 加载激励视频广告
            rewardVideoAD.loadAD();
        } else {
            long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
            //广告展示检查3：展示广告前判断广告数据未过期
//            if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
            rewardVideoAD.showAD(activity);
//            } else {
//                if (kpAdListener != null)
//                    kpAdListener.onAdFailed("激励视频播放失败");
//                rewardVideoAD = null;
//            }
        }
    }

    private void showGdtVideoError(int errorCode, String errorMes, Activity activity, AdRewardVideoListener listener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            listener.onError(-100, "Activity已关闭");
            return;
        }
        if (AppConfig.getShipingType().equals("gdt")) {
            String id_String = AppConfig.configBean.ad_shiping_idMap.get("csj");
            if (!TextUtils.isEmpty(id_String) && id_String.split(",").length == 2) {
                String[] a = id_String.split(",");
                String appid = a[0];
                String adplaceid = a[1];
                showCsjVideo(activity, appid, adplaceid, listener);
            } else {
                listener.onError(errorCode, errorMes);
            }
        } else {
            listener.onError(errorCode, errorMes);
        }
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
            public void onSplashLoadSuccess(CSJSplashAd csjSplashAd) {

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

    //初始化穿山甲广告
    public static Boolean InitCSJPosAd(Activity context) {
        if (AppConfig.isShowPosAD())//展示退屏广告
        {
            String kpType = AppConfig.getPOSType();//获取广告类型，baidu，gdt，google,csj
            String kp_String = AppConfig.configBean.ad_pos_idMap.get(kpType);

//            String kpType = "csj";
//            String kp_String = "5224187,946870774";
            if (!TextUtils.isEmpty(kp_String)) {
                String[] a = kp_String.split(",");
                if (a.length == 2) {
                    String appid = a[0];
                    String adplaceid = a[1];
                    if ("csj".equals(kpType)) {
                        ADManager.getInsatance().initPosAD(context, appid, adplaceid);
                        return true;
                    } else if ("csj2".equals(kpType)) {
                        ADManager.getInsatance().initPosAD2(context, appid, adplaceid);
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
                    } else {
                        showSelfCpControl(context);
                    }
                } else {
                    showSelfCpControl(context);
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
                    } else {
                        showSelfCpControl(context);
                    }
                } else {
                    showSelfCpControl(context);
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

    /**
     * 控制是否显示自身插屏
     * @param context
     */
    private void showSelfCpControl(Context context){
        if (!AppConfig.isShowSelfAd) return;
        ShowSelfCP(context);
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
                    } else if ("gdt2".equals(cpType)) {
                        ShowGDTCP2(context, appid, adplaceid);
                    } else if ("self".equals(cpType)) {
                        ShowSelfCP(context);
                    } else {
                        // kpAdListener.onAdFailed("其他不支持广告类型" + kp_String);
                    }
                }
            } else {
                showSelfCpControl(context);
            }
        }
    }

    public void addCSJBanner(final LinearLayout lyt, final Activity context, final String appid, final String adplaceid) {
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
                    .setExpressViewAcceptedSize(ScreenUtils.getScreenWidth(context), ScreenUtils.dp2px(context, 60)) //期望模板广告view的size,单位dp
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
                        } else {
                            addSelfBannerControl(lyt, context);
                        }
                    } else {
                        addSelfBannerControl(lyt, context);
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
                        } else {
                            addSelfBannerControl(lyt, context);
                        }
                    } else {
                        addSelfBannerControl(lyt, context);
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

    public void addSelfBannerControl(LinearLayout lyt, Activity context){
        if (!AppConfig.isShowSelfAd) return;
        addSelfBanner(lyt, context);
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
    private TTNativeExpressAd mPostAd;

    public void addCSJPosAd(final LinearLayout lyt, Activity context) {
        if (!AppConfig.isShowPosAD()) {
            return;
        }

        if (mPostAd != null) {
            mPostAd.destroy();
            mPostAd = null;
        }
        if (!ADManager.getInsatance().getCsjADList().isEmpty()) {
            mPostAd = ADManager.getInsatance().getCsjADList().remove(0);
            if (ADManager.getInsatance().getCsjADList().size() <= 0) {
                ADControl.InitCSJPosAd(context);
            }
        } else {
            ADControl.InitCSJPosAd(context);
        }

        if (mPostAd == null) {
            return;
        }
        mPostAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
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
        mPostAd.render();

        //使用默认模板中默认dislike弹出样式
        mPostAd.setDislikeCallback(context, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                if (lyt != null) {
                    lyt.removeAllViews();
                }
                //用户选择不喜欢原因后，移除广告展示
                AppConfig.isCanShowBanner = false;
            }

            @Override
            public void onCancel() {
            }

        });
    }

    /**
     * 显示广告
     *
     * @param lyt     banner广告位置
     * @param context context
     */
    public void showAd(LinearLayout lyt, Activity context) {
        if (ADManager.getInsatance().getCsjADList().isEmpty() || System.currentTimeMillis() - ADManager.initTime > 45 * 60 * 1000) {
            InitCSJPosAd(context);
        }
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
                addSelfBannerControl(lyt, context);
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

        new DialogTextViewBuilderAD.Builder(context, "评论建议", "若对本软件有任何想法或建议，欢迎大家到评论区留言，我们会根据大家的意见进行改进。", "留言")
                .isCancelable()
                .twoButton("以后再说")
                .listener(new DialogTextViewBuilderAD.DialogOnClickListener(){
                    @Override
                    public void oneClick() {
                        setISGiveHaoping(context, true);
                        goodPinglun(context);
                        isonshow = false;
                    }

                    @Override
                    public void twoClick() {
                        isonshow = false;
                    }
                }).build(false);
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
        if (mPostAd != null) {
            mPostAd.destroy();
            mPostAd = null;
        }
    }


}
