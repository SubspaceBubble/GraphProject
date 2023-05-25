package me.cometkaizo.graph.tool;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface Tool extends MouseListener, MouseMotionListener, KeyListener {

    void activate();
    void deactivate();

    void paint(Graphics2D graphics);

    String getName();
    String toPrettyString();

}
