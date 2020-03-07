package ye.da.baseutil.device;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ye.da.baseutil.InitCtx;
import ye.da.baseutil.log.YeLogger;
import ye.da.baseutil.sp.YeSpUtil;


/**
 * @author ChenYe created by on 2019/4/1 0001. 09:18
 *         <p>
 *         在登录的时候要求必须给获取手机id的权限(Manifest.permission.READ_PHONE_STATE)。
 *         <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 *         mac地址, androidId ，生成的UUid，手机名称,手机版本，登录人工号，当前使用版本号，版本code，安装时间,登录时间
 *         厂商定制系统的Bug：不同的设备可能会产生相同的ANDROID_ID：9774d56d682e549c。
 *         <p>
 *         组合标识：安装时间和生成的uuid 是一样的代表是组合标识,一个组合标识代表一个安装量（如果后台按照我这个逻辑判断，这张表有多少条数据就代表有多少安装量）
 *         如果最优先考虑
 *         （1）mac地址不为空 并且 mac 地址不为02:00:00:00:00:02，那么mac一样的为同一台手机。
 *         (2)androidId不为空，并且androidId 不为 9774d56d682e549c,那么androidId 一样的代表是同一台手机。
 *         （3）那么生成的UUid 和 手机Name 一样的代表同一台手机。
 *         <p>
 *         在确定同一台手机的基础上跟 组合标识 进行关联，那么组合标书数量代表在同一台设备安装次数
 **/

public class DeviceUtil {

    private static final String MAC_ID = "macId", ANDROID_ID = "androidId", INSTALL_ID = "installId",
            INSTALL_TIME = "installTime", PHONE_NAME = "phoneName";
    private static final String TAG = "DeviceUtil";

    /**
     * 创建安装id 和 安装时间,在设备启动的时候掉
     */
    public static void createInstallMsg() {
        Context context = InitCtx.getInstance().getCtx();
        boolean contains = YeSpUtil.getInstance().contains(INSTALL_ID);
        if (!contains) {
            YeSpUtil.getInstance().put(INSTALL_ID, UUID.randomUUID().toString());
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                YeSpUtil.getInstance().put(INSTALL_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(packageInfo.firstInstallTime)));
            } catch (Exception e) {
                YeSpUtil.getInstance().put(INSTALL_TIME, "获取安装时间失败");
            }
        }
    }

    /**
     * 在登录的时候，将mac地址, androidId ,DeviceUuid，生成的UUid，手机名称,登录人工号，当前使用版本号，版本code，安装时间,登录时间
     * 等信息传给后台，然后后台自行判断是插入数据还是更新数据
     * <p>
     * 提示：在点击登录的时候，让用户必须给予一些权限
     */
    public static Map<String, String> createIdMsgMap() {
        createIdMsg();

        Map<String, String> map = new HashMap<>(12);
        map.put("install.installUuid", (String) YeSpUtil.getInstance().get(INSTALL_ID, ""));
        map.put("install.installDate", (String) YeSpUtil.getInstance().get(INSTALL_TIME, ""));
        map.put("install.installMac", (String) YeSpUtil.getInstance().get(MAC_ID, ""));
        map.put("install.installAndroidId", (String) YeSpUtil.getInstance().get(ANDROID_ID, ""));
        map.put("install.installPhone", (String) YeSpUtil.getInstance().get(PHONE_NAME, ""));
        map.put("install.installEdition", saveSys());
        return map;
    }

    /**
     * 在登录的时候，将mac地址, androidId ,DeviceUuid，生成的UUid，手机名称,登录人工号，当前使用版本号，版本code，安装时间,登录时间
     * 等信息传给后台，然后后台自行判断是插入数据还是更新数据
     * <p>
     * 提示：在点击登录的时候，让用户必须给予一些权限
     */
    public static void createIdMsg() {
        Context context = InitCtx.getInstance().getCtx();
        boolean contains = YeSpUtil.getInstance().contains(MAC_ID);
        if (!contains) {
            saveMacId(context);
            saveAndroidId(context);
            savePhoneNameAndVersion(context);
        }
    }

    private static void savePhoneNameAndVersion(Context context) {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String name;
        if (model.startsWith(manufacturer)) {
            name = capitalize(model);
        } else {
            name = capitalize(manufacturer) + " " + model;
        }

        YeLogger.e(TAG, "最终的手机名称是:" + name + ",厂商:" + Build.BRAND + "系统:" + Build.VERSION.RELEASE);
        YeSpUtil.getInstance().put(PHONE_NAME, name);
    }

    /**
     * 获取/保存当前手机系统版本
     */
    public static String saveSys(){
        return Build.VERSION.RELEASE;
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    /**
     * 关于：在设备首次运行的时候，系统会随机生成一64位的数字，并把这个数值以16进制保存下来，这个16进制的数字就是ANDROID_ID，
     * 但是如果手机恢复出厂设置这个值会发生改变。
     * <p>
     * 弊端
     * 1、手机恢复出厂设置以后该值会发生变化
     * 2、在国内Android定制的大环境下，有些设备是不会返回ANDROID_ID的
     *
     * @param context
     */
    private static void saveAndroidId(Context context) {
        String androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        Log.e(TAG, "最终的androidId是:" + androidId);
        YeSpUtil.getInstance().put(ANDROID_ID, androidId);
    }

    /**
     * 需要配置但不用单独申请 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     * <p>
     * 关于:Mac 指的就是我们设备网卡的唯一设别码，该码全球唯一，一般称为物理地址，硬件地址用来定义设备的位置
     * <p>
     * 弊端:
     * 1、如果使用Mac地址最重要的一点就是手机必须具有上网功能，
     * 2、在Android6.0以后 google 为了运行时权限对geMacAddress();作出修改通过该方法得到的mac地址永远是一样的， 但是可以其他途径获取
     */
    private static void saveMacId(Context context) {
        String macAddress;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                macAddress = "02:00:00:00:00:02";
            } else {
                byte[] addr = networkInterface.getHardwareAddress();
                for (byte b : addr) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                macAddress = buf.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
            macAddress = "02:00:00:00:00:02";
        }

        Log.e(TAG, "最终的mac地址是:" + macAddress);
        YeSpUtil.getInstance().put(MAC_ID, macAddress);
    }

    /**
     * 需要配置但不用单独申请 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     * <p>
     * 关于:Mac 指的就是我们设备网卡的唯一设别码，该码全球唯一，一般称为物理地址，硬件地址用来定义设备的位置
     * <p>
     * 弊端:
     * 1、如果使用Mac地址最重要的一点就是手机必须具有上网功能，
     * 2、在Android6.0以后 google 为了运行时权限对geMacAddress();作出修改通过该方法得到的mac地址永远是一样的， 但是可以其他途径获取
     */
    public static void saveMacId() {
        String macAddress;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                macAddress = "02:00:00:00:00:02";
            } else {
                byte[] addr = networkInterface.getHardwareAddress();
                for (byte b : addr) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                macAddress = buf.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
            macAddress = "02:00:00:00:00:02";
        }

        Log.e(TAG, "最终的mac地址是:" + macAddress);
        YeSpUtil.getInstance().put(MAC_ID, macAddress);
    }

    /**
     * 获取app上次更新时间
     *
     * @return
     */
    public static String getLastUpdateTime() {
        Context context = InitCtx.getInstance().getCtx();
        String lastUpdateTime = "未更新过";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(packageInfo.lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            lastUpdateTime = "获取上次更新时间失败";
        }
        return lastUpdateTime;
    }

    /**
     * 获取app versionName
     *
     * @return
     */
    public static String getVersionName() {
        Context context = InitCtx.getInstance().getCtx();
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            name = "获取失败";
        }

        return name;
    }

    /**
     * 获取app versionCode
     *
     * @return
     */
    public static String getVersionCode() {
        Context context = InitCtx.getInstance().getCtx();
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (Exception e) {
            code = 999;
        }
        return String.valueOf(code);
    }
}
