package me.cometkaizo.graph;

import java.awt.event.*;

public class GraphListener implements ComponentListener, MouseListener, MouseMotionListener, KeyListener {
    private final GraphState graphState;

    public GraphListener(GraphState graphState) {
        this.graphState = graphState;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        graphState.prevWidth = graphState.width;
        graphState.prevHeight = graphState.height;

        graphState.width = e.getComponent().getWidth();
        graphState.height = e.getComponent().getHeight();

        graphState.sizeChanged = graphState.width != graphState.prevWidth ||
                graphState.height != graphState.prevHeight;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        graphState.tool.mouseReleased(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        graphState.tool.keyReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        graphState.tool.mouseMoved(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        graphState.tool.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        graphState.tool.mousePressed(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        graphState.tool.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        graphState.tool.mouseExited(e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        graphState.tool.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        graphState.tool.keyPressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        graphState.tool.mouseDragged(e);
    }
}
