/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

@Environment(value=EnvType.CLIENT)
public class ChatHudLine {
    private final int creationTick;
    private final StringRenderable text;
    private final int id;

    public ChatHudLine(int i, StringRenderable arg, int j) {
        this.text = arg;
        this.creationTick = i;
        this.id = j;
    }

    public StringRenderable getText() {
        return this.text;
    }

    public int getCreationTick() {
        return this.creationTick;
    }

    public int getId() {
        return this.id;
    }
}

