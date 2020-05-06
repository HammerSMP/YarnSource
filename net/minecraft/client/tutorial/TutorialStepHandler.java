/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.input.Input;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public interface TutorialStepHandler {
    default public void destroy() {
    }

    default public void tick() {
    }

    default public void onMovement(Input arg) {
    }

    default public void onMouseUpdate(double d, double e) {
    }

    default public void onTarget(ClientWorld arg, HitResult arg2) {
    }

    default public void onBlockAttacked(ClientWorld arg, BlockPos arg2, BlockState arg3, float f) {
    }

    default public void onInventoryOpened() {
    }

    default public void onSlotUpdate(ItemStack arg) {
    }
}

