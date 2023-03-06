package com.yingyongduoduo.ad.utils;

import android.content.Context;
import android.text.TextUtils;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.newad.PublicUtil;

public class MapUtil {

   /**
    * 获取地图审图号
    * @param context
    * @return
    */
   public static String getMapNo(Context context){
      if (!AppConfig.isShowMapNO()) return "";
      String mapNo = PublicUtil.metadata(context, "MAP_NO");
      if (!TextUtils.isEmpty(mapNo)) {
         return mapNo;
      } else {
         return AppConfig.getMapNO();
      }
   }
}
