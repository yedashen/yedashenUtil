package ye.da.shen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ye.da.baseutil.textview.DancingNumberView;
import ye.da.shen.base.BaseActivity;

public class DanceTvActivity extends BaseActivity {

    public static void newInstance(Activity activity) {
        activity.startActivity(new Intent(activity, DanceTvActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance_tv);
        final DancingNumberView dancingNumberView = ((DancingNumberView) findViewById(R.id.dance_tv));
        dancingNumberView.setText("9124.657");
        dancingNumberView.setDuration(300);
        dancingNumberView.setFormat("%.2f");
        findViewById(R.id.start_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dancingNumberView.dance();
            }
        });
    }
}
