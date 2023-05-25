package me.cometkaizo.graph;

import me.cometkaizo.graph.painter.OutlinePainter;
import me.cometkaizo.graph.painter.TextPainter;
import me.cometkaizo.graph.shape.GraphShape;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class GraphPainter {

    private int prevGraphWidth;
    private int prevGraphHeight;

    private OutlinePainter axisPainter;
    private TextPainter axisNumberPainter;

    private final GraphState graphState;

    private Line2D xAxis;
    private Line2D yAxis;
    private OutlinePainter.Options axisOptions;
    private final List<TextPainter.PositionedString> xAxisNumbers = new ArrayList<>(13);
    private final List<TextPainter.PositionedString> yAxisNumbers = new ArrayList<>(7);
    private float axisNumberInterval;
    private TextPainter.Options xAxisNumberOptions;
    private TextPainter.Options yAxisNumberOptions;

    public GraphPainter(GraphState graphState, OutlinePainter.Options axisOptions, TextPainter.Options xAxisNumberOptions, TextPainter.Options yAxisNumberOptions, float axisNumberInterval) {
        this.graphState = graphState;
        this.axisOptions = axisOptions;
        this.xAxisNumberOptions = xAxisNumberOptions;
        this.yAxisNumberOptions = yAxisNumberOptions;
        this.axisNumberPainter = new TextPainter(graphState);
        this.axisNumberInterval = axisNumberInterval;
        this.axisPainter = new OutlinePainter(graphState, axisOptions);
        recalcAxisNumbers();
    }

    public void paint(Graphics2D graphics) {
        if (graphics == null) return;

        paintGraph(graphics);
        paintShapeHighlight(graphics);
        paintShapes(graphics);
        graphState.tool.paint(graphics);

        prevGraphWidth = graphState.width;
        prevGraphHeight = graphState.height;
    }

    private void paintShapeHighlight(Graphics2D graphics) {
        if (graphState.selectionBoxPainter != null) {
            Shape selectionBox = graphState.getSelectedShape().getSelectionBounds();
            graphState.selectionBoxPainter.draw(selectionBox, graphics);
        }
        if (graphState.hoverBoxPainter != null && graphState.selectedShape != graphState.hoveredShape) {
            Shape hoverBox = graphState.getHoveredShape().getSelectionBounds();
            graphState.hoverBoxPainter.draw(hoverBox, graphics);
        }
    }

    public void paintGraph(Graphics2D graphics) {
        paintAxis(graphics);
        paintNumbers(graphics);
    }

    private void paintNumbers(Graphics2D graphics) {
        updateAxisNumbers();

        for (TextPainter.PositionedString number : xAxisNumbers) {
            axisNumberPainter.draw(number, graphics, xAxisNumberOptions);
        }

        for (TextPainter.PositionedString number : yAxisNumbers) {
            axisNumberPainter.draw(number, graphics, yAxisNumberOptions);
        }

    }

    private void updateAxisNumbers() {
        float offset = axisOptions.lineStroke() == null ? 0 : axisOptions.lineStroke().getLineWidth() / 2 + 5;

        if (graphState.width != prevGraphWidth) {
            if (graphState.width > (xAxisNumbers.size() + 1) * axisNumberInterval) {
                int newNumber = (int) ((int) (xAxisNumbers.size() / 2F + 1) * axisNumberInterval);

                xAxisNumbers.add(new TextPainter.PositionedString(
                        String.valueOf(newNumber), newNumber, offset
                ));
                xAxisNumbers.add(0, new TextPainter.PositionedString(
                        String.valueOf(-newNumber), -newNumber, offset
                ));
            }
            if (graphState.width < (xAxisNumbers.size() - 1) * axisNumberInterval) {
                xAxisNumbers.remove(0);
                xAxisNumbers.remove(xAxisNumbers.size() - 1);
            }
        }

        if (graphState.height != prevGraphHeight) {
            if (graphState.height > (yAxisNumbers.size() + 1) * axisNumberInterval) {
                int newNumber = (int) ((int) (yAxisNumbers.size() / 2F + 1) * axisNumberInterval);
                yAxisNumbers.add(new TextPainter.PositionedString(
                        String.valueOf(newNumber), offset, newNumber
                ));
                yAxisNumbers.add(0, new TextPainter.PositionedString(
                        String.valueOf(-newNumber), offset, -newNumber
                ));
            }
            if (graphState.height < (yAxisNumbers.size() - 1) * axisNumberInterval) {
                yAxisNumbers.remove(0);
                yAxisNumbers.remove(yAxisNumbers.size() - 1);
            }
        }
    }

    protected void recalcAxisNumbers() {
        int halfWidth = graphState.width / 2;
        int halfHeight = graphState.height / 2;

        float offset = axisOptions.lineStroke() == null ? 0 : axisOptions.lineStroke().getLineWidth() / 2 + 5;
        for (int x = 0; x < halfWidth; x += axisNumberInterval) {
            xAxisNumbers.add(
                    new TextPainter.PositionedString(String.valueOf(x), x, offset)
            );
            if (x != 0) xAxisNumbers.add(0,
                    new TextPainter.PositionedString(String.valueOf(-x), -x, offset)
            );
        }

        for (int y = 0; y < halfHeight; y += axisNumberInterval) {
            yAxisNumbers.add(
                    new TextPainter.PositionedString(String.valueOf(y), offset, y)
            );
            if (y != 0) yAxisNumbers.add(0,
                    new TextPainter.PositionedString(String.valueOf(-y), offset, -y)
            );
        }
    }


    public void paintAxis(Graphics2D graphics) {
        if (prevGraphWidth != graphState.width || prevGraphHeight != graphState.height ||
                xAxis == null || yAxis == null) {
            float halfWidth = graphState.width / 2F;
            float halfHeight = graphState.height / 2F;

            xAxis = new Line2D.Float(-halfWidth, 0, halfWidth, 0);
            yAxis = new Line2D.Float(0, -halfHeight, 0, halfHeight);
        }

        axisPainter.draw(xAxis, graphics);
        axisPainter.draw(yAxis, graphics);
    }

    private void paintShapes(Graphics2D graphics) {
        for (GraphShape<?> shape : graphState.shapes) {
            shape.draw(graphics);
        }
    }

    public void setAxisPainter(OutlinePainter axisPainter) {
        this.axisPainter = axisPainter;
    }

    public void setAxisNumberInterval(float axisNumberInterval) {
        this.axisNumberInterval = axisNumberInterval;
        recalcAxisNumbers();
    }

    public void setAxisNumberPainter(TextPainter axisNumberPainter) {
        this.axisNumberPainter = axisNumberPainter;
    }

    public void setXAxisNumberOptions(TextPainter.Options xAxisNumberOptions) {
        this.xAxisNumberOptions = xAxisNumberOptions;
    }

    public void setYAxisNumberOptions(TextPainter.Options yAxisNumberOptions) {
        this.yAxisNumberOptions = yAxisNumberOptions;
    }

    public void setAxisOptions(OutlinePainter.Options axisOptions) {
        this.axisOptions = axisOptions;
    }
}
