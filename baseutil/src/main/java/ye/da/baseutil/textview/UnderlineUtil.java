package ye.da.baseutil.textview;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

/**
 * Created by Android Studio.
 * User: Administrator
 * Date: 2020/2/16 0016
 * Time: 下午 10:09
 * desc: 调用系统的下划线功能进行基本封装
 * 给文字加下划线有很多方式： https://www.jianshu.com/p/91d89f2e14be
 */
public class UnderlineUtil {

    /**
     * 直接将返回值setText到TextView就行，记住不能在外面对返回值拼接setText
     * todo 正确用法:mTextView.setText(showMsg("测试"),0,"测试".length(),"#333333");
     * todo 错误用法:mTextView.setText(showMsg("测试"),0,"测试".length(),"#333333")+"XXX");，修改为:mTextView.setText(showMsg("测试"+"XXX"),0,"测试"+"XXX".length(),"#333333"));
     *
     * @param color 比如："#0099EE"
     * @return
     */
    public static SpannableString showMsg(String desc, int begin, int end, String color) {
        SpannableString content = new SpannableString(desc);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        content.setSpan(underlineSpan, begin, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(color));
        content.setSpan(colorSpan, begin, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return content;
    }

}
