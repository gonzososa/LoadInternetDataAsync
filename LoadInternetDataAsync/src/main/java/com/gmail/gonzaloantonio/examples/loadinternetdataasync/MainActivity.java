package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    /*private String [] urls = {
            "https://pbs.twimg.com/profile_images/462981550003740672/Jb-UpOux.jpeg",
            "https://pbs.twimg.com/media/BqbBadJCIAA9qNm.jpg:large",
            "https://pbs.twimg.com/media/BpkWF6gIcAAxM6l.jpg:large",
            "https://pbs.twimg.com/media/BpkKnBXIEAAHFDp.jpg:large",
            "https://pbs.twimg.com/media/BqWXLFoCMAAdq5a.jpg:large",
            "https://pbs.twimg.com/media/Bp8-6VMIEAEXbAg.jpg:large",
            "https://pbs.twimg.com/media/BpuHSuNCQAA2K1W.jpg:large",
            "https://pbs.twimg.com/media/BqRf3TCCAAACCZC.jpg:large",
            "https://pbs.twimg.com/media/BpkJvfCIAAILJc-.jpg:large",
            "https://pbs.twimg.com/media/Bp3eOzcCIAEM94n.jpg:large",
            "https://pbs.twimg.com/media/BjwUBCICAAAFS0m.jpg:large",
            "https://pbs.twimg.com/media/BpabvckCQAAT4ll.jpg:large",
            "https://pbs.twimg.com/media/Bp3D7_uIAAAXBxj.jpg:large",
            "https://pbs.twimg.com/media/BppFs51IAAAQ60s.jpg:large",
            "https://pbs.twimg.com/media/BpkYt8DCAAAmaZ8.jpg:large",
            "https://pbs.twimg.com/media/BnJqdCKCMAASTxj.jpg:large",
            "https://pbs.twimg.com/media/BoHECEXIgAEwrDi.jpg:large",
            "https://pbs.twimg.com/media/BqIYlwpCMAAulPk.jpg:large",
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
            "https://pbs.twimg.com/media/BkOcfdJCcAEH0MG.jpg:large",
            "https://pbs.twimg.com/media/BkEoEADIEAA3obl.jpg:large",
            "https://pbs.twimg.com/media/Bj_tyNaIIAAqOp7.jpg:large",
            "https://pbs.twimg.com/media/Bj1dPJqCAAA8m2Y.jpg:large",
            "https://pbs.twimg.com/media/Bjwes9ICYAA7u-H.png:large",
            "https://pbs.twimg.com/media/BjwUBCICAAAFS0m.jpg:large",
            "https://pbs.twimg.com/media/BjrLItoCIAA3r7D.jpg:large",
            "https://pbs.twimg.com/media/BjqQsrTIEAAUiR7.jpg:large",
            "https://pbs.twimg.com/media/BjnaKhYIgAA3VXi.jpg:large",
            "https://pbs.twimg.com/media/BjbHdI-IYAAVE0s.jpg:large",
            "https://pbs.twimg.com/media/BjH47HoCMAAH6qU.jpg:large",
            "https://pbs.twimg.com/media/BnPrWI3CEAAIzSP.jpg:large",
            "https://pbs.twimg.com/media/BnO6BriIcAEkCy5.jpg:large",
            "https://pbs.twimg.com/media/BnEJAzoCIAAT4ZD.jpg:large",
            "https://pbs.twimg.com/media/BnCzJNgIUAAIsiJ.jpg:large",
            "https://pbs.twimg.com/media/Bm4ll80IQAAp9qP.jpg:large",
            "https://pbs.twimg.com/media/Bm4N-biIAAA1o57.jpg:large",
            "https://pbs.twimg.com/media/BmXRC6FIIAAXTQG.jpg:large",
            "https://pbs.twimg.com/media/BmQx40GIIAAF2Dt.jpg:large",
            "https://pbs.twimg.com/media/BmITsr6IMAA92OS.jpg:large",
            "https://pbs.twimg.com/media/BmGQZujIAAAdHEi.jpg:large",
            "https://pbs.twimg.com/media/Bl7VnpLIYAEGVeR.jpg:large",
            "https://pbs.twimg.com/media/Bltkx8cIgAE9WBq.jpg:large",
            "https://pbs.twimg.com/media/BlNHyNUIQAASMjF.jpg:large",
            "https://pbs.twimg.com/media/Bk8ycCLIQAAQ798.jpg:large",
            "https://pbs.twimg.com/media/Bk5Hq2uCYAAkq5l.jpg:large",
            "https://pbs.twimg.com/media/BizjLsIIQAA3q5E.jpg:large",
            "https://pbs.twimg.com/media/Bie1fMFCUAAk6z_.jpg:large",
            "https://pbs.twimg.com/media/BidYGudIcAAKs9U.jpg:large",
            "https://pbs.twimg.com/media/BieqLjVIYAE3SsG.jpg:large",
            "https://pbs.twimg.com/media/BiZKfovCUAA_fTP.jpg:large",
            "https://pbs.twimg.com/media/BiVgETQIcAAlVKZ.jpg:large",
            "https://pbs.twimg.com/media/BiTgFHKCQAAY2Yn.jpg:large",
            "https://pbs.twimg.com/media/BiQ3DaGIMAAWyDg.jpg:large",
            "https://pbs.twimg.com/media/Bhn06w1IYAAMt0s.jpg:large",
            "https://pbs.twimg.com/media/Bhh8kV6IIAAiF10.jpg:large",
            "https://pbs.twimg.com/media/BhdTlm7IEAEQTGD.jpg:large",
            "https://pbs.twimg.com/media/Bhb7OBUIQAAPyzx.jpg:large",
            "https://pbs.twimg.com/media/Bha-DtSIgAE7FBu.jpg:large",
            "https://pbs.twimg.com/media/Bha9GUoIcAEQVN8.jpg:large",
            "https://pbs.twimg.com/media/BhWzYgsIUAAMe3k.jpg:large",
            "https://pbs.twimg.com/media/BhQ5VtsIgAAXtOe.jpg:large",
            "https://pbs.twimg.com/media/BhQs5yGIAAALsw1.jpg:large",
            "https://pbs.twimg.com/media/BhQgVUNIQAAsfg3.jpg:large",
            "https://pbs.twimg.com/media/BhNz37HIAAAxIh3.jpg:large",
            "https://pbs.twimg.com/media/BhNhmgsIEAAdDDG.jpg:large",
            "https://pbs.twimg.com/media/Bg3PGadCMAAd6pC.jpg:large",
            "https://pbs.twimg.com/media/BgzYT05IUAAcd14.jpg:large",
            "https://pbs.twimg.com/media/BgZ0mfzIIAAc4QJ.jpg:large",
            "https://pbs.twimg.com/media/BgSa1BtIQAE06dw.jpg:large",
            "https://pbs.twimg.com/media/BgPiAXVIIAAd26L.jpg:large",
            "https://pbs.twimg.com/media/BgKLYWYIcAAkGy2.jpg:large",
            "https://pbs.twimg.com/media/Bf4L0APIIAAufb6.jpg:large",
            "https://pbs.twimg.com/media/Bf1qTHGIgAACFRr.jpg:large",
            "https://pbs.twimg.com/media/Bf0FZ6kIYAA0tZO.jpg:large",
            "https://pbs.twimg.com/media/Bfz3sr4IEAAKe74.jpg:large",
            "https://pbs.twimg.com/media/BfwzcupCIAAIHKt.jpg:large",
            "https://pbs.twimg.com/media/Bfqo9HLIIAA1xf-.jpg:large",
            "https://pbs.twimg.com/media/Bfj0MnPIIAELSyA.jpg:large",
            "https://pbs.twimg.com/media/BfKeuN7CcAAFhl0.jpg:large",
            "https://pbs.twimg.com/media/BfAjiWBIcAA8TW0.jpg:large",
            "https://pbs.twimg.com/media/Be8GPggIYAAfv2q.jpg:large",
            "https://pbs.twimg.com/media/Be1hk6RIMAAPzbP.jpg:large",
            "https://pbs.twimg.com/media/BetEXxBIUAAjWhG.jpg:large",
            "https://pbs.twimg.com/media/BehmMOZIUAAexJ2.jpg:large",
            "https://pbs.twimg.com/media/Beehm99IMAAlPZZ.jpg:large",
            "https://pbs.twimg.com/media/BeeMw_OCEAAdnLl.jpg:large",
            "https://pbs.twimg.com/media/BediG8kIYAA2jS8.jpg:large",
            "https://pbs.twimg.com/media/BeTQExrIQAAkDhb.jpg:large",
            "https://pbs.twimg.com/media/BeIxny4IMAAXwEl.jpg:large",
            "https://pbs.twimg.com/media/Bdt4qL5IgAEFNcI.jpg:large",
            "https://pbs.twimg.com/media/BdRMvasIAAAozmY.jpg:large",
            "https://pbs.twimg.com/media/BdHcPYRIcAAhrWJ.jpg:large",
            "https://pbs.twimg.com/media/BdEG0fcIUAAa3h0.png:large",
            "https://pbs.twimg.com/media/BbdZ6Q7IEAACd9i.jpg:large",
            "https://pbs.twimg.com/media/BaiS5fuIgAAudPC.jpg:large",
            "https://pbs.twimg.com/media/BaBeO7LCYAELODZ.jpg:large",
            "https://pbs.twimg.com/media/BZkAepoCcAA0KxU.jpg:large",
            "https://pbs.twimg.com/media/BZjqbINCQAAQLHI.jpg:large",
            "https://pbs.twimg.com/media/BZRdL6YCUAA6K1-.jpg:large",
            "https://pbs.twimg.com/media/BYzoVkWIYAA1djc.jpg:large"
    };*/

    private String [] urls;
    private ListView listView1;
    private ProgressBar progressBarMain;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        listView1 = (ListView) findViewById (R.id.listView1);
        progressBarMain = (ProgressBar) findViewById (R.id.progressBarMain);

        getSupportActionBar().setTitle ("Jen's Gallery");

        new Runnable () {
            @Override
            public void run () {
                synchronized (Utils.diskCacheLock) {
                    Utils.diskCache = new DiskLRUCacheWrapper (getBaseContext (), Utils.UniqueName, Utils.SizeOfCache);
                    Utils.diskCacheStarting = false;
                    Utils.diskCacheLock.notifyAll ();
                }
            }
        }.run ();

        listView1.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent (getBaseContext(), ZoomActivity.class);
                intent.putExtra ("URL", (String) adapterView.getAdapter().getItem (i));
                startActivity (intent);
            }
        });
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
                //load ();
                progressBarMain.setVisibility (View.VISIBLE);
                new DownloadURLTask().execute("https://dl.dropboxusercontent.com/u/52679306/jen.txt");
                break;
            }
        }

        return super.onOptionsItemSelected (item);
    }

    private void load () {
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

    class DownloadURLTask extends AsyncTask<String, Void, String []> {
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

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity ();
                BufferedHttpEntity buffer = new BufferedHttpEntity (entity);
                InputStream inputStream = buffer.getContent ();

                BufferedReader reader = new BufferedReader (new InputStreamReader (inputStream));
                String line;

                while ((line = reader.readLine()) != null ) {
                    urls.add (line);
                }

            } catch (IOException e) {

            }

            return urls.toArray (new String [urls.size ()]);
        }
    }
}
