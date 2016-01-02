package com.yanyi.luckbag.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AmayaImageUtil {

    //	图片缩小处理 start
    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;//压缩前最大的图片像素值

    @SuppressWarnings("unused")
    private static Bitmap readBitMap(String filePath) throws FileNotFoundException {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
//		return BitmapFactory.decodeStream(new FileInputStream(file), null, opt);
        return BitmapFactory.decodeFile(filePath, opt);
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap
     * @param w
     * @param h
     * @param reduce 是否裁减（根据规则）
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h, boolean reduce) {
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) w / width);
            float scaleHeight = ((float) h / height);
            if (height * scaleWidth > h) {
                height = (int) (h / scaleWidth);
                if (h <= 1600) {//正常截图时，当新浪分享时，图片超过1600高时，做无法返回null,让程序做局部隐藏再截屏操作
                    return null;
                } else if (h > 1600)//当最终版时，必须给出个图时，取默认值 1600 高的切断图处理
                    height = (int) (1600 / scaleWidth);

            }
            int _startX = 0, _startY = 0;
            if (reduce) {
                if (width != height) {
                    if (width > height) {
                        scaleWidth = scaleHeight;
                        _startX = (width - height) / 2;
                        width = height;
                    } else {
                        scaleHeight = scaleWidth;
                        _startY = (height - width) / 2;
                        height = width;
                    }
                }
            }
            matrix.postScale(scaleWidth, scaleWidth);
            Bitmap newbmp = Bitmap.createBitmap(bitmap, _startX, _startY, width, height, matrix, true);
            return newbmp;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    // 将Drawable转化为Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int width, int height, float roundPx) {

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // 获得带阴影的图片方法
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas
                .drawRect(0, height, width, height + reflectionGap,
                        deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 生成自定义的缩略图大小
     */
    public synchronized static File saveMyBitmap(String image_path, int image_width, int image_height) throws IOException {
        Assert.assertTrue(image_path != null && !image_path.equals("") && image_height > 0 && image_width > 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(image_path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }
            int _width = options.outWidth;
            int _height = options.outHeight;
            if (_width < image_width || _height < image_height) {
                image_width = _width;
                image_height = _height;
            } else if (_width < _height) {//图片为竖显示时
                image_height = _height * ((image_width * 1000) / _width) / 1000;
            } else if (_width > _height) {//图片为横显示时
                image_width = _width * ((image_height * 1000) / _height) / 1000;
            }

            options.inSampleSize = 1;

            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            options.inJustDecodeBounds = false;

            Bitmap bm = BitmapFactory.decodeFile(image_path, options);
            if (bm == null) {
                return null;
            }

            final Bitmap scale = Bitmap.createScaledBitmap(bm, image_width, image_height, true);
            if (scale != null) {
                //bm.recycle();
                bm = scale;
            }
            File originalFile = new File(image_path);
            File thumbnailsFile = new File(originalFile.getParent() + "/t_" + originalFile.getName());
            thumbnailsFile.createNewFile();
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(thumbnailsFile);
//				if(ImageManager.getExifOrientation(image_path)!=0){
//					Matrix matrix = new Matrix();
//					matrix.preRotate(ImageManager.getExifOrientation(image_path));
//					bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//				}
                bm.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                recoverBitmap(bm);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                }
//				Util.closeSilently(fOut);
            }
            return thumbnailsFile;
        } catch (final OutOfMemoryError e) {
            options = null;
            return null;
        } catch (Exception e) {
            options = null;
            return null;
        }
    }

    /**
     * @param file
     * @param image_width
     * @param image_height
     * @return
     * @hide
     */
    @SuppressWarnings("unused")
    private static Bitmap decodeFile(File file, int image_width, int image_height) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);

            int REQUIRED_HEIGHT = image_height;
            int REQUIRED_WIDTH = image_width;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;

//			Log.w("===", (width_tmp + "  " + height_tmp));

            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_WIDTH
                        && height_tmp / 2 < REQUIRED_HEIGHT)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;

//				Log.w("===", scale + "''" + width_tmp + "  " + height_tmp);
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
//	图片缩小处理 end

    public static File save2Storage(Bitmap bitmap, String path, boolean saveAsjpg, int compress) {
        FileOutputStream out = null;
        try {
//            AmayaLog.e("amaya","save2Storage()...saveAsjpg="+saveAsjpg+"--path="+path);
            File filename = new File(path);
            out = new FileOutputStream(filename);
            bitmap.compress(saveAsjpg ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, compress, out);
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static File save2Storage(String srcPath, String dstPath) {
        FileInputStream fis = null;
        BufferedOutputStream bos = null;
        try {
            File fo = new File(dstPath);
//            AmayaLog.e("amaya","save2Storage()...srcPath="+srcPath+"--dstPath="+fo.getAbsolutePath());
            if (!fo.getParentFile().exists()) fo.getParentFile().mkdirs();
            fo.createNewFile();
            fis = new FileInputStream(srcPath);
            bos = new BufferedOutputStream(new FileOutputStream(fo));
            int len = 0;
            byte[] buf = new byte[2048];
            while ((len = fis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
            return fo;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bos != null) try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] Bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bt = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
        }
        return bt;
    }

    /**
     * 为微信分享时的传图片字节组
     *
     * @param imgPath
     * @return
     * @throws Exception 当文件大于32K时会失败
     */
    public static byte[] wxRecommendBitmap(String imgPath) throws Exception {
        int sampleSize = 1;
        while (true) {
            File file = new File(imgPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            byte[] bs = Bitmap2Bytes(bitmap);
            if (bs.length > 32 * 1024) {
                if (sampleSize > 10) {
                    throw new Exception();
                }
                recoverBitmap(bitmap);
                bs = null;
                sampleSize++;
                continue;
            }
            return bs;
        }
    }

    /**
     * 回收资源
     *
     * @param bm
     */
    private static void recoverBitmap(Bitmap bm) {
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
        }
        bm = null;
    }

    public static boolean isImageType(String metaType) {
        if (metaType != null) {
            List<String> allowType = Arrays.asList("image/bmp", "image/gif", "image/jpeg", "image/jpg", "image/pjpeg", "image/png", "image/tif", "image/tiff", "image/x-ms-bmp");
            return allowType.contains(metaType);
        }
        return false;
    }

    public static Bitmap getScaleBitmap(String path, int height, int width) {
        if (TextUtils.isEmpty(path)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//不加载bitmap到内存中
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Config.ARGB_8888;
        options.inSampleSize = 1;
        if (outWidth != 0 && outHeight != 0) {
            int sampleSize = (outWidth / height + outHeight / height) / 2;
            options.inSampleSize = sampleSize;
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getBitmap(String path, boolean rotate, float width, float height) {
        if (TextUtils.isEmpty(path)) return null;
        int[] rs = getRotation(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Config.ARGB_8888;
        BitmapFactory.decodeFile(path, options);
        float w = options.outWidth / width;
        float h = options.outHeight / height;
        float min = Math.max(w, h);
        if (min > 1.0) {
            options.outWidth = (int) (options.outWidth / min);
            options.outHeight = (int) (options.outHeight / min);
            options.inSampleSize = (int) Math.ceil(min);
        }
        options.inJustDecodeBounds = false;
        if (!rotate) rs[0] = 0;
        Bitmap decodedBitmap = BitmapFactory.decodeFile(path, options);
        Bitmap finalBitmap = ScaleAndRotateBitmap(decodedBitmap, rs[0], rs[1]);
        return finalBitmap;
    }

    public static File createUploadSuitPic(String path, int compress, Map<String, String> textMap, boolean saveAsPNG, boolean forceSave) {
        if (TextUtils.isEmpty(path)) return null;
        String tempPath = new String(AmayaConstants.AMAYA_DIR_CACHE + "/" + System.currentTimeMillis() + ".jpg");
        if (saveAsPNG) tempPath = tempPath.replace(".jpg", ".png");
        File tempFile = new File(tempPath);
        if (!forceSave && tempFile.exists()) {
            return tempFile;
        }
        int[] rs = getRotation(path);
        FileOutputStream fos = null;
        try {
//            AmayaLog.e("amaya", "createUploadSuitPic()...path=" + path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Config.ARGB_8888;
            BitmapFactory.decodeFile(path, options);
            float min = Math.max(options.outHeight, options.outWidth) / 600f;
            if (min > 1.0) {
                options.outWidth = (int) (options.outWidth / min);
                options.outHeight = (int) (options.outHeight / min);
                options.inSampleSize = (int) Math.ceil(min);
            }
            options.inJustDecodeBounds = false;
            Bitmap decodedBitmap = BitmapFactory.decodeFile(path, options);
            Bitmap finalBitmap = ScaleAndRotateBitmap(decodedBitmap, rs[0], rs[1]);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }

            if (textMap != null) {
                ExifInterface eif = new ExifInterface(path);
                JSONObject jo = new JSONObject();
                try {
                    jo.put("DateTime", eif.getAttribute(ExifInterface.TAG_DATETIME));
                    jo.put("Width", finalBitmap.getWidth());
                    jo.put("Height", finalBitmap.getHeight());
                    jo.put("GPSTimeStamp", eif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP));
                    jo.put("GPSDateStamp", eif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP));
                    jo.put("GPSLongitude", eif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                    jo.put("GPSLatitude", eif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                    textMap.put("extendInformation", jo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            fos = new FileOutputStream(tempFile);
            finalBitmap.compress(saveAsPNG ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, compress, fos);
            return tempFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static Bitmap ScaleAndRotateBitmap(Bitmap bitmap, int rotation, int flipHorizontal) {
        Matrix m = new Matrix();
        if (flipHorizontal != 0) {
            m.postScale(-1, 1);
        }
        if (rotation != 0) {
            m.postRotate(rotation);
        }
        Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        if (finalBitmap != bitmap) {
            bitmap.recycle();
        }
        return finalBitmap;
    }


    public static int[] getRotation(String imgPath) {
        int[] rs = new int[2];
        int rotation = 0;
        int flip = 0;
        try {
            ExifInterface exif = new ExifInterface(imgPath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    flip = 1;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    flip = 1;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    flip = 1;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    flip = 1;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rs[0] = rotation;
        rs[1] = flip;
        return rs;
    }

    public static Bitmap transform(Matrix scaler,
                                   Bitmap source,
                                   int targetWidth,
                                   int targetHeight,
                                   boolean scaleUp) {

        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
             * than the target.  Transform it by placing as much of the image
             * as possible into the target and leaving the top/bottom or
             * left/right (or both) black.
             */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
                    Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(
                    deltaXHalf,
                    deltaYHalf,
                    deltaXHalf + Math.min(targetWidth, source.getWidth()),
                    deltaYHalf + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(
                    dstX,
                    dstY,
                    targetWidth - dstX,
                    targetHeight - dstY);
            c.drawBitmap(source, src, dst, null);
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to mFilter here.
            b1 = Bitmap.createBitmap(source, 0, 0,
                    source.getWidth(), source.getHeight(), scaler, true);
        } else {
            b1 = source;
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(
                b1,
                dx1 / 2,
                dy1 / 2,
                targetWidth,
                targetHeight);

        if (b1 != source) {
            b1.recycle();
        }

        return b2;
    }

    public static Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    public static long getBitmapsize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}