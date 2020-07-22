/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class BlockOutlineDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public BlockOutlineDebugRenderer(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        World lv = this.client.player.world;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        BlockPos lv2 = new BlockPos(cameraX, cameraY, cameraZ);
        for (BlockPos lv3 : BlockPos.iterate(lv2.add(-6, -6, -6), lv2.add(6, 6, 6))) {
            BlockState lv4 = lv.getBlockState(lv3);
            if (lv4.isOf(Blocks.AIR)) continue;
            VoxelShape lv5 = lv4.getOutlineShape(lv, lv3);
            for (Box lv6 : lv5.getBoundingBoxes()) {
                Box lv7 = lv6.offset(lv3).expand(0.002).offset(-cameraX, -cameraY, -cameraZ);
                double g = lv7.minX;
                double h = lv7.minY;
                double i = lv7.minZ;
                double j = lv7.maxX;
                double k = lv7.maxY;
                double l = lv7.maxZ;
                float m = 1.0f;
                float n = 0.0f;
                float o = 0.0f;
                float p = 0.5f;
                if (lv4.isSideSolidFullSquare(lv, lv3, Direction.WEST)) {
                    Tessellator lv8 = Tessellator.getInstance();
                    BufferBuilder lv9 = lv8.getBuffer();
                    lv9.begin(5, VertexFormats.POSITION_COLOR);
                    lv9.vertex(g, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv9.vertex(g, h, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv9.vertex(g, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv9.vertex(g, k, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv8.draw();
                }
                if (lv4.isSideSolidFullSquare(lv, lv3, Direction.SOUTH)) {
                    Tessellator lv10 = Tessellator.getInstance();
                    BufferBuilder lv11 = lv10.getBuffer();
                    lv11.begin(5, VertexFormats.POSITION_COLOR);
                    lv11.vertex(g, k, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv11.vertex(g, h, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv11.vertex(j, k, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv11.vertex(j, h, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv10.draw();
                }
                if (lv4.isSideSolidFullSquare(lv, lv3, Direction.EAST)) {
                    Tessellator lv12 = Tessellator.getInstance();
                    BufferBuilder lv13 = lv12.getBuffer();
                    lv13.begin(5, VertexFormats.POSITION_COLOR);
                    lv13.vertex(j, h, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv13.vertex(j, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv13.vertex(j, k, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv13.vertex(j, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv12.draw();
                }
                if (lv4.isSideSolidFullSquare(lv, lv3, Direction.NORTH)) {
                    Tessellator lv14 = Tessellator.getInstance();
                    BufferBuilder lv15 = lv14.getBuffer();
                    lv15.begin(5, VertexFormats.POSITION_COLOR);
                    lv15.vertex(j, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv15.vertex(j, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv15.vertex(g, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv15.vertex(g, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv14.draw();
                }
                if (lv4.isSideSolidFullSquare(lv, lv3, Direction.DOWN)) {
                    Tessellator lv16 = Tessellator.getInstance();
                    BufferBuilder lv17 = lv16.getBuffer();
                    lv17.begin(5, VertexFormats.POSITION_COLOR);
                    lv17.vertex(g, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv17.vertex(j, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv17.vertex(g, h, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv17.vertex(j, h, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    lv16.draw();
                }
                if (!lv4.isSideSolidFullSquare(lv, lv3, Direction.UP)) continue;
                Tessellator lv18 = Tessellator.getInstance();
                BufferBuilder lv19 = lv18.getBuffer();
                lv19.begin(5, VertexFormats.POSITION_COLOR);
                lv19.vertex(g, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                lv19.vertex(g, k, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                lv19.vertex(j, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                lv19.vertex(j, k, l).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                lv18.draw();
            }
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}

