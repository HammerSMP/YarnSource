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
import net.minecraft.class_5348;

@Environment(value=EnvType.CLIENT)
public class ChatHudLine {
    private final int creationTick;
    private final class_5348 text;
    private final int id;

    public ChatHudLine(int i, class_5348 arg, int j) {
        this.text = arg;
        this.creationTick = i;
        this.id = j;
    }

    public class_5348 getText() {
        return this.text;
    }

    public int getCreationTick() {
        return this.creationTick;
    }

    public int getId() {
        return this.id;
    }
}

