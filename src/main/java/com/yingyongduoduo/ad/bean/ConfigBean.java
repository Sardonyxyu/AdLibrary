package com.yingyongduoduo.ad.bean;

import java.util.HashMap;
import java.util.Map;

public class ConfigBean {
    public UpdateBean updatemsg = new UpdateBean();
    public Map<String, String> ad_banner_idMap = new HashMap<String, String>();
    public Map<String, String> ad_kp_idMap = new HashMap<String, String>();
    public Map<String, String> ad_cp_idMap = new HashMap<String, String>();
    public Map<String, String> ad_tp_idMap = new HashMap<String, String>();
    public Map<String, String> ad_shiping_idMap = new HashMap<String, String>();
    public Map<String, String> ad_pos_idMap = new HashMap<String, String>();
    public String cpuidorurl = "";//baidu的内容联盟的链接或者id，如果是链接，直接解析
    public String nomeinvchannel = "";
    public String noselfadchannel = "";
    public String nocpuadchannel = "";//没有内容联盟内容的渠道
    public String nofenxiang = "";
    public String nosearch = "";
    public String nohaoping = "";
    public String nosearchpaychannel = "";//没有付费搜索的版本
    public String noadbannerchannel = "";//没广告条的版本
    public String noadkpchannel = "";//没有开屏的版本
    public String noadtpchannel = "";//没有开屏的版本
    public String noadcpchannel = "";//没有插屏的版本
    public String nopaychannel = "";//不用付费的版本
    public String noshipingadchannel = "";//没有视频广告的版本
    public String noshortvideochannel = "";//没有短视频的版本
    public String nodownloadtaskchannel = "";//没有下载任务的版本
    public String noadposchannel = "";//没有消息流的版本
    public String isfirstfreeusechannel = "";//首次免费版本
    public String showselflogochannel = "";//没有LOGO遮挡的版本
    public String greythemechannel = "";//全局灰色主题
    public String noshowdschannel = "";//打开应用，不显示打赏对话框版本
    public String noupdatechannel = "";
    public String noaddvideochannel = "";
    public String noadVideowebchannel = "";//使用第三方去广告链接对web地址进行转化播放，这时候是给浏览器播放
    public String playonwebchannel = "";//使用网页播放的版本
    public String nomapnochannel = "";//是否显示地图审图号
    public String mapno = "";// 地图审图号
    public String nogzhchannel = "";
    public String bannertype = "";
    public String cptype = "";
    public String kptype = "";
    public String tptype = "";
    public String postype = "";
    public String shipingtype = "";
    public String nojpbdchannel = "";
    public String baidu_xiaoshuo_id = ""; // 百度小说id
    public String audiobook_show_recommend = ""; // 听书是否显示找相似功能

    public String nozhikouling = "";//没有吱口令的渠道
}
