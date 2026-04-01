package com.vivianhonghoa.chat.shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DatagramSocketHelper {
    public static void envoyerMessage(String message, DatagramSocket senderSocket, String receiverHostName, int receiverPort) {
        byte[] buffer = message.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(receiverHostName), receiverPort);
            senderSocket.send(packet);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PacketMessage attendreMessage(DatagramSocket socket) throws IOException {
        byte[] responseBuffer = new byte[1024];
        DatagramPacket responsePacket = null;
        while(responsePacket == null || responsePacket.getLength() == 0) {
            responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);
        }
        String message = new String(responsePacket.getData(), 0, responsePacket.getLength());
        return new PacketMessage(message, responsePacket);
    }

}
