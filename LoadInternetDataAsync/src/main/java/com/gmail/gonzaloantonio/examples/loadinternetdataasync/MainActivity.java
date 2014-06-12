package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
//import android.os.AsyncTask;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.jakewharton.disklrucache.DiskLruCache;

//import java.io.BufferedInputStream;
import java.io.File;

public class MainActivity extends Activity {
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
            "https://pbs.twimg.com/media/BpuHSuNCQAA2K1W.jpg:large",
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

    private DiskCache dc = null;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        final ListView listView1 = (ListView) findViewById (R.id.listView1);

        //dc = new DiskCache (this, "thumbnails", (10 * 1024 * 1024), Bitmap.CompressFormat.JPEG, 90);
        /*Foo f = new Foo (this, "thumbnails", (10 * 1024 * 1024), Bitmap.CompressFormat.JPEG, 90);
        InitializeDiskCacheTask initializeDiskCache = new InitializeDiskCacheTask();
        initializeDiskCache.execute(f);*/

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

                    String key = urls[position];
                    cancelPotentialDownload (key, img);
                    Bitmap b = getBitmapFromMemCache (key);
                    if (b != null) {
                        //Log.i ("JENSELTER", "Recovering image from mem cache: " + key);
                        img.setImageBitmap (b);
                    } else {
                        //if (cancelPotentialDownload (key, img)) {
                        DownloadImageTask task = new DownloadImageTask (img);
                        img.setImageDrawable (new DefaultDrawable (task));
                        task.execute (urls [position]);
                        //}
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
            String imageUrl = task.getURL ();
            if ((imageUrl == null) || (!imageUrl.equals (url))) {
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

    /*private void addBitmapToMemoryCache (String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) != null) {
            //memCache.put (key, bitmap);
            MemoryCache.addBitmapToMemoryCache (key, bitmap);
        }
    }*/

    public Bitmap getBitmapFromMemCache (String key)  {
        //return memCache.get (key);
        return MemoryCache.getBitmapFromMemoryCache (key);
    }

    class InitializeDiskCacheTask extends AsyncTask<Foo, Void, Void> {
        @Override
        protected Void doInBackground (Foo...params) {
            Foo f = params [0];
            dc = new DiskCache (f.getContext(), f.getUniqueName(), f.getSize(), f.getFormat(), f.getQuality());
            return null;
        }
    }

    class Foo {
        private Context context;
        private String uniqueName;
        private int size;
        private Bitmap.CompressFormat format;
        private int quality;

        public String x;

        public Foo (Context context, String uniqueName, int size, Bitmap.CompressFormat format, int quality) {
            this.context = context;
            this.uniqueName = uniqueName;
            this.size = size;
            this.format = format;
            this.quality = quality;
        }

        public Context getContext () {
            return context;
        }

        public String getUniqueName () {
            return uniqueName;
        }

        public int getSize () {
            return size;
        }

        public Bitmap.CompressFormat getFormat () {
            return format;
        }

        public int getQuality () {
            return  quality;
        }
    }

    /*private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
                }
            } catch (MalformedURLException e) {
                Log.i ("JENSELTER", "Error Message:" + e.toString ());
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
    }*/

    /*public File getDiskCacheDir (Context context, String uniqueName) {
        //String cachePath =
        //        Environment.MEDIA_MOUNTED.equals (Environment.getExternalStorageState ()) ||
        //                !Environment.isExternalStorageRemovable() ? getExternalCacheDir().getPath () :
        //                context.getCacheDir().getPath();
        return null;
    }*/

    /*private class DefaultDrawable extends ColorDrawable {
        WeakReference<DownloadImageTask> downloadTask;

        public DefaultDrawable (DownloadImageTask task) {
            super (Color.BLACK);
            downloadTask = new WeakReference<DownloadImageTask> (task);
        }

        public DownloadImageTask getDownloadImageTask () {
            return downloadTask != null ? downloadTask.get () : null;
        }
    }*/
}
