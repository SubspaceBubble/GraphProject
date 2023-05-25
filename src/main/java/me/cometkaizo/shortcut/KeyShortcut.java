package me.cometkaizo.shortcut;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyShortcut implements KeyListener {

    private final Map<Integer, Boolean> keys;
    private final Runnable task;

    public KeyShortcut(Runnable task, int... keys) {
        this.task = task;
        this.keys = new HashMap<>(keys.length);

        for (int key : keys) {
            this.keys.put(key, false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.computeIfPresent(e.getKeyCode(), (_i, _p) -> true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (boolean keyPressed : keys.values()) {
            if (!keyPressed) return;
        }
        task.run();

        keys.computeIfPresent(e.getKeyCode(), (_i, _p) -> false);
    }
}
