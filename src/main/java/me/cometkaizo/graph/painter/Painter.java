package me.cometkaizo.graph.painter;

import java.awt.*;

public interface Painter<S, O> {

    void draw(S object, Graphics2D graphics, O options);
    void draw(S object, Graphics2D graphics);

}
