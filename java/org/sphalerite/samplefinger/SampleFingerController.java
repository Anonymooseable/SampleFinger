package org.sphalerite.samplefinger;

import java.io.IOException;
import java.net.SocketException;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

class SampleFingerController extends Listener {
	private PureDataMessenger messenger;
	
	public SampleFingerController() throws SocketException {
		messenger = new PureDataMessenger();
	}
	
	private final double CONTACT_THRESHOLD = 18.0;
	private final double CROSSING_THRESHOLD = 18.0;
	
	private boolean areTouched(Finger a, Finger b) {
		return a.tipPosition().distanceTo(b.tipPosition()) < CONTACT_THRESHOLD;
	}
	
	private final double HAND_CONTACT_THRESHOLD = 30.0;
	private boolean areTouched(Finger a, Hand b) {
		return a.tipPosition().distanceTo(b.palmPosition()) < HAND_CONTACT_THRESHOLD;
	}
	
	private boolean areCrossed(Hand a, Hand b) {
		return (a.palmPosition().distanceTo(b.palmPosition()) > CROSSING_THRESHOLD && a.palmPosition.getX() > b.palmPosition.getY());
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
			processContact(rightFinger, leftHand);
			for (Finger leftFinger : leftHand.fingers()) {
				processContact(rightFinger, leftFinger);
			}
			if (!locked[fingerIndex]) {
				messenger.send("scrub " + fingerIndex + " " + rightFinger.tipPosition().getY());
			}
		}
		if (areCrossed(leftHand, rightHand)) {
			processCrossing(leftHand, rightHand);
		}
	}
	
	private int crossingFrameCount;
	private void processCrossing(Hand leftHand, Hand rightHand) {
		boolean crossing = areCrosssed(leftHand, righHand); 
		if (crossing) {
			if (crossingFrameCount < 20) crossingFrameCount++;
		} else {
			if (crossingFrameCount > 0) crossingFrameCount--;
			if (crossingFrameCount == 10) {
				messenger.send("reset");
			}
		}
	}

	private int[][] contactFrameCount = new int[5][5];
	private void processContact(Finger rightFinger, Finger leftFinger) {
		int rightIndex = rightFinger.type().ordinal(),
		    leftIndex = leftFinger.type().ordinal();
		boolean touching = areTouched(rightFinger, leftFinger); 
		if (touching) {
			if (contactFrameCount[rightIndex][leftIndex] < 20) contactFrameCount[rightIndex][leftIndex]++;
		} else {
			if (contactFrameCount[rightIndex][leftIndex] > 0) contactFrameCount[rightIndex][leftIndex]--;
			if (contactFrameCount[rightIndex][leftIndex] == 10) {
				switch (leftFinger.type()) {
				case TYPE_INDEX: nextMode(rightIndex); break;
				case TYPE_THUMB: record(rightIndex); break;
				case TYPE_MIDDLE: toggleLock(rightIndex); break;
				default: break;
				}
			}
		}
	}

	private void processContact(Finger rightFinger, Hand hand) {
		int rightIndex = rightFinger.type().ordinal();
		boolean touching = areTouched(rightFinger, hand); 
		if (touching) {
			messenger.send("volume " + rightIndex + " " + hand.palmVelocity().getY());
		}
	}

	private boolean locked[] = {true, true, true, true, true};
	private void toggleLock(int fingerIndex) {
		locked[fingerIndex] = !locked[fingerIndex];
		System.out.println("Lock " + fingerIndex + ": " + locked[fingerIndex]);
	}

	private SampleMode[] sampleModes = {SampleMode.OFF,
										SampleMode.OFF,
										SampleMode.OFF,
										SampleMode.OFF,
										SampleMode.OFF };
	private void nextMode(int fingerIndex) {
		SampleMode next = SampleMode.LOOP;
		switch(sampleModes[fingerIndex]) {
		case OFF: next = SampleMode.LOOP; break;
		case LOOP: next = SampleMode.SCRUB; break;
		case SCRUB: next = SampleMode.OFF; break;
		}
		messenger.send("mode " + fingerIndex + " " + next.toString());
		System.out.println("Sample " + fingerIndex + " in mode " + next);
		sampleModes[fingerIndex] = next;
	}

	private void record(int fingerIndex) {
		messenger.send("record " + fingerIndex);
		System.out.println("Recording sample " + fingerIndex);
	}
	

	public static void main(String[] args) throws SocketException {
        Listener listener = new SampleFingerController();
        Controller controller = new Controller();
        controller.addListener(listener);
        System.out.println("Ready!");
        try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
        controller.removeListener(listener);
	}

}
