package com.winkmeat.glass.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.winkmeat.glass.R;

public class SliderView extends FrameLayout {
	private static final long HIDE_SLIDER_TIMEOUT_MILLIS = 2000L;
	private static final int MIN_SLIDER_WIDTH_PX = 40;
	private static final long SLIDER_BAR_RESIZE_ANIMATION_DURATION_MILLIS = 300L;
	private float animatedCount = 0.0F;
	private int count = 0;
	private ObjectAnimator countAnimator;
	private Runnable hideSliderRunnable = new Runnable() {
		@Override
		public void run() {
			SliderView.this.hideSlider(true);
		}
	};
	private final ImageView indeterminateSlider;
	private float index = 0.0F;
	private ViewPropertyAnimator progressAnimator;
	private float slideableScale = 1.0F;
	private final View slider;
	private boolean sliderShowing = true;
	private boolean sliderWasShowing = false;

	public SliderView(Context context) {
		this(context, null);
	}

	public SliderView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public SliderView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		LayoutInflater.from(getContext()).inflate(R.layout.slider, this);
		this.slider = findViewById(R.id.slider_control);
		this.indeterminateSlider = ((ImageView) findViewById(R.id.indeterminate_slider));
		hideSlider(false);
		hideIndeterminateSlider(false);
	}

	private void animateCountTo(float count) {
		if ((this.countAnimator != null) && (this.countAnimator.isRunning())) {
			this.countAnimator.cancel();
		}
		float[] arrayOfFloat = new float[2];
		arrayOfFloat[0] = this.animatedCount;
		arrayOfFloat[1] = count;
		this.countAnimator = ObjectAnimator.ofFloat(this, "animatedCount",
				arrayOfFloat);
		this.countAnimator
				.setDuration(SLIDER_BAR_RESIZE_ANIMATION_DURATION_MILLIS);
		this.countAnimator.start();
	}

	private int getBaseSliderWidth() {
		return Math
				.max((int) (getResources().getDisplayMetrics().widthPixels / this.animatedCount),
						MIN_SLIDER_WIDTH_PX);
	}

	private void hideIndeterminateSlider(boolean animate) {
		int height = getResources().getDimensionPixelSize(
				R.dimen.slider_bar_height);
		if (animate) {
			this.indeterminateSlider
					.animate()
					.translationY(height)
					.setDuration(
							getResources()
									.getInteger(
											R.integer.slider_in_out_animation_duration_ms));
		} else {
			this.indeterminateSlider.setTranslationY(height);
		}
	}

	private void hideSlider(boolean animate) {
		if (!this.sliderShowing) {
			return;
		}
		int height = getResources().getDimensionPixelSize(
				R.dimen.slider_bar_height);
		if (animate) {
			this.slider
					.animate()
					.translationY(height)
					.setDuration(
							getResources()
									.getInteger(
											R.integer.slider_in_out_animation_duration_ms));
		} else {
			slider.setTranslationY(height);
			sliderShowing = false;
			return;
		}

	}

	private void hideSliderAfterTimeout() {
		removeCallbacks(this.hideSliderRunnable);
		postDelayed(this.hideSliderRunnable, HIDE_SLIDER_TIMEOUT_MILLIS);
	}

	private void setProportionalIndex(float nextIndex, int animationDuration,
			boolean showSlider) {
		if (this.count < 2) {
			hideSlider(true);
			return;
		}

		this.index = nextIndex;

		float numItemsOnScreen = 1.0F / this.slideableScale;
		float indexOnLeftEdge = (0.5F + this.index - numItemsOnScreen / 2.0F);

		int width = getResources().getDisplayMetrics().widthPixels;
		int baseSliderWidth = width / count;

		float newX = indexOnLeftEdge * baseSliderWidth;

		if (animationDuration == 0) {
			this.slider.setTranslationX(newX);
		} else {
			slider.animate().translationX(newX).setDuration(animationDuration)
					.setInterpolator(new AccelerateDecelerateInterpolator());
		}

		if (showSlider) {
			showSlider(true);
			hideSliderAfterTimeout();
		}
		return;

	}

	private void showIndeterminateSlider(boolean animate) {
		if (animate) {
			this.indeterminateSlider
					.animate()
					.translationY(0.0F)
					.setDuration(
							getResources()
									.getInteger(
											R.integer.slider_in_out_animation_duration_ms));
		} else {
			this.indeterminateSlider.setTranslationY(0.0F);
		}
	}

	private void showSlider(boolean animate) {
		removeCallbacks(this.hideSliderRunnable);
		if (this.sliderShowing) {
			return;
		}
		if (animate) {
			this.slider
					.animate()
					.translationY(0.0F)
					.setDuration(
							getResources()
									.getInteger(
											R.integer.slider_in_out_animation_duration_ms));
		} else {
			this.slider.setTranslationY(0.0F);
		}
		this.sliderShowing = true;
	}

	private void updateSliderWidth(boolean showSlider) {
		if (this.count < 2) {
			hideSlider(true);
			return;
		}
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.slider
				.getLayoutParams();
		params.width = ((int) (1.0F / this.slideableScale * getBaseSliderWidth()));
		params.leftMargin = 0;
		this.slider.setLayoutParams(params);
		if (showSlider) {
			showSlider(true);
		}
		setProportionalIndex(this.index, 0, showSlider);
	}

	public void dismissManualProgress() {
		hideSlider(true);
	}

	float getAnimatedCount() {
		return this.animatedCount;
	}

	void setAnimatedCount(float count) {
		setAnimatedCount(count, true);
	}

	void setAnimatedCount(float count, boolean showSlider) {
		this.animatedCount = count;
		updateSliderWidth(showSlider);
	}

	public void setCount(int count) {
		setCount(count, true);
	}

	public void setCount(int count, boolean showSlider) {
		hideIndeterminateSlider(true);
		hideSlider(true);
		this.count = count;
		this.index = Math.max(Math.min(this.index, count - 1), 0.0F);
		if (showSlider) {
			animateCountTo(count);
			return;
		}
		setAnimatedCount(count, false);
	}

	public void setManualProgress(float progress) {
		setManualProgress(progress, false);
	}

	public void setManualProgress(float progress, boolean animate) {
		hideIndeterminateSlider(true);
		showSlider(false);
		int width = getResources().getDisplayMetrics().widthPixels;
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.slider
				.getLayoutParams();
		params.width = width;
		params.setMargins(-width, 0, 0, 0);
		this.slider.setLayoutParams(params);
		if (animate) {
			this.slider.animate().translationX(progress * width);
		} else {
			this.slider.setTranslationX(progress * width);
		}
	}

	public void setProportionalIndex(float nextIndex) {
		setProportionalIndex(nextIndex, 0, true);
	}

	public void setProportionalIndex(float nextIndex, int animationDuration) {
		setProportionalIndex(nextIndex, animationDuration, true);
	}

	public void setScale(float scale) {
		this.slideableScale = scale;
		updateSliderWidth(true);
	}

	public void startIndeterminate() {
		int width = getResources().getDisplayMetrics().widthPixels;
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.slider
				.getLayoutParams();
		params.width = width;
		params.setMargins(0, 0, 0, 0);
		this.slider.setLayoutParams(params);
		if (this.sliderShowing) {
			this.sliderWasShowing = true;
			hideSlider(true);
		}
		showIndeterminateSlider(true);
		((AnimationDrawable) this.indeterminateSlider.getBackground()).start();
	}

	public void startProgress(long animationDuration) {
		startProgress(animationDuration, new AccelerateDecelerateInterpolator());
	}

	public void startProgress(long animationDuration,
			Animator.AnimatorListener listener) {
		startProgress(animationDuration,
				new AccelerateDecelerateInterpolator(), listener);
	}

	public void startProgress(long animationDuration,
			TimeInterpolator interpolator) {
		startProgress(animationDuration, interpolator, null);
	}

	public void startProgress(long animationDuration,
			TimeInterpolator interpolator, Animator.AnimatorListener listener) {
		hideIndeterminateSlider(true);
		this.slider.setTranslationX(0.0F);
		showSlider(false);
		int width = getResources().getDisplayMetrics().widthPixels;
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.slider
				.getLayoutParams();
		params.width = width;
		params.setMargins(-width, 0, 0, 0);
		this.slider.setLayoutParams(params);
		this.progressAnimator = this.slider.animate().translationX(width)
				.setDuration(animationDuration).setInterpolator(interpolator)
				.setListener(listener);
	}

	public void stopIndeterminate() {
		if (this.sliderWasShowing) {
			showSlider(true);
		}
		((AnimationDrawable) this.indeterminateSlider.getBackground()).stop();
		hideIndeterminateSlider(true);
	}

	public void stopProgress() {
		if (this.progressAnimator != null) {
			this.progressAnimator.cancel();
		}
		hideSlider(true);
	}
}