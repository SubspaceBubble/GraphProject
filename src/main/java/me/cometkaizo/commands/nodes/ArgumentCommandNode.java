package me.cometkaizo.commands.nodes;

import me.cometkaizo.commands.arguments.Argument;

class ArgumentCommandNode extends CommandNode {

    private final Argument argument;

    public ArgumentCommandNode(ArgumentCommandNodeBuilder builder) {
        super(builder);
        this.argument = builder.argument;
    }

    @Override
    protected boolean accepts(String arg) {
        //LogUtils.debug("arg: {}, argument: {}, equals? {}", arg, argument, argument.accepts(arg));
        return argument.accepts(arg);
    }

    @Override
    protected void executeFunctionality() {
        context.parsedArgs.put(argument.getName(), argument.translate(context.args[level]));
    }

    @Override
    public String toString() {
        return "ArgumentCommandNode{" +
                "argument=" + argument +
                '}';
    }

    @Override
    public String toPrettyString() {
        return argument.toPrettyString() + " ARGUMENT";
    }
}
