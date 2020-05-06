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
import net.minecraft.client.gui.screen.recipebook.SmokerRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SmokerScreen
extends AbstractFurnaceScreen<SmokerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/smoker.png");

    public SmokerScreen(SmokerScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, new SmokerRecipeBookScreen(), arg2, arg3, TEXTURE);
    }
}

