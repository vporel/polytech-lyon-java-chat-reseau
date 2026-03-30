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
    private final String adresseServeur;
    private final DatagramSocket socket;
    private int portServeurDedie;

    public ClientChatUDP(String pseudo, String adresseServeur) throws SocketException {
        this.pseudo = pseudo;
        this.adresseServeur = adresseServeur;
        this.socket = new DatagramSocket();
    }

    public void connecter(int serverPort) throws IOException {
        String joinMessage = ToServeurRegistreCommandes.JOIN.format(pseudo);
        envoyerMessage(joinMessage, serverPort);
        //Wait for response
        String response = attendreMessage();
        if(ToClientRegistreCommandes.PORT.matches(response)) {
            this.portServeurDedie = Integer.parseInt(ToClientRegistreCommandes.PORT.extractParameters(response)[0]);
            System.out.println("Connecté au server. Port dédié : " + this.portServeurDedie);
            ecouter();
        }else{
            throw new IOException("Echec de la connexion au serveur : " + response);
        }
    }

    private void ecouter() throws IOException {
        while(true) {
            String message = attendreMessage();
            System.out.println(message);
        }
    }

    public void envoyerMessage(String message) throws IOException {
        if(portServeurDedie == 0){
            throw new IllegalStateException("Client non connecté au serveur. Veuillez appeler connect() avant d'envoyer des messages.");
        }
        envoyerMessage(message, portServeurDedie);
    }

    private void envoyerMessage(String message, int portServeur) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(adresseServeur), portServeur);
        socket.send(packet);
    }

    private String attendreMessage() throws IOException {
        byte[] responseBuffer = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
        socket.receive(responsePacket);
        return new String(responsePacket.getData(), 0, responsePacket.getLength());
    }
}
