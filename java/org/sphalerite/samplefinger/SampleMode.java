package org.sphalerite.samplefinger;

public enum SampleMode {
	OFF, LOOP, SCRUB;
	public String toString() {
		switch(this) {
		case OFF: return "off";
		case LOOP: return "loop";
		case SCRUB: return "scrub";
		default: return "";
		}
	}
}
