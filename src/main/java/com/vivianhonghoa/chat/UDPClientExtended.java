package com.vivianhonghoa.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClientExtended extends UDPClient {

    public UDPClientExtended(String serverAddress, int serverPort) {
        super(serverAddress, serverPort);
    }

    public void listenMessage(){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Message to send ('exit' to quit): ");
            String message = scanner.nextLine();
            try {
                sendMessage(message);
                if(message.equalsIgnoreCase("exit")) {
                    System.out.println("Client exiting...");
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
