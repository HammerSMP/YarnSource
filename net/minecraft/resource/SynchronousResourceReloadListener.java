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
    default public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> {
            applyProfiler.startTick();
            applyProfiler.push("listener");
            this.apply(manager);
            applyProfiler.pop();
            applyProfiler.endTick();
        }, applyExecutor);
    }

    public void apply(ResourceManager var1);
}

