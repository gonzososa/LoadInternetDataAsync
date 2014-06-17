package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ZoomActivity extends ActionBarActivity {
    String url;
    ImageView img;

    int screenHeight;
    int screenWidth;
    int orientation;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        supportRequestWindowFeature (Window.FEATURE_INDETERMINATE_PROGRESS);

        getSupportActionBar().setHomeAsUpIndicator (0);
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar().setHomeButtonEnabled (true);

        getSupportActionBar().setTitle ("Jen's Gallery");

        img = new TouchImageView (this);
        img.setBackgroundColor (Color.BLACK);
        img.setPadding (0, 0, 0, 0);

        orientation = getResources().getConfiguration().orientation;

        DisplayMetrics metrics = new DisplayMetrics ();
        getWindowManager().getDefaultDisplay().getMetrics (metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        img.setMinimumHeight(screenHeight);
        img.setMinimumWidth(screenWidth);

        setContentView(img);
        setSupportProgressBarIndeterminateVisibility (true);

        Intent intent  = getIntent ();
        url = intent.getStringExtra ("URL");
        img.setImageBitmap (MemoryCache.getBitmapFromMemoryCache (url));

        new DownloadFullImageTask(this).execute (url);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate (R.menu.zoom_activity, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.save:
                Toast.makeText (this, "¡Saving original image!", Toast.LENGTH_LONG).show();
                break;
            case R.id.refresh: {
                break;
            }
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onStop () {
        //Log.i ("JENSELTER", "Stopping activity Zoom");
        cleanup ();
        super.onStop ();
    }

    public void onDestroy () {
        //Log.i ("JENSELTER", "Destroying activity Zoom");
        super.onDestroy ();
    }

    @Override
    public void onBackPressed () {
        //Log.i ("JENSELTER", "Back Pressed activity Zoom");
        cleanup ();
        super.onBackPressed ();
    }

    private void cleanup () {
        if (img != null) {
            img.destroyDrawingCache ();

            try {
                ((BitmapDrawable) img.getDrawable()).getBitmap().recycle ();
            } catch (NullPointerException e) {

            } catch (Exception e) {

            }

            img = null;
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

            /*ProgressBar progressBar = new ProgressBar (context, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setPadding (5, 5, 5, 5);
            progressBar.setIndeterminate (true);
            MenuItem menuItem = mMenu.getItem (1);
            MenuItemCompat.setActionView (menuItem, progressBar);
            MenuItemCompat.expandActionView (menuItem);*/
        }

        @Override
        protected Bitmap doInBackground (String...params) {
            String url = (params [0]);
            if (orientation == 1) {
                return new DownloadManager (false, screenWidth, screenHeight).download (url);
            } else if (orientation == 2) {
                return new DownloadManager (false, screenHeight, screenWidth).download (url);
            }

            return null;
        }

        @Override
        protected void onPostExecute (Bitmap result) {
            if (isCancelled ()) {
                result = null;
            }

            if (result != null) {
                setSupportProgressBarIndeterminateVisibility(false);
                img.setImageBitmap (result);
            }

            super.onPostExecute (result);
        }
    }

}
