package com.vivianhonghoa.chat;


import com.vivianhonghoa.chat.client.ClientChatUDP;
import com.vivianhonghoa.chat.server.ServeurChatUDP;

import java.io.IOException;

public class App {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8500;

    public static void main(String[] args) throws IOException {
        // Start the server in a separate thread
        new Thread(() -> {
            try {
                ServeurChatUDP server = new ServeurChatUDP(SERVER_PORT);
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        //Start a client
        ClientChatUDP client = new ClientChatUDP("Alice", SERVER_ADDRESS);
        client.connect(SERVER_PORT);
    }
}