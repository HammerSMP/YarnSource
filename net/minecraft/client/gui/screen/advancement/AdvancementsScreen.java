/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AdvancementsScreen
extends Screen
implements ClientAdvancementManager.Listener {
    private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
    private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
    private final ClientAdvancementManager advancementHandler;
    private final Map<Advancement, AdvancementTab> tabs = Maps.newLinkedHashMap();
    private AdvancementTab selectedTab;
    private boolean movingTab;

    public AdvancementsScreen(ClientAdvancementManager arg) {
        super(NarratorManager.EMPTY);
        this.advancementHandler = arg;
    }

    @Override
    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;
        this.advancementHandler.setListener(this);
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.advancementHandler.selectTab(this.tabs.values().iterator().next().getRoot(), true);
        } else {
            this.advancementHandler.selectTab(this.selectedTab == null ? null : this.selectedTab.getRoot(), true);
        }
    }

    @Override
    public void removed() {
        this.advancementHandler.setListener(null);
        ClientPlayNetworkHandler lv = this.client.getNetworkHandler();
        if (lv != null) {
            lv.sendPacket(AdvancementTabC2SPacket.close());
        }
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (i == 0) {
            int j = (this.width - 252) / 2;
            int k = (this.height - 140) / 2;
            for (AdvancementTab lv : this.tabs.values()) {
                if (!lv.isClickOnTab(j, k, d, e)) continue;
                this.advancementHandler.selectTab(lv.getRoot(), true);
                break;
            }
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.client.options.keyAdvancements.matchesKey(i, j)) {
            this.client.openScreen(null);
            this.client.mouse.lockCursor();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        int k = (this.width - 252) / 2;
        int l = (this.height - 140) / 2;
        this.renderBackground(arg);
        this.drawAdvancementTree(arg, i, j, k, l);
        this.drawWidgets(arg, k, l);
        this.drawWidgetTooltip(arg, i, j, k, l);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (i != 0) {
            this.movingTab = false;
            return false;
        }
        if (!this.movingTab) {
            this.movingTab = true;
        } else if (this.selectedTab != null) {
            this.selectedTab.move(f, g);
        }
        return true;
    }

    private void drawAdvancementTree(MatrixStack arg, int i, int j, int k, int l) {
        AdvancementTab lv = this.selectedTab;
        if (lv == null) {
            AdvancementsScreen.fill(arg, k + 9, l + 18, k + 9 + 234, l + 18 + 113, -16777216);
            String string = I18n.translate("advancements.empty", new Object[0]);
            int m = this.textRenderer.getWidth(string);
            this.textRenderer.getClass();
            this.textRenderer.draw(arg, string, (float)(k + 9 + 117 - m / 2), (float)(l + 18 + 56 - 9 / 2), -1);
            this.textRenderer.getClass();
            this.textRenderer.draw(arg, ":(", (float)(k + 9 + 117 - this.textRenderer.getWidth(":(") / 2), (float)(l + 18 + 113 - 9), -1);
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(k + 9, l + 18, 0.0f);
        lv.render(arg);
        RenderSystem.popMatrix();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
    }

    public void drawWidgets(MatrixStack arg, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        this.client.getTextureManager().bindTexture(WINDOW_TEXTURE);
        this.drawTexture(arg, i, j, 0, 0, 252, 140);
        if (this.tabs.size() > 1) {
            this.client.getTextureManager().bindTexture(TABS_TEXTURE);
            for (AdvancementTab lv : this.tabs.values()) {
                lv.drawBackground(arg, i, j, lv == this.selectedTab);
            }
            RenderSystem.enableRescaleNormal();
            RenderSystem.defaultBlendFunc();
            for (AdvancementTab lv2 : this.tabs.values()) {
                lv2.drawIcon(i, j, this.itemRenderer);
            }
            RenderSystem.disableBlend();
        }
        this.textRenderer.draw(arg, I18n.translate("gui.advancements", new Object[0]), (float)(i + 8), (float)(j + 6), 0x404040);
    }

    private void drawWidgetTooltip(MatrixStack arg, int i, int j, int k, int l) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.selectedTab != null) {
            RenderSystem.pushMatrix();
            RenderSystem.enableDepthTest();
            RenderSystem.translatef(k + 9, l + 18, 400.0f);
            this.selectedTab.drawWidgetTooltip(arg, i - k - 9, j - l - 18, k, l);
            RenderSystem.disableDepthTest();
            RenderSystem.popMatrix();
        }
        if (this.tabs.size() > 1) {
            for (AdvancementTab lv : this.tabs.values()) {
                if (!lv.isClickOnTab(k, l, i, j)) continue;
                this.renderTooltip(arg, lv.getTitle(), i, j);
            }
        }
    }

    @Override
    public void onRootAdded(Advancement arg) {
        AdvancementTab lv = AdvancementTab.create(this.client, this, this.tabs.size(), arg);
        if (lv == null) {
            return;
        }
        this.tabs.put(arg, lv);
    }

    @Override
    public void onRootRemoved(Advancement arg) {
    }

    @Override
    public void onDependentAdded(Advancement arg) {
        AdvancementTab lv = this.getTab(arg);
        if (lv != null) {
            lv.addAdvancement(arg);
        }
    }

    @Override
    public void onDependentRemoved(Advancement arg) {
    }

    @Override
    public void setProgress(Advancement arg, AdvancementProgress arg2) {
        AdvancementWidget lv = this.getAdvancementWidget(arg);
        if (lv != null) {
            lv.setProgress(arg2);
        }
    }

    @Override
    public void selectTab(@Nullable Advancement arg) {
        this.selectedTab = this.tabs.get(arg);
    }

    @Override
    public void onClear() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public AdvancementWidget getAdvancementWidget(Advancement arg) {
        AdvancementTab lv = this.getTab(arg);
        return lv == null ? null : lv.getWidget(arg);
    }

    @Nullable
    private AdvancementTab getTab(Advancement arg) {
        while (arg.getParent() != null) {
            arg = arg.getParent();
        }
        return this.tabs.get(arg);
    }
}

