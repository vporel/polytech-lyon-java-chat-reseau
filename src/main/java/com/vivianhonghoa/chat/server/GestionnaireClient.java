package com.vivianhonghoa.chat.server;

import com.vivianhonghoa.chat.shared.ToServeurRegistreCommandes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class GestionnaireClient implements Runnable {
    private final ClientInfo clientInfo;
    private final DatagramSocket clientSocket;
    private final ConcurrentMap<String, GestionnaireClient> clientsConnectes;

    public GestionnaireClient(ClientInfo clientInfo, DatagramSocket clientSocket, ConcurrentMap<String, GestionnaireClient> clientsConnectes) {
        this.clientInfo = clientInfo;
        this.clientSocket = clientSocket;
        this.clientsConnectes = clientsConnectes;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(packet);
                if(packet.getLength() > 0) {
                    handlePacket(packet);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handlePacket(DatagramPacket packet) {
        String message = new String(packet.getData(), 0, packet.getLength());
        if(ToServeurRegistreCommandes.EXIT.matches(message)){
            broadcastMessage(String.format("%s a quitté le chat", clientInfo.pseudo()));
            clientSocket.close();
            Thread.currentThread().interrupt();
        }else{
            broadcastMessage(message);
        }
    }

    public void sendMessage(String message) {
        byte[] buffer = message.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(clientInfo.adresseIP()), clientInfo.port());
            clientSocket.send(packet);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcastMessage(String message){
        for(GestionnaireClient manager : clientsConnectes.values()){
            manager.sendMessage(message);
        }
    }
}
