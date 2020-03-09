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
import ye.da.baseutil.file.FileFindUtil;
import ye.da.baseutil.net.NetWorkSpeedUtils;
import ye.da.baseutil.toast.ToastUtil;
import ye.da.shen.base.BasePermissionActivity;

/**
 * 下载APK（带进度、带网速显示）
 */
public class DownloadFileActivity extends BasePermissionActivity {

    private TextView mNetSpeedTv, mProgressTv;
    private NetWorkSpeedUtils mNetSpeedUtil = null;
    private DownloadHelper mDownloadHelper;
    private NetHandler mHandler;

    public static void newInstance(Activity activity) {
        activity.startActivity(new Intent(activity, DownloadFileActivity.class));
    }

    @Override
    public void requestResult(List<String> grantedList, List<String> deniedList, int requestCode) {
        if (deniedList.isEmpty()) {
            mNetSpeedTv.setText("网速:");
            mProgressTv.setText("进度:");
            mDownloadHelper.beginDownload(mHandler, "http://183.134.216.90:18081/pwgcjd/upload/downloadApk?url=app-release(1.0.4).apk", FileFindUtil.findFile2External(1,"yedashen","apk","yeda.apk").getAbsolutePath(), mNetSpeedUtil);
        } else {
            ToastUtil.newInstance().showToast("没有读写SD卡权限，无法开始下载");
        }
    }

    private static class NetHandler extends Handler {

        private final WeakReference<DownloadFileActivity> mActivity;

        public NetHandler(DownloadFileActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DownloadFileActivity activity = mActivity.get();
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
        setContentView(R.layout.activity_download_file);
        mNetSpeedTv = findViewById(R.id.tv_net_speed);
        mProgressTv = findViewById(R.id.tv_progress);
        mHandler = new NetHandler(DownloadFileActivity.this);
        mDownloadHelper = new DownloadHelper();
        mNetSpeedUtil = new NetWorkSpeedUtils(mHandler);
        findViewById(R.id.begin_download).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRequest(2, "请给予读写SD卡权限", Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });

        findViewById(R.id.end_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mNetSpeedUtil) {
                    mNetSpeedUtil.stopGetNetSpeed();
                }
                mDownloadHelper.stopDownload();
            }
        });
    }

    public void updateNetSpeed(String netSpeed) {
        mNetSpeedTv.setText("网速:" + netSpeed);
    }

    public void updateProgress(String progress) {
        mProgressTv.setText("进度:" + progress);
    }

    public void downloadFinish(boolean isSuccess) {
        if (isSuccess) {
            mProgressTv.setText("下载完毕");
        } else {
            mProgressTv.setText("下载出错");
        }
        if (null != mNetSpeedUtil) {
            mNetSpeedUtil.stopGetNetSpeed();
            mNetSpeedTv.setText("已停止网速监听");
        }
    }
}
