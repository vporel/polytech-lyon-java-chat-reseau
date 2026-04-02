package com.vivianhonghoa.chat.client;

import com.vivianhonghoa.chat.shared.DatagramSocketHelper;
import com.vivianhonghoa.chat.shared.RegistreCommandes;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Thread qui reçoit les messages du serveur et les affiche à l'utilisateur.
 * Il gère aussi les messages de timeout pour fermer la connexion.
 */
public class EcouteurServeur extends Thread {
    private final DatagramSocket socket;

    public EcouteurServeur(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                String message = DatagramSocketHelper.attendreMessage(socket).message();
                if(RegistreCommandes.TIMEOUT.matches(message)){
                    System.out.println("Déconnecté du serveur pour inactivité.");
                    socket.close();
                }else {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.err.println("Erreur de réception : " + e.getMessage());
            }
        }
    }
}
