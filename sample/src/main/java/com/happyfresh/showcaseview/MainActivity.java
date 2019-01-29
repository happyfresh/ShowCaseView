package com.happyfresh.showcaseview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.happyfresh.showcase.GuideView;
import com.happyfresh.showcase.config.AlignType;
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
        showCoachLayout();
    }

    private void showCoachLayout() {
        builder = new GuideView.Builder(this)
                .setTitle("title lorem ipsum dolor sir amet")
                .setContentText("content lorem ipsum dolor sir amet")
                .setViewAlign(AlignType.center)
                .setTitleGravity(Gravity.CENTER)
                .setContentGravity(Gravity.RIGHT)
                .setButtonGravity(Gravity.CENTER)
                .setButtonText("OK")
                .setTargetView(myButton)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                       Log.d(TAG,"Success");
                    }
                });

        mGuideView = builder.build();
        mGuideView.show();

    }
}
