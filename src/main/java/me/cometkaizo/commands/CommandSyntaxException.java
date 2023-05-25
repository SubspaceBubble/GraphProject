package me.cometkaizo.commands;

public class CommandSyntaxException extends RuntimeException {
    public CommandSyntaxException(String message) {
        super(message);
    }

}
