package com.winkmeat.glass;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.winkmeat.glass.data.PollingResult;
import com.winkmeat.glass.util.Alarm;
import com.winkmeat.glass.util.Alarm.OnResetListener;
import com.winkmeat.glass.util.Alarm.OnTripListener;
import com.winkmeat.glass.util.HistoryImageGenerator;
import com.winkmeat.glass.util.HistoryProvider;
import com.winkmeat.glass.util.SmokerTalker;

public class LiveCardService extends Service implements OnTripListener,
		OnResetListener {

	private static final String CARD_ID = "winkmeat_card";
	private static final long POLLING_PERIOD = 2000;
	private static final int RESET_POINT_DIFF = 5;
	private static boolean DEBUG_ALARM = false;

	public static void startService(Context context, Uri probeUri) {
		Intent intent = new Intent(context, LiveCardService.class);
		intent.setData(probeUri);
		context.startService(intent);
	}

	private Uri mProbeUri;

	private LiveCard mLiveCard;
	private PollingThread mPollingThread;

	private Alarm alarm1;
	private Alarm alarm2;
	private Alarm alarm3;

	private HistoryProvider history1 = new HistoryProvider();
	private HistoryProvider history2 = new HistoryProvider();
	private HistoryProvider history3 = new HistoryProvider();

	@Override
	public void onCreate() {
		super.onCreate();
		mPollingThread = new PollingThread();
		mPollingThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mProbeUri = intent.getData();
		refreshAlarms();
		registerReceiver(alarmChangedReceiver, alarmChangedFilter);
		super.onStartCommand(intent, flags, startId);
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(alarmChangedReceiver);
		mPollingThread.quit();
		removeCard();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private static final IntentFilter alarmChangedFilter = new IntentFilter(
			SetAlarmActivity.ACTION_BROADCAST_ALARM_CHANGED);
	private BroadcastReceiver alarmChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			refreshAlarms();
		}
	};

	private void refreshAlarms() {
		if (BuildConfig.DEBUG) {
			Log.v(getClass().getSimpleName(), "refreshing alarms");
		}
		int alarm1progress = SetAlarmActivity.getProgress(this, R.id.alarm1);
		Log.v(getClass().getSimpleName(), "alarm progress: " + alarm1progress);
		if (alarm1progress == 0) {
			if (alarm1 != null) {
				alarm1.reset();
			}
			alarm1 = null;
		} else {
			int alarm1Value = SetAlarmActivity.getValue(alarm1progress);
			if (BuildConfig.DEBUG) {
				Log.v(getClass().getSimpleName(), "alarm value: " + alarm1Value);
			}
			if (alarm1 == null) {
				alarm1 = new Alarm(alarm1Value, alarm1Value - RESET_POINT_DIFF);
				alarm1.setOnTripListener(this);
				alarm1.setOnResetListener(this);
			} else {
				alarm1.setPoints(alarm1Value, alarm1Value - RESET_POINT_DIFF);
			}
		}

		int alarm2progress = SetAlarmActivity.getProgress(this, R.id.alarm2);
		if (alarm2progress == 0) {
			if (alarm2 != null) {
				alarm2.reset();
			}
			alarm2 = null;
		} else {
			int alarm2Value = SetAlarmActivity.getValue(alarm2progress);
			if (BuildConfig.DEBUG) {
				Log.v(getClass().getSimpleName(), "alarm value: " + alarm2Value);
			}
			if (alarm2 == null) {
				alarm2 = new Alarm(alarm2Value, alarm2Value - RESET_POINT_DIFF);
				alarm2.setOnTripListener(this);
				alarm2.setOnResetListener(this);
			} else {
				alarm2.setPoints(alarm2Value, alarm2Value - RESET_POINT_DIFF);
			}
		}

		int alarm3progress = SetAlarmActivity.getProgress(this, R.id.alarm3);
		if (alarm3progress == 0) {
			if (alarm3 != null) {
				alarm3.reset();
			}
			alarm3 = null;
		} else {
			int alarm3Value = SetAlarmActivity.getValue(alarm3progress);
			if (BuildConfig.DEBUG) {
				Log.v(getClass().getSimpleName(), "alarm value: " + alarm3Value);
			}
			if (alarm3 == null) {
				alarm3 = new Alarm(alarm3Value, alarm3Value - RESET_POINT_DIFF);
				alarm3.setOnTripListener(this);
				alarm3.setOnResetListener(this);
			} else {
				alarm3.setPoints(alarm3Value, alarm3Value - RESET_POINT_DIFF);
			}
		}
	}

	private void updateAlarmValues(double temp1, double temp2, double temp3) {
		if (alarm1 != null) {
			alarm1.consumeInput(temp1);
		}

		if (alarm2 != null) {
			alarm2.consumeInput(temp2);
		}

		if (alarm3 != null) {
			alarm3.consumeInput(temp3);
		}
	}

	private void publishCard(Context context, PollingResult data) {
		if (mLiveCard == null) {
			mLiveCard = new LiveCard(this, CARD_ID);

			Intent intent = new Intent(context, AlarmListActivity.class);
			mLiveCard.setAction(PendingIntent
					.getActivity(context, 0, intent, 0));
			mLiveCard.publish(PublishMode.REVEAL);
			mLiveCard.attach(this);
		}
		updateAlarmValues(data.getTemps().get(0).getTemperature(), data
				.getTemps().get(1).getTemperature(), data.getTemps().get(2)
				.getTemperature());
		bindView(context, mLiveCard, data);
	}

	private void bindView(Context context, LiveCard card, PollingResult data) {
		RemoteViews view = new RemoteViews(context.getPackageName(),
				R.layout.activity_main);

		view.setTextViewText(R.id.probe1temp,
				String.format("%.1f°", data.getTemps().get(0).getTemperature()));
		view.setTextColor(R.id.probe1temp,
				alarm1 == null || !alarm1.getIsTripped() ? 0xffffffff
						: 0xffcc3333);
		float probe1alarm = SetAlarmActivity.getProgress(this, R.id.alarm1);
		view.setViewVisibility(R.id.probe1alarm,
				probe1alarm == 0 ? View.INVISIBLE : View.VISIBLE);
		view.setTextViewText(R.id.probe1alarm,
				SetAlarmActivity.getReadableValue(probe1alarm));
		history1.putTemp(data.getTemps().get(0).getTemperature());
		view.setImageViewBitmap(R.id.probe1graph, HistoryImageGenerator
				.drawHistory(history1.getTemps(), probe1alarm == 0 ? null
						: (double) SetAlarmActivity.getValue(probe1alarm)));

		view.setTextViewText(R.id.probe2temp,
				String.format("%.1f°", data.getTemps().get(1).getTemperature()));
		view.setTextColor(R.id.probe2temp,
				alarm2 == null || !alarm2.getIsTripped() ? 0xffffffff
						: 0xffcc3333);
		float probe2alarm = SetAlarmActivity.getProgress(this, R.id.alarm2);
		view.setViewVisibility(R.id.probe2alarm,
				probe2alarm == 0 ? View.INVISIBLE : View.VISIBLE);
		view.setTextViewText(R.id.probe2alarm,
				SetAlarmActivity.getReadableValue(probe2alarm));
		history2.putTemp(data.getTemps().get(1).getTemperature());
		view.setImageViewBitmap(R.id.probe2graph, HistoryImageGenerator
				.drawHistory(history2.getTemps(), probe2alarm == 0 ? null
						: (double) SetAlarmActivity.getValue(probe2alarm)));

		view.setTextViewText(R.id.probe3temp,
				String.format("%.1f°", data.getTemps().get(2).getTemperature()));
		view.setTextColor(R.id.probe3temp,
				alarm3 == null || !alarm3.getIsTripped() ? 0xffffffff
						: 0xffcc3333);
		float probe3alarm = SetAlarmActivity.getProgress(this, R.id.alarm3);
		view.setViewVisibility(R.id.probe3alarm,
				probe3alarm == 0 ? View.INVISIBLE : View.VISIBLE);
		view.setTextViewText(R.id.probe3alarm,
				SetAlarmActivity.getReadableValue(probe3alarm));
		history3.putTemp(data.getTemps().get(2).getTemperature());
		view.setImageViewBitmap(R.id.probe3graph, HistoryImageGenerator
				.drawHistory(history3.getTemps(), probe3alarm == 0 ? null
						: (double) SetAlarmActivity.getValue(probe3alarm)));

		card.setViews(view);
	}

	private void removeCard() {
		if (mLiveCard != null) {
			mLiveCard.unpublish();
			mLiveCard = null;
		}
	}

	private class PollingThread extends Thread {
		private boolean mShouldRun = true;

		public PollingThread() {
			super("RenderThread");
		}

		private synchronized boolean shouldRun() {
			return mShouldRun;
		}

		private synchronized void quit() {
			mShouldRun = false;
		}

		@Override
		public void run() {
			while (shouldRun()) {
				if (mProbeUri != null) {
					poll();
				}
				SystemClock.sleep(POLLING_PERIOD);
			}
		}

		protected void poll() {
			PollingResult result = SmokerTalker.getSmokerStatus(mProbeUri);
			if (DEBUG_ALARM) {
				result.getTemps().get(0).setTemperature(TEST_TEMP.getNext());
			}
			publishCard(LiveCardService.this, result);
		}

	}

	private TestTemp TEST_TEMP = new TestTemp();

	private class TestTemp {
		double temp = 100d;

		public double getNext() {
			if (alarm1 == null || !alarm1.getIsTripped()) {
				temp++;
			} else {
				temp--;
			}
			return temp;
		}
	}

	@Override
	public void onTrip(Alarm alarm) {
		Intent intent = new Intent(this, AlarmActivity.class);
		if (alarm == alarm1) {
			intent.putExtra(AlarmActivity.EXTRA_ALARM_ID, R.id.alarm1);
		}
		if (alarm == alarm2) {
			intent.putExtra(AlarmActivity.EXTRA_ALARM_ID, R.id.alarm2);
		}
		if (alarm == alarm3) {
			intent.putExtra(AlarmActivity.EXTRA_ALARM_ID, R.id.alarm3);
		}
		intent.putExtra(AlarmActivity.EXTRA_ALARM_TEMP, alarm.getSetPoint());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(intent);
	}

	@Override
	public void onReset(Alarm alarm) {

		Intent intent = new Intent(AlarmActivity.ACTION_ALARM_RESET);
		if (alarm == alarm1) {
			intent.putExtra(AlarmActivity.EXTRA_ALARM_ID, R.id.alarm1);
		}
		if (alarm == alarm2) {
			intent.putExtra(AlarmActivity.EXTRA_ALARM_ID, R.id.alarm2);
		}
		if (alarm == alarm3) {
			intent.putExtra(AlarmActivity.EXTRA_ALARM_ID, R.id.alarm3);
		}
		sendBroadcast(intent);
	}

}
