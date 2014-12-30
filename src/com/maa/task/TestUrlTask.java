package com.maa.task;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.util.Log;

import com.maa.control.MaaQos;
import com.maa.control.QosService;
import com.maa.util.QosUtil;

public class TestUrlTask implements Runnable {

	private String url;
	private Handler handler;
	
	public TestUrlTask(String url, Handler myHandler) {
		this.url = url;
		this.handler = myHandler;
	}

	@Override
	public void run() {
		getInput(url);
	}

	private void getInput(String url) {
		try {
			QosService.setUrl(url);

			QosService.startCount();

			HttpURLConnection conn = QosService.getConnection(url);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.connect();
			int code = conn.getResponseCode();
			if (QosService.getList() != null
					&& QosService.getList().size() > 0)
				QosService.getList().get(QosService.getList().size() - 1)
						.setStatusCode(code);

			if (code == 200) {
				InputStream in = conn.getInputStream();
				QosUtil.readStream(in);
				in.close();
			}
			QosService.stopCount();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("MAA", "download exception");
		} finally {
			handler.sendEmptyMessage(MaaQos.TEST_FINISH);
		}
	}

	
}
