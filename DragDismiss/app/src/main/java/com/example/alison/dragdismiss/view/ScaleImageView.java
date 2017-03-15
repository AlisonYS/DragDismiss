package com.example.alison.dragdismiss.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.alison.dragdismiss.R;
import com.example.alison.dragdismiss.utils.ViewInfo;

/**
 * Created by xuanmu on 17/3/14.
 */
public class ScaleImageView extends FrameLayout {

    private ViewInfo clickViewInfo;
    private ImageView imageView;
    private View backgroundView;
    private FrameLayout.LayoutParams params;
    private float scale;
    private int screenHeight;
    private int screenWidth;
    private int fullMarginTop;
    private DismissListener dismissListener;

    private float startX = -1.0f;
    private float startY = -1.0f;
    private float moveX = -1.0f;
    private float moveY = -1.0f;

    public ScaleImageView(Context context) {
        super(context);
        init(context);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        getScreenInfo(context);
        LayoutInflater.from(context).inflate(R.layout.view_scale_image, this, true);
        imageView = (ImageView) findViewById(R.id.image_view);
        params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        backgroundView = findViewById(R.id.back_view);
        backgroundView.setAlpha(0);
        this.setClickable(true);
    }

    public void setClickViewInfo(ViewInfo info, Drawable drawable){
        this.clickViewInfo = info;
        imageView.setImageDrawable(drawable);
        scale = drawable.getIntrinsicHeight() / (float) drawable.getIntrinsicWidth();
        startEnlargeAnimation(info, 0.0f);
    }

    public void setDismissListener(DismissListener listener){
        this.dismissListener = listener;
    }

    private void startEnlargeAnimation(ViewInfo startView, float startAlpha){
        FullScreenAnimation fullScreenAnimation = new FullScreenAnimation(startView, startAlpha);
        fullScreenAnimation.setDuration(500);
        imageView.startAnimation(fullScreenAnimation);
    }


    private void startDismissAnimation(){
        ViewInfo viewInfo = new ViewInfo();
        viewInfo.height = params.height;
        viewInfo.width = params.width;
        viewInfo.x = params.leftMargin;
        viewInfo.y = params.topMargin;
        DismissAnimation dismissAnimation = new DismissAnimation(viewInfo, movedAlpha);
        dismissAnimation.setDuration(500);
        dismissAnimation.setAnimationListener(new DissmissAnimListener());
        imageView.startAnimation(dismissAnimation);
    }

    private class FullScreenAnimation extends Animation {
        private int targetY;
        private float originalAlpha;
        private ViewInfo originalView;

        protected FullScreenAnimation(ViewInfo originalViewInfo, float originalAlpha) {
            this.targetY = (int) ((screenHeight - screenWidth * scale) / 2);
            this.originalView = originalViewInfo;
            this.originalAlpha = originalAlpha;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            params.width = (int) (interpolatedTime * (screenWidth - originalView.width) + originalView.width);
            params.height = (int) (params.width * scale);
            params.leftMargin = (int) ((1 - interpolatedTime) * originalView.x);
            fullMarginTop = (int) (interpolatedTime * (targetY - originalView.y) + originalView.y);
            params.topMargin = fullMarginTop;
            imageView.requestLayout();
            backgroundView.setAlpha(originalAlpha + interpolatedTime);
        }
    }


    private class DismissAnimation extends Animation {
        private float originalAlpha;
        private ViewInfo originalView;

        protected DismissAnimation(ViewInfo originalViewInfo, float originalAlpha) {
            this.originalView = originalViewInfo;
            this.originalAlpha = originalAlpha;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            params.width = (int) (interpolatedTime * (clickViewInfo.width - originalView.width) + originalView.width);
            params.height = (int) (params.width * scale);
            params.leftMargin = (int) (interpolatedTime * (clickViewInfo.x - originalView.x) + originalView.x);
            fullMarginTop = (int) (interpolatedTime * (clickViewInfo.y - originalView.y) + originalView.y);
            params.topMargin = fullMarginTop;
            imageView.requestLayout();
            backgroundView.setAlpha(originalAlpha - interpolatedTime);
        }
    }

    private class DissmissAnimListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (dismissListener != null) {
                dismissListener.onDismiss();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    private void getScreenInfo(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
    }

    private boolean isMoving = false;
    private float movedAlpha;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEvent.ACTION_MASK & event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.startX = event.getX();
                this.startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                if (!isMoving && (moveY - startY) > 0 && Math.abs(moveY - startY) > Math.abs(moveX - startX)){
                    isMoving = true;
                }
                if (isMoving){
                    Log.d("xuanmu", "ACTION_MOVE "+ moveX +" y :"+ moveY);
                    onTouchMoveEvent(moveX, moveY);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                Log.d("xuanmu", "ACTION_UP");
                isMoving = false;
                if (params.topMargin > clickViewInfo.y + clickViewInfo.height) {
                    startDismissAnimation();
                } else {
                    ViewInfo info = new ViewInfo();
                    info.width = params.width;
                    info.height = params.height;
                    info.x = params.leftMargin;
                    info.y = params.topMargin;
                    startEnlargeAnimation(info, movedAlpha);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean onTouchMoveEvent(float x, float y){
        params.topMargin += (int) (y - startY );
        params.leftMargin += (int) (x - startX);
        startX = x;
        startY = y;
        movedAlpha = (float) Math.exp(-2 * (params.topMargin - fullMarginTop)/(float) screenHeight);
        if (params.topMargin > fullMarginTop) {
            params.width = (int) (movedAlpha  * screenWidth);
            params.height = (int) (params.width * scale);
        }
        imageView.requestLayout();
        backgroundView.setAlpha(movedAlpha);
        return true;
    }

    public interface DismissListener {
        void onDismiss();
    }
}

