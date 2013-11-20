package com.winkmeat.glass;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.TimelineManager;
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
	public int onStartCommand (Intent intent, int flags, int startId){
/*		TODO Using the card from here causes the app to quit.  Talk w/ Josh.
 * 
 * if (LiveCardService.interstitialLiveCard == null){
			Context context = getApplicationContext();
			TimelineManager timeMan = TimelineManager.from(context);
			LiveCardService.interstitialLiveCard = timeMan.getLiveCard(LiveCardService.INTERSTITIAL_CARD_ID);

			LiveCardService.interstitialLiveCard.setNonSilent(true);

			LiveCardService.interstitialLiveCard.publish();
			RemoteViews view = new RemoteViews(context.getPackageName(),
					R.layout.interstitial_layout);
			LiveCardService.interstitialLiveCard.setViews(view);
		}*/
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		LiveCardService.startService(this, SmokerTalker.LOCAL_URI);
		PollingResult smokerStatus = SmokerTalker.getSmokerStatus(null);
		Log.d("StartupService", smokerStatus.toString());
	}
}
