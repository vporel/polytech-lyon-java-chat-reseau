package com.vivianhonghoa.chat.shared;

public class ToServeurRegistreCommandes {
    public static final Commande JOIN = new Commande("JOIN", 1);
    public static final Commande EXIT = new Commande("EXIT", 0);
    public static final Commande LISTE = new Commande("/liste", 0);
    public static final Commande PRIVATE_MESSAGE = new Commande("/mp", 2, " ");
}
