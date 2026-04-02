package com.vivianhonghoa.chat.server;

import com.vivianhonghoa.chat.shared.DatagramSocketHelper;
import com.vivianhonghoa.chat.shared.PacketMessage;
import com.vivianhonghoa.chat.shared.RegistreCommandes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServeurChatUDP {
    private final int port;
    private final ConcurrentMap<String, GestionnaireClient> clientsConnectes;
    private final DatagramSocket socket;

    public ServeurChatUDP(int port) throws SocketException {
        this.port = port;
        this.clientsConnectes = new ConcurrentHashMap<>();
        socket = new DatagramSocket(port);
    }

    public void start() throws IOException {
        System.out.println("UDP Server started on port " + port);
        while (true) {
            PacketMessage packetMessage = DatagramSocketHelper.attendreMessage(socket);
            gererPacket(packetMessage);
        }
    }

    private void gererPacket(PacketMessage input) throws SocketException {
        String response;
        if(RegistreCommandes.JOIN.matches(input.message())){
            System.out.println("Nouveau client : @" + input.packet().getAddress() + ":" + input.packet().getPort());
            DatagramSocket clientSocket = new DatagramSocket();
            response = RegistreCommandes.PORT.format(String.valueOf(clientSocket.getLocalPort()));
            String pseudo = RegistreCommandes.JOIN.extractParameters(input.message())[0];
            GestionnaireClient manager = new GestionnaireClient(
                    new ClientInfo(pseudo, input.packet().getAddress().getHostAddress(), input.packet().getPort()),
                    clientSocket,
                    clientsConnectes
            );
            broadcastMessage(String.format("%s a rejoint le chat", pseudo));
            clientsConnectes.put(pseudo, manager);
            new Thread(manager).start();
        }else{
            response = "Unknown command: " + input.message();
        }
        //Respond to the client
        byte[] responseData = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, input.packet().getAddress(), input.packet().getPort());
        try {
            socket.send(responsePacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcastMessage(String message){
        for(GestionnaireClient manager : clientsConnectes.values()){
            manager.envoyerMessage(message);
        }
    }
}
