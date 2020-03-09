package ye.da.shen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import ye.da.baseutil.app.DownloadHelper;
import ye.da.baseutil.app.InstallUtil;
import ye.da.baseutil.app.UpdateHelper;
import ye.da.baseutil.net.NetWorkSpeedUtils;
import ye.da.baseutil.toast.ToastUtil;
import ye.da.shen.base.BasePermissionActivity;

/**
 * 其实跟DownloadFileActivity 业务差不多
 */
public class AppUpdateActivity extends BasePermissionActivity {

    private TextView mNetSpeedTv, mProgressTv;
    private NetHandler mHandler;
    private NetWorkSpeedUtils mNetUtil;

    public static void newInstance(Activity activity) {
        activity.startActivity(new Intent(activity, AppUpdateActivity.class));
    }

    @Override
    public void requestResult(List<String> grantedList, List<String> deniedList, int requestCode) {
        if (deniedList.isEmpty()) {
            UpdateHelper.autoUpdate(true, 4, mHandler, "", "", mNetUtil);
        } else {
            ToastUtil.newInstance().showToast("没有读写SD卡权限，无法开始下载");
        }
    }

    private static class NetHandler extends Handler {

        private final WeakReference<AppUpdateActivity> mActivity;

        public NetHandler(AppUpdateActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppUpdateActivity activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    case NetWorkSpeedUtils.WHAT_SPEED:
                        activity.updateNetSpeed(msg.obj.toString());
                        break;
                    case DownloadHelper.DOWN_UPDATE:
                        activity.updateProgress(msg.obj.toString());
                        break;
                    case DownloadHelper.DOWN_OVER:
                        activity.downloadFinish(true);
                        break;
                    case DownloadHelper.DOWN_ERROR:
                        activity.downloadFinish(false);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);
        mNetSpeedTv = findViewById(R.id.tv_net_speed);
        mProgressTv = findViewById(R.id.tv_progress);
        mHandler = new NetHandler(this);
        mNetUtil = new NetWorkSpeedUtils(mHandler);
        findViewById(R.id.begin_download).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRequest(3, "请给予读写SD卡权限", Manifest.permission_group.STORAGE);
            }
        });
    }

    public void updateNetSpeed(String netSpeed) {
        mNetSpeedTv.setText("网速:" + netSpeed);
    }

    public void updateProgress(String netSpeed) {
        mProgressTv.setText("进度:" + netSpeed);
    }

    public void downloadFinish(boolean isSuccess) {
        if (isSuccess) {
            mProgressTv.setText("下载完毕");
            InstallUtil.beginInstall(true, this, "");
        } else {
            mProgressTv.setText("下载出错");
        }
    }
}
