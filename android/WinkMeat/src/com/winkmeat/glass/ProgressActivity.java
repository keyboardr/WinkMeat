package com.winkmeat.glass;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.winkmeat.glass.view.SliderView;

public class ProgressActivity extends Activity {
	public static final String PROGRESS_FINISH = "progressFinish";
	private final ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);
		((SliderView) findViewById(R.id.sliderView1)).startIndeterminate();
		registerReceiver(finishReceiver, finishFilter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent serviceBinding = new Intent(this, StartupService.class);
		bindService(serviceBinding, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		unbindService(serviceConnection);
		finish();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(finishReceiver);
	}

	private static final IntentFilter finishFilter = new IntentFilter(
			PROGRESS_FINISH);

	private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
}
