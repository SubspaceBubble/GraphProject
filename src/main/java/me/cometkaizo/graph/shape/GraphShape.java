package me.cometkaizo.graph.shape;

import java.awt.*;
import java.util.function.Function;

public interface GraphShape<S extends Shape> extends Shape {
    void draw(Graphics2D graphics);
    void addTransform(Function<? super S, Shape> effect);
    void removeTransform(Function<? super S, Shape> effect);
    void clearTransforms();
    Shape getSelectionBounds();

    S getShape();

}
