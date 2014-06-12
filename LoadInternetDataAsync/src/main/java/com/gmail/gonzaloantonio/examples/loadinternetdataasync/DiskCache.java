package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskCache {
    private DiskLruCache diskCache;
    private final Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private final int compressQuality = 80;
    private final int APP_VERSION = 1;
    private final int VALUE_COUNT = 1;
    private final String TAG = "DiskCache";

    public DiskCache (Context context, String uniqueName, int diskCachesize) {
        try {
            final File diskCacheDir = getDiskCacheDir (context, uniqueName);
            diskCache = DiskLruCache.open (diskCacheDir, APP_VERSION, VALUE_COUNT, diskCachesize);
            Log.i ("JENSELTER", diskCache.getDirectory().getPath());
        } catch (IOException e) {
            Log.i("JENSELTER", e.getMessage());
        }
    }

    private boolean writeBitmapToFile (Bitmap bitmap, DiskLruCache.Editor editor) throws IOException, FileNotFoundException {
        OutputStream out = null;

        try {
            out = new BufferedOutputStream (editor.newOutputStream (0), Utils.IO_BUFFER_SIZE);
            return bitmap.compress (compressFormat, compressQuality, out);
        } finally {
            if (out != null) {
                out.close ();
            }
        }
    }

    private File getDiskCacheDir (Context context, String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals (
                        Environment.getExternalStorageState ()) ||
                        !Utils.isExternalStorageRemovable () ?
                        Utils.getExternalCacheDir (context).getPath () :
                        context.getCacheDir().getPath ();

        return new File (cachePath + File.separator + uniqueName);
    }

    private void put (String key, Bitmap data) {
        DiskLruCache.Editor editor = null;

        try {
            editor = diskCache.edit (key);
            if (editor == null) return;

            if (writeBitmapToFile (data, editor)) {
                diskCache.flush ();
                editor.commit ();
            }

        } catch (IOException e) {
            if (BuildConfig.DEBUG) {

            }
            try {
                if (editor != null) {
                    editor.abort ();
                }
            } catch (IOException dismiss) {}
        }
    }

    public Bitmap getBitmap (String key) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;

        try {
            snapshot = diskCache.get (key);
            if (snapshot == null) return null;

            final InputStream in = snapshot.getInputStream (0);
            if (in != null) {
                final BufferedInputStream buffer = new BufferedInputStream (in, Utils.IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream (buffer);
            }
        } catch (IOException e) {

        } finally {
            if (snapshot != null) {
                snapshot.close ();
            }
        }
        return bitmap;
    }

    public boolean containsKey (String key) {
        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;

        try {
            snapshot = diskCache.get (key);
            contained = snapshot != null;
        } catch (IOException e) {

        } finally {
            if (snapshot != null) {
                snapshot.close ();
            }
        }

        return contained;
    }

    public void clearCache () {
        try {
            diskCache.delete ();
        } catch (IOException e) {

        }
    }

    public File getCacheFolder () {
        return diskCache.getDirectory ();
    }

}
