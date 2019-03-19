package com.dace.textreader.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * 图片格式转换工具类
 * Created by 70391 on 2017/8/25.
 */

public class ImageUtils {
    /**
     * 从本地path中获取bitmap，压缩后保存小图片到本地
     *
     * @param context
     * @param path    图片存放的路径
     * @return 返回压缩后图片的存放路径
     */
    public static String saveBitmap(Context context, String path) {
        String compressdPicPath = "";

//      ★★★★★★★★★★★★★★重点★★★★★★★★★★★★★
      /*  //★如果不压缩直接从path获取bitmap，这个bitmap会很大，下面在压缩文件到100kb时，会循环很多次，
        // ★而且会因为迟迟达不到100k，options一直在递减为负数，直接报错
        //★ 即使原图不是太大，options不会递减为负数，也会循环多次，UI会卡顿，所以不推荐不经过压缩，直接获取到bitmap
        Bitmap bitmap=BitmapFactory.decodeFile(path);*/
//      ★★★★★★★★★★★★★★重点★★★★★★★★★★★★★

//        建议先将图片压缩到控件所显示的尺寸大小后，再压缩图片质量
//        首先得到手机屏幕的高宽，根据此来压缩图片，当然想要获取跟精确的控件显示的高宽（更加节约内存）,可以使用getImageViewSize();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;  // 屏幕宽度（像素）
        int height = displayMetrics.heightPixels;  // 屏幕高度（像素）
//        获取按照屏幕高宽压缩比压缩后的bitmap
        Bitmap bitmap = decodeSampledBitmapFromPath(path, width / 7, height / 7);

        String oldName = path.substring(path.lastIndexOf("/"), path.lastIndexOf("."));
        String name = oldName + "_compress.jpg";//★很奇怪oldName之前不能拼接字符串，只能拼接在后面，否则图片保存失败
        String saveDir = Environment.getExternalStorageDirectory()
                + "/compress";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 保存入sdCard
        File file = new File(saveDir, name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        /* options表示 如果不压缩是100，表示压缩率为0。如果是70，就表示压缩率是70，表示压缩30%; */
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        while (baos.toByteArray().length / 1024 > 500) {
// 循环判断如果压缩后图片是否大于500kb继续压缩

            baos.reset();
            options -= 10;
            if (options < 11) {//为了防止图片大小一直达不到500kb，options一直在递减，当options<0时，下面的方法会报错
                // 也就是说即使达不到500kb，也就压缩到10了
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                break;
            }
// 这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(baos.toByteArray());
            out.flush();
            out.close();
            compressdPicPath = file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressdPicPath;
    }

    /**
     * 根据图片要显示的宽和高，对图片进行压缩，避免OOM
     *
     * @param path
     * @param width  要显示的imageview的宽度
     * @param height 要显示的imageview的高度
     * @return
     */
    private static Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {

//      获取图片的宽和高，并不把他加载到内存当中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options, width, height);
//      使用获取到的inSampleSize再次解析图片(此时options里已经含有压缩比 options.inSampleSize，再次解析会得到压缩后的图片，不会oom了 )
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;

    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     *
     * @param options
     * @param reqWidth  要显示的imageview的宽度
     * @param reqHeight 要显示的imageview的高度
     * @return
     * @compressExpand 这个值是为了像预览图片这样的需求，他要比所要显示的imageview高宽要大一点，放大才能清晰
     */
    private static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if (width >= reqWidth || height >= reqHeight) {

            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(width * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);

        }

        return inSampleSize;
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     */
    public static String getImageAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * bitmap图片转字节数组
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 把网络资源图片转化成bitmap
     *
     * @return Bitmap
     */
    public static Bitmap GetNetworkBitmap(String path) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 截取scrollview的屏幕
     **/
    public static Bitmap getScrollViewBitmap(ScrollView scrollView, Context context) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }

        h += DensityUtil.dip2px(context, 66);

        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getMeasuredWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 截取viewGroup内容，生成图片
     *
     * @param viewGroup 容器控件
     * @return 图片bitmap
     */
    public static Bitmap getViewGroupBitmap(ViewGroup viewGroup) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            h += viewGroup.getChildAt(i).getHeight();
        }
        // 创建相应大小的bitmap
        bitmap = Bitmap.createBitmap(viewGroup.getMeasuredWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        //获取当前主题背景颜色，设置canvas背景
        canvas.drawColor(Color.WHITE);
        //画文字水印，不需要的可删去下面这行
//        drawTextToBitmap(viewGroup.getContext(), canvas, viewGroup.getMeasuredWidth(), h);
        //绘制viewGroup内容
        viewGroup.draw(canvas);
        //createWaterMaskImage为添加logo的代码，不需要的可直接返回bitmap
//        return createWaterMaskImage(bitmap, BitmapFactory.decodeResource(viewGroup.getResources(), R.mipmap.ic_launcher));
        return bitmap;
    }

    /**
     * 截取viewGroup内容，生成图片
     *
     * @param viewGroup 容器控件
     * @return 图片bitmap
     */
    public static Bitmap getViewGroupBitmap(ViewGroup viewGroup, ViewGroup group) {
        int h = 0;
        Bitmap bitmap;
        h += viewGroup.getHeight();
        h += group.getHeight();
        // 创建相应大小的bitmap
        bitmap = Bitmap.createBitmap(viewGroup.getMeasuredWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        //获取当前主题背景颜色，设置canvas背景
        canvas.drawColor(Color.WHITE);
        //画文字水印，不需要的可删去下面这行
//        drawTextToBitmap(viewGroup.getContext(), canvas, viewGroup.getMeasuredWidth(), h);
        //绘制viewGroup内容
        viewGroup.draw(canvas);
        //createWaterMaskImage为添加logo的代码，不需要的可直接返回bitmap
//        return createWaterMaskImage(bitmap, BitmapFactory.decodeResource(viewGroup.getResources(), R.mipmap.ic_launcher));
        return bitmap;
    }

    /**
     * 获取webview图片
     *
     * @param webView
     * @return
     */
    public static Bitmap getWebViewBitmap(Context context, WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float scale = webView.getScale();
            int width = webView.getWidth();
            int height = (int) (webView.getContentHeight() * scale
                    + DensityUtil.dip2px(context, 66));
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
            return bitmap;
        } else {
            Picture picture = webView.capturePicture();
            int width = picture.getWidth();
            int height = picture.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            picture.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 给图片添加水印
     *
     * @param context
     * @param canvas  画布
     * @param width   宽
     * @param height  高
     */
    public static void drawTextToBitmap(Context context, Canvas canvas, int width, int height) {
        //要添加的文字
        String logo = "派知语文";
        //新建画笔，默认style为实心
        Paint paint = new Paint();
        //设置颜色，颜色可用Color.parseColor("#6b99b9")代替
        paint.setColor(Color.parseColor("#ff9933"));
        //设置透明度
        paint.setAlpha(80);
        //抗锯齿
        paint.setAntiAlias(true);
        //画笔粗细大小
        paint.setTextSize((float) DensityUtil.dip2px(context, 30));
        //保存当前画布状态
        canvas.save();
        //画布旋转-30度
        canvas.rotate(-30);
        //获取要添加文字的宽度
        float textWidth = paint.measureText(logo);
        int index = 0;
        //行循环，从高度为0开始，向下每隔80dp开始绘制文字
        for (int positionY = -DensityUtil.dip2px(context, 30); positionY <= height; positionY += DensityUtil.dip2px(context, 80)) {
            //设置每行文字开始绘制的位置,0.58是根据角度算出tan30°,后面的(index++ % 2) * textWidth是为了展示效果交错绘制
            float fromX = -0.58f * height + (index++ % 2) * textWidth;
            //列循环，从每行的开始位置开始，向右每隔2倍宽度的距离开始绘制（文字间距1倍宽度）
            for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                //绘制文字
                canvas.drawText(logo, positionX, positionY, paint);
            }
        }
        //恢复画布状态
        canvas.restore();
    }

    /**
     * 添加logo水印
     *
     * @param src  原图片
     * @param logo logo
     * @return 水印图片
     */
    public static Bitmap createWaterMaskImage(Context context, Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        //原图宽高
        int w = src.getWidth();
        int h = src.getHeight();
        //logo宽高
        int ww = logo.getWidth();
        int wh = logo.getHeight();
        //创建一个和原图宽高一样的bitmap
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //创建
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        //绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //新建矩阵
        Matrix matrix = new Matrix();
        //对矩阵作缩放处理
        matrix.postScale(1f, 1f);
        //对矩阵作位置偏移，移动到底部中间的位置
        matrix.postTranslate(0, h - DensityUtil.dip2px(context, 66));
        //将logo绘制到画布上并做矩阵变换
        canvas.drawBitmap(logo, matrix, null);
        // 保存状态
        canvas.save();// 保存
        // 恢复状态
        canvas.restore();
        return newBitmap;
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap
     * @return
     */
    public static String downloadBitmap(Bitmap bitmap) {
        String filepath = "";
        FileOutputStream fos;
        try {
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                // SD卡根目录
                File sdRoot = Environment.getExternalStorageDirectory();
                String filename = String.valueOf(new Date().getTime());
                filepath = sdRoot.getAbsolutePath() + "/" + filename + ".png";
                File file = new File(sdRoot, filename + ".png");
                fos = new FileOutputStream(file);
            } else {
                throw new Exception("创建文件失败!");
            }

            //压缩图片 30 是压缩率，表示压缩70%; 如果不压缩是100，表示压缩率为0
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            bitmap.recycle();
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filepath;
    }

}
