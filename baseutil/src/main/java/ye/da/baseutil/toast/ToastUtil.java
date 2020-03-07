package ye.da.baseutil.toast;

import android.widget.Toast;

import ye.da.baseutil.InitCtx;
import ye.da.baseutil.log.YeLogger;

/**
 * @author ChenYe
 * created by on 2017/12/15 0015. 09:55
 **/

public class ToastUtil {

    private Toast mToast = null;

    private ToastUtil() {
        mToast = Toast.makeText(InitCtx.getInstance().getCtx(), "", Toast.LENGTH_SHORT);
    }

    private static class ToastHolder {
        private static final ToastUtil INSTANCE = new ToastUtil();
    }

    public static ToastUtil newInstance() {
        return ToastHolder.INSTANCE;
    }

    public void showToast(String msg) {
        if (mToast != null) {
            mToast.setText(msg);
            mToast.show();
        } else {
            YeLogger.e("ToastUtil", "mToast == null,请查看原因!!");
            Toast.makeText(InitCtx.getInstance().getCtx(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
