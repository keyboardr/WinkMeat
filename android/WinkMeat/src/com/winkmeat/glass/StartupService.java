package com.winkmeat.glass;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import com.winkmeat.glass.util.SmokerTalker;

public class StartupService extends IntentService {

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
		Intent finishProgressIntent = new Intent(
				ProgressActivity.PROGRESS_FINISH);
		sendBroadcast(finishProgressIntent);
		LiveCardService.startService(this, SmokerTalker.LOCAL_URI);
	}
}