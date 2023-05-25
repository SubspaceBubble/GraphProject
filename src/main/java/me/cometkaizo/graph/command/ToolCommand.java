package me.cometkaizo.graph.command;

import me.cometkaizo.commands.arguments.StringArgument;
import me.cometkaizo.commands.nodes.ArgumentCommandNodeBuilder;
import me.cometkaizo.commands.nodes.Command;
import me.cometkaizo.commands.nodes.LiteralCommandNodeBuilder;
import me.cometkaizo.graph.GraphState;

import java.util.List;

public class ToolCommand extends Command {
    public static final List<String> names = List.of("tool");
    private final GraphState graphState;

    public ToolCommand(GraphState graphState) {
        this.graphState = graphState;

        rootNode.then(new LiteralCommandNodeBuilder("set"))
                .then(new ArgumentCommandNodeBuilder(new StringArgument("toolName")))
                .executes(this::setTool);
    }

    private void setTool() {
        String toolName = (String) parsedArgs.get("toolName");
        String prevToolName = graphState.tool == null ? null : graphState.tool.toPrettyString();

        boolean success = graphState.setTool(toolName);
        if (success) {
            log("Changed tool from '" + prevToolName + "' to '" + graphState.tool.toPrettyString() + "'");
        } else {
            log("Unavailable tool '" + toolName + "'");
        }
    }

    @Override
    public List<String> getNames() {
        return names;
    }
}
