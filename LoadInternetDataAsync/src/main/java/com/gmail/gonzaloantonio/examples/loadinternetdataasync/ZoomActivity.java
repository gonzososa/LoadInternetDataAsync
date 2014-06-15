package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ZoomActivity extends ActionBarActivity {
    Menu mMenu;
    String url;
    ImageView img;

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

        setContentView (img);
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

    private class DownloadFullImageTask extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        public DownloadFullImageTask (Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute () {
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
            return new DownloadManager(false).download (url);
        }

        @Override
        protected void onPostExecute (Bitmap result) {
            if (isCancelled ()) {
                result = null;
            }

            if (result != null) {
                setSupportProgressBarVisibility (false);
                img.setImageBitmap (result);
            }

            super.onPostExecute (result);
        }

        private Bitmap downloadFullImage (String uri) {
            return null;
        }
    }

}
