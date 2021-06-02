package com.happyfresh.showcase;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.text.Spannable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import com.happyfresh.showcase.config.AlignType;
import com.happyfresh.showcase.config.DismissType;
import com.happyfresh.showcase.listener.GuideListener;

public class GuideView extends FrameLayout {

    static final String TAG = "GuideView";

    private static final int INDICATOR_HEIGHT              = 40;
    private static final int MESSAGE_VIEW_PADDING          = 5;
    private static final int SIZE_ANIMATION_DURATION       = 700;
    private static final int APPEARING_ANIMATION_DURATION  = 400;
    private static final int CIRCLE_INDICATOR_SIZE         = 6;
    private static final int LINE_INDICATOR_WIDTH_SIZE     = 3;
    private static final int STROKE_CIRCLE_INDICATOR_SIZE  = 3;
    private static final int RADIUS_SIZE_TARGET_RECT       = 15;
    private static final int MARGIN_INDICATOR              = 15;

    private static final int BACKGROUND_COLOR              = 0x99000000;
    private static final int CIRCLE_INNER_INDICATOR_COLOR  = 0xffcccccc;
    private static final int CIRCLE_INDICATOR_COLOR        = Color.WHITE;
    private static final int LINE_INDICATOR_COLOR          = Color.WHITE;

    private final Paint selfPaint           = new Paint();
    private final Paint paintLine           = new Paint();
    private final Paint paintCircle         = new Paint();
    private final Paint paintCircleInner    = new Paint();
    private final Paint targetPaint         = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Xfermode X_FER_MODE_CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private View target;
    private RectF targetRect;
    private final Rect selfRect = new Rect();

    private float density, stopY;
    private boolean isTop;
    private boolean mIsShowing;
    private int yMessageView = 0;

    private float startYLineAndCircle;
    private float circleIndicatorSize = 0;
    private float circleIndicatorSizeFinal;
    private float circleInnerIndicatorSize = 0;
    private float lineIndicatorWidthSize;
    private int   messageViewPadding;
    private float marginGuide;
    private float strokeCircleWidth;
    private float indicatorHeight;

    private boolean isPerformedAnimationSize = false;

    private GuideListener mGuideListener;
    private AlignType mAlignType;
    private DismissType dismissType;
    public GuideMessageView mMessageView;

    private GuideView(Context context, final View view) {
        super(context);
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.target = view;
        density = context.getResources().getDisplayMetrics().density;
        init();

        int[] locationTarget = new int[2];
        target.getLocationOnScreen(locationTarget);
        targetRect = new RectF(locationTarget[0],
                locationTarget[1],
                locationTarget[0] + target.getWidth(),
                locationTarget[1] + target.getHeight());

        mMessageView = new GuideMessageView(getContext());
        mMessageView.setPadding(messageViewPadding, messageViewPadding, messageViewPadding, messageViewPadding);

        addView(mMessageView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setMessageLocation(resolveMessageViewLocation());

        ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);

                setMessageLocation(resolveMessageViewLocation());
                int[] locationTarget = new int[2];
                target.getLocationOnScreen(locationTarget);

                targetRect = new RectF(locationTarget[0],
                        locationTarget[1],
                        locationTarget[0] + target.getWidth(),
                        locationTarget[1] + target.getHeight());

                selfRect.set(getPaddingLeft(),
                        getPaddingTop(),
                        getWidth() - getPaddingRight(),
                        getHeight() - getPaddingBottom());

                marginGuide = (int) (isTop ? marginGuide : -marginGuide);
                startYLineAndCircle = (isTop ? targetRect.bottom : targetRect.top) + marginGuide;
                stopY = yMessageView + indicatorHeight;
                startAnimationSize();
                getViewTreeObserver().addOnGlobalLayoutListener(this);
            }
        };
        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

    }

    private void startAnimationSize() {
        if (!isPerformedAnimationSize) {
            final ValueAnimator circleSizeAnimator = ValueAnimator.ofFloat(0f, circleIndicatorSizeFinal);
            circleSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    circleIndicatorSize = (float) circleSizeAnimator.getAnimatedValue();
                    circleInnerIndicatorSize = (float) circleSizeAnimator.getAnimatedValue() - density;
                    postInvalidate();
                }
            });

            final ValueAnimator linePositionAnimator = ValueAnimator.ofFloat(stopY, startYLineAndCircle);
            linePositionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    startYLineAndCircle = (float) linePositionAnimator.getAnimatedValue();
                    postInvalidate();
                }
            });

            linePositionAnimator.setDuration(SIZE_ANIMATION_DURATION);
            linePositionAnimator.start();
            linePositionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    circleSizeAnimator.setDuration(SIZE_ANIMATION_DURATION);
                    circleSizeAnimator.start();
                    isPerformedAnimationSize = true;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    private void init() {
        lineIndicatorWidthSize = LINE_INDICATOR_WIDTH_SIZE * density;
        marginGuide = MARGIN_INDICATOR * density;
        indicatorHeight = INDICATOR_HEIGHT * density;
        messageViewPadding = (int) (MESSAGE_VIEW_PADDING * density);
        strokeCircleWidth = STROKE_CIRCLE_INDICATOR_SIZE * density;
        circleIndicatorSizeFinal = CIRCLE_INDICATOR_SIZE * density;
    }


    private int getNavigationBarSize() {
        Resources resources = getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private boolean isLandscape() {
        int display_mode = getResources().getConfiguration().orientation;
        return display_mode != Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (target != null) {

            canvas.drawRect(selfRect, selfPaint);

            paintLine.setStyle(Paint.Style.FILL);
            paintLine.setStrokeWidth(lineIndicatorWidthSize);
            paintLine.setAntiAlias(true);

            paintCircle.setStyle(Paint.Style.STROKE);
            paintCircle.setColor(CIRCLE_INDICATOR_COLOR);
            paintCircle.setStrokeCap(Paint.Cap.ROUND);
            paintCircle.setStrokeWidth(strokeCircleWidth);
            paintCircle.setAntiAlias(true);

            paintCircleInner.setStyle(Paint.Style.FILL);
            paintCircleInner.setColor(CIRCLE_INNER_INDICATOR_COLOR);
            paintCircleInner.setAntiAlias(true);


            final float x = (targetRect.left / 2 + targetRect.right / 2);
            canvas.drawLine(x,
                    startYLineAndCircle,
                    x,
                    stopY,
                    paintLine);

            canvas.drawCircle(x, startYLineAndCircle, circleIndicatorSize, paintCircle);
            canvas.drawCircle(x, startYLineAndCircle, circleInnerIndicatorSize, paintCircleInner);

            targetPaint.setXfermode(X_FER_MODE_CLEAR);
            targetPaint.setAntiAlias(true);

            canvas.drawRoundRect(targetRect, RADIUS_SIZE_TARGET_RECT, RADIUS_SIZE_TARGET_RECT, targetPaint);
        }
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public void dismiss() {
        dismiss(target);
    }

    public void dismiss(View view) {
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(this);
        mIsShowing = false;
        if (mGuideListener != null) {
            mGuideListener.onDismiss(view);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if(dismissType == null) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (dismissType) {

                case outside:
                    if (!isViewContains(mMessageView, x, y)) {
                        dismiss();
                    }
                    break;

                case anywhere:
                    dismiss();
                    break;

                case targetView:
                    if (targetRect.contains(x, y)) {
                        target.performClick();
                        dismiss();
                    }
                    break;

            }

            return true;
        }

        return false;
    }

    private boolean isViewContains(View view, float rx, float ry) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int w = view.getWidth();
        int h = view.getHeight();

        return !(rx < x || rx > x + w || ry < y || ry > y + h);
    }

    private void setMessageLocation(Point p) {
        mMessageView.setX(p.x);
        mMessageView.setY(p.y);
        postInvalidate();
    }

    public void updateGuideViewLocation(){
        requestLayout();
    }

    private Point resolveMessageViewLocation() {

        int xMessageView = 0;
        if (mAlignType == AlignType.center) {
            xMessageView = (int) (targetRect.left - mMessageView.getWidth() / 2 + target.getWidth() / 2);
        } else
            xMessageView = (int) (targetRect.right) - mMessageView.getWidth();

        if (isLandscape()) {
            xMessageView -= getNavigationBarSize();
        }

        if (xMessageView + mMessageView.getWidth() > getWidth())
            xMessageView = getWidth() - mMessageView.getWidth();
        if (xMessageView < 0)
            xMessageView = 0;


        //set message view bottom
        if (targetRect.top + (indicatorHeight) > getHeight() / 2) {
            isTop = false;
            yMessageView = (int) (targetRect.top - mMessageView.getHeight() - indicatorHeight);
        }
        //set message view top
        else {
            isTop = true;
            yMessageView = (int) (targetRect.top + target.getHeight() + indicatorHeight);
        }

        if (yMessageView < 0)
            yMessageView = 0;


        return new Point(xMessageView, yMessageView);
    }


    public void show() {
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.setClickable(false);

        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).addView(this);
        AlphaAnimation startAnimation = new AlphaAnimation(0.0f, 1.0f);
        startAnimation.setDuration(APPEARING_ANIMATION_DURATION);
        startAnimation.setFillAfter(true);
        this.startAnimation(startAnimation);
        mIsShowing = true;
    }

    public void setTitle(String str) {
        mMessageView.setTitle(str);
    }

    public void setContentText(String str) {
        mMessageView.setContentText(str);
    }

    public void setTitleGravity(int setGravity){
        mMessageView.mTitleTextView.setGravity(setGravity);
    }

    public void setContentGravity(int setGravity){
        mMessageView.mContentTextView.setGravity(setGravity);
    }

    public void setButtonGravity(int setGravity){
        mMessageView.childContentButton.setGravity(setGravity);
    }

    public void setButtonText(String buttonText){
        mMessageView.okButton.setText(buttonText);
    }

    public void setButtonBackground(Drawable drawable){
        mMessageView.okButton.setBackground(drawable);
    }

    public void setButtonTextColor(@ColorInt int color){
        mMessageView.okButton.setTextColor(color);
    }

    public void setTitlePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom){

        paddingLeft = mMessageView.padding + paddingLeft;
        paddingRight = mMessageView.padding + paddingRight;
        paddingBottom = mMessageView.paddingBetween + paddingBottom;
        paddingTop = mMessageView.paddingBetween + paddingTop;

        mMessageView.mTitleTextView.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
    }

    public void setMessagePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom){
        paddingLeft = mMessageView.padding + paddingLeft;
        paddingRight = mMessageView.padding + paddingRight;
        paddingBottom = mMessageView.paddingBetween + paddingBottom;
        paddingTop = mMessageView.paddingBetween + paddingTop;

        mMessageView.mContentTextView.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
    }

    public void setButtonPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom){
        paddingLeft = mMessageView.padding + paddingLeft;
        paddingRight = mMessageView.padding + paddingRight;
        paddingBottom = mMessageView.paddingBetween + paddingBottom;
        paddingTop = mMessageView.paddingBetween + paddingTop;

        mMessageView.childContentButton.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
    }

    public void setContentSpan(Spannable span) {
        mMessageView.setContentSpan(span);
    }

    public void setTitleTypeFace(Typeface typeFace) {
        mMessageView.setTitleTypeFace(typeFace);
    }

    public void setContentTypeFace(Typeface typeFace) {
        mMessageView.setContentTypeFace(typeFace);
    }


    public void setTitleTextSize(int size) {
        mMessageView.setTitleTextSize(size);
    }

    public void setContentTextSize(int size) {
        mMessageView.setContentTextSize(size);
    }

    public void setVisibleBackgroundOverlay(boolean isVisibleBackground) {
        if(isVisibleBackground) {
            selfPaint.setColor(BACKGROUND_COLOR);
            selfPaint.setStyle(Paint.Style.FILL);
            selfPaint.setAntiAlias(true);
        }
    }

    public void setBackgroundColor(int backgroundColor) {
        mMessageView.setColor(backgroundColor);
        paintLine.setColor(backgroundColor);
    }

    public static class Builder {
        private View targetView;
        private String title, contentText;
        private AlignType alignType;
        private DismissType dismissType;
        private Context context;
        private Spannable contentSpan;
        private Typeface titleTypeFace, contentTypeFace;
        private boolean visibleBackground;
        private int backgroundColor;
        private GuideListener guideListener;
        private int titleTextSize;
        private int contentTextSize;
        private float lineIndicatorHeight;
        private float lineIndicatorWidthSize;
        private float circleIndicatorSize;
        private float circleInnerIndicatorSize;
        private float strokeCircleWidth;
        private int titleGravity;
        private int contentGravity;
        private int buttonGravity;
        private String buttonText;
        private Drawable buttonBackground;
        private Integer buttonTextColor;
        private int paddingLeftTitle;
        private int paddingRightTitle;
        private int paddingTopTitle;
        private int paddingBottomTitle;
        private int paddingLeftMessage;
        private int paddingRightMessage;
        private int paddingBottomMessage;
        private int paddingTopMessage;
        private int paddingLeftButton;
        private int paddingRightButton;
        private int paddingTopButton;
        private int paddingBottomButton;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTargetView(View view) {
            this.targetView = view;
            return this;
        }

        /**
         * alignType GuideView
         *
         * @param alignType it should be one type of AlignType enum.
         **/

        public Builder setViewAlign(AlignType alignType) {
            this.alignType = alignType;
            return this;
        }

        /**
         * defining a title
         *
         * @param title a title. for example: submit button.
         **/

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * defining a description for the target view
         *
         * @param contentText a description. for example: this button can for submit your information..
         **/

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        /**
         *
         * @param paddingLeftTitle
         * @param paddingTopTitle
         * @param paddingRightTitle
         * @param paddingBottomTitle
         * @return
         */

        public Builder setPaddingTitle(int paddingLeftTitle, int paddingTopTitle, int paddingRightTitle, int paddingBottomTitle){
            this.paddingLeftTitle = paddingLeftTitle;
            this.paddingTopTitle = paddingTopTitle;
            this.paddingRightTitle = paddingRightTitle;
            this.paddingBottomTitle = paddingBottomTitle;
            return this;
        }

        /**
         *
         * @param paddingLeftMessage
         * @param paddingTopTitle
         * @param paddingRightTitle
         * @param paddingBottomTitle
         * @return
         */

        public Builder setPaddingMessage(int paddingLeftMessage, int paddingTopTitle, int paddingRightTitle, int paddingBottomTitle){
            this.paddingLeftMessage = paddingLeftMessage;
            this.paddingTopMessage = paddingTopTitle;
            this.paddingRightMessage = paddingRightTitle;
            this.paddingBottomMessage = paddingBottomTitle;
            return this;
        }

        public Builder setPaddingButton(int paddingLeftButton, int paddingTopButton, int paddingRightButton, int paddingBottomButton){
            this.paddingLeftButton = paddingLeftButton;
            this.paddingTopButton = paddingTopButton;
            this.paddingRightButton = paddingRightButton;
            this.paddingBottomButton = paddingBottomButton;
            return this;
        }

        /**
         * setting spannable type
         *
         * @param span a instance of spannable
         **/

        public Builder setContentSpan(Spannable span) {
            this.contentSpan = span;
            return this;
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         **/

        public Builder setContentTypeFace(Typeface typeFace) {
            this.contentTypeFace = typeFace;
            return this;
        }

        /**
         * adding a listener on show case view
         *
         * @param guideListener a listener for events
         **/

        public Builder setGuideListener(GuideListener guideListener) {
            this.guideListener = guideListener;
            return this;
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         **/

        public Builder setTitleTypeFace(Typeface typeFace) {
            this.titleTypeFace = typeFace;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */

        public Builder setContentTextSize(int size) {
            this.contentTextSize = size;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */

        public Builder setTitleTextSize(int size) {
            this.titleTextSize = size;
            return this;
        }

        /**
         *  define visible background showcase
         *
         * @param visibleBackground
         * @return
         */
        public Builder setVisibleBackgroundOverlay(boolean visibleBackground) {
            this.visibleBackground = visibleBackground;
            return this;
        }

        /**
         * define background color
         * @param colorOverlay
         * @return
         */
        public Builder setBackgroundColor(int colorOverlay) {
            this.backgroundColor = colorOverlay;
            return this;
        }

        /**
         * this method defining the type of dismissing function
         *
         * @param dismissType should be one type of DismissType enum. for example: outside -> Dismissing with click on outside of MessageView
         */

        public Builder setDismissType(DismissType dismissType) {
            this.dismissType = dismissType;
            return this;
        }

        /**
         * changing line height indicator
         *
         * @param height you can change height indicator (Converting to Dp)
         */

        public Builder setIndicatorHeight(float height) {
            this.lineIndicatorHeight = height;
            return this;
        }

        /**
         * changing line width indicator
         *
         * @param width you can change width indicator
         */

        public Builder setIndicatorWidthSize(float width) {
            this.lineIndicatorWidthSize = width;
            return this;
        }

        /**
         * changing circle size indicator
         *
         * @param size you can change circle size indicator
         */

        public Builder setCircleIndicatorSize(float size) {
            this.circleIndicatorSize = size;
            return this;
        }

        /**
         * set title gravity. you can use Gravity.CENTER, Gravity.LEFT, or Gravity.RIGHT
         * @param titleGravity int
         * @return builder
         */

        public Builder setTitleGravity(int titleGravity){
            this.titleGravity = titleGravity;
            return this;
        }

        /**
         * set messages gravity, you can use Gravity.CENTER, Gravity.LEFT, or Gravity.RIGHT
         * @param contentGravity int
         * @return builder
         */

        public Builder setContentGravity(int contentGravity) {
            this.contentGravity = contentGravity;
            return this;
        }

        /**
         * set button gravity, you can use Gravity.CENTER, Gravity.LEFT, or Gravity.RIGHT
         * @param buttonGravity int
         * @return builder
         */

        public Builder setButtonGravity(int buttonGravity){
            this.buttonGravity = buttonGravity;
            return this;
        }

        public Builder setButtonText(String buttonText){
            this.buttonText = buttonText;
            return this;
        }

        public Builder setButtonBackground(Drawable drawable){
            this.buttonBackground = drawable;
            return this;
        }

        public Builder setButtonTextColor(@ColorInt int color){
            this.buttonTextColor = color;
            return this;
        }
        /**
         * changing inner circle size indicator
         *
         * @param size you can change inner circle indicator size
         */

        public Builder setCircleInnerIndicatorSize(float size) {
            this.circleInnerIndicatorSize = size;
            return this;
        }

        /**
         * changing stroke circle size indicator
         *
         * @param size you can change stroke circle indicator size
         */

        public Builder setCircleStrokeIndicatorSize(float size) {
            this.strokeCircleWidth = size;
            return this;
        }


        public GuideView build() {
            GuideView guideView = new GuideView(context, targetView);
            guideView.mAlignType = alignType != null ? alignType : AlignType.auto;
            guideView.dismissType = dismissType;
            float density = context.getResources().getDisplayMetrics().density;

            guideView.setTitle(title);
            if (contentText != null)
                guideView.setContentText(contentText);
            if (titleTextSize != 0)
                guideView.setTitleTextSize(titleTextSize);
            if (contentTextSize != 0)
                guideView.setContentTextSize(contentTextSize);
            if (contentSpan != null)
                guideView.setContentSpan(contentSpan);
            if (titleTypeFace != null) {
                guideView.setTitleTypeFace(titleTypeFace);
            }
            if(guideView.selfPaint !=null) {
                guideView.setVisibleBackgroundOverlay(visibleBackground);
            }
            if(backgroundColor !=0) {
                guideView.setBackgroundColor(backgroundColor);
            }
            if (contentTypeFace != null) {
                guideView.setContentTypeFace(contentTypeFace);
            }
            if (guideListener != null) {
                guideView.mGuideListener = guideListener;
            }
            if (lineIndicatorHeight != 0) {
                guideView.indicatorHeight = lineIndicatorHeight * density;
            }
            if (lineIndicatorWidthSize != 0) {
                guideView.lineIndicatorWidthSize = lineIndicatorWidthSize * density;
            }
            if (circleIndicatorSize != 0) {
                guideView.circleIndicatorSize = circleIndicatorSize * density;
            }
            if (circleInnerIndicatorSize != 0) {
                guideView.circleInnerIndicatorSize = circleInnerIndicatorSize * density;
            }
            if (strokeCircleWidth != 0) {
                guideView.strokeCircleWidth = strokeCircleWidth * density;
            }
            if(titleGravity !=0) {
                guideView.setTitleGravity(titleGravity);
            }
            if(contentGravity !=0){
                guideView.setContentGravity(contentGravity);
            }
            if(buttonGravity !=0){
                guideView.setButtonGravity(buttonGravity);
            }
            if(buttonText !=null) {
                guideView.setButtonText(buttonText);
            }
            if (buttonBackground != null) {
                guideView.setButtonBackground(buttonBackground);
            }
            if (buttonTextColor != null) {
                guideView.setButtonTextColor(buttonTextColor);
            }
            if(paddingLeftTitle !=0 || paddingTopTitle !=0 || paddingRightTitle !=0 || paddingBottomTitle !=0){
                guideView.setTitlePadding(paddingLeftTitle,paddingTopTitle,paddingRightTitle,paddingBottomTitle);
            }
            if(paddingLeftMessage !=0 || paddingTopMessage !=0 || paddingRightMessage !=0 || paddingBottomMessage !=0){
                guideView.setMessagePadding(paddingLeftMessage,paddingTopMessage,paddingRightMessage,paddingBottomMessage);
            }
            if(paddingLeftButton !=0 || paddingRightButton !=0 || paddingBottomButton !=0 || paddingTopButton !=0){
                guideView.setButtonPadding(paddingLeftButton, paddingTopButton, paddingRightButton, paddingBottomButton);
            }
            return guideView;
        }


    }
}
