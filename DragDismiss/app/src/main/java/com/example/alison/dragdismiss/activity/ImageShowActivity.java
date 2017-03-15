package com.example.alison.dragdismiss.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.alison.dragdismiss.R;
import com.example.alison.dragdismiss.utils.Constant;
import com.example.alison.dragdismiss.utils.ViewInfo;
import com.example.alison.dragdismiss.view.ScaleImageView;

/**
 * Created by xuanmu on 17/3/14.
 */
public class ImageShowActivity extends Activity implements ScaleImageView.DismissListener {

    private ScaleImageView scaleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        scaleImageView = (ScaleImageView) findViewById(R.id.scale_image);
        setViewInfo();
    }

    /**
     * 获取点击的view的信息
     */
    private void setViewInfo() {
        Intent intent = getIntent();
        if (scaleImageView!= null){
            Drawable drawable = getResources().getDrawable(R.mipmap.timg);
            scaleImageView.setClickViewInfo((ViewInfo) intent.getSerializableExtra(Constant.DRAG_VIEW), drawable);
            scaleImageView.setDismissListener(this);
        }
    }

    @Override
    public void onDismiss() {
        this.finish();
    }
}
