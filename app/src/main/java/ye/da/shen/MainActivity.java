package ye.da.shen;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import ye.da.baseutil.InitCtx;
import ye.da.baseutil.log.YeLogger;
import ye.da.shen.base.BaseActivity;

/**
 * @author ChenYe
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitCtx.getInstance().setContextListener(new InitCtx.ContextListener() {
            @Override
            public Context getContext() {
                return MainActivity.this.getApplicationContext();
            }
        });
        YeLogger.LOG_ENABLE = true;
        findViewById(R.id.download_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFileActivity.newInstance(MainActivity.this);
            }
        });
        findViewById(R.id.update_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdateActivity.newInstance(MainActivity.this);
            }
        });
        findViewById(R.id.progress_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressActivity.newInstance(MainActivity.this);
            }
        });
        findViewById(R.id.dance_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DanceTvActivity.newInstance(MainActivity.this);
            }
        });
    }
}
