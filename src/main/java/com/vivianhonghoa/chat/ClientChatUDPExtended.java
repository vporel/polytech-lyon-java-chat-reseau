package com.vivianhonghoa.chat;

import com.vivianhonghoa.chat.client.ClientChatUDP;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class ClientChatUDPExtended extends ClientChatUDP {

    public ClientChatUDPExtended(String pseudo, String serverAddress) throws SocketException {
        super(pseudo, serverAddress);
    }

    public void listenMessage(){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Message to send ('exit' to quit): ");
            String message = scanner.nextLine();
            try {
                envoyerMessage(message);
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
