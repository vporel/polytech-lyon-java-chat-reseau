package com.vivianhonghoa.chat.server;

import com.vivianhonghoa.chat.shared.ToClientRegistreCommandes;
import com.vivianhonghoa.chat.shared.ToServeurRegistreCommandes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

public class ServeurChatUDP {
    private final int port;
    private final List<GestionnaireClient> gestionnairesClients;

    public ServeurChatUDP(int port) {
        this.port = port;
        this.gestionnairesClients = new java.util.concurrent.CopyOnWriteArrayList<>();
    }

    public void start() throws IOException {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP Server started on port " + port);
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                if(packet.getLength() > 0) {
                    handlePacket(socket, packet);
                }
            }
        }
    }

    private void handlePacket(DatagramSocket socket, DatagramPacket packet) throws SocketException {
        String message = new String(packet.getData(), 0, packet.getLength());
        String response;
        if(ToServeurRegistreCommandes.JOIN.matches(message)){
            System.out.println("New client : @" + packet.getAddress() + ":" + packet.getPort());
            DatagramSocket clientSocket = new DatagramSocket();
            response = ToClientRegistreCommandes.PORT.format(String.valueOf(clientSocket.getLocalPort()));
            String pseudo = ToServeurRegistreCommandes.JOIN.extractParameters(message)[0];
            GestionnaireClient manager = new GestionnaireClient(
                    new ClientInfo(pseudo, packet.getAddress().getHostAddress(), packet.getPort()),
                    clientSocket,
                    this::broadcastMessage
            );
            this.broadcastMessage(ToClientRegistreCommandes.NEW_CLIENT.format(pseudo));
            this.gestionnairesClients.add(manager);
            new Thread(manager).start();
        }else{
            response = "Unknown command: " + message;
        }
        //Respond to the client
        byte[] responseData = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
        try {
            socket.send(responsePacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcastMessage(String message){
        for(GestionnaireClient manager : gestionnairesClients){
            manager.sendMessage(message);
        }
    }
}
