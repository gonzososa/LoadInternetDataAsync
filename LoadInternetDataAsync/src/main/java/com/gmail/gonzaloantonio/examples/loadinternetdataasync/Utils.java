package com.gmail.gonzaloantonio.examples.loadinternetdataasync;


import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class Utils {
    public static DiskCache diskCache;
    public static final String UniqueName = "thumbnails";
    public static final int SizeOfCache = 1024 * 1024 * 10;
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    public Utils () {}

    public static boolean isExternalStorageRemovable () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable ();
        }

        return true;
    }

    public static File getExternalCacheDir (Context context) {
        if (hasExternalCacheDir ()) {
            return context.getExternalCacheDir ();
        }

        final String cacheDir = "/Android/data/" + context.getPackageName () + "/cache/";
        return new File (Environment.getExternalStorageDirectory () +  cacheDir);
    }

    public static boolean hasExternalCacheDir () {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }
}
