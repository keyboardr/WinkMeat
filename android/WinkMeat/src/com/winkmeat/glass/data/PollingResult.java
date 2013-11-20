package com.winkmeat.glass.data;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class PollingResult implements Parcelable {

	Long time;
	Integer set;
	Integer lid;
	ArrayList<Temp> temps;

	public PollingResult() {
	}

	public PollingResult(Parcel source) {
		this.time = source.readLong();
		this.set = source.readInt();
		this.lid = source.readInt();
		source.readTypedList(temps, Temp.CREATOR);
	}

	public static final Parcelable.Creator<PollingResult> CREATOR = new Parcelable.Creator<PollingResult>() {

		@Override
		public PollingResult createFromParcel(Parcel source) {
			return new PollingResult(source);
		}

		@Override
		public PollingResult[] newArray(int size) {
			return new PollingResult[size];
		}
	};

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.time);
		dest.writeInt(this.set);
		dest.writeInt(this.lid);
		dest.writeTypedList(this.temps);
	}

	@Override
	public String toString() {
		return "PollingResult [time=" + time + ", set=" + set + ", lid=" + lid + ", temps=" + temps + "]";
	}
}