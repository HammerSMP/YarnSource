/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ResourcePackListWidget
extends AlwaysSelectedEntryListWidget<ResourcePackEntry> {
    private static final Identifier RESOURCE_PACKS_LOCATION = new Identifier("textures/gui/resource_packs.png");
    private static final Text INCOMPATIBLE = new TranslatableText("resourcePack.incompatible");
    private static final Text INCOMPATIBLE_CONFIRM = new TranslatableText("resourcePack.incompatible.confirm.title");
    protected final MinecraftClient client;
    private final Text title;

    public ResourcePackListWidget(MinecraftClient arg, int i, int j, Text arg2) {
        super(arg, i, j, 32, j - 55 + 4, 36);
        this.client = arg;
        this.centerListVertically = false;
        arg.textRenderer.getClass();
        this.setRenderHeader(true, (int)(9.0f * 1.5f));
        this.title = arg2;
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

    public void add(ResourcePackEntry arg) {
        this.addEntry(arg);
        arg.resourcePackList = this;
    }

    @Environment(value=EnvType.CLIENT)
    public static class ResourcePackEntry
    extends AlwaysSelectedEntryListWidget.Entry<ResourcePackEntry> {
        private ResourcePackListWidget resourcePackList;
        protected final MinecraftClient client;
        protected final ResourcePackOptionsScreen screen;
        private final ClientResourcePackProfile pack;

        public ResourcePackEntry(ResourcePackListWidget arg, ResourcePackOptionsScreen arg2, ClientResourcePackProfile arg3) {
            this.screen = arg2;
            this.client = MinecraftClient.getInstance();
            this.pack = arg3;
            this.resourcePackList = arg;
        }

        public void enable(SelectedResourcePackListWidget arg) {
            this.getPack().getInitialPosition().insert(arg.children(), this, ResourcePackEntry::getPack, true);
            this.method_24232(arg);
        }

        public void method_24232(SelectedResourcePackListWidget arg) {
            this.resourcePackList = arg;
        }

        protected void drawIcon() {
            this.pack.drawIcon(this.client.getTextureManager());
        }

        protected ResourcePackCompatibility getCompatibility() {
            return this.pack.getCompatibility();
        }

        public ClientResourcePackProfile getPack() {
            return this.pack;
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            int r;
            ResourcePackCompatibility lv = this.getCompatibility();
            if (!lv.isCompatible()) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.fill(arg, k - 1, j - 1, k + l - 9, j + m + 1, -8978432);
            }
            this.drawIcon();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(arg, k, j, 0.0f, 0.0f, 32, 32, 32, 32);
            Text lv2 = this.pack.getDisplayName();
            Text lv3 = this.pack.getDescription();
            if (this.isMoveable() && (this.client.options.touchscreen || bl)) {
                this.client.getTextureManager().bindTexture(RESOURCE_PACKS_LOCATION);
                DrawableHelper.fill(arg, k, j, k + 32, j + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int p = n - k;
                int q = o - j;
                if (!lv.isCompatible()) {
                    lv2 = INCOMPATIBLE;
                    lv3 = lv.getNotification();
                }
                if (this.isSelectable()) {
                    if (p < 32) {
                        DrawableHelper.drawTexture(arg, k, j, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(arg, k, j, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.isRemovable()) {
                        if (p < 16) {
                            DrawableHelper.drawTexture(arg, k, j, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(arg, k, j, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canMoveUp()) {
                        if (p < 32 && p > 16 && q < 16) {
                            DrawableHelper.drawTexture(arg, k, j, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(arg, k, j, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.canMoveDown()) {
                        if (p < 32 && p > 16 && q > 16) {
                            DrawableHelper.drawTexture(arg, k, j, 64.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            DrawableHelper.drawTexture(arg, k, j, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            if ((r = this.client.textRenderer.getWidth(lv2)) > 157) {
                MutableText lv4 = this.client.textRenderer.trimToWidth(lv2, 157 - this.client.textRenderer.getWidth("...")).append("...");
                this.client.textRenderer.drawWithShadow(arg, lv4, (float)(k + 32 + 2), (float)(j + 1), 0xFFFFFF);
            } else {
                this.client.textRenderer.drawWithShadow(arg, lv2, (float)(k + 32 + 2), (float)(j + 1), 0xFFFFFF);
            }
            this.client.textRenderer.drawWithShadow(arg, lv2, (float)(k + 32 + 2), (float)(j + 1), 0xFFFFFF);
            List<Text> list = this.client.textRenderer.wrapLines(lv3, 157);
            for (int s = 0; s < 2 && s < list.size(); ++s) {
                this.client.textRenderer.drawWithShadow(arg, list.get(s), (float)(k + 32 + 2), (float)(j + 12 + 10 * s), 0x808080);
            }
        }

        protected boolean isMoveable() {
            return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
        }

        protected boolean isSelectable() {
            return !this.screen.isEnabled(this);
        }

        protected boolean isRemovable() {
            return this.screen.isEnabled(this) && !this.pack.isAlwaysEnabled();
        }

        protected boolean canMoveUp() {
            List list = this.resourcePackList.children();
            int i = list.indexOf(this);
            return i > 0 && !((ResourcePackEntry)list.get((int)(i - 1))).pack.isPinned();
        }

        protected boolean canMoveDown() {
            List list = this.resourcePackList.children();
            int i = list.indexOf(this);
            return i >= 0 && i < list.size() - 1 && !((ResourcePackEntry)list.get((int)(i + 1))).pack.isPinned();
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            double f = d - (double)this.resourcePackList.getRowLeft();
            double g = e - (double)this.resourcePackList.getRowTop(this.resourcePackList.children().indexOf(this));
            if (this.isMoveable() && f <= 32.0) {
                if (this.isSelectable()) {
                    this.getScreen().markDirty();
                    ResourcePackCompatibility lv = this.getCompatibility();
                    if (lv.isCompatible()) {
                        this.getScreen().enable(this);
                    } else {
                        Text lv2 = lv.getConfirmMessage();
                        this.client.openScreen(new ConfirmScreen(bl -> {
                            this.client.openScreen(this.getScreen());
                            if (bl) {
                                this.getScreen().enable(this);
                            }
                        }, INCOMPATIBLE_CONFIRM, lv2));
                    }
                    return true;
                }
                if (f < 16.0 && this.isRemovable()) {
                    this.getScreen().disable(this);
                    return true;
                }
                if (f > 16.0 && g < 16.0 && this.canMoveUp()) {
                    List<ResourcePackEntry> list = this.resourcePackList.children();
                    int j = list.indexOf(this);
                    list.remove(j);
                    list.add(j - 1, this);
                    this.getScreen().markDirty();
                    return true;
                }
                if (f > 16.0 && g > 16.0 && this.canMoveDown()) {
                    List<ResourcePackEntry> list2 = this.resourcePackList.children();
                    int k = list2.indexOf(this);
                    list2.remove(k);
                    list2.add(k + 1, this);
                    this.getScreen().markDirty();
                    return true;
                }
            }
            return false;
        }

        public ResourcePackOptionsScreen getScreen() {
            return this.screen;
        }
    }
}

