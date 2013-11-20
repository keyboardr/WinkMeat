package com.winkmeat.glass.util;

import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class HistoryImageGenerator {
	private static final float MIN_RANGE = 30f;

	public static Bitmap drawHistory(List<Double> history, Double alarm) {
		int height = 72;
		int width = 72;
		Bitmap bitmap = Bitmap.createBitmap(height, width,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		Paint axisPaint = new Paint();
		axisPaint.setColor(0xff888888);
		canvas.drawLine(0, 0, 0, height, axisPaint);
		canvas.drawLine(0, height - 1, width, height - 1, axisPaint);

		double maxValue = Collections.max(history);
		double minValue = Collections.min(history);
		double median = (maxValue + minValue) / 2;

		maxValue = Math.max(maxValue, median + MIN_RANGE / 2);
		minValue = Math.min(minValue, median - MIN_RANGE / 2);

		Paint historyPaint = new Paint();
		historyPaint.setColor(0xff34a7ff);
		historyPaint.setStrokeWidth(3f);
		for (int i = history.size() - 1; i > 0; i--) {
			double y = height
					- ((history.get(i) - minValue) / (maxValue - minValue))
					* height;
			double x = (((float) i) / 15) * width;
			double newY = height
					- ((history.get(i - 1) - minValue) / (maxValue - minValue))
					* height;
			double newX = (((float) i - 1) / 15) * width;
			canvas.drawLine((float) x, (float) y, (float) newX, (float) newY,
					historyPaint);
		}

		if (alarm != null && alarm < maxValue && alarm > minValue) {
			Paint alarmPaint = new Paint();
			alarmPaint.setColor(0xffcc3333);
			alarmPaint.setStrokeWidth(2f);
			double y = height - ((alarm - minValue) / (maxValue - minValue))
					* height;
			canvas.drawLine(0, (float) y, width, (float) y, alarmPaint);
		}
		return bitmap;
	}
}
