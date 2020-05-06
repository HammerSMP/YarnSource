/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class AsyncTexture
extends ResourceTexture {
    @Nullable
    private CompletableFuture<ResourceTexture.TextureData> future;

    public AsyncTexture(ResourceManager arg, Identifier arg2, Executor executor) {
        super(arg2);
        this.future = CompletableFuture.supplyAsync(() -> ResourceTexture.TextureData.load(arg, arg2), executor);
    }

    @Override
    protected ResourceTexture.TextureData loadTextureData(ResourceManager arg) {
        if (this.future != null) {
            ResourceTexture.TextureData lv = this.future.join();
            this.future = null;
            return lv;
        }
        return ResourceTexture.TextureData.load(arg, this.location);
    }

    public CompletableFuture<Void> getLoadCompleteFuture() {
        return this.future == null ? CompletableFuture.completedFuture(null) : this.future.thenApply(arg -> null);
    }

    @Override
    public void registerTexture(TextureManager arg, ResourceManager arg2, Identifier arg3, Executor executor) {
        this.future = CompletableFuture.supplyAsync(() -> ResourceTexture.TextureData.load(arg2, this.location), Util.getServerWorkerExecutor());
        this.future.thenRunAsync(() -> arg.registerTexture(this.location, this), AsyncTexture.method_22808(executor));
    }

    private static Executor method_22808(Executor executor) {
        return runnable -> executor.execute(() -> RenderSystem.recordRenderCall(runnable::run));
    }
}

