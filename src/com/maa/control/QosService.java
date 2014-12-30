package com.maa.control;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

import com.maa.entity.QosData;
import com.maa.util.QosUtil;
import com.mato.sdk.proxy.Address;
import com.mato.sdk.proxy.Proxy;

public class QosService {

	private static List<QosData> qosDataList = new LinkedList<QosData>();
	private static long start, end;
	private static String province = "null";
	private static String longitude = "null";
	private static String latitude = "null";

	private QosService() {

	}

	public static List<QosData> getList() {
		return qosDataList;
	}

	public static void setContext(Context context) {
		QosUtil.setContext(context);
	}

	public static String getProvince() {
		return province;
	}

	public static void setProvince(String province) {
		QosService.province = province;
	}

	public static String getLongitude() {
		return longitude;
	}

	public static void setLongitude(String longitude) {
		QosService.longitude = longitude;
	}

	public static String getLatitude() {
		return latitude;
	}

	public static void setLatitude(String latitude) {
		QosService.latitude = latitude;
	}

	public static void clear() {
		if (qosDataList != null)
			qosDataList.clear();
	}

	public synchronized static void setUrl(String url) {
		String carrier = QosUtil.getProviderName();
		String networkType = QosUtil.getNetworkType();
		QosData qosData = new QosData();
		qosData.setUrl(url);
		qosData.setDns(QosUtil.getDns());
		qosData.setCarrier(carrier);
		qosData.setNetworkType(networkType);
		qosDataList.add(qosData);
	}

	public synchronized static void startCount() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
		String startTime = sdf.format(new java.util.Date());
		if (qosDataList != null && qosDataList.size() > 0)
			qosDataList.get(qosDataList.size() - 1).setTestTimeStamp(startTime);
		start = System.currentTimeMillis();
		Log.d("QOS", "start is" + start);
	}

	public synchronized static void stopCount() {
		end = System.currentTimeMillis();
		if (qosDataList != null && qosDataList.size() > 0)
			qosDataList.get(qosDataList.size() - 1).setConsuming((end - start));
		Log.d("QOS", "stop is" + (end - start));
	}

	public static HttpURLConnection getConnection(String url) {
		/**
		 * 设置代理
		 */
		HttpURLConnection conn = null;
		try {
			URL u = null;
			u = new URL(url);
			Address address = Proxy.getAddress();
			if (address != null) {
				String host = address.getHost();
				int port = address.getPort();
				java.net.Proxy proxy = new java.net.Proxy(
						java.net.Proxy.Type.HTTP, new InetSocketAddress(host,
								port));

				conn = (HttpURLConnection) u.openConnection(proxy);
				return conn;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static void fetchQosData() {
		try {
			String uuid = UUID.randomUUID().toString();
			
			StringBuffer stringBuffer = new StringBuffer();
			for (QosData data : qosDataList) {
				stringBuffer.append(data.getTestTimeStamp()).append("\t");
				stringBuffer.append(data.getUrl()).append("\t");
				stringBuffer.append(data.getStatusCode()).append("\t");
				stringBuffer.append(data.getConsuming()).append("\t");
				stringBuffer.append(data.getCarrier()).append("\t");
				stringBuffer.append(data.getNetworkType()).append("\n");
			}
			Log.d("QOS", stringBuffer.toString());
			QosUtil.createAccessLog(stringBuffer.toString(), uuid + ".log");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
