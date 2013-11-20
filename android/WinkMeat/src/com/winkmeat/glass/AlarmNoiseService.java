package com.winkmeat.glass;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class AlarmNoiseService extends Service {

	private static final int START = 0;
	private static final int STOP = 1;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Handler backgroundHandler;

	PowerManager pm;
	WakeLock wl;

	@Override
	public void onCreate() {
		super.onCreate();

		pm = (PowerManager) getSystemService(POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		wl.acquire();

		HandlerThread backgroundThread = new HandlerThread("AlarmNoise");
		backgroundThread.start();
		backgroundHandler = new Handler(backgroundThread.getLooper()) {
			private MediaPlayer mMediaPlayer;

			@Override
			public void dispatchMessage(Message msg) {
				switch (msg.what) {
				case START:
					int resID = getResources().getIdentifier("alarm", "raw",
							getPackageName());
					mMediaPlayer = MediaPlayer.create(AlarmNoiseService.this,
							resID);
					mMediaPlayer.setLooping(true);
					mMediaPlayer.start();
					break;
				case STOP:
					mMediaPlayer.stop();
					mMediaPlayer.release();
					getLooper().quit();
					break;
				}
				super.dispatchMessage(msg);
			}
		};

		backgroundHandler.sendMessage(backgroundHandler.obtainMessage(START));
	}

	@Override
	public void onDestroy() {
		if (wl != null) {
			wl.release();
			wl = null;
		}
		backgroundHandler.sendMessage(backgroundHandler.obtainMessage(STOP));
		super.onDestroy();
	}

}
