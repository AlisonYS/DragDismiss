package com.example.alison.dragdismiss.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.alison.dragdismiss.R;
import com.example.alison.dragdismiss.utils.Constant;
import com.example.alison.dragdismiss.utils.ViewInfo;



/**
 * Created by xuanmu on 17/2/8.
 */
public class DragDismissActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_dismiss);
    }

    public void imageClick(View view){
        ViewInfo info = new ViewInfo();
        info.x = (int) view.getX();
        info.y = (int) view.getY();
        info.width = view.getWidth();
        info.height = view.getHeight();
        showImage(ImageShowActivity.class.getName(), info);
    }

    private void showImage(String className, ViewInfo info) {
        Intent intent = new Intent();
        intent.setClassName(DragDismissActivity.this, className);
        intent.putExtra(Constant.DRAG_VIEW, info);
        startActivity(intent);
    }
}
