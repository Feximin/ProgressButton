package com.feximin.progressbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ProgressButton mProgressButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressButton = (ProgressButton) findViewById(R.id.btn);
        mProgressButton.post(progressRunnable);
        final TextView tv = (TextView) findViewById(R.id.tv);
        tv.post(new Runnable() {
            @Override
            public void run() {
                int w = tv.getWidth();
                int h = tv.getHeight();
            }
        });

    }
    private int mProgress;
    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            mProgress ++;
            if (mProgress > 100){
                mProgress = 0;
            }
            mProgressButton.setProgress(mProgress);
            mProgressButton.postDelayed(this, 100);
        }
    };
}
