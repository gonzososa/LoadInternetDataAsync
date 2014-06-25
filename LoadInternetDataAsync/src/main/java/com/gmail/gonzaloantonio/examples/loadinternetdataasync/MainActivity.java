package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private String [] urls;
    private ListView listView1;
    private ProgressBar progressBarMain;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        listView1 = (ListView) findViewById (R.id.listView1);
        progressBarMain = (ProgressBar) findViewById (R.id.progressBarMain);

        DisplayMetrics metrics = new DisplayMetrics ();
        getWindowManager().getDefaultDisplay().getMetrics (metrics);
        int deviceScreenHeight = metrics.heightPixels;
        if  (deviceScreenHeight < 480) {
            Utils.thumbnailHeight = 150;
            Utils.thumbnailWidth = 150;
        } else {
            Utils.thumbnailHeight = 250;
            Utils.thumbnailWidth = 250;
        }

        getSupportActionBar().setTitle ("Jen's Gallery");

        if (savedInstanceState == null) {
            new Runnable() {
                @Override
                public void run() {
                    synchronized (Utils.diskCacheLock) {
                        Utils.diskCache = new DiskLRUCacheWrapper(getBaseContext(), Utils.UniqueName, Utils.SizeOfCache);
                        Utils.diskCacheStarting = false;
                        Utils.diskCacheLock.notifyAll();
                    }
                }
            }.run();
        }

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), ZoomActivity.class);
                intent.putExtra("URL", (String) adapterView.getAdapter().getItem(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState (savedInstanceState);
        savedInstanceState.putStringArray ("URLJENSELTER", urls);
    }

    @Override
    public void onRestoreInstanceState (Bundle savedInstanceState) {
        super.onRestoreInstanceState (savedInstanceState);
        progressBarMain.setVisibility (View.GONE);
        listView1.setVisibility (View.VISIBLE);
        urls = savedInstanceState.getStringArray ("URLJENSELTER");
        load ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate (R.menu.main_activity, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load: {
                progressBarMain.setVisibility (View.VISIBLE);
                new DownloadLinksTask().execute ("https://dl.dropboxusercontent.com/u/52679306/jen.txt");
                break;
            }
        }

        return super.onOptionsItemSelected (item);
    }

    private void load () {
        listView1.setAdapter (new ArrayAdapter<String>(MainActivity.this, R.id.list_item, urls) {
            @Override
            public View getView (int position, View convertView, ViewGroup parent) {
                ImageView img;

                if (convertView == null) {
                    img = new ImageView (parent.getContext ());
                    img.setMinimumHeight (Utils.thumbnailHeight);
                    img.setPadding (7, 7, 7, 7);
                } else {
                    img = (ImageView) convertView;
                }

                String key = urls [position];
                cancelPotentialDownload (key, img);
                Bitmap b = getBitmapFromMemCache (key);
                if (b != null) {
                    img.setImageBitmap (b);
                } else {
                    DownloadImageTask task = new DownloadImageTask (img);
                    img.setImageDrawable (new DefaultDrawable (task));
                    task.execute (urls [position]);
                }

                return img;
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

    public Bitmap getBitmapFromMemCache (String key)  {
        return MemoryCache.getBitmapFromMemoryCache (key);
    }

    class DownloadLinksTask extends AsyncTask<String, Void, String []> {
        @Override
        public String [] doInBackground (String...params) {
            return download (params [0]);
        }

        @Override
        public void onPostExecute (String [] result) {
            if (isCancelled ()) {
                result = null;
            }

            if (result != null && listView1  != null) {
                progressBarMain.setVisibility (View.GONE);
                listView1.setVisibility (View.VISIBLE);
                urls = result;
                load ();
            }
        }

        private String [] download (String url) {
            ArrayList<String> urls = new ArrayList<String> ();

            HttpURLConnection client;

            try {
                // WTF!
                //HttpClient client = new DefaultHttpClient();
                //HttpGet httpGet = new HttpGet(url);
                //HttpResponse response = client.execute(httpGet);
                //HttpEntity entity = response.getEntity ();
                //BufferedHttpEntity buffer = new BufferedHttpEntity (entity);
                //InputStream inputStream = buffer.getContent ();

                client = (HttpURLConnection) new URL(url).openConnection ();
                if (client.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream inputStream = client.getInputStream ();
                BufferedReader reader = new BufferedReader (new InputStreamReader (inputStream));
                String line;

                while ((line = reader.readLine()) != null ) {
                    urls.add (line);
                }

                inputStream.close ();
                client.disconnect ();
            } catch (IOException e) {

            }

            return urls.toArray (new String [urls.size ()]);
        }
    }
}
