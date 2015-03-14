package org.sphalerite.samplefinger;

import com.leapmotion.leap.Finger;

public class FingerWrap {
	public final Finger fingerData;
	public final boolean isLeft;
	
	public FingerWrap(Finger data, boolean left) {
		fingerData = data;
		isLeft = left;
	}
}
