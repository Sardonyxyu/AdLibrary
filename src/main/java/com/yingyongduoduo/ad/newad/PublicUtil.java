package com.yingyongduoduo.ad.newad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shanlin on 2017/10/12.
 */
public class PublicUtil {

    public static String metadata(Context context, String key) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            String value = appInfo.metaData.getString(key);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int metadataInt(Context context, String key) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            int value = appInfo.metaData.getInt(key);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getAppName(Context context) {
        PackageManager packageManagers = context.getPackageManager();
        try {
            String appName = (String) packageManagers.getApplicationLabel(packageManagers.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA));
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAppPackage(Context context) {
        return context.getPackageName();
    }


    public static String readAssets(Context context, String filePath) {
        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = context.getAssets().open(filePath);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return buf.toString();
    }

    public static int getVersionCode(Context context) {
        return getAppInfo(context) == null ? 1 : getAppInfo(context).versionCode;
    }

    public static PackageInfo getAppInfo(Context context) {
        try {
            PackageManager packageManagers = context.getPackageManager();
            return packageManagers.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????textview??????
     *
     * @param textView
     */
    public static void setTextViewBold(TextView textView) {
        if (textView == null) {
            return;
        }
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(true);
    }

    /**
     * ??????textview?????????
     *
     * @param textView
     */
    public static void setTextViewNotBold(TextView textView) {
        if (textView == null) {
            return;
        }
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(false);
    }

    /**
     * ????????????????????????????????????
     *
     * @param context
     * @return true ??????  false??????
     */
    public static boolean checkAlertWindowsPermission(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 6.0?????????4.4???????????????????????????????????????
     *
     * @param context
     */
    public static void applyCommonPermission(Context context) {
        try {
            Class clazz = Settings.class;
            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "???????????????????????????" + getAppName(context) + "??????????????????", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param ctx
     * @param path
     */
    public static void dealwithMediaScanIntentData(Context ctx, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(path));
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    /**
     * ???????????????URL??????????????????
     *
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        String domain = "";
        if (!TextUtils.isEmpty(url)) {
            try {
                String host = Uri.parse(url).getHost();
                if (host == null && !url.startsWith("http")) {
                    url = "http://" + url;
                    host = Uri.parse(url).getHost();
                }
                if (host == null && !url.startsWith("http")) {
                    url.replace("http://", "");
                    url = "https://" + url;
                    host = Uri.parse(url).getHost();
                }
                if (!TextUtils.isEmpty(host) && host.contains(".")) {
                    domain = host.substring(host.indexOf(".") + 1, host.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return domain;
    }

    /**
     * ???????????????0????????????
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        try {
            if (s.indexOf(".") > 0) {
                s = s.replaceAll("0+?$", "");//???????????????0
                s = s.replaceAll("[.]$", "");//??????????????????.?????????
            }
            return s;
        } catch (Exception e) {
            return s;
        }
    }

    /**
     * ??????SSID(9.0???9.0????????????)
     *
     * @param activity ?????????
     * @return WIFI ???SSID
     */
    public static String getWIFISSID(Activity activity) {
        String ssid = "unknown id";

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {

            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {

            ConnectivityManager connManager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    return networkInfo.getExtraInfo().replace("\"", "");
                }
            }
        }
        return ssid;
    }


    /**
     * ?????????????????????
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
//        int n = 0;
//        for (int i = 0; i < str.length(); i++) {
//            n = (int) str.charAt(i);
//            if (!(19968 <= n && n < 40869)) {
//                return false;
//            }
//        }
//        return true;

        Pattern p = Pattern.compile("[\u4E00-\u9FFF]");
        Matcher m = p.matcher(str);
        m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ?????????????????????????????????999?????????????????????
     *
     * @param infos
     * @return
     */
    public static <T> List<List<T>> getBatchesList(List<T> infos) {
        List<List<T>> batchesList = new ArrayList<>();
        int max = 400; // ?????????????????????
        for (int i = 1; i <= infos.size() / max; i++) {
            int sum = i * max;
            List<T> downloadInfos = new ArrayList<>(infos.subList(sum - max, sum));
            batchesList.add(downloadInfos);
        }
        if (infos.size() - batchesList.size() * 400 != 0) {
            batchesList.add(new ArrayList<>(infos.subList((batchesList.size() * 400), infos.size())));
        }
        return batchesList;
    }

    /**
     * ?????????????????????(??????????????????????????????)
     *
     * @param txt
     * @return
     */
    public static boolean isHanzi(String txt) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(txt);
        m = p.matcher(txt);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     * @param ctx
     */
    public static void notWxPayCallbackHint(Context ctx){
        try {
            Class toolClass = Class.forName(ctx.getPackageName() + ".wxapi.WXPayEntryActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            new AlertDialog.Builder(ctx).setMessage("???????????????????????? WXPayEntryActivity").show();
        }
    }

    /**
     * ???????????????????????????
     * @param context
     * @return
     */
    public static boolean isAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * ????????????????????????
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// ??????packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// ???????????????????????????????????????
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ??????byte????????????
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        if (byte_1 == null || byte_1.length == 0){
            return byte_2;
        }
        if (byte_2 == null|| byte_2.length == 0){
            return  byte_1;
        }
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * ?????????????????????????????????????????????
     * @param str
     * @param regEx
     * @return
     */
    public static String[] getStringList(String str, String regEx){
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        /*?????????????????????????????????*/
        String[] words = p.split(str);

        /*?????????????????????????????????????????????*/
        if(words.length > 0){
            int count = 0;
            while(count < words.length){
                if(m.find()){
                    words[count] += m.group();
                }
                count++;
            }
        }
        return words;
    }

    /**
     * ??????????????????????????????
     * @param str
     * @return
     */
    public static List<String> stringTransitionStringArray(String str){
        List<String> mList = new ArrayList<>();
        if (str == null) return mList;
        char [] chars = str.toCharArray();
        for (char c : chars){
            mList.add(String.valueOf(c));
        }
        return mList;
    }

    /**
     * ??????????????????
     *
     * @param bitmap
     *            ???
     * @param dst_w
     *            ????????????
     * @param dst_h
     *            ????????????
     * @return
     */
    public static Bitmap changeBitmapSize(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,true);
        return dstbmp;
    }

    /**
     * ??????????????????????????????
     * @param iDouble
     * @return
     */
    public static String latLongitudeTransition(double iDouble){
        String str = "";
        try {
            int i = (int) iDouble;
            str += i + "??";
            //??????textfield3?????????????????????
            double j1 = iDouble - i;
            Double j2 = j1 * 60;
            int j3 = j2.intValue();
            str += j3 + "???";
            //??????textField4
            double k1 = j2 - j3;
            Double k2 = k1 * 60;
            str += Math.round(k2) + "???";
        } catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }

    /**
     * ??????JSONObject?????????????????????????????????????????????""?????????
     *
     * @param object
     * @param key
     * @return
     */
    public static String getJSONObjectString(JSONObject object, String key) {
        try {
            if (object == null || key == null) {
                return "";
            }
            if (object.has(key)) {
                return object.getString(key);
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * ??????JSONObject??????????????????????????????????????????0?????????
     *
     * @param object
     * @param key
     * @return
     */
    public static int getJSONObjectInt(JSONObject object, String key) {
        try {
            if (object == null || key == null) {
                return 0;
            }
            if (object.has(key)) {
                return object.getInt(key);
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     *  ???????????????????????????????
     * @param v
     * @param scale
     * @return
     */
    public static double round(Double v, int scale) {
        if (scale < 0) {
            return v;
//            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
