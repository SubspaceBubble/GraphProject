package me.cometkaizo.graph.tool;

import me.cometkaizo.Main;
import me.cometkaizo.graph.GraphState;
import me.cometkaizo.graph.painter.Painter;
import me.cometkaizo.graph.shape.GraphShape;
import me.cometkaizo.shortcut.KeyShortcut;
import me.cometkaizo.util.GraphUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static me.cometkaizo.util.GraphUtils.*;
import static me.cometkaizo.util.GraphUtils.getAngleDegrees;
import static me.cometkaizo.util.MathUtils.roundToMultipleOf;

public class RotateTool extends SelectTool {
    private double rotateAngle = 45; // east = 0, CCW
    private double actualRotateAngle = 45;
    private final Point2D rotateOrigin = new Point(0, 0);
    private final Line2D rotateOriginShape = new Line2D.Float(rotateOrigin, rotateOrigin);
    private Line2D rotateLine;
    private int rotateLineLength;
    private Painter<? super Shape, ?> rotateLinePainter;
    private Painter<? super Shape, ?> rotateOriginPainter;

    private int prevGraphWidth;
    private int prevGraphHeight;
    private int prevMouseGraphX;
    private int prevMouseGraphY;
    private boolean prevCanDragOrigin;
    private boolean prevCanDragAngle;
    private boolean isAngleSnapping;

    private float originDragDistanceSq;
    private double prevAngleFromOrigin;
    private float angleDragDistanceSq;
    private double angleSnap;
    
    
    public RotateTool(GraphState graphState, Painter<? super Shape, ?> rotateLinePainter, Painter<? super Shape, ?> rotateOriginPainter, float originDragDistance, float angleDragDistance, double angleSnap) {
        super(graphState);
        this.rotateLinePainter = rotateLinePainter;

        this.rotateOriginPainter = rotateOriginPainter;
        this.originDragDistanceSq = originDragDistance * originDragDistance;
        this.angleDragDistanceSq = angleDragDistance * angleDragDistance;
        this.angleSnap = angleSnap;
        updateRotateLine();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        prevCanDragOrigin = false;
        prevCanDragAngle = false;
        if (graphState.hasShape(graphState.hoveredShape)) graphState.setSelectedShape(graphState.hoveredShape);
    }

    private void rotateSelectedShape() {
        if (!graphState.hasShape(graphState.selectedShape)) return;
        GraphShape<?> selectedShape = graphState.getSelectedShape();
        selectedShape.addTransform(new RotateTransform(
                rotateOrigin,
                actualRotateAngle
        ));
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        int coordX = toCoordinateX(e.getX(), graphState.width / 2);
        int coordY = toCoordinateY(e.getY(), graphState.height / 2);
        double angleFromOrigin = getAngleDegrees(rotateOrigin.getX(), rotateOrigin.getY(), coordX, coordY);

        if (prevCanDragOrigin || canDragRotateOrigin(coordX, coordY)) {
            prevCanDragOrigin = true;
            int deltaX = coordX - prevMouseGraphX;
            int deltaY = coordY - prevMouseGraphY;
            offsetRotate(deltaX, deltaY);
        } else if (prevCanDragAngle || canDragRotateAngle(coordX, coordY)) {
            prevCanDragAngle = true;
            offsetAngle(angleFromOrigin - prevAngleFromOrigin);

            actualRotateAngle = isAngleSnapping ? roundToMultipleOf(rotateAngle, angleSnap) : rotateAngle;
        }

        prevMouseGraphX = coordX;
        prevMouseGraphY = coordY;
        prevAngleFromOrigin = angleFromOrigin;
    }

    private boolean canDragRotateOrigin(int coordX, int coordY) {
        return rotateOrigin.distanceSq(coordX, coordY) < originDragDistanceSq;
    }

    private void offsetRotate(int deltaX, int deltaY) {
        rotateOrigin.setLocation(rotateOrigin.getX() + deltaX, rotateOrigin.getY() + deltaY);
        rotateOriginShape.setLine(rotateOrigin, rotateOrigin);
        rotateLine.setLine(rotateLine.getX1() + deltaX, rotateLine.getY1() + deltaY,
                rotateLine.getX2() + deltaX, rotateLine.getY2() + deltaY);
    }

    private void offsetAngle(double deltaAngle) {
        rotateAngle += deltaAngle;
        updateRotateLine();
    }

    private boolean canDragRotateAngle(int coordX, int coordY) {
        return rotateLine.ptSegDistSq(coordX, coordY) < angleDragDistanceSq;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        prevMouseGraphX = toCoordinateX(e.getX(), graphState.width / 2);
        prevMouseGraphY = toCoordinateY(e.getY(), graphState.height / 2);
        prevAngleFromOrigin = getAngleDegrees(rotateOrigin.getX(), rotateOrigin.getY(), prevMouseGraphX, prevMouseGraphY);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isAngleSnapping = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isAngleSnapping = false;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            rotateSelectedShape();
        }
    }

    @Override
    public void paint(Graphics2D graphics) {
        super.paint(graphics);
        if (graphState.width != prevGraphWidth || graphState.height != prevGraphHeight) updateRotateLine();

        drawRotateLine(graphics);
        drawRotateOrigin(graphics);

        prevGraphWidth = graphState.width;
        prevGraphHeight = graphState.height;
    }

    private void drawRotateOrigin(Graphics2D graphics) {
        rotateOriginPainter.draw(rotateOriginShape, graphics);
    }

    private void drawRotateLine(Graphics2D graphics) {
        rotateLinePainter.draw(rotateLine, graphics);
    }


    private void updateRotateLine() {
        updateRotateLineLength();

        float width = (float) (sin(toRadians(90 - actualRotateAngle)) * rotateLineLength);
        float height = (float) (sin(toRadians(actualRotateAngle)) * rotateLineLength);

        float originX = (float) rotateOrigin.getX();
        float originY = (float) rotateOrigin.getY();

        if (rotateLine == null) rotateLine = new Line2D.Float();
        rotateLine.setLine(originX, originY,
                originX + width, originY + height);
    }

    private void updateRotateLineLength() {
        int width = graphState.width;
        int height = graphState.height;
        rotateLineLength = 2 + (int) Math.sqrt(width * width + height * height) / 2;
    }

    public void setRotateLinePainter(Painter<? super Shape, ?> rotateLinePainter) {
        this.rotateLinePainter = rotateLinePainter;
    }

    public void setRotateOriginPainter(Painter<? super Shape, ?> rotateOriginPainter) {
        this.rotateOriginPainter = rotateOriginPainter;
    }

    public void setOriginDragDistanceSq(float originDragDistanceSq) {
        this.originDragDistanceSq = originDragDistanceSq;
    }

    public void setAngleDragDistanceSq(float angleDragDistanceSq) {
        this.angleDragDistanceSq = angleDragDistanceSq;
    }

    public void setOriginDragDistance(float originDragDistance) {
        this.originDragDistanceSq = originDragDistance * originDragDistance;
    }

    public void setAngleDragDistance(float angleDragDistance) {
        this.angleDragDistanceSq = angleDragDistance * angleDragDistance;
    }

    public void setAngleSnap(double angleSnap) {
        this.angleSnap = angleSnap;
    }

    public static class Shortcut extends KeyShortcut {
        public Shortcut(GraphState graphState) {
            super(() -> setTool(graphState), KeyEvent.VK_R);
        }

        private static void setTool(GraphState graphState) {
            String oldToolName = graphState.tool.toPrettyString();
            boolean success = graphState.setTool(RotateTool.class);
            String newToolName = graphState.tool.toPrettyString();

            if (success) {
                Main.log("Changed tool from '" + oldToolName + "' to '" + newToolName + "'");
            } else {
                Main.log("Unavailable tool of type '" + RotateTool.class.getSimpleName() + "'");
            }
        }
    }

    public static class RotateTransform implements Function<Shape, Shape> {
        private Shape transformedShape;
        private AffineTransform targetTransform;
        private final Point2D rotateOrigin;
        private final double rotateAngle;

        public RotateTransform(Point2D rotateOrigin, double rotateAngle) {
            this.rotateOrigin = rotateOrigin;
            this.rotateAngle = rotateAngle;
        }

        @Override
        public Shape apply(Shape shape) {
            if (targetTransform == null) updateTargetTransform(shape);
            if (transformedShape == null) transformedShape = targetTransform.createTransformedShape(shape);
            return transformedShape;
        }

        private void updateTargetTransform(Shape shape) {
            targetTransform = new AffineTransform();
            double degreesToCenter = getDegreesFromOrigin(shape);
            targetTransform.rotate(toRadians(rotateAngle - degreesToCenter), rotateOrigin.getX(), rotateOrigin.getY());
        }

        private double getDegreesFromOrigin(Shape shape) {
            Rectangle2D shapeBounds = shape.getBounds2D();
            return GraphUtils.getAngleDegrees(rotateOrigin.getX(),
                    rotateOrigin.getY(),
                    shapeBounds.getCenterX(),
                    shapeBounds.getCenterY());
        }
    }
}
