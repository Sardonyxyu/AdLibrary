package com.yingyongduoduo.ad.bean;

import java.io.Serializable;

public class SearchListBean extends ADBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4654810357611L;
	private String id = ""; // id
	private String title = ""; // 名字
	private String imgsize = ""; // 图片大小
	private String imgtype = ""; // 类型
	private String img = ""; // 图片地址
	private String thumb = ""; // 略缩图地址
	private String _thumb = ""; // 备份地址
	private String grpcnt = ""; // 有多少张

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgsize() {
		return imgsize;
	}

	public void setImgsize(String imgsize) {
		this.imgsize = imgsize;
	}

	public String getImgtype() {
		return imgtype;
	}

	public void setImgtype(String imgtype) {
		this.imgtype = imgtype;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getGrpcnt() {
		return grpcnt;
	}

	public void setGrpcnt(String grpcnt) {
		this.grpcnt = grpcnt;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String get_thumb() {
		return _thumb;
	}

	public void set_thumb(String _thumb) {
		this._thumb = _thumb;
	}

}
