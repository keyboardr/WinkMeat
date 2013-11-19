package com.winkmeat.glass;

import android.content.Context;

import com.winkmeat.glass.data.PollingResult;
import com.winkmeat.glass.util.CachedLoader;

public class PollingLoader extends CachedLoader<PollingResult> {

	public PollingLoader(Context context) {
		super(context);
	}

	@Override
	public PollingResult loadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}

}
