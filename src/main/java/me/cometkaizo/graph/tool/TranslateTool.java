package me.cometkaizo.graph.tool;

import me.cometkaizo.Main;
import me.cometkaizo.graph.GraphState;
import me.cometkaizo.graph.painter.Painter;
import me.cometkaizo.graph.shape.GraphShape;
import me.cometkaizo.shortcut.KeyShortcut;
import me.cometkaizo.util.MathUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;

import static java.lang.Math.*;
import static me.cometkaizo.util.GraphUtils.*;

public class TranslateTool extends SelectTool {
    private final Point2D translateOrigin = new Point(0, 0);
    private final Point2D translateTarget = new Point(0, 0);
    private final Line2D translateOriginShape = new Line2D.Float(translateOrigin, translateOrigin);
    private final Line2D translateTargetShape = new Line2D.Float(translateTarget, translateTarget);
    private final Line2D translateLine = new Line2D.Float(translateOrigin, translateTarget);
    private Painter<? super Shape, ?> translateLinePainter;
    private Painter<? super Shape, ?> translateOriginPainter;
    private Painter<? super Shape, ?> translateTargetPainter;

    private int prevGraphWidth;
    private int prevGraphHeight;
    private int prevMouseGraphX;
    private int prevMouseGraphY;
    private boolean prevCanDragTarget;
    private boolean prevCanDragAngle;
    private boolean isAngleSnapping;

    private float targetDragDistanceSq;
    private float angleDragDistanceSq;
    private double angleSnap;
    private double translateDistance;


    public TranslateTool(GraphState graphState,
                         Painter<? super Shape, ?> translateLinePainter,
                         Painter<? super Shape, ?> translateOriginPainter,
                         Painter<? super Shape, ?> translateTargetPainter,
                         float targetDragDistance,
                         float angleDragDistance,
                         double angleSnap) {
        super(graphState);
        this.translateLinePainter = translateLinePainter;
        this.translateOriginPainter = translateOriginPainter;
        this.translateTargetPainter = translateTargetPainter;

        this.targetDragDistanceSq = targetDragDistance * targetDragDistance;
        this.angleDragDistanceSq = angleDragDistance * angleDragDistance;
        this.angleSnap = angleSnap;
        updateTranslateLine();
    }

    @Override
    public void activate() {
        super.activate();
        updateTranslateOrigin();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        prevCanDragTarget = false;
        prevCanDragAngle = false;
        if (graphState.hasShape(graphState.hoveredShape)) graphState.setSelectedShape(graphState.hoveredShape);

        if (graphState.hasShape(graphState.selectedShape)) {
            updateTranslateOrigin();
        }
    }

    private void translateSelectedShape() {
        if (!graphState.hasShape(graphState.selectedShape)) return;
        GraphShape<?> selectedShape = graphState.getSelectedShape();
        selectedShape.addTransform(new TranslateTransform(
                translateOrigin,
                translateTarget
        ));
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        int coordX = toCoordinateX(e.getX(), graphState.width / 2);
        int coordY = toCoordinateY(e.getY(), graphState.height / 2);
        double angleFromOrigin = getAngleDegrees(translateOrigin.getX(), translateOrigin.getY(), coordX, coordY);

        if (prevCanDragTarget || canDragTranslateTarget(coordX, coordY)) {
            prevCanDragTarget = true;
            int deltaX = coordX - prevMouseGraphX;
            int deltaY = coordY - prevMouseGraphY;
            offsetTranslateTarget(deltaX, deltaY);
        } else if (prevCanDragAngle || canDragTranslateAngle(coordX, coordY)) {
            prevCanDragAngle = true;
            if (isAngleSnapping) setAngle(MathUtils.roundToMultipleOf(angleFromOrigin, angleSnap));
            else setAngle(angleFromOrigin);
        }

        prevMouseGraphX = coordX;
        prevMouseGraphY = coordY;
    }

    private boolean canDragTranslateTarget(int coordX, int coordY) {
        return translateTarget.distanceSq(coordX, coordY) < targetDragDistanceSq;
    }

    private void setAngle(double angle) {
        double angleSin = sin(toRadians(angle));
        double angleCos = cos(toRadians(angle));

        double newX = translateOrigin.getX() +
                angleCos * translateDistance;
        double newY = translateOrigin.getY() +
                angleSin * translateDistance;

        setTranslateTarget(newX, newY);
    }


    private boolean canDragTranslateAngle(int coordX, int coordY) {
        return translateLine.ptSegDistSq(coordX, coordY) < angleDragDistanceSq;
    }

    private void offsetTranslateTarget(int deltaX, int deltaY) {
        setTranslateTarget(translateTarget.getX() + deltaX, translateTarget.getY() + deltaY);
        updateTranslateDistance();
    }

    private void setTranslateTarget(double newX, double newY) {
        translateTarget.setLocation(newX, newY);
        translateTargetShape.setLine(translateTarget, translateTarget);
        updateTranslateLine();
    }

    private void updateTranslateDistance() {
        translateDistance = translateOrigin.distance(translateTarget);
    }

    private void updateTranslateOrigin() {
        Rectangle2D bounds = graphState.getSelectedShape().getBounds2D();
        translateOrigin.setLocation(bounds.getCenterX(), bounds.getCenterY());
        translateOriginShape.setLine(translateOrigin, translateOrigin);
        updateTranslateLine();
        updateTranslateDistance();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        prevMouseGraphX = toCoordinateX(e.getX(), graphState.width / 2);
        prevMouseGraphY = toCoordinateY(e.getY(), graphState.height / 2);
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
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            updateTranslateOrigin();
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isAngleSnapping = false;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            translateSelectedShape();
        }
    }

    @Override
    public void paint(Graphics2D graphics) {
        super.paint(graphics);
        if (graphState.width != prevGraphWidth || graphState.height != prevGraphHeight) updateTranslateLine();

        drawTranslateLine(graphics);
        drawTranslateHandles(graphics);

        prevGraphWidth = graphState.width;
        prevGraphHeight = graphState.height;
    }

    private void drawTranslateHandles(Graphics2D graphics) {
        translateOriginPainter.draw(translateOriginShape, graphics);
        translateTargetPainter.draw(translateTargetShape, graphics);
    }

    private void drawTranslateLine(Graphics2D graphics) {
        translateLinePainter.draw(translateLine, graphics);
    }


    private void updateTranslateLine() {
        translateLine.setLine(translateOrigin, translateTarget);
    }

    public void setTranslateLinePainter(Painter<? super Shape, ?> translateLinePainter) {
        this.translateLinePainter = translateLinePainter;
    }

    public void setTranslateOriginPainter(Painter<? super Shape, ?> translateOriginPainter) {
        this.translateOriginPainter = translateOriginPainter;
    }

    public void setTargetDragDistanceSq(float targetDragDistanceSq) {
        this.targetDragDistanceSq = targetDragDistanceSq;
    }

    public void setAngleDragDistanceSq(float angleDragDistanceSq) {
        this.angleDragDistanceSq = angleDragDistanceSq;
    }

    public void setOriginDragDistance(float originDragDistance) {
        this.targetDragDistanceSq = originDragDistance * originDragDistance;
    }

    public void setAngleDragDistance(float angleDragDistance) {
        this.angleDragDistanceSq = angleDragDistance * angleDragDistance;
    }

    public void setAngleSnap(double angleSnap) {
        this.angleSnap = angleSnap;
    }

    public void setTranslateTargetPainter(Painter<? super Shape, ?> translateTargetPainter) {
        this.translateTargetPainter = translateTargetPainter;
    }

    public static class Shortcut extends KeyShortcut {
        public Shortcut(GraphState graphState) {
            super(() -> setTool(graphState), KeyEvent.VK_T);
        }

        private static void setTool(GraphState graphState) {
            String oldToolName = graphState.tool.toPrettyString();
            boolean success = graphState.setTool(TranslateTool.class);
            String newToolName = graphState.tool.toPrettyString();

            if (success) {
                Main.log("Changed tool from '" + oldToolName + "' to '" + newToolName + "'");
            } else {
                Main.log("Unavailable tool of type '" + TranslateTool.class.getSimpleName() + "'");
            }
        }
    }

    public static class TranslateTransform implements Function<Shape, Shape> {
        private Shape transformedShape;
        private final AffineTransform targetTransform;

        public TranslateTransform(Point2D translateOrigin, Point2D translateTarget) {

            targetTransform = AffineTransform.getTranslateInstance(
                    translateTarget.getX() - translateOrigin.getX(),
                    translateTarget.getY() - translateOrigin.getY()
            );
        }

        @Override
        public Shape apply(Shape shape) {
            if (transformedShape == null)
                transformedShape = targetTransform.createTransformedShape(shape);
            return transformedShape;
        }
    }
}
