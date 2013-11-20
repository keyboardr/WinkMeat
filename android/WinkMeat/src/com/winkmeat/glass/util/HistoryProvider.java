package com.winkmeat.glass.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HistoryProvider {

	public static final int MAX_SIZE = 15;
	private LinkedList<Double> historyItems = new LinkedList<Double>();

	public void putTemp(Double temp) {
		historyItems.add(temp);
		if (historyItems.size() > MAX_SIZE) {
			historyItems.poll();
		}
	}

	public List<Double> getTemps() {
		return Collections.unmodifiableList(historyItems);
	}

}
