/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AdvancementWidget
extends DrawableHelper {
    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
    private static final int[] field_24262 = new int[]{0, 10, -10, 25, -25};
    private final AdvancementTab tab;
    private final Advancement advancement;
    private final AdvancementDisplay display;
    private final StringRenderable title;
    private final int width;
    private final List<StringRenderable> description;
    private final MinecraftClient client;
    private AdvancementWidget parent;
    private final List<AdvancementWidget> children = Lists.newArrayList();
    private AdvancementProgress progress;
    private final int xPos;
    private final int yPos;

    public AdvancementWidget(AdvancementTab tab, MinecraftClient client, Advancement advancement, AdvancementDisplay display) {
        this.tab = tab;
        this.advancement = advancement;
        this.display = display;
        this.client = client;
        this.title = client.textRenderer.trimToWidth(display.getTitle(), 163);
        this.xPos = MathHelper.floor(display.getX() * 28.0f);
        this.yPos = MathHelper.floor(display.getY() * 27.0f);
        int i = advancement.getRequirementCount();
        int j = String.valueOf(i).length();
        int k = i > 1 ? client.textRenderer.getWidth("  ") + client.textRenderer.getWidth("0") * j * 2 + client.textRenderer.getWidth("/") : 0;
        int l = 29 + client.textRenderer.getWidth(this.title) + k;
        this.description = this.wrapDescription(Texts.setStyleIfAbsent(display.getDescription().shallowCopy(), Style.EMPTY.withColor(display.getFrame().getTitleFormat())), l);
        for (StringRenderable lv : this.description) {
            l = Math.max(l, client.textRenderer.getWidth(lv));
        }
        this.width = l + 3 + 5;
    }

    private static float method_27572(TextHandler arg, List<StringRenderable> list) {
        return (float)list.stream().mapToDouble(arg::getWidth).max().orElse(0.0);
    }

    private List<StringRenderable> wrapDescription(Text arg, int width) {
        TextHandler lv = this.client.textRenderer.getTextHandler();
        List<StringRenderable> list = null;
        float f = Float.MAX_VALUE;
        for (int j : field_24262) {
            List<StringRenderable> list2 = lv.wrapLines(arg, width - j, Style.EMPTY);
            float g = Math.abs(AdvancementWidget.method_27572(lv, list2) - (float)width);
            if (g <= 10.0f) {
                return list2;
            }
            if (!(g < f)) continue;
            f = g;
            list = list2;
        }
        return list;
    }

    @Nullable
    private AdvancementWidget getParent(Advancement advancement) {
        while ((advancement = advancement.getParent()) != null && advancement.getDisplay() == null) {
        }
        if (advancement == null || advancement.getDisplay() == null) {
            return null;
        }
        return this.tab.getWidget(advancement);
    }

    public void renderLines(MatrixStack arg, int i, int j, boolean bl) {
        if (this.parent != null) {
            int p;
            int k = i + this.parent.xPos + 13;
            int l = i + this.parent.xPos + 26 + 4;
            int m = j + this.parent.yPos + 13;
            int n = i + this.xPos + 13;
            int o = j + this.yPos + 13;
            int n2 = p = bl ? -16777216 : -1;
            if (bl) {
                this.drawHorizontalLine(arg, l, k, m - 1, p);
                this.drawHorizontalLine(arg, l + 1, k, m, p);
                this.drawHorizontalLine(arg, l, k, m + 1, p);
                this.drawHorizontalLine(arg, n, l - 1, o - 1, p);
                this.drawHorizontalLine(arg, n, l - 1, o, p);
                this.drawHorizontalLine(arg, n, l - 1, o + 1, p);
                this.drawVerticalLine(arg, l - 1, o, m, p);
                this.drawVerticalLine(arg, l + 1, o, m, p);
            } else {
                this.drawHorizontalLine(arg, l, k, m, p);
                this.drawHorizontalLine(arg, n, l, o, p);
                this.drawVerticalLine(arg, l, o, m, p);
            }
        }
        for (AdvancementWidget lv : this.children) {
            lv.renderLines(arg, i, j, bl);
        }
    }

    public void renderWidgets(MatrixStack arg, int i, int j) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            AdvancementObtainedStatus lv2;
            float f;
            float f2 = f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
            if (f >= 1.0f) {
                AdvancementObtainedStatus lv = AdvancementObtainedStatus.OBTAINED;
            } else {
                lv2 = AdvancementObtainedStatus.UNOBTAINED;
            }
            this.client.getTextureManager().bindTexture(WIDGETS_TEX);
            this.drawTexture(arg, i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + lv2.getSpriteIndex() * 26, 26, 26);
            this.client.getItemRenderer().renderInGui(this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
        }
        for (AdvancementWidget lv3 : this.children) {
            lv3.renderWidgets(arg, i, j);
        }
    }

    public void setProgress(AdvancementProgress progress) {
        this.progress = progress;
    }

    public void addChild(AdvancementWidget widget) {
        this.children.add(widget);
    }

    public void drawTooltip(MatrixStack arg, int i, int j, float f, int y, int l) {
        int r;
        AdvancementObtainedStatus lv12;
        AdvancementObtainedStatus lv11;
        AdvancementObtainedStatus lv10;
        boolean bl = y + i + this.xPos + this.width + 26 >= this.tab.getScreen().width;
        String string = this.progress == null ? null : this.progress.getProgressBarFraction();
        int m = string == null ? 0 : this.client.textRenderer.getWidth(string);
        this.client.textRenderer.getClass();
        boolean bl2 = 113 - j - this.yPos - 26 <= 6 + this.description.size() * 9;
        float g = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
        int n = MathHelper.floor(g * (float)this.width);
        if (g >= 1.0f) {
            n = this.width / 2;
            AdvancementObtainedStatus lv = AdvancementObtainedStatus.OBTAINED;
            AdvancementObtainedStatus lv2 = AdvancementObtainedStatus.OBTAINED;
            AdvancementObtainedStatus lv3 = AdvancementObtainedStatus.OBTAINED;
        } else if (n < 2) {
            n = this.width / 2;
            AdvancementObtainedStatus lv4 = AdvancementObtainedStatus.UNOBTAINED;
            AdvancementObtainedStatus lv5 = AdvancementObtainedStatus.UNOBTAINED;
            AdvancementObtainedStatus lv6 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (n > this.width - 2) {
            n = this.width / 2;
            AdvancementObtainedStatus lv7 = AdvancementObtainedStatus.OBTAINED;
            AdvancementObtainedStatus lv8 = AdvancementObtainedStatus.OBTAINED;
            AdvancementObtainedStatus lv9 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            lv10 = AdvancementObtainedStatus.OBTAINED;
            lv11 = AdvancementObtainedStatus.UNOBTAINED;
            lv12 = AdvancementObtainedStatus.UNOBTAINED;
        }
        int o = this.width - n;
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        int p = j + this.yPos;
        if (bl) {
            int q = i + this.xPos - this.width + 26 + 6;
        } else {
            r = i + this.xPos;
        }
        this.client.textRenderer.getClass();
        int s = 32 + this.description.size() * 9;
        if (!this.description.isEmpty()) {
            if (bl2) {
                this.method_2324(arg, r, p + 26 - s, this.width, s, 10, 200, 26, 0, 52);
            } else {
                this.method_2324(arg, r, p, this.width, s, 10, 200, 26, 0, 52);
            }
        }
        this.drawTexture(arg, r, p, 0, lv10.getSpriteIndex() * 26, n, 26);
        this.drawTexture(arg, r + n, p, 200 - o, lv11.getSpriteIndex() * 26, o, 26);
        this.drawTexture(arg, i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + lv12.getSpriteIndex() * 26, 26, 26);
        if (bl) {
            this.client.textRenderer.drawWithShadow(arg, this.title, (float)(r + 5), (float)(j + this.yPos + 9), -1);
            if (string != null) {
                this.client.textRenderer.drawWithShadow(arg, string, (float)(i + this.xPos - m), (float)(j + this.yPos + 9), -1);
            }
        } else {
            this.client.textRenderer.drawWithShadow(arg, this.title, (float)(i + this.xPos + 32), (float)(j + this.yPos + 9), -1);
            if (string != null) {
                this.client.textRenderer.drawWithShadow(arg, string, (float)(i + this.xPos + this.width - m - 5), (float)(j + this.yPos + 9), -1);
            }
        }
        if (bl2) {
            for (int t = 0; t < this.description.size(); ++t) {
                this.client.textRenderer.getClass();
                this.client.textRenderer.draw(arg, this.description.get(t), (float)(r + 5), (float)(p + 26 - s + 7 + t * 9), -5592406);
            }
        } else {
            for (int u = 0; u < this.description.size(); ++u) {
                this.client.textRenderer.getClass();
                this.client.textRenderer.draw(arg, this.description.get(u), (float)(r + 5), (float)(j + this.yPos + 9 + 17 + u * 9), -5592406);
            }
        }
        this.client.getItemRenderer().renderInGui(this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
    }

    protected void method_2324(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
        this.drawTexture(arg, i, j, p, q, m, m);
        this.method_2321(arg, i + m, j, k - m - m, m, p + m, q, n - m - m, o);
        this.drawTexture(arg, i + k - m, j, p + n - m, q, m, m);
        this.drawTexture(arg, i, j + l - m, p, q + o - m, m, m);
        this.method_2321(arg, i + m, j + l - m, k - m - m, m, p + m, q + o - m, n - m - m, o);
        this.drawTexture(arg, i + k - m, j + l - m, p + n - m, q + o - m, m, m);
        this.method_2321(arg, i, j + m, m, l - m - m, p, q + m, n, o - m - m);
        this.method_2321(arg, i + m, j + m, k - m - m, l - m - m, p + m, q + m, n - m - m, o - m - m);
        this.method_2321(arg, i + k - m, j + m, m, l - m - m, p + n - m, q + m, n, o - m - m);
    }

    protected void method_2321(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, int p) {
        for (int q = 0; q < k; q += o) {
            int r = i + q;
            int s = Math.min(o, k - q);
            for (int t = 0; t < l; t += p) {
                int u = j + t;
                int v = Math.min(p, l - t);
                this.drawTexture(arg, r, u, m, n, s, v);
            }
        }
    }

    public boolean shouldRender(int originX, int originY, int mouseX, int mouseY) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int m = originX + this.xPos;
        int n = m + 26;
        int o = originY + this.yPos;
        int p = o + 26;
        return mouseX >= m && mouseX <= n && mouseY >= o && mouseY <= p;
    }

    public void addToTree() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getParent(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getY() {
        return this.yPos;
    }

    public int getX() {
        return this.xPos;
    }
}

