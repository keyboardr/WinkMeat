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
import android.view.View;

import com.google.android.glass.app.Card;

public class AlarmActivity extends Activity {
	public static final String ACTION_ALARM_RESET = "ACTION_ALARM_RESET";
	public static final String EXTRA_ALARM_ID = "EXTRA_ALARM_ID";
	public static final String EXTRA_ALARM_TEMP = "EXTRA_ALARM_TEMP";

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

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
		Card card = new Card(this);
		int alarm = getIntent().getIntExtra(EXTRA_ALARM_ID, 0);
		int temp = getIntent().getIntExtra(EXTRA_ALARM_TEMP, 0);
		switch (alarm) {
		case R.id.alarm1:
			card.setText("Probe 1 Alarming");
			break;
		case R.id.alarm2:
			card.setText("Probe 2 Alarming");
			break;
		case R.id.alarm3:
			card.setText("Probe 3 Alarming");
			break;
		default:
			throw new IllegalArgumentException("Invalid alarm id:" + alarm);
		}
		card.setFootnote(String.format("%d°", temp));
		View view = card.toView();
		view.setKeepScreenOn(true);
		setContentView(view);
		registerReceiver(resetReceiver, resetFilter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent serviceBinding = new Intent(this, AlarmNoiseService.class);
		bindService(serviceBinding, mServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		unbindService(mServiceConnection);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(resetReceiver);
	}

	private static final IntentFilter resetFilter = new IntentFilter(
			ACTION_ALARM_RESET);
	private BroadcastReceiver resetReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra(EXTRA_ALARM_ID, 0) == getIntent()
					.getIntExtra(EXTRA_ALARM_ID, 0)) {
				finish();
			}
		}
	};
}
