package com.winkmeat.glass.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Temp implements Parcelable {

	String n;
	Double c;

	public Temp() {
	}

	public Temp(Parcel source) {
		this.n = source.readString();
		this.c = source.readDouble();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(n);
		dest.writeDouble(c);
	}
	
	public static final Parcelable.Creator<Temp> CREATOR = new Parcelable.Creator<Temp>() {

		@Override
		public Temp createFromParcel(Parcel source) {
			return new Temp(source);
		}

		@Override
		public Temp[] newArray(int size) {
			return new Temp[size];
		}

	};

	public String getProbeName() {
		return n;
	}

	public Double getTemperature() {
		return c;
	}

	@Override
	public String toString() {
		return n + ":" + c;
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
