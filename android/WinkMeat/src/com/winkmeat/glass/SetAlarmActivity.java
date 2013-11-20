package com.winkmeat.glass;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.touchpad.GestureDetector.BaseListener;
import com.google.android.glass.touchpad.GestureDetector.ScrollListener;
import com.winkmeat.glass.view.SliderView;

public class SetAlarmActivity extends Activity {

	public static final String EXTRA_ALARM = "EXTRA_ALARM";

	private static final int MIN_VALUE = 100;
	private static final int MAX_VALUE = 300;
	private static final float MAX_PROGRESS = 1000f;

	private SliderView mProgress;
	private TextView mText;

	private GestureDetector mGestureDetector;

	private float position;

	private boolean tipHidden;

	private String alarmKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alarm);

		mProgress = (SliderView) findViewById(R.id.progress);
		mText = (TextView) findViewById(R.id.text);

		TextView title = (TextView) findViewById(R.id.title);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		int alarm = getIntent().getIntExtra(EXTRA_ALARM, R.id.alarm1);
		alarmKey = null;
		switch (alarm) {
		case R.id.alarm1:
			title.setText("Probe 1 Alarm:");
			alarmKey = "alarm1";
			break;
		case R.id.alarm2:
			title.setText("Probe 2 Alarm:");
			alarmKey = "alarm2";
			break;
		case R.id.alarm3:
			title.setText("Probe 3 Alarm:");
			alarmKey = "alarm3";
			break;
		default:
			throw new IllegalArgumentException("Invalid alarm id:" + alarm);
		}

		position = prefs.getFloat(alarmKey, 0);

		mGestureDetector = createGestureDetector();
		updateProgress();
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return super.onGenericMotionEvent(event);
	}

	private GestureDetector createGestureDetector() {
		GestureDetector detector = new GestureDetector(this);
		detector.setBaseListener(new BaseListener() {

			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {
					finish();
					return true;
				}
				return false;
			}
		});
		detector.setScrollListener(new ScrollListener() {

			@Override
			public boolean onScroll(float displacement, float delta,
					float velocity) {
				Log.v(getClass().getCanonicalName(),
						String.format(
								"displacement: %f, delta: %f, velocity: %f, position: %f",
								displacement, delta, velocity, position));
				setPosition(position + delta);
				if (!tipHidden) {
					findViewById(R.id.tip).animate().alpha(0);
				}
				return true;
			}
		});
		return detector;
	}

	@Override
	protected void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
				.edit().putFloat(alarmKey, position).apply();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	protected void setPosition(float f) {
		position = Math.max(0, Math.min(MAX_PROGRESS, f));
		updateProgress();
	}

	private void updateProgress() {
		mText.setText(getReadableValue(position));
		mProgress.setManualProgress(position / MAX_PROGRESS);
	}

	public static String getReadableValue(float progress) {
		return progress == 0 ? "Alarm Off" : String.format("%d°",
				getValue(progress));
	}

	public static final int getValue(float progress) {
		return (int) (MIN_VALUE + (progress / MAX_PROGRESS)
				* (MAX_VALUE - MIN_VALUE));
	}

	public static float getProgress(Context context, int alarmId) {
		String alarmKey;
		switch (alarmId) {
		case R.id.alarm1:
			alarmKey = "alarm1";
			break;
		case R.id.alarm2:
			alarmKey = "alarm2";
			break;
		case R.id.alarm3:
			alarmKey = "alarm3";
			break;
		default:
			throw new IllegalArgumentException("Invalid alarm id:" + alarmId);
		}
		return PreferenceManager.getDefaultSharedPreferences(
				context.getApplicationContext()).getFloat(alarmKey, 0);
	}

}
