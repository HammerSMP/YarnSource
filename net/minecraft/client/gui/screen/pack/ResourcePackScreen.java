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
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ResourcePackScreen
extends AbstractPackScreen {
    public ResourcePackScreen(Screen arg, ResourcePackManager<ClientResourcePackProfile> arg2, Consumer<ResourcePackManager<ClientResourcePackProfile>> consumer, File file) {
        super(arg, new TranslatableText("resourcePack.title"), (Runnable runnable) -> new ResourcePackOrganizer<ClientResourcePackProfile>((Runnable)runnable, ClientResourcePackProfile::drawIcon, arg2, consumer), file);
    }
}

