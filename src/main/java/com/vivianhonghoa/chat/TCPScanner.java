package com.vivianhonghoa.chat;

import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPScanner {

    /**
     * Scans TCP ports in the specified range to check if they are available.
     * @param portMin minimum port number to scan (inclusive)
     * @param portMax maximum port number to scan (inclusive)
     */
    public static void scanTCPPorts(int portMin, int portMax) {
        // We attempt to connect to each port. If the connection is successful, the port is not available.
        // If it fails, the port is likely available.
        for (int port = portMin; port <= portMax; port++) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", port), 200);
                System.out.println("TCP Port " + port + " is not available.");
            } catch (Exception e) {
                // Port is available or connection failed
            }
        }
    }
}
