package ye.da.baseutil.file;

import android.text.TextUtils;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @author ChenYe created by on 2019/1/15 0015. 15:21
 **/

public class FileFormatUtil {

    private static final int B_SIZE = 1024, KB_SIZE = 1048576, MB_SIZE = 1073741824;

    /**
     * 转换文件大小
     * 补充知识,文件的物理大小和占用内存大小不一定是一样的，比如照片：假如一张照片物理大小是50KB,但是
     * 你加载到内存里面去显示的时候，生成的是Bitmap对象，这个bitmap对象的大小可能是4Mb。我想表达的是
     * 你加载到内存里面的大小应该指的是文件对象占用内存大小而不是文件本身大小(纯属我个人理解)。
     *
     * @param fileS 文件大小
     * @return
     */
    public static String fileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < B_SIZE) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < KB_SIZE) {
            fileSizeString = df.format((double) fileS / B_SIZE) + "KB";
        } else if (fileS < MB_SIZE) {
            fileSizeString = df.format((double) fileS / KB_SIZE) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / MB_SIZE) + "GB";
        }
        return fileSizeString;
    }

    /**
     * @param length 毫秒值，比如8000mills
     * @return 是02:00
     */
    public static String formatMediaLength(long length) {
        return String.format("%02d:%02d", length / 1000 / 60 % 60, length / 1000 % 60);
    }

    public static String fileSize(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "0.0B";
        }
        return fileSize(new File(filePath));
    }

    public static String fileSize(File file) {
        if (null != file && file.exists()) {
            return fileSize(file.length());
        }
        return "0.0B";
    }
}
