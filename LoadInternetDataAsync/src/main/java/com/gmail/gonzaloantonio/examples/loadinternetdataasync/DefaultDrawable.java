package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.lang.ref.WeakReference;

public class DefaultDrawable extends ColorDrawable {
    WeakReference<DownloadImageTask> downloadTask;

    public DefaultDrawable (DownloadImageTask task) {
        super (Color.BLACK);
        downloadTask = new WeakReference<DownloadImageTask> (task);
    }

    public DownloadImageTask getDownloadImageTask () {
        return downloadTask != null ? downloadTask.get () : null;
    }
}
