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
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.BlastFurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BlastFurnaceScreen
extends AbstractFurnaceScreen<BlastFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/blast_furnace.png");

    public BlastFurnaceScreen(BlastFurnaceScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, new BlastFurnaceRecipeBookScreen(), arg2, arg3, TEXTURE);
    }
}

