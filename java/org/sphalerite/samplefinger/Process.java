package org.sphalerite.samplefinger;

import java.io.IOException;
import java.util.HashMap;
import java.io.*;
import java.net.*;

class Process {
	
	private boolean areTouched(Vector a, Vector b) {
		return a.distanceFrom(b) < 1.0;
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
