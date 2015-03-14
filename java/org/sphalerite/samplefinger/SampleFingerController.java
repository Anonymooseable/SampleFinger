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
	
	private final double CONTACT_THRESHOLD = 30.0;
	
	private boolean areTouched(Finger a, Finger b) {
		return a.tipPosition().distanceTo(b.tipPosition()) < CONTACT_THRESHOLD;
	}
	
	public void onFrame(Controller c) {
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
				processContact(rightFinger, leftFinger);
			}
			if (!locked[fingerIndex]) {
				messenger.send("scrub " + fingerIndex + " " + rightFinger.tipPosition().getY());
			}
		}
	}

	private int[][] contactFrameCount = new int[5][5];
	private final int IGNORE_CONTACT_THRESHOLD = 2;
	private void processContact(Finger rightFinger, Finger leftFinger) {
		int rightIndex = rightFinger.type().ordinal(),
		    leftIndex = leftFinger.type().ordinal();
		if (areTouched(rightFinger, leftFinger)) {
			contactFrameCount[rightIndex][leftIndex]++;
		} else {
			if (contactFrameCount[rightIndex][leftIndex] > IGNORE_CONTACT_THRESHOLD) {
				switch(leftFinger.type()) {
				case TYPE_THUMB: record(rightIndex); break;
				case TYPE_INDEX: nextMode(rightIndex); break;
				case TYPE_MIDDLE: toggleLock(rightIndex); break;
				default: break;
				}
			}
			contactFrameCount[rightIndex][leftIndex] = 0;
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
