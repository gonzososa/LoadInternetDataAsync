package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.app.Activity;
import android.os.Bundle;

public class GestureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TouchImageView img = new TouchImageView(this);
        img.setPadding (7, 7, 7, 7);
        setContentView(img);
    }
}
    /*class MyImageView extends View {
        private static final int INVALID_POINTER_ID = -1;

        private Drawable image;
        private float mPosY;
        private float mPosX;

        private float mLastTouchX;
        private float mLastTouchY;

        private int mActivePointerId = INVALID_POINTER_ID;

        private ScaleGestureDetector mScaleDetector;
        private float mScaleFactor = 1.0f;

        public MyImageView (Context context) {
            this (context, null, 0);
        }

        public MyImageView (Context context, AttributeSet attrs) {
            this (context, attrs, 0);
        }

        public MyImageView (Context context, AttributeSet attrs, int defStyle) {
            super (context, attrs, defStyle);
            mScaleDetector = new ScaleGestureDetector (context, new ScaleListener ());
        }

        @Override
        public boolean onTouchEvent (MotionEvent ev) {
            mScaleDetector.onTouchEvent (ev);
            final int action = ev.getAction ();

            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    break;
                }
                case MotionEvent.ACTION_CANCEL: {
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP: {

                }
            }

            return true;
        }

        @Override
        public void onDraw (Canvas canvas) {
            super.onDraw (canvas);

            canvas.save ();
            canvas.translate (mPosX, mPosY);
            canvas.scale (mScaleFactor, mScaleFactor);
            image.draw (canvas);
            canvas.restore ();
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale (ScaleGestureDetector detector) {
                mScaleFactor *= detector.getScaleFactor ();
                mScaleFactor = Math.max (0.1f, Math.min (mScaleFactor, 10.0f));

                invalidate ();
                return true;
            }
        }
    }*/

