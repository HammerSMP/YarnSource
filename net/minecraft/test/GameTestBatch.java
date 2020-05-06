/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestFunction;

public class GameTestBatch {
    private final String id;
    private final Collection<TestFunction> testFunctions;
    @Nullable
    private final Consumer<ServerWorld> worldSetter;

    public GameTestBatch(String string, Collection<TestFunction> collection, @Nullable Consumer<ServerWorld> consumer) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
        }
        this.id = string;
        this.testFunctions = collection;
        this.worldSetter = consumer;
    }

    public String getId() {
        return this.id;
    }

    public Collection<TestFunction> getTestFunctions() {
        return this.testFunctions;
    }

    public void setWorld(ServerWorld arg) {
        if (this.worldSetter != null) {
            this.worldSetter.accept(arg);
        }
    }
}

