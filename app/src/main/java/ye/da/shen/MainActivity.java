package ye.da.shen;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ye.da.baseutil.InitCtx;
import ye.da.baseutil.sp.YeSpUtil;

/**
 * @author ChenYe
 */
public class MainActivity extends AppCompatActivity {

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
        YeSpUtil.getInstance().clear();
    }
}
