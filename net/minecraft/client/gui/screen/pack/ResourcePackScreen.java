/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.AbstractPackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ResourcePackScreen
extends AbstractPackScreen {
    public ResourcePackScreen(Screen arg, GameOptions arg2, ResourcePackManager<ClientResourcePackProfile> arg3, Runnable runnable) {
        super(arg, new TranslatableText("resourcePack.title"), (Runnable runnable2) -> {
            arg3.scanPacks();
            ArrayList list3 = Lists.newArrayList(arg3.getEnabledProfiles());
            ArrayList list22 = Lists.newArrayList(arg3.getProfiles());
            list22.removeAll(list3);
            return new ResourcePackOrganizer<ClientResourcePackProfile>((Runnable)runnable2, ClientResourcePackProfile::drawIcon, Lists.reverse((List)list3), list22, (list, list2, bl) -> {
                List list3 = Lists.reverse((List)list);
                List list4 = (List)list3.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
                arg3.setEnabledProfiles(list4);
                arg2.resourcePacks.clear();
                arg2.incompatibleResourcePacks.clear();
                for (ClientResourcePackProfile lv : list3) {
                    if (lv.isPinned()) continue;
                    arg2.resourcePacks.add(lv.getName());
                    if (lv.getCompatibility().isCompatible()) continue;
                    arg2.incompatibleResourcePacks.add(lv.getName());
                }
                arg2.write();
                if (!bl) {
                    runnable.run();
                }
            });
        }, MinecraftClient::getResourcePackDir);
    }
}

