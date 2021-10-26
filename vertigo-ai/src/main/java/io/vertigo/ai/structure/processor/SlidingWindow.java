package io.vertigo.ai.structure.processor;

import java.util.OptionalInt;

public class SlidingWindow {

	private SlidingWindowType slidingWindowType;
	private OptionalInt minValue;
	private OptionalInt maxValue;

	public SlidingWindow(SlidingWindowType slidingWindowType, OptionalInt minValue, OptionalInt maxValue) {
		super();
		this.slidingWindowType = slidingWindowType;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public SlidingWindowType getSlidingWindowType() {
		return slidingWindowType;
	}
	
	public void setSlidingWindowType(SlidingWindowType slidingWindowType) {
		this.slidingWindowType = slidingWindowType;
	}
	
	public OptionalInt getMinValue() {
		return minValue;
	}
	
	public void setMinValue(OptionalInt minValue) {
		this.minValue = minValue;
	}
	
	public OptionalInt getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(OptionalInt maxValue) {
		this.maxValue = maxValue;
	}
	
}