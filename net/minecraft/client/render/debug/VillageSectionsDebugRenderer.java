/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

@Environment(value=EnvType.CLIENT)
public class VillageSectionsDebugRenderer
implements DebugRenderer.Renderer {
    private final Set<ChunkSectionPos> sections = Sets.newHashSet();

    VillageSectionsDebugRenderer() {
    }

    @Override
    public void clear() {
        this.sections.clear();
    }

    public void addSection(ChunkSectionPos arg) {
        this.sections.add(arg);
    }

    public void removeSection(ChunkSectionPos arg) {
        this.sections.remove(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.drawSections(d, e, f);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void drawSections(double d, double e, double f) {
        BlockPos lv = new BlockPos(d, e, f);
        this.sections.forEach(arg2 -> {
            if (lv.isWithinDistance(arg2.getCenterPos(), 60.0)) {
                VillageSectionsDebugRenderer.drawBoxAtCenterOf(arg2);
            }
        });
    }

    private static void drawBoxAtCenterOf(ChunkSectionPos arg) {
        float f = 1.0f;
        BlockPos lv = arg.getCenterPos();
        BlockPos lv2 = lv.add(-1.0, -1.0, -1.0);
        BlockPos lv3 = lv.add(1.0, 1.0, 1.0);
        DebugRenderer.drawBox(lv2, lv3, 0.2f, 1.0f, 0.2f, 0.15f);
    }
}

