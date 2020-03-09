package ye.da.shen.base;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import ye.da.baseutil.log.YeLogger;

/**
 * create by ChenYe on 2020/3/9 0009 20:09
 * email:1226949796@qq.com
 * 如果你的Activity里面需要申请和处理权限，可以直接继承这个Activity。
 * 使用案例 ZgdActivity ，如果还是不清楚可以直接问我(陈业 13777373144)
 * 或者看我写的使用blog：https://blog.csdn.net/qq_34723470/article/details/86711407
 **/

public abstract class BasePermissionActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "BasePermissionActivity";
    private List<String> mGrantedList = new ArrayList<>();
    private List<String> mDeniedList = new ArrayList<>();
    /**
     * 这个是你真实的需要申请几个权限。因为如果你一次性传了五个权限进来，那么这五个里面之前就有两个已经给了权限，所以
     * 你只需要申请三个权限
     */
    private int mRequestCode = 0, mOriginalList = 0;
    private String mAlertMsg = "";

    /**
     * 先判断code 再 判断deniedList是否为空，为空代表你申请的权限给你了
     *
     * @param grantedList 已给予了的权限列表
     * @param deniedList  已拒绝的权限列表
     * @param requestCode code
     */
    public abstract void requestResult(List<String> grantedList, List<String> deniedList, int requestCode);


    /**
     * 当申请权限的时候，直接调用这个方法就行。你可以无赖的每次将你所有的权限都扔进来，然后在requestResult()回调
     * 里面进行处理，
     */
    protected void startRequest(int requestCode, String alert, String... list) {
        mRequestCode = requestCode;
        mAlertMsg = alert;
        mOriginalList = list.length;
        mGrantedList.clear();
        mDeniedList.clear();
        requestPermission(list);
    }

    private void requestPermission(String... list) {
        EasyPermissions.requestPermissions(this, mAlertMsg, mRequestCode, list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == mRequestCode) {
            dealResult(false, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == mRequestCode) {
            dealResult(true, perms);
        }
    }

    /**
     * 其实这个方法最多走两次。假如你同时申请了多个权限，然后界面会弹框多次让用户去分别给权限。然后用户操作完之后
     * 才会走拒绝和同意的回调。假如你有的权限同意了，有的权限拒绝了，那么就会先回调同意的方法onPermissionsGranted
     * ，然后再走onPermissionsDenied,那么当前的这个方法dealResult 就会走两边的。
     *
     * @param granted true代表是同意
     * @param perms   回调结果list
     */
    private void dealResult(boolean granted, List<String> perms) {
        for (String per : perms) {
            if (granted) {
                if (!mGrantedList.contains(per)) {
                    mGrantedList.add(per);
                }
            } else {
                if (!mDeniedList.contains(per)) {
                    mDeniedList.add(per);
                }
            }
        }
        if (mDeniedList.size() + mGrantedList.size() == mOriginalList) {
            requestResult(mGrantedList, mDeniedList, mRequestCode);
        } else {
            YeLogger.e(TAG, "应该是同时申请了多个权限，并且有的权限同意了，有的拒绝了，还在继续加，不用着急，最后肯定会走总和那里的");
        }
    }
}

