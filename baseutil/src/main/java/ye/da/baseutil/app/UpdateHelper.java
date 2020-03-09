package ye.da.baseutil.app;


import android.os.Handler;

import ye.da.baseutil.device.DeviceUtil;
import ye.da.baseutil.net.NetWorkSpeedUtils;
import ye.da.baseutil.toast.ToastUtil;

/**
 * create by ChenYe on 2020/3/9 0009 15:00
 * email:1226949796@qq.com
 * 其实更新可以做在多个地方：
 * （1）首页。
 * （2）设置界面。
 */
public class UpdateHelper {

    /**
     * @param isAuto         true:app启动的时候自动检查版本更新：自动更新;  false:用户到设置里面手动点击检查更新按钮：手动更新
     * @param codeFromServer 从服务器返回的最新code
     * @param handler        监听handler
     */
    public static void autoUpdate(boolean isAuto, int codeFromServer, Handler handler, String downloadUrl, String savePtah) {
        autoUpdate(isAuto, codeFromServer, handler, downloadUrl, savePtah);
    }

    /**
     * @param isAuto         true:app启动的时候自动检查版本更新：自动更新;  false:用户到设置里面手动点击检查更新按钮：手动更新
     * @param codeFromServer 从服务器返回的最新code
     * @param handler        监听handler
     */
    public static void autoUpdate(boolean isAuto, int codeFromServer, Handler handler, String downloadUrl, String savePtah, NetWorkSpeedUtils netWorkSpeedUtils) {
        if (Integer.parseInt(DeviceUtil.getVersionCode()) > codeFromServer) {
            //去下载
            new DownloadHelper().beginDownload(handler, downloadUrl, savePtah, netWorkSpeedUtils);
        } else {
            if (!isAuto) {
                ToastUtil.newInstance().showToast("当前已是最新版本!");
            }
        }
    }
}
