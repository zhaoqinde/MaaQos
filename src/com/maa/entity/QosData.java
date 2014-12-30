package com.maa.entity;

public class QosData {

	private String networkType = "null";
	private String carrier = "null";
	private String url = "null";
	private long consuming = -1;
	private String dns;
	private String testTimeStamp = "null";
	private int statusCode = -1;

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getConsuming() {
		return consuming;
	}

	public void setConsuming(long consuming) {
		this.consuming = consuming;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getTestTimeStamp() {
		return testTimeStamp;
	}

	public void setTestTimeStamp(String testTimeStamp) {
		this.testTimeStamp = testTimeStamp;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
