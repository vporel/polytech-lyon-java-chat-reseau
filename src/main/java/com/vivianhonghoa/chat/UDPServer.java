package com.vivianhonghoa.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer {
    private final int port;

    public UDPServer(int port) {
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
                    new Thread(() -> {
                        System.out.println("New client : @" + packet.getAddress() + ":" + packet.getPort());
                        String message = new String(packet.getData(), 0, packet.getLength());
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
                    }).start();
                }
            }
        }
    }
}
