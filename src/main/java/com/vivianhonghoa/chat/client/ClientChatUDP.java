package com.vivianhonghoa.chat.client;

import com.vivianhonghoa.chat.shared.ToClientRegistreCommandes;
import com.vivianhonghoa.chat.shared.ToServeurRegistreCommandes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClientChatUDP {
    private final String pseudo;
    private final String serverAddress;
    private final DatagramSocket socket;
    private int dedicatedServerPort;

    public ClientChatUDP(String pseudo, String serverAddress) throws SocketException {
        this.pseudo = pseudo;
        this.serverAddress = serverAddress;
        this.socket = new DatagramSocket();
    }

    public void connect(int serverPort) throws IOException {
        String joinMessage = ToServeurRegistreCommandes.JOIN.format(pseudo);
        sendMessage(joinMessage, serverPort);
        //Wait for response
        String response = waitForResponse();
        if(ToClientRegistreCommandes.PORT.matches(response)) {
            this.dedicatedServerPort = Integer.parseInt(ToClientRegistreCommandes.PORT.extractParameters(response)[0]);
            System.out.println("Connected to server. Dedicated port: " + this.dedicatedServerPort);
            listen();
        }else{
            throw new IOException("Failed to connect to server: " + response);
        }
    }

    private void listen() throws IOException {
        while(true) {
            String message = waitForResponse();
            System.out.println(message);
        }
    }

    public void sendMessage(String message) throws IOException {
        if(dedicatedServerPort == 0){
            throw new IllegalStateException("Client not connected to server. Please join the chat first.");
        }
        sendMessage(message, dedicatedServerPort);
    }

    private void sendMessage(String message, int serverPort) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverAddress), serverPort);
        socket.send(packet);
    }

    private String waitForResponse() throws IOException {
        byte[] responseBuffer = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
        socket.receive(responsePacket);
        return new String(responsePacket.getData(), 0, responsePacket.getLength());
    }
}
