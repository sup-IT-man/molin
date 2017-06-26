package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Administrator on 2017/6/22 0022.
 */

public class BinnarViewGroupFramelayout extends FrameLayout implements BinnerView.selectImageIndexListener, BinnerView.onItemClickListener {
    private BinnerView mBinnarView;

    private LinearLayout linearLayout;


    public BinnarViewGroupFramelayout(Context context) {
        this(context,null);
    }

    public BinnarViewGroupFramelayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BinnarViewGroupFramelayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitBinnarView(context);
        InitDotView(context);
    }

    private void InitBinnarView(Context context){
        mBinnarView=new BinnerView(context);
        mBinnarView.setMselectImageIndexListener(this);
        mBinnarView.setOnItemClickListener(this);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        mBinnarView.setLayoutParams(params);
        addView(mBinnarView);
    }

    private void InitDotView(Context context){
        linearLayout=new LinearLayout(context);
        linearLayout.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,40);
        linearLayout.setLayoutParams(params);
//        params.gravity=Gravity.CENTER;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(linearLayout);
        //设置圆点的位置
        FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)linearLayout.getLayoutParams();
        lp.gravity=Gravity.BOTTOM;

        linearLayout.setAlpha(0.5f);
    }

    public void addBitmap(List<Bitmap> bitmaps) {
        for(int i=0;i<bitmaps.size();i++){
            addImageToBinnar(bitmaps.get(i));
            addDotTolinearLayout();
        }
    }

    private void addDotTolinearLayout() {
        ImageView iv=new ImageView(getContext());
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(5,5,5,5);
        iv.setLayoutParams(p);
        iv.setImageResource(R.drawable.dot_white);
        linearLayout.addView(iv);
    }

    private void addImageToBinnar(Bitmap bitmap) {
            ImageView iv=new ImageView(getContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(C.WIDTH,ViewGroup.LayoutParams.WRAP_CONTENT);
            iv.setLayoutParams(p);//让每个子View宽度都是充满屏幕
            iv.setImageBitmap(bitmap);
            mBinnarView.addView(iv);
    }

    @Override
    public void selectIndex(int index) {
        int count=linearLayout.getChildCount();
        for(int i=0;i<count;i++){
            if(i == index){
                ImageView view=(ImageView)linearLayout.getChildAt(i);
                view.setImageResource(R.drawable.dot_dark);
            }else{
                ImageView view=(ImageView)linearLayout.getChildAt(i);
                view.setImageResource(R.drawable.dot_white);
            }
        }
    }

    private ImageOnclickListener mImageOnclickListener;

    public void setmImageOnclickListener(ImageOnclickListener mImageOnclickListener) {
        this.mImageOnclickListener = mImageOnclickListener;
    }

    public interface ImageOnclickListener{
        public void clickImageIndex(int position);
    }

    @Override
    public void onItemClick(int position) {
        if(mImageOnclickListener !=null){
            mImageOnclickListener.clickImageIndex(position);
        }
    }
}
