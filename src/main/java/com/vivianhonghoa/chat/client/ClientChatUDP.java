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
    private final String adresseServeur;
    private final DatagramSocket socket;
    private int portServeurDedie;

    public ClientChatUDP(String pseudo, String adresseServeur) throws SocketException {
        this.pseudo = pseudo;
        this.adresseServeur = adresseServeur;
        this.socket = new DatagramSocket();
    }

    public void connecter(int serverPort) throws IOException {
        // Envoie JOIN:<pseudo> au serveur sur le port principal
        String joinMessage = ToServeurRegistreCommandes.JOIN.format(pseudo);
        envoyerMessage(joinMessage, serverPort);

        // Attend la réponse PORT:<n> et retient le port dédié
        String response = attendreMessage();
        if (!ToClientRegistreCommandes.PORT.matches(response)) {
            throw new IOException("Réponse inattendue du serveur : " + response);
        }
        portServeurDedie = Integer.parseInt(
                ToClientRegistreCommandes.PORT.extractParameters(response)[0]);
        System.out.println("Connecté au serveur. Port dédié : " + portServeurDedie);
        ecouterServeur();
        ecouterUtilisateur();

    }

    /**
     * Démarre un thread d'écoute qui reçoit et affiche les messages
     */
    private void ecouterServeur(){
        new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    String msg = attendreMessage();
                    System.out.println(msg);
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("Erreur de réception : " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Lit en boucle les messages saisis au clavier et les envoie sur le port dédié
     */
    private void ecouterUtilisateur() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Entrez un message ('exit' pour quitter) : ");
            String ligne = scanner.nextLine();
            // Si l'utilisateur tape "exit", envoie EXIT et ferme la socket
            if (ligne.equalsIgnoreCase("exit")) {
                envoyerMessage(ToServeurRegistreCommandes.EXIT.format());
                socket.close();
                System.out.println("Déconnecté du chat.");
                break;
            }

            envoyerMessage(ligne);
        }
        scanner.close();
    }

    /**
     * Envoie un message au serveur sur le port dédié. S'assurer que le client est connecté avant d'envoyer.
     */
    private void envoyerMessage(String message) throws IOException {
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
