package me.cometkaizo.commands;

import java.util.List;

public class UnknownCommandException extends CommandSyntaxException {
    public UnknownCommandException(String commandName, List<List<String>> names) {
        this("Command '" + commandName + "' is not in this command group: \n" + names);
    }
    public UnknownCommandException(String message) {
        super(message);
    }

}
