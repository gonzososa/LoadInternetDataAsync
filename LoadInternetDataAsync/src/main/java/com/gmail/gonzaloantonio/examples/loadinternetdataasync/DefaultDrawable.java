package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;

import java.lang.ref.WeakReference;

public class DefaultDrawable extends ColorDrawable {
    WeakReference<DownloadImageTask> downloadTask;

    private final Rect mBounds = getBounds ();
    private final Paint mProgress = new Paint ();
    private final int mColor = Color.WHITE;
    private final int mFill = Color.BLUE;


    public DefaultDrawable (DownloadImageTask task) {
        //super (Color.BLACK);
        downloadTask = new WeakReference<DownloadImageTask> (task);

        mProgress.setColor (mColor);
        mProgress.setAntiAlias (true);
    }

    @Override
    public void draw (Canvas canvas) {
        super.draw (canvas);
        //canvas.drawCircle (mBounds.width() / 2, mBounds.height() / 2, 25, mProgress);
        canvas.drawText ("Loading...", mBounds.width() / 3, mBounds.height() / 2, mProgress);
    }

    public DownloadImageTask getDownloadImageTask () {
        return downloadTask != null ? downloadTask.get () : null;
    }
}
