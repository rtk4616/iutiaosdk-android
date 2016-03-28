package com.iutiao.sdk.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iutiao.sdk.R;
import com.iutiao.sdk.util.DisplayUtil;

public class FloatView extends RelativeLayout {
    // TODO: 16/3/28 onTouchEvent交给GestureDetector处理，更简洁灵敏
    // TODO: 16/3/28 封装调试模式才打印的Log工具
    private Context mContext;
    private View rootView;
    private ImageView ivArrowInLeft;
    private ImageView ivArrowInRight;
    private ImageView ivMask;
    private ImageView ivBg;

    private float touchStartX;
    private float touchStartY;
    private float x;
    private float y;
    private int screenWidth;
    private int screenHeight;
    private boolean isTouching;
    private boolean isLeft;
    private boolean mScreenProtrait = true;
    private static int count = 0;

    private WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    private WindowManager.LayoutParams wmParams ;
    OrientationEventListener mScreenOrientationEventListener;

    private int state; //FloatState中的一种

    public static final class FloatState {
        public static final int FOLDED = 0;// 折叠
        public static final int HOVERING = 1;//悬停
        public static final int MOVING = 2;//移动
        public static final int DOCKING = 3;//停靠边缘
    }

    public FloatView(Context context, WindowManager.LayoutParams param) {
        super(context);
        mContext = context;
        this.wmParams = param;
        init(mContext);
    }

    private void init(final Context mContext) {
//        初始化view
        LayoutInflater.from(mContext).inflate(R.layout.com_iutiao_view_flaotview, this, true);
        rootView = findViewById(R.id.float_view);
        ivMask = (ImageView) findViewById(R.id.ivMask);
        ivBg = (ImageView) findViewById(R.id.ivBg);
        ivArrowInLeft = (ImageView) findViewById(R.id.ivArrowInLeft);
        ivArrowInRight = (ImageView) findViewById(R.id.ivArrowInRight);
//        初始状态折叠在左边
        isTouching = false;
        isLeft = true;
        state = FloatState.FOLDED;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isTouching = true;
        //获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY() - 25;   //25是系统状态栏的高度
        Log.i("currP", "currX" + x + "====currY" + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取相对View的坐标，即以此View左上角为原点
                touchStartX = event.getX();
                touchStartY = event.getY();
                if (state == FloatState.HOVERING) {
                    getHandler().removeCallbacks(dockRunnable);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (state == FloatState.HOVERING || state == FloatState.DOCKING) {
                    state = FloatState.MOVING;
                }
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                isTouching = false;
                touchStartX = touchStartY = 0;
                refreshRegion();
                if(state == FloatState.HOVERING||state== FloatState.DOCKING){
                    Toast.makeText(mContext, "click", Toast.LENGTH_SHORT).show();
                }
                if (state == FloatState.MOVING || state == FloatState.HOVERING) {
                    state = FloatState.HOVERING;
                    getHandler().postDelayed(dockRunnable, 1000);
                }
                if (state == FloatState.FOLDED) {
                    expend();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void refreshRegion() {
        isLeft = wmParams.x > screenWidth / 2 ? false : true;
    }

    private void smoothLightDown() {
        if (state == FloatState.DOCKING) {
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(ObjectAnimator.ofFloat(ivBg, "alpha", 1, 0).setDuration(200));
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    fold();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animSet.start();
        }
    }

    private void dock() {
        state = FloatState.DOCKING;
        if (!isLeft) {
            wmParams.x = screenWidth - ivBg.getMeasuredWidth();
        } else {
            wmParams.x = 0;
        }
        instantScroll();
        getHandler().postDelayed(dimRunnable, 1000);
    }

    private void instantScroll() {
        wm.updateViewLayout(FloatView.this, wmParams);
    }

    private void smoothScroll(final int scrollStartX, final int targetX) {
        final int deltaX = targetX - scrollStartX;
        final ValueAnimator animator = ValueAnimator.ofInt(0, 1).setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isTouching) {
                    float fraction = animator.getAnimatedFraction();
                    wmParams.x = scrollStartX + (int) (deltaX * fraction);
                    wm.updateViewLayout(FloatView.this, wmParams);
                }
            }
        });
        animator.start();
    }

    private void updateViewPosition() {
        if (state == FloatState.MOVING) {
            //更新浮动窗口位置参数
            wmParams.x = (int) (x - touchStartX);
            wmParams.y = (int) (y - touchStartY);
            // 坐标约束
            if (wmParams.x > screenWidth - wmParams.width) {
                wmParams.x = screenWidth - wmParams.width;
            }
            if (wmParams.x < 0) {
                wmParams.x = 0;
            }
            if (wmParams.y > screenHeight - wmParams.height) {
                wmParams.y = screenHeight - wmParams.height;
            }
            if (wmParams.y < 0) {
                wmParams.y = 0;
            }
            wm.updateViewLayout(FloatView.this, wmParams);
        }
    }

    private void fold() {
        if (state == FloatState.DOCKING) {
            state = FloatState.FOLDED;
            animOut();
        }
    }

    private void expend() {
        if (state == FloatState.FOLDED) {
            state = FloatState.DOCKING;
            logoAnimIn();
        }
    }

    public void hide() {
        wm.removeView(this);
    }
    public void show() {
        // TODO: 16/3/28  优化：尝试监听屏幕切换来更新屏幕参数，持久化坐标
        //更新屏幕参数
        screenWidth = DisplayUtil.getDisplayWidthPixels(mContext);
        screenHeight = DisplayUtil.getDisplayheightPixels(mContext);
        if (!DisplayUtil.isScreenOriatationPortrait(mContext)) {
            int temp = screenWidth;
            screenWidth = screenHeight;
            screenHeight = temp;
            if(mScreenProtrait){
                mScreenProtrait = false;
                wmParams.x = wmParams.y = 0;
            }
        }else{
            if(!mScreenProtrait){
                mScreenProtrait = true;
                wmParams.x = wmParams.y = 0;
            }
        }
        //显示myFloatView图像
        wm.addView(this, wmParams);
    }

    private void logoAnimIn() {
        ivArrowInRight.setVisibility(GONE);
        ivArrowInLeft.setVisibility(GONE);
        ivBg.setVisibility(VISIBLE);
        ivMask.setVisibility(VISIBLE);
        int translationX;
        if (isLeft) {
            translationX = -rootView.getMeasuredWidth();
        } else {
            translationX = rootView.getMeasuredWidth();
        }
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(rootView, "translationX", translationX, 0).setDuration(200), ObjectAnimator.ofFloat(ivBg, "alpha", 0, 1).setDuration(1000));
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getHandler().postDelayed(dimRunnable, 1000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    private void animOut() {
        int translationX;
        if (isLeft) {
            translationX = -rootView.getMeasuredWidth();
        } else {
            translationX = rootView.getMeasuredWidth();
        }
        ValueAnimator outAnim = ObjectAnimator.ofFloat(rootView, "translationX", translationX);
        outAnim.setDuration(200);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (state == FloatState.FOLDED) {
                    arrowAnimIn();
                } else {
                    logoAnimIn();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        outAnim.start();
    }

    private void arrowAnimIn() {
        ivMask.setVisibility(GONE);
        ivBg.setVisibility(GONE);
        int translationX;
        if (isLeft) {
            ivArrowInRight.setVisibility(GONE);
            ivArrowInLeft.setVisibility(VISIBLE);
            translationX = -rootView.getMeasuredWidth();
        } else {
            ivArrowInLeft.setVisibility(GONE);
            ivArrowInRight.setVisibility(VISIBLE);
            translationX = rootView.getMeasuredWidth();
        }
        ValueAnimator inAnim = ObjectAnimator.ofFloat(rootView, "translationX", translationX, 0);
        inAnim.setDuration(200);
        inAnim.start();
    }

    private Runnable dimRunnable = new Runnable() {
        @Override
        public void run() {
            if (state == FloatState.DOCKING) {
                smoothLightDown();
            }
        }
    };
    private Runnable dockRunnable = new Runnable() {
        @Override
        public void run() {
            if (state == FloatState.HOVERING) {
                dock();
            }
        }
    };
    public static class Builder {
        private Context context;
        private final WindowManager.LayoutParams param;

        public Builder(Context context) {
            this.context = context;
            param = new WindowManager.LayoutParams();
            param.type = WindowManager.LayoutParams.TYPE_TOAST;     // 系统提示类型,重要
            param.format = 1;
            param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
            param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制

            param.alpha = 1.0f;

            param.gravity = Gravity.LEFT | Gravity.TOP;   //调整悬浮窗口至左上角
            //以屏幕左上角为原点，设置x、y初始值
            param.x = 0;
            param.y = 0;

            //设置悬浮窗口长宽数据
            param.width = param.WRAP_CONTENT;
            param.height = param.WRAP_CONTENT;
        }

        public FloatView create() {
            final FloatView floatView = new FloatView(context,param);
            ((Application)context).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

                @Override
                public void onActivityStopped(Activity activity) {
                    count--;
                    if (count == 0) {
                        floatView.hide();
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    if (count == 0) {
                        floatView.show();

                    }
                    count++;
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override
                public void onActivityResumed(Activity activity) {
                }

                @Override
                public void onActivityPaused(Activity activity) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                }

                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                }
            });
            return floatView;
        }
//        public FloatView show() {
//            final FloatView floatView = create();
//            floatView.show();
//            return floatView;
//        }

    }
}