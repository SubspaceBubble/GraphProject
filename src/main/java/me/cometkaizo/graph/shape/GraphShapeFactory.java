package me.cometkaizo.graph.shape;

import me.cometkaizo.graph.painter.Painter;

import java.awt.*;

public interface GraphShapeFactory {

    <S extends Shape> GraphShape<S> create(S shape, Painter<? super S, ?> painter, Painter<? super Shape, ?> transformPainter);
    <S extends Shape> GraphShape<S> create(S shape, Painter<? super S, ?> painter);
    <S extends Shape> GraphShape<S> create(S shape);

}
