package me.cometkaizo.util;

public interface TriConsumer<T, U, O> {

    void accept(T t, U u, O o);

}
