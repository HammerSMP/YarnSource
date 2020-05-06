/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class RaidCenterDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private Collection<BlockPos> raidCenters = Lists.newArrayList();

    public RaidCenterDebugRenderer(MinecraftClient arg) {
        this.client = arg;
    }

    public void setRaidCenters(Collection<BlockPos> collection) {
        this.raidCenters = collection;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        BlockPos lv = this.method_23125().getBlockPos();
        for (BlockPos lv2 : this.raidCenters) {
            if (!lv.isWithinDistance(lv2, 160.0)) continue;
            RaidCenterDebugRenderer.method_23122(lv2);
        }
    }

    private static void method_23122(BlockPos arg) {
        DebugRenderer.drawBox(arg.add(-0.5, -0.5, -0.5), arg.add(1.5, 1.5, 1.5), 1.0f, 0.0f, 0.0f, 0.15f);
        int i = -65536;
        RaidCenterDebugRenderer.method_23123("Raid center", arg, -65536);
    }

    private static void method_23123(String string, BlockPos arg, int i) {
        double d = (double)arg.getX() + 0.5;
        double e = (double)arg.getY() + 1.3;
        double f = (double)arg.getZ() + 0.5;
        DebugRenderer.drawString(string, d, e, f, i, 0.04f, true, 0.0f, true);
    }

    private Camera method_23125() {
        return this.client.gameRenderer.getCamera();
    }
}

