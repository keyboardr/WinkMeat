package com.winkmeat.glass;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;
import com.winkmeat.glass.data.PollingResult;

public class LiveCardService extends Service {

	private static final String CARD_ID = "winkmeat_card";

	public static void startService(Context context, Uri probeUri) {
		// TODO
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private LiveCard mLiveCard;

	private void publishCard(Context context, PollingResult data) {
		if (mLiveCard == null) {
			TimelineManager timeMan = TimelineManager.from(context);
			mLiveCard = timeMan.getLiveCard(CARD_ID);
		}
	}

	private void bindView(Context context, LiveCard card, PollingResult data) {
		RemoteViews view = new RemoteViews(context.getPackageName(),
				R.layout.activity_main);

	}

	private void updateCard(Context context, PollingResult data) {
		// TODO
	}

	private void removeCard(Context context) {
		// TODO
	}

}
