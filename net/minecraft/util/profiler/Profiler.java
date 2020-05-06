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
            public void push(String string) {
                arg.push(string);
                arg2.push(string);
            }

            @Override
            public void push(Supplier<String> supplier) {
                arg.push(supplier);
                arg2.push(supplier);
            }

            @Override
            public void pop() {
                arg.pop();
                arg2.pop();
            }

            @Override
            public void swap(String string) {
                arg.swap(string);
                arg2.swap(string);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public void swap(Supplier<String> supplier) {
                arg.swap(supplier);
                arg2.swap(supplier);
            }

            @Override
            public void visit(String string) {
                arg.visit(string);
                arg2.visit(string);
            }

            @Override
            public void visit(Supplier<String> supplier) {
                arg.visit(supplier);
                arg2.visit(supplier);
            }
        };
    }
}

