package com.gmail.gonzaloantonio.examples.loadinternetdataasync;


import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static DiskLRUCacheWrapper diskCache;
    public static final String UniqueName = "thumbnails";
    public static final int SizeOfCache = 1024 * 1024 * 10;
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    public static final Object diskCacheLock = new Object ();
    public static boolean diskCacheStarting = true;

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

    public static String createMD5String (String key) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update (key.getBytes ());
            BigInteger bigInt = new BigInteger (1, md.digest ());
            result = String.format ("%1$032x", bigInt);
        } catch (NoSuchAlgorithmException e) {}

        return result;
    }
}
