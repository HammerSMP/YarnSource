/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class BufferRenderer {
    public static void draw(BufferBuilder arg) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = arg.popData();
                BufferBuilder.DrawArrayParameters lv = (BufferBuilder.DrawArrayParameters)pair.getFirst();
                BufferRenderer.draw((ByteBuffer)pair.getSecond(), lv.getMode(), lv.getVertexFormat(), lv.getCount());
            });
        } else {
            Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = arg.popData();
            BufferBuilder.DrawArrayParameters lv = (BufferBuilder.DrawArrayParameters)pair.getFirst();
            BufferRenderer.draw((ByteBuffer)pair.getSecond(), lv.getMode(), lv.getVertexFormat(), lv.getCount());
        }
    }

    private static void draw(ByteBuffer byteBuffer, int i, VertexFormat arg, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        byteBuffer.clear();
        if (j <= 0) {
            return;
        }
        arg.startDrawing(MemoryUtil.memAddress((ByteBuffer)byteBuffer));
        GlStateManager.drawArrays(i, 0, j);
        arg.endDrawing();
    }
}

