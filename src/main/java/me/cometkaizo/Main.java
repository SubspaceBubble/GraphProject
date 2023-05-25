package me.cometkaizo;

import me.cometkaizo.window.GraphDriver;

public class Main {
    public static void main(String[] args) {
        GraphDriver driver = new GraphDriver(System.in);
        driver.start();
    }

    public static void log(String message) {
        System.out.println(message);
    }

    public static void err(String message) {
        System.err.println(message);
    }

}
