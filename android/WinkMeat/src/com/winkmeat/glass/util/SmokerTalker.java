package com.winkmeat.glass.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winkmeat.glass.data.PollingResult;

public class SmokerTalker {

	public static final Uri LOCAL_URI = Uri
			.parse("http://192.168.1.3/luci/lm/hmstatus");
	public static final boolean USE_DEBUG = false;

	public static PollingResult getSmokerStatus(Uri uri) {
		if (uri == null) {
			uri = SmokerTalker.LOCAL_URI;
		}

		Gson gson = new Gson();
		Type pollingResultType = new TypeToken<PollingResult>() {
		}.getType();
		String pollingResultJson = null;
		if (SmokerTalker.USE_DEBUG) {
			pollingResultJson = "{\"time\":1384903341,\"set\":225,\"lid\":0,\"fan\":{\"c\":0,\"a\":0},\"temps\":[{\"n\":\"Probe 0\",\"c\":74.3,\"a\":{\"l\":-40,\"h\":-200,\"r\":null}},{\"n\":\"Probe 1\",\"c\":80.3,\"a\":{\"l\":-40,\"h\":-200,\"r\":null}},{\"n\":\"Probe 2\",\"c\":72.2,\"a\":{\"l\":-40,\"h\":-200,\"r\":null}},{\"n\":\"Probe 3\",\"c\":88.9,\"a\":{\"l\":-40,\"h\":-200,\"r\":null}}]}";
		} else {
			pollingResultJson = SmokerTalker.getUrlResult(uri);
		}

		return gson.fromJson(pollingResultJson, pollingResultType);
	}

	public static String getUrlResult(Uri uri) {
		try {
			URL url = new URL(uri.toString());
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line = null;
			final StringBuffer buffer = new StringBuffer(2048);
			while ((line = rd.readLine()) != null) {
				buffer.append(line);
			}
			return buffer.toString();
		} catch (MalformedURLException e) {
			Log.e("StartupService", "Bad server address");
		} catch (UnknownHostException e) {
			Log.e("StartupService", "Unknown host: " + e.getLocalizedMessage());
		} catch (IOException e) {
			Log.e("StartupService", "IO exception");
		} catch (IllegalArgumentException e) {
			Log.e("StartupService", "Argument exception (probably bad port)");
		}

		return null;
	}

}
