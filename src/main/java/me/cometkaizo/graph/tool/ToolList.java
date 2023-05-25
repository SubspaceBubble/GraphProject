package me.cometkaizo.graph.tool;

import java.util.HashMap;
import java.util.Map;

public class ToolList {
    private final Map<String, Tool> tools;

    public ToolList(Tool... tools) {
        this.tools = new HashMap<>(tools.length);

        for (Tool tool : tools) {
            add(tool);
        }
    }

    public Tool get(String key) {
        return tools.get(key);
    }

    public Tool get(Class<? extends Tool> type) {
        for (Tool tool : tools.values()) {
            if (type.equals(tool.getClass())) return tool;
        }
        return null;
    }

    public Tool getOfType(Class<? extends Tool> type) {
        for (Tool tool : tools.values()) {
            if (type.isAssignableFrom(tool.getClass())) return tool;
        }
        return null;
    }

    public void add(Tool tool) {
        tools.put(tool.getName(), tool);
    }
}
