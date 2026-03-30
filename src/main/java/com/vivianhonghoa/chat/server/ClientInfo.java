package com.vivianhonghoa.chat.server;

public record ClientInfo(
        String pseudo,
        String adresseIP,
        int port
) {
}
