package me.cometkaizo.graph.tool;

import me.cometkaizo.Main;
import me.cometkaizo.animation.Ease;
import me.cometkaizo.graph.GraphState;
import me.cometkaizo.graph.painter.Painter;
import me.cometkaizo.graph.shape.GraphShape;
import me.cometkaizo.shortcut.KeyShortcut;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.function.Function;

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static me.cometkaizo.util.GraphUtils.*;
import static me.cometkaizo.util.MathUtils.roundToMultipleOf;

public class MirrorTool extends SelectTool {
    private double mirrorAngle = 45; // east = 0, CCW
    private double actualMirrorAngle = 45;
    private final Point2D mirrorOrigin = new Point(0, 0);
    private final Line2D mirrorOriginShape = new Line2D.Float(mirrorOrigin, mirrorOrigin);
    private Line2D mirrorLine;
    private int mirrorLineLength;
    private Painter<? super Shape, ?> mirrorLinePainter;
    private Painter<? super Shape, ?> mirrorOriginPainter;

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

    private Ease mirrorEase;

    /**
     * Constructs a new Mirror tool
     *
     * @param graphState          the graph state instance
     * @param mirrorEase          the mirroring ease
     * @param mirrorLinePainter   the painter for the mirroring line
     * @param mirrorOriginPainter the painter for the mirroring line origin
     * @param originDragDistance  the distance the mouse must be from the mirror origin handle to drag it
     * @param angleDragDistance   the distance the mouse must be from the mirror angle handle to drag it
     * @param angleSnap           the snapping angle when holding the snap key
     */
    public MirrorTool(GraphState graphState,
                      Ease mirrorEase,
                      Painter<? super Shape, ?> mirrorLinePainter,
                      Painter<? super Shape, ?> mirrorOriginPainter,
                      float originDragDistance,
                      float angleDragDistance,
                      double angleSnap) {
        super(graphState);
        this.mirrorEase = mirrorEase;
        this.mirrorOriginPainter = mirrorOriginPainter;
        this.mirrorLinePainter = mirrorLinePainter;
        this.originDragDistanceSq = originDragDistance * originDragDistance;
        this.angleDragDistanceSq = angleDragDistance * angleDragDistance;
        this.angleSnap = angleSnap;
        updateMirrorLine();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        prevCanDragOrigin = false;
        prevCanDragAngle = false;
        if (graphState.hasShape(graphState.hoveredShape)) graphState.setSelectedShape(graphState.hoveredShape);
    }

    private void mirrorSelectedShape() {
        if (!graphState.hasShape(graphState.selectedShape)) return;
        GraphShape<?> selectedShape = graphState.getSelectedShape();
        selectedShape.addTransform(new MirrorTransform(
                mirrorOrigin,
                actualMirrorAngle
        ));
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        int coordX = toCoordinateX(e.getX(), graphState.width / 2);
        int coordY = toCoordinateY(e.getY(), graphState.height / 2);
        double angleFromOrigin = getAngleDegrees(mirrorOrigin.getX(), mirrorOrigin.getY(), coordX, coordY);

        if (prevCanDragOrigin || canDragMirrorOrigin(coordX, coordY)) {
            prevCanDragOrigin = true;
            int deltaX = coordX - prevMouseGraphX;
            int deltaY = coordY - prevMouseGraphY;
            offsetMirror(deltaX, deltaY);
        } else if (prevCanDragAngle || canDragMirrorAngle(coordX, coordY)) {
            prevCanDragAngle = true;
            offsetAngle(angleFromOrigin - prevAngleFromOrigin);

            actualMirrorAngle = isAngleSnapping ? roundToMultipleOf(mirrorAngle, angleSnap) : mirrorAngle;
        }

        prevMouseGraphX = coordX;
        prevMouseGraphY = coordY;
        prevAngleFromOrigin = angleFromOrigin;
    }

    private boolean canDragMirrorOrigin(int coordX, int coordY) {
        return mirrorOrigin.distanceSq(coordX, coordY) < originDragDistanceSq;
    }

    private void offsetMirror(int deltaX, int deltaY) {
        mirrorOrigin.setLocation(mirrorOrigin.getX() + deltaX, mirrorOrigin.getY() + deltaY);
        mirrorOriginShape.setLine(mirrorOrigin, mirrorOrigin);
        mirrorLine.setLine(mirrorLine.getX1() + deltaX, mirrorLine.getY1() + deltaY,
                mirrorLine.getX2() + deltaX, mirrorLine.getY2() + deltaY);
    }

    private void offsetAngle(double deltaAngle) {
        mirrorAngle += deltaAngle;
        updateMirrorLine();
    }

    private boolean canDragMirrorAngle(int coordX, int coordY) {
        return mirrorLine.ptLineDistSq(coordX, coordY) < angleDragDistanceSq;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        prevMouseGraphX = toCoordinateX(e.getX(), graphState.width / 2);
        prevMouseGraphY = toCoordinateY(e.getY(), graphState.height / 2);
        prevAngleFromOrigin = getAngleDegrees(mirrorOrigin.getX(), mirrorOrigin.getY(), prevMouseGraphX, prevMouseGraphY);
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
            mirrorSelectedShape();
        }
    }

    @Override
    public void paint(Graphics2D graphics) {
        super.paint(graphics);
        if (graphState.width != prevGraphWidth || graphState.height != prevGraphHeight) updateMirrorLine();

        drawMirrorLine(graphics);
        drawMirrorOrigin(graphics);

        prevGraphWidth = graphState.width;
        prevGraphHeight = graphState.height;
    }

    private void drawMirrorOrigin(Graphics2D graphics) {
        mirrorOriginPainter.draw(mirrorOriginShape, graphics);
    }

    private void drawMirrorLine(Graphics2D graphics) {
        mirrorLinePainter.draw(mirrorLine, graphics);
    }

    private void updateMirrorLine() {
        updateMirrorLineLength();
        float halfMirrorLength = mirrorLineLength / 2F;

        float halfWidth = (float) (sin(toRadians(90 - actualMirrorAngle)) * halfMirrorLength);
        float halfHeight = (float) (sin(toRadians(actualMirrorAngle)) * halfMirrorLength);

        float originX = (float) mirrorOrigin.getX();
        float originY = (float) mirrorOrigin.getY();

        if (mirrorLine == null) mirrorLine = new Line2D.Float();
        mirrorLine.setLine(originX + halfWidth, originY + halfHeight,
                originX - halfWidth, originY - halfHeight);
    }

    private void updateMirrorLineLength() {
        int width = graphState.width;
        int height = graphState.height;
        mirrorLineLength = 2 + (int) Math.sqrt(width * width + height * height);
    }

    public void setMirrorLinePainter(Painter<? super Shape, ?> mirrorLinePainter) {
        this.mirrorLinePainter = mirrorLinePainter;
    }

    public void setMirrorOriginPainter(Painter<? super Shape, ?> mirrorOriginPainter) {
        this.mirrorOriginPainter = mirrorOriginPainter;
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

    public void setMirrorEase(Ease mirrorEase) {
        this.mirrorEase = mirrorEase;
    }


    public static class Shortcut extends KeyShortcut {
        public Shortcut(GraphState graphState) {
            super(() -> setTool(graphState), KeyEvent.VK_M);
        }

        private static void setTool(GraphState graphState) {
            String oldToolName = graphState.tool.toPrettyString();
            boolean success = graphState.setTool(MirrorTool.class);
            String newToolName = graphState.tool.toPrettyString();

            if (success) {
                Main.log("Changed tool from '" + oldToolName + "' to '" + newToolName + "'");
            } else {
                Main.log("Unavailable tool of type '" + MirrorTool.class.getSimpleName() + "'");
            }
        }
    }

    public static class MirrorTransform implements Function<Shape, Shape> {
        private Shape transformedShape;
        private final AffineTransform targetTransform;

        public MirrorTransform(Point2D mirrorOrigin, double mirrorAngle) {

            targetTransform = new AffineTransform();
            targetTransform.translate(mirrorOrigin.getX(), mirrorOrigin.getY());
            double rotationOffset = toRadians(mirrorAngle - 90);
            targetTransform.rotate(rotationOffset, 0, 0);
            targetTransform.scale(-1, 1);
            targetTransform.rotate(-rotationOffset, 0, 0);
            targetTransform.translate(-mirrorOrigin.getX(), -mirrorOrigin.getY());

        }

        @Override
        public Shape apply(Shape shape) {
            if (transformedShape == null) transformedShape = targetTransform.createTransformedShape(shape);
            return transformedShape;
        }
    }
}
