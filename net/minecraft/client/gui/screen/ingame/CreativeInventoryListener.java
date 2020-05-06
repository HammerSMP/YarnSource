/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class CreativeInventoryListener
implements ScreenHandlerListener {
    private final MinecraftClient client;

    public CreativeInventoryListener(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void onHandlerRegistered(ScreenHandler arg, DefaultedList<ItemStack> arg2) {
    }

    @Override
    public void onSlotUpdate(ScreenHandler arg, int i, ItemStack arg2) {
        this.client.interactionManager.clickCreativeStack(arg2, i);
    }

    @Override
    public void onPropertyUpdate(ScreenHandler arg, int i, int j) {
    }
}

