/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.List;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class PostProcessShader
implements AutoCloseable {
    private final JsonGlProgram program;
    public final Framebuffer input;
    public final Framebuffer output;
    private final List<IntSupplier> samplerValues = Lists.newArrayList();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> samplerWidths = Lists.newArrayList();
    private final List<Integer> samplerHeights = Lists.newArrayList();
    private Matrix4f projectionMatrix;

    public PostProcessShader(ResourceManager arg, String string, Framebuffer arg2, Framebuffer arg3) throws IOException {
        this.program = new JsonGlProgram(arg, string);
        this.input = arg2;
        this.output = arg3;
    }

    @Override
    public void close() {
        this.program.close();
    }

    public void addAuxTarget(String string, IntSupplier intSupplier, int i, int j) {
        this.samplerNames.add(this.samplerNames.size(), string);
        this.samplerValues.add(this.samplerValues.size(), intSupplier);
        this.samplerWidths.add(this.samplerWidths.size(), i);
        this.samplerHeights.add(this.samplerHeights.size(), j);
    }

    public void setProjectionMatrix(Matrix4f arg) {
        this.projectionMatrix = arg;
    }

    public void render(float f) {
        this.input.endWrite();
        float g = this.output.textureWidth;
        float h = this.output.textureHeight;
        RenderSystem.viewport(0, 0, (int)g, (int)h);
        this.program.bindSampler("DiffuseSampler", this.input::method_30277);
        for (int i = 0; i < this.samplerValues.size(); ++i) {
            this.program.bindSampler(this.samplerNames.get(i), this.samplerValues.get(i));
            this.program.getUniformByNameOrDummy("AuxSize" + i).set(this.samplerWidths.get(i).intValue(), this.samplerHeights.get(i).intValue());
        }
        this.program.getUniformByNameOrDummy("ProjMat").set(this.projectionMatrix);
        this.program.getUniformByNameOrDummy("InSize").set(this.input.textureWidth, this.input.textureHeight);
        this.program.getUniformByNameOrDummy("OutSize").set(g, h);
        this.program.getUniformByNameOrDummy("Time").set(f);
        MinecraftClient lv = MinecraftClient.getInstance();
        this.program.getUniformByNameOrDummy("ScreenSize").set(lv.getWindow().getFramebufferWidth(), lv.getWindow().getFramebufferHeight());
        this.program.enable();
        this.output.clear(MinecraftClient.IS_SYSTEM_MAC);
        this.output.beginWrite(false);
        RenderSystem.depthMask(false);
        BufferBuilder lv2 = Tessellator.getInstance().getBuffer();
        lv2.begin(7, VertexFormats.POSITION_COLOR);
        lv2.vertex(0.0, 0.0, 500.0).color(255, 255, 255, 255).next();
        lv2.vertex(g, 0.0, 500.0).color(255, 255, 255, 255).next();
        lv2.vertex(g, h, 500.0).color(255, 255, 255, 255).next();
        lv2.vertex(0.0, h, 500.0).color(255, 255, 255, 255).next();
        lv2.end();
        BufferRenderer.draw(lv2);
        RenderSystem.depthMask(true);
        this.program.disable();
        this.output.endWrite();
        this.input.endRead();
        for (IntSupplier object : this.samplerValues) {
            if (!(object instanceof Framebuffer)) continue;
            ((Framebuffer)((Object)object)).endRead();
        }
    }

    public JsonGlProgram getProgram() {
        return this.program;
    }
}

