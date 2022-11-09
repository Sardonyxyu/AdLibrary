package com.yingyongduoduo.ad.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.yingyongduoduo.ad.bean.ADBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/***
 * 下载类
 *
 * @author Administrator
 */
public class DownLoaderAPK {

    private final List<ADBean> DOWNLOAD_INFOS = new ArrayList<>();                // 储存下载对象
    /**
     * 线程池
     */
    public static ThreadPoolExecutor EXECUTOR_SERVICE = null;
    private static Context context;
    public static DownLoaderAPK downLoader;
    private String baseDir = "";

    private DownLoaderAPK() {
        baseDir = context.getCacheDir() + "/tv1/gps_test/apk/";
        EXECUTOR_SERVICE = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    /**
     * 获取下载类的实例
     *
     * @param
     * @return
     */
    public static DownLoaderAPK getInstance(Context mContext) {
        context=mContext;
        if (downLoader == null) {
            synchronized (DownLoaderAPK.class) {
                if (downLoader == null) {
                    downLoader = new DownLoaderAPK();

                }
            }
        }
        return downLoader;
    }

    public void setHandler(Handler handler) { // 设置 handler 对象,更新UI使用
        Logger.debug("set handler!");
        this.uiHandler = handler;
    }

    /**
     * 执行下载的类
     *
     * @author Administrator
     */
    private class Command implements Runnable {
        private final ADBean info;

        public Command(ADBean info) {
            this.info = info;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            if (!baseDir.endsWith(File.separator)) {
                baseDir = baseDir + File.separator;
            }
            File filebaseDir = new File(baseDir);
            if (!filebaseDir.exists()) {
                PlayerFileUtil.creatFolder(baseDir); // 创建文件夹
            }
            String url = info.getAd_apkurl();//下载url
            String filePath = baseDir + info.getAd_packagename() + "_" + info.getAd_versioncode() + ".apk";
            String filePathTmp = baseDir + info.getAd_packagename() + "_" + info.getAd_versioncode() + ".apk.temp";
            File file1 = new File(filePath);
            if (file1.exists()) {//如果文件已经存在，则直接发给UI去安装
                if (uiHandler != null) {
                    Message message = uiHandler.obtainMessage(2000);//通知成功
                    message.obj = info;
                    uiHandler.sendMessage(message);
                }
                DOWNLOAD_INFOS.remove(info);
                return;
            }
            FileOutputStream randomAccessFile = null;
            InputStream is = null;
            HttpURLConnection connection = null;
            try {
                int progress = 0;
                File file = new File(filePathTmp);
                long where = 0;
                if (file.exists()){
                    where = file.length();
                }
                URL uri = new URL(url);
                connection = (HttpURLConnection) uri.openConnection();
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                if (where > 0) {
                    connection.setRequestProperty("Range", "bytes=" + where + "-"); // 断点代码
                }
                connection.setRequestProperty("Connection", "Keep-Alive");
                randomAccessFile = new FileOutputStream(filePathTmp,true);
//                if (where > 0) {
//                    randomAccessFile.seek(where); // 移动到哪里开始写
//                }
                is = connection.getInputStream();
                byte[] buffer = new byte[8192];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, length);
                    progress += length;
                    if (uiHandler != null) {
                        Message message = uiHandler.obtainMessage();
                        message.what = 1000;
                        message.obj = progress + "";
                        uiHandler.sendMessage(message);
                    }
                }
                //下载完成
                new File(filePathTmp).renameTo(new File(filePath)); // 重命名
                // 删除文件
                new File(filePathTmp).delete();
                if (uiHandler != null) {
                    Message message = uiHandler.obtainMessage(2000);//通知成功
                    message.obj = info;
                    uiHandler.sendMessage(message);
                }
            } catch (Exception e) {
                if (uiHandler != null) {
                    Message message = uiHandler.obtainMessage(3000);//通知成功
                    message.obj = info;
                    uiHandler.sendMessage(message);
                }
                try {
                    // fos.close();
                    if (is != null) {
                        is.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            } finally {
                DOWNLOAD_INFOS.remove(info);
            }


        }
    }

    public List<ADBean> getDownloadInfos() {
        return DOWNLOAD_INFOS;
    }

    /*
    * 返回-1表示不在下载列表里面
    * */
    public int getDownloadStatue(ADBean adBean) {

        for (int i = 0; i < DOWNLOAD_INFOS.size(); i++) {
            if (DOWNLOAD_INFOS.get(i).getAd_packagename().equals(adBean.getAd_packagename())) {
                return DOWNLOAD_INFOS.get(i).getAd_doload_statue();
            }
        }
        return -1;
    }

    public boolean addDownload(ADBean info) {
        if (EXECUTOR_SERVICE == null) {
            EXECUTOR_SERVICE = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        }
        boolean re = true;
        for (int i = 0; i < DOWNLOAD_INFOS.size(); i++) {
            if (DOWNLOAD_INFOS.get(i).getAd_packagename().equals(info.getAd_packagename())) {
                re = false;
            }
        }
        if (re) {
            DOWNLOAD_INFOS.add(info);
            Command cmd = new Command(info);
//			commonCache.put(info.getVid(), cmd);
            EXECUTOR_SERVICE.submit(cmd); // 添加一个下载
        }
        return re;
    }


    public boolean removeDownload(ADBean info) {
        return false;
    }

    public Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000: // 更新数据
                    System.out.println(String.format("下载进度:%s", (String) msg.obj));
                    break;
                case 2000: // 更新界面
                {
                    ADBean info = (ADBean) msg.obj;
                    String filePath = baseDir + info.getAd_packagename() + "_" + info.getAd_versioncode() + ".apk";
                    PackageUtil.installNormal(context, filePath);
                }
                    break;
                case 3000: // 出了问题了
                {
                    ADBean info = (ADBean) msg.obj;
                    Toast.makeText(context, info.getAd_name()+"下载失败", Toast.LENGTH_SHORT).show();
                }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public boolean stopDownload(ADBean info) {
        for (int i = 0; i < DOWNLOAD_INFOS.size(); i++) {
            if (DOWNLOAD_INFOS.get(i).getAd_packagename().equals(info.getAd_packagename())) {

            }
        }
        handlerNotify();
        return true;
    }


    public void stopAll() {
        for (int i = 0; i < DOWNLOAD_INFOS.size(); i++) {

        }
        if (EXECUTOR_SERVICE != null) {
            EXECUTOR_SERVICE.shutdownNow(); // shutdownNow 后,线程池不可以使用了,必须new一个
            EXECUTOR_SERVICE = null;
        }
        handlerNotify();
    }


    /**
     * 通知更新
     */
    private void handlerNotify() {
        if (uiHandler != null) { // 通知主线程更新
            Message message = uiHandler.obtainMessage();
            message.what = 1000; //
            uiHandler.sendMessage(message);
        }
    }

}
