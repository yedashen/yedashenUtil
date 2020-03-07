package ye.da.baseutil.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import ye.da.baseutil.log.YeLogger;

/**
 * Created by Android Studio.
 * User: Administrator
 * Date: 2020/2/16
 * Time: 下午 7:54
 * desc:这个是系统原生的AlertDialog弹框封装，是为了节约调用代码
 */
public class CommonAlertDialog {

    private static final String TAG = "CommonAlertDialog";

    /**
     * @param activity 必传，并且不能只是传context
     *                 todo 解决当activity 不可见的时候显示diaolog的问题，做一个util，所有显示dialog都必须去进行判断
     */
    public static void showCommonDialog(Activity activity, String desc, String negativeMsg, String positiveMsg, final CommonDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(desc);
        builder.setPositiveButton(positiveMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.positiveClick();
            }
        });
        builder.setNegativeButton(negativeMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.negativeClick();
            }
        });
        if (!activity.isFinishing()) {
            //如果activity没有finish
            AlertDialog alertDialog = builder.show();
        } else {
            //如果activity 已经finish了或者不可见，那么就存在调用已经，需要注意
            YeLogger.e(TAG, "Dialog show 已经，因为activity 已经finish了");
        }
    }

    /**
     * @param activity 必传，并且不能只是传context
     */
    public static void showCommonDialog(Activity activity, String desc, CommonDialogClickListener listener) {
        showCommonDialog(activity, desc, "取消", "确定", listener);
    }

    /**
     * 弹出的对话框的点击事件,positive点击事件，我没将两个点击事件写到一个interface里面，
     * 因为一般来说，普通应用场景只需要右边按钮的点击事件
     */
    public interface CommonDialogClickListener {

        /**
         * （右边按钮点击事件）
         */
        void positiveClick();

        /**
         * （左边按钮点击事件）
         */
        void negativeClick();
    }
}
