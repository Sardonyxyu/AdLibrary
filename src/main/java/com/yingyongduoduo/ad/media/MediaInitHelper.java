package com.yingyongduoduo.ad.media;

import android.content.Context;
import android.util.Log;
import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.DPSdkConfig;
import com.bytedance.sdk.dp.IDPPrivacyController;
import com.bytedance.sdk.dp.ILiveListener;
import com.yingyongduoduo.ad.BuildConfig;
import com.yingyongduoduo.ad.event.ShortVideoInitSucceedEvent;
import com.yingyongduoduo.ad.newad.PublicUtil;
import de.greenrobot.event.EventBus;

public class MediaInitHelper {

   public static String TAG = "MediaInitHelper";
   public static boolean isDPInited = false;
   public static boolean disableABTest = false;
   public static boolean newUser = false;
   public static int aliveSec = 0;
   public static boolean isTeenMode = false;
   // sdk 内部使用弱引用包装 ILiveListener，声明为成员变量防止被回收，直播初始化
   private static final ILiveListener liveListener = new ILiveListener(){
      @Override
      public void onLiveInitResult(boolean isAvailable) {
         Log.d(TAG, "live init result=" + isAvailable);
//        Bus.getInstance().sendEvent(DPLiveInitEvent(isAvailable))
      }
   };

   public static void init(Context context) {
      try {
         initDp(context);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void initDp(Context context) {
      boolean isXlFont = context.getSharedPreferences("sp_dpsdk", Context.MODE_PRIVATE).getBoolean("sp_key_xl_font", false);
      //1. 初始化，最好放到application.onCreate()执行
      //2. 【重要】如果needInitAppLog=false，请确保AppLog初始化一定要在合作sdk初始化前

      // 如需接入直播功能，构造直播功能参数
//        val liveConfig = LiveServiceImpl.Companion.instance.getLiveConfig() as? LiveConfig

      DPSdkConfig.Builder configBuilder = new DPSdkConfig.Builder()
              .debug(BuildConfig.DEBUG)
              .disableABTest(disableABTest)
              .newUser(newUser)
              .aliveSeconds(aliveSec)
              .needInitAppLog(false)
              .interestType(10)
//            .liveConfig(liveConfig) // 直播功能，可选
              .initListener (new DPSdkConfig.InitListener() {
                 @Override
                 public void onInitComplete(boolean isSuccess, String message) {
                    //注意：1如果您的初始化没有放到application，请确保使用时初始化已经成功
                    //     2如果您的初始化在application
                    //isSuccess=true表示初始化成功
                    //初始化失败，可以再次调用初始化接口（建议最多不要超过3次)
                    isDPInited = isSuccess;
                    EventBus.getDefault().post(new ShortVideoInitSucceedEvent());
                    Log.e(TAG, "init result=" + isSuccess + ", msg=" + message);
//                Bus.getInstance().sendEvent(DPInitEvent(isSuccess))

                    // 设置直播功能初始化监听，可选
                    DPSdk.liveService().registerLiveListener(liveListener);
//                if (RuntimeUtils.containsLuckyCat()) {
//                    DPLuck.callback(DPCallback.instance)
//                    DPLuck.drawListener(DPCallback.instance)
//                    DPLuck.gridListener(DPCallback.instance)
//                    DPLuck.newsListener(DPCallback.instance)
                 }
         })//接入了红包功能需要传入的参数，没有接入的话可以忽略该配置
         .fontStyle(isXlFont ? DPSdkConfig.ArticleDetailListTextStyle.FONT_XL : DPSdkConfig.ArticleDetailListTextStyle.FONT_NORMAL);
      // 去掉
//        configBuilder.luckConfig(DPSdkConfig.LuckConfig().application(application).enableLuck(false))
      DPSdkConfig config = configBuilder.build();

      // 配置青少年模式，可选
      config.setPrivacyController(new IDPPrivacyController() {
         @Override
         public boolean isTeenagerMode() {
            return isTeenMode;
         }
      });
      DPSdk.init(context, PublicUtil.metadata(context, "CSJ_CONFIG"), config);
   }
}
