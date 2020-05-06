/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

public interface SynchronousResourceReloadListener
extends ResourceReloadListener {
    @Override
    default public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg, ResourceManager arg2, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        return arg.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> this.apply(arg2), executor2);
    }

    public void apply(ResourceManager var1);
}

