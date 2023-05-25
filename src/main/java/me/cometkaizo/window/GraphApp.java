package me.cometkaizo.window;

import me.cometkaizo.Main;
import me.cometkaizo.commands.CommandGroup;
import me.cometkaizo.commands.CommandSyntaxException;
import me.cometkaizo.graph.GraphListener;
import me.cometkaizo.graph.GraphPainter;
import me.cometkaizo.graph.GraphState;
import me.cometkaizo.graph.command.ToolCommand;
import me.cometkaizo.graph.tool.*;
import me.cometkaizo.shortcut.KeyShortcut;
import me.cometkaizo.system.app.App;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphApp extends App {

    private final GraphAppSettings settings;
    private final GraphState graphState;
    private final CommandGroup commandGroup;
    private final List<KeyShortcut> shortcuts;
    private JFrame frame;

    protected GraphApp(GraphAppSettings settings) {
        super(settings);
        this.settings = settings;
        this.graphState = new GraphState(settings);

        this.commandGroup = new CommandGroup(
                () -> new ToolCommand(graphState)
        );
        this.shortcuts = List.of(
                new SelectTool.Shortcut(graphState),
                new MirrorTool.Shortcut(graphState),
                new RotateTool.Shortcut(graphState),
                new TranslateTool.Shortcut(graphState),
                new DilateTool.Shortcut(graphState),
                new PolyLineTool.Shortcut(graphState),
                new RectangleTool.Shortcut(graphState),
                new EllipseTool.Shortcut(graphState)
        );
    }
    protected GraphApp() {
        this(new GraphAppSettings());
    }


    public void parseInput(String input) {
        try {
            commandGroup.execute(input);
        } catch (CommandSyntaxException e) {
            Main.log(e.getMessage());
        }
    }

    @Override
    public void setup() {
        frame = new JFrame(settings.name);
        GraphPanel graphPanel = new GraphPanel(new Dimension(settings.defaultWidth, settings.defaultHeight),
                new GraphPainter(graphState, settings.defaultAxisOptions, settings.xAxisNumberOptions, settings.yAxisNumberOptions, settings.axisNumberInterval));

        GraphListener graphListener = new GraphListener(graphState);
        graphPanel.addComponentListener(graphListener);
        graphPanel.addMouseListener(graphListener);
        graphPanel.addMouseMotionListener(graphListener);

        frame.addKeyListener(graphListener);
        for (KeyShortcut shortcut : shortcuts) {
            frame.addKeyListener(shortcut);
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphPanel);
        frame.setBackground(Color.BLACK);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void tick() {
        super.tick();
        frame.repaint();
    }

    @Override
    public GraphAppSettings getSettings() {
        return settings;
    }

    public GraphState getState() {
        return graphState;
    }
}
