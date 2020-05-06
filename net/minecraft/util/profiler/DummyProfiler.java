/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.EmptyProfileResult;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ReadableProfiler;

public class DummyProfiler
implements ReadableProfiler {
    public static final DummyProfiler INSTANCE = new DummyProfiler();

    private DummyProfiler() {
    }

    @Override
    public void startTick() {
    }

    @Override
    public void endTick() {
    }

    @Override
    public void push(String string) {
    }

    @Override
    public void push(Supplier<String> supplier) {
    }

    @Override
    public void pop() {
    }

    @Override
    public void swap(String string) {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void swap(Supplier<String> supplier) {
    }

    @Override
    public void visit(String string) {
    }

    @Override
    public void visit(Supplier<String> supplier) {
    }

    @Override
    public ProfileResult getResult() {
        return EmptyProfileResult.INSTANCE;
    }
}

