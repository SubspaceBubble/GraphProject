package me.cometkaizo.graph.painter;

import me.cometkaizo.graph.GraphState;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Objects;

public abstract class AbstractPainter<S, O> implements Painter<S, O> {

    protected final GraphState graphState;
    protected final O defaultOptions;
    protected AffineTransform coordToScreen;
    protected int prevGraphWidth;
    protected int prevGraphHeight;

    public AbstractPainter(GraphState graphState, O defaultOptions) {
        Objects.requireNonNull(graphState, "Graph State cannot be null");
        this.graphState = graphState;
        this.defaultOptions = defaultOptions;
        this.coordToScreen = new AffineTransform();
        update(graphState);
    }


    @Override
    public void draw(S object, Graphics2D graphics, O options) {
        Objects.requireNonNull(object, "Shape cannot be null");
        Objects.requireNonNull(graphics, "Graphics cannot be null");
        update(graphState);
    }

    @Override
    public void draw(S object, Graphics2D graphics) {
        draw(object, graphics, defaultOptions);
    }

    private void update(GraphState graphState) {
        if (graphState.width != prevGraphWidth || graphState.height != prevGraphHeight) {
            coordToScreen.setToTranslation(graphState.width / 2F, graphState.height / 2F);
            coordToScreen.scale(1, -1);
        }

        prevGraphWidth = graphState.width;
        prevGraphHeight = graphState.height;
    }
}
