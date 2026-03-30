package com.vivianhonghoa.chat;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {
    private final String serverAddress;
    private final int serverPort;
    
    public TCPClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    
    public void sendMessage(String message) throws IOException, ClassNotFoundException {
        InetSocketAddress socketAddress = new InetSocketAddress(serverAddress, serverPort);
        try (Socket socket = new Socket()) {
            socket.connect(socketAddress, 5000); // Connect with a timeout of 5 seconds
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(message);
            // Wait for response
            String response = (String) inputStream.readObject();
            System.out.println("Response from server: " + response);
            outputStream.close();
            inputStream.close();
        }
    }
}
