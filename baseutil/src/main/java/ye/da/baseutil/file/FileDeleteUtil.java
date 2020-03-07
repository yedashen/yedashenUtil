package ye.da.baseutil.file;

import android.text.TextUtils;

import java.io.File;

/**
 * @author ChenYe created by on 2019/1/15 0015. 15:23
 **/

public class FileDeleteUtil {

    /**
     * 删除指定文件或文件夹：
     * （1）如果是文件的话就直接删掉了。
     * （2）如果要删的文件是文件夹的话，就会递归删下去
     *
     * @param fileOrDirectory
     */
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    /**
     * 根据Path删除指定文件或文件夹
     *
     * @param path
     */
    public static void deleteRecursive(String path) {
        if (!TextUtils.isEmpty(path)) {
            deleteRecursive(new File(path));
        }
    }
}
