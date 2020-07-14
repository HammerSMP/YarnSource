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
import net.minecraft.util.profiler.DummyProfiler;

public interface Profiler {
    public void startTick();

    public void endTick();

    public void push(String var1);

    public void push(Supplier<String> var1);

    public void pop();

    public void swap(String var1);

    @Environment(value=EnvType.CLIENT)
    public void swap(Supplier<String> var1);

    public void visit(String var1);

    public void visit(Supplier<String> var1);

    public static Profiler union(final Profiler arg, final Profiler arg2) {
        if (arg == DummyProfiler.INSTANCE) {
            return arg2;
        }
        if (arg2 == DummyProfiler.INSTANCE) {
            return arg;
        }
        return new Profiler(){

            @Override
            public void startTick() {
                arg.startTick();
                arg2.startTick();
            }

            @Override
            public void endTick() {
                arg.endTick();
                arg2.endTick();
            }

            @Override
            public void push(String location) {
                arg.push(location);
                arg2.push(location);
            }

            @Override
            public void push(Supplier<String> locationGetter) {
                arg.push(locationGetter);
                arg2.push(locationGetter);
            }

            @Override
            public void pop() {
                arg.pop();
                arg2.pop();
            }

            @Override
            public void swap(String location) {
                arg.swap(location);
                arg2.swap(location);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public void swap(Supplier<String> locationGetter) {
                arg.swap(locationGetter);
                arg2.swap(locationGetter);
            }

            @Override
            public void visit(String marker) {
                arg.visit(marker);
                arg2.visit(marker);
            }

            @Override
            public void visit(Supplier<String> markerGetter) {
                arg.visit(markerGetter);
                arg2.visit(markerGetter);
            }
        };
    }
}

