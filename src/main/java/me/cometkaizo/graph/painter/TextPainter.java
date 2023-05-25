package me.cometkaizo.graph.painter;

import me.cometkaizo.graph.GraphState;
import me.cometkaizo.util.GraphUtils;

import java.awt.*;
import java.util.function.Function;

public class TextPainter extends AbstractPainter<TextPainter.PositionedString, TextPainter.Options> {
    public TextPainter(GraphState graphState, Options defaultOptions) {
        super(graphState, defaultOptions);
    }
    public TextPainter(GraphState graphState) {
        super(graphState, Options.DEFAULT);
    }

    @Override
    public void draw(PositionedString text, Graphics2D graphics, Options options) {
        FontMetrics metrics = graphics.getFontMetrics(options.font);

        float x = text.x + options.xOffset.apply(metrics.stringWidth(text.text));
        float y = text.y + options.yOffset.apply(metrics.getHeight()) + metrics.getAscent();

        draw(text, options.font, graphics, x, y);
    }

    private void draw(PositionedString text, Font font, Graphics2D graphics, float x, float y) {
        if (font != null) graphics.setFont(font);

        graphics.drawString(text.text,
                GraphUtils.toScreenX((int) x, graphState.width / 2),
                GraphUtils.toScreenY((int) y, graphState.height / 2));
    }

    public record PositionedString(String text, float x, float y) {}

    public record Options(Font font, Function<Integer, Integer> xOffset, Function<Integer, Integer> yOffset) {
        public static final Options DEFAULT = new Options(new Font("Monospaced", Font.PLAIN, 10), width -> 0, height -> 0);
        public static final Options DEFAULT_CENTERED = centered(DEFAULT);

        public static Options centered(Font font) {
            return new Options(font, width -> -(width / 2), height -> -(height / 2));
        }
        public static Options centered(Options options) {
            return centered(options.font);
        }
    }
}
