package ye.da.shen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import ye.da.baseutil.progress.NumberProgressBar;
import ye.da.shen.base.BaseActivity;

public class ProgressActivity extends BaseActivity {

    public static void newInstance(Activity activity) {
        activity.startActivity(new Intent(activity, ProgressActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        final NumberProgressBar bnp = (NumberProgressBar) findViewById(R.id.numberbar1);
        final NumberProgressBar bnp2 = (NumberProgressBar) findViewById(R.id.numberbar2);
        final NumberProgressBar bnp3 = (NumberProgressBar) findViewById(R.id.numberbar3);
        final NumberProgressBar bnp4 = (NumberProgressBar) findViewById(R.id.numberbar4);
        final NumberProgressBar bnp5 = (NumberProgressBar) findViewById(R.id.numberbar5);
        final NumberProgressBar bnp6 = (NumberProgressBar) findViewById(R.id.numberbar6);
        bnp.setReachedBarHeight(15);
        bnp.setUnreachedBarHeight(15);
        bnp.setProgressIsAbove(10,2);
        bnp.setProgressTextColor(Color.parseColor("#333333"));
        findViewById(R.id.xnr_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bnp.startAnim(ProgressActivity.this, 90, 30, 300);
            }
        });

        bnp2.setReachedBarHeight(15);
        bnp2.setUnreachedBarHeight(15);
        bnp2.setRadius(8f,8f);
        bnp2.setProgressIsAbove(10,2);
        bnp2.setProgressTextColor(Color.parseColor("#333333"));
        findViewById(R.id.xr_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bnp2.startAnim(ProgressActivity.this, 90, 30, 300);
            }
        });

        bnp3.setReachedBarHeight(15);
        bnp3.setUnreachedBarHeight(15);
        bnp3.setProgressTextColor(Color.parseColor("#333333"));
        bnp3.setRadius(8f,8f,10,2);
        findViewById(R.id.by_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bnp3.startAnim(ProgressActivity.this, 90, 30, 300);
            }
        });

        bnp4.setReachedBarHeight(15);
        bnp4.setUnreachedBarHeight(15);
        bnp4.setProgressTextColor(Color.parseColor("#333333"));
        findViewById(R.id.bb_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bnp4.startAnim(ProgressActivity.this, 90, 30, 300);
            }
        });

        bnp5.setProgressTextVisibility(NumberProgressBar.ProgressTextVisibility.Invisible);
        bnp5.setRadius(8f,8f,0,2);
        bnp5.setReachedBarHeight(15);
        bnp5.setUnreachedBarHeight(15);
        bnp5.setProgressTextColor(Color.parseColor("#333333"));
        findViewById(R.id.np_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bnp5.startAnim(ProgressActivity.this, 90, 30, 300);
            }
        });

        bnp6.setProgressTextVisibility(NumberProgressBar.ProgressTextVisibility.Invisible);
        bnp6.setReachedBarHeight(15);
        bnp6.setUnreachedBarHeight(15);
        findViewById(R.id.npn_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bnp6.startAnim(ProgressActivity.this, 90, 30, 300);
            }
        });
    }
}
