package ye.da.baseutil;

import android.content.Context;

/**
 * @author ChenYe created by on 2020/1/17 0017. 09:52
 * 如果你用本人的lib ，然后里面的Util用到了Context，那么就必须在Application里面进行初始化传递Context进来
 **/
public class InitCtx {

    private ContextListener mListener = null;

    public Context getCtx() {
        if (null == mListener) {
            throw new RuntimeException("请在调用InitCtx.getCtx()之前进行初始化并且对setContextListener进行赋值");
        }
        return mListener.getContext();
    }

    private InitCtx() {

    }

    private static final class InitCtxHolder {
        private static final InitCtx INSTANCE = new InitCtx();
    }

    public static InitCtx getInstance() {
        return InitCtxHolder.INSTANCE;
    }

    public interface ContextListener {
        /**
         * 返回context
         *
         * @return
         */
        Context getContext();
    }

    /**
     * 个人建议这个初始化方法放在Application里面
     *
     * @param listener
     */
    public void setContextListener(ContextListener listener) {
        mListener = listener;
    }
}
