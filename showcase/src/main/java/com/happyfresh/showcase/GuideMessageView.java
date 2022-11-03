package com.happyfresh.showcase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

import com.happyfresh.showcase.config.ShowCaseType;

public class GuideMessageView extends LinearLayout {

    private Paint mPaint;
    private RectF mRect;

    public TextView mTitleTextView;
    public TextView mContentTextView;
    public Button okButton;

    public LinearLayout childContentButton;
    private LinearLayout childContent;
    public int padding = 0;
    public int paddingBetween = 0;

    private int tooltipVerticalPadding = 0;
    private int tooltipHorizontalPadding = 0;

    public String buttonText = "";

    GuideMessageView(Context context) {
        super(context);
        initViewOnBoarding();
    }

    GuideMessageView(Context context, ShowCaseType showCaseType) {
        super(context);
        if (showCaseType == ShowCaseType.ON_BOARDING) {
            initViewOnBoarding();
        }
        if (showCaseType == ShowCaseType.TOOLTIP) {
            initViewTooltip();
        }
    }

    private void initViewOnBoarding() {
        setupParam();
        initContentLayout();
        initTitleTextView();
        initMessageTextView();
        initButtonTextView();
    }

    private void initViewTooltip() {
        setupParam();
        initContentLayout();
        initMessageTooltipTextView();
    }

    private void setupParam() {
        float density = getContext().getResources().getDisplayMetrics().density;
        setWillNotDraw(false);
        setOrientation(VERTICAL);
        mRect = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        padding = (int) (10 * density);
        paddingBetween = (int) (3 * density);
        tooltipHorizontalPadding = (int) (12 * density);
        tooltipVerticalPadding = (int) (8 * density);
    }

    private void initContentLayout() {
        childContent = new LinearLayout(getContext());
        childContent.setOrientation(VERTICAL);
    }

    private void initTitleTextView() {
        mTitleTextView = new TextView(getContext());
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mTitleTextView.setTextColor(Color.BLACK);
        mTitleTextView.setTypeface(mTitleTextView.getTypeface(), Typeface.BOLD);
        childContent.addView(mTitleTextView);
    }

    private void initMessageTextView() {
        mContentTextView = new TextView(getContext());
        mContentTextView.setTextColor(Color.BLACK);
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        childContent.addView(mContentTextView);
        addView(childContent, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void initMessageTooltipTextView() {
        mContentTextView = new TextView(getContext());
        mContentTextView.setPadding(tooltipHorizontalPadding, tooltipVerticalPadding,
                tooltipHorizontalPadding, tooltipVerticalPadding);
        mContentTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        childContent.addView(mContentTextView);
        addView(childContent, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void initButtonTextView() {
        childContentButton = new LinearLayout(getContext());
        childContentButton.setOrientation(HORIZONTAL);
        childContentButton.setGravity(Gravity.CENTER);

        okButton = new Button(getContext());
        okButton.setText(buttonText);
        okButton.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        okButton.setTextColor(Color.WHITE);
        okButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        okButton.setGravity(TEXT_ALIGNMENT_CENTER);
        okButton.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.button_positive_selector));
        childContentButton.addView(okButton);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT );
        params.setMargins(convertDp(10), convertDp(10), convertDp(10), convertDp(10));
        addView(childContentButton, params);
    }

    public void setTitle(String title) {
        if (title == null) {
            removeView(mTitleTextView);
            return;
        }
        mTitleTextView.setText(title);
    }

    public void setContentText(String content) {
        mContentTextView.setText(content);
    }

    public void setContentTextAppearance(@StyleRes int styleRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mContentTextView.setTextAppearance(styleRes);
        }
        else {
            mContentTextView.setTextAppearance(getContext(), styleRes);

        }
    }

    public void setContentTextColor(@ColorRes int colorRes) {
        mContentTextView.setTextColor(ContextCompat.getColor(getContext(), colorRes));
    }

    public void setContentBackground(@DrawableRes int drawRes) {
        mContentTextView.setBackground(ContextCompat.getDrawable(getContext(), drawRes));
    }

    public void setContentSpan(Spannable content) {
        mContentTextView.setText(content);
    }

    public void setContentTypeFace(Typeface typeFace) {
        mContentTextView.setTypeface(typeFace);
    }

    public void setTitleTypeFace(Typeface typeFace) {
        mTitleTextView.setTypeface(typeFace);
    }

    public void setTitleTextSize(int size) {
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setContentTextSize(int size) {
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setColor(int color) {

        mPaint.setAlpha(255);
        mPaint.setColor(color);

        invalidate();
    }

    int location[] = new int[2];

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        this.getLocationOnScreen(location);


        mRect.set(getPaddingLeft(),
                getPaddingTop(),
                getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom());


        canvas.drawRoundRect(mRect, 15, 15, mPaint);
    }

    private int convertDp(int margin){
        int marginInDp = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, margin, getResources()
                .getDisplayMetrics());

        return marginInDp;
    }
}
