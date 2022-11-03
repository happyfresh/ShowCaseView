package com.happyfresh.showcaseview;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.happyfresh.showcase.GuideView;
import com.happyfresh.showcase.config.AlignType;
import com.happyfresh.showcase.config.DismissType;
import com.happyfresh.showcase.config.ShowCaseType;
import com.happyfresh.showcase.config.TooltipArrow;
import com.happyfresh.showcase.listener.GuideListener;

public class MainActivity extends AppCompatActivity {

    private GuideView mGuideView;
    private GuideView.Builder builder;
    private Button myButton;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton = (Button) findViewById(R.id.my_button);
//        showCoachLayout();
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTooltip(view);
            }
        });
    }

    private void showCoachLayout() {
        builder = new GuideView.Builder(this)
                .setTitle("Kami butuh alamatmu")
                .setContentText("Untuk memastikan alamat pengantaran berada dalam jangkauan HappyFresh")
                .setViewAlign(AlignType.center)
                .setTooltipTriangleColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright))
//                 remove this comment to perform tooltip type
                .setShowCaseType(ShowCaseType.ON_BOARDING_ARROW)
                .setTooltipTriangleSize(15)
                .setTitleGravity(Gravity.LEFT)
                .setContentGravity(Gravity.LEFT)
                .setButtonGravity(Gravity.RIGHT)
                .setButtonBackground(ContextCompat.getDrawable(this, R.drawable.rounded))
                .setButtonTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setPaddingTitle(50,10,40,10)
                .setPaddingMessage(50,10,40,10)
                .setPaddingButton(0,10,40,10)
                .setButtonText("OK")
                .setTargetView(myButton)
                .setVisibleBackgroundOverlay(true)
                .setBackgroundColor(Color.BLUE)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.my_button :

                                Log.d("button target","view");
                                break;
                        }


                    }
                });

        mGuideView = builder.build();
        mGuideView.show();

        mGuideView.mMessageView.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGuideView.dismiss(v);
            }
        });
    }

    private void showTooltip(View targetView) {
        builder = new GuideView.Builder(this);
        builder.setShowCaseType(ShowCaseType.TOOLTIP);
        builder.setTargetView(targetView);
        builder.setViewAlign(AlignType.center);
        builder.setDismissType(DismissType.outside);
        builder.setBackgroundColor(android.R.color.transparent);
        builder.setContentTextColor(android.R.color.white);
        builder.setContentBackground(R.drawable.rounded);
        builder.setTooltipTriangleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.setTooltipTriangleSize(10);
        builder.setTooltipArrow(TooltipArrow.RIGHT);
        builder.setContentText("This is tooltip implementation");
        mGuideView = builder.build();
        mGuideView.show();
    }
}
