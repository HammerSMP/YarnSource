/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.resourcepack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SelectedResourcePackListWidget
extends ResourcePackListWidget {
    public SelectedResourcePackListWidget(MinecraftClient arg, int i, int j) {
        super(arg, i, j, new TranslatableText("resourcePack.selected.title"));
    }
}

