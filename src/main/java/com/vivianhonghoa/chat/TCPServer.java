package com.vivianhonghoa.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private final int port;
    
    public TCPServer(int port) {
        this.port = port;
    }
    
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client : @" + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                //New thread to handle the client connection
                new Thread(() -> {
                    try(
                        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())
                    ){
                        String message = (String) inputStream.readObject();
                        System.out.println("Received from client: " + message);
                        String response = "200 server OK";
                        outputStream.writeObject(response);
                    } catch (ClassNotFoundException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }
    }
}
