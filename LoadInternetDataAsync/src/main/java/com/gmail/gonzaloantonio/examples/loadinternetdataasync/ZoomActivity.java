package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

public class ZoomActivity extends ActionBarActivity {
    Menu mMenu;
    String url;
    ImageView img;

    int deviceScreenHeight;
    int deviceScreenWidth;
    int deviceOrientation;

    ProgressBar progressBar;
    AsyncTask task;

    //SerializeBitmap serializator;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.zoom_activity);

        //getSupportActionBar().setHomeAsUpIndicator (0);
        getSupportActionBar().setDisplayHomeAsUpEnabled (false);
        getSupportActionBar().setHomeButtonEnabled (false);
        getSupportActionBar().setTitle ("Jen's Gallery");

        img = (TouchImageView) findViewById (R.id.touchView1);
        progressBar = (ProgressBar) findViewById (R.id.progressBar1);
        deviceOrientation = getResources().getConfiguration().orientation;
        //serializator = new SerializeBitmap ();

        DisplayMetrics metrics = new DisplayMetrics ();
        getWindowManager().getDefaultDisplay().getMetrics (metrics);
        deviceScreenHeight = metrics.heightPixels;
        deviceScreenWidth = metrics.widthPixels;

        Intent intent  = getIntent ();
        url = intent.getStringExtra ("URL");

        if (savedInstanceState == null || MemoryCache.fullSizeImage == null) {
            task = new DownloadFullImageTask(this).execute (url);
        } else if (MemoryCache.fullSizeImage != null) {
            /*try {
                String path = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_PICTURES).getPath ();
                File file = new File (path, "temp.jpg");
                ObjectInputStream inputStream = new ObjectInputStream (new FileInputStream (file));
                Bitmap bitmap = serializator.readObject (inputStream);
                inputStream.close ();

                progressBar.setVisibility (View.GONE);
                img.setVisibility (View.VISIBLE);
                img.setImageBitmap (bitmap);
                Toast.makeText (this, Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_PICTURES).getPath (), Toast.LENGTH_LONG).show();
            } catch (StreamCorruptedException e) {
                Log.i ("JENSELTER", "Error SCEX: " + e.getMessage());
            } catch (IOException e) {
                Log.i ("JENSELTER", "Error IOEX: " + e.getMessage());
            }*/

            progressBar.setVisibility (View.GONE);
            img.setVisibility (View.VISIBLE);
            byte [] bitmapBytes = MemoryCache.fullSizeImage.toByteArray ();
            Bitmap bitmap = BitmapFactory.decodeByteArray (bitmapBytes, 0,bitmapBytes.length);
            img.setImageBitmap (bitmap);
        }
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged (newConfig);
    }

    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState (outState);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.zoom_activity, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.save:
                new SaveOriginalImageTask(this).execute ();
                break;
            case R.id.refresh: {
                // this is unnecessary, i think
                /*try {
                    Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap ();
                    bitmap.recycle ();
                }
                catch (NullPointerException e) {}
                catch (Exception e) {}*/

                img.setImageBitmap (null);
                img.setVisibility (View.GONE);
                progressBar.setVisibility (View.VISIBLE);
                MemoryCache.fullSizeImage = null;
                task = new DownloadFullImageTask(this).execute (url);
                break;
            }
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onDestroy () {
        super.onDestroy ();
    }

    @Override
    public void onStop () {
        cleanup ();
        super.onStop ();
    }

    @Override
    public void onBackPressed () {
        cleanup ();
        super.onBackPressed ();
    }

    private void cleanup () {
        if (img != null) {
            img.destroyDrawingCache ();

            try {
                BitmapDrawable drawable = ((BitmapDrawable) img.getDrawable ());
                Bitmap bitmap = drawable.getBitmap ();
                if (bitmap != null) {
                    bitmap.recycle ();
                    bitmap = null;
                }
                drawable = null;
            } catch (NullPointerException e) {

            } catch (RuntimeException e) {

            } catch (Exception e) {

            }

            img = null;
        }

        if (task != null) {
            if (task.getStatus() == AsyncTask.Status.RUNNING) {
                task.cancel (true);
                task = null;
            }
        }

        //MemoryCache.fullSizeImage = null;
    }

    private class DownloadFullImageTask extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        public DownloadFullImageTask (Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute ();

            /*ProgressBar progressBar = new ProgressBar (context, null, android.R.attr.progressBarStyle);
            progressBar.setPadding (5, 5, 5, 5);
            progressBar.setIndeterminate (true);
            MenuItem menuItem = mMenu != null ? mMenu.findItem (R.id.refresh) : null;
            if (menuItem != null) {
                MenuItemCompat.setActionView (menuItem, progressBar);
            }*/
        }

        @Override
        protected Bitmap doInBackground (String...params) {
            String url = (params [0]);

            int lado_mayor_dispositivo = deviceScreenWidth > deviceScreenHeight ? deviceScreenWidth : deviceScreenHeight;
            //int BUFFER_SIZE = 16 * 1024;
            int orientation = -1;

            HttpURLConnection client;
            //BufferedInputStream buffer;

            try {
                client = (HttpURLConnection) new URL (url).openConnection ();

                if (client.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream ();
                InputStream inputStream = client.getInputStream ();
                byte [] buf = new byte [1024 * 4];
                int i;
                while ((i = inputStream.read (buf)) != -1) {
                    bos.write (buf, 0, i);
                }
                inputStream.close ();
                client.disconnect ();

                byte [] bitmapBytes = bos.toByteArray ();
                MemoryCache.fullSizeImage = bos;
                bos = null;

                //buffer = new BufferedInputStream (new FlushedInputStream (client.getInputStream ()), BUFFER_SIZE);
                //buffer.mark (BUFFER_SIZE);

                BitmapFactory.Options options = new BitmapFactory.Options ();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inJustDecodeBounds = true;
                //BitmapFactory.decodeStream (buffer, null, options);
                BitmapFactory.decodeByteArray (bitmapBytes, 0, bitmapBytes.length, options);

                int ancho_imagen_original = options.outWidth;
                int largo_imagen_original = options.outHeight;

                int lado_mayor_imagen_original;
                if (ancho_imagen_original > largo_imagen_original) {
                    lado_mayor_imagen_original = ancho_imagen_original;
                    orientation = 0; // LANDSCAPE
                } else if (largo_imagen_original > ancho_imagen_original) {
                    lado_mayor_imagen_original = largo_imagen_original;
                    orientation = 1; // PORTRAIT
                } else {
                    lado_mayor_imagen_original = ancho_imagen_original; // SQUARE
                }

                final int BASE_SCALAR;
                if ((lado_mayor_imagen_original / 2) < lado_mayor_dispositivo) {
                    BASE_SCALAR = lado_mayor_dispositivo;
                } else {
                    BASE_SCALAR = lado_mayor_imagen_original / 2;
                }

                int ancho_imagen_escalada = 0;
                int largo_imagen_escalada = 0;
                switch (orientation) {
                    case -1:
                        ancho_imagen_escalada = BASE_SCALAR;
                        largo_imagen_escalada = BASE_SCALAR;
                        break;
                    case 0:
                        ancho_imagen_escalada = BASE_SCALAR;
                        float foo = ((float) largo_imagen_original / (float) ancho_imagen_original) * BASE_SCALAR;
                        largo_imagen_escalada = (int) foo;
                        break;
                    case 1:
                        largo_imagen_escalada = BASE_SCALAR;
                        float bar = ((float) ancho_imagen_original / (float) largo_imagen_original) * BASE_SCALAR;
                        ancho_imagen_escalada = (int) bar;
                        break;
                }

                options.inSampleSize = calculateInSampleSize (options, ancho_imagen_escalada, largo_imagen_escalada);
                options.inJustDecodeBounds = false;
                //buffer.reset();

                //Bitmap bitmap = BitmapFactory.decodeStream (buffer, null, options);
                Bitmap bitmap = BitmapFactory.decodeByteArray (bitmapBytes, 0, bitmapBytes.length, options);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap (bitmap, ancho_imagen_escalada, largo_imagen_escalada, true);
                bitmap.recycle ();

                //buffer.close ();
                //client.disconnect ();

                /*try {
                    File path = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_PICTURES);
                    File file = new File (path, "temp.jpg");
                    path.mkdirs ();
                    ObjectOutputStream outputStream = new ObjectOutputStream (new FileOutputStream (file, false));
                    serializator.writeObject (outputStream, scaledBitmap);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    Log.i ("JENSELTER", "Error FNFEX: " + e.getMessage());
                } catch (IOException e) {
                    Log.i ("JENSELTER", "Error IOEX: " + e.getMessage());
                }*/

                return scaledBitmap;
            } catch (MalformedURLException e) {
                Log.i ("JENSELTER", "Error MFEX: " + e.getMessage ());
            } catch (IOException e) {
                Log.i ("JENSELTER", "Error IOEX: " + e.getMessage ());
            } catch (NullPointerException e) {
                Log.i ("JENSELTER", "Error NPEX: " + e.getMessage ());
            }

            return null;
        }

        @Override
        protected void onPostExecute (Bitmap result) {
            if (isCancelled ()) {
                result = null;
            }

            if (result != null && img != null) {
                progressBar.setVisibility(View.GONE);
                img.setImageBitmap (result);
                img.setVisibility (View.VISIBLE);
            }

            super.onPostExecute (result);
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
    }

    ProgressDialog dialog;

    private class SaveOriginalImageTask extends AsyncTask<Void, Void, Void> {
        private Context context;

        public SaveOriginalImageTask (Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute () {
            dialog = new ProgressDialog (context);
            dialog.setTitle ("Saving...");
            dialog.setMessage ("Please wait.");
            dialog.setCancelable (false);
            dialog.setIndeterminate (true);
            dialog.show ();
        }

        @Override
        protected Void doInBackground (Void...params) {
            try {
                Thread.sleep (3000);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            if (dialog != null) {
                dialog.dismiss ();
                dialog = null;

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String ruta = Environment.getExternalStorageDirectory().getPath();
                    ruta += "/Download/JenSelter/";
                    Toast.makeText (context, "Image saved in: " + ruta, Toast.LENGTH_LONG).show ();
                }
            }
        }
    }

    /*private class SerializeBitmap implements Serializable {
        public void writeObject (ObjectOutputStream out, Bitmap bitmap) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate (bitmap.getHeight() * bitmap.getRowBytes());
            bitmap.copyPixelsToBuffer (buffer);
            byte [] bitmapBytes = buffer.array ();
            out.write (bitmapBytes, 0, bitmapBytes.length);
        }

        public Bitmap readObject (ObjectInputStream in) throws IOException {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream ();
            int b;

            while ((b = in.read()) != -1) {
                byteStream.write (b);
            }

            byte [] bitmapBytes = byteStream.toByteArray ();
            return BitmapFactory.decodeByteArray (bitmapBytes, 0, bitmapBytes.length);
        }
    }*/
}
