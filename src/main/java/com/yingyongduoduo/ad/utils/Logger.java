package com.yingyongduoduo.ad.utils;

import android.util.Log;

/**
 * 日志类
 * 
 * @author duoduoapp
 * 
 */
public class Logger {

	private static final String TAG = "GPSTest";
	private static boolean DEBUG = false;

	/**
	 * 设置是否需要输出日志
	 * 
	 * @param debug
	 */
	public static void setLog(boolean debug) {
		DEBUG = debug;
	}

	public static void info(String msg) {
		if (!DEBUG) {
			return;
		}
		Log.i(TAG, msg);
	}

	public static void debug(String msg) {
		if (!DEBUG) {
			return;
		}
		Log.d(TAG, msg);
	}

	public static void error(String msg) {
		if (!DEBUG) {
			return;
		}
		Log.e(TAG, msg);
	}

	public static void error(String msg, Throwable e) {
		if (!DEBUG) {
			return;
		}
		Log.e(TAG, msg, e);
	}

	public static void error(Throwable e) {
		if (!DEBUG) {
			return;
		}
		Log.e(TAG, "error:", e);
	}

	public static void debug(String tag, String msg) {
		if (!DEBUG) {
			return;
		}
		Log.d(tag, msg);
	}

	public static void error(String tag, String msg) {
		if (!DEBUG) {
			return;
		}
		Log.e(tag, msg);
	}

	public static void error(String tag, String msg, Throwable e) {
		if (!DEBUG) {
			return;
		}
		Log.e(tag, msg, e);
	}
}
