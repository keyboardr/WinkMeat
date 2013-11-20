package com.winkmeat.glass;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.glass.view.MenuUtils;

public class AlarmListActivity extends Activity {
	@Override
	protected void onResume() {
		super.onResume();
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		MenuUtils.setDescription(
				menu.findItem(R.id.alarm1),
				"Probe 1: "
						+ SetAlarmActivity.getReadableValue(SetAlarmActivity
								.getProgress(this, R.id.alarm1)));
		MenuUtils.setDescription(
				menu.findItem(R.id.alarm2),
				"Probe 2: "
						+ SetAlarmActivity.getReadableValue(SetAlarmActivity
								.getProgress(this, R.id.alarm2)));
		MenuUtils.setDescription(
				menu.findItem(R.id.alarm3),
				"Probe 3: "
						+ SetAlarmActivity.getReadableValue(SetAlarmActivity
								.getProgress(this, R.id.alarm3)));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.alarm1:
		case R.id.alarm2:
		case R.id.alarm3:
			Intent intent = new Intent(this, SetAlarmActivity.class);
			intent.putExtra(SetAlarmActivity.EXTRA_ALARM, item.getItemId());
			startActivity(intent);
			return true;
		case R.id.stop:
			stopService(new Intent(this, LiveCardService.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		finish();
	}
}
