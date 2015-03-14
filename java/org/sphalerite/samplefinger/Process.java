package org.sphalerite.samplefinger;

import java.net.SocketException;

import com.leapmotion.leap.Vector;

class Process {
	
	private boolean areTouched(Vector a, Vector b) {
		return a.distanceTo(b) < 1.0;
	}


	public static void main(String[] args) throws SocketException {
		PureDataMessenger c = new PureDataMessenger();
		if (c.send("Test from Process.main")) {
			System.out.println("Sent");
		}
		else {
			System.out.println("Failed to send");
		}
	}

}
