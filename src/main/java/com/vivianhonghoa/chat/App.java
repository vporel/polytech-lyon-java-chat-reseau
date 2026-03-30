package com.vivianhonghoa.chat;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        testUDPCommunicationExtended();
    }

    public static void testUDPCommunication() throws InterruptedException, IOException {
        new Thread(() -> {
            try {
                new UDPServer(9000).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.sleep(1000); // Wait for the server to start
        UDPClient client = new UDPClient("localhost", 9000);
        client.sendMessage("Hello server");
    }

    public static void testUDPCommunicationExtended() throws InterruptedException, IOException {
        new Thread(() -> {
            try {
                new UDPServer(9000).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.sleep(1000); // Wait for the server to start
        UDPClientExtended client = new UDPClientExtended("localhost", 9000);
        client.listenMessage();
    }

    public static void testTCPClientServerCommunication() throws InterruptedException, IOException, ClassNotFoundException {
        new Thread(() -> {
            try {
                new TCPServer(9000).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.sleep(1000); // Wait for the server to start
        TCPClient client = new TCPClient("localhost", 9000);
        client.sendMessage("hello server");
    }
}