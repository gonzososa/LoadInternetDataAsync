package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

        getSupportActionBar().setHomeAsUpIndicator (0);
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar().setHomeButtonEnabled (true);
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
            //if (orientation == 1) {
                return new DownloadManager (false, screenWidth, screenHeight).download (url);
            //} else if (orientation == 2) {
            //    return new DownloadManager (false, screenHeight, screenWidth).download (url);
            //}

            //return null;
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
    }

}
