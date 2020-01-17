package ye.da.baseutil.sp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import ye.da.baseutil.InitCtx;

/**
 * @author ChenYe created by on 2020/1/17 0017. 09:56
 **/
public class YeSpUtil {

    private static YeSpUtil mInstance = null;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static String mFileName = "";

    /**
     * @param fileName xml文件名
     */
    private YeSpUtil(String fileName) {
        Context ctx = InitCtx.getInstance().getCtx();
        mSharedPreferences = ctx.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        mFileName = fileName;
    }

    /**
     * 您用的时候如果觉得每次传xmlName可以选择不传，那么默认就存在config.xml下
     *
     * @return YeSpUtil
     */
    public static YeSpUtil getInstance() {
        return getInstance("config");
    }

    public static YeSpUtil getInstance(String fileName) {
        if (mInstance == null || !mFileName.equals(fileName)) {
            synchronized (YeSpUtil.class) {
                if (mInstance == null || !mFileName.equals(fileName)) {
                    mInstance = new YeSpUtil(fileName);
                }
            }
        }
        return mInstance;
    }

    /**
     * 存值
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, Object value) {
        if (null == mEditor) {
            mEditor = mSharedPreferences.edit();
        }
        if (value instanceof Integer) {
            mEditor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            mEditor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            mEditor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (value instanceof Set) {
            mEditor.putStringSet(key, (Set) value);
        } else {
            if (value == null) {
                value = "";
                mEditor.putString(key, (String) value);
            }
        }
        mEditor.apply();
    }

    /**
     * 取值
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public Object get(String key, Object defValue) {
        Object value = null;
        if (defValue instanceof Integer) {
            value = mSharedPreferences.getInt(key, (Integer) defValue);
        } else if (defValue instanceof String) {
            value = mSharedPreferences.getString(key, (String) defValue);
        } else if (defValue instanceof Boolean) {
            value = mSharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Long) {
            value = mSharedPreferences.getLong(key, (Long) defValue);
        } else if (defValue instanceof Float) {
            value = mSharedPreferences.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Set) {
            value = mSharedPreferences.getStringSet(key, (Set) defValue);
        }
        return value;
    }

    /**
     * 取值,免得每次你输入默认值很烦，你可以直接调用这个方法
     *
     * @param key 键
     * @return
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * 取值,带默认值的
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * 取值,免得每次你输入默认值很烦，你可以直接调用这个方法
     *
     * @param key 键
     * @return
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 取值,带默认值的
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 取值,免得每次你输入默认值很烦，你可以直接调用这个方法
     *
     * @param key 键
     * @return
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 取值,带默认值的
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * 取值,带默认值的
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public Set getStringSet(String key, Set defaultValue) {
        return mSharedPreferences.getStringSet(key, defaultValue);
    }

    /**
     * 取值,免得每次你输入默认值很烦，你可以直接调用这个方法
     *
     * @param key 键
     * @return
     */
    public Long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * 取值,带默认值的
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public Long getLong(String key, Long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    /**
     * 取值,免得每次你输入默认值很烦，你可以直接调用这个方法
     *
     * @param key 键
     * @return
     */
    public Float getFloat(String key) {
        return getFloat(key, 1.0f);
    }

    /**
     * 取值,带默认值的
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public Float getFloat(String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * 移除键值对
     *
     * @param key
     */
    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    /**
     * 清除所有键值对
     */
    public void clear() {
        mEditor.clear();
        mEditor.commit();
    }
}
