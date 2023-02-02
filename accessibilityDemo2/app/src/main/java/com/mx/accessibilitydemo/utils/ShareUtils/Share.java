package com.mx.accessibilitydemo.utils.ShareUtils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class Share {

    //传入的fileName是文件名的全路径
    @SuppressLint("QueryPermissionsNeeded")
    public static void shareFile(String fileName, Context context) {
        final Uri uri;
        final File file = new File(fileName);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 24) {//若SDK大于等于24  获取uri采用共享文件模式
//            Log.e(TAG, "版本大于24，获取uri采用共享文件模式, 根目录：" +  Environment.getExternalStorageDirectory().getPath());
            uri = FileProvider.getUriForFile(context, "com.mx.accessibilitydemo.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType(FileUtil.getMIMEType(new File(file.getAbsolutePath())));//此处可发送多种文件
        if (share.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(share);
        } else {
            Toast.makeText(context,"没有可以处理的对象",Toast.LENGTH_SHORT).show();
        }
    }
    //getMIMEType
}
