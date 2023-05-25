package me.cometkaizo.graph;

public enum Axis2D {
    X,
    Y;

    public Axis2D opposite() {
        if (this == X) return Y;
        return X;
    }
}
