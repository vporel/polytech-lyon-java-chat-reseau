package com.vivianhonghoa.chat.shared;

public class RegistreCommandes {
    /**
     * Commandes des clients vers le serveur
     */
    public static final Commande JOIN = new Commande("JOIN", 1);
    public static final Commande EXIT = new Commande("EXIT", 0);
    public static final Commande LISTE = new Commande("/liste", 0);
    public static final Commande PRIVATE_MESSAGE = new Commande("/mp", 2, " ");

    /**
     * Commandes du serveur vers les clients
     */
    public static final Commande PORT = new Commande("PORT", 1);
    public static final Commande TIMEOUT = new Commande("TIMEOUT", 0);
}
