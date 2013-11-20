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

public class LiveCardService extends Service {

	private static final String CARD_ID = "winkmeat_card";
	public static final long POLLING_PERIOD = 2000;

	public static void startService(Context context, Uri probeUri) {
		Intent intent = new Intent(context, LiveCardService.class);
		intent.setData(probeUri);
		context.startService(intent);
	}

	private Uri mProbeUri;

	private LiveCard mLiveCard;
	private PollingThread mPollingThread;

	@Override
	public void onCreate() {
		super.onCreate();
		mPollingThread = new PollingThread();
		mPollingThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mProbeUri = intent.getData();
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
		bindView(context, mLiveCard, data);
	}

	private void bindView(Context context, LiveCard card, PollingResult data) {
		RemoteViews view = new RemoteViews(context.getPackageName(),
				R.layout.activity_main);

		view.setTextViewText(R.id.probe1temp,
				String.format("%.1f°", data.getTemps().get(0).getTemperature()));
		view.setViewVisibility(R.id.probe1alarm, View.INVISIBLE);

		view.setTextViewText(R.id.probe2temp,
				String.format("%.1f°", data.getTemps().get(1).getTemperature()));
		view.setViewVisibility(R.id.probe2alarm, View.INVISIBLE);

		view.setTextViewText(R.id.probe3temp,
				String.format("%.1f°", data.getTemps().get(2).getTemperature()));
		view.setViewVisibility(R.id.probe3alarm, View.INVISIBLE);

		card.setViews(view);
	}

	private void removeCard() {
		mLiveCard.unpublish();
		mLiveCard = null;
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
			PollingResult result = StartupService.getSmokerStatus(mProbeUri);
			publishCard(LiveCardService.this, result);
			if (BuildConfig.DEBUG) {
				Log.v(getClass().getCanonicalName(), result.getTemps()
						.toString());
			}
		}

	}

}
