/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface MessageListener<Msg>
extends AutoCloseable {
    public String getName();

    public void send(Msg var1);

    @Override
    default public void close() {
    }

    default public <Source> CompletableFuture<Source> ask(Function<? super MessageListener<Source>, ? extends Msg> function) {
        CompletableFuture completableFuture = new CompletableFuture();
        Msg object = function.apply(MessageListener.create("ask future procesor handle", completableFuture::complete));
        this.send(object);
        return completableFuture;
    }

    public static <Msg> MessageListener<Msg> create(final String string, final Consumer<Msg> consumer) {
        return new MessageListener<Msg>(){

            @Override
            public String getName() {
                return string;
            }

            @Override
            public void send(Msg object) {
                consumer.accept(object);
            }

            public String toString() {
                return string;
            }
        };
    }
}

