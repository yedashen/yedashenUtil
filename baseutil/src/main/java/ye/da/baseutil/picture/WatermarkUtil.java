package ye.da.baseutil.picture;

import android.graphics.Bitmap;

//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;

/**
 * @author ChenYe created by on 2019/4/29 0029. 14:58
 *         因为经过测试发现，原来的方案是拍完照之后直接添加水印，然后上传的时候对添加完水印之后的照片上传会对水印效果也有压缩影响。
 *         所以我准备采用新的方式，拍完照，加水印，然后再上传的时候再加水印，因为加水印的位置和数据都是一样的，所以相当于覆盖了。
 *         之所以这样做是为了做到以下几点：（1）保证用户手机里面保留原图。（2）上传一定做压缩处理，并且保证水印最清晰。
 *         (3)用户在没有上传之前能够预览到水印。
 **/

public class WatermarkUtil {

    /**
     * （1）调用本方法请在异步里面执行。
     * （2）调用本方法之前，请自行判断权限
     *
     * @param path 原照片路径
     * @param time 水印内容
     * @return 返回的地址如果为空代表加水印出错，不为空就为水印地址(现在也是原图地址，直接覆盖了)
     */
//    public static Observable<String> addWatermark(String path, String time) {
//        if (TextUtils.isEmpty(path)) {
//            return Observable.just("");
//        }
//        return addWatermark(new File(path), time);
//    }

    /**
     * （1）调用本方法请在异步里面执行。
     * （2）调用本方法之前，请自行判断权限
     *
     * @param file 原照片file对象
     * @param time 水印内容
     * @return 返回的地址如果为空代表加水印出错，不为空就为水印地址
     */
//    public static Observable<String> addWatermark(File file, String time) {
//        if (null == file || !file.exists() || file.length() == 0) {
//            return Observable.just("");
//        }
//        try {
//            Bitmap sourceBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//            if (isEmptyBitmap(sourceBitmap)) {
//                return Observable.just("");
//            }
//            return addTextWatermark(sourceBitmap, time, 158, Color.WHITE, sourceBitmap.getWidth() / 2, sourceBitmap.getHeight() - 200)
//                    .flatMap(bitmap -> save(bitmap, file, Bitmap.CompressFormat.JPEG))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread());
//        } catch (Exception e) {
//            return Observable.just("");
//        }
//    }

//    private static Observable<Bitmap> addTextWatermark(Bitmap src, String content, int textSize, int color, float x, float y) {
//        Bitmap ret = src.copy(src.getConfig(), true);
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        Canvas canvas = new Canvas(ret);
//        paint.setColor(color);
//        paint.setTextSize(textSize);
//        Rect bounds = new Rect();
//        paint.getTextBounds(content, 0, content.length(), bounds);
//        canvas.drawText(content, x, y, paint);
//        if (!src.isRecycled()) {
//            src.recycle();
//        }
//        return Observable.just(ret);
//    }

//    private static Observable<String> save(Bitmap src, File file, Bitmap.CompressFormat format) {
//        if (isEmptyBitmap(src)) {
//            return Observable.just("");
//        }
//
//        OutputStream os;
//        try {
//            os = new BufferedOutputStream(new FileOutputStream(file));
//            src.compress(format, 100, os);
//            if (!src.isRecycled()) {
//                src.recycle();
//            }
//        } catch (IOException e) {
//            return Observable.just("");
//        }
//
//        return Observable.just(file.getAbsolutePath());
//    }

    /**
     * Bitmap对象是否为空。
     */
    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
}
