package com.yingyongduoduo.ad.media;

import com.bytedance.sdk.dp.DPSdk;
import com.bytedance.sdk.dp.DPWidgetBannerParams;
import com.bytedance.sdk.dp.DPWidgetBubbleParams;
import com.bytedance.sdk.dp.DPWidgetDrawParams;
import com.bytedance.sdk.dp.DPWidgetGridParams;
import com.bytedance.sdk.dp.DPWidgetInnerPushParams;
import com.bytedance.sdk.dp.DPWidgetNewsParams;
import com.bytedance.sdk.dp.DPWidgetTextChainParams;
import com.bytedance.sdk.dp.DPWidgetVideoCardParams;
import com.bytedance.sdk.dp.DPWidgetVideoSingleCardParams;
import com.bytedance.sdk.dp.IDPNativeData;
import com.bytedance.sdk.dp.IDPWidget;
import com.bytedance.sdk.dp.IDPWidgetFactory;

import org.json.JSONObject;

/**
 * Create by hanweiwei on 2020-03-26.
 */
public final class DPHolder {
    private static volatile DPHolder sInstance;

    public static DPHolder getInstance() {
        if (sInstance == null) {
            synchronized (DPHolder.class) {
                if (sInstance == null) {
                    sInstance = new DPHolder();
                }
            }
        }
        return sInstance;
    }

    private DPHolder() {
    }


    public IDPWidget buildDrawWidget(DPWidgetDrawParams params) {
        //创建draw视频流组件
        return getFactory().createDraw(params);
    }

    public IDPWidget buildGridWidget(DPWidgetGridParams params) {
        //创建宫格组件
        return getFactory().createGrid(params);
    }

    public IDPWidget buildDoubleFeedWidget(DPWidgetGridParams params) {
        //创建双Feed组件
        return getFactory().createDoubleFeed(params);
    }

    public IDPWidget buildNewsTabsWidget(DPWidgetNewsParams params) {
        //创建多频道新闻组件
        return getFactory().createNewsTabs(params);
    }

    public IDPWidget buildNewsOneTabWidget(DPWidgetNewsParams params) {
        //创建单列表新闻组件
        return getFactory().createNewsOneTab(params);
    }

    public void loadVideoCard(DPWidgetVideoCardParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadVideoCard(params, callback);
    }

    public void loadSmallVideoCard(DPWidgetVideoCardParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadSmallVideoCard(params, callback);
    }

    public void loadVideoSingleCard(DPWidgetVideoSingleCardParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadVideoSingleCard(params, callback);
    }

    public void loadTextChain(DPWidgetTextChainParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadTextChain(params, callback);
    }
    public void loadCustomVideoCard(DPWidgetVideoCardParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadCustomVideoCard(params, callback);
    }

    public void loadBubble(DPWidgetBubbleParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadBubble(params, callback);
    }

    public void loadBanner(DPWidgetBannerParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadBanner(params, callback);
    }

    public void loadInnerPush(DPWidgetInnerPushParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadInnerPush(params, callback);
    }

    public void loadVideoSingleCard4News(DPWidgetVideoSingleCardParams params, IDPWidgetFactory.Callback callback) {
        getFactory().loadVideoSingleCard4News(params, callback);
    }

    public void loadNativeNews(DPWidgetNewsParams params, IDPNativeData.DPNativeDataListener listener) {
        getFactory().loadNativeNews(params, listener);
    }

    public void enterNewsDetail(DPWidgetNewsParams params, long groupId, String data) {
        getFactory().enterNewsDetail(params, groupId, data);
    }

    public void loadPush(DPWidgetNewsParams params) {
        getFactory().pushNews(params);
    }

    public void uploadLog(String category, String event, JSONObject json) {
        getFactory().uploadLog(category, event, json);
    }

    public void notifyUserInfo() {
        //接入红包功能的开发者，在用户登录成功后需要刷新用户信息
//        long uid = LoginActivity.getUserId();
//        if (uid == 0) {
//            return;
//        }
//        getFactory().notifyUserInfo(new DPUser()
//                .setUserId(uid)//必须透传用户uid
//                .setName("test_name")//用户昵称，可选
//                .setAvatarUrl("xxx")//用户图像，可选
//        );
    }

    private IDPWidgetFactory getFactory() {
        //一定要初始化后才能调用，否则会发生异常问题
        return DPSdk.factory();
    }

}
