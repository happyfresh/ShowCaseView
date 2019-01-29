package com.happyfresh.showcase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GuideMessageView extends LinearLayout {

    private Paint mPaint;
    private RectF mRect;

    public TextView mTitleTextView;
    public TextView mContentTextView;
    public Button okButton;

    public LinearLayout childContentButton;
    private LinearLayout childContent;

    public String buttonText = "";

    GuideMessageView(Context context) {
        super(context);

        float density = context.getResources().getDisplayMetrics().density;
        setWillNotDraw(false);
        setOrientation(VERTICAL);

        mRect = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        final int padding = (int) (10 * density);
        final int paddingBetween = (int) (3 * density);

        childContent = new LinearLayout(context);
        childContent.setOrientation(VERTICAL);
        mTitleTextView = new TextView(context);
        mTitleTextView.setPadding(padding, padding, padding, paddingBetween);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mTitleTextView.setTextColor(Color.BLACK);
        mTitleTextView.setTypeface(mTitleTextView.getTypeface(), Typeface.BOLD);
        childContent.addView(mTitleTextView);


        mContentTextView = new TextView(context);
        mContentTextView.setTextColor(Color.BLACK);
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mContentTextView.setPadding(padding, paddingBetween, padding, padding);
        childContent.addView(mContentTextView);
        addView(childContent, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        childContentButton = new LinearLayout(context);
        childContentButton.setOrientation(HORIZONTAL);
        childContentButton.setGravity(Gravity.CENTER);

        okButton = new Button(context);
        okButton.setText(buttonText);
        okButton.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        okButton.setTextColor(Color.WHITE);
        okButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        okButton.setGravity(TEXT_ALIGNMENT_CENTER);
        okButton.setBackground(ContextCompat.getDrawable(context,R.drawable.button_positive_selector));
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
