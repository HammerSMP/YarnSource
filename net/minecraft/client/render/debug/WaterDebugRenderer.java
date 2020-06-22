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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class WaterDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public WaterDebugRenderer(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        BlockPos lv = this.client.player.getBlockPos();
        World lv2 = this.client.player.world;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        for (BlockPos lv3 : BlockPos.iterate(lv.add(-10, -10, -10), lv.add(10, 10, 10))) {
            FluidState lv4 = lv2.getFluidState(lv3);
            if (!lv4.isIn(FluidTags.WATER)) continue;
            double g = (float)lv3.getY() + lv4.getHeight(lv2, lv3);
            DebugRenderer.drawBox(new Box((float)lv3.getX() + 0.01f, (float)lv3.getY() + 0.01f, (float)lv3.getZ() + 0.01f, (float)lv3.getX() + 0.99f, g, (float)lv3.getZ() + 0.99f).offset(-d, -e, -f), 1.0f, 1.0f, 1.0f, 0.2f);
        }
        for (BlockPos lv5 : BlockPos.iterate(lv.add(-10, -10, -10), lv.add(10, 10, 10))) {
            FluidState lv6 = lv2.getFluidState(lv5);
            if (!lv6.isIn(FluidTags.WATER)) continue;
            DebugRenderer.drawString(String.valueOf(lv6.getLevel()), (double)lv5.getX() + 0.5, (float)lv5.getY() + lv6.getHeight(lv2, lv5), (double)lv5.getZ() + 0.5, -16777216);
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}

