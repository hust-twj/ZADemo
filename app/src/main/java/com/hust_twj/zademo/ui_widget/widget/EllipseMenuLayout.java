package com.hust_twj.zademo.ui_widget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hust_twj.zademo.R;
import com.hust_twj.zademo.utils.DensityUtils;
import com.hust_twj.zademo.utils.LogUtils;

import java.util.List;

/**
 * description ：椭圆菜单控件
 * Created by Wenjing.Tang on 2019-06-02.
 */
public class EllipseMenuLayout<T> extends ViewGroup {
    /**
     * 该容器内child item的默认尺寸
     */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private static final int FLINGABLE_VALUE = 300;
    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int NOCLICK_VALUE = 3;
    private List<T> mItems;
    private int mRadius;
    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private int mFlingableValue = FLINGABLE_VALUE;
    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 90;
    /**
     * 菜单项的文本
     */
    private String[] mItemTexts;
    /**
     * 菜单项的图标
     */
    private int[] mItemImgs;

    /**
     * 菜单的个数
     */
    private int mMenuItemCount;

    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isFling;

    /**
     * MenuItem的点击事件接口
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnLoadResCallback mLoadCallback;
    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;
    /**
     * 自动滚动的Runnable
     */
    private AutoFlingRunnable mFlingRunnable;

    public EllipseMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 无视padding
        setPadding(0, 0, 0, 0);
    }

    /**
     * 设置布局的宽高，并策略menu item宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth;
        int resHeight;

        //根据传入的参数，分别获取测量模式和测量值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //如果宽或者高的测量模式非精确值
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度
            resWidth = getSuggestedMinimumWidth();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;

            resHeight = getSuggestedMinimumHeight();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
        } else {
            // 如果都设置为精确值，则直接取小值；
            resWidth = resHeight = Math.min(width, height);
        }

        setMeasuredDimension(resWidth, resHeight);

        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

         int count = getChildCount();
        // menu item尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
        // 迭代测量
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
    }

    /**
     * 设置item的点击事件接口
     *
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    /**
     * 设置item的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutRadius = mRadius;

        int childCount = getChildCount();

        int left, top;
        //item 的尺寸
        int itemWidth = DensityUtils.dp2px(getContext(), 50);
        int itemHeight = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);

        // 根据menu item的个数，计算角度
        float angleDelay = 360 / ((childCount == 1) ? 1f : (childCount - 1f));

        // 计算，中心点到item中心的距离
        float tmp = layoutRadius / 2f - itemWidth / 2f;

        // 遍历去设置item的位置
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getId() == R.id.id_circle_menu_item_center || child.getVisibility() == GONE) {
                continue;
            }

            mStartAngle %= 360;

            // tmp cosa 即menu item中心点的横坐标
            left = layoutRadius / 2 - itemWidth / 2 + (int) (tmp * Math.cos(Math.toRadians(mStartAngle)));

            //控制y方向的大小，值=1时为圆；否则为椭圆；值大于1且越大，竖直方向椭圆（短）轴越短；
            float yFactor = 2f;
            // tmp sina 即menu item的纵坐标
            top = layoutRadius / 2 - itemHeight / 2 + (int) (tmp * Math.sin(Math.toRadians(mStartAngle)) / yFactor);

            Log.e("twj124", "i: " + i + "   " + "  mStartAngle:" + mStartAngle + "    " + left);

            child.layout(left, top, left + itemWidth, top + itemHeight);
            // 叠加尺寸
            mStartAngle += angleDelay;
            applyToView(i,child);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;

                // 如果当前已经在快速滚动
                if (isFling) {
                    // 移除快速滚动的回调
                    removeCallbacks(mFlingRunnable);
                    isFling = false;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //获得开始的角度
                float start = getAngle(mLastX, mLastY);
                //获得当前的角度
                float end = getAngle(x, y);

                // Log.e("TAG", "start = " + start + " , end =" + end);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                } else {
                    // 二、三象限，色角度值是负值
                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                }
                // 重新布局
                requestLayout();
                Log.e("twj124", "-----------------");
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                // 计算，每秒移动的角度
                float anglePerSecond = mTmpAngle * 1000 / (System.currentTimeMillis() - mDownTime);

                // Log.e("TAG", anglePrMillionSecond + " , mTmpAngel = " + mTmpAngle);

                // 如果达到该值认为是快速移动
                if (Math.abs(anglePerSecond) > mFlingableValue && !isFling) {
                    // post一个任务，去自动滚动
                    post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));
                    return true;
                }

                // 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 主要为了action_down时，返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        int count = getChildCount();

        for (int i = 0; i < count; ++i) {
            View v = getChildAt(i);
            if (v.getId() == R.id.id_circle_menu_item_center || v.getVisibility() == GONE) {
                continue;
            }
            applyToView(i, v);
        }
    }

    /**
     * item的缩放
     *
     * @param v View
     */
    public void applyToView(int i, View v) {
        float mScalingRatio = 0.001f;
        float halfWidth = v.getWidth() * 0.5f;
        float parentHalfWidth = getMeasuredWidth() * 0.5f;
        float y = v.getY() / 2;

        v.setPivotX(halfWidth);
        v.setPivotY(0.0f);
        float scale = 1.0f - Math.abs(parentHalfWidth - halfWidth - 1.3f*y) * mScalingRatio;

        Log.e("twj124", "i  " + i + "  "  + scale +"   " + y + "   "+(parentHalfWidth - halfWidth - 1.3f*y) );
        v.setScaleX(scale);
        v.setScaleY(scale);
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch x坐标
     * @param yTouch y坐标
     * @return 计算角度
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x x坐标
     * @param y y坐标
     * @return 象限
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    /**
     * 设置菜单条目的图标和文本
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(int[] resIds, String[] texts) {
        mItemImgs = resIds;
        mItemTexts = texts;

        // 参数检查
        if (resIds == null && texts == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }

        // 初始化mMenuCount
        mMenuItemCount = resIds == null ? texts.length : resIds.length;

        if (resIds != null && texts != null) {
            mMenuItemCount = Math.min(resIds.length, texts.length);
        }

        addMenuItems();
    }

    /**
     * 设置菜单项
     */
    public void setMenus(List<T> menus, OnLoadResCallback callback) {
        mItems = menus;
        mLoadCallback = callback;
        // 参数检查
        if (menus == null || menus.size() < 2) {
            throw new IllegalArgumentException("至少两个菜单");
        }

        mMenuItemCount = menus.size();
        addMenus();
    }

    /**
     * 添加菜单项
     */
    private void addMenuItems() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        //根据用户设置的参数，初始化view
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View view = mInflater.inflate(R.layout.circle_menu_item, this, false);
            ImageView iv = view.findViewById(R.id.id_circle_menu_item_image);
            TextView tv = view.findViewById(R.id.id_circle_menu_item_text);

            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(mItemImgs[i]);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnMenuItemClickListener != null) {
                            mOnMenuItemClickListener.itemClick(v, j);
                        }
                    }
                });
            }
            if (tv != null) {
                tv.setVisibility(View.VISIBLE);
                tv.setText(mItemTexts[i]);
            }

            // 添加view到容器中
            addView(view);
        }
    }

    /**
     * 添加菜单项
     */
    private void addMenus() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        //根据用户设置的参数，初始化view
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View view = mInflater.inflate(R.layout.circle_menu_item, this, false);
            ImageView iv = view.findViewById(R.id.id_circle_menu_item_image);
            TextView tv =  view.findViewById(R.id.id_circle_menu_item_text);
            iv.setVisibility(VISIBLE);
            tv.setVisibility(VISIBLE);
            mLoadCallback.showItem(mItems.get(i), iv, tv);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.itemClick(v, j);
                    }
                }
            });
            // 添加view到容器中
            addView(view);
        }
    }

    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param mFlingableValue 旋转角
     */
    public void setFlingableValue(int mFlingableValue) {
        this.mFlingableValue = mFlingableValue;
    }

    /**
     * 获得默认该layout的尺寸
     *
     * @return layout的尺寸
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /**
     * MenuItem的点击事件接口
     *
     */
    public interface OnMenuItemClickListener {
        void itemClick(View view, int pos);
    }

    /**
     * 资源加载回调
     */

    public interface OnLoadResCallback {
        void showItem(Object menu, ImageView img, TextView txt);
    }

    /**
     * 自动滚动的任务
     *
     * @author zhy
     */
    private class AutoFlingRunnable implements Runnable {

        private float angelPerSecond;

        public AutoFlingRunnable(float velocity) {
            this.angelPerSecond = velocity;
        }

        public void run() {
            // 如果小于20,则停止
            if ((int) Math.abs(angelPerSecond) < 20) {
                isFling = false;
                return;
            }
            isFling = true;
            // 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
            mStartAngle += (angelPerSecond / 30);
            // 逐渐减小这个值
            angelPerSecond /= 1.0666F;
            postDelayed(this, 30);
            // 重新布局
            requestLayout();
        }
    }

}
