package com.yingyongduoduo.ad.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static String getJson(String uri) throws IOException {
		return getJson(uri, false);
	}



	public static String getJson(String uri, boolean referer) throws IOException {
		return getJson(uri, referer, "");
	}

	public static String getJson(String uri, String saveFilePath) throws IOException {
		return getJson(uri, false, saveFilePath);
	}

	public static String getJson(String uri, boolean referer, String saveFilePath) throws IOException {
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// 然后, 设置请求方式为GET方式，就是相当于浏览器打开网页
		connection.setRequestMethod("GET");
		// 接着设置超时时间为5秒，5秒内若连接不上，则通知用户网络连接有问题
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		connection.setReadTimeout(10 * 1000);
		connection.setConnectTimeout(10 * 1000);
//		if (referer) {
//			connection.setRequestProperty("referer", REFERER);
//		}
		// 若连接上之后，得到网络的输入流，内容就是网页源码的字节码
		InputStream inStream = connection.getInputStream();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
		FileOutputStream fos = null;
		if(!TextUtils.isEmpty(saveFilePath)){
			File file = new File(saveFilePath);
			if(file.exists())
				file.delete();
			fos = new FileOutputStream(saveFilePath);
		}

		StringBuffer result = new StringBuffer();
		String line = null;
		while ((line = bReader.readLine()) != null) {
			result.append(line);
			if(fos != null)
				fos.write(line.getBytes());
		}
		connection.disconnect();

		if(fos != null) {
			fos.flush();
			fos.close();
		}

		bReader.close();
		inStream.close();
		return toUtf8(result.toString());
	}

	public static String toUtf8(String str) {
		String result = null;
		try {
			result = new String(str.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

}
