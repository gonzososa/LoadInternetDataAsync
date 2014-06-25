package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewWeakReference;
    private String url;

    public DownloadImageTask (ImageView img) {
        imageViewWeakReference = new WeakReference<ImageView>(img);
    }

    public String getURL () {
        return url;
    }

    @Override
    protected Bitmap doInBackground (String...urls) {
        url = urls [0];

        Bitmap bitmap = getBitmapFromDiskCache (url);
        if (bitmap != null) return bitmap;

        return downloadBitmap (url);
    }

    @Override
    protected void onPostExecute (Bitmap result) {
        if (isCancelled ()) {
            result = null;
        }

        ImageView imageView = imageViewWeakReference.get ();
        if (imageView != null && result != null) {
            imageView.setImageBitmap (result);
            addBitmapToMemoryCache (url, result);
            if (Utils.diskCache != null && !Utils.diskCache.containsKey (Utils.createMD5String (url))) {
                addBitmapToDiskCache (url, result);
            }
        }

        super.onPostExecute (result);
    }

    private void addBitmapToMemoryCache (String key, Bitmap bitmap) {
        MemoryCache.addBitmapToMemoryCache (key, bitmap);
    }

    private Bitmap downloadBitmap (String uri) {
        try {
            URL url = new URL (uri);
            HttpURLConnection client = (HttpURLConnection) url.openConnection ();
            final int statusCode = client.getResponseCode ();

            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream ();
            byte [] bitmapBytes = new byte [1024 * 4];
            InputStream inputStream = client.getInputStream ();
            int i;

            while ((i = inputStream.read (bitmapBytes)) != -1) {
                bos.write (bitmapBytes, 0, i);
            }

            byte[] buffer = bos.toByteArray ();
            inputStream.close ();
            inputStream = null;
            bos.close ();
            bos = null;

            BitmapFactory.Options options = new BitmapFactory.Options ();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray (buffer, 0, buffer.length, options);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = calculateInSampleSize (options, Utils.thumbnailWidth, Utils.thumbnailHeight);
            options.inJustDecodeBounds = false;

            return scaleImage (BitmapFactory.decodeByteArray (buffer, 0, buffer.length, options), Utils.thumbnailWidth, Utils.thumbnailHeight);
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }

        return null;
    }

/*    private Bitmap downloadBitmap (String uri) {
        try {
            URL url = new URL (uri);
            HttpURLConnection client = (HttpURLConnection) url.openConnection ();
            final int statusCode = client.getResponseCode ();

            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = null;
            BufferedInputStream buffer;
            int BUFFER_SIZE = 16 * 1024;

            try {
                inputStream = client.getInputStream ();
                buffer = new BufferedInputStream (new FlushedInputStream (inputStream), BUFFER_SIZE);
                buffer.mark (BUFFER_SIZE);

                BitmapFactory.Options options = new BitmapFactory.Options ();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream (buffer, null, options);
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = calculateInSampleSize (options, Utils.thumbnailWidth, Utils.thumbnailHeight);
                options.inJustDecodeBounds = false;

                buffer.reset ();
                return scaleImage (BitmapFactory.decodeStream (buffer, null, options), Utils.thumbnailWidth, Utils.thumbnailHeight);
            } finally {
                if (inputStream != null) {
                    inputStream.close ();
                    inputStream = null;
                }

                client.disconnect ();
                client = null;
                uri = null;
            }
        } catch (MalformedURLException e) {
            //Log.i("JENSELTER", "Error Message:" + e.toString());
        } catch (IOException e) {
            //Log.i ("JENSELTER", "Error Message: " + e.toString ());
        }

        return null;
    }*/

    private int calculateInSampleSize (BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while (((halfHeight / inSampleSize) > reqHeight) && ((halfWidth / inSampleSize) > reqWidth)) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap scaleImage (Bitmap bitmap, int width, int height) {
        int oWIdth = bitmap.getWidth ();
        int oHeight = bitmap.getHeight ();

        if (oWIdth > oHeight) {
            height = (int) (((double) oHeight / (double) oWIdth) * height);
        } else if (oHeight > oWIdth) {
            width = (int) (((double) oWIdth / (double) oHeight) * width);
        }

        Bitmap b = Bitmap.createScaledBitmap (bitmap, width, height, true);
        bitmap = null;
        return b;
    }

    private void addBitmapToDiskCache (String key, Bitmap bitmap) {
        synchronized (Utils.diskCacheLock) {
            key = Utils.createMD5String (key);
            if (Utils.diskCache != null && !Utils.diskCache.containsKey (key)) {
                Utils.diskCache.put (key, bitmap);
            }
        }
    }

    private Bitmap getBitmapFromDiskCache (String key) {
        synchronized (Utils.diskCacheLock) {
            while (Utils.diskCacheStarting) {
                try {
                    Utils.diskCacheLock.wait ();
                } catch (InterruptedException e) {

                }
            }

            key = Utils.createMD5String (key);
            if (Utils.diskCache != null && Utils.diskCache.containsKey (key)) {
                return Utils.diskCache.getBitmap (key);
            }
        }

        return null;
    }
}