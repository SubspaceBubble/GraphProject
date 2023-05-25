package me.cometkaizo.graph.tool;

import me.cometkaizo.graph.GraphState;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class DrawTool extends AbstractTool {
    protected DrawTool(GraphState graphState) {
        super(graphState);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClicked(e.getPoint());
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            graphState.setSelectedShape(graphState.shapes.size());
            rightClicked(e.getPoint());
        }
    }

    public abstract void leftClicked(Point point);
    public abstract void rightClicked(Point point);
}
