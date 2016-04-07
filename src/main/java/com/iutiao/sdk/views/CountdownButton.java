package com.iutiao.sdk.views;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.iutiao.sdk.R;

public class CountdownButton extends TextView {
    private Context mContext;
    private CountDownTask countDownTask;
    public CountdownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(mContext);
    }

    public CountdownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        private int i = 60;
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
            this.countTv.setText(mContext.getString(R.string.com_iutiao_verify_resend));// TODO: 16/4/5  spannable
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