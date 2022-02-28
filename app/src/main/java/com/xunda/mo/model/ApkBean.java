package com.xunda.mo.model;

import java.io.Serializable;

/**
 * 版本对象
 */
public class ApkBean implements Serializable {


	private String website;
	private Integer isForceUpdate;
	private String remark;
	private String version;
	private String platform;

	public String getWebsite() {
		return website;
	}

	public Integer getIsForceUpdate() {
		return isForceUpdate;
	}

	public String getRemark() {
		return remark;
	}

	public String getVersion() {
		return version;
	}

	public String getPlatform() {
		return platform;
	}
}
