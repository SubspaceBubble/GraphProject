package me.cometkaizo.window;

import me.cometkaizo.graph.*;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {

    private final GraphPainter painter;

    public GraphPanel(Dimension size, GraphPainter painter) {

        setPreferredSize(size);
        setBackground(Color.BLACK);

        this.painter = painter;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        painter.paint((Graphics2D) g);
        g.dispose();
    }
}
