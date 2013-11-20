package com.winkmeat.glass;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;
import com.winkmeat.glass.data.PollingResult;
import com.winkmeat.glass.util.Alarm;
import com.winkmeat.glass.util.Alarm.OnResetListener;
import com.winkmeat.glass.util.Alarm.OnTripListener;
import com.winkmeat.glass.util.SmokerTalker;

public class LiveCardService extends Service implements OnTripListener,
		OnResetListener {

	private static final String CARD_ID = "winkmeat_card";
	private static final long POLLING_PERIOD = 2000;
	private static final int RESET_POINT_DIFF = 5;
	private static boolean DEBUG_ALARM = true;
	public static final String INTERSTITIAL_CARD_ID = "interstitialCard";
	
	public static LiveCard interstitialLiveCard;

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
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		mPollingThread.quit();
		removeCard();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		refreshAlarms();
		return false;
	}

	private void refreshAlarms() {
		float alarm1progress = SetAlarmActivity.getProgress(this, R.id.alarm1);
		if (alarm1progress == 0) {
			alarm1 = null;
		} else {
			int alarm1Value = SetAlarmActivity.getValue(alarm1progress);
			if (alarm1 == null) {
				alarm1 = new Alarm(alarm1Value, alarm1Value - RESET_POINT_DIFF);
				alarm1.setOnTripListener(this);
				alarm1.setOnResetListener(this);
			} else {
				alarm1.setPoints(alarm1Value, alarm1Value - RESET_POINT_DIFF);
			}
		}

		float alarm2progress = SetAlarmActivity.getProgress(this, R.id.alarm2);
		if (alarm2progress == 0) {
			alarm2 = null;
		} else {
			int alarm2Value = SetAlarmActivity.getValue(alarm2progress);
			if (alarm2 == null) {
				alarm2 = new Alarm(alarm2Value, alarm2Value - RESET_POINT_DIFF);
				alarm2.setOnTripListener(this);
				alarm2.setOnResetListener(this);
			} else {
				alarm2.setPoints(alarm2Value, alarm2Value - RESET_POINT_DIFF);
			}
		}

		float alarm3progress = SetAlarmActivity.getProgress(this, R.id.alarm3);
		if (alarm3progress == 0) {
			alarm3 = null;
		} else {
			int alarm3Value = SetAlarmActivity.getValue(alarm3progress);
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
			TimelineManager timeMan = TimelineManager.from(context);
			mLiveCard = timeMan.getLiveCard(CARD_ID);

			mLiveCard.setNonSilent(true);

			Intent intent = new Intent(context, AlarmListActivity.class);
			mLiveCard.setAction(PendingIntent
					.getActivity(context, 0, intent, 0));
			mLiveCard.publish();
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
			if (BuildConfig.DEBUG) {
				Log.v(getClass().getCanonicalName(), result.getTemps()
						.toString());
			}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onReset(Alarm alarm) {
		// TODO Auto-generated method stub

	}

}
