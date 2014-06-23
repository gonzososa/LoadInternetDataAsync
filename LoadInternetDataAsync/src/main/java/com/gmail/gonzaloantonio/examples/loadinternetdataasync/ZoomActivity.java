package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ZoomActivity extends ActionBarActivity {
    Menu mMenu;
    String url;
    ImageView img;

    int screenHeight;
    int screenWidth;
    int orientation;

    ProgressBar progressBar;
    AsyncTask task;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.zoom_activity);

        //getSupportActionBar().setHomeAsUpIndicator (0);
        //getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        //getSupportActionBar().setHomeButtonEnabled (true);
        getSupportActionBar().setTitle ("Jen's Gallery");

        img = (TouchImageView) findViewById (R.id.touchView1);
        progressBar = (ProgressBar) findViewById (R.id.progressBar1);
        orientation = getResources().getConfiguration().orientation;

        DisplayMetrics metrics = new DisplayMetrics ();
        getWindowManager().getDefaultDisplay().getMetrics (metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        Intent intent  = getIntent ();
        url = intent.getStringExtra ("URL");

        task = new DownloadFullImageTask(this).execute (url);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate (R.menu.zoom_activity, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.save:
                Toast.makeText (this, "¡Saving original image!", Toast.LENGTH_LONG).show();
                break;
            case R.id.refresh: {
                // this is unnecessary i think
                /*try {
                    Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap ();
                    bitmap.recycle ();
                }
                catch (NullPointerException e) {}
                catch (Exception e) {}*/

                img.setImageBitmap (null);
                img.setVisibility (View.GONE);
                progressBar.setVisibility (View.VISIBLE);
                task = new DownloadFullImageTask(this).execute (url);
                break;
            }
        }

        return super.onOptionsItemSelected (item);
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

            int lado_mayor_dispositivo = screenWidth > screenHeight ? screenWidth : screenHeight;
            int BUFFER_SIZE = 16 * 1024;
            int orientation = -1;

            HttpURLConnection client;
            BufferedInputStream buffer;

            try {
                client = (HttpURLConnection) new URL (url).openConnection ();

                if (client.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                buffer = new BufferedInputStream (new FlushedInputStream (client.getInputStream ()), BUFFER_SIZE);
                buffer.mark (BUFFER_SIZE);

                BitmapFactory.Options options = new BitmapFactory.Options ();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream (buffer, null, options);

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
                buffer.reset();

                Bitmap bitmap = BitmapFactory.decodeStream (buffer, null, options);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap (bitmap, ancho_imagen_escalada, largo_imagen_escalada, true);

                bitmap.recycle ();
                buffer.close ();
                client.disconnect ();

                return scaledBitmap;
            } catch (MalformedURLException e) {

            } catch (IOException e) {

            } catch (NullPointerException e) {

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

}
