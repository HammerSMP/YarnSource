/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class EnchantmentScreen
extends HandledScreen<EnchantmentScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXURE = new Identifier("textures/entity/enchanting_table_book.png");
    private static final BookModel BOOK_MODEL = new BookModel();
    private final Random random = new Random();
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3);
    }

    @Override
    public void tick() {
        super.tick();
        this.doTick();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        for (int l = 0; l < 3; ++l) {
            double f = d - (double)(j + 60);
            double g = e - (double)(k + 14 + 19 * l);
            if (!(f >= 0.0) || !(g >= 0.0) || !(f < 108.0) || !(g < 19.0) || !((EnchantmentScreenHandler)this.handler).onButtonClick(this.client.player, l)) continue;
            this.client.interactionManager.clickButton(((EnchantmentScreenHandler)this.handler).syncId, l);
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(arg, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int m = (int)this.client.getWindow().getScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * m, (this.height - 240) / 2 * m, 320 * m, 240 * m);
        RenderSystem.translatef(-0.34f, 0.23f, 0.0f);
        RenderSystem.multMatrix(Matrix4f.viewboxMatrix(90.0, 1.3333334f, 9.0f, 80.0f));
        RenderSystem.matrixMode(5888);
        arg.push();
        MatrixStack.Entry lv = arg.peek();
        lv.getModel().loadIdentity();
        lv.getNormal().loadIdentity();
        arg.translate(0.0, 3.3f, 1984.0);
        float g = 5.0f;
        arg.scale(5.0f, 5.0f, 5.0f);
        arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20.0f));
        float h = MathHelper.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
        arg.translate((1.0f - h) * 0.2f, (1.0f - h) * 0.1f, (1.0f - h) * 0.25f);
        float n = -(1.0f - h) * 90.0f - 90.0f;
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n));
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
        float o = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.25f;
        float p = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.75f;
        o = (o - (float)MathHelper.fastFloor(o)) * 1.6f - 0.3f;
        p = (p - (float)MathHelper.fastFloor(p)) * 1.6f - 0.3f;
        if (o < 0.0f) {
            o = 0.0f;
        }
        if (p < 0.0f) {
            p = 0.0f;
        }
        if (o > 1.0f) {
            o = 1.0f;
        }
        if (p > 1.0f) {
            p = 1.0f;
        }
        RenderSystem.enableRescaleNormal();
        BOOK_MODEL.setPageAngles(0.0f, o, p, h);
        VertexConsumerProvider.Immediate lv2 = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        VertexConsumer lv3 = lv2.getBuffer(BOOK_MODEL.getLayer(BOOK_TEXURE));
        BOOK_MODEL.render(arg, lv3, 0xF000F0, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        lv2.draw();
        arg.pop();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantingPhrases.getInstance().setSeed(((EnchantmentScreenHandler)this.handler).getSeed());
        int q = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int r = 0; r < 3; ++r) {
            int s = k + 60;
            int t = s + 20;
            this.setZOffset(0);
            this.client.getTextureManager().bindTexture(TEXTURE);
            int u = ((EnchantmentScreenHandler)this.handler).enchantmentPower[r];
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (u == 0) {
                this.drawTexture(arg, s, l + 14 + 19 * r, 0, 185, 108, 19);
                continue;
            }
            String string = "" + u;
            int v = 86 - this.textRenderer.getWidth(string);
            StringRenderable lv4 = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, v);
            int w = 6839882;
            if (!(q >= r + 1 && this.client.player.experienceLevel >= u || this.client.player.abilities.creativeMode)) {
                this.drawTexture(arg, s, l + 14 + 19 * r, 0, 185, 108, 19);
                this.drawTexture(arg, s + 1, l + 15 + 19 * r, 16 * r, 239, 16, 16);
                this.textRenderer.drawTrimmed(lv4, t, l + 16 + 19 * r, v, (w & 0xFEFEFE) >> 1);
                w = 4226832;
            } else {
                int x = i - (k + 60);
                int y = j - (l + 14 + 19 * r);
                if (x >= 0 && y >= 0 && x < 108 && y < 19) {
                    this.drawTexture(arg, s, l + 14 + 19 * r, 0, 204, 108, 19);
                    w = 0xFFFF80;
                } else {
                    this.drawTexture(arg, s, l + 14 + 19 * r, 0, 166, 108, 19);
                }
                this.drawTexture(arg, s + 1, l + 15 + 19 * r, 16 * r, 223, 16, 16);
                this.textRenderer.drawTrimmed(lv4, t, l + 16 + 19 * r, v, w);
                w = 8453920;
            }
            this.textRenderer.drawWithShadow(arg, string, (float)(t + 86 - this.textRenderer.getWidth(string)), (float)(l + 16 + 19 * r + 7), w);
        }
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        f = this.client.getTickDelta();
        this.renderBackground(arg);
        super.render(arg, i, j, f);
        this.drawMouseoverTooltip(arg, i, j);
        boolean bl = this.client.player.abilities.creativeMode;
        int k = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int l = 0; l < 3; ++l) {
            int m = ((EnchantmentScreenHandler)this.handler).enchantmentPower[l];
            Enchantment lv = Enchantment.byRawId(((EnchantmentScreenHandler)this.handler).enchantmentId[l]);
            int n = ((EnchantmentScreenHandler)this.handler).enchantmentLevel[l];
            int o = l + 1;
            if (!this.isPointWithinBounds(60, 14 + 19 * l, 108, 17, i, j) || m <= 0 || n < 0 || lv == null) continue;
            ArrayList list = Lists.newArrayList();
            list.add(new TranslatableText("container.enchant.clue", lv.getName(n)).formatted(Formatting.WHITE, Formatting.ITALIC));
            if (!bl) {
                list.add(LiteralText.EMPTY);
                if (this.client.player.experienceLevel < m) {
                    list.add(new TranslatableText("container.enchant.level.requirement", ((EnchantmentScreenHandler)this.handler).enchantmentPower[l]).formatted(Formatting.RED));
                } else {
                    TranslatableText lv5;
                    TranslatableText lv3;
                    if (o == 1) {
                        TranslatableText lv2 = new TranslatableText("container.enchant.lapis.one");
                    } else {
                        lv3 = new TranslatableText("container.enchant.lapis.many", o);
                    }
                    list.add(lv3.formatted(k >= o ? Formatting.GRAY : Formatting.RED));
                    if (o == 1) {
                        TranslatableText lv4 = new TranslatableText("container.enchant.level.one");
                    } else {
                        lv5 = new TranslatableText("container.enchant.level.many", o);
                    }
                    list.add(lv5.formatted(Formatting.GRAY));
                }
            }
            this.renderTooltip(arg, list, i, j);
            break;
        }
    }

    public void doTick() {
        ItemStack lv = ((EnchantmentScreenHandler)this.handler).getSlot(0).getStack();
        if (!ItemStack.areEqual(lv, this.stack)) {
            this.stack = lv;
            do {
                this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.approximatePageAngle + 1.0f && this.nextPageAngle >= this.approximatePageAngle - 1.0f);
        }
        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;
        for (int i = 0; i < 3; ++i) {
            if (((EnchantmentScreenHandler)this.handler).enchantmentPower[i] == 0) continue;
            bl = true;
        }
        this.nextPageTurningSpeed = bl ? (this.nextPageTurningSpeed += 0.2f) : (this.nextPageTurningSpeed -= 0.2f);
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4f;
        float g = 0.2f;
        f = MathHelper.clamp(f, -0.2f, 0.2f);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9f;
        this.nextPageAngle += this.pageRotationSpeed;
    }
}

