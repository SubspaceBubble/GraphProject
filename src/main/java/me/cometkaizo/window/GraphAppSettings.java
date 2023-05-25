package me.cometkaizo.window;

import me.cometkaizo.animation.Ease;
import me.cometkaizo.animation.SimpleEaseInOut;
import me.cometkaizo.graph.painter.OutlinePainter;
import me.cometkaizo.graph.painter.TextPainter;
import me.cometkaizo.system.app.AppSettings;

import java.awt.*;

public class GraphAppSettings extends AppSettings {
    public String name = "Graph App Thingy";
    public int defaultWidth = 1280;
    public int defaultHeight = 720;

    public OutlinePainter.Options defaultAxisOptions = new OutlinePainter.Options(Color.WHITE, new BasicStroke(4), null, null);
    public float axisNumberInterval = 100;
    public TextPainter.Options xAxisNumberOptions = TextPainter.Options.DEFAULT_CENTERED;
    public TextPainter.Options yAxisNumberOptions = new TextPainter.Options(new Font("Monospaced", Font.PLAIN, 10), width -> 0, height -> -(height / 2));

    public OutlinePainter.Options defaultDrawToolOptions = new OutlinePainter.Options(Color.LIGHT_GRAY, new BasicStroke(3), Color.GREEN, new BasicStroke(5));
    public OutlinePainter.Options defaultAltDrawToolOptions = new OutlinePainter.Options(Color.LIGHT_GRAY, new BasicStroke(3), Color.RED, new BasicStroke(5));
    public OutlinePainter.Options defaultToolPreviewOptions = new OutlinePainter.Options(Color.GRAY, new BasicStroke(3), new Color(0, 255, 0, 100), new BasicStroke(5));
    public OutlinePainter.Options selectionBoxOptions = new OutlinePainter.Options(new Color(255, 255, 255, 150), new BasicStroke(9), new Color(0, 0, 255), new BasicStroke(7));
    public OutlinePainter.Options hoverBoxOptions = new OutlinePainter.Options(new Color(255, 255, 255, 50), new BasicStroke(7), new Color(255, 255, 0), new BasicStroke(5));
    public float defaultToolSnapDistance = 15;
    public OutlinePainter.Options defaultTransformLineOptions = new OutlinePainter.Options(Color.LIGHT_GRAY, new BasicStroke(3), null, null);
    public float defaultToolHandleGrabDistance = 15;
    public double defaultToolAngleSnap = 22.5F;
    public Ease defaultToolEase = SimpleEaseInOut.CUBIC;
    public OutlinePainter.Options defaultToolTransformOptions = new OutlinePainter.Options(new Color(128, 128, 128, 150), new BasicStroke(3), null, null);
}
