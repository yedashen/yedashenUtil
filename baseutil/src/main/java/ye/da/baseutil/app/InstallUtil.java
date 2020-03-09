package ye.da.baseutil.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

import ye.da.baseutil.InitCtx;
import ye.da.baseutil.dialog.CommonAlertDialog;
import ye.da.baseutil.log.YeLogger;
import ye.da.baseutil.toast.ToastUtil;

/**
 * @author ChenYe
 * 介绍：本工具可自定义场景安装
 * todo （1）从android7.0之后安装需要配置xml。（2）从android8.0之后安装需要配置权限。android.permission.REQUEST_INSTALL_PACKAGES
 * 可选择是否弹出安装提示框
 * （1）如果弹出，用户点击安装才安装。
 * （2）不弹出：自动安装
 **/

public class InstallUtil {

    /**
     * 防止出现连续重复调用情况
     */
    private static boolean isInstalling = false;
    private static final String TAG = "InstallUtil";

    public static void beginInstall(boolean isShowAlert, final Activity activity, final String path) {
        beginInstall(isShowAlert,activity,new File(path));
    }

    /**
     * @param isShowAlert true代表弹出选择框,false不弹出，自动安装
     */
    public static void beginInstall(boolean isShowAlert, final Activity activity, final File file) {
        if (!file.exists()) {
            ToastUtil.newInstance().showToast("文件不存在/已损坏");
            return;
        }

        if (isInstalling) {
            YeLogger.e(TAG, "已经正在安装了");
            return;
        }

        isInstalling = true;

        if (isShowAlert) {
            AlertDialog dialog = CommonAlertDialog.showCommonDialog(activity, "可以安装啦，立马安装?", "安装", "取消", new CommonAlertDialog.CommonDialogClickListener() {
                @Override
                public void positiveClick() {
                    installApk(activity, file);
                    System.exit(0);
                }

                @Override
                public void negativeClick() {
                    ToastUtil.newInstance().showToast("已取消");
                }

                @Override
                public void cantShow() {
                    installApk(activity, file);
                    System.exit(0);
                }
            });
        } else {
            installApk(activity, file);
        }
    }


    public static void installApk(Activity activity, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(activity, InitCtx.getInstance().getCtx().getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        isInstalling = false;
        activity.startActivity(intent);
    }
}
