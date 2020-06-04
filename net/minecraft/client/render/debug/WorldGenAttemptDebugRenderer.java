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
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
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
public class WorldGenAttemptDebugRenderer
implements DebugRenderer.Renderer {
    private final List<BlockPos> field_4640 = Lists.newArrayList();
    private final List<Float> field_4635 = Lists.newArrayList();
    private final List<Float> field_4637 = Lists.newArrayList();
    private final List<Float> field_4639 = Lists.newArrayList();
    private final List<Float> field_4636 = Lists.newArrayList();
    private final List<Float> field_4638 = Lists.newArrayList();

    public void method_3872(BlockPos arg, float f, float g, float h, float i, float j) {
        this.field_4640.add(arg);
        this.field_4635.add(Float.valueOf(f));
        this.field_4637.add(Float.valueOf(j));
        this.field_4639.add(Float.valueOf(g));
        this.field_4636.add(Float.valueOf(h));
        this.field_4638.add(Float.valueOf(i));
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(5, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < this.field_4640.size(); ++i) {
            BlockPos lv3 = this.field_4640.get(i);
            Float float_ = this.field_4635.get(i);
            float g = float_.floatValue() / 2.0f;
            WorldRenderer.drawBox(lv2, (double)((float)lv3.getX() + 0.5f - g) - d, (double)((float)lv3.getY() + 0.5f - g) - e, (double)((float)lv3.getZ() + 0.5f - g) - f, (double)((float)lv3.getX() + 0.5f + g) - d, (double)((float)lv3.getY() + 0.5f + g) - e, (double)((float)lv3.getZ() + 0.5f + g) - f, this.field_4639.get(i).floatValue(), this.field_4636.get(i).floatValue(), this.field_4638.get(i).floatValue(), this.field_4637.get(i).floatValue());
        }
        lv.draw();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

