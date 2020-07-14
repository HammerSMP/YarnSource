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

    public PackListWidget(MinecraftClient client, int width, int height, Text title) {
        super(client, width, height, 32, height - 55 + 4, 36);
        this.title = title;
        this.centerListVertically = false;
        client.textRenderer.getClass();
        this.setRenderHeader(true, (int)(9.0f * 1.5f));
    }

    @Override
    protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator arg2) {
        MutableText lv = new LiteralText("").append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
        this.client.textRenderer.draw(matrices, lv, (float)(x + this.width / 2 - this.client.textRenderer.getWidth(lv) / 2), (float)Math.min(this.top + 3, y), 0xFFFFFF);
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

        public ResourcePackEntry(MinecraftClient client, PackListWidget widget, Screen screen, ResourcePackOrganizer.Pack pack) {
            this.client = client;
            this.screen = screen;
            this.pack = pack;
            this.widget = widget;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int r;
            ResourcePackCompatibility lv = this.pack.getCompatibility();
            if (!lv.isCompatible()) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.fill(matrices, x - 1, y - 1, x + entryWidth - 9, y + entryHeight + 1, -8978432);
            }
            this.client.getTextureManager().bindTexture(this.pack.method_30286());
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            Text lv2 = this.pack.getDisplayName();
            StringRenderable lv3 = this.pack.getDecoratedDescription();
            if (this.isSelectable() && (this.client.options.touchscreen || hovered)) {
                this.client.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
                DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int p = mouseX - x;
                int q = mouseY - y;
                if (!lv.isCompatible()) {
                    lv2 = INCOMPATIBLE;
                    lv3 = lv.getNotification();
                }
                if (this.pack.canBeEnabled()) {
                    if (p < 32) {
                        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.pack.canBeDisabled()) {
                        if (p < 16) {
                            DrawableHelper.drawTexture(matrices, x, y, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(matrices, x, y, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveTowardStart()) {
                        if (p < 32 && p > 16 && q < 16) {
                            DrawableHelper.drawTexture(matrices, x, y, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(matrices, x, y, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveTowardEnd()) {
                        if (p < 32 && p > 16 && q > 16) {
                            DrawableHelper.drawTexture(matrices, x, y, 64.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(matrices, x, y, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            if ((r = this.client.textRenderer.getWidth(lv2)) > 157) {
                StringRenderable lv4 = StringRenderable.concat(this.client.textRenderer.trimToWidth(lv2, 157 - this.client.textRenderer.getWidth("...")), StringRenderable.plain("..."));
                this.client.textRenderer.drawWithShadow(matrices, lv4, (float)(x + 32 + 2), (float)(y + 1), 0xFFFFFF);
            } else {
                this.client.textRenderer.drawWithShadow(matrices, lv2, (float)(x + 32 + 2), (float)(y + 1), 0xFFFFFF);
            }
            this.client.textRenderer.drawWithShadow(matrices, lv2, (float)(x + 32 + 2), (float)(y + 1), 0xFFFFFF);
            List<StringRenderable> list = this.client.textRenderer.wrapLines(lv3, 157);
            for (int s = 0; s < 2 && s < list.size(); ++s) {
                this.client.textRenderer.drawWithShadow(matrices, list.get(s), (float)(x + 32 + 2), (float)(y + 12 + 10 * s), 0x808080);
            }
        }

        private boolean isSelectable() {
            return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double f = mouseX - (double)this.widget.getRowLeft();
            double g = mouseY - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
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

