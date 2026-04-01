package com.vivianhonghoa.chat.shared;

public class Commande {
    private final String name;
    private final int parametersCount;
    private final String parameterSeparator;

    public Commande(String name, int parametersCount) {
        this(name, parametersCount, ":");
    }

    public Commande(String name, int parametersCount, String parameterSeparator) {
        this.name = name;
        this.parametersCount = parametersCount;
        this.parameterSeparator = parameterSeparator;
    }

    public String format(String... parameters) {
        if (parameters.length != parametersCount) {
            throw new IllegalArgumentException("Expected " + parametersCount + " parameters, but got " + parameters.length);
        }
        if(parametersCount == 0) {
            return name;
        }
        return name + parameterSeparator + String.join(parameterSeparator, parameters);
    }

    public boolean matches(String commandString) {
        String[] parts = commandString.split(parameterSeparator, parametersCount+1);
        if(parts.length != parametersCount+1) return false;
        return parts[0].equals(name);
    }

    public String[] extractParameters(String commandString) {
        if(!matches(commandString)) {
            throw new IllegalArgumentException("Command string does not match this command");
        }
        String[] parts = commandString.split(parameterSeparator, 2);
        return parts.length > 1 ? parts[1].split(parameterSeparator, parametersCount) : new String[0];
    }
}
