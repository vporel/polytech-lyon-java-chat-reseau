package com.vivianhonghoa.chat.server;

import com.vivianhonghoa.chat.shared.DatagramSocketHelper;
import com.vivianhonghoa.chat.shared.PacketMessage;
import com.vivianhonghoa.chat.shared.RegistreCommandes;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GestionnaireClient implements Runnable {
    private static final int TIMEOUT_INACTIVITE = 60; // secondes

    private final ClientInfo clientInfo;
    private final DatagramSocket clientSocket;
    private final ConcurrentMap<String, GestionnaireClient> clientsConnectes;
    private final AtomicInteger compteurInactivite = new AtomicInteger(0);

    public GestionnaireClient(ClientInfo clientInfo, DatagramSocket clientSocket, ConcurrentMap<String, GestionnaireClient> clientsConnectes) {
        this.clientInfo = clientInfo;
        this.clientSocket = clientSocket;
        this.clientsConnectes = clientsConnectes;
        this.lancerTimerInactivite();
    }

    private void lancerTimerInactivite() {
        Runnable compteurInactiviteTask = () -> {
            if(compteurInactivite.incrementAndGet() >= TIMEOUT_INACTIVITE) {
                GestionnaireClient.this.envoyerMessage(RegistreCommandes.TIMEOUT.format());
                System.out.println("Client '" + clientInfo.pseudo() + "' déconnecté pour inactivité.");
                clientSocket.close();
                clientsConnectes.remove(clientInfo.pseudo());
                Thread.currentThread().interrupt();
            }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(compteurInactiviteTask, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            while (true) {
                PacketMessage packetMessage = DatagramSocketHelper.attendreMessage(clientSocket);
                gererPacket(packetMessage);
            }
        } catch (java.net.SocketException e) {
            // Socket fermé (ex: timeout d'inactivité), fin normale du gestionnaire
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void gererPacket(PacketMessage input) {
        // Réinitialise le compteur d'inactivité à chaque message reçu
        compteurInactivite.set(0);
        if(RegistreCommandes.EXIT.matches(input.message())){
            broadcastMessage(String.format("%s a quitté le chat", clientInfo.pseudo()));
            clientSocket.close();
            clientsConnectes.remove(clientInfo.pseudo());
            Thread.currentThread().interrupt();
        }else if(RegistreCommandes.LISTE.matches(input.message())) {
            StringBuilder response = new StringBuilder("Utilisateurs connectés : ");
            for(int i = 0; i < clientsConnectes.size(); i++) {
                response.append(clientsConnectes.keySet().toArray()[i]);
                if (i < clientsConnectes.size() - 1) response.append("; ");
            }
            envoyerMessage(response.toString());
        }else if(RegistreCommandes.PRIVATE_MESSAGE.matches(input.message())) {
            String[] params = RegistreCommandes.PRIVATE_MESSAGE.extractParameters(input.message());
            String destinataire = params[0];
            String msg = params[1];
            //Find the manager with insensitive pseudo
            GestionnaireClient destManager = null;
            for(String pseudo : clientsConnectes.keySet()) {
                if(pseudo.equalsIgnoreCase(destinataire)) {
                    destManager = clientsConnectes.get(pseudo);
                    break;
                }
            }
            if(destManager != null) {
                destManager.envoyerMessage(String.format("[MP de %s]: %s", clientInfo.pseudo(), msg));
            }else{
                envoyerMessage("Utilisateur " + destinataire + " non trouvé.");
            }
        }else{
            broadcastMessage(String.format("%s: %s", clientInfo.pseudo(), input.message()));
        }
    }

    public void envoyerMessage(String message) {
        DatagramSocketHelper.envoyerMessage(message, clientSocket, clientInfo.adresseIP(), clientInfo.port());
    }

    private void broadcastMessage(String message){
        for(GestionnaireClient manager : clientsConnectes.values()){
            if(manager != this)
                manager.envoyerMessage(message);
        }
    }
}
