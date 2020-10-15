package com.acme.networkinglibrary;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPThread implements Runnable {
    private String connectionIP;
    private int connectionPort = 7777;
    private boolean isLocal = false;

    // Use is local for the midi drum
    public UDPThread(String ip, int port, boolean _islocal) {
        connectionIP = ip;
        connectionPort = port;
        isLocal = _islocal;
    }
    @Override
    public void run() {
        boolean run = true;
        try {
            DatagramSocket udpSocket = new DatagramSocket(connectionPort);
            InetAddress serverAddr = InetAddress.getByName(connectionIP);
            byte[] buf = ("FILES").getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,serverAddr, connectionPort);
            udpSocket.send(packet);


            while (run) {
                try {
                    byte[] message = new byte[8000];
                    DatagramPacket packet2 = new DatagramPacket(message,message.length);
                    Log.i("UDP client: ", "about to wait to receive");
//                    udpSocket.setSoTimeout(100000);
                    udpSocket.receive(packet2);
                    String text = new String(message, 0, packet.getLength());
                    Log.d("Received text", text);
                } catch (IOException e) {
                    Log.e(" UDP client has IOExcep", "error: ", e);
                    run = false;
                    udpSocket.close();
                }
            }
        } catch (SocketException | UnknownHostException e) {
            Log.e("Socket Open:", "Error:", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
