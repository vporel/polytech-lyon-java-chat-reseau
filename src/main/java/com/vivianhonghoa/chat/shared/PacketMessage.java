package com.vivianhonghoa.chat.shared;

import java.net.DatagramPacket;

public record PacketMessage(String message, DatagramPacket packet) {
}
