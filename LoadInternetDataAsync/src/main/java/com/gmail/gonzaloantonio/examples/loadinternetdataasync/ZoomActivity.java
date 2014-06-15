package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ZoomActivity extends ActionBarActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        getSupportActionBar().setHomeAsUpIndicator (0);
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar().setHomeButtonEnabled (true);

        ImageView img = new TouchImageView (this);
        img.setBackgroundColor (Color.BLACK);

        progressBar = new ProgressBar (this);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setSupportProgressBarIndeterminateVisibility (true);
        setContentView (img);

        Intent intent  = getIntent ();
        img.setImageBitmap(MemoryCache.getBitmapFromMemoryCache(intent.getStringExtra ("URL")));
    }

    @Override
    public void onStart () {

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
        }

        return super.onOptionsItemSelected (item);
    }

    class l implements View.OnTouchListener {
        @Override
        public boolean onTouch (View view, MotionEvent event) {
            return true;
        }
    }

}
