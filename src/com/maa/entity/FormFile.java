package com.maa.entity;

public class FormFile {

	private byte[] data;

	private String filname;

	private String formname;

	private String contentType = "application/octet-stream"; // 需要查阅相关的资料

	public FormFile(String filname, byte[] data, String formname,
			String contentType) {
		this.data = data;
		this.filname = filname;
		this.formname = formname;
		if (contentType != null)
			this.contentType = contentType;
	}

	public byte[] getData() {
		return data;
	}

	public String getFilname() {
		return filname;
	}

	public String getFormname() {
		return formname;
	}

	public String getContentType() {
		return contentType;
	}
}
