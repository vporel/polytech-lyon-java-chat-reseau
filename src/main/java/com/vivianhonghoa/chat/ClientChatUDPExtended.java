package com.vivianhonghoa.chat;

import com.vivianhonghoa.chat.client.ClientChatUDP;

import java.io.IOException;

public class ClientChatUDPExtended extends ClientChatUDP {

    public ClientChatUDPExtended(String pseudo, String serverAddress) throws IOException {
        super(pseudo, serverAddress);
    }
}
