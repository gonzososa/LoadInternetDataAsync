package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

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

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        final ListView listView1 = (ListView) findViewById (R.id.listView1);

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
        });

        listView1.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent (getBaseContext(), ZoomActivity.class);
            intent.putExtra ("URL", (String) adapterView.getAdapter().getItem (i));
            startActivity (intent);
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
}
