package com.vivianhonghoa.chat.shared;

public record Command(
        String name,
        int parametersCount
) {
    public String format(String... parameters) {
        if (parameters.length != parametersCount) {
            throw new IllegalArgumentException("Expected " + parametersCount + " parameters, but got " + parameters.length);
        }
        return name + ":" + String.join(" ", parameters);
    }

    public boolean matches(String commandString) {
        String[] parts = commandString.split(":");
        if(parts.length != 2) return false;
        return parts[0].equals(name);
    }
}
