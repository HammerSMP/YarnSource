/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.resourcepack;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class ResourcePackOptionsScreen
extends GameOptionsScreen {
    private AvailableResourcePackListWidget availablePacks;
    private SelectedResourcePackListWidget enabledPacks;
    private boolean dirty;

    public ResourcePackOptionsScreen(Screen arg, GameOptions arg2) {
        super(arg, arg2, new TranslatableText("resourcePack.title"));
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, new TranslatableText("resourcePack.openFolder"), arg -> Util.getOperatingSystem().open(this.client.getResourcePackDir())));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, ScreenTexts.DONE, arg -> {
            if (this.dirty) {
                ArrayList list = Lists.newArrayList();
                for (ResourcePackListWidget.ResourcePackEntry lv : this.enabledPacks.children()) {
                    list.add(lv.getPack());
                }
                Collections.reverse(list);
                this.client.getResourcePackManager().setEnabledProfiles(list);
                this.gameOptions.resourcePacks.clear();
                this.gameOptions.incompatibleResourcePacks.clear();
                for (ClientResourcePackProfile lv2 : list) {
                    if (lv2.isPinned()) continue;
                    this.gameOptions.resourcePacks.add(lv2.getName());
                    if (lv2.getCompatibility().isCompatible()) continue;
                    this.gameOptions.incompatibleResourcePacks.add(lv2.getName());
                }
                this.gameOptions.write();
                this.client.openScreen(this.parent);
                this.client.reloadResources();
            } else {
                this.client.openScreen(this.parent);
            }
        }));
        AvailableResourcePackListWidget lv = this.availablePacks;
        SelectedResourcePackListWidget lv2 = this.enabledPacks;
        this.availablePacks = new AvailableResourcePackListWidget(this.client, 200, this.height);
        this.availablePacks.setLeftPos(this.width / 2 - 4 - 200);
        if (lv != null) {
            this.availablePacks.children().addAll(lv.children());
        }
        this.children.add(this.availablePacks);
        this.enabledPacks = new SelectedResourcePackListWidget(this.client, 200, this.height);
        this.enabledPacks.setLeftPos(this.width / 2 + 4);
        if (lv2 != null) {
            lv2.children().forEach(arg -> {
                this.enabledPacks.children().add((ResourcePackListWidget.ResourcePackEntry)arg);
                arg.method_24232(this.enabledPacks);
            });
        }
        this.children.add(this.enabledPacks);
        if (!this.dirty) {
            this.availablePacks.children().clear();
            this.enabledPacks.children().clear();
            ResourcePackManager<ClientResourcePackProfile> lv3 = this.client.getResourcePackManager();
            lv3.scanPacks();
            ArrayList list = Lists.newArrayList(lv3.getProfiles());
            list.removeAll(lv3.getEnabledProfiles());
            for (ClientResourcePackProfile lv4 : list) {
                this.availablePacks.add(new ResourcePackListWidget.ResourcePackEntry(this.availablePacks, this, lv4));
            }
            for (ClientResourcePackProfile lv5 : Lists.reverse((List)Lists.newArrayList(lv3.getEnabledProfiles()))) {
                this.enabledPacks.add(new ResourcePackListWidget.ResourcePackEntry(this.enabledPacks, this, lv5));
            }
        }
    }

    public void enable(ResourcePackListWidget.ResourcePackEntry arg) {
        this.availablePacks.children().remove(arg);
        arg.enable(this.enabledPacks);
        this.markDirty();
    }

    public void disable(ResourcePackListWidget.ResourcePackEntry arg) {
        this.enabledPacks.children().remove(arg);
        this.availablePacks.add(arg);
        this.markDirty();
    }

    public boolean isEnabled(ResourcePackListWidget.ResourcePackEntry arg) {
        return this.enabledPacks.children().contains(arg);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderDirtBackground(0);
        this.availablePacks.render(arg, i, j, f);
        this.enabledPacks.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("resourcePack.folderInfo", new Object[0]), this.width / 2 - 77, this.height - 26, 0x808080);
        super.render(arg, i, j, f);
    }

    public void markDirty() {
        this.dirty = true;
    }
}

