package ye.da.baseutil.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ye.da.baseutil.file.FileFindUtil;
import ye.da.baseutil.log.YeLogger;


/**
 * @author ChenYe
 */

public class CrashHelper implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHelper";

    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * 用来保存崩溃日志的文件夹路径
     */
    private String mSaveDir = "", mUserAccount = "", mUserName = "";

    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * 用来存储设备信息和异常信息
     */
    private Map<String, String> infos = new HashMap<>();

    private static final class CrashHolder {
        private static final CrashHelper INSTANCE = new CrashHelper();
    }

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHelper() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHelper getInstance() {
        return CrashHolder.INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     * @param saveDir 保存崩溃日志的文件夹
     */
    public void init(Context context, String saveDir) {
        mContext = context;
        mSaveDir = saveDir;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 传入当前使用账户信息,你也可以在上传崩溃日志的时候将这些信息写到json里面去
     */
    public void setAccountInfo(String userName, String userAccount) {
        mUserName = userName;
        mUserAccount = userAccount;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        boolean permission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;
        if (!permission) {
            saveCrashInfo2File(ex);
        } else {
            //这里就不帮你申请了
            YeLogger.e(TAG, "没有读写Sd卡权限");
        }
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
                infos.put("crashTime", new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA).format(new Date(System.currentTimeMillis())));
                infos.put("crashUser", TextUtils.isEmpty(mUserName) ? "暂无登录" : mUserName);
                infos.put("crashUserAccount", TextUtils.isEmpty(mUserAccount) ? "暂无登录" : mUserAccount);
            }
        } catch (NameNotFoundException e) {
            YeLogger.e(TAG, "an error occured when collect package info");
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                YeLogger.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                YeLogger.e(TAG, "an error occured when collect crash info");
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private void saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            File crashFile = FileFindUtil.findFile2ExternalInternal(2, "", mSaveDir);
            if (null != crashFile) {
                YeLogger.e(TAG, "crash文件地址:" + crashFile.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(crashFile);
                fos.write(sb.toString().getBytes());
                fos.close();
            } else {
                YeLogger.e(TAG, "生产的crash 文件为null");
            }
        } catch (Exception e) {
            YeLogger.e(TAG, "an error occured while writing file...");
        }
    }
}