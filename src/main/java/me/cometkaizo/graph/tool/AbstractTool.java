package me.cometkaizo.graph.tool;

import me.cometkaizo.graph.shape.GraphShape;
import me.cometkaizo.graph.GraphState;
import me.cometkaizo.util.GraphUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class AbstractTool implements Tool {

    protected final GraphState graphState;
    protected Point mousePos;
    protected Point prevMousePos;

    protected AbstractTool(GraphState graphState) {
        this.graphState = graphState;
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            graphState.clearShapes();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            graphState.setSelectedShape(-1);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        prevMousePos = mousePos;
        mousePos = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        prevMousePos = mousePos;
        mousePos = e.getPoint();
        updateHoveredShape(e);
    }

    protected void updateHoveredShape(MouseEvent e) {
        int mouseX = GraphUtils.toCoordinateX(e.getPoint().x, graphState.width / 2);
        int mouseY = GraphUtils.toCoordinateY(e.getPoint().y, graphState.height / 2);
        List<GraphShape<?>> shapes = graphState.shapes;

        for (int index = 0; index < shapes.size(); index++) {
            GraphShape<?> shape = shapes.get(index);

            if (shape.getSelectionBounds().contains(mouseX, mouseY)) {
                graphState.setHoveredShape(index);
                return;
            }
        }
        graphState.setHoveredShape(-1);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void paint(Graphics2D graphics) {

    }

    @Override
    public String getName() {
        return getClass().getSimpleName()
                .replaceAll("(?<=.)" + Tool.class.getSimpleName() + "$", "")
                .toLowerCase();
    }

    @Override
    public String toPrettyString() {
        return getClass().getSimpleName()
                .replaceAll("(?<=.)" + Tool.class.getSimpleName() + "$", "")
                .replaceAll("(?<=.)([A-Z])", " $1");
    }
}
