package me.cometkaizo.graph.painter;

import me.cometkaizo.graph.GraphState;
import me.cometkaizo.util.GraphUtils;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class OutlinePainter extends AbstractPainter<Shape, OutlinePainter.Options> {

    private GeneralPath arc;

    public OutlinePainter(GraphState graphState) {
        super(graphState, Options.DEFAULT);
    }
    public OutlinePainter(GraphState graphState, Options defaultOptions) {
        super(graphState, defaultOptions);
    }

    @Override
    public void draw(Shape object, Graphics2D graphics, Options options) {
        super.draw(object, graphics, options);

        drawSegments(object, graphics, options);
        drawVertices(object, graphics, options);
    }

    private void drawSegments(Shape shape, Graphics2D graphics, Options options) {
        if (options.lineStroke != null) graphics.setStroke(options.lineStroke);
        if (options.lineColor != null) graphics.setColor(options.lineColor);


        PathIterator pathIterator = shape.getPathIterator(coordToScreen);

        GraphUtils.forEachSegment(pathIterator, (segType, start, coords) -> {
            switch (segType) {
                case PathIterator.SEG_LINETO -> drawSegment(graphics, start.x, start.y, coords);
                case PathIterator.SEG_QUADTO -> drawQuadArc(graphics, start.x, start.y, coords);
                case PathIterator.SEG_CUBICTO -> drawCubicArc(graphics, start.x, start.y, coords);
            }
        });
    }

    private void drawCubicArc(Graphics2D graphics, int startX, int startY, float[] coords) {
        if (arc == null) arc = new GeneralPath();
        arc.moveTo(startX, startY);
        arc.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
        graphics.draw(arc);
        arc.reset();
    }

    private void drawQuadArc(Graphics2D graphics, int startX, int startY, float[] coords) {
        if (arc == null) arc = new GeneralPath();
        arc.moveTo(startX, startY);
        arc.quadTo(coords[0], coords[1], coords[2], coords[3]);
        graphics.draw(arc);
        arc.reset();
    }

    private void drawSegment(Graphics2D graphics, int startX, int startY, float[] coords) {
        graphics.drawLine(startX, startY, (int) coords[0], (int) coords[1]);
    }

    private void drawVertices(Shape shape, Graphics2D graphics, Options options) {
        if (options.vertexStroke != null) graphics.setStroke(options.vertexStroke);
        if (options.vertexColor != null) graphics.setColor(options.vertexColor);

        PathIterator pathIterator = shape.getPathIterator(coordToScreen);

        GraphUtils.forEachPoint(pathIterator, (x, y) -> drawVertex(graphics, x.intValue(), y.intValue()));
    }

    private void drawVertex(Graphics2D graphics, int x, int y) {
        graphics.drawLine(x, y, x, y);
    }


    public record Options(
            Color lineColor,
            BasicStroke lineStroke,
            Color vertexColor,
            BasicStroke vertexStroke
    ) {
        public static final Options DEFAULT = new Options(Color.LIGHT_GRAY, new BasicStroke(3), Color.WHITE, new BasicStroke(5));
    }

}
