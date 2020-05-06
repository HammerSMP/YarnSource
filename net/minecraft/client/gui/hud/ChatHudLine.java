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
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatHudLine {
    private final int creationTick;
    private final Text text;
    private final int id;

    public ChatHudLine(int i, Text arg, int j) {
        this.text = arg;
        this.creationTick = i;
        this.id = j;
    }

    public Text getText() {
        return this.text;
    }

    public int getCreationTick() {
        return this.creationTick;
    }

    public int getId() {
        return this.id;
    }
}

