/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;

public interface ReloadableResourceManager
extends ResourceManager,
AutoCloseable {
    default public CompletableFuture<Unit> beginReload(Executor executor, Executor executor2, List<ResourcePack> list, CompletableFuture<Unit> completableFuture) {
        return this.beginMonitoredReload(executor, executor2, completableFuture, list).whenComplete();
    }

    public ResourceReloadMonitor beginMonitoredReload(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<ResourcePack> var4);

    public void registerListener(ResourceReloadListener var1);

    @Override
    public void close();
}

