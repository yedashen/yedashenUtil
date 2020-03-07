package ye.da.baseutil.map;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.net.URISyntaxException;

import ye.da.baseutil.InitCtx;
import ye.da.baseutil.log.YeLogger;
import ye.da.baseutil.toast.ToastUtil;

import static java.lang.Double.parseDouble;

/**
 * @author ChenYe created by on 2019/6/17 0017. 15:06
 * 使用本工具记得跳转之前，请调用checkApkExist判断当前手机是否安装了对应地图软件
 **/

public class JumpMapUtil {

    public static String BAI_DU_URI = "com.baidu.BaiduMap";
    public static String GAO_DE_URI = "com.autonavi.minimap";
    private static final String TAG = "JumpMapUtil";

    /**
     * 判断地图APP是否存在
     *
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(String packageName) {
        try {
            ApplicationInfo info = InitCtx.getInstance().getCtx().getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 跳转到高德地图
     *
     * @param lat     纬度
     * @param lon     经度
     * @param address 地址
     */
    public static void goToGaode(String lat, String lon, String address) {
        try {
            double x = parseDouble(lon) - 0.0065, y = parseDouble(lat) - 0.006;
            double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
            double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
            //longitude:120.118881,latitude:30.291784
            YeLogger.e(TAG, "z * sin(theta):" + z * Math.sin(theta) + ",z * cos(theta):" + z * Math.cos(theta));
            Uri uri = Uri.parse(new StringBuilder("amapuri://route/plan/?")
                    .append("&dlat=").append(z * Math.sin(theta))
                    .append("&dlon=").append(z * Math.cos(theta))
                    .append("&dname=").append(address)
                    .append("&dev=").append(0)
                    .append("&t=").append(0).toString());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            InitCtx.getInstance().getCtx().startActivity(intent);
        } catch (Exception e) {
            ToastUtil.newInstance().showToast("跳转高德地图失败!");
        }
    }


    /**
     * 跳转到百度地图
     *
     * @param latLng  latlng:39.9761,116.3282|name:中关村 (注意：坐标先纬度，后经度)
     * @param address 地址
     */
    public static void goToBaidu(String latLng, String address) {
        Intent intent;
        try {
            intent = Intent.getIntent("intent://map/direction?destination=latlng:" + latLng + "|name:" + address + "&mode=driving&src=#Intent;" + "scheme=bdapp;package=com.baidu.BaiduMap;end");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            InitCtx.getInstance().getCtx().startActivity(intent);
        } catch (URISyntaxException e) {
            ToastUtil.newInstance().showToast("跳转到百度地图APP失败");
        }
    }
}
