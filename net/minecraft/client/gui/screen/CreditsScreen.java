/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CreditsScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
    private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
    private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
    private static final String field_24260 = "" + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.OBFUSCATED) + (Object)((Object)Formatting.GREEN) + (Object)((Object)Formatting.AQUA);
    private final boolean endCredits;
    private final Runnable finishAction;
    private float time;
    private List<StringRenderable> credits;
    private IntSet field_24261;
    private int creditsHeight;
    private float speed = 0.5f;

    public CreditsScreen(boolean bl, Runnable runnable) {
        super(NarratorManager.EMPTY);
        this.endCredits = bl;
        this.finishAction = runnable;
        if (!bl) {
            this.speed = 0.75f;
        }
    }

    @Override
    public void tick() {
        this.client.getMusicTracker().tick();
        this.client.getSoundManager().tick(false);
        float f = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
        if (this.time > f) {
            this.close();
        }
    }

    @Override
    public void onClose() {
        this.close();
    }

    private void close() {
        this.finishAction.run();
        this.client.openScreen(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void init() {
        if (this.credits != null) {
            return;
        }
        this.credits = Lists.newArrayList();
        this.field_24261 = new IntOpenHashSet();
        Resource lv = null;
        try {
            String string4;
            int i = 274;
            if (this.endCredits) {
                String string;
                lv = this.client.getResourceManager().getResource(new Identifier("texts/end.txt"));
                InputStream inputStream = lv.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                Random random = new Random(8124371L);
                while ((string = bufferedReader.readLine()) != null) {
                    int j;
                    string = string.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
                    while ((j = string.indexOf(field_24260)) != -1) {
                        String string2 = string.substring(0, j);
                        String string3 = string.substring(j + field_24260.length());
                        string = string2 + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.OBFUSCATED) + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string3;
                    }
                    this.credits.addAll(this.client.textRenderer.getTextHandler().wrapLines(string, 274, Style.EMPTY));
                    this.credits.add(StringRenderable.EMPTY);
                }
                inputStream.close();
                for (int k = 0; k < 8; ++k) {
                    this.credits.add(StringRenderable.EMPTY);
                }
            }
            InputStream inputStream2 = this.client.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2, StandardCharsets.UTF_8));
            while ((string4 = bufferedReader2.readLine()) != null) {
                boolean bl2;
                string4 = string4.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
                if ((string4 = string4.replaceAll("\t", "    ")).startsWith("[C]")) {
                    string4 = string4.substring(3);
                    boolean bl = true;
                } else {
                    bl2 = false;
                }
                List<StringRenderable> list = this.client.textRenderer.getTextHandler().wrapLines(string4, 274, Style.EMPTY);
                for (StringRenderable lv2 : list) {
                    if (bl2) {
                        this.field_24261.add(this.credits.size());
                    }
                    this.credits.add(lv2);
                }
                this.credits.add(StringRenderable.EMPTY);
            }
            inputStream2.close();
            this.creditsHeight = this.credits.size() * 12;
            IOUtils.closeQuietly((Closeable)lv);
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load credits", (Throwable)exception);
        }
        finally {
            IOUtils.closeQuietly(lv);
        }
    }

    private void renderBackground(int i, int j, float f) {
        this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
        int k = this.width;
        float g = -this.time * 0.5f * this.speed;
        float h = (float)this.height - this.time * 0.5f * this.speed;
        float l = 0.015625f;
        float m = this.time * 0.02f;
        float n = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
        float o = (n - 20.0f - this.time) * 0.005f;
        if (o < m) {
            m = o;
        }
        if (m > 1.0f) {
            m = 1.0f;
        }
        m *= m;
        m = m * 96.0f / 255.0f;
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(0.0, this.height, this.getZOffset()).texture(0.0f, g * 0.015625f).color(m, m, m, 1.0f).next();
        lv2.vertex(k, this.height, this.getZOffset()).texture((float)k * 0.015625f, g * 0.015625f).color(m, m, m, 1.0f).next();
        lv2.vertex(k, 0.0, this.getZOffset()).texture((float)k * 0.015625f, h * 0.015625f).color(m, m, m, 1.0f).next();
        lv2.vertex(0.0, 0.0, this.getZOffset()).texture(0.0f, h * 0.015625f).color(m, m, m, 1.0f).next();
        lv.draw();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(i, j, f);
        int k = 274;
        int l = this.width / 2 - 137;
        int m = this.height + 50;
        this.time += f;
        float g = -this.time * this.speed;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, g, 0.0f);
        this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        this.method_29343(l, m, (integer, integer2) -> {
            this.drawTexture(arg, integer + 0, (int)integer2, 0, 0, 155, 44);
            this.drawTexture(arg, integer + 155, (int)integer2, 0, 45, 155, 44);
        });
        RenderSystem.disableBlend();
        this.client.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
        CreditsScreen.drawTexture(arg, l + 88, m + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        RenderSystem.disableAlphaTest();
        int n = m + 100;
        for (int o = 0; o < this.credits.size(); ++o) {
            float h;
            if (o == this.credits.size() - 1 && (h = (float)n + g - (float)(this.height / 2 - 6)) < 0.0f) {
                RenderSystem.translatef(0.0f, -h, 0.0f);
            }
            if ((float)n + g + 12.0f + 8.0f > 0.0f && (float)n + g < (float)this.height) {
                StringRenderable lv = this.credits.get(o);
                if (this.field_24261.contains(o)) {
                    this.textRenderer.drawWithShadow(arg, lv, (float)(l + (274 - this.textRenderer.getWidth(lv)) / 2), (float)n, 0xFFFFFF);
                } else {
                    this.textRenderer.random.setSeed((long)((float)((long)o * 4238972211L) + this.time / 4.0f));
                    this.textRenderer.drawWithShadow(arg, lv, (float)l, (float)n, 0xFFFFFF);
                }
            }
            n += 12;
        }
        RenderSystem.popMatrix();
        this.client.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
        int p = this.width;
        int q = this.height;
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        lv3.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv3.vertex(0.0, q, this.getZOffset()).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        lv3.vertex(p, q, this.getZOffset()).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        lv3.vertex(p, 0.0, this.getZOffset()).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        lv3.vertex(0.0, 0.0, this.getZOffset()).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        lv2.draw();
        RenderSystem.disableBlend();
        super.render(arg, i, j, f);
    }
}

