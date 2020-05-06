/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

public abstract class SinglePreparationResourceReloadListener<T>
implements ResourceReloadListener {
    @Override
    public final CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg, ResourceManager arg2, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> this.prepare(arg2, arg3), executor).thenCompose(arg::whenPrepared)).thenAcceptAsync(object -> this.apply(object, arg2, arg4), executor2);
    }

    protected abstract T prepare(ResourceManager var1, Profiler var2);

    protected abstract void apply(T var1, ResourceManager var2, Profiler var3);
}

