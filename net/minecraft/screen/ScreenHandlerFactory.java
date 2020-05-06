/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.screen;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

@FunctionalInterface
public interface ScreenHandlerFactory {
    @Nullable
    public ScreenHandler createMenu(int var1, PlayerInventory var2, PlayerEntity var3);
}

