package com.vivianhonghoa.chat.server;

import com.vivianhonghoa.chat.shared.ToServerCommandRegistry;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPChatServer {
    private final int port;

    public UDPChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP Server started on port " + port);
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                if(packet.getLength() > 0) {
                    String message = new String(packet.getData(), 0, packet.getLength());
                    if(ToServerCommandRegistry.JOIN.matches())
                    System.out.println("New client : @" + packet.getAddress() + ":" + packet.getPort());
                    System.out.println("Received from client: " + message);
                    //Respond to the client
                    String response = "200 server OK";
                    byte[] responseData = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
                    try {
                        socket.send(responsePacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
