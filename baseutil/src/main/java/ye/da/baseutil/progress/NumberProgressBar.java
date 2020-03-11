package ye.da.baseutil.progress;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import ye.da.baseutil.R;


/**
 * 本自定义控件是在github 的一个自定义控件基础数上进行修改的，原控件地址为：https://github.com/daimajia/NumberProgressBar
 * 修改人：ChenYe
 * 这个进度条其实是左边进度矩形和数字以及右边矩形拼接的
 */
public class NumberProgressBar extends View {

    private static final String TAG = "NumberProgressBar";
    private int mMaxProgress = 100;

    /**
     * mCurrentProgress:当前进度
     * mReachedBarColor：进度条左边进度柱状图背景颜色
     * mUnreachedBarColor:进度条右边未进度柱状图背景颜色
     * mTextColor:显示进度数字的颜色
     */
    private int mCurrentProgress = 0, mReachedBarColor, mUnreachedBarColor, mTextColor;

    /**
     * mTextSize:进度数字大小（像素）,但是如果是你从xml传进来的，是sp，会自动转换成px
     * mReachedBarHeight:进度条左边进度柱状图高度
     * mUnreachedBarHeight:进度条右边未进度柱状图背景高度
     */
    private float mTextSize, mReachedBarHeight, mUnreachedBarHeight;

    /**
     * mSuffix:进度数字后缀
     * mPrefix：进度数组前缀
     */
    private String mSuffix = "%", mPrefix = "";

    /**
     * default_text_color:默认进度数字颜色
     * default_reached_color默认进度条左边进度柱状图背景颜色
     * default_unreached_color:默认进度条左边进度柱状图背景颜色
     */
    private final int default_text_color = Color.rgb(66, 145, 241), default_reached_color = Color.rgb(66, 145, 241), default_unreached_color = Color.rgb(204, 204, 204);
    private final float default_progress_text_offset;
    private final float default_text_size;
    private final float default_reached_bar_height;
    private final float default_unreached_bar_height;

    /**
     * For save and restore instance of progressbar.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_REACHED_BAR_HEIGHT = "reached_bar_height";
    private static final String INSTANCE_REACHED_BAR_COLOR = "reached_bar_color";
    private static final String INSTANCE_UNREACHED_BAR_HEIGHT = "unreached_bar_height";
    private static final String INSTANCE_UNREACHED_BAR_COLOR = "unreached_bar_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_TEXT_VISIBILITY = "text_visibility";
    private static final int PROGRESS_TEXT_VISIBLE = 0;

    /**
     * The drawn text start.
     */
    private float mDrawTextStart;

    /**
     * The drawn text end.
     */
    private float mDrawTextEnd;

    /**
     * The text that to be drawn in onDraw().
     */
    private String mCurrentDrawText;

    /**
     * The Paint of the reached area.
     */
    private Paint mReachedBarPaint;
    /**
     * The Paint of the unreached area.
     */
    private Paint mUnreachedBarPaint;
    /**
     * The Paint of the progress text.
     */
    private Paint mTextPaint;

    /**
     * Unreached bar area to draw rect.
     */
    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);
    /**
     * Reached bar area rect.
     */
    private RectF mReachedRectF = new RectF(0, 0, 0, 0);

    /**
     * The progress text offset.
     */
    private float mOffset;

    /**
     * Determine if need to draw unreached area.
     */
    private boolean mDrawUnreachedBar = true, mDrawReachedBar = true, mIfDrawText = true, isRadius = false;

    /**
     * Listener
     */
    private OnProgressBarListener mListener;

    private float mRx, mRy;

    /**
     * 是否悬浮，true是悬浮，false不悬浮，都是基于数字是显示的
     * 悬浮：左边进度跟右边进度是无缝拼接模式，数字是显示在右边进度上面，如果快100了，就显示在左边进度上面
     * 不悬浮：是左边进度、数字、右边进度 三者拼接模式
     */
    private boolean isAbove = false;

    /**
     * 因为如果左右两个矩形都是圆角，中间接壤地方是两个圆角对接，所以需要右边压住左边
     * false 需要压住右边的进度
     * true，不需要压住右边进度（一般是进度超过90%几）
     */
    private boolean isNeedContain = false;

    private long mAboveOffest = 0l, containR = 0l;

    public enum ProgressTextVisibility {
        Visible, Invisible
    }

    public NumberProgressBar(Context context) {
        this(context, null);
    }

    public NumberProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_reached_bar_height = dp2px(1.5f);
        default_unreached_bar_height = dp2px(1.0f);
        default_text_size = sp2px(10);
        default_progress_text_offset = dp2px(3.0f);

        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberProgressBar,
                defStyleAttr, 0);

        mReachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_progress_reached_color, default_reached_color);
        mUnreachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_progress_unreached_color, default_unreached_color);
        mTextColor = attributes.getColor(R.styleable.NumberProgressBar_progress_text_color, default_text_color);
        mTextSize = attributes.getDimension(R.styleable.NumberProgressBar_progress_text_size, default_text_size);

        mReachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_progress_reached_bar_height, default_reached_bar_height);
        mUnreachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_progress_unreached_bar_height, default_unreached_bar_height);
        mOffset = attributes.getDimension(R.styleable.NumberProgressBar_progress_text_offset, default_progress_text_offset);

        int textVisible = attributes.getInt(R.styleable.NumberProgressBar_progress_text_visibility, PROGRESS_TEXT_VISIBLE);
        if (textVisible != PROGRESS_TEXT_VISIBLE) {
            mIfDrawText = false;
        }

        setProgress(attributes.getInt(R.styleable.NumberProgressBar_progress_current, 0));
        setMax(attributes.getInt(R.styleable.NumberProgressBar_progress_max, 100));

        attributes.recycle();
        initializePainters();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) mTextSize, Math.max((int) mReachedBarHeight, (int) mUnreachedBarHeight));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIfDrawText) {
            calculateDrawRectF();
        } else {
            calculateDrawRectFWithoutProgressText();
        }
//        todo 圆角和非悬浮不能一起使用（isRadius 是 true就不允许isAbove 为false，不然就造成视觉效果很有问题）,我这里就不强制闪退了
        if (isRadius) {
            //带圆角的就先画右边
            if (mDrawUnreachedBar) {
                if ((isAbove || !mIfDrawText) && mCurrentProgress != 0) {
                    mUnreachedRectF.left = mUnreachedRectF.left - mReachedRectF.right / 2;
                }
                canvas.drawRoundRect(mUnreachedRectF, mRx, mRy, mUnreachedBarPaint);
            }

            if (mDrawReachedBar) {
                //字显示必须是悬浮才咬
                //字显示不悬浮可以不咬
                //字不显示要咬
//                if (!isNeedContain && mCurrentProgress != 0) {
//                    if (isAbove || !mIfDrawText) {
//                        mReachedRectF.right = mReachedRectF.right + containR;
//                    }
//                }
                canvas.drawRoundRect(mReachedRectF, mRx, mRy, mReachedBarPaint);
            }

            if (mIfDrawText) {
                canvas.drawText(mCurrentDrawText, mDrawTextStart, mDrawTextEnd, mTextPaint);
            }
        } else {
            if (mDrawReachedBar) {
                canvas.drawRect(mReachedRectF, mReachedBarPaint);
            }

            if (mDrawUnreachedBar) {
                canvas.drawRect(mUnreachedRectF, mUnreachedBarPaint);
            }

            if (mIfDrawText) {
                canvas.drawText(mCurrentDrawText, mDrawTextStart, mDrawTextEnd, mTextPaint);
            }
        }
    }

    private void initializePainters() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedBarColor);

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedBarColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }


    private void calculateDrawRectFWithoutProgressText() {
        getReachedRectF();

        mUnreachedRectF.left = mReachedRectF.right;
        getUnreachedRectF();
    }

    /**
     * 有进度数字的情况下进行计算进度矩形计算
     */
    private void calculateDrawRectF() {
        mCurrentDrawText = String.format("%d", getProgress() * 100 / getMax());
        mCurrentDrawText = mPrefix + mCurrentDrawText + mSuffix;
        //要绘制的文本的宽度。
        float drawTextWidth = mTextPaint.measureText(mCurrentDrawText);
        //最先判断是否是悬浮还是拼接
        if (isAbove) {
            //悬浮
            if (getProgress() == 0) {
                mDrawReachedBar = false;
                mDrawTextStart = getPaddingLeft() + mAboveOffest;
                mReachedRectF.right = getPaddingLeft();
                isNeedContain = true;
            } else {
                mDrawReachedBar = true;
                getReachedRectF();
                mDrawTextStart = mReachedRectF.right + mOffset + mAboveOffest;
                if ((mDrawTextStart + drawTextWidth) >= (getWidth() - getPaddingRight())) {
                    //如果文字超出屏幕外了，就直接改成在左边进度最右顶端显示
                    mDrawTextStart = mReachedRectF.right - drawTextWidth - mOffset;
                    isNeedContain = true;
                } else {
                    isNeedContain = false;
                }
            }

            //获取数字结尾位置
            mDrawTextEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

            if (mReachedRectF.right + mOffset >= getWidth() - getPaddingRight()) {
                mDrawUnreachedBar = false;
            } else {
                mDrawUnreachedBar = true;
                mUnreachedRectF.left = mReachedRectF.right;
                getUnreachedRectF();
            }
        } else {
            //写这个自定义控件当有数字的时候最原始的判断 https://github.com/daimajia/NumberProgressBar
            if (getProgress() == 0) {
                mDrawReachedBar = false;
                mDrawTextStart = getPaddingLeft();
                isNeedContain = true;
            } else {
                mDrawReachedBar = true;
                getReachedRectF();
                mDrawTextStart = (mReachedRectF.right + mOffset);
            }

            if ((mDrawTextStart + drawTextWidth) >= getWidth() - getPaddingRight()) {
                mDrawTextStart = getWidth() - getPaddingRight() - drawTextWidth;
                mReachedRectF.right = mDrawTextStart - mOffset;
                isNeedContain = true;
            } else {
                isNeedContain = false;
            }

            mDrawTextEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

            float unreachedBarStart = mDrawTextStart + drawTextWidth + mOffset;
            if (unreachedBarStart >= getWidth() - getPaddingRight()) {
                mDrawUnreachedBar = false;
            } else {
                mDrawUnreachedBar = true;
                mUnreachedRectF.left = unreachedBarStart;
                getUnreachedRectF();
            }
        }
    }

    private void getReachedRectF() {
        mDrawReachedBar = true;
        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
        mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() - mOffset + getPaddingLeft();
        mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;
    }

    private void getUnreachedRectF() {
        mUnreachedRectF.right = getWidth() - getPaddingRight();
        mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
        mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
    }

    /**
     * Get progress text color.
     *
     * @return progress text color.
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Get progress text size.
     *
     * @return progress text size.
     */
    public float getProgressTextSize() {
        return mTextSize;
    }

    public int getUnreachedBarColor() {
        return mUnreachedBarColor;
    }

    public int getReachedBarColor() {
        return mReachedBarColor;
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    public int getMax() {
        return mMaxProgress;
    }

    public float getReachedBarHeight() {
        return mReachedBarHeight;
    }

    public float getUnreachedBarHeight() {
        return mUnreachedBarHeight;
    }

    public void setProgressTextSize(float textSize) {
        this.mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public void setProgressTextColor(int textColor) {
        this.mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setUnreachedBarColor(int barColor) {
        this.mUnreachedBarColor = barColor;
        mUnreachedBarPaint.setColor(mUnreachedBarColor);
        invalidate();
    }

    public void setReachedBarColor(int progressColor) {
        this.mReachedBarColor = progressColor;
        mReachedBarPaint.setColor(mReachedBarColor);
        invalidate();
    }

    public void setReachedBarHeight(float height) {
        mReachedBarHeight = height;
    }

    public void setUnreachedBarHeight(float height) {
        mUnreachedBarHeight = height;
    }

    public void setMax(int maxProgress) {
        if (maxProgress > 0) {
            this.mMaxProgress = maxProgress;
            invalidate();
        }
    }

    public void setSuffix(String suffix) {
        if (suffix == null) {
            mSuffix = "";
        } else {
            mSuffix = suffix;
        }
    }

    public String getSuffix() {
        return mSuffix;
    }

    public void setPrefix(String prefix) {
        if (prefix == null)
            mPrefix = "";
        else {
            mPrefix = prefix;
        }
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }

        if (mListener != null) {
            mListener.onProgressChange(getProgress(), getMax());
        }
    }

    public void setProgress(int progress) {
        if (progress <= getMax() && progress >= 0) {
            this.mCurrentProgress = progress;
            invalidate();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getProgressTextSize());
        bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT, getReachedBarHeight());
        bundle.putFloat(INSTANCE_UNREACHED_BAR_HEIGHT, getUnreachedBarHeight());
        bundle.putInt(INSTANCE_REACHED_BAR_COLOR, getReachedBarColor());
        bundle.putInt(INSTANCE_UNREACHED_BAR_COLOR, getUnreachedBarColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX, getSuffix());
        bundle.putString(INSTANCE_PREFIX, getPrefix());
        bundle.putBoolean(INSTANCE_TEXT_VISIBILITY, getProgressTextVisibility());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            mReachedBarHeight = bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT);
            mUnreachedBarHeight = bundle.getFloat(INSTANCE_UNREACHED_BAR_HEIGHT);
            mReachedBarColor = bundle.getInt(INSTANCE_REACHED_BAR_COLOR);
            mUnreachedBarColor = bundle.getInt(INSTANCE_UNREACHED_BAR_COLOR);
            initializePainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            setPrefix(bundle.getString(INSTANCE_PREFIX));
            setSuffix(bundle.getString(INSTANCE_SUFFIX));
            setProgressTextVisibility(bundle.getBoolean(INSTANCE_TEXT_VISIBILITY) ? ProgressTextVisibility.Visible : ProgressTextVisibility.Invisible);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public void setProgressTextVisibility(ProgressTextVisibility visibility) {
        mIfDrawText = visibility == ProgressTextVisibility.Visible;
        if (!mIfDrawText) {
            isAbove = false;
        }
        invalidate();
    }

    public boolean getProgressTextVisibility() {
        return mIfDrawText;
    }

    /**
     * 设置圆角dp
     * 圆角和非悬浮不能一起使用（isRadius 是 true就不允许isAbove 为false，不然就造成视觉效果很有问题）
     *
     * @param rx 建议5f往上，至于设置多少，自己试一下
     * @param ry 建议5f往上，至于设置多少，自己试一下
     */
    public void setRadius(float rx, float ry) {
        setRadius(rx, ry, 0, 10);
    }

    /**
     * 设置圆角dp
     * 圆角和非悬浮不能一起使用（isRadius 是 true并且进度数字是显示的情况下，就不允许isAbove 为false，不然就造成视觉效果很有问题）
     *
     * @param rx     建议5f往上，至于设置多少，自己试一下
     * @param ry     建议5f往上，至于设置多少，自己试一下
     * @param offset 进度数字与左边进度的距离
     */
    public void setRadius(float rx, float ry, float offset, long containR) {
        isRadius = true;
        mRx = rx;
        mRy = ry;
        mOffset = offset;
        this.containR = containR;
    }

    /**
     * 是否数字悬浮在进度上面,true是悬浮，False是不悬浮，当前，无论是否悬浮，都不再是左边进度拼接数字再拼接右边进度模式
     * 圆角和非悬浮不能一起使用（isRadius 是 true就不允许isAbove 为false，不然就造成视觉效果很有问题）
     *
     * @param offset   当数字悬浮的时候，默认里最左边多少
     * @param containR 如果是圆角，左边咬住右边多少。不是圆角可以传0
     */
    public void setProgressIsAbove(long offset, long containR) {
        this.isAbove = true;
        mAboveOffest = offset;
        this.containR = containR;
        mTextPaint.setStyle(Paint.Style.STROKE);
    }

    public void setOnProgressBarListener(OnProgressBarListener listener) {
        mListener = listener;
    }

    /**
     * 1、开始动画，如果你使用了开始动画功能，建议不要去调用setProgress
     * 2、如果调用了本功能，记得再adapter或者是界面销毁的时候调用releaseAnim
     * todo 经过测试发现，间隔时间为30毫秒的时候，动画比较流畅,然后总体时间在300毫秒左右效果不错
     *
     * @param progress
     * @param period    执行动画间隔，我自己测试间隔为30毫秒还不错
     * @param totalTime 总共花费时间，我自己测试300毫秒还不错
     */
    public void startAnim(final Activity activity, final int progress, int period, int totalTime) {
        releaseAnim();
        if (progress == 0) {
            setProgress(0);
            return;
        }
        mCurrentProgress = 0;
        mTimer = new Timer();
        //每次加多少
        int more = progress / (totalTime / period);
        if (more <= 0) {
            more = 1;
        }
        final int finalMore = more;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurrentProgress < progress) {
                            if (mCurrentProgress + finalMore < progress) {
                                setProgress(mCurrentProgress + finalMore);
                            } else {
                                setProgress(mCurrentProgress + 1);
                            }
                        }else {
                            mTimer.cancel();
                        }
                    }
                });
            }
        };
        mTimer.schedule(timerTask, 200, period);
    }

    private Timer mTimer = null;

    public void releaseAnim() {
        if (null != mTimer) {
            mTimer.cancel();
        }
    }
}
