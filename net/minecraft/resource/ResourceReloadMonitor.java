/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Unit;

public interface ResourceReloadMonitor {
    public CompletableFuture<Unit> whenComplete();

    @Environment(value=EnvType.CLIENT)
    public float getProgress();

    @Environment(value=EnvType.CLIENT)
    public boolean isPrepareStageComplete();

    @Environment(value=EnvType.CLIENT)
    public boolean isApplyStageComplete();

    @Environment(value=EnvType.CLIENT)
    public void throwExceptions();
}

