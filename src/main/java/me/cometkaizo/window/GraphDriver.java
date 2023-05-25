package me.cometkaizo.window;

import me.cometkaizo.system.driver.SystemDriver;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GraphDriver extends SystemDriver {

    private static final GraphApp app = new GraphApp();

    public GraphDriver(InputStream input) {
        super(app);

        addLoop(new Runnable() {
            private final Scanner scanner = new Scanner(input);
            @Override
            public void run() {
                if (scanner.hasNextLine()) {
                    app.parseInput(scanner.nextLine());
                }
            }
        }, 300, TimeUnit.MILLISECONDS);

        addLoop(app::tick, 1000 / 20, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void setup() {
        super.setup();
        app.setup();
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        app.cleanup();
    }
}
