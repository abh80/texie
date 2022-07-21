package com.abh80.texie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import android.support.wearable.watchface.CanvasWatchFaceService;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;
import java.util.Calendar;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class MyWatchFace extends CanvasWatchFaceService {

    private static Calendar mCalendar;
    private static final int MSG_UPDATE_TIME = 0;
    private SharedPreferences sharedPreferences;


    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> engineWeakReference;

        public EngineHandler(MyWatchFace.Engine engineWeakReference) {
            this.engineWeakReference = new WeakReference<>(engineWeakReference);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MyWatchFace.Engine engine = engineWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final EngineHandler handler = new EngineHandler(this);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Engine.this.invalidate();
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            sharedPreferences = getSharedPreferences("com.abh80.texie.store", MODE_PRIVATE);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
        }

        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long delayMs = 1000;
                handler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onDestroy() {
            handler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
            if (registeredReceiver) {
                unregisterReceiver(receiver);
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mCalendar = Calendar.getInstance();
            if (sharedPreferences == null)
                sharedPreferences = getSharedPreferences("com.abh80.texie.store", MODE_PRIVATE);
            canvas.drawColor(Color.BLACK);
            int fontSize = 80;
            if (mCalendar.getTime().getHours() > 20) {
                fontSize = 60;
            } else if (mCalendar.getTime().getHours() + 1 > 20 && mCalendar.get(Calendar.MINUTE) > 30) {
                fontSize = 60;
            }
            int finalFontSize = fontSize;
            Paint paint = new Paint() {{
                setTextSize(finalFontSize);
                setColor(getResources().getColor(sharedPreferences.getInt("fc", android.support.wearable.R.color.white)));
                setTypeface(Typeface.DEFAULT_BOLD);
            }};

            String hoursText = Numbers.convert(mCalendar.getTime().getHours()).toUpperCase();

            int minutes = mCalendar.getTime().getMinutes();

            String minutesText;
            Paint paint2 = new Paint(
            ) {{
                setTextSize(60);
                setColor(getResources().getColor(sharedPreferences.getInt("fc", android.support.wearable.R.color.white)));
                setTypeface(Typeface.DEFAULT_BOLD);

            }};
            Paint paint1 = new Paint(
            ) {{
                setTextSize(80);
                setColor(getResources().getColor(sharedPreferences.getInt("fc", android.support.wearable.R.color.white)));

                setTypeface(Typeface.DEFAULT_BOLD);
            }};
            Paint paint3 = new Paint(

            ) {{
                setTextSize(50);
                setColor(getResources().getColor(sharedPreferences.getInt("fc", android.support.wearable.R.color.white)));
                setTypeface(Typeface.DEFAULT_BOLD);
            }};
            if (isInAmbientMode()) {
                float[] hsv = new float[3];
                Color.colorToHSV(getResources().getColor(sharedPreferences.getInt("fc", android.support.wearable.R.color.white)), hsv);
                hsv[2] = 0.25f;
                paint.setColor(Color.HSVToColor(hsv));
                paint1.setColor(Color.HSVToColor(hsv));
                paint2.setColor(Color.HSVToColor(hsv));
                paint3.setColor(Color.HSVToColor(hsv));
            }
            if (minutes == 30) {
                minutesText = "HALF";
                canvas.drawText("past", bounds.exactCenterX() - paint2.measureText("past") / 2, bounds.centerY(), paint2);
                canvas.drawText(minutesText, bounds.exactCenterX() - paint1.measureText(minutesText) / 2, bounds.centerY() - 75, paint1);
                canvas.drawText(hoursText, bounds.centerX() - paint.measureText(hoursText) / 2, bounds.centerY() + 95, paint);
            } else if (minutes == 0) {
                canvas.drawText("It's already", bounds.exactCenterX() - paint3.measureText("It's already") / 2, bounds.centerY() - 75 - 75, paint3);
                canvas.drawText(hoursText, bounds.exactCenterX() - paint.measureText(hoursText) / 2, bounds.centerY() - 30, paint);
                canvas.drawText("o' clock!", bounds.centerX() - paint.measureText("o' clock!") / 2, bounds.centerY() + 90, paint);
            } else if (minutes == 15) {
                minutesText = "QUARTER";
                canvas.drawText("past", bounds.exactCenterX() - paint2.measureText("past") / 2, bounds.centerY(), paint2);
                canvas.drawText(minutesText, bounds.exactCenterX() - paint1.measureText(minutesText) / 2, bounds.centerY() - 75, paint1);
                canvas.drawText(hoursText, bounds.centerX() - paint.measureText(hoursText) / 2, bounds.centerY() + 95, paint);
            } else if (minutes == 45) {
                minutesText = "QUARTER";
                canvas.drawText("to", bounds.exactCenterX() - paint2.measureText("to") / 2, bounds.centerY(), paint2);
                canvas.drawText(minutesText, bounds.exactCenterX() - paint1.measureText(minutesText) / 2, bounds.centerY() - 75, paint1);
                int hours = mCalendar.getTime().getHours();
                if (hours == 23) {
                    hours = 0;
                } else {
                    hours += 1;
                }
                hoursText = Numbers.convert(hours);
                canvas.drawText(hoursText.toUpperCase(), bounds.centerX() - paint.measureText(hoursText.toUpperCase()) / 2, bounds.centerY() + 95, paint);
            } else if (minutes < 30) {
                minutesText = String.valueOf(minutes);
                canvas.drawText("past", bounds.exactCenterX() - paint2.measureText("past") / 2, bounds.centerY(), paint2);
                canvas.drawText(minutesText, bounds.exactCenterX() - paint1.measureText(minutesText) / 2, bounds.centerY() - 75, paint1);
                canvas.drawText(hoursText, bounds.centerX() - paint.measureText(hoursText) / 2, bounds.centerY() + 95, paint);
            } else {
                minutes = 60 - minutes;
                minutesText = String.valueOf(minutes);
                canvas.drawText("to", bounds.exactCenterX() - paint2.measureText("to") / 2, bounds.centerY(), paint2);
                canvas.drawText(minutesText, bounds.exactCenterX() - paint1.measureText(minutesText) / 2, bounds.centerY() - 75, paint1);
                int hours = mCalendar.getTime().getHours();
                if (hours == 23) {
                    hours = 0;
                } else {
                    hours += 1;
                }
                hoursText = Numbers.convert(hours);
                canvas.drawText(hoursText.toUpperCase(), bounds.centerX() - paint.measureText(hoursText.toUpperCase()) / 2, bounds.centerY() + 95, paint);
            }
            if (isInAmbientMode()) return;
            Paint paint0 = new Paint() {{
                setTextSize(50);
                setColor(getResources().getColor(sharedPreferences.getInt("fc", android.support.wearable.R.color.white)));
                setTypeface(Typeface.DEFAULT_BOLD);
            }};
            int seconds = mCalendar.get(Calendar.SECOND);
            canvas.drawText(String.valueOf(seconds), bounds.exactCenterX() - paint0.measureText(String.valueOf(seconds)) / 2, bounds.centerY() + 95 + 80, paint0);
        }

        private boolean registeredReceiver = false;

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            updateTimer();
            if (visible && !registeredReceiver) {
                registerReceiver(receiver, new IntentFilter("com.abh80.texie.settings_update"));
                registeredReceiver = true;
            }
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (inAmbientMode) {
                invalidate();
            }
            updateTimer();
        }

        private void updateTimer() {
            handler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                handler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }
    }
}