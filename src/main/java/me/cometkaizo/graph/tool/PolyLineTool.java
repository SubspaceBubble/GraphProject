package me.cometkaizo.graph.tool;

import me.cometkaizo.Main;
import me.cometkaizo.graph.painter.Painter;
import me.cometkaizo.graph.shape.GraphShape;
import me.cometkaizo.graph.GraphState;
import me.cometkaizo.graph.painter.OutlinePainter;
import me.cometkaizo.shortcut.KeyShortcut;
import me.cometkaizo.util.GraphUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static me.cometkaizo.util.GraphUtils.*;

public class PolyLineTool extends DrawTool {

    private Point2D previewStartPoint;
    private Line2D previewSegment;
    private Painter<? super Shape, ?> painter;
    private Painter<? super Shape, ?> previewPainter;
    private Painter<? super Shape, ?> transformPainter;

    private float snapDistance;
    private List<Point2D> polyLinePoints = new ArrayList<>(5);

    public PolyLineTool(GraphState graphState, Painter<? super Shape, ?> painter, Painter<? super Shape, ?> previewPainter, Painter<? super Shape, ?> transformPainter, float snapDistance) {
        super(graphState);
        this.painter = painter;
        this.previewPainter = previewPainter;
        this.transformPainter = transformPainter;
        this.previewPainter = new OutlinePainter(graphState);
        this.snapDistance = snapDistance;
    }

    @Override
    public void activate() {
        super.activate();
        if (selectedShapeNotPresent() || selectedShapeNotPath()) return;
        Path2D polyLine = (Path2D) graphState.shapes.get(graphState.selectedShape).getShape();
        previewStartPoint = polyLine.getCurrentPoint();
        polyLinePoints = getPoints(polyLine);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        removeInfo();
    }

    public void removeInfo() {
        previewStartPoint = null;
        previewSegment = null;
        polyLinePoints.clear();
    }

    @Override
    public void leftClicked(Point point) {
        if (selectedShapeNotPresent() || selectedShapeNotPath()) {
            graphState.setSelectedShape(graphState.shapes.size());
            addNewLine(point);
        } else {
            addPointToSelectedLine(point);
            previewSegment = null;
        }
    }

    @Override
    public void rightClicked(Point point) {
        removeInfo();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            removeInfo();
        }
    }

    private boolean selectedShapeNotPath() {
        return !(graphState.shapes.get(graphState.selectedShape).getShape() instanceof Path2D);
    }

    private boolean selectedShapeNotPresent() {
        return graphState.selectedShape < 0 || graphState.selectedShape >= graphState.shapes.size();
    }

    private void addNewLine(Point point) {
        Point coordinatePoint = GraphUtils.toCoordinate(point, graphState.width / 2, graphState.height / 2);

        Path2D.Float polyLine = new Path2D.Float(Path2D.WIND_EVEN_ODD, 1);
        polyLine.moveTo(coordinatePoint.x, coordinatePoint.y);
        previewStartPoint = coordinatePoint;
        polyLinePoints.add(coordinatePoint);

        graphState.addShape(polyLine, painter, transformPainter);
    }

    private void addPointToSelectedLine(Point point) {
        GraphShape<?> selectedShape = graphState.shapes.get(graphState.selectedShape);
        Path2D polyLine = (Path2D) selectedShape.getShape();

        int originX = graphState.width / 2;
        int originY = graphState.height / 2;

        Point2D coordinatePoint = applySnapping(toCoordinate(point, originX, originY));

        polyLine.lineTo(coordinatePoint.getX(), coordinatePoint.getY());
        previewStartPoint = coordinatePoint;
        polyLinePoints.add(coordinatePoint);

        graphState.updateSelectionPainter();
        graphState.updateHoverBoxPainter();
    }

    private Point2D applySnapping(Point coordinatePoint) {
        return GraphUtils.snap(snapDistance, coordinatePoint, polyLinePoints);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        int originX = graphState.width / 2;
        int originY = graphState.height / 2;

        Point2D endPoint = applySnapping(toCoordinate(e.getPoint(), originX, originY));

        updatePreview(endPoint);
    }

    public void updatePreview(Point2D endPoint) {
        if (previewStartPoint == null || endPoint == null) {
            previewSegment = null;
            return;
        }

        if (previewSegment == null) {
            previewSegment = new Line2D.Float(previewStartPoint, endPoint);
        } else {
            previewSegment.setLine(previewStartPoint, endPoint);
        }
    }

    @Override
    public void paint(Graphics2D graphics) {
        super.paint(graphics);
        if (previewSegment != null) previewPainter.draw(previewSegment, graphics);
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

    public void setSnapDistance(float snapDistance) {
        this.snapDistance = snapDistance;
    }

    public static class Shortcut extends KeyShortcut {
        public Shortcut(GraphState graphState) {
            super(() -> setTool(graphState), KeyEvent.VK_B);
        }

        private static void setTool(GraphState graphState) {
            String oldToolName = graphState.tool.toPrettyString();
            boolean success = graphState.setTool(PolyLineTool.class);
            String newToolName = graphState.tool.toPrettyString();
            if (success) {
                Main.log("Changed tool from '" + oldToolName + "' to '" + newToolName + "'");
            } else {
                Main.log("Unavailable tool of type '" + PolyLineTool.class.getSimpleName() + "'");
            }
        }
    }
}
