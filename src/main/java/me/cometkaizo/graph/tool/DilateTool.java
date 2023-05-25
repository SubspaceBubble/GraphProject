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

public class DilateTool extends SelectTool {
    private final Point2D shapeOrigin = new Point(0, 0);
    private final Point2D dilateOrigin = new Point(0, 0);
    private final Point2D scalePoint = new Point(0, 0);
    private double scaleSq;
    private final Line2D shapeOriginShape = new Line2D.Float(shapeOrigin, shapeOrigin);
    private final Line2D dilateOriginShape = new Line2D.Float(dilateOrigin, dilateOrigin);
    private final Line2D scalePointShape = new Line2D.Float(scalePoint, scalePoint);
    private final Line2D dilateLine = new Line2D.Float(shapeOrigin, dilateOrigin);
    private Painter<? super Shape, ?> dilateLinePainter;
    private Painter<? super Shape, ?> shapeOriginPainter;
    private Painter<? super Shape, ?> dilateOriginPainter;
    private Painter<? super Shape, ?> scalePointPainter;

    private int prevGraphWidth;
    private int prevGraphHeight;
    private int prevMouseGraphX;
    private int prevMouseGraphY;
    private boolean prevCanDragOrigin;
    private boolean prevCanDragAngle;
    private boolean prevCanDragScale;
    private boolean isAngleSnapping;

    private float originDragDistanceSq;
    private float angleDragDistanceSq;
    private float scaleDragDistanceSq;
    private double angleSnap;
    private double dilateOriginDistance;
    private double scalePointDistance;


    public DilateTool(GraphState graphState,
                      Painter<? super Shape, ?> dilateLinePainter,
                      Painter<? super Shape, ?> shapeOriginPainter,
                      Painter<? super Shape, ?> dilateOriginPainter,
                      Painter<? super Shape, ?> scalePointPainter,
                      float originDragDistance,
                      float angleDragDistance,
                      float scaleDragDistance,
                      double angleSnap) {
        super(graphState);
        this.dilateLinePainter = dilateLinePainter;
        this.shapeOriginPainter = shapeOriginPainter;
        this.dilateOriginPainter = dilateOriginPainter;
        this.scalePointPainter = scalePointPainter;

        this.originDragDistanceSq = originDragDistance * originDragDistance;
        this.angleDragDistanceSq = angleDragDistance * angleDragDistance;
        this.scaleDragDistanceSq = scaleDragDistance * scaleDragDistance;
        this.angleSnap = angleSnap;
        updateDilateLine();
    }

    @Override
    public void activate() {
        super.activate();
        update();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        prevCanDragOrigin = false;
        prevCanDragAngle = false;
        prevCanDragScale = false;
        if (graphState.hasShape(graphState.hoveredShape)) graphState.setSelectedShape(graphState.hoveredShape);

        update();
    }

    private void update() {
        updateShapeOrigin();
        double angleFromOrigin = getAngleDegrees(shapeOrigin, dilateOrigin);
        setScaleAngle(angleFromOrigin);
    }

    private void dilateSelectedShape() {
        if (!graphState.hasShape(graphState.selectedShape)) return;
        GraphShape<?> selectedShape = graphState.getSelectedShape();
        selectedShape.addTransform(new DilateTransform(
                dilateOrigin,
                sqrt(abs(scaleSq)) * signum(scaleSq) / dilateOriginDistance
        ));
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        int coordX = toCoordinateX(e.getX(), graphState.width / 2);
        int coordY = toCoordinateY(e.getY(), graphState.height / 2);
        double angleFromOrigin = getAngleDegrees(shapeOrigin.getX(), shapeOrigin.getY(), coordX, coordY);

        if (prevCanDragScale || canDragDilateScale(coordX, coordY)) {
            prevCanDragScale = true;
            dragDilateScale(coordX, coordY);
        } else if (prevCanDragOrigin || canDragDilateOrigin(coordX, coordY)) {
            prevCanDragOrigin = true;
            int deltaX = coordX - prevMouseGraphX;
            int deltaY = coordY - prevMouseGraphY;
            offsetDilateOrigin(deltaX, deltaY);
            setScaleAngle(angleFromOrigin);
        } else if (prevCanDragAngle || canDragDilateAngle(coordX, coordY)) {
            prevCanDragAngle = true;
            if (isAngleSnapping) setAngle(MathUtils.roundToMultipleOf(angleFromOrigin, angleSnap));
            else setAngle(angleFromOrigin);
        }

        prevMouseGraphX = coordX;
        prevMouseGraphY = coordY;
    }

    private boolean canDragDilateScale(int coordX, int coordY) {
        return scalePoint.distanceSq(coordX, coordY) < scaleDragDistanceSq;
    }

    private boolean canDragDilateOrigin(int coordX, int coordY) {
        return dilateOrigin.distanceSq(coordX, coordY) < originDragDistanceSq;
    }

    private void setAngle(double angle) {
        setDilateOriginAngle(angle);
        setScaleAngle(angle);
    }

    private void setDilateOriginAngle(double angle) {
        double angleSin = sin(toRadians(angle));
        double angleCos = cos(toRadians(angle));

        double newX = shapeOrigin.getX() +
                angleCos * dilateOriginDistance;
        double newY = shapeOrigin.getY() +
                angleSin * dilateOriginDistance;

        setDilateOrigin(newX, newY);
    }

    private void setScaleAngle(double angle) {
        double angleSin = sin(toRadians(angle));
        double angleCos = cos(toRadians(angle));

        double newX = shapeOrigin.getX() +
                angleCos * scalePointDistance;
        double newY = shapeOrigin.getY() +
                angleSin * scalePointDistance;

        setScalePoint(newX, newY);
    }

    private void setScalePoint(double newX, double newY) {
        scalePoint.setLocation(newX, newY);
        scalePointShape.setLine(scalePoint, scalePoint);
    }


    private boolean canDragDilateAngle(int coordX, int coordY) {
        return dilateLine.ptSegDistSq(coordX, coordY) < angleDragDistanceSq;
    }

    private void offsetDilateOrigin(int deltaX, int deltaY) {
        setDilateOrigin(dilateOrigin.getX() + deltaX, dilateOrigin.getY() + deltaY);
        updateDilateDistance();
    }

    private void setDilateOrigin(double newX, double newY) {
        dilateOrigin.setLocation(newX, newY);
        dilateOriginShape.setLine(dilateOrigin, dilateOrigin);
        updateDilateLine();
    }

    private void dragDilateScale(int mouseX, int mouseY) {
        Point2D closestPointOnLine = getClosestPointOnLine(dilateLine, mouseX, mouseY);
        setScalePoint(closestPointOnLine.getX(), closestPointOnLine.getY());
        //Main.log("scale point: " + scalePoint);
        updateScaleSq();
        updateScaleDistance();
    }

    private void updateScaleSq() {
        int sign = isFartherFromInSameQuadrant(scalePoint, dilateOrigin, shapeOrigin) ? -1 : 1;
        scaleSq = sign * scalePoint.distanceSq(dilateOrigin);
    }

    private void updateDilateDistance() {
        dilateOriginDistance = shapeOrigin.distance(dilateOrigin);
    }

    private void updateScaleDistance() {
        int sign = isFartherFromInSameQuadrant(scalePoint, shapeOrigin, dilateOrigin) ? -1 : 1;
        scalePointDistance = sign * shapeOrigin.distance(scalePoint);
    }

    private void updateShapeOrigin() {
        if (graphState.hasShape(graphState.selectedShape)) {
            Rectangle2D bounds = graphState.getSelectedShape().getBounds2D();
            shapeOrigin.setLocation(bounds.getCenterX(), bounds.getCenterY());
        } else {
            shapeOrigin.setLocation(0, 0);
        }
        shapeOriginShape.setLine(shapeOrigin, shapeOrigin);
        updateDilateLine();
        updateDilateDistance();
        updateScaleDistance();
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
            update();
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isAngleSnapping = false;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            dilateSelectedShape();
        }
    }

    @Override
    public void paint(Graphics2D graphics) {
        super.paint(graphics);
        if (graphState.width != prevGraphWidth || graphState.height != prevGraphHeight) updateDilateLine();

        drawDilateLine(graphics);
        drawDilateHandles(graphics);

        prevGraphWidth = graphState.width;
        prevGraphHeight = graphState.height;
    }

    private void drawDilateHandles(Graphics2D graphics) {
        shapeOriginPainter.draw(shapeOriginShape, graphics);
        dilateOriginPainter.draw(dilateOriginShape, graphics);
        scalePointPainter.draw(scalePointShape, graphics);
    }

    private void drawDilateLine(Graphics2D graphics) {
        dilateLinePainter.draw(dilateLine, graphics);
    }


    private void updateDilateLine() {
        dilateLine.setLine(shapeOrigin, dilateOrigin);
    }

    public void setDilateLinePainter(Painter<? super Shape, ?> dilateLinePainter) {
        this.dilateLinePainter = dilateLinePainter;
    }

    public void setShapeOriginPainter(Painter<? super Shape, ?> shapeOriginPainter) {
        this.shapeOriginPainter = shapeOriginPainter;
    }

    public void setOriginDragDistanceSq(float originDragDistanceSq) {
        this.originDragDistanceSq = originDragDistanceSq;
    }

    public void setAngleDragDistanceSq(float angleDragDistanceSq) {
        this.angleDragDistanceSq = angleDragDistanceSq;
    }

    public void setScaleDragDistanceSq(float scaleDragDistanceSq) {
        this.scaleDragDistanceSq = scaleDragDistanceSq;
    }

    public void setOriginDragDistance(float originDragDistance) {
        this.originDragDistanceSq = originDragDistance * originDragDistance;
    }

    public void setAngleDragDistance(float angleDragDistance) {
        this.angleDragDistanceSq = angleDragDistance * angleDragDistance;
    }

    public void setScaleDragDistance(float scaleDragDistance) {
        this.scaleDragDistanceSq = scaleDragDistance * scaleDragDistance;
    }

    public void setAngleSnap(double angleSnap) {
        this.angleSnap = angleSnap;
    }

    public void setDilateOriginPainter(Painter<? super Shape, ?> dilateOriginPainter) {
        this.dilateOriginPainter = dilateOriginPainter;
    }

    public void setScalePointPainter(Painter<? super Shape, ?> scalePointPainter) {
        this.scalePointPainter = scalePointPainter;
    }

    public static class Shortcut extends KeyShortcut {
        public Shortcut(GraphState graphState) {
            super(() -> setTool(graphState), KeyEvent.VK_D);
        }

        private static void setTool(GraphState graphState) {
            String oldToolName = graphState.tool.toPrettyString();
            boolean success = graphState.setTool(DilateTool.class);
            String newToolName = graphState.tool.toPrettyString();

            if (success) {
                Main.log("Changed tool from '" + oldToolName + "' to '" + newToolName + "'");
            } else {
                Main.log("Unavailable tool of type '" + DilateTool.class.getSimpleName() + "'");
            }
        }
    }

    public static class DilateTransform implements Function<Shape, Shape> {
        private Shape transformedShape;
        private AffineTransform targetTransform;
        private final Point2D dilateOrigin;
        private final double scale;

        public DilateTransform(Point2D dilateOrigin, double scale) {
            this.dilateOrigin = dilateOrigin;
            this.scale = scale;
        }

        @Override
        public Shape apply(Shape shape) {
            if (targetTransform == null) updateTargetTransform();
            if (transformedShape == null)
                transformedShape = targetTransform.createTransformedShape(shape);
            return transformedShape;
        }

        private void updateTargetTransform() {
            targetTransform = new AffineTransform();
            targetTransform.translate(dilateOrigin.getX(), dilateOrigin.getY());
            targetTransform.scale(scale, scale);
            targetTransform.translate(-dilateOrigin.getX(), -dilateOrigin.getY());
        }
    }
}
