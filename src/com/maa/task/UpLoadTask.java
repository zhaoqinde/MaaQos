package com.maa.task;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpException;

import android.os.Handler;
import android.util.Log;

import com.maa.control.MaaQos;
import com.maa.control.QosService;
import com.maa.entity.FormFile;
import com.maa.util.QosUtil;
import com.mato.sdk.proxy.Proxy;

public class UpLoadTask implements Runnable {

	private String url;
	private byte[] data;
	private Handler handler;
	
	public UpLoadTask(String url, byte[] data, Handler handler) {
		this.url = url;
		this.data = data;
		this.handler = handler;
	}

	@Override
	public void run() {
		try {
			FormFile formFile = new FormFile("accesslog.zip", data, "filename",
					null);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
			String date = sdf.format(new java.util.Date());
			boolean isViaProxy = Proxy.getViaProxy();
			String packageName = QosUtil.getPackageName();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("date", date);
			paramMap.put("area", QosService.getProvince());
			paramMap.put("longitude", QosService.getLongitude());
			paramMap.put("latitude", QosService.getLatitude());
			paramMap.put("appname", packageName);
			paramMap.put("viaproxy", isViaProxy + "");
			paramMap.put("type", "qos");
			paramMap.put("codec", "zip");
			Log.d("QOS", "the parammap is:" + paramMap.toString());
			
			post(url, paramMap, new FormFile[] { formFile });
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void post(String actionUrl, Map<String, String> params, FormFile[] files)
			throws HttpException, IOException, KeyManagementException,
			NoSuchAlgorithmException {

		String BOUNDARY = "---7d4a6d158c9";
		String MULTIPART_FORM_DATA = "multipart/form-data";

		URL url = new URL(actionUrl);
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
				new java.security.SecureRandom());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false);// 不使用Cache
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
				+ "; boundary=" + BOUNDARY);
//		conn.setSSLSocketFactory(sc.getSocketFactory());
//		conn.setHostnameVerifier(new TrustAnyHostnameVerifier());

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"\r\n\r\n");
			sb.append(entry.getValue());
			sb.append("\r\n");
		}

		byte[] end_data = ("--" + BOUNDARY + "–\r\n").getBytes();// 数据结束标志
		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());// 发送表单字段数据

		// 上传的文件部分
		for (FormFile file : files) {
			StringBuilder split = new StringBuilder();
			split.append("--");
			split.append(BOUNDARY);
			split.append("\r\n");
			split.append("Content-Disposition: form-data; name=\""
					+ file.getFormname() + "\";filename=\"" + file.getFilname()
					+ "\"\r\n");
			split.append("Content-Type: " + file.getContentType() + "\r\n\r\n");
			outStream.write(split.toString().getBytes());
			outStream.write(file.getData(), 0, file.getData().length);
			outStream.write("\r\n".getBytes());
		}

		outStream.write(end_data);
		outStream.flush();
		int cah = conn.getResponseCode();

		if (cah != 200) {
			conn.disconnect();
			throw new HttpException("请求url失败");
		}
		InputStream is = conn.getInputStream();
		int ch;
		StringBuilder b = new StringBuilder();
		while ((ch = is.read()) != -1) {
			b.append((char) ch);
		}
		outStream.close();
		conn.disconnect();
		
		if (actionUrl.equals(MaaQos.MAA_SERVICE)) {
			handler.sendEmptyMessage(MaaQos.UPLOAD_FINISH);
		}
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

}


