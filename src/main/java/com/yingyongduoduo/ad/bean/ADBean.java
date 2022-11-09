package com.yingyongduoduo.ad.bean;

import java.io.Serializable;

/**
 * 广告的
 *
 * @author Administrator
 */
public class ADBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 354982111L;

    private String ad_iconurl = "";  // 图片
    private String ad_apkurl = "";// 下载地址,或者需要打开的网页地址
    private String ad_packagename = ""; // 包名
    private int ad_type = 1;// 广告类型
    private boolean ad_isConfirm = false;// 是否二次确认
    private int ad_versioncode = 1;// 版本的
    private String ad_name = "";// 名字
    private String ad_description = "";   // 描述
    private String ad_banner = "";
    private String ad_kp = "";
    private String ad_thumbnail = "";
    private float ad_thumbnailscal=0.2f;
    private float ad_iconscal=0.2f;
    private boolean ad_have = false;// 本地是否安装了；不在json里面
    private String ad_platform;//是否是广告,广告会将他设置为"ad"；不在json中
    private int ad_doload_statue = 0;//这个参数是下载状态参数,0=未开始，1=下载中，2取消；不在json中

    public String getAd_iconurl() {
        return ad_iconurl;
    }

    public void setAd_iconurl(String ad_iconurl) {
        this.ad_iconurl = ad_iconurl;
    }

    public String getAd_apkurl() {
        return ad_apkurl;
    }

    public void setAd_apkurl(String ad_apkurl) {
        this.ad_apkurl = ad_apkurl;
    }

    public String getAd_packagename() {
        return ad_packagename;
    }

    public void setAd_packagename(String ad_packagename) {
        this.ad_packagename = ad_packagename;
    }

    public int getAd_type() {
        return ad_type;
    }

    public void setAd_type(int ad_type) {
        this.ad_type = ad_type;
    }

    public boolean isAd_have() {
        return ad_have;
    }

    public void setAd_have(boolean ad_have) {
        this.ad_have = ad_have;
    }

    public boolean isAd_isConfirm() {
        return ad_isConfirm;
    }

    public void setAd_isConfirm(boolean ad_isConfirm) {
        this.ad_isConfirm = ad_isConfirm;
    }

    public int getAd_versioncode() {
        return ad_versioncode;
    }

    public void setAd_versioncode(int ad_versioncode) {
        this.ad_versioncode = ad_versioncode;
    }

    public int getAd_doload_statue() {
        return ad_doload_statue;
    }

    public void setAd_doload_statue(int ad_doload_statue) {
        this.ad_doload_statue = ad_doload_statue;
    }

    public String getAd_platform() {
        return ad_platform;
    }

    public void setAd_platform(String ad_platform) {
        this.ad_platform = ad_platform;
    }

    public String getAd_name() {
        return ad_name;
    }

    public void setAd_name(String ad_name) {
        this.ad_name = ad_name;
    }

    public String getAd_description() {
        return ad_description;
    }

    public void setAd_description(String ad_description) {
        this.ad_description = ad_description;
    }

    public ADBean() {

    }

    public String getAd_thumbnail() {
        return ad_thumbnail;
    }

    public void setAd_thumbnail(String ad_thumbnail) {
        this.ad_thumbnail = ad_thumbnail;
    }

    public float getAd_thumbnailscal() {
        return ad_thumbnailscal;
    }

    public void setAd_thumbnailscal(float ad_thumbnailscal) {
        this.ad_thumbnailscal = ad_thumbnailscal;
    }

    public float getAd_iconscal() {
        return ad_iconscal;
    }

    public void setAd_iconscal(float ad_iconscal) {
        this.ad_iconscal = ad_iconscal;
    }

    public String getAd_banner() {
        return ad_banner;
    }

    public void setAd_banner(String ad_banner) {
        this.ad_banner = ad_banner;
    }

    public String getAd_kp() {
        return ad_kp;
    }

    public void setAd_kp(String ad_kp) {
        this.ad_kp = ad_kp;
    }
}
