/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import java.io.File;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.AbstractPackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DataPackScreen
extends AbstractPackScreen {
    private static final Identifier UNKNOWN_PACK_TEXTURE = new Identifier("textures/misc/unknown_pack.png");

    public DataPackScreen(Screen arg, ResourcePackManager<ResourcePackProfile> arg2, Consumer<ResourcePackManager<ResourcePackProfile>> consumer, File file) {
        super(arg, new TranslatableText("dataPack.title"), (Runnable runnable) -> new ResourcePackOrganizer<ResourcePackProfile>((Runnable)runnable, (arg, arg2) -> arg2.bindTexture(UNKNOWN_PACK_TEXTURE), arg2, consumer), file);
    }
}

