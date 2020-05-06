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
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AdvancementTab
extends DrawableHelper {
    private final MinecraftClient client;
    private final AdvancementsScreen screen;
    private final AdvancementTabType type;
    private final int index;
    private final Advancement root;
    private final AdvancementDisplay display;
    private final ItemStack icon;
    private final Text title;
    private final AdvancementWidget rootWidget;
    private final Map<Advancement, AdvancementWidget> widgets = Maps.newLinkedHashMap();
    private double originX;
    private double originY;
    private int minPanX = Integer.MAX_VALUE;
    private int minPanY = Integer.MAX_VALUE;
    private int maxPanX = Integer.MIN_VALUE;
    private int maxPanY = Integer.MIN_VALUE;
    private float alpha;
    private boolean initialized;

    public AdvancementTab(MinecraftClient arg, AdvancementsScreen arg2, AdvancementTabType arg3, int i, Advancement arg4, AdvancementDisplay arg5) {
        this.client = arg;
        this.screen = arg2;
        this.type = arg3;
        this.index = i;
        this.root = arg4;
        this.display = arg5;
        this.icon = arg5.getIcon();
        this.title = arg5.getTitle();
        this.rootWidget = new AdvancementWidget(this, arg, arg4, arg5);
        this.addWidget(this.rootWidget, arg4);
    }

    public Advancement getRoot() {
        return this.root;
    }

    public Text getTitle() {
        return this.title;
    }

    public void drawBackground(MatrixStack arg, int i, int j, boolean bl) {
        this.type.drawBackground(arg, this, i, j, bl, this.index);
    }

    public void drawIcon(int i, int j, ItemRenderer arg) {
        this.type.drawIcon(i, j, this.index, arg, this.icon);
    }

    public void render(MatrixStack arg) {
        if (!this.initialized) {
            this.originX = 117 - (this.maxPanX + this.minPanX) / 2;
            this.originY = 56 - (this.maxPanY + this.minPanY) / 2;
            this.initialized = true;
        }
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0f, 0.0f, 950.0f);
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill(arg, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0f, 0.0f, -950.0f);
        RenderSystem.depthFunc(518);
        AdvancementTab.fill(arg, 234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        Identifier lv = this.display.getBackground();
        if (lv != null) {
            this.client.getTextureManager().bindTexture(lv);
        } else {
            this.client.getTextureManager().bindTexture(TextureManager.MISSING_IDENTIFIER);
        }
        int i = MathHelper.floor(this.originX);
        int j = MathHelper.floor(this.originY);
        int k = i % 16;
        int l = j % 16;
        for (int m = -1; m <= 15; ++m) {
            for (int n = -1; n <= 8; ++n) {
                AdvancementTab.drawTexture(arg, k + 16 * m, l + 16 * n, 0.0f, 0.0f, 16, 16, 16, 16);
            }
        }
        this.rootWidget.renderLines(arg, i, j, true);
        this.rootWidget.renderLines(arg, i, j, false);
        this.rootWidget.renderWidgets(arg, i, j);
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0f, 0.0f, -950.0f);
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill(arg, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0f, 0.0f, 950.0f);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();
    }

    public void drawWidgetTooltip(MatrixStack arg, int i, int j, int k, int l) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 200.0f);
        AdvancementTab.fill(arg, 0, 0, 234, 113, MathHelper.floor(this.alpha * 255.0f) << 24);
        boolean bl = false;
        int m = MathHelper.floor(this.originX);
        int n = MathHelper.floor(this.originY);
        if (i > 0 && i < 234 && j > 0 && j < 113) {
            for (AdvancementWidget lv : this.widgets.values()) {
                if (!lv.shouldRender(m, n, i, j)) continue;
                bl = true;
                lv.drawTooltip(arg, m, n, this.alpha, k, l);
                break;
            }
        }
        RenderSystem.popMatrix();
        this.alpha = bl ? MathHelper.clamp(this.alpha + 0.02f, 0.0f, 0.3f) : MathHelper.clamp(this.alpha - 0.04f, 0.0f, 1.0f);
    }

    public boolean isClickOnTab(int i, int j, double d, double e) {
        return this.type.isClickOnTab(i, j, this.index, d, e);
    }

    @Nullable
    public static AdvancementTab create(MinecraftClient arg, AdvancementsScreen arg2, int i, Advancement arg3) {
        if (arg3.getDisplay() == null) {
            return null;
        }
        for (AdvancementTabType lv : AdvancementTabType.values()) {
            if (i >= lv.getTabCount()) {
                i -= lv.getTabCount();
                continue;
            }
            return new AdvancementTab(arg, arg2, lv, i, arg3, arg3.getDisplay());
        }
        return null;
    }

    public void move(double d, double e) {
        if (this.maxPanX - this.minPanX > 234) {
            this.originX = MathHelper.clamp(this.originX + d, (double)(-(this.maxPanX - 234)), 0.0);
        }
        if (this.maxPanY - this.minPanY > 113) {
            this.originY = MathHelper.clamp(this.originY + e, (double)(-(this.maxPanY - 113)), 0.0);
        }
    }

    public void addAdvancement(Advancement arg) {
        if (arg.getDisplay() == null) {
            return;
        }
        AdvancementWidget lv = new AdvancementWidget(this, this.client, arg, arg.getDisplay());
        this.addWidget(lv, arg);
    }

    private void addWidget(AdvancementWidget arg, Advancement arg2) {
        this.widgets.put(arg2, arg);
        int i = arg.getX();
        int j = i + 28;
        int k = arg.getY();
        int l = k + 27;
        this.minPanX = Math.min(this.minPanX, i);
        this.maxPanX = Math.max(this.maxPanX, j);
        this.minPanY = Math.min(this.minPanY, k);
        this.maxPanY = Math.max(this.maxPanY, l);
        for (AdvancementWidget lv : this.widgets.values()) {
            lv.addToTree();
        }
    }

    @Nullable
    public AdvancementWidget getWidget(Advancement arg) {
        return this.widgets.get(arg);
    }

    public AdvancementsScreen getScreen() {
        return this.screen;
    }
}

