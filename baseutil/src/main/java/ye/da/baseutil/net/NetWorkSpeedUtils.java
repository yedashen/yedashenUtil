package ye.da.baseutil.net;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import ye.da.baseutil.InitCtx;
import ye.da.baseutil.log.YeLogger;

/**
 * create by ChenYe on 2020/3/9 0009 16:31
 * email:1226949796@qq.com
 * 网速工具类:这个显示的网速是当前应用的网速
 * todo 必须在不用的时候或者是界面关闭的时候 调用stopGetNetSpeed
 */
public class NetWorkSpeedUtils {

    private Handler mHandler;
    private long lastTotalRxBytes = 0, lastTimeStamp = 0;
    public static final int WHAT_SPEED = 100;
    private static final String TAG = "NetWorkSpeedUtils";
    private Timer mTimer = null;
    private TimerTask mTimeTask = new TimerTask() {
        @Override
        public void run() {
            YeLogger.e(TAG, "正在测试网速");
            long nowTotalRxBytes = TrafficStats.getUidRxBytes(InitCtx.getInstance().getCtx().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
            long nowTimeStamp = System.currentTimeMillis();
            long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
            long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 % (nowTimeStamp - lastTimeStamp));//毫秒转换

            lastTimeStamp = nowTimeStamp;
            lastTotalRxBytes = nowTotalRxBytes;

            Message msg = mHandler.obtainMessage();
            msg.what = WHAT_SPEED;
            msg.obj = speed + "." + speed2 + " kb/s";
            mHandler.sendMessage(msg);
        }
    };

    public NetWorkSpeedUtils(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public NetWorkSpeedUtils startShowNetSpeed() {
        lastTotalRxBytes = TrafficStats.getUidRxBytes(InitCtx.getInstance().getCtx().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
        lastTimeStamp = System.currentTimeMillis();
        // 1s后启动任务，每1s执行一次
        mTimer = new Timer();
        mTimer.schedule(mTimeTask, 1000, 1000);
        return this;
    }

    public void stopGetNetSpeed() {
        if (null != mTimer) {
            mTimer.cancel();
        }
        YeLogger.e(TAG, "停止测速");
    }
}
