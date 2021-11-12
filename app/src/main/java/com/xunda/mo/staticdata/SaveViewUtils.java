package com.xunda.mo.staticdata;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveViewUtils {
    private static final String TAG = "ViewUtils";




    /*
     * 保存文件，文件名为当前日期
     */
    public static void saveBitmap(Context context, View view) {
//        Log.d(TAG, "Build.BRAND============" + Build.BRAND);
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        Bitmap bitmap = view.getDrawingCache(); // 获取图片
        //创建文件路径
        File dir = new File(context.getExternalFilesDir(null).getPath() + "xunDa");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        //创建文件
        File file = new File(dir , fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out;
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }

}
