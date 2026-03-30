package com.vivianhonghoa.chat.client;

import com.vivianhonghoa.chat.shared.ToClientRegistreCommandes;
import com.vivianhonghoa.chat.shared.ToServeurRegistreCommandes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

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
        // Envoie JOIN:<pseudo> au serveur sur le port principal
        String joinMessage = ToServeurRegistreCommandes.JOIN.format(pseudo);
        sendTo(joinMessage, serverPort);

        // Attend la réponse PORT:<n> et retient le port dédié
        String response = receiveMessage();
        if (!ToClientRegistreCommandes.PORT.matches(response)) {
            throw new IOException("Réponse inattendue du serveur : " + response);
        }
        this.dedicatedServerPort = Integer.parseInt(
                ToClientRegistreCommandes.PORT.extractParameters(response)[0]);
        System.out.println("Connecté au serveur. Port dédié : " + dedicatedServerPort);

        // Démarre un thread d'écoute qui reçoit et affiche les messages
        Thread ecouteThread = new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    String msg = receiveMessage();
                    System.out.println(msg);
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("Erreur de réception : " + e.getMessage());
                }
            }
        });
        ecouteThread.setDaemon(true);
        ecouteThread.start();

        // Lit en boucle les messages saisis au clavier et les envoie sur le port dédié
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String ligne = scanner.nextLine();

            // Si l'utilisateur tape "exit", envoie EXIT et ferme la socket
            if (ligne.equalsIgnoreCase("exit")) {
                sendTo(ToServeurRegistreCommandes.EXIT.format(), dedicatedServerPort);
                socket.close();
                System.out.println("Déconnecté du chat.");
                break;
            }

            sendTo(ligne, dedicatedServerPort);
        }
        scanner.close();
    }

    private void sendTo(String message, int port) throws IOException {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(serverAddress), port);
        socket.send(packet);
    }

    private String receiveMessage() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());
    }

}
