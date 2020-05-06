/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.world;

import java.util.concurrent.Executor;
import net.minecraft.class_5268;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

public class SecondaryServerWorld
extends ServerWorld {
    public SecondaryServerWorld(ServerWorld arg, class_5268 arg2, MinecraftServer minecraftServer, Executor executor, LevelStorage.Session arg3, DimensionType arg4, WorldGenerationProgressListener arg5) {
        super(minecraftServer, executor, arg3, new UnmodifiableLevelProperties(arg4, minecraftServer.method_27728(), arg2), arg4, arg5);
        arg.getWorldBorder().addListener(new WorldBorderListener.WorldBorderSyncer(this.getWorldBorder()));
    }

    @Override
    protected void tickTime() {
    }
}

