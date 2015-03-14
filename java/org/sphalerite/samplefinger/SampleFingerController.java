package org.sphalerite.samplefinger;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

class SampleFingerController extends Listener {
	private PureDataMessenger messenger;
	
	public SampleFingerController() throws SocketException {
		messenger = new PureDataMessenger();
	}
	
	private boolean areTouched(Vector a, Vector b) {
		return a.distanceTo(b) < 10.0;
	}
	
	public void onFrame(Controller c) {
		ArrayList<FingerWrap> fingers = new ArrayList<FingerWrap>();
		Frame f = c.frame();
		Hand rightHand = null, leftHand = null;
		for (Hand hand : f.hands()) {
			if (hand.isLeft()) {
				leftHand = hand;
			} else {
				rightHand = hand;
			}
		}
		if (rightHand == null) {
			return;
		}
		for (Finger rightFinger : rightHand.fingers()) {
			int fingerIndex = rightFinger.type().ordinal();
			for (Finger leftFinger : leftHand.fingers()) {
				if (areTouched(rightFinger.tipPosition(), leftFinger.tipPosition())) {
					switch(leftFinger.type()) {
					case TYPE_THUMB: record(fingerIndex); break;
					case TYPE_INDEX: nextMode(fingerIndex); break;
					case TYPE_MIDDLE: toggleLock(fingerIndex); break;
					default: break;
					}
				}
			}
			if (!locked[fingerIndex]) {
				messenger.send("scrub " + fingerIndex + " " + rightFinger.tipPosition().getY());
			}
		}
	}

	private boolean locked[] = {true, true, true, true, true};
	private void toggleLock(int fingerIndex) {
		locked[fingerIndex] = !locked[fingerIndex];
	}

	private SampleMode[] sampleModes = {SampleMode.OFF,
										SampleMode.OFF,
										SampleMode.OFF,
										SampleMode.OFF,
										SampleMode.OFF };
	private void nextMode(int fingerIndex) {
		SampleMode next = SampleMode.OFF;
		switch(sampleModes[fingerIndex]) {
		case OFF: next = SampleMode.LOOP;
		case LOOP: next = SampleMode.SCRUB;
		case SCRUB: next = SampleMode.OFF;
		}
		messenger.send("mode " + fingerIndex + " " + next.toString());
		sampleModes[fingerIndex] = next;
	}

	private void record(int fingerIndex) {
		messenger.send("record " + fingerIndex);
	}
	

	public static void main(String[] args) throws SocketException {
        Listener listener = new SampleFingerController();
        Controller controller = new Controller();
        controller.addListener(listener);
        System.out.print("Hello");
        try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
        controller.removeListener(listener);
	}

}
