package com.vivianhonghoa.chat.client;

import com.vivianhonghoa.chat.shared.DatagramSocketHelper;
import com.vivianhonghoa.chat.shared.RegistreCommandes;

import java.net.DatagramSocket;
import java.util.Scanner;

/**
 * Classe qui écoute les entrées de l'utilisateur dans la console et envoie les messages au serveur.
 * Elle utilise un Consumer<String> pour envoyer les messages, ce qui permet de séparer
 * la logique d'écoute de la console de la logique d'envoi des messages.
 */
public class EcouteurConsole {
    private final DatagramSocket socket;
    private final String addressServeur;
    private final int portServeur;

    public EcouteurConsole(DatagramSocket socket, String addressServeur, int portServeur) {
        this.socket = socket;
        this.addressServeur = addressServeur;
        this.portServeur = portServeur;
    }

    public void ecouter(){
        Scanner scanner = new Scanner(System.in);
        while (!socket.isClosed()) {
            System.out.println("Entrez un message ('exit' pour quitter) : ");
            String ligne = scanner.nextLine();
            // Si l'utilisateur tape "exit", envoie EXIT et ferme la socket
            if (ligne.equalsIgnoreCase("exit")) {
                DatagramSocketHelper.envoyerMessage(RegistreCommandes.EXIT.format(), socket, addressServeur, portServeur);
                socket.close();
                System.out.println("Déconnecté du chat.");
            }else {
                DatagramSocketHelper.envoyerMessage(ligne, socket, addressServeur, portServeur);
            }
        }
        scanner.close();
    }
}
