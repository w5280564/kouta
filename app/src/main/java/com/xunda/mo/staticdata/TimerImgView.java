package com.xunda.mo.staticdata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class TimerImgView extends ImageView {
    private CountDownTimer timer;

    private TimerListener timerListener;
    private boolean isTiming = false;
    public TimerImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTimer(10000);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void setTimer(int millis){
        initTimer(millis);
    }



    private void initTimer(int millis) {
        timer = new CountDownTimer(millis, 1000) {
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
