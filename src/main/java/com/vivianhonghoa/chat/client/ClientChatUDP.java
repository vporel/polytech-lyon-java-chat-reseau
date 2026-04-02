package com.vivianhonghoa.chat.client;

import com.vivianhonghoa.chat.shared.DatagramSocketHelper;
import com.vivianhonghoa.chat.shared.RegistreCommandes;

import java.io.IOException;
import java.net.DatagramSocket;
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

    public void connecter(int portServeur) throws IOException {
        // Envoie JOIN:<pseudo> au serveur sur le port principal
        String joinMessage = RegistreCommandes.JOIN.format(pseudo);
        DatagramSocketHelper.envoyerMessage(joinMessage, socket, adresseServeur, portServeur);

        // Attend la réponse PORT:<n> et retient le port dédié
        String response = DatagramSocketHelper.attendreMessage(socket).message();
        if (!RegistreCommandes.PORT.matches(response)) {
            throw new IOException("Réponse inattendue du serveur : " + response);
        }
        portServeurDedie = Integer.parseInt(
                RegistreCommandes.PORT.extractParameters(response)[0]);
        System.out.println("Connecté au serveur. Port dédié : " + portServeurDedie);
        ecouter();
    }

    /**
     * Lance l'écoute des messages du serveur dans un thread séparé
     * et l'écoute de la console dans le thread principal.
     */
    private void ecouter(){
        new EcouteurServeur(socket).start();
        new EcouteurConsole(socket, adresseServeur, portServeurDedie).ecouter();
    }
}
