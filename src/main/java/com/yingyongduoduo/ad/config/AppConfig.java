package com.yingyongduoduo.ad.config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.qq.e.comm.managers.GDTAdSdk;
import com.yingyongduoduo.ad.TTAdManagerHolder;
import com.yingyongduoduo.ad.bean.ADBean;
import com.yingyongduoduo.ad.bean.ConfigBean;
import com.yingyongduoduo.ad.bean.PublicConfigBean;
import com.yingyongduoduo.ad.bean.VideoBean;
import com.yingyongduoduo.ad.bean.WXGZHBean;
import com.yingyongduoduo.ad.bean.ZiXunItemBean;
import com.yingyongduoduo.ad.bean.ZiXunListItemBean;
import com.yingyongduoduo.ad.newad.PublicUtil;
import com.yingyongduoduo.ad.utils.DownLoaderAPK;
import com.yingyongduoduo.ad.utils.HttpUtil;
import com.yingyongduoduo.ad.utils.PackageUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * 保存软件的配置信息
 * Created by yuminer on 2017/3/16.
 */
public class AppConfig {

    /**
     * index.html主页路径，从后台下载到手机本地的index.html 路径
     */
    public static String INDEX_HTML_LOCAL_PATH;
    /**
     * index.html本地路径：前缀加了file://，直接提供给webView使用
     */
    public static String URL_INDEX_HTML;
    /**
     * start.html本地路径
     */
    public static String START_HTML_LOCAL_PATH;
    /**
     * start.html本地路径：前缀加了file://，直接提供给webView使用
     */
    public static String URL_START_HTML;

    public static String youkulibPath;
    public static String GZHPath;

    public static String versioncode = "";
    public static String Channel = "";
    public static String APPKEY = "";

    private final static String baseURLGetAdconfig = "https://api.csdtkj.cn/xly/webcloud/jsonadconfig/getadconfig?application=%s&apppackage=%s&appversion=%s&appmarket=%s&agencychannel=%s";
    private final static String baseURLGetPublicconfig = "https://api.csdtkj.cn/xly/webcloud/jsonadconfig/getpublic?application=%s&apppackage=%s&appversion=%s&appmarket=%s&agencychannel=%s";
    private final static String baseURLGetSelfad = "https://api.csdtkj.cn/xly/webcloud/jsonadconfig/getselfad?application=%s&apppackage=%s&appversion=%s&appmarket=%s&agencychannel=%s";
    private final static String baseURLGetZixun = "https://api.csdtkj.cn/xly/webcloud/jsonadconfig/getzixun?application=%s&apppackage=%s&appversion=%s&appmarket=%s&agencychannel=%s";

    public static ConfigBean configBean;
    public static PublicConfigBean publicConfigBean;
    public static List<VideoBean> videoBeans = new ArrayList<VideoBean>();
    public static List<ADBean> selfadBeans = new ArrayList<ADBean>();
    public static List<ZiXunItemBean> ziXunBeans = new ArrayList<>();
    public static List<WXGZHBean> wxgzhBeans = new ArrayList<WXGZHBean>();

    /**
     * 联网初始化广告配置
     * 在启动页进行初始化
     *
     * @param context
     */
    public static void Init(Context context) {
        initConfigJson(context);
        initPublicConfigJson(context);
        initselfadJson(context);
        initzixunJson(context);

        initADManager(context);
    }

    private static void initADManager(Context context) {
        boolean isHasAppId = false;
        if (AppConfig.configBean != null && AppConfig.configBean.ad_kp_idMap != null) {
            Set<Map.Entry<String, String>> entries = AppConfig.configBean.ad_kp_idMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String adType = entry.getKey();
                String kp_String = entry.getValue();
                if (!TextUtils.isEmpty(adType) && !TextUtils.isEmpty(kp_String)) {
                    String[] a = kp_String.split(",");
                    if (a.length == 2) {
                        String appid = a[0];
                        if (!TextUtils.isEmpty(appid)) {
                            if ("csj".equals(adType)) {
                                TTAdManagerHolder.init(context.getApplicationContext(), appid);
                            } else if ("gdt".equals(adType)) {
                                GDTAdSdk.init(context.getApplicationContext(), appid);
                            }
                            isHasAppId = true;
                        }
                    }
                }
            }
        }

        if (!isHasAppId) {
            Log.e("AppConfig", "获取广告APP_ID 为Null，请检查广告相关配置");
        }
    }

    /**
     * 初始化本地参数配置
     * 在Application 进行初始化
     *
     * @param context
     */
    public static void initLocalConfig(Context context) {
        ApplicationInfo appInfo;
        try {
            AppConfig.isCanShowBanner = true;
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            AppConfig.versioncode = GetVersionCode(context);
            AppConfig.APPKEY = appInfo.metaData.getString("UMENG_APPKEY");
            AppConfig.Channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        AppConfig.INDEX_HTML_LOCAL_PATH = context.getCacheDir() + File.separator + "index.html";// 浏览器主页HTML存放位置
        AppConfig.START_HTML_LOCAL_PATH = context.getCacheDir() + File.separator + "start.html";// 浏览器主页HTML存放位置
        AppConfig.URL_INDEX_HTML = String.format("%s" + AppConfig.INDEX_HTML_LOCAL_PATH, "file://");
        AppConfig.URL_START_HTML = String.format("%s" + AppConfig.START_HTML_LOCAL_PATH, "file://");
        AppConfig.youkulibPath = context.getCacheDir() + File.separator + "videoparse.jar";// 初始化引擎存放位置
        AppConfig.GZHPath = context.getCacheDir() + "/tv1/gps_test/gzh/";// 公众号的目录不能用缓存目录
        InitLocal(context);
    }

    private static String GetVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(info.versionCode); //获取版本cood
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void InitLocal(Context context) {

        initConfigBean(context);
        initPublicConfigBean(context);
//        initVideoBean(context);
        initselfadBeans(context);
        initZixunBeans(context);
//        initwxgzhBeans(context);
        //initvideosourceVersion(context);这个没有必要初始化

        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String appId = mSettings.getString("app_id", "");
        if (!TextUtils.isEmpty(appId)) {
            TTAdManagerHolder.init(context.getApplicationContext(), appId);
            GDTAdSdk.init(context.getApplicationContext(), appId);
        }
    }

    public static ConfigBean getConfigBean(String configJson) {
        ConfigBean bean = new ConfigBean();

        try {
            final JSONObject jo = new JSONObject(configJson);
            if (haveKey(jo, "updatemsg")) {
                JSONObject jo_ad_banner_id = jo.getJSONObject("updatemsg");
                bean.updatemsg.msg = jo_ad_banner_id.optString("msg");
                bean.updatemsg.versioncode = jo_ad_banner_id.optInt("versioncode");
                bean.updatemsg.url = jo_ad_banner_id.optString("url");
                bean.updatemsg.packageName = jo_ad_banner_id.optString("packageName");
            }
            if (haveKey(jo, "ad_banner_id")) {
                JSONObject jo_ad_banner_id = jo.getJSONObject("ad_banner_id");
                Iterator<String> keys = jo_ad_banner_id.keys();
                while (keys.hasNext()) { // 只要一个
                    String key = keys.next();
                    bean.ad_banner_idMap.put(key, jo_ad_banner_id.getString(key));
                }
            }
            if (haveKey(jo, "ad_kp_id")) {
                JSONObject jo_ad_kp_id = jo.getJSONObject("ad_kp_id");
                Iterator<String> keys = jo_ad_kp_id.keys();
                while (keys.hasNext()) { // 只要一个
                    String key = keys.next();
                    bean.ad_kp_idMap.put(key, jo_ad_kp_id.getString(key));
                }
            }
            if (haveKey(jo, "ad_cp_id")) {
                JSONObject jo_ad_banner_id = jo.getJSONObject("ad_cp_id");
                Iterator<String> keys = jo_ad_banner_id.keys();
                while (keys.hasNext()) { // 只要一个
                    String key = keys.next();
                    bean.ad_cp_idMap.put(key, jo_ad_banner_id.getString(key));
                }
            }
            if (haveKey(jo, "ad_tp_id")) {
                JSONObject jo_ad_banner_id = jo.getJSONObject("ad_tp_id");
                Iterator<String> keys = jo_ad_banner_id.keys();
                while (keys.hasNext()) { // 只要一个
                    String key = keys.next();
                    bean.ad_tp_idMap.put(key, jo_ad_banner_id.getString(key));
                }
            }
            if (haveKey(jo, "cpuidorurl")) {
                bean.cpuidorurl = jo.getString("cpuidorurl");
            }

            if (haveKey(jo, "channel")) {
                JSONObject jo_channel = jo.getJSONObject("channel");
                if (haveKey(jo_channel, Channel)) {
                    JSONObject jo_channelInfo = jo_channel.getJSONObject(Channel);

                    if (haveKey(jo_channelInfo, "nomeinvchannel")) {
                        bean.nomeinvchannel = jo_channelInfo.getString("nomeinvchannel");
                    }
                    if (haveKey(jo_channelInfo, "nocpuadchannel")) {
                        bean.nocpuadchannel = jo_channelInfo.getString("nocpuadchannel");
                    }
                    if (haveKey(jo_channelInfo, "nofenxiang")) {
                        bean.nofenxiang = jo_channelInfo.getString("nofenxiang");
                    }
                    if (haveKey(jo_channelInfo, "nozhikouling")) {
                        bean.nozhikouling = jo_channelInfo.getString("nozhikouling");
                    }
                    if (haveKey(jo_channelInfo, "nosearch")) {
                        bean.nosearch = jo_channelInfo.getString("nosearch");
                    }
                    if (haveKey(jo_channelInfo, "nohaoping")) {
                        bean.nohaoping = jo_channelInfo.getString("nohaoping");
                    }
                    if (haveKey(jo_channelInfo, "noadbannerchannel")) {
                        bean.noadbannerchannel = jo_channelInfo.getString("noadbannerchannel");
                    }
                    if (haveKey(jo_channelInfo, "noadkpchannel")) {
                        bean.noadkpchannel = jo_channelInfo.getString("noadkpchannel");
                    }
                    if (haveKey(jo_channelInfo, "noadtpchannel")) {
                        bean.noadtpchannel = jo_channelInfo.getString("noadtpchannel");
                    }
                    if (haveKey(jo_channelInfo, "noadcpchannel")) {
                        bean.noadcpchannel = jo_channelInfo.getString("noadcpchannel");
                    }
                    if (haveKey(jo_channelInfo, "nopaychannel")) {
                        bean.nopaychannel = jo_channelInfo.getString("nopaychannel");
                    }
                    if (haveKey(jo_channelInfo, "isfirstfreeusechannel")) {
                        bean.isfirstfreeusechannel = jo_channelInfo.getString("isfirstfreeusechannel");
                    }
                    if (haveKey(jo_channelInfo, "showselflogochannel")) {
                        bean.showselflogochannel = jo_channelInfo.getString("showselflogochannel");
                    }
                    if (haveKey(jo_channelInfo, "greythemechannel")) {
                        bean.greythemechannel = jo_channelInfo.getString("greythemechannel");
                    }
                    if (haveKey(jo_channelInfo, "noshowdschannel")) {
                        bean.noshowdschannel = jo_channelInfo.getString("noshowdschannel");
                    }
                    if (haveKey(jo_channelInfo, "noupdatechannel")) {
                        bean.noupdatechannel = jo_channelInfo.getString("noupdatechannel");
                    }
                    if (haveKey(jo_channelInfo, "noaddvideochannel")) {
                        bean.noaddvideochannel = jo_channelInfo.getString("noaddvideochannel");
                    }
                    if (haveKey(jo_channelInfo, "noadVideowebchannel")) {
                        bean.noadVideowebchannel = jo_channelInfo.getString("noadVideowebchannel");
                    }
                    bean.playonwebchannel = jo_channelInfo.optString("playonwebchannel");

                    if (haveKey(jo_channelInfo, "nogzhchannel")) {
                        bean.nogzhchannel = jo_channelInfo.getString("nogzhchannel");
                    }
                    if (haveKey(jo_channelInfo, "bannertype")) {
                        bean.bannertype = jo_channelInfo.getString("bannertype");
                    }
                    if (haveKey(jo_channelInfo, "cptype")) {
                        bean.cptype = jo_channelInfo.getString("cptype");
                    }
                    if (haveKey(jo_channelInfo, "kptype")) {
                        bean.kptype = jo_channelInfo.getString("kptype");
                    }
                    if (haveKey(jo_channelInfo, "tptype")) {
                        bean.tptype = jo_channelInfo.getString("tptype");
                    }
                    if (haveKey(jo_channelInfo, "nomapnochannel")) {
                        bean.nomapnochannel = jo_channelInfo.getString("nomapnochannel");
                    }
                    if (haveKey(jo_channelInfo, "mapnobaidu")) {
                        bean.mapnobaidu = jo_channelInfo.getString("mapnobaidu");
                    }
                    if (haveKey(jo_channelInfo, "mapnogaode")) {
                        bean.mapnogaode = jo_channelInfo.getString("mapnogaode");
                    }

                } else {
                    bean = null;//连channel都没有，这可能是服务器异常
                }
            }
        } catch (Exception e) {
            bean = null;
        }
        return bean;
    }

    public static PublicConfigBean getpublicConfigBean(String configJson) {
        PublicConfigBean bean = new PublicConfigBean();

        try {
            final JSONObject jo = new JSONObject(configJson);

            if (haveKey(jo, "videosourceVersion")) {
                bean.videosourceVersion = jo.getString("videosourceVersion");
            }
            if (haveKey(jo, "selfadVersion")) {
                bean.selfadVersion = jo.getString("selfadVersion");
            }
            if (haveKey(jo, "zixunVersion")) {
                bean.zixunVersion = jo.getString("zixunVersion");
            }
            if (haveKey(jo, "dashangContent")) {
                bean.dashangContent = jo.getString("dashangContent");
            }
            if (haveKey(jo, "wxgzhversion")) {
                bean.wxgzhversion = jo.getString("wxgzhversion");
            }
            if (haveKey(jo, "goodPinglunVersion")) {
                bean.goodPinglunVersion = jo.getString("goodPinglunVersion");
            }
            if (haveKey(jo, "onlineVideoParseVersion")) {
                bean.onlineVideoParseVersion = jo.getString("onlineVideoParseVersion");
            }
            if (haveKey(jo, "baiduCpuId")) {
                bean.baiduCpuId = jo.getString("baiduCpuId");
            }
            if (haveKey(jo, "regionurl")) {
                bean.regionurl = jo.getString("regionurl");
            }
            if (haveKey(jo, "mar3dserver")) {
                bean.mar3dserver = jo.getString("mar3dserver");
            }
            if (haveKey(jo, "mar3ddomain")) {
                bean.mar3ddomain = jo.getString("mar3ddomain");
            }
            if (haveKey(jo, "googlestreetip")) {
                bean.googlestreetip = jo.getString("googlestreetip");
            }
            if (haveKey(jo, "qqKey")) {
                bean.qqKey = jo.getString("qqKey");
            }
            if (haveKey(jo, "Information")) {
                bean.Information = jo.getString("Information");
            }
            if (haveKey(jo, "fenxiangInfo")) {
                bean.fenxiangInfo = jo.getString("fenxiangInfo");
            }
            if (haveKey(jo, "searchbaiduworld")) {
                bean.searchbaiduworld = jo.getString("searchbaiduworld");
            }
            if (haveKey(jo, "zhikouling")) {
                bean.zhikouling = jo.getString("zhikouling");
            }
        } catch (Exception e) {
            bean = null;
        }
        return bean;
    }

    public static List<VideoBean> getVideoBean(String videoJson) {
        List<VideoBean> beans = new ArrayList<VideoBean>();

        try {
            final JSONArray ja = new JSONArray(videoJson);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                VideoBean bean = new VideoBean();
                if (haveKey(jo, "platform") && haveKey(jo, "name") && haveKey(jo, "playonbroswer")) {
                    bean.platform = jo.getString("platform");
                    bean.name = jo.getString("name");
                    bean.playonbroswer = jo.getString("playonbroswer");
                    bean.noadVideowebBaseUrl = jo.getString("noadVideowebBaseUrl");
                    bean.imgUrl = jo.getString("imgUrl");
                    beans.add(bean);
                }
            }

        } catch (Exception e) {
        }
        return beans;
    }

    private static List<ADBean> getSelfAdBeans(String selfadJson) {
        List<ADBean> beans = new ArrayList<ADBean>();

        try {
            final JSONArray ja = new JSONArray(selfadJson);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                ADBean bean = new ADBean();
                bean.setAd_name(jo.optString("name"));
                bean.setAd_description(jo.optString("description"));
                bean.setAd_iconurl(jo.optString("iconurl"));
                bean.setAd_iconscal((float) jo.optDouble("iconscal", bean.getAd_iconscal()));
                bean.setAd_thumbnail(jo.optString("thumbnail"));
                bean.setAd_thumbnailscal((float) jo.optDouble("thumbnailscal", bean.getAd_thumbnailscal()));
                bean.setAd_banner(jo.optString("banner"));
                bean.setAd_kp(jo.optString("kp"));
                bean.setAd_apkurl(jo.optString("apkurl"));
                bean.setAd_packagename(jo.optString("packagename"));
                bean.setAd_isConfirm(jo.optBoolean("isConfirm"));
                bean.setAd_type(jo.optInt("type"));
                bean.setAd_versioncode(jo.optInt("versioncode"));
                bean.setAd_platform("ad");
                beans.add(bean);
//                if (haveKey(jo, "displayName") && haveKey(jo, "secondConfirm") && haveKey(jo, "adtype") && haveKey(jo, "scal") && haveKey(jo, "iconthunb1") && haveKey(jo, "url") && haveKey(jo, "packageName")) {
//                    bean.displayName = jo.getString("displayName");
//                    bean.secondConfirm = jo.getString("secondConfirm");
//                    bean.adtype = jo.getString("adtype");
//                    bean.scal = (float) jo.getDouble("scal");
//                    bean.iconthunb1 = jo.getString("iconthunb1");
//                    bean.url = jo.getString("url");
//                    bean.packageName = jo.getString("packageName");
//                    beans.add(bean);
//                }
            }

        } catch (Exception e) {
        }
        return beans;
    }

    private static List<ZiXunItemBean> getZiXunBeans(String zixunJson) {
        List<ZiXunItemBean> beans = new ArrayList<>();

        try {
            final JSONArray ja = new JSONArray(zixunJson);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                JSONObject bottomTab = jo.optJSONObject("bottomTab");
                ZiXunItemBean ziXunItemBean = new ZiXunItemBean();
                ziXunItemBean.setTabName(bottomTab.optString("tabName"));
                ziXunItemBean.setIcon(bottomTab.optString("icon"));
                ziXunItemBean.setSelIcon(bottomTab.optString("selIcon"));
                JSONArray list = bottomTab.optJSONArray("list");
                if (list != null) {
                    List<ZiXunListItemBean> ziXunListItemBeans = new ArrayList<>();
                    for (int l = 0; l < list.length(); l++) {
                        JSONObject jsonObject = list.getJSONObject(l);
                        ZiXunListItemBean ziXunListItemBean = new ZiXunListItemBean();
                        ziXunListItemBean.setName(jsonObject.optString("name"));
                        ziXunListItemBean.setUrl(jsonObject.optString("url"));
                        ziXunListItemBeans.add(ziXunListItemBean);
                    }
                    ziXunItemBean.setList(ziXunListItemBeans);
                }

                beans.add(ziXunItemBean);
            }

        } catch (Exception e) {
        }
        return beans;
    }

    public static List<WXGZHBean> getWXGZHBeans(String wxgzhJson) {
        List<WXGZHBean> beans = new ArrayList<WXGZHBean>();

        try {
            final JSONArray ja = new JSONArray(wxgzhJson);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                WXGZHBean bean = new WXGZHBean();
                if (haveKey(jo, "displayName") && haveKey(jo, "introduction") && haveKey(jo, "url") && haveKey(jo, "id") && haveKey(jo, "thumb") && haveKey(jo, "type")) {
                    bean.displayName = jo.getString("displayName");
                    bean.id = jo.getString("id");
                    bean.type = jo.getString("type");
                    bean.introduction = jo.getString("introduction");
                    bean.thumb = jo.getString("thumb");
                    bean.url = jo.getString("url");
                    if (new File(GZHPath + bean.id + ".jpg").exists()) {
                        bean.isPicExist = true;
                    }
                    beans.add(bean);
                }
            }

        } catch (Exception e) {
        }
        return beans;
    }

    private static String getConfigJson(String url) {
        String ConfigJson = "";
        try {
            ConfigJson = HttpUtil.getJson(url);
            ConfigBean bean1 = getConfigBean(ConfigJson);
            if (bean1 == null) {
                ConfigJson = "";

            }
        } catch (Exception ex) {
            ConfigJson = "";
        }
        return ConfigJson;
    }

    private static String getPubConfigJson(String url) {
        String getpubConfigJson = "";
        try {
            getpubConfigJson =
                    new HttpUtil().getJson(url);
            PublicConfigBean bean1 = getpublicConfigBean(getpubConfigJson);
            if (bean1 == null) {
                getpubConfigJson = "";

            }
        } catch (Exception ex) {
            getpubConfigJson = "";
        }
        return getpubConfigJson;
    }

    /**
     * 设置配置参数
     * @param url
     * @return
     */
    public static String setConfigData(Context context, String url){
        String application = "GPSTEST"; // 对应项目
        String apppackage = PublicUtil.getAppPackage(context);  // 应用包名
        String appversion = PublicUtil.getVersionCode(context)+""; // 应用版本
        String appmarket = PublicUtil.metadata(context, "UMENG_CHANNEL"); // 应用市场
        String agencychannel = PublicUtil.metadata(context, "AGENCY_CHANNEL"); // 代理渠道
        return String.format(url, application, apppackage, appversion, appmarket, agencychannel);
    }

    public static void initConfigJson(Context context) {
        String ConfigJson = "";
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        ConfigJson = getConfigJson(setConfigData(context, baseURLGetAdconfig));
        if (!ConfigJson.isEmpty()) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("ConfigJson", ConfigJson);
            editor.apply();
        }

        initConfigBean(context);
    }

    public static void initPublicConfigJson(Context context) {
        String ConfigJson = "";
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        ConfigJson = getPubConfigJson(setConfigData(context, baseURLGetPublicconfig));
        if (!ConfigJson.isEmpty()) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("publicConfigJson", ConfigJson);
            editor.apply();
        }

        initPublicConfigBean(context);
    }

    public static void initConfigBean(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String ConfigJson = mSettings.getString("ConfigJson", "");
        try {
            ConfigBean bean1 = getConfigBean(ConfigJson);
            configBean = bean1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initPublicConfigBean(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String ConfigJson = mSettings.getString("publicConfigJson", "");
        try {
            PublicConfigBean bean1 = getpublicConfigBean(ConfigJson);
            publicConfigBean = bean1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getVideoJson(String url) {

        String VideoJson = "";
        try {
            VideoJson = new HttpUtil().getJson(url);
            List<VideoBean> currentVideoBeans = getVideoBean(VideoJson);
            if (currentVideoBeans.size() == 0) {
                VideoJson = "";
            }
        } catch (Exception e) {

            VideoJson = "";
        }
        return VideoJson;
    }

    private static String getSelfadJson(String url) {
        String SelfadJson = "";
        try {
            SelfadJson = HttpUtil.getJson(url);
            List<ADBean> currentSelfAdBeans = getSelfAdBeans(SelfadJson);
            if (currentSelfAdBeans.size() == 0) {
                SelfadJson = "";
            }
        } catch (IOException e) {
            SelfadJson = "";
        }
        return SelfadJson;
    }

    /**
     * 读取asset里的文件
     *
     * @param context  上下文
     * @param filename 文件名
     * @return
     */
    public static String getZixunJsonFromAssets(Context context, String filename) {
        String string = "";
        try {
            InputStream in = context.getResources().getAssets().open(filename);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            string = new String(buffer);

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    private static String getZixunJson(String url) {
        String SelfadJson = "";
        try {
            SelfadJson = HttpUtil.getJson(url);
            List<ZiXunItemBean> currentSelfAdBeans = getZiXunBeans(SelfadJson);
            if (currentSelfAdBeans.size() == 0) {
                SelfadJson = "";
            }
        } catch (IOException e) {
            SelfadJson = "";
        }
        return SelfadJson;
    }

    public static void initselfadJson(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);

        if (publicConfigBean != null && !"".equals(publicConfigBean.selfadVersion) && !publicConfigBean.selfadVersion.equals(mSettings.getString("selfadVersion", ""))) {//需要更新
            String SelfadJson = getSelfadJson(setConfigData(context, baseURLGetSelfad));
            if (!SelfadJson.isEmpty()) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("SelfadJson", SelfadJson);
                editor.putString("selfadVersion", publicConfigBean.selfadVersion);
                editor.apply();
            }

        }
        initselfadBeans(context);
    }

    private static void initselfadBeans(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String SelfadJson = mSettings.getString("SelfadJson", "");
        try {
            List<ADBean> currentSelfAdBeans = getSelfAdBeans(SelfadJson);
            if (currentSelfAdBeans.size() == 0) {
                SelfadJson = "";
            }
            selfadBeans = currentSelfAdBeans;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initzixunJson(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String SelfadJson = "";
        if (publicConfigBean != null && !TextUtils.isEmpty(publicConfigBean.zixunVersion) && !publicConfigBean.zixunVersion.equals(mSettings.getString("zixunVersion", ""))) {//需要更新
            SelfadJson = getZixunJson(setConfigData(context, baseURLGetZixun));
            if (!TextUtils.isEmpty(SelfadJson)) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("zixunJson", SelfadJson);
                editor.putString("zixunVersion", publicConfigBean.zixunVersion);
                editor.apply();
            }
        }
        if (TextUtils.isEmpty(SelfadJson)) {
            SelfadJson = getZixunJsonFromAssets(context, "zixun.json");
            if (!TextUtils.isEmpty(SelfadJson)) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("zixunJson", SelfadJson);
                editor.putString("zixunVersion", "");
                editor.apply();
            }
        }

        initZixunBeans(context);
    }

    private static void initZixunBeans(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String ZixunJson = mSettings.getString("zixunJson", "");
        try {
            List<ZiXunItemBean> currentSelfAdBeans = getZiXunBeans(ZixunJson);
            if (currentSelfAdBeans.size() == 0) {
                ZixunJson = "";
            }
            ziXunBeans = currentSelfAdBeans;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ex) {
        }

    }


    private static boolean haveKey(JSONObject jo, String key) {
        return jo.has(key) && !jo.isNull(key);
    }

    /**
     * 是否展示资讯
     *
     * @return
     */
    public static boolean isShowCpuWeb() {
        if (configBean == null) {
            return false;
        }
        for (String version :
                configBean.nocpuadchannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isFengxiang() {
        if (configBean == null) {
            return false;
        }
        for (String version :
                configBean.nofenxiang.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSearch() {
        if (configBean == null) {
            return false;
        }
        for (String version :
                configBean.nosearch.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isShowHaoPing() {
        if (configBean == null) {
            return false;
        }
        for (String version : configBean.nohaoping.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCanShowBanner = true;

    public static boolean isShowBanner() {
        if (!isCanShowBanner) {
            return false;
        }
        if (configBean == null) {
            return false;
        }
        for (String version : configBean.noadbannerchannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isShowKP() {
        if (configBean == null) {//如果configbean都没有获取到
            return false;
        }
        for (String version : configBean.noadkpchannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isShowTP() {
        if (configBean == null) {//如果configbean都没有获取到
            return false;
        }
        for (String version : configBean.noadtpchannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isShowCP() {
        if (configBean == null) {
            return false;
        }
        for (String version : configBean.noadcpchannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNeedPay() {
        if (configBean == null) {
            return false;
        }
        for (String version : configBean.nopaychannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFirstFreeUse() {
        if (configBean == null) {
            return true;
        }
        for (String version : configBean.isfirstfreeusechannel.split(",")) {
            if (version.equals(versioncode)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isShowSelfLogo() {
        if (configBean == null) {
            return true;
        }
        for (String version : configBean.showselflogochannel.split(",")) {
            if (version.equals(versioncode)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isShowDS() {
        if (configBean == null) {
            return false;
        }
        for (String version : configBean.noshowdschannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isShowUpdate() {
        if (configBean == null) {
            return false;
        }
        for (String version : configBean.noupdatechannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 是否显示地图审图号
     * @return
     */
    public static boolean isShowMapNO() {
        if (configBean == null) {
            return true;
        }
        for (String version : configBean.nomapnochannel.split(",")) {
            if (version.equals(versioncode)) {
                return false;
            }
        }
        return true;
    }

    public static String getBaiduMapNO() {
        if (configBean == null) {
            return "©2022 北京百度网讯科技有限公司 - GS(2021)6026号 - 甲测资字11111342";
        }
        for (String str : configBean.mapnobaidu.split(",")) {
            String[] a = str.split(":");
            if (a.length == 2) {
                String versionItem = a[0];
                String adNameItem = a[1];
                if (versioncode.equals(versionItem)) {//平台与版本对应了，因为渠道已经选定了
                    return adNameItem;
                }

            }
        }
        return "©2022 北京百度网讯科技有限公司 - GS(2021)6026号 - 甲测资字11111342";
    }

    public static String getGaodeMapNO() {
        if (configBean == null) {
            return "©2022 高德软件有限公司 GS(2021)6375号 - 甲测资字11111093";
        }
        for (String str : configBean.mapnogaode.split(",")) {
            String[] a = str.split(":");
            if (a.length == 2) {
                String versionItem = a[0];
                String adNameItem = a[1];
                if (versioncode.equals(versionItem)) {//平台与版本对应了，因为渠道已经选定了
                    return adNameItem;
                }

            }
        }
        return "©2022 高德软件有限公司 GS(2021)6375号 - 甲测资字11111093";
    }

    public static String getKPType() {
        if (configBean == null) {
            return "csj";
        }
        for (String str : configBean.kptype.split(",")) {
            String[] a = str.split(":");
            if (a.length == 2) {
                String versionItem = a[0];
                String adNameItem = a[1];
                if (versioncode.equals(versionItem)) {//平台与版本对应了，因为渠道已经选定了
                    return adNameItem;
                }

            }
        }
        return "csj";

    }

    public static String getTPType() {
        if (configBean == null) {
            return "gdtmb";
        }
        for (String str : configBean.tptype.split(",")) {
            String[] a = str.split(":");
            if (a.length == 2) {
                String versionItem = a[0];
                String adNameItem = a[1];
                if (versioncode.equals(versionItem)) {//平台与版本对应了，因为渠道已经选定了
                    return adNameItem;
                }

            }
        }
        return "gdtmb";

    }

    public static String getCPType() {
        if (configBean == null) {
            return "csj2";
        }
        for (String str : configBean.cptype.split(",")) {
            String[] a = str.split(":");
            if (a.length == 2) {
                String versionItem = a[0];
                String adNameItem = a[1];
                if (versioncode.equals(versionItem)) {//平台与版本对应了，因为渠道已经选定了
                    return adNameItem;
                }

            }
        }
        return "csj2";

    }

    public static String getBannerType() {
        if (configBean == null) {
            return "csj";
        }
        for (String str : configBean.bannertype.split(",")) {
            String[] a = str.split(":");
            if (a.length == 2) {
                String versionItem = a[0];
                String adNameItem = a[1];
                if (versioncode.equals(versionItem)) {//平台与版本对应了，因为渠道已经选定了
                    return adNameItem;
                }

            }
        }
        return "csj";

    }

    private static List<Integer> GetRandomList(int size, int max) {
        Random r = new Random();
        List<Integer> list = new ArrayList<Integer>();
        int i;
        while (list.size() < size) {
            i = r.nextInt(max);
            if (!list.contains(i)) {
                list.add(i);
            }
        }
        Collections.sort(list);
        return list;
    }

    /*
     * 随机取广告，size表示取的数量
     * */
    public static List<ADBean> GetSelfADByCount(Context context, int size, String event_id) {
        List<ADBean> selfADs = new ArrayList<ADBean>();
        List<ADBean> ok_selfadBeans = new ArrayList<ADBean>();
        for (ADBean selfad : selfadBeans) {//过滤掉已经安装了的app
            if (!PackageUtil.isInstallApp(context, selfad.getAd_packagename())) {
                ok_selfadBeans.add(selfad);
            }

        }
        if (size >= ok_selfadBeans.size()) {
            selfADs.addAll(ok_selfadBeans);
        } else {
            //建立一个size大的0-selfadBeans.size()之间不重复的list
            List<Integer> positionList = GetRandomList(size, ok_selfadBeans.size());
            for (int i : positionList) {
                selfADs.add(ok_selfadBeans.get(i));
            }
        }
//        for (ADBean bean : selfADs) {
//            Map<String, String> map_ekv = new HashMap<String, String>();
//            map_ekv.put("show", bean.getAd_name());
//        }
        return selfADs;
    }

    public static void openAD(final Context context, final ADBean adbean, String tag) {//如果本条是广告
        if (context == null || adbean == null) return;
//        Map<String, String> map_ekv = new HashMap<String, String>();
//        map_ekv.put("click", adbean.getAd_name());
        


        int type = adbean.getAd_type();
        if (type == 1)//下载
        {
            if (PackageUtil.isInstallApp(context, adbean.getAd_packagename()))//已经安装直接打开
            {
                PackageUtil.startApp(context, adbean.getAd_packagename());
                return;
            }
            if (adbean.isAd_isConfirm()) {
                new AlertDialog.Builder(context).setTitle("确定下载：" + adbean.getAd_name() + "?")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“确认”后的操作
                                if (DownLoaderAPK.getInstance(context).addDownload(adbean)) {
                                    Toast.makeText(context, "开始下载:" + adbean.getAd_name(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, adbean.getAd_name() + " 已经在下载了:", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“返回”后的操作,这里不设置没有任何操作
                            }
                        }).show();
            } else {
                if (DownLoaderAPK.getInstance(context).addDownload(adbean)) {
                    Toast.makeText(context, "开始下载:" + adbean.getAd_name(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, adbean.getAd_name() + " 已经在下载了:", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (type == 2)//打开网页
        {
            if (adbean.getAd_apkurl().contains(".taobao.com"))//是淘宝链接
            {
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW"); //
                    String url = "";
                    if (adbean.getAd_apkurl().startsWith("http://")) {
                        url = adbean.getAd_apkurl().replaceFirst("http://", "taobao://");
                    } else {
                        url = adbean.getAd_apkurl().replaceFirst("https://", "taobao://");
                    }
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    PackageUtil.qidongLiulanqi((Activity) context, adbean.getAd_apkurl());
                }
            } else if (adbean.getAd_apkurl().contains("item.jd.com/"))//是淘宝链接
            {
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW"); //
                    int begin = adbean.getAd_apkurl().indexOf("item.jd.com/") + "item.jd.com/".length();
                    int end = adbean.getAd_apkurl().indexOf(".html");
                    String id = adbean.getAd_apkurl().substring(begin, end);
                    String url = "openapp.jdmobile://virtual?params=%7B%22sourceValue%22:%220_productDetail_97%22,%22des%22:%22productDetail%22,%22skuId%22:%22" + id + "%22,%22category%22:%22jump%22,%22sourceType%22:%22PCUBE_CHANNEL%22%7D";

                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    PackageUtil.qidongLiulanqi((Activity) context, adbean.getAd_apkurl());
                }
            } else {

                PackageUtil.qidongLiulanqi((Activity) context, adbean.getAd_apkurl());
            }
        } else if (type == 3)//打开公众号
        {
            WXGZHBean wxgzhbean = new WXGZHBean();
            if (AppConfig.wxgzhBeans != null && AppConfig.wxgzhBeans.size() > 0) {
                wxgzhbean.type = AppConfig.wxgzhBeans.get(0).type;
            } else {
                wxgzhbean.type = "pengyouquan,pengyou,putong";
            }

            wxgzhbean.thumb = adbean.getAd_thumbnail();
            wxgzhbean.displayName = adbean.getAd_name();
            wxgzhbean.id = adbean.getAd_packagename();
            wxgzhbean.url = adbean.getAd_apkurl();
            wxgzhbean.introduction = adbean.getAd_description();
//            Intent intent = new Intent(context, GZHAddActivity.class);
//            intent.putExtra("wxgzhbean", wxgzhbean);
//            context.startActivity(intent);
        } else {
            PackageUtil.qidongLiulanqi((Activity) context, adbean.getAd_apkurl());
        }

    }

}
