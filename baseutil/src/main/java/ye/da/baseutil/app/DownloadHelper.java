package ye.da.baseutil.app;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ye.da.baseutil.log.YeLogger;
import ye.da.baseutil.net.NetWorkSpeedUtils;

/**
 * create by ChenYe on 2020/3/9 0009 15:27
 * email:1226949796@qq.com
 * 专门用来下载的
 */
public class DownloadHelper {

    public static final int DOWN_OVER = 2, DOWN_UPDATE = 3, DOWN_ERROR = 4;
    private static final String TAG = "DownloadHelper";
    private int preProgress = 0;

    /**
     * true就是停止下载了
     */
    private boolean interceptFlag = false;

    public void beginDownload(final Handler handler, final String downUrl, final String savePath) {
        beginDownload(handler, downUrl, savePath, null);
    }

    public void beginDownload(final Handler handler, final String downUrl, final String savePath, final NetWorkSpeedUtils netSpeedUtil) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    interceptFlag = false;
                    preProgress = 0;
                    URL url = new URL(downUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    YeLogger.e(TAG, "线程启动，并开始建立链接");
                    conn.setRequestMethod("GET");
                    //在指定时间内还没有连接到服务器就会报SocketTimeout异常
                    conn.setConnectTimeout(5000);
                    //是连接后在指定时间还没有获取到数据就超时
                    conn.setReadTimeout(20000);
                    conn.connect();
                    YeLogger.e(TAG, "建立链接成功");
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File localSaveFile = new File(savePath);
                    FileOutputStream fos = new FileOutputStream(localSaveFile);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    if (null != netSpeedUtil) {
                        netSpeedUtil.startShowNetSpeed();
                    }
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        int progress = (int) (((float) count / length) * 100);
                        //更新进度

                        if (numread <= 0) {
                            //下载完成通知安装
                            if (null != netSpeedUtil) {
                                netSpeedUtil.stopGetNetSpeed();
                            }
                            conn.disconnect();
                            handler.sendEmptyMessage(DOWN_OVER);
                            break;
                        } else {
                            if (preProgress == 0 || progress > preProgress + 1) {
                                preProgress = progress;
                                Message msg = handler.obtainMessage();
                                msg.what = DOWN_UPDATE;
                                msg.obj = progress + "";
                                handler.sendMessage(msg);
                            }
                        }
                        fos.write(buf, 0, numread);
                    } while (!interceptFlag);

                    fos.close();
                    is.close();
                } catch (Exception e) {
                    handler.sendEmptyMessage(DOWN_ERROR);
                    if (null != netSpeedUtil) {
                        netSpeedUtil.stopGetNetSpeed();
                    }
                    YeLogger.e(TAG, "下载出错" + e.getMessage());
                }
            }
        }).start();
    }

    public void stopDownload() {
        interceptFlag = true;
        YeLogger.e(TAG, "停止下载");
    }
}
