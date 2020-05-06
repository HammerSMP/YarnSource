/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class GameOptionsScreen
extends Screen {
    protected final Screen parent;
    protected final GameOptions gameOptions;

    public GameOptionsScreen(Screen arg, GameOptions arg2, Text arg3) {
        super(arg3);
        this.parent = arg;
        this.gameOptions = arg2;
    }

    @Override
    public void removed() {
        this.client.options.write();
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }
}

