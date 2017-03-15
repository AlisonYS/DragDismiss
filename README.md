# DragDismiss

## Synopsis 项目大纲

An imageview that be showed just like facebook showed when the image should scale to full screen and drag down to dismiss. 

一个imageveiw类，可以像facebook中的全屏预览图片一样，点击全屏展示详情，向下拖拽消失

## Code Example 代码示例

```
private void setViewInfo() {
    Intent intent = getIntent();
    if (scaleImageView!= null){
    Drawable drawable = getResources().getDrawable(R.mipmap.timg);
    scaleImageView.setClickViewInfo((ViewInfo) intent.getSerializableExtra(Constant.DRAG_VIEW), drawable);
    scaleImageView.setDismissListener(this);
    }
}
```


## Tests 项目运行效果

![drag gif](https://github.com/AlisonYS/DragDismiss/blob/master/AUmipCcGbduMBgaojVAV.gif)
