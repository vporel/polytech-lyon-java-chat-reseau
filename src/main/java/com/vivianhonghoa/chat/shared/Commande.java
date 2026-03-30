package com.vivianhonghoa.chat.shared;

public record Commande(
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
        if(parts.length != parametersCount+1) return false;
        return parts[0].equals(name);
    }

    public String[] extractParameters(String commandString) {
        if(!matches(commandString)) {
            throw new IllegalArgumentException("Command string does not match this command");
        }
        String[] parts = commandString.split(":");
        return parts.length > 1 ? parts[1].split(" ") : new String[0];
    }
}
