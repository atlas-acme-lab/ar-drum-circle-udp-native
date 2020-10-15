package com.acme.networkinglibrary;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDPMidiConnector {
    private String connectionIP;
    private int connectionPort = 7778;
    private boolean connectionRunning = false;
    private DatagramSocket udpSocket;
    private Thread receiveThread;
    private InetAddress serverAddr;
    private boolean isDebug = false;

    private void UnityDebug(String msg) {
        if (isDebug) {
            Log.d("Unity", msg);
        }
    }

    public void SetDebug(boolean state) {
        isDebug = state;
    }

    public void StartConnectionThread(String ip) {
        UnityDebug("making local midi connection to: " + ip);
        connectionIP = ip;

        try {
            udpSocket = new DatagramSocket(connectionPort);
            serverAddr = InetAddress.getByName(connectionIP);

            connectionRunning = true;
            // This is the thread
            receiveThread = new Thread(new Runnable() {
                public void run() {
                    while (connectionRunning) {
                        try {
                            byte[] message = new byte[200];
                            DatagramPacket packet = new DatagramPacket(message,message.length);
                            UnityDebug("about to wait to receive local UDP");
                            // udpSocket.setSoTimeout(100000);
                            udpSocket.receive(packet);
                            String text = new String(message, 0, packet.getLength());

                            // Send this text to UNITY
                            UnityDebug("received text: " + text);
                            UnityPlayer.UnitySendMessage("UDPConnectionManager", "PlayNoteFromLocal", text);
                        } catch (IOException e) {
                            Log.e(" UDP client has IOExcep", "error: ", e);
                            connectionRunning = false;
                            udpSocket.close();
                        }
                    }
                    UnityDebug("Uh Oh -- not reading from local for some reason");
                }
            }); 
            receiveThread.start();
            UnityDebug("Connection to local midi device started");
        } catch (SocketException | UnknownHostException e) {
            Log.e("Socket Open:", "Error:", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendMessage(String message) {
        UnityDebug("making local midi connection to: local");
        byte[] buf = (message).getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, connectionPort);

        try {
            udpSocket.send(packet);
        } catch (SocketException | UnknownHostException e) {
            Log.e("Socket Open:", "Error:", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
