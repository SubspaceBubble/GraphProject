package me.cometkaizo.graph.shape;

import me.cometkaizo.graph.GraphState;
import me.cometkaizo.graph.painter.OutlinePainter;
import me.cometkaizo.graph.painter.Painter;

import java.awt.*;

public class SimpleGraphShapeFactory implements GraphShapeFactory {

    private final GraphState graphState;

    public SimpleGraphShapeFactory(GraphState graphState) {
        this.graphState = graphState;
    }

    @Override
    public <S extends Shape> GraphShape<S> create(S shape, Painter<? super S, ?> painter, Painter<? super Shape, ?> transformPainter) {
        return new SimpleGraphShape<>(shape, painter, transformPainter);
    }
    @Override
    public <S extends Shape> GraphShape<S> create(S shape, Painter<? super S, ?> painter) {
        return new SimpleGraphShape<>(shape, painter, null);
    }

    @Override
    public <S extends Shape> GraphShape<S> create(S shape) {
        return new SimpleGraphShape<>(shape, new OutlinePainter(graphState), null);
    }
}
