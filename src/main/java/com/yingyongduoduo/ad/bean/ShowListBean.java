package com.yingyongduoduo.ad.bean;

import java.io.Serializable;

public class ShowListBean extends ADBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1686513201L;
	private String id = ""; // id
	private String imageid = ""; // 图片id
	private String group_title = ""; // 标题
	private String cover_imgurl = ""; // 实际图片
	private String total_count = ""; // 有几个
	private String qhimg_thumb_url = ""; // 预览图片

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImageid() {
		return imageid;
	}

	public void setImageid(String imageid) {
		this.imageid = imageid;
	}

	public String getGroup_title() {
		return group_title;
	}

	public void setGroup_title(String group_title) {
		this.group_title = group_title;
	}

	public String getCover_imgurl() {
		return cover_imgurl;
	}

	public void setCover_imgurl(String cover_imgurl) {
		this.cover_imgurl = cover_imgurl;
	}

	public String getTotal_count() {
		return total_count;
	}

	public void setTotal_count(String total_count) {
		this.total_count = total_count;
	}

	public String getQhimg_thumb_url() {
		return qhimg_thumb_url;
	}

	public void setQhimg_thumb_url(String qhimg_thumb_url) {
		this.qhimg_thumb_url = qhimg_thumb_url;
	}

}
