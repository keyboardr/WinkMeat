package com.winkmeat.glass;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

public class LiveCardService extends Service {

	public static void startService(Uri probeUri) {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
