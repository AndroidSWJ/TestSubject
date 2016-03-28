/**
 * Created by SWJ on 2015/9/30.
 */
package com.developer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import com.developer.moveitem.R;

public class ArcMenu extends ViewGroup implements View.OnClickListener{
    private Postion mPostion=Postion.RIGHT_BOTTOM;
    private int mRadius;
    private Status mCurrentStatus=Status.CLOSE;
    private View mCBtuoon;
    public static String TAG="Program";

    public void setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        onMenuItemClickListener = menuItemClickListener;
    }

    private OnMenuItemClickListener onMenuItemClickListener;



    public enum  Status{
        OPEN,CLOSE
    }
    public enum Postion{
        LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    }

    public interface OnMenuItemClickListener{
        void onClick(View view ,int pos);
    }

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRadius= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics());
        TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenu,defStyleAttr,0);
        int pos=a.getInt(R.styleable.ArcMenu_postion,3);
        switch(pos){
            case 0:
                mPostion=Postion.LEFT_TOP;
                break;
            case 1:
                mPostion=Postion.LEFT_BOTTOM;
                break;
            case 2:
                mPostion=Postion.RIGHT_TOP;
                break;
            case 3:
                mPostion=Postion.RIGHT_BOTTOM;
                break;
        }
        mRadius = (int) a.getDimension(R.styleable.ArcMenu_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        a.recycle();
        Log.e(TAG, "postion = " + mPostion + ",radius = " + mRadius);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count=getChildCount();
        for(int i=0;i<count;i++){
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            layoutCButton();
            int count = getChildCount();

            for (int i = 0; i < count - 1; i++)
            {
                View child = getChildAt(i + 1);

                child.setVisibility(View.GONE);

                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2)
                        * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2)
                        * i));

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

                // 如果菜单位置在底部 左下，右下
                if (mPostion == Postion.LEFT_BOTTOM
                        || mPostion == Postion.RIGHT_BOTTOM)
                {
                    ct = getMeasuredHeight() - cHeight - ct;
                }
                // 右上，右下
                if (mPostion == Postion.RIGHT_TOP
                        || mPostion == Postion.RIGHT_BOTTOM)
                {
                    cl = getMeasuredWidth() - cWidth - cl;
                }
                child.layout(cl, ct, cl + cWidth, ct + cHeight);

            }

        }
    }

    private void layoutCButton() {
        mCBtuoon=getChildAt(0);
        mCBtuoon.setOnClickListener(this);
        int l=0;
        int t=0;
        int width=mCBtuoon.getMeasuredWidth();
        int height=mCBtuoon.getMeasuredHeight();
        switch (mPostion){
            case LEFT_TOP:
                l=0;
                t=0;
                break;
            case LEFT_BOTTOM:
                l=0;
                t=getMeasuredHeight()-height;
                break;
            case RIGHT_TOP:
                l=getMeasuredWidth()-width;
                t=0;
                break;
            case RIGHT_BOTTOM:
                l=getMeasuredWidth()-width;
                t=getMeasuredHeight()-height;
                break;
        }
        mCBtuoon.layout(l, t, l + width, t + height);

    }

    @Override
    public void onClick(View v) {
//        mCBtuoon=findViewById(R.id.id_button);
//        if(mCBtuoon==null)
//        {
//            mCBtuoon=getChildAt(0);
//        }

        rotateCButton(v, 0f, 360f, 300);
        toggleMenu(300);
        
    }

    private void toggleMenu(int duration) {
        int count=getChildCount();
        for(int i=0;i<count-1;i++){
            final View childView=getChildAt(i+1);
            childView.setVisibility(View.VISIBLE);
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2)
                    * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2)
                    * i));
            int xflag = 1;
            int yflag = 1;

            if (mPostion == Postion.LEFT_TOP
                    || mPostion == Postion.LEFT_BOTTOM)
            {
                xflag = -1;
            }

            if (mPostion == Postion.LEFT_TOP
                    || mPostion == Postion.RIGHT_TOP)
            {
                yflag = -1;
            }
            AnimationSet animset=new AnimationSet(true);
            Animation tranAnim=null;
            if(mCurrentStatus==Status.CLOSE){
                tranAnim=new TranslateAnimation(xflag*cl,0,yflag*ct,0);
                childView.setClickable(true);
                childView.setFocusable(true);
            }else{
                tranAnim=new TranslateAnimation(0,xflag*cl,0,yflag*ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset(i*100/count);
            tranAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            RotateAnimation rotateAnim=new RotateAnimation(0,720, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setFillAfter(true);
            animset.addAnimation(rotateAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);
        }
        if(mCurrentStatus==Status.OPEN){
            mCurrentStatus=Status.CLOSE;
        }else
        {
            mCurrentStatus=Status.OPEN;
        }
    }

    private void rotateCButton(View v, float start, float end, int duration) {
        RotateAnimation anim=new RotateAnimation(start,end, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }
}
