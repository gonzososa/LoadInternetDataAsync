package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.jakewharton.disklrucache.DiskLruCache;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

public class MainActivity extends Activity {
    //private ImageView img;
    private String [] urls = {
            "https://pbs.twimg.com/media/BkovgPLIAAA58ZL.jpg",
            "https://pbs.twimg.com/media/BnCzJNgIUAAIsiJ.jpg",
            "https://pbs.twimg.com/media/Bmv_RAfIMAAm0aa.jpg",
            "https://pbs.twimg.com/media/BnJKb4_CYAEVtV9.jpg",
            "https://pbs.twimg.com/media/BnPrWI3CEAAIzSP.jpg",
            "https://pbs.twimg.com/media/BnEJAzoCIAAT4ZD.jpg",
            "https://pbs.twimg.com/media/BnJqdCKCMAASTxj.jpg",
            "https://pbs.twimg.com/media/Bm4ll80IQAAp9qP.jpg",
            "https://pbs.twimg.com/media/Bm4N-biIAAA1o57.jpg"
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
        button.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            listView1.setAdapter (
                new ArrayAdapter<String> (MainActivity.this, R.id.list_item, urls) {
                    @Override
                    public View getView (int position, View convertView, ViewGroup parent) {
                        ImageView img;

                        if (convertView == null) {
                            img = new ImageView (parent.getContext ());
                            img.setMinimumHeight (200);
                            img.setPadding (7, 7, 7, 7);
                        } else {
                            img = (ImageView) convertView;
                        }

                        /*LayoutInflater inflater = (LayoutInflater) getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                        View listItem = inflater.inflate (R.layout.list_item, parent, false);
                        ImageView img = (ImageView) listItem.findViewById (R.id.imageView1);

                        new DownloadImageTask(img).execute (urls [position]);*/
                        Log.i ("JENSELTER", urls [position]);
                        String key = urls [position];
                        if (getBitmapFromMemCache(key) != null) {
                            img.setImageBitmap (getBitmapFromMemCache (key));
                        }
                        else {
                            img.setImageDrawable(new ColorDrawable(Color.BLACK));
                            new DownloadImageTask(img).execute(urls[position]);
                        }

                        return img;
                    }
                }
            );
            }
        });
    }

    private void addBitmapToMemoryCache (String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memCache.put (key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache (String key)  {
        return memCache.get (key);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private ImageView img;
        //private final WeakReference<ImageView> imageViewWeakReference;

        public DownloadImageTask (ImageView img) {
            this.img = img;
            //imageViewWeakReference = new WeakReference<ImageView>(img);
        }

        @Override
        protected Bitmap doInBackground (String...urls) {
            url = String.valueOf (urls [0]);
            return downloadBitmap(url);
        }

        @Override
        protected void onPostExecute (Bitmap result) {
            if (isCancelled ()) {
                result = null;
            }

            /*if (imageViewWeakReference != null) {
                ImageView imageView = imageViewWeakReference.get ();
                if (imageView != null) {
                    imageView.setImageBitmap (result);
                }
            }*/

            addBitmapToMemoryCache (url, result);
            img.setImageBitmap (result);

            super.onPostExecute (result);
        }

        private Bitmap downloadBitmap (String url) {
            final int IO_BUFFER_SIZE = 1024 * 4;
            final HttpClient client = AndroidHttpClient.newInstance ("Android");
            final HttpGet getRequest = new HttpGet (url);

            try {
                HttpResponse response = client.execute (getRequest);
                final int statusCode = response.getStatusLine().getStatusCode ();

                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                final HttpEntity entity  = response.getEntity ();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = entity.getContent ();

                        return scaleImage (BitmapFactory.decodeStream (new FilterInputStream (inputStream) {
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
                            }), 100, 100);

                    } finally {
                        if (inputStream != null) {
                            inputStream.close ();
                        }

                        entity.consumeContent ();
                    }
                }
            } catch (IOException e) {
                getRequest.abort ();
            }
            catch (IllegalStateException e) {
                getRequest.abort ();
            }
            catch (Exception e) {
                getRequest.abort ();
            }
            finally {
                ((AndroidHttpClient) client).close ();
            }

            return null;
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

            return Bitmap.createScaledBitmap (bitmap, width, height, false);
        }
    }

    public File getDiskCacheDir (Context context, String uniqueName) {
        //String cachePath =
        //        Environment.MEDIA_MOUNTED.equals (Environment.getExternalStorageState ()) ||
        //                !Environment.isExternalStorageRemovable() ? getExternalCacheDir().getPath () :
        //                context.getCacheDir().getPath();
        return null;
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
