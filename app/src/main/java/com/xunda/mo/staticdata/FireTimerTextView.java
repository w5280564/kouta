package com.xunda.mo.staticdata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class FireTimerTextView extends TextView {
    private CountDownTimer timer;

    private TimerListener timerListener;
    private boolean isTiming = false;
    public FireTimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        getText().toString().length();
        initTimer();
    }



    private void initTimer() {
        timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                isTiming = false;
                setEnabled(true);
                timerListener.onFinish();
            }

        };
    }

    public void startTimer(TimerListener timerListener) {
        if (!isTiming){
            timer.cancel();
        }
        isTiming = true;
        setEnabled(false);
        this.timerListener = timerListener;
        timer.start();
    }

    public boolean isTiming() {
        return isTiming;
    }

    public interface TimerListener {
        void onFinish();
    }

    public void cancel() {
        timer.cancel();
        isTiming = false;
    }
}
