package com.vivianhonghoa.chat.server;

public record ClientInfo(
        String pseudo,
        String ipAddress,
        int port
) {
}
