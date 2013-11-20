package com.winkmeat.glass.util;

public class Alarm {

	public static interface OnTripListener {
		public void onTrip(Alarm alarm);
	}

	public static interface OnResetListener {
		public void onReset(Alarm alarm);
	}

	private int mSetPoint;
	private int mResetPoint;

	private boolean isTripped;
	private Double mLastValue;

	private OnTripListener mTripListener;
	private OnResetListener mResetListener;

	public Alarm(int setPoint, int resetPoint) {
		this.mSetPoint = setPoint;
		this.mResetPoint = resetPoint;
	}

	public int getSetPoint() {
		return mSetPoint;
	}

	public void setSetPoint(int setPoint) {
		this.mSetPoint = setPoint;
		if (mLastValue != null) {
			consumeInput(mLastValue);
		}
	}

	public int getResetPoint() {
		return mResetPoint;
	}

	public void setResetPoint(int resetPoint) {
		this.mResetPoint = resetPoint;
		if (mLastValue != null) {
			consumeInput(mLastValue);
		}
	}

	public void setPoints(int setPoint, int resetPoint) {
		this.mSetPoint = setPoint;
		this.mResetPoint = resetPoint;
		if (mLastValue != null) {
			consumeInput(mLastValue);
		}
	}

	public void consumeInput(double value) {
		mLastValue = value;
		if (value >= mSetPoint && !isTripped) {
			trip();
		} else if (value <= mResetPoint && isTripped) {
			reset();
		}
	}

	public void setOnTripListener(OnTripListener listener) {
		mTripListener = listener;
	}

	public void setOnResetListener(OnResetListener listener) {
		mResetListener = listener;
	}

	public void trip() {
		isTripped = true;
		if (mTripListener != null) {
			mTripListener.onTrip(this);
		}
	}

	public void reset() {
		isTripped = false;
		if (mResetListener != null) {
			mResetListener.onReset(this);
		}
	}

	public boolean getIsTripped() {
		return isTripped;
	}

}
