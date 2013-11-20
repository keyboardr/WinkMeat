package com.winkmeat.glass.data;

import java.util.ArrayList;

public class PollingResult {
	public Long getTime() {
		return time;
	}
	public Integer getSetPoint() {
		return set;
	}
	public Integer getLidOpen() {
		return lid;
	}
	public ArrayList<Temp> getTemps() {
		return temps;
	}
	Long time;
	Integer set;
	Integer lid;
	ArrayList<Temp> temps;
}