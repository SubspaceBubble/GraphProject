package me.cometkaizo.graph.shape;

import me.cometkaizo.graph.painter.Painter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SimpleGraphShape<S extends Shape> implements GraphShape<S> {

    private final S shape;
    private final Painter<? super S, ?> painter;
    private final Painter<? super Shape, ?> transformPainter;
    private final List<Function<? super S, Shape>> transforms = new ArrayList<>(1);

    public SimpleGraphShape(S shape, Painter<? super S, ?> painter, Painter<? super Shape, ?> transformPainter) {
        this.shape = shape;
        this.painter = painter;
        this.transformPainter = transformPainter;
    }


    @Override
    public void draw(Graphics2D graphics) {
        if (painter != null) painter.draw(shape, graphics);
        if (transformPainter != null) transforms.forEach(transform -> transformPainter.draw(transform.apply(shape), graphics));
    }

    @Override
    public void addTransform(Function<? super S, Shape> effect) {
        transforms.add(effect);
    }

    public void removeTransform(Function<? super S, Shape> effect) {
        transforms.remove(effect);
    }

    @Override
    public void clearTransforms() {
        transforms.clear();
    }

    @Override
    public Shape getSelectionBounds() {
        return getBounds2D();
    }

    @Override
    public S getShape() {
        return shape;
    }

    @Override
    public Rectangle getBounds() {
        return shape.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return shape.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
        return shape.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return shape.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return shape.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return shape.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return shape.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return shape.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return shape.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return shape.getPathIterator(at, flatness);
    }
}
