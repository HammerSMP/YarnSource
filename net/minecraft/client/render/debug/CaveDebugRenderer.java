/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class CaveDebugRenderer
implements DebugRenderer.Renderer {
    private final Map<BlockPos, BlockPos> field_4507 = Maps.newHashMap();
    private final Map<BlockPos, Float> field_4508 = Maps.newHashMap();
    private final List<BlockPos> field_4506 = Lists.newArrayList();

    public void method_3704(BlockPos arg, List<BlockPos> list, List<Float> list2) {
        for (int i = 0; i < list.size(); ++i) {
            this.field_4507.put(list.get(i), arg);
            this.field_4508.put(list.get(i), list2.get(i));
        }
        this.field_4506.add(arg);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos lv = new BlockPos(cameraX, 0.0, cameraZ);
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        lv3.begin(5, VertexFormats.POSITION_COLOR);
        for (Map.Entry<BlockPos, BlockPos> entry : this.field_4507.entrySet()) {
            BlockPos lv4 = entry.getKey();
            BlockPos lv5 = entry.getValue();
            float g = (float)(lv5.getX() * 128 % 256) / 256.0f;
            float h = (float)(lv5.getY() * 128 % 256) / 256.0f;
            float i = (float)(lv5.getZ() * 128 % 256) / 256.0f;
            float j = this.field_4508.get(lv4).floatValue();
            if (!lv.isWithinDistance(lv4, 160.0)) continue;
            WorldRenderer.drawBox(lv3, (double)((float)lv4.getX() + 0.5f) - cameraX - (double)j, (double)((float)lv4.getY() + 0.5f) - cameraY - (double)j, (double)((float)lv4.getZ() + 0.5f) - cameraZ - (double)j, (double)((float)lv4.getX() + 0.5f) - cameraX + (double)j, (double)((float)lv4.getY() + 0.5f) - cameraY + (double)j, (double)((float)lv4.getZ() + 0.5f) - cameraZ + (double)j, g, h, i, 0.5f);
        }
        for (BlockPos lv6 : this.field_4506) {
            if (!lv.isWithinDistance(lv6, 160.0)) continue;
            WorldRenderer.drawBox(lv3, (double)lv6.getX() - cameraX, (double)lv6.getY() - cameraY, (double)lv6.getZ() - cameraZ, (double)((float)lv6.getX() + 1.0f) - cameraX, (double)((float)lv6.getY() + 1.0f) - cameraY, (double)((float)lv6.getZ() + 1.0f) - cameraZ, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        lv2.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

