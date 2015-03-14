package org.sphalerite.samplefinger;

import java.io.IOException;
import java.net.SocketException;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

class SampleFingerController extends Listener {
	private PureDataMessenger messenger;
	
	public SampleFingerController() throws SocketException {
		messenger = new PureDataMessenger();
	}
	
	private boolean areTouched(Vector a, Vector b) {
		return a.distanceTo(b) < 1.0;
	}


	public static void main(String[] args) throws SocketException {
        Listener listener = new SampleFingerController();
        Controller controller = new Controller();
        controller.addListener(listener);
        try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
        controller.removeListener(listener);
	}

}
