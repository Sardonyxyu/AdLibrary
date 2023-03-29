package com.yingyongduoduo.ad.utils;

import android.content.Context;
import android.text.TextUtils;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.newad.PublicUtil;

public class MapUtil {

   /**
    * 默认显示的审图号
    */
   private static final String defaultMapNo = "©2023 北京百度网讯科技有限公司 - GS(2021)6026号 - 甲测资字11111342";


   /**
    * 获取地图审图号
    * @param context
    * @return
    */
   public static String getMapNo(Context context){
      if (!AppConfig.isShowMapNO()) return "";
      String onLineMapNo = AppConfig.getOnLineMapNo();
      String mapNo = PublicUtil.metadata(context, "MAP_NO");
      if (!TextUtils.isEmpty(onLineMapNo)) {
         return onLineMapNo;
      } else if (!TextUtils.isEmpty(mapNo)) {
         return mapNo;
      } else {
         return defaultMapNo;
      }
   }
}
