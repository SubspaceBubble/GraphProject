package me.cometkaizo.graph.tool;

import me.cometkaizo.Main;
import me.cometkaizo.graph.GraphState;
import me.cometkaizo.graph.painter.Painter;
import me.cometkaizo.shortcut.KeyShortcut;
import me.cometkaizo.util.GraphUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class EllipseTool extends DrawTool {
    private Point startPoint;
    private Ellipse2D preview;
    private Painter<? super Shape, ?> painter;
    private Painter<? super Shape, ?> previewPainter;
    private Painter<? super Shape, ?> transformPainter;

    public EllipseTool(GraphState graphState, Painter<? super Shape, ?> painter, Painter<? super Shape, ?> previewPainter, Painter<? super Shape, ?> transformPainter) {
        super(graphState);
        this.painter = painter;
        this.previewPainter = previewPainter;
        this.transformPainter = transformPainter;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        removePreview();
    }

    @Override
    public void leftClicked(Point point) {
        if (startPoint == null) {
            graphState.setSelectedShape(graphState.shapes.size());
            startPoint = GraphUtils.toCoordinate(point, graphState.width / 2, graphState.height / 2);
        } else {
            addEllipse();
            startPoint = null;
        }
    }

    @Override
    public void rightClicked(Point point) {
        removePreview();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            removePreview();
        }
    }

    public void removePreview() {
        startPoint = null;
        preview = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        int originX = graphState.width / 2;
        int originY = graphState.height / 2;

        Point endPoint = GraphUtils.toCoordinate(e.getPoint(), originX, originY);

        updatePreview(endPoint);
    }

    private void addEllipse() {
        Ellipse2D ellipse = new Ellipse2D.Float((float) preview.getX(), (float) preview.getY(), (float) preview.getWidth(), (float) preview.getHeight());

        graphState.addShape(ellipse, painter, transformPainter);

        graphState.updateSelectionPainter();
        graphState.updateHoverBoxPainter();
    }

    public void updatePreview(Point endPoint) {
        if (startPoint == null || endPoint == null) {
            preview = null;
            return;
        }

        if (preview == null) preview = createEllipse(startPoint, endPoint);
        else {
            int width = endPoint.x - startPoint.x;
            int height = endPoint.y - startPoint.y;
            preview.setFrame(
                    startPoint.x + Math.min(width, 0),
                    startPoint.y + Math.min(height, 0),
                    Math.abs(width),
                    Math.abs(height)
            );
        }
    }

    public static Ellipse2D createEllipse(Point startPoint, Point endPoint) {
        int width = endPoint.x - startPoint.x;
        int height = endPoint.y - startPoint.y;
        return new Ellipse2D.Float(
                startPoint.x + Math.min(width, 0),
                startPoint.y + Math.min(height, 0),
                Math.abs(width),
                Math.abs(height)
        );
    }

    @Override
    public void paint(Graphics2D graphics) {
        super.paint(graphics);
        if (preview != null) previewPainter.draw(preview, graphics);
    }

    public void setPainter(Painter<? super Shape, ?> painter) {
        this.painter = painter;
    }

    public void setPreviewPainter(Painter<? super Shape, ?> previewPainter) {
        this.previewPainter = previewPainter;
    }

    public void setTransformPainter(Painter<? super Shape, ?> transformPainter) {
        this.transformPainter = transformPainter;
    }

    public static class Shortcut extends KeyShortcut {
        public Shortcut(GraphState graphState) {
            super(() -> setTool(graphState), KeyEvent.VK_O);
        }

        private static void setTool(GraphState graphState) {
            String oldToolName = graphState.tool.toPrettyString();
            boolean success = graphState.setTool(EllipseTool.class);
            String newToolName = graphState.tool.toPrettyString();
            if (success) {
                Main.log("Changed tool from '" + oldToolName + "' to '" + newToolName + "'");
            } else {
                Main.log("Unavailable tool of type '" + EllipseTool.class.getSimpleName() + "'");
            }
        }
    }
}
