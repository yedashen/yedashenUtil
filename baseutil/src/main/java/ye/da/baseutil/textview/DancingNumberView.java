package ye.da.baseutil.textview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ye.da.baseutil.R;

/**
 * create by ChenYe on 2020/3/11 0011 20:25
 * email:1226949796@qq.com
 * 是从github上拷贝过来的：https://github.com/JianxunRao/DancingNumberView
 */

public class DancingNumberView extends TextView {

    private static final String TAG = "DancingNumberView";
    public static final String PLACEHOLDER = "@@@";

    /**
     * 从数字开始跳动到结束跳动显示原值的持续时间,单位是ms
     */
    private int duration;
    /**
     * 跳动时数字显示的格式
     */
    private String format = "%.0f";
    /**
     * 算数因子
     */
    private float factor;
    /**
     * 文本中数字原值
     */
    private ArrayList<Float> numbers;
    /**
     * 保存跳动数字的数组
     */
    private float[] numberTemp;
    /**
     * 文本原值
     */
    private String text;
    /**
     * 文本去除数字的样式
     */
    private String textPattern;

    public DancingNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DancingNumberView);
        duration = ta.getInteger(R.styleable.DancingNumberView_dnv_duration, 1500);
        if (ta.hasValue(R.styleable.DancingNumberView_dnv_format)) {
            format = ta.getString(R.styleable.DancingNumberView_dnv_format);
        }
        ta.recycle();
    }

    /**
     * 文本中的数字开始跳动
     */
    public void dance() {

        text = getText().toString();
        numbers = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            numbers.add(Float.parseFloat(matcher.group()));
        }
        Log.e(TAG,"numbers.size():"+numbers.size());
        textPattern = text.replaceAll("\\d+(\\.\\d+)?", PLACEHOLDER);
        numberTemp = new float[numbers.size()];

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "factor", 0, 1);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    /**
     * 获取算数因子
     *
     * @return 算数因子
     */
    public float getFactor() {
        return factor;
    }

    /**
     * 设置算数因子,为ObjectAnimator调用
     *
     * @param factor 算数因子
     * @see ObjectAnimator
     */
    public void setFactor(float factor) {
        String textNow = textPattern;
        this.factor = factor;
        for (int i = 0; i < numberTemp.length; i++) {
            numberTemp[i] = numbers.get(i) * factor;
            textNow = textNow.replaceFirst(PLACEHOLDER, String.format(format, numberTemp[i]));
        }
        setText(textNow);
    }

    /**
     * 获取跳动持续时长
     *
     * @return 持续时长 单位ms
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 设置跳动持续时长
     *
     * @param duration 持续时长 单位ms
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 获取数字显示的格式
     *
     * @return 数字显示的格式
     */
    public String getFormat() {
        return format;
    }

    /**
     * 获取数字显示的格式
     *
     * @param format 数字显示的格式
     *               "%.2f" 保留2位小数
     *               "%.0f"保留整数
     */
    public void setFormat(String format) {
        this.format = format;
    }
}
