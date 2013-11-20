package com.winkmeat.glass;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.winkmeat.glass.data.PollingResult;
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
	protected void onHandleIntent(Intent intent) {
		LiveCardService.startService(this, SmokerTalker.LOCAL_URI);
		PollingResult smokerStatus = SmokerTalker.getSmokerStatus(null);
		Log.d("StartupService", smokerStatus.toString());
	}
}
