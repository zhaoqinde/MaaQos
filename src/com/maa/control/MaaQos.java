package com.maa.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.maa.task.TestUrlTask;
import com.maa.task.UpLoadTask;
import com.maa.util.QosUtil;
import com.maa.util.ZipUtils;
import com.mato.sdk.proxy.Proxy;

public class MaaQos {
	
	public static final String MAA_SERVICE = "http://218.104.133.69/file";
	public static final int TEST_FINISH = 0;
	public static final int UPLOAD_FINISH = 1;
	public static final String version = "1.0.0";
	
	private ArrayList<String> testUrls = new ArrayList<String>();
	private int testCount = 0;
	private String upLoadUrl = null;
	
	private ExecutorService executorService = new ThreadPoolExecutor(1, 1,
			5000l, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	private MyHandler myHandler;
	
	public MaaQos() {
		myHandler = new MyHandler();
	}

	public void setUrls(ArrayList<String> testUrls) {
		if (testUrls != null) {
			this.testUrls = testUrls;
		}
	}

	public void setUploadUrl(String uploadUrl) {
		this.upLoadUrl = uploadUrl;
	}

	public void setProvince(String province) {
		QosService.setProvince(province);
	}

	public void start(Context context) {
		QosService.setContext(context);
		Proxy.start(context);
		QosUtil.getLocation();
		File file = new File(QosUtil.getFilesDir());
		QosUtil.delete(file);
		for (int i = 0; i < testUrls.size(); i++) {
			executorService
					.execute(new TestUrlTask(testUrls.get(i), myHandler));
		}
	}

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TEST_FINISH:
				testCount++;
				if (testCount == testUrls.size()) {
					reportQos();
				}
				break;
			case UPLOAD_FINISH:
				QosService.clear();
				break;
			default:
				break;
			}
		}

		private void reportQos() {
			QosService.fetchQosData();
			byte[] gzipData = null;
			try {
				gzipData = ZipUtils.zipFiles(QosUtil.getFilesDir());
			} catch (IOException e2) {
				Log.e("logReport", "get gzip fail!" + e2);
			}

			if (upLoadUrl != null) {
				executorService.execute(new UpLoadTask(upLoadUrl, gzipData, myHandler));
			}
			executorService.execute(new UpLoadTask(MAA_SERVICE, gzipData, myHandler));
		}
	}
}
