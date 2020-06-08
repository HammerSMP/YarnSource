/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PackListWidget
extends AlwaysSelectedEntryListWidget<ResourcePackEntry> {
    private static final Identifier RESOURCE_PACKS_TEXTURE = new Identifier("textures/gui/resource_packs.png");
    private static final Text INCOMPATIBLE = new TranslatableText("pack.incompatible");
    private static final Text INCOMPATIBLE_CONFIRM = new TranslatableText("pack.incompatible.confirm.title");
    private final Text title;

    public PackListWidget(MinecraftClient arg, int i, int j, Text arg2) {
        super(arg, i, j, 32, j - 55 + 4, 36);
        this.title = arg2;
        this.centerListVertically = false;
        arg.textRenderer.getClass();
        this.setRenderHeader(true, (int)(9.0f * 1.5f));
    }

    @Override
    protected void renderHeader(MatrixStack arg, int i, int j, Tessellator arg2) {
        MutableText lv = new LiteralText("").append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
        this.client.textRenderer.draw(arg, lv, (float)(i + this.width / 2 - this.client.textRenderer.getWidth(lv) / 2), (float)Math.min(this.top + 3, j), 0xFFFFFF);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.right - 6;
    }

    @Environment(value=EnvType.CLIENT)
    public static class ResourcePackEntry
    extends AlwaysSelectedEntryListWidget.Entry<ResourcePackEntry> {
        private PackListWidget widget;
        protected final MinecraftClient client;
        protected final Screen screen;
        private final ResourcePackOrganizer.Pack pack;

        public ResourcePackEntry(MinecraftClient arg, PackListWidget arg2, Screen arg3, ResourcePackOrganizer.Pack arg4) {
            this.client = arg;
            this.screen = arg3;
            this.pack = arg4;
            this.widget = arg2;
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            int r;
            ResourcePackCompatibility lv = this.pack.getCompatibility();
            if (!lv.isCompatible()) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.fill(arg, k - 1, j - 1, k + l - 9, j + m + 1, -8978432);
            }
            this.pack.render(this.client.getTextureManager());
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(arg, k, j, 0.0f, 0.0f, 32, 32, 32, 32);
            Text lv2 = this.pack.getDisplayName();
            StringRenderable lv3 = this.pack.getDecoratedDescription();
            if (this.isSelectable() && (this.client.options.touchscreen || bl)) {
                this.client.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
                DrawableHelper.fill(arg, k, j, k + 32, j + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int p = n - k;
                int q = o - j;
                if (!lv.isCompatible()) {
                    lv2 = INCOMPATIBLE;
                    lv3 = lv.getNotification();
                }
                if (this.pack.canBeEnabled()) {
                    if (p < 32) {
                        DrawableHelper.drawTexture(arg, k, j, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(arg, k, j, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.pack.canBeDisabled()) {
                        if (p < 16) {
                            DrawableHelper.drawTexture(arg, k, j, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(arg, k, j, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveTowardStart()) {
                        if (p < 32 && p > 16 && q < 16) {
                            DrawableHelper.drawTexture(arg, k, j, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(arg, k, j, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveTowardEnd()) {
                        if (p < 32 && p > 16 && q > 16) {
                            DrawableHelper.drawTexture(arg, k, j, 64.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(arg, k, j, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            if ((r = this.client.textRenderer.getWidth(lv2)) > 157) {
                StringRenderable lv4 = StringRenderable.concat(this.client.textRenderer.trimToWidth(lv2, 157 - this.client.textRenderer.getWidth("...")), StringRenderable.plain("..."));
                this.client.textRenderer.drawWithShadow(arg, lv4, (float)(k + 32 + 2), (float)(j + 1), 0xFFFFFF);
            } else {
                this.client.textRenderer.drawWithShadow(arg, lv2, (float)(k + 32 + 2), (float)(j + 1), 0xFFFFFF);
            }
            this.client.textRenderer.drawWithShadow(arg, lv2, (float)(k + 32 + 2), (float)(j + 1), 0xFFFFFF);
            List<StringRenderable> list = this.client.textRenderer.wrapLines(lv3, 157);
            for (int s = 0; s < 2 && s < list.size(); ++s) {
                this.client.textRenderer.drawWithShadow(arg, list.get(s), (float)(k + 32 + 2), (float)(j + 12 + 10 * s), 0x808080);
            }
        }

        private boolean isSelectable() {
            return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            double f = d - (double)this.widget.getRowLeft();
            double g = e - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
            if (this.isSelectable() && f <= 32.0) {
                if (this.pack.canBeEnabled()) {
                    ResourcePackCompatibility lv = this.pack.getCompatibility();
                    if (lv.isCompatible()) {
                        this.pack.enable();
                    } else {
                        Text lv2 = lv.getConfirmMessage();
                        this.client.openScreen(new ConfirmScreen(bl -> {
                            this.client.openScreen(this.screen);
                            if (bl) {
                                this.pack.enable();
                            }
                        }, INCOMPATIBLE_CONFIRM, lv2));
                    }
                    return true;
                }
                if (f < 16.0 && this.pack.canBeDisabled()) {
                    this.pack.disable();
                    return true;
                }
                if (f > 16.0 && g < 16.0 && this.pack.canMoveTowardStart()) {
                    this.pack.moveTowardStart();
                    return true;
                }
                if (f > 16.0 && g > 16.0 && this.pack.canMoveTowardEnd()) {
                    this.pack.moveTowardEnd();
                    return true;
                }
            }
            return false;
        }
    }
}

