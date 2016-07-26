package com.yoga.isha.sockettest;

/**
 * Created by Macist on 7/20/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

public class TouchHandlerView extends View {

    private Paint paint = new Paint();
    float mLastTouchX ,mPosX,width;
    float mLastTouchY ,mPosY,height;
    int INVALID_POINTER_ID = 0;
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private OnTouch mListener;
    Context mcontext;

    public TouchHandlerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

        mcontext = context;

        initView();


    }

//    public TouchHandlerView(Context context, AttributeSet attrs,int width,int height) {
//        super(context, attrs);
//
//        this.width = width;
//        this.height = height;
//
//        paint.setAntiAlias(true);
//        paint.setStrokeWidth(1.0f);
//        paint.setColor(Color.GRAY);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//
//       ;
//
//        initView();
//
//    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;

        canvas.drawLine(width/2, 0, width/2, height, paint); //vertical line
        canvas.drawLine(0, height/2, width, height/2, paint); //horizontal line
    }

    private void init(Context context, AttributeSet attrs) {

        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.0f);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void initView() {

        mScaleDetector = new ScaleGestureDetector(this.getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
            public boolean onScaleBegin(ScaleGestureDetector detector) {

                return true;
            }
            public boolean onScale(ScaleGestureDetector detector) {
                // do scaling here
                float scale = detector.getScaleFactor();
                Log.d("scale","scale factor"+scale);
                JSONObject obj = new JSONObject();
                try {

                    obj.put("scaleValue", scale);
                }catch(Exception e){}
                mListener.onTouch(true,obj);
                return false;
            }
        });
    }

    public void setTouchListener(OnTouch listener) {

        this.mListener = listener;
    }

    public interface OnTouch {
        public void onTouch(Boolean isScale,JSONObject obj);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);


        switch (action) {

            case MotionEvent.ACTION_POINTER_DOWN:
            {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                System.out.print("pointer_id : "+pointerId);

            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                System.out.print("pointer_id : "+pointerId);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(event, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(event, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                }
                break;
            }


        }

        if (event.getPointerCount() == 2) {

            mScaleDetector.onTouchEvent(event);

        }else {

            //        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
//            Log.d("TouchTest", "Touch down");
//        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
//            Log.d("TouchTest", "Touch up");
//        }

            // mGestureDetector.onTouchEvent(event);


            switch (action) {

                case MotionEvent.ACTION_DOWN: {
                    final int pointerIndex = MotionEventCompat.getActionIndex(event);
                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float y = MotionEventCompat.getY(event, pointerIndex);

                    // Remember where we started (for dragging)
                    mLastTouchX = x;
                    mLastTouchY = y;
                    // Save the ID of this pointer (for dragging)
                    mActivePointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    // Find the index of the active pointer and fetch its position
                    final int pointerIndex =MotionEventCompat.findPointerIndex(event, mActivePointerId);

                    float x = MotionEventCompat.getX(event, pointerIndex);
                    float y = MotionEventCompat.getY(event, pointerIndex);

                    // Calculate the distance moved
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

//                    x -= width/2;
//                    y -= height/2;

//                    mPosX += dx;
//                    mPosY += dy;
                    JSONObject obj = new JSONObject();
                    try {

                        obj.put("moveX",dx);
                        obj.put("moveY",dy);
                    }catch(Exception e){}
//

                    Log.d("touch location: ", "" + x + "-----" + y);
                    Log.d("Pointer_id: ", "" + mActivePointerId);


                    // Remember this touch position for the next move event
                    mLastTouchX = x;
                    mLastTouchY = y;
                    mListener.onTouch(false,obj);
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }


            }

        }


        return true;
    }
}
