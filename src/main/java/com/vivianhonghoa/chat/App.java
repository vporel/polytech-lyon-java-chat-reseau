package com.vivianhonghoa.chat;


import com.vivianhonghoa.chat.client.ClientChatUDP;
import com.vivianhonghoa.chat.server.ServeurChatUDP;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length == 0){
            System.out.println("At least one argument is required: 'serveur' or 'client'");
            return;
        }
        String mode = args[0];
        if(mode.equalsIgnoreCase("serveur")) {
            if (args.length < 3) {
                System.out.println("Please provide a port number for the server (e.g: --port 7000)");
                return;
            }
            String portArg = args[1];
            if (!portArg.equalsIgnoreCase("--port")) {
                System.out.println("Invalid argument: " + portArg + ". Expected '--port'");
                return;
            }
            int port = Integer.parseInt(args[2]);
            startServer(port);
        }else if(mode.equalsIgnoreCase("client")) {
            if(args.length < 3) {
                System.out.println("Please provide the server address and port for the client (e.g: --serveur localhost:7000)");
                return;
            }
            String serverArg = args[1];
            if (!serverArg.equalsIgnoreCase("--serveur")) {
                System.out.println("Invalid argument: " + serverArg + ". Expected '--serveur'");
                return;
            }
            String serverInfo = args[2];
            String[] serverParts = serverInfo.split(":");
            if(serverParts.length != 2) {
                System.out.println("Invalid server address format: " + serverInfo + ". Expected 'address:port'");
                return;
            }
            String serverAddress = serverParts[0];
            int serverPort = Integer.parseInt(serverParts[1]);
            startClient(serverAddress, serverPort);
        }else{
            System.out.println("Invalid mode: " + mode + ". Expected 'serveur' or 'client'");
        }
    }

    public static void startServer(int serverPort) {
        try {
            ServeurChatUDP server = new ServeurChatUDP(serverPort);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startClient(String serverAddress, int serverPort) throws IOException {
        //Ask the user for a pseudo
        System.out.println("Entrez votre pseudo: ");
        String pseudo = new Scanner(System.in).nextLine();
        ClientChatUDP client = new ClientChatUDP(pseudo, serverAddress);
        client.connect(serverPort);
    }

}