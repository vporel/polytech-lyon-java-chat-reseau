package com.vivianhonghoa.chat;

public class UDPScanner {

    /**
     * Scans UDP ports in the specified range to check if they are available.
     * @param portMin minimum port number to scan (inclusive)
     * @param portMax maximum port number to scan (inclusive)
     */
    public static void scanUDPPorts(int portMin, int portMax) {
        // We attempt to bind a DatagramSocket to each port. If it succeeds, the port is available.
        for (int port = portMin; port <= portMax; port++) {
            try {
                new java.net.DatagramSocket(port).close();
            } catch (java.net.SocketException e) {
                System.out.println("UDP Port " + port + " is not available.");
            }
        }
    }
}
