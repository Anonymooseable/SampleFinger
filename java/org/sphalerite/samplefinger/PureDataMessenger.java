package org.sphalerite.samplefinger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class PureDataMessenger
{
	private DatagramSocket sock;
	private InetAddress addr;
	
	public PureDataMessenger() throws SocketException {
		sock = new DatagramSocket();
		addr = InetAddress.getLoopbackAddress();
	}
  
    public boolean send(String message) {
    	byte[] sendData = (message + "\n").getBytes();
    	DatagramPacket packet = new DatagramPacket(sendData, sendData.length, addr, 9001);
    	try {
    		sock.send(packet);
    	}
    	catch (IOException e) {
    		return false;
    	}
    	return true;
    }
    public static void main (String[] args) throws IOException {
	    PureDataMessenger me = new PureDataMessenger();
	    me.send("Testing from PureDataMessenger.main");
    }
}