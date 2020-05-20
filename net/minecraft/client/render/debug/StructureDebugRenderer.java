/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public class StructureDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient field_4624;
    private final Map<DimensionType, Map<String, BlockBox>> field_4626 = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, BlockBox>> field_4627 = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, Boolean>> field_4625 = Maps.newIdentityHashMap();

    public StructureDebugRenderer(MinecraftClient arg) {
        this.field_4624 = arg;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        Camera lv = this.field_4624.gameRenderer.getCamera();
        ClientWorld lv2 = this.field_4624.world;
        DimensionType lv3 = lv2.method_27983();
        BlockPos lv4 = new BlockPos(lv.getPos().x, 0.0, lv.getPos().z);
        VertexConsumer lv5 = arg2.getBuffer(RenderLayer.getLines());
        if (this.field_4626.containsKey(lv3)) {
            for (BlockBox blockBox : this.field_4626.get(lv3).values()) {
                if (!lv4.isWithinDistance(blockBox.getCenter(), 500.0)) continue;
                WorldRenderer.drawBox(arg, lv5, (double)blockBox.minX - d, (double)blockBox.minY - e, (double)blockBox.minZ - f, (double)(blockBox.maxX + 1) - d, (double)(blockBox.maxY + 1) - e, (double)(blockBox.maxZ + 1) - f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (this.field_4627.containsKey(lv3)) {
            for (Map.Entry entry : this.field_4627.get(lv3).entrySet()) {
                String string = (String)entry.getKey();
                BlockBox lv7 = (BlockBox)entry.getValue();
                Boolean lv8 = this.field_4625.get(lv3).get(string);
                if (!lv4.isWithinDistance(lv7.getCenter(), 500.0)) continue;
                if (lv8.booleanValue()) {
                    WorldRenderer.drawBox(arg, lv5, (double)lv7.minX - d, (double)lv7.minY - e, (double)lv7.minZ - f, (double)(lv7.maxX + 1) - d, (double)(lv7.maxY + 1) - e, (double)(lv7.maxZ + 1) - f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
                    continue;
                }
                WorldRenderer.drawBox(arg, lv5, (double)lv7.minX - d, (double)lv7.minY - e, (double)lv7.minZ - f, (double)(lv7.maxX + 1) - d, (double)(lv7.maxY + 1) - e, (double)(lv7.maxZ + 1) - f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }

    public void method_3871(BlockBox arg, List<BlockBox> list, List<Boolean> list2, DimensionType arg2) {
        if (!this.field_4626.containsKey(arg2)) {
            this.field_4626.put(arg2, Maps.newHashMap());
        }
        if (!this.field_4627.containsKey(arg2)) {
            this.field_4627.put(arg2, Maps.newHashMap());
            this.field_4625.put(arg2, Maps.newHashMap());
        }
        this.field_4626.get(arg2).put(arg.toString(), arg);
        for (int i = 0; i < list.size(); ++i) {
            BlockBox lv = list.get(i);
            Boolean lv2 = list2.get(i);
            this.field_4627.get(arg2).put(lv.toString(), lv);
            this.field_4625.get(arg2).put(lv.toString(), lv2);
        }
    }

    @Override
    public void clear() {
        this.field_4626.clear();
        this.field_4627.clear();
        this.field_4625.clear();
    }
}

