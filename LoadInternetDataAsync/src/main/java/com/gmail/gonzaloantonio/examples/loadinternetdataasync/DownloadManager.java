package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadManager {
    public InputStream download (String uri) {
        URL url;
        HttpURLConnection client = null;

        try {
            url = new URL(uri);
            client = (HttpURLConnection) url.openConnection();
            final int statusCode = client.getResponseCode();

            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return client.getInputStream ();
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        } finally {
            if (client != null) {
                client.disconnect ();
            }
        }

        return null;
    }

}

/*public class DownloadManager {
    boolean isSampled;
    final int thumbWidth = 150;
    final int thumbHeight = 150;
    int fullWidth;
    int fullHeight;

    public DownloadManager (boolean isThumbnail) {
        this.isSampled = isThumbnail;
    }

    public DownloadManager (boolean isSampled, int width, int height) {
        this.isSampled = isSampled;
        //fullWidth = width;
        //fullHeight = height;
    }

    public Bitmap download (String uri) {
        try {
            URL url = new URL (uri);
            HttpURLConnection client = (HttpURLConnection) url.openConnection ();
            final int statusCode = client.getResponseCode ();

            if (statusCode != 200) {
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
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream (buffer, null, options);

                if (isSampled) {
                    options.inSampleSize = calculateInSampleSize (options, thumbWidth, thumbHeight);
                } else {
                    fullWidth = options.outWidth / 2;
                    fullHeight = options.outHeight / 2;
                    options.inSampleSize = calculateInSampleSize (options, fullWidth, fullHeight);
                }

                options.inJustDecodeBounds = false;
                buffer.reset ();

                Bitmap bitmap;
                if (isSampled) {
                    bitmap = scaleImage (BitmapFactory.decodeStream (buffer, null, options), thumbWidth, thumbHeight);
                } else {
                    bitmap = scaleImage (BitmapFactory.decodeStream (buffer, null, options), fullWidth, fullHeight);
                }

                return bitmap;
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
    }

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
}*/
