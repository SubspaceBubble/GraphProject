package me.cometkaizo.graph;

import me.cometkaizo.graph.painter.OutlinePainter;
import me.cometkaizo.graph.painter.Painter;
import me.cometkaizo.graph.shape.GraphShape;
import me.cometkaizo.graph.shape.GraphShapeFactory;
import me.cometkaizo.graph.shape.SimpleGraphShapeFactory;
import me.cometkaizo.graph.tool.*;
import me.cometkaizo.window.GraphAppSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphState {
    public GraphAppSettings settings;

    public int width = 1280;
    public int height = 720;
    public int prevWidth = width;
    public int prevHeight = height;
    public boolean sizeChanged = true;

    public GraphShapeFactory shapeFactory = new SimpleGraphShapeFactory(this);
    public List<GraphShape<?>> shapes = new ArrayList<>(2);

    public OutlinePainter selectionBoxPainter;
    public int selectedShape = 0;
    public OutlinePainter hoverBoxPainter;
    public int hoveredShape = 0;

    public ToolList tools;
    public Tool tool;


    public GraphState(GraphAppSettings settings) {
        this.settings = settings;
        tools = new ToolList(
                new PolyLineTool(this,
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        new OutlinePainter(this, settings.defaultToolPreviewOptions),
                        new OutlinePainter(this, settings.defaultToolTransformOptions),
                        settings.defaultToolSnapDistance),
                new RectangleTool(this,
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        new OutlinePainter(this, settings.defaultToolPreviewOptions),
                        new OutlinePainter(this, settings.defaultToolTransformOptions)),
                new EllipseTool(this,
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        new OutlinePainter(this, settings.defaultToolPreviewOptions),
                        new OutlinePainter(this, settings.defaultToolTransformOptions)),
                new MirrorTool(this,
                        settings.defaultToolEase,
                        new OutlinePainter(this, settings.defaultTransformLineOptions),
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolAngleSnap),
                new RotateTool(this,
                        new OutlinePainter(this, settings.defaultTransformLineOptions),
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolAngleSnap),
                new TranslateTool(this,
                        new OutlinePainter(this, settings.defaultTransformLineOptions),
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolAngleSnap),
                new DilateTool(this,
                        new OutlinePainter(this, settings.defaultTransformLineOptions),
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        new OutlinePainter(this, settings.defaultDrawToolOptions),
                        new OutlinePainter(this, settings.defaultAltDrawToolOptions),
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolHandleGrabDistance,
                        settings.defaultToolAngleSnap),
                new SelectTool(this)
        );

        setTool(PolyLineTool.class);
    }

    public void update(GraphAppSettings settings) {
        this.settings = settings;

        updateHoverBoxPainter();
        updateSelectionPainter();
    }

    public <S extends Shape> void addShape(S shape, Painter<? super S, ?> painter, Painter<? super Shape, ?> transformPainter) {
        shapes.add(shapeFactory.create(shape, painter, transformPainter));
    }

    public void clearShapes() {
        shapes.clear();

        selectedShape = 0;
        hoveredShape = 0;
        hoverBoxPainter = null;
        selectionBoxPainter = null;
    }

    public void setSelectedShape(int index) {
        selectedShape = index;
        updateSelectionPainter();
    }

    public void updateSelectionPainter() {
        if (selectedShape >= 0 && selectedShape < shapes.size()) {
            selectionBoxPainter = new OutlinePainter(this, settings.selectionBoxOptions);
        } else {
            selectionBoxPainter = null;
        }
    }

    public void setHoveredShape(int index) {
        hoveredShape = index;
        updateHoverBoxPainter();
    }

    public void updateHoverBoxPainter() {
        if (hoveredShape >= 0 && hoveredShape < shapes.size()) {
            hoverBoxPainter = new OutlinePainter(this, settings.hoverBoxOptions);
        } else {
            hoverBoxPainter = null;
        }
    }

    public boolean setTool(String toolName) {
        Tool newTool = tools.get(toolName);
        if (newTool != null) {
            if (tool != null) tool.deactivate();
            tool = newTool;
            tool.activate();
        }
        return newTool != null;
    }

    public boolean setTool(Class<? extends Tool> type) {
        Tool newTool = tools.get(type);
        if (newTool != null) {
            if (tool != null) tool.deactivate();
            tool = newTool;
            tool.activate();
        }
        return newTool != null;
    }

    public boolean hasShape(int index) {
        return index >= 0 && index < shapes.size();
    }

    public GraphShape<?> getShape(int index) {
        return hasShape(index) ? shapes.get(index) : null;
    }

    public GraphShape<?> getSelectedShape() {
        return getShape(selectedShape);
    }

    public GraphShape<?> getHoveredShape() {
        return getShape(hoveredShape);
    }
}
