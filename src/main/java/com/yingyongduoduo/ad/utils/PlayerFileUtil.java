package com.yingyongduoduo.ad.utils;




import java.io.File;

public class PlayerFileUtil {

	/**
	 * 根据路径创建文件夹 文件夹以"/"结尾,不然就默认文件
	 * 
	 * @param path
	 *            路径
	 */
	public static void creatFolder(String path) {
		File file = new File(path);
		if (!file.exists()) {
			Boolean isSuccess=file.mkdirs();
			if(!isSuccess)
			{
				System.out.println("新建文件夹失败:"+file.getPath());
			}
			Logger.debug("creat folder:" + file.getAbsolutePath());
		}

	}
}
