package me.cometkaizo.graph.tool;

import me.cometkaizo.Main;
import me.cometkaizo.graph.GraphState;
import me.cometkaizo.shortcut.KeyShortcut;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SelectTool extends AbstractTool {

    public SelectTool(GraphState graphState) {
        super(graphState);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        graphState.setSelectedShape(graphState.hoveredShape);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (graphState.hasShape(graphState.selectedShape)) {
                graphState.getSelectedShape().clearTransforms();
            } else {
                graphState.clearShapes();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            graphState.setSelectedShape(-1);
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (graphState.hasShape(graphState.selectedShape)) {
                graphState.shapes.remove(graphState.selectedShape);
                graphState.setSelectedShape(-1);
                graphState.updateHoverBoxPainter();
            }
        }
    }

    public static class Shortcut extends KeyShortcut {
        public Shortcut(GraphState graphState) {
            super(() -> setTool(graphState), KeyEvent.VK_V);
        }

        private static void setTool(GraphState graphState) {
            String oldToolName = graphState.tool.toPrettyString();
            boolean success = graphState.setTool(SelectTool.class);
            String newToolName = graphState.tool.toPrettyString();
            if (success) {
                Main.log("Changed tool from '" + oldToolName + "' to '" + newToolName + "'");
            } else {
                Main.log("Unavailable tool of type '" + SelectTool.class.getSimpleName() + "'");
            }
        }
    }
}
