package ye.da.baseutil.file;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import ye.da.baseutil.InitCtx;
import ye.da.baseutil.toast.ToastUtil;


/**
 * @author ChenYe created by on 2019/1/15 0015. 15:20
 * 这个类其实是按照你传入的各个参数，来生成指向某个路径的file对象或找到指定路径的文件对象，然后你拿着这个对象一顿操作。
 * (1)请看清楚每个方法的备注。
 **/

public class FileFindUtil {

    private static final String TAG = "FileFindUtil";

    /**
     * 写文件到内部存储，app卸载的时候内存存储都会自动卸载掉,访问这些数据一般不需要权限,并且别的app和用户(不root)都无法
     * 查看到这些文件,并且不允许在app内部存储里面创建文件夹
     * （1）缓存文件,地址一般在/data/data/package_name/cache （缓存一些json和小文件用的）
     * (2)文件,地址一般在/data/data/package_name/files (存放小文件)
     *
     * @param type 1 是cache 2是file
     * @param name 文件名(可不传,但是如果传的话一定要自带后缀,比如你传test.txt，后面的txt不能掉)
     * @return file 返回一个file，让别人自由发挥
     */
    public static File findFile2Internal(int type, String name) {
        Context ctx = InitCtx.getInstance().getCtx();
        File file1 = new File(type == 2 ? ctx.getFilesDir() : ctx.getCacheDir(), checkFileName(name));
        Log.e(TAG, "创建的文件地址是:" + file1.getAbsolutePath());
        return file1;
    }

    private static String checkFileName(String name) {
        if (TextUtils.isEmpty(name)) {
            //默认用txt后缀了
            return getFileNameTime() + ".txt";
        } else {
            return name;
        }
    }

    /**
     * 文件名尽量不要带有冒号，不然用华为手机助手导出的时候会遇到问题。
     *
     * @return
     */
    public static String getFileNameTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return UUID.randomUUID().toString() + "_" + formatter.format(curDate);
    }

    /**
     * 存在外部存储的私密存储，别的app不会访问到，会随着app的删除一起删除,用户看的见并且可以操作
     * /storage/emulated/0/Android/data/package_name/cache/文件 （缓存照片，文档等大文件用的，用手机助手清缓存，是会将这个清除掉）
     * /storage/emulated/0/Android/data/package_name/files/文件夹名称/文件 （存放大文件）
     * /storage/emulated/0/Android/data/package_name/files/Pictures/test.png
     * //TODO 为了将业务抽离，这里不帮你进行SD卡是否挂载处理（你可以调 isExternalStorageWritable()自己判断）
     * //TODO ，也不帮你做权限判断（你可以继承BasePermissionActivity 进行权限申请和判断）
     * (1)调用外部存储的话需要先去判断sd卡是否挂载;
     * (2)如果系统版本超过6.0(sdk = 23)需要判断用户是否给予了读写权限
     *
     * @param type 1 是cache， 2 是file
     * @param name 文件名(可不传,但是如果传的话一定要自带后缀,比如你传test.txt，后面的txt不能掉)
     * @param dir  当type 为 1 的时候，这个值传了也没用；当type为2 的时候，dir就是文件夹名称，可为空
     *             ,传的时候最好是用Environment. ，比如Environment.DIRECTORY_PICTURE，用Environment.命名能被系统正确对待
     * @return TODO file 记得自己判断非空再使用，因为你看我代码，如果没有权限、SD卡没有挂载的情况下返回的是null，尤其是没有权限！！！自己看
     * <p>
     * 用法如下:
     * File file = FileFindUtil.findFile2ExternalInternal(this, 2, "", "", "", 20);
     * if (null != file) {//再操作} else {CyLogger.e(TAG, "file == null，应该是没有权限或者是SD卡未挂载");}
     */
    public static File findFile2ExternalInternal(int type, String name, String dir) {
        Context ctx = InitCtx.getInstance().getCtx();
        if (type == 1) {
            if (null != ctx.getExternalCacheDir()) {
                return new File(ctx.getExternalCacheDir().getAbsolutePath(), checkFileName(name));
            } else {
                Log.e(TAG, "null == context.getExternalCacheDir()");
                return null;
            }
        } else {
            File file = new File(ctx.getExternalFilesDir(dir), checkFileName(name));
            Log.e(TAG, "file的地址是:" + file.getAbsolutePath());
            return file;
        }
    }

    /**
     * 写文件到外部存储，app卸载的时候外部存储的文件不会自动卸载掉，访问这些数据一般需要权限。比如下载的照片，文档
     * Environment.getExternalStorageDirectory() 获取到的其实是外部存储的根目录
     * Environment.getExternalStoragePublicDirectory() 获取到得则是外部存储的公共目录
     * //TODO 为了将业务抽离，这里不帮你进行SD卡是否挂载处理（你可以调 isExternalStorageWritable()自己判断）
     * //TODO ，也不帮你做权限判断（你可以继承BasePermissionActivity 进行权限申请和判断）
     *
     * @param type          1 是你创建的文件在根目录下， 2 是你创建的文件在根目录的公共文件夹下
     * @param publicDirName 当type 为 1 的时候，publicDirName不允为中文；
     *                      当type 为 2 的时候，值最好是用Environment.DIRECTORY_PICTURES这样类似的传值,如果
     *                      你的type是1，那么这个值可以为空
     * @param dirName       你自己创建的文件放在你创建的文件夹下,我这里做强制要求不允许为空
     * @param fileName      文件名(可不传,但是如果传的话一定要自带后缀,比如你传test.txt，后面的txt不能掉)
     *                      用法如下:
     *                      File file = FileFindUtil.findFile2External(this, 2, "", "", "", 20);
     *                      if (null != file) {//再操作} else {CyLogger.e(TAG, "file == null，创建file为null");}
     */

    public static File findFile2External(int type, String publicDirName, String dirName, String fileName) {
        File dir;
        if (type == 1) {
            String parent = Environment.getExternalStorageDirectory().getAbsolutePath() + (TextUtils.isEmpty(publicDirName) ? "" : "/" + publicDirName);
            dir = new File(parent, dirName);
        } else {
            if (!TextUtils.isEmpty(publicDirName)) {
                if (!TextUtils.isEmpty(dirName)) {
                    dir = new File(Environment.getExternalStoragePublicDirectory(publicDirName), dirName);
                } else {
                    throw new RuntimeException("你看清楚注释再调这个方法!");
                }
            } else {
                throw new RuntimeException("你看清楚注释再调这个方法!");
            }
        }

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                ToastUtil.newInstance().showToast("无法读写文件!");
                return null;
            }
        }

        File mediaFile = new File(dir + File.separator + checkFileName(fileName));
        Log.e(TAG, "file的地址是:" + mediaFile.getAbsolutePath());
        return mediaFile;
    }

    /**
     * 检查外部存储器是否挂载了,挂载了才可以读写的(6.0及以上需要还要申请权限)
     *
     * @return true代表已挂载
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
