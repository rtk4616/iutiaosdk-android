package com.iutiao.sdk.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.iutiao.sdk.R;

public class CountdownButton extends TextView {
    private Context mContext;
    private CountDownTask countDownTask;
    private String resendText = "resend";
    private int countTime = 60;
    public CountdownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.IUTCountdownButton);
        resendText = a.getString(R.styleable.IUTCountdownButton_resend_text);
        countTime = a.getInt(R.styleable.IUTCountdownButton_count_time,60);
        init(mContext);
    }

    public CountdownButton(Context context) {
        super(context);
    }

    private void init(final Context mContext) {
//        初始化view
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startCountDown();
                return false;
            }
        });
    }

    public void startCountDown() {
        // execute parallel
        // TODO: 16/4/5  并发触发处理
        countDownTask = new CountDownTask(CountdownButton.this);
        countDownTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class CountDownTask extends AsyncTask<Void, Integer, Void> {
        private int i = countTime;
        private TextView countTv;

        public CountDownTask(TextView btn) {
            this.countTv = btn;
        }

        @Override
        protected void onPreExecute() {
            this.countTv.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            this.countTv.setEnabled(true);
            this.countTv.setText(resendText);
            this.countTv.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setCountdown(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (i > 0) {
                if (isCancelled()) {
                    return null;
                }
                try {
                    Thread.sleep(1000 * 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
                publishProgress(i);
            }
            return null;
        }

        private void setCountdown(Integer value) {
            this.countTv.setText(String.format("%d s", value));
        }
    }
}