package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
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
        if (Utils.diskCache != null) {
            Log.i ("JENSELTER", "Diskcache working!");
        }
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
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream (buffer, null, options);
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = calculateInSampleSize (options, 150, 150);
                options.inJustDecodeBounds = false;

                buffer.reset ();
                return scaleImage (BitmapFactory.decodeStream (buffer, null, options), 150, 150);
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
            Log.i("JENSELTER", "Error Message:" + e.toString());
        } catch (IOException e) {
            Log.i ("JENSELTER", "Error Message: " + e.toString ());
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
}