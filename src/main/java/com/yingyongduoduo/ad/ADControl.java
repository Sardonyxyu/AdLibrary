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

    //??????5????????????????????????????????????????????????????????????????????????10???
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
     * ??????????????????
     * @param activity
     * @param listener
     */
    public void showShiping(final Activity activity, final AdRewardVideoListener listener) {
        if (AppConfig.isShowShiping()) { //??????????????????
            String spType = AppConfig.getShipingType();//???????????????????????????baidu???gdt???google
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
                        listener.onError(-100, "???????????????????????????");
                    }
                }
            }
        }
    }

    /**
     * ???????????????????????????
     * @param activity
     * @param listener
     */
    public void showCsjVideo(final Activity activity, String appid, String codeId, final AdRewardVideoListener listener){

        final String TAG = "showCsjVideo";
        //step3:??????TTAdNative??????,??????????????????????????????
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                //????????????????????????????????????????????????????????????????????????????????????TTAdLoadType.PRELOAD
                .setAdLoadType(TTAdLoadType.LOAD)
                .build();

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Logger.error(TAG, "Callback --> onError: " + code + ", " + message);
                showCsjVideoError(code, message, activity, listener);
            }

            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            @Override
            public void onRewardVideoCached() {
                Logger.error(TAG, "Callback --> onRewardVideoCached");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ad) {
                Logger.error(TAG, "Callback --> onRewardVideoCached");
            }

            //????????????????????????????????????????????????url?????????????????????????????????????????????????????????????????????????????????????????????????????????
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Logger.error(TAG, "Callback --> onRewardVideoAdLoad");

                TTRewardVideoAd mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        // ?????????????????????
                        Logger.error(TAG, "Callback --> rewardVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        // ????????????bar????????????
                        Logger.error(TAG, "Callback --> rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        // ??????????????????
                        Logger.error(TAG, "Callback --> rewardVideoAd close");
                    }

                    //????????????????????????
                    @Override
                    public void onVideoComplete() {
                        // ????????????????????????
                        Logger.error(TAG, "Callback --> rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        // ????????????????????????
                        Logger.error(TAG, "Callback -->  rewardPlayAgain error");
                        showCsjVideoError(-100, "??????????????????????????????", activity, listener);
                    }

                    //?????????????????????????????????????????????rewardVerify??????????????????rewardAmount??????????????????rewardName???????????????
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        Logger.error(TAG, "Callback --> " + logString);
                        if (rewardVerify) {
                            // ????????????
                            listener.onReceiveAward();
                        } else {
                            showCsjVideoError(errorCode, errorMsg, activity, listener);
                        }
                    }

                    @Override
                    public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                        Logger.error(TAG, "Callback --> rewardVideoAd has onRewardArrived " +
                                "\n?????????????????????" + isRewardValid +
                                "\n???????????????" + rewardType);
                    }

                    @Override
                    public void onSkippedVideo() {
                        // ????????????????????????
                        Logger.error(TAG, "Callback --> rewardVideoAd has onSkippedVideo");
                    }
                });

                if (mttRewardVideoAd != null) {
                    //step6:???????????????????????????,???????????????onRewardVideoCached?????????????????????????????????????????????
                    //???????????????????????????
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

                    //?????????????????????????????????????????????
                    mttRewardVideoAd.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                    mttRewardVideoAd = null;
                }
            }
        });
    }

    private void showCsjVideoError(int code, String message, Activity activity, AdRewardVideoListener listener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            listener.onError(-100, "Activity?????????");
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
     * ??????????????????
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
                    // 3. ????????????????????????
                    if (rewardVideoAD != null) {//??????????????????1?????????????????????????????????????????????videoCached?????????????????????????????????????????????????????????????????????
                        if (!rewardVideoAD.hasShown()) {//??????????????????2???????????????????????????????????????
                            long delta = 1000;//?????????????????????????????????buffer?????????ms?????????demo??????1000ms???buffer
                            //??????????????????3?????????????????????????????????????????????
//                            if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
                            isShow = true;
                            rewardVideoAD.showAD(activity);
//                            }
//                        else {
//                            Toast.makeText(this, "???????????????????????????????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
//                        }
//                            rewardVideoAD.showAD(activity);
                        } else {
//                        Toast.makeText(this, "???????????????????????????????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (!isShow) {
                        showGdtVideoError(-100, "???????????????????????????", activity, listener);
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
            // 2. ????????????????????????
            rewardVideoAD.loadAD();
        } else {
            long delta = 1000;//?????????????????????????????????buffer?????????ms?????????demo??????1000ms???buffer
            //??????????????????3?????????????????????????????????????????????
//            if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
            rewardVideoAD.showAD(activity);
//            } else {
//                if (kpAdListener != null)
//                    kpAdListener.onAdFailed("????????????????????????");
//                rewardVideoAD = null;
//            }
        }
    }

    private void showGdtVideoError(int errorCode, String errorMes, Activity activity, AdRewardVideoListener listener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            listener.onError(-100, "Activity?????????");
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
        //??????TTAdNative?????????createAdNative(Context context) context????????????Activity??????
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adplaceid)
                .setImageAcceptedSize(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context) - ScreenUtils.getVirtualBarHeigh(context))
                .setAdLoadType(PRELOAD)//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP) // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                .build();

        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.CSJSplashAdListener() {
            @Override
            public void onSplashLoadSuccess() {

            }

            @Override
            public void onSplashLoadFail(CSJAdError csjAdError) {
                //????????????????????????APP???????????????
//                kpAdListener.onAdFailed(csjAdError.getMsg() + "");
//                csjAdError.getCode() == 23?????????
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
                    //???SplashView ?????????ViewGroup???,??????????????????view???width =????????????height >=75%?????????
                    ad.showSplashView(adsParent);
                    //?????????????????????????????????????????????????????????????????????,??????????????????????????????????????????????????????
                    //ad.setNotAllowSdkCountdown();
                } else {
                    //????????????????????????APP???????????????
                    kpAdListener.onAdDismissed();
                }

                //??????SplashView??????????????????
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
                //????????????????????????APP???????????????
//                kpAdListener.onAdFailed(csjAdError.getMsg() + "");
//                csjAdError.getCode() == 23?????????
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
            public void onAdDismissed(ADBean bean) {//??????????????????
                kpAdListener.onAdDismissed();
            }

            @Override
            public void onAdFailed(ADBean bean) {//??????????????????
                kpAdListener.onAdFailed("");
            }

            @Override
            public void onAdPresent(ADBean bean) {//??????????????????
                kpAdListener.onAdPresent();
//                if (bean != null && !TextUtils.isEmpty(bean.getAd_name())) {
//                    Map<String, String> map_ekv = new HashMap<String, String>();
//                    map_ekv.put("show", bean.getAd_name());
//                }
            }

            @Override
            public void onAdClick(ADBean bean) {//???????????????
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

    //??????????????????????????????
    public static Boolean InitGDTMuBanTP(Context context) {
        if (AppConfig.isShowTP())//??????????????????
        {
            String kpType = AppConfig.getTPType();//???????????????????????????baidu???gdt???google
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
        } else//?????????????????????
        {
            return false;
        }

    }

    //??????????????????????????????
    public Boolean ShowTPAD(Context context) {
        if (AppConfig.isShowTP())//??????????????????
        {
            String kpType = AppConfig.getTPType();//???????????????????????????baidu???gdt???google
            String kp_String = AppConfig.configBean.ad_tp_idMap.get(kpType);
            if (!TextUtils.isEmpty(kpType) && "self".equals(kpType)) {//????????????????????????
                SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context);
                sfCP.show();
                return false;
            } else if (!TextUtils.isEmpty(kp_String)) {//?????????????????????????????????????????????????????????????????????
                String[] a = kp_String.split(",");
                if (a.length == 2) {
                    String appid = a[0];
                    String adplaceid = a[1];
                    if ("gdtmb".equals(kpType)) {
                        GDTMuBanTuiPingDialog sfCP = new GDTMuBanTuiPingDialog(context);
                        sfCP.show();
                        return true;
                    } else {//?????????id???????????????????????????
                        SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context, null);
                        sfCP.show();
                        return false;
                    }
                } else {//id????????????????????????????????????????????????????????????????????????id????????????????????????????????????
                    SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context, null);
                    sfCP.show();
                    return false;
                }
            } else {//???????????????id????????????????????????????????????????????????????????????????????????????????????????????????
                SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context, null);
                sfCP.show();
                return true;
            }
        } else {//?????????????????????
            SelfTuiPingDialog sfCP = new SelfTuiPingDialog(context, null);
            sfCP.show();
            return false;
        }

    }

    public void showKp(Activity context, RelativeLayout adsParent, View skipView, final ADListener kpAdListener) {
        if (AppConfig.isShowKP()) {//??????????????????
            String kpType = AppConfig.getKPType();//???????????????????????????baidu???gdt???google
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
                        kpAdListener.onAdFailed("???????????????????????????" + kp_String);
                    }
                }
            } else {
                kpAdListener.onAdFailed("???????????????????????????");
            }
        } else {//?????????????????????
            kpAdListener.onAdFailed("???????????????????????????");
        }

    }

    private UnifiedInterstitialAD interAd;
    private TTNativeExpressAd mInterAd;

    private void showCSJNewCp(final Activity context, final String appid, final String adplaceid) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adplaceid)
                //????????????????????????????????????????????????????????????,??????dp,?????????????????????????????????????????????0??????
                .setExpressViewAcceptedSize(500,500)
                .setSupportDeepLink(true)
                .setOrientation(TTAdConstant.VERTICAL)//?????????????????????????????????????????????TTAdConstant.HORIZONTAL ??? TTAdConstant.VERTICAL
                .setAdLoadType(PRELOAD)//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                .build();


        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            //??????????????????
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

            //?????????????????????????????????
            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {

                // ?????????????????????
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

            //????????????/???????????????????????????????????????????????????????????????????????????
            @Override
            public void onFullScreenVideoCached() {

            }

            @Override
            public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {
                if (ttFullScreenVideoAd != null) {
                    //?????????????????????????????????????????????
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
     * ??????????????????????????????
     * @param context
     */
    private void showSelfCpControl(Context context){
        if (AppConfig.isCommonServer) return;
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
        if (AppConfig.isShowCP()) {//??????????????????
            if (System.currentTimeMillis() - lastshowadTime < showadTimeDuration) {
                System.out.println("??????????????????" + (System.currentTimeMillis() - lastshowadTime));
                return;
            }
            lastshowadTime = System.currentTimeMillis();
            showCpContent(context);
        }
    }

    public void showCpContent(Activity context) {
        if (AppConfig.isShowCP()) {
            String cpType = AppConfig.getCPType();//???????????????????????????baidu???gdt???google
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
                        // kpAdListener.onAdFailed("???????????????????????????" + kp_String);
                    }
                }
            } else {
                showSelfCpControl(context);
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

            //step4:????????????????????????AdSlot,??????????????????????????????
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(adplaceid) //?????????id

                    .setAdCount(1) //?????????????????????1???3???
                    .setExpressViewAcceptedSize(ScreenUtils.getScreenWidth(context), 60) //??????????????????view???size,??????dp
//                    .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP) // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    .build();
            //step5:??????????????????????????????????????????????????????
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
                            //??????view????????? ?????? dp
                            if (lyt != null) {
                                lyt.removeAllViews();
                                lyt.addView(view);
                            }
                        }
                    });
                    mTTAd.render();

                    //???????????????????????????dislike????????????
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
                    System.out.println("????????????????????????");
                }

                @Override
                public void onADLeftApplication() {

                }

            });
            lyt.addView(unifiedBannerView, getUnifiedBannerLayoutParams(context));
            // ???????????????????????????banner????????????????????????????????????????????????????????????????????????????????????????????????
            unifiedBannerView.loadAD();

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    /**
     * banner2.0??????banner??????????????????6.4:1 , ????????????????????????????????????????????????????????????????????????
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
        if (AppConfig.isCommonServer) return;
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
//                        System.out.println("???????????????:"+adBean.getAd_name());
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
     * ????????????
     *
     * @param lyt     banner????????????
     * @param context context
     */
    public void showAd(LinearLayout lyt, Activity context) {
        ShowCp(context);
        homeGet5Score(context);
        if (AppConfig.isShowBanner() && lyt != null) {//?????????????????????
            String bannerType = AppConfig.getBannerType();//???????????????????????????baidu???gdt???google
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
//                        kpAdListener.onAdFailed("???????????????????????????" + kp_String);
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
            Log.i("??????????????????", (System.currentTimeMillis() - lastshowHaopingTime) + "");
            return;
        }

        lastshowHaopingTime = System.currentTimeMillis();
        isonshow = true;

        new DialogTextViewBuilderAD.Builder(context, "????????????", "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????", "??????")
                .isCancelable()
                .twoButton("????????????")
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
     * ??????QQ????????????
     * */
    public boolean joinQQGroup(Context context) {
        try {
            String key = AppConfig.publicConfigBean.qqKey;
            if (key == null || "".equals(key)) {
                Toast.makeText(context, "???????????????QQ???:286239217", Toast.LENGTH_SHORT).show();
                return false;
            }
            Intent intent = new Intent();
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
            // ???Flag??????????????????????????????????????????????????????????????????????????????????????????Q???????????????????????????????????????????????????????????????    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "???????????????QQ???:286239217", Toast.LENGTH_SHORT).show();
            // ????????????Q???????????????????????????
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
            Toast.makeText(activity, "??????", Toast.LENGTH_SHORT).show();
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
