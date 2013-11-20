package com.winkmeat.glass;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;
import com.winkmeat.glass.data.PollingResult;
import com.winkmeat.glass.util.SmokerTalker;

public class StartupService extends IntentService {

	private View pcView;
	private Card progressCard;

	public StartupService() {
		super("StartupService");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent progressIntent = new Intent(this, ProgressActivity.class);
		progressIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(progressIntent);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (pcView != null)
			pcView.setVisibility(View.GONE);
		progressCard = null;
		LiveCardService.startService(this, SmokerTalker.LOCAL_URI);
		PollingResult smokerStatus = SmokerTalker.getSmokerStatus(null);
		Log.d("StartupService", smokerStatus.toString());
		Intent finishProgressIntent = new Intent(ProgressActivity.PROGRESS_FINISH);
		sendBroadcast(finishProgressIntent);
	}
}
