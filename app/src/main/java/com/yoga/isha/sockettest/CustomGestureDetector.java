package com.yoga.isha.sockettest;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

class CustomGestureDetector implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d("tap","onSingleTapConfirmed");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        Log.d("tap","onDoubleTap");



        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d("tap","onDoubleTapEvent");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("tap","onDown");
        return true;
    }


    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("tap","onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("tap","onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("tap","onScroll");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
         if (e.getAction() == android.view.MotionEvent.ACTION_UP ) {
            Log.d("tap", "onLongPress Touch up");
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("tap","onFling");
        return true;
    }

}

