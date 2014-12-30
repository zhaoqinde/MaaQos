package com.maa.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.maa.control.QosService;

public class QosUtil {

	private static Context matoContext;

	/**
	 * 
	 * 网络连接状态
	 * 
	 */
	public static enum NetWorkEnum {
		NoConnection, Mobile, Wifi
	}

	public static void setContext(Context context) {
		matoContext = context;
	}

	public static String getDns() {

		try {
			Process p = Runtime.getRuntime().exec("getprop net.dns1");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String dns = in.readLine();
			return dns;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取IMEI
	 * 
	 * @param context
	 *            上下文
	 * @return string IMEI
	 */
	public static String getIMEI() {
		checkNull();
		TelephonyManager telephonyManager = (TelephonyManager) matoContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return imei == null ? "null" : imei;
	}

	public static void checkNull() {
		if (matoContext == null) {
			throw new NullPointerException("context is null");
		}
	}

	/**
	 * 获取应用程序包名
	 * 
	 * @return
	 */
	public static String getPackageName() {
		checkNull();
		return matoContext.getPackageName();
	}

	/**
	 * 获取运营商信息
	 * 
	 * @return
	 */
	public static String getProviderName() {

		String providerName = "Unknown";
		TelephonyManager telephonyManager = (TelephonyManager) matoContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telephonyManager.getSubscriberId();

		if (imsi != null) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
				providerName = "中国移动";
			} else if (imsi.startsWith("46001")) {
				providerName = "中国联通";
			} else if (imsi.startsWith("46003")) {
				providerName = "中国电信";
			}
		}
		return providerName;
	}

	/**
	 * 获取网络连接状态
	 * 
	 * @param context
	 * @return
	 */
	public static NetWorkEnum getNetWorkState() {

		ConnectivityManager cwjManager = (ConnectivityManager) matoContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();

		if (info != null) {
			if (!info.isAvailable()) {
				return NetWorkEnum.NoConnection;
			} else {
				if (info.getType() == ConnectivityManager.TYPE_WIFI
						&& info.isConnected()) {
					return NetWorkEnum.Wifi;
				} else if (info.getType() == ConnectivityManager.TYPE_MOBILE
						&& info.isConnected()) {
					return NetWorkEnum.Mobile;
				}
			}
		}
		return NetWorkEnum.NoConnection;
	}

	/**
	 * 获取网络类型
	 * 
	 * @return
	 */
	public static String getNetworkType() {

		String networkType = "Unknown";
		TelephonyManager telephonyManager = (TelephonyManager) matoContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		NetWorkEnum netWorkState = getNetWorkState();
		if (netWorkState == NetWorkEnum.Wifi) {
			networkType = "WIFI";
		} else if (netWorkState == NetWorkEnum.Mobile) {
			int type = telephonyManager.getNetworkType();

			if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
				networkType = "2G";
			} else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
				networkType = "2G";
			} else if (type == TelephonyManager.NETWORK_TYPE_CDMA
					|| type == TelephonyManager.NETWORK_TYPE_1xRTT) {
				networkType = "2G";
			} else if (type == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| type == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| type == TelephonyManager.NETWORK_TYPE_EVDO_B
					|| type == TelephonyManager.NETWORK_TYPE_UMTS
					|| type == TelephonyManager.NETWORK_TYPE_HSDPA
					|| type == TelephonyManager.NETWORK_TYPE_HSUPA
					|| type == TelephonyManager.NETWORK_TYPE_HSPA
					|| type == TelephonyManager.NETWORK_TYPE_HSPAP) {
				networkType = "3G";
			} else if (type == TelephonyManager.NETWORK_TYPE_LTE) {
				networkType = "4G";
			} else {
				networkType = "Unknown";
			}

		}

		return networkType;
	}

	public static String getFilesDir() {
		return matoContext.getFilesDir().getAbsolutePath() + "/qos";
	}

	public static byte[] readStream(InputStream in) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		in.close();
		return outputStream.toByteArray();
	}

	public static void createAccessLog(String accessLog, String fileName) {

		makeDirectory(getFilesDir());  
		FileOutputStream fos = null;
		try {
			File newFile = new File(getFilesDir(), fileName);
			if (!newFile.exists()) {
				newFile.createNewFile();
			}

			fos = new FileOutputStream(newFile, false);
			Writer writer = new OutputStreamWriter(fos);
			writer.write(accessLog);
			writer.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void makeDirectory(String filePath) {  
        File file = null;  
        try {  
            file = new File(filePath);  
            if (!file.exists()) {  
                file.mkdir();  
            }  
        } catch (Exception e) {  
  
        }  
    }  
	
	public static void delete(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			
			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}
	
	public static void getLocation(){
		try {
			LocationManager locationm = (LocationManager) matoContext.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			String provider = locationm.getBestProvider(criteria, true);
			Location location = locationm.getLastKnownLocation(provider);
			QosService.setLatitude(location.getLatitude() + "");
			QosService.setLongitude(location.getLongitude() + "");
		} catch (NullPointerException e) {
			e.printStackTrace();
			Log.d("QOS", "can not get location");
		}
	}
}
