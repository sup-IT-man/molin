package com.example.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/22 0022.
 */

public class BinnerView extends ViewGroup {
    private int childwidth;

    private int childheight;

    private int count;//子视图个数

    private int downx;


    private int scrollX;

    private int index=0;

    private Scroller mScroller;

    private boolean isAuto=true;//默认是开启图片轮播

    private Timer timer;

    private TimerTask task;


    private int leftborder;

    private int rightborder;

    private boolean isClick;//判断是否点击
    private Handler autohandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if(isAuto){//如果开启自动轮播，那么当轮播到第三个的时候再从第一个开始轮播
                        if(++index>=count){
                            index=0;
                        }
                        scrollTo(index*childwidth,0);
                        if(mselectImageIndexListener !=null){
                            mselectImageIndexListener.selectIndex(index);
                        }
                    }
                    break;
            }
        }
    };


    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public interface  onItemClickListener{
        public void onItemClick(int position);
    }

    private void startAuto(){
        isAuto=true;
    }

    private void stopAuto(){
        isAuto=false;
    }

    public BinnerView(Context context) {
        this(context,null);
    }

    public BinnerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller=new Scroller(context);
        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                autohandler.sendEmptyMessage(0);
            }
        };
        timer.schedule(task,100,2000);//第一次间隔100微秒，以后每两秒执行一次
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){//滑动完成返回false，未完成返回true
            scrollTo(mScroller.getCurrX(),0);
            invalidate();//重新绘制
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        count=getChildCount();
        if(count == 0){
            setMeasuredDimension(0,0);
        }else{
            measureChildren(widthMeasureSpec,heightMeasureSpec);//测量所有子视图的宽高
            View child=getChildAt(0);
            childheight=child.getMeasuredHeight();
            childwidth=child.getMeasuredWidth();
            int width=childwidth*count;

            leftborder=child.getLeft();
            rightborder=getChildAt(count-1).getRight();
            setMeasuredDimension(width,childheight);
        }
    }

    //手势事件处理


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                stopAuto();
                isClick=true;
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();//如果手指按下的时候滑动动画没有结束，那么以第二次手指按下的命令为主
                }
                downx=(int)event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                isClick=false;
                int movex=(int)event.getX();
                int distance=movex-downx;
               // scrollX=getScrollX();
                if((-distance+getScrollX())<leftborder){//如果小于最左边，那么不能继续拉动
                    scrollTo(leftborder,0);
                    return true;
                }

                if((-distance+getScrollX()+childwidth)>rightborder){//如果大于最右边，那么不能继续拉动
                    scrollTo(rightborder-childwidth,0);
                    return true;
                }
                scrollBy(-distance,0);
                downx=movex;
                break;
            case MotionEvent.ACTION_UP:
                startAuto();
                int scrollx=getScrollX();//滑动的距离
                index=(scrollx+childwidth/2)/childwidth;
                if(isClick){//如果为true代表点击了(记住这个时候不要用子view的点击事件，因为子view的事件已经被拦截)
                    onItemClickListener.onItemClick(index);
                }else{
                    if(index < 0){
                        index=0;
                    }else if(index > count-1){
                        index=count-1;
                    }
                    int dx=index*childwidth-scrollx;//要滑动的距离
                    mScroller.startScroll(scrollx,0,dx,0);
                    if(mselectImageIndexListener !=null){
                        mselectImageIndexListener.selectIndex(index);
                    }
                    invalidate();
                }
                //scrollTo(index*childwidth,0);
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            //如果布局发生改变，那么对子视图进行重新布局
            int leftmargin=0;
            for(int i=0;i<count;i++){
                View view=getChildAt(i);
                view.layout(leftmargin,0,leftmargin+childwidth,childheight);
                leftmargin+=childwidth;
            }
        }
    }


    private selectImageIndexListener mselectImageIndexListener;

    public void setMselectImageIndexListener(selectImageIndexListener mselectImageIndexListener){
        this.mselectImageIndexListener=mselectImageIndexListener;
    }

    public interface selectImageIndexListener{
        public void selectIndex(int index);
    }
}
