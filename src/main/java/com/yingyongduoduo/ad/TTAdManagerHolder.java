package com.yingyongduoduo.ad;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;
import com.bytedance.sdk.openadsdk.mediation.init.MediationPrivacyConfig;
import com.umeng.commonsdk.UMConfigure;
import com.yingyongduoduo.ad.interfaces.CsjAdInitListener;
import com.yingyongduoduo.ad.utils.SpUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

    private static final String TAG = "TTAdManagerHolder";

    private static boolean sInit;
    private static String appId;
    private static boolean isGetOaid;
    private static CsjAdInitListener csjAdInitListener;


    public static TTAdManager get() {
        return TTAdSdk.getAdManager();
    }

    public static void init(final Context context, String appId, CsjAdInitListener csjAdInitListener) {
        TTAdManagerHolder.appId = appId;
        TTAdManagerHolder.csjAdInitListener = csjAdInitListener;
        // 获取友盟的OAID
        getUmengOaid(context);
        doInit(context);
    }

    private static void getUmengOaid(Context context) {
        try {
            if (!isGetOaid) {
                isGetOaid = true;
                UMConfigure.getOaid(context, s -> {
                    SpUtils.put("umeng_oaid", s);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context) {
        if (!sInit) {
            //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
            TTAdSdk.init(context, buildConfig(context));
            //setp1.2：启动SDK
            TTAdSdk.start(new TTAdSdk.Callback() {
                  @Override
                  public void success() {
                      Log.i(TAG, "success: " + TTAdSdk.isSdkReady());
                      if (csjAdInitListener != null) csjAdInitListener.onSucceed();
                  }

                  @Override
                  public void fail(int code, String msg) {
                      Log.i(TAG, "fail:  code = " + code + " msg = " + msg);
                  }
              });
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig(Context context) {
        return new TTAdConfig.Builder()
                .appId(appId)
                .customController(getTTCustomController()) // 隐私合规设置
//                .useMediation(true)//开启聚合功能，默认false
//                .useTextureView(false) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .debug(false) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType() //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
//                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_MOBILE) //允许直接下载的网络状态集合
                .supportMultiProcess(false)//是否支持多进程
                .build();
    }

    //函数返回值表示隐私开关开启状态，未重写函数使用默认值
    private static TTCustomController getTTCustomController(){
        return new TTCustomController() {

            @Override
            public boolean isCanUseLocation() {
                // 是否允许SDK主动使用地理位置信息
                return false;
            }

            @Override
            public boolean isCanUseAndroidId() {
                // 是否获取AndroidId
                return false;
            }

            @Override
            public String getAndroidId() {
                // 返回AndroidId
                return "";
            }

            @Override
            public boolean alist() {
                // 是否获取已安装包名列表
                return false;
            }

            @Override
            public String getDevOaid() {
                // 是否获取Oaid，缓存获取友盟的Oaid
                return (String) SpUtils.get("umeng_oaid", "");
            }

            @Override
            public Map<String, Object> userPrivacyConfig() {
                Map<String, Object> map = new HashMap<>();
                // 控制oaid获取频率，"0"表示关闭，"1"或者其他值表示打开。
                map.put("mcod", "0");
                return map;
            }

            @Override
            public String getMacAddress() {
                // 返回MAC地址
                return "";
            }

            @Override
            public boolean isCanUseWifiState() {
                return super.isCanUseWifiState();
            }

            @Override
            public boolean isCanUseWriteExternal() {
                return super.isCanUseWriteExternal();
            }

            @Override
            public MediationPrivacyConfig getMediationPrivacyConfig() {
                return new MediationPrivacyConfig() {

                    @Override
                    public boolean isLimitPersonalAds() {
                        return super.isLimitPersonalAds();
                    }

                    @Override
                    public boolean isProgrammaticRecommend() {
                        return super.isProgrammaticRecommend();
                    }
                };
            }

            @Override
            public boolean isCanUsePermissionRecordAudio() {
                return super.isCanUsePermissionRecordAudio();
            }
        };
    }
}
