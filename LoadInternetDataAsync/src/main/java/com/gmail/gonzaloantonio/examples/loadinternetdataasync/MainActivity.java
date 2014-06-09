package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    //private ImageView img;
    private String [] urls = {
            "https://pbs.twimg.com/profile_images/462981550003740672/Jb-UpOux.jpeg",
            "https://pbs.twimg.com/media/BoHECEXIgAEwrDi.jpg:large",
            "https://pbs.twimg.com/media/BnemgXxCIAA-aif.jpg:large",
            "https://pbs.twimg.com/media/BnXGVKbCAAA4LoV.jpg:large",
            "https://pbs.twimg.com/media/BcMu4KhIMAAJSqC.jpg:large",
            "https://pbs.twimg.com/media/Bb9qAagIgAAfSvg.jpg:large",
            "https://pbs.twimg.com/media/BoU_qgLCMAAnAai.jpg:large",
            "https://pbs.twimg.com/media/Bo0_K-2CcAArf_4.jpg:large",
            "https://pbs.twimg.com/media/BjBZ9RLIIAEmDJ1.jpg:large",
            "https://pbs.twimg.com/media/BkovgPLIAAA58ZL.jpg:large",
            "https://pbs.twimg.com/media/BklXhMBIIAAAlvx.jpg:large",
            "https://pbs.twimg.com/media/Bkjq2N4IEAE335P.jpg:large",
            "https://pbs.twimg.com/media/Bka0wlkIIAAn1k8.jpg:large",
            "https://pbs.twimg.com/media/BkaETdSCQAAfb7G.jpg:large",
            "https://pbs.twimg.com/media/BkQFryrIcAAiodL.jpg:large",
            "https://pbs.twimg.com/media/BkOpckpCcAA_70R.jpg:large",
            "https://pbs.twimg.com/media/BpkWF6gIcAAxM6l.jpg:large",
            "https://pbs.twimg.com/media/BpkKnBXIEAAHFDp.jpg:large",
            "https://pbs.twimg.com/media/BpkJvfCIAAILJc-.jpg:large",
            "https://pbs.twimg.com/media/BkOcfdJCcAEH0MG.jpg:large",
            "https://pbs.twimg.com/media/BkEoEADIEAA3obl.jpg:large",
            "https://pbs.twimg.com/media/Bj_tyNaIIAAqOp7.jpg:large",
            "https://pbs.twimg.com/media/Bj1dPJqCAAA8m2Y.jpg:large",
            "https://pbs.twimg.com/media/Bjwes9ICYAA7u-H.png:large",
            "https://pbs.twimg.com/media/BjwUBCICAAAFS0m.jpg:large",
            "https://pbs.twimg.com/media/BkovgPLIAAA58ZL.jpg:large",
            "https://pbs.twimg.com/media/BpabvckCQAAT4ll.jpg:large",
            "https://pbs.twimg.com/media/BnCzJNgIUAAIsiJ.jpg:large",
            "https://pbs.twimg.com/media/BppFs51IAAAQ60s.jpg:large",
            "https://pbs.twimg.com/media/BpkYt8DCAAAmaZ8.jpg:large",
            "https://pbs.twimg.com/media/BnPrWI3CEAAIzSP.jpg:large",
            "https://pbs.twimg.com/media/BnEJAzoCIAAT4ZD.jpg:large",
            "https://pbs.twimg.com/media/BnJqdCKCMAASTxj.jpg:large",
            "https://pbs.twimg.com/media/Bm4ll80IQAAp9qP.jpg:large",
            "https://pbs.twimg.com/media/Bm4N-biIAAA1o57.jpg:large"
    };

    private LruCache<String, Bitmap> memCache;
    private DiskLruCache diskCache;

    private final Object diskCacheLock = new Object ();
    private final boolean diskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memCache = new LruCache<String, Bitmap>(cacheSize);

        File cacheDir = getDiskCacheDir (this, DISK_CACHE_SUBDIR);
        //diskCache = DiskLruCache.open (cacheDir);

        final ListView listView1 = (ListView) findViewById (R.id.listView1);


        Button button = (Button) findViewById (R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            listView1.setAdapter (new ArrayAdapter<String>(MainActivity.this, R.id.list_item, urls) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ImageView img;

                    if (convertView == null) {
                        img = new ImageView(parent.getContext());
                        img.setMinimumHeight (150);
                        img.setPadding(7, 7, 7, 7);
                    } else {
                        img = (ImageView) convertView;
                    }

                    /*LayoutInflater inflater = (LayoutInflater) getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                    View listItem = inflater.inflate (R.layout.list_item, parent, false);
                    ImageView img = (ImageView) listItem.findViewById (R.id.imageView1);

                    new DownloadImageTask(img).execute (urls [position]);*/
                    //Log.i("JENSELTER", "URL: " + urls[position]);
                    String key = urls[position];
                    Bitmap b = getBitmapFromMemCache (key);
                    if (b != null) {
                        img.setImageBitmap (b);
                    } else {
                        if (cancelPotentialDownload (key, img)) {
                            DownloadImageTask task = new DownloadImageTask (img);
                            img.setImageDrawable (new DefaultDrawable (task));
                            task.execute (urls[position]);
                        }
                    }

                    return img;
                }
            });
            }
        });
    }

    private boolean cancelPotentialDownload (String url, ImageView imageView) {
        DownloadImageTask task = getDownloadImageTask (imageView);

        if (task != null) {
            String imageUrl = task.url;
            if ((imageUrl == null) || (!imageUrl.equals (url))) {
                Log.i ("JENSELTER", "Canceling task  for url: " + (imageUrl != null ? imageUrl : "URL Empty!!"));
                task.cancel (true);
            } else {
                return false;
            }
        }

        return true;
    }

    private DownloadImageTask getDownloadImageTask (ImageView imageView) {
        if (imageView != null) {
            Drawable d = imageView.getDrawable ();
            if (d instanceof DefaultDrawable) {
                return ((DefaultDrawable) d).getDownloadImageTask ();
            }
        }

        return null;
    }

    private void addBitmapToMemoryCache (String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) != null) {
            memCache.put (key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache (String key)  {
        return memCache.get (key);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private String url;

        public DownloadImageTask (ImageView img) {
            imageViewWeakReference = new WeakReference<ImageView>(img);
        }

        @Override
        protected Bitmap doInBackground (String...urls) {
            url = String.valueOf (urls [0]);
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

        private Bitmap downloadBitmap (String uri) {
            //final int IO_BUFFER_SIZE = 1024 * 4;
            //final HttpClient client = AndroidHttpClient.newInstance ("Android");
            //final HttpGet getRequest = new HttpGet (url);

            try {
                URL url = new URL (uri);
                HttpURLConnection client = (HttpURLConnection) url.openConnection ();
                //HttpResponse response = client.execute (getRequest);
                final int statusCode = client.getResponseCode (); //response.getStatusLine().getStatusCode ();

                if (statusCode != 200) {
                    //.i ("JENSELTER", "Status Error: " + statusCode);
                    return null;
                }

                //final HttpEntity entity  = response.getEntity ();
                //if (entity != null) {
                    InputStream inputStream = null;
                    BufferedInputStream buffer;

                    try {
                        //inputStream = entity.getContent ();
                        inputStream = client.getInputStream ();
                        buffer = new BufferedInputStream (new FlushedInputStream (inputStream));
                        BitmapFactory.Options options = new BitmapFactory.Options ();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream (buffer, null, options);
                        options.inSampleSize = calculateInSampleSize (options, 150, 150);
                        options.inJustDecodeBounds = false;
                        //return scaleImage (BitmapFactory.decodeStream (inputStream), 100, 100);
                        buffer.reset ();
                        return scaleImage (BitmapFactory.decodeStream (buffer, null, options), 150, 150);

                        /*return scaleImage (BitmapFactory.decodeStream (new FilterInputStream (inputStream) {
                            @Override
                            public long skip (long n) throws IOException {
                                long totalBytesSkipped = 0L;

                                while (totalBytesSkipped < n) {
                                    long bytesSkipped = in.skip (n - totalBytesSkipped);
                                    if (bytesSkipped == 0L) {
                                        int b = read ();
                                        if (b < 0)
                                            break;
                                        else
                                            bytesSkipped = 1;
                                    }
                                    totalBytesSkipped += bytesSkipped;
                                }

                                return totalBytesSkipped;
                            }
                            }, null, options), 150, 150);*/

                    } finally {
                        if (inputStream != null) {
                            inputStream.close ();
                            inputStream = null;
                        }

                        client.disconnect ();
                        client = null;
                        //entity.consumeContent ();
                    }
                //}
            } catch (IOException e) {
                //getRequest.abort ();
                //Log.i ("JENSELTER", "Error Message: " + e.toString ());
            }
            catch (IllegalStateException e) {
                //getRequest.abort ();
                //Log.i ("JENSELTER", "Error Message:" + e.toString ());
            }
            catch (Exception e) {
                //getRequest.abort ();
                //Log.i ("JENSELTER", "Error Message: " + e.toString ());
            }
            finally {
                //((AndroidHttpClient) client).close ();
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

            Log.i ("JENSELTER", String.valueOf (inSampleSize));
            return inSampleSize;
        }

        /*private Bitmap loadImageFromNetwork (String url) {
            Bitmap bitmap = null;

            try {
                URL u = new URL (url);
                InputStream inputStream = (InputStream) u.getContent ();
                bitmap =  BitmapFactory.decodeStream (inputStream);
            } catch (Exception ex) {
                ex.printStackTrace ();
            }

            return scaleImage (bitmap, 200, 200);
        }*/

        /*private Bitmap decodeSampledBitmapFromNetwork (Bitmap bitmap, int reqWidth, int reqHeight) {
            BitmapFactory.Options options = new BitmapFactory.Options ();
            options.inJustDecodeBounds = true;
            return null;
        }*/

        /*private ImageBounds decodeBounds (InputStream stream) {
            ImageBounds bounds = new ImageBounds ();
            BitmapFactory.Options options = new BitmapFactory.Options ();
            options.inJustDecodeBounds = true;
            bounds.setWidth (options.outWidth);
            bounds.setHeight (options.outHeight);
            return bounds;
        }*/

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

    public File getDiskCacheDir (Context context, String uniqueName) {
        //String cachePath =
        //        Environment.MEDIA_MOUNTED.equals (Environment.getExternalStorageState ()) ||
        //                !Environment.isExternalStorageRemovable() ? getExternalCacheDir().getPath () :
        //                context.getCacheDir().getPath();
        return null;
    }

    private class DefaultDrawable extends ColorDrawable {
        WeakReference<DownloadImageTask> downloadTask;

        public DefaultDrawable (DownloadImageTask task) {
            super (Color.BLACK);
            downloadTask = new WeakReference<DownloadImageTask> (task);
        }

        public DownloadImageTask getDownloadImageTask () {
            return downloadTask != null ? downloadTask.get () : null;
        }
    }

    /*class ImageBounds {
        private int height;
        private int width;

        public int getHeigth () {
            return height;
        }

        public int getWidth () {
            return width;
        }

        public void setHeight (int value) {
            height = value;
        }

        public void setWidth (int value) {
            width = value;
        }
    }*/
}
