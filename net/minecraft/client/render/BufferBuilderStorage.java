/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class BufferBuilderStorage {
    private final BlockBufferBuilderStorage blockBuilders = new BlockBufferBuilderStorage();
    private final SortedMap<RenderLayer, BufferBuilder> entityBuilders = (SortedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), object2ObjectLinkedOpenHashMap -> {
        object2ObjectLinkedOpenHashMap.put((Object)TexturedRenderLayers.getEntitySolid(), (Object)this.blockBuilders.get(RenderLayer.getSolid()));
        object2ObjectLinkedOpenHashMap.put((Object)TexturedRenderLayers.getEntityCutout(), (Object)this.blockBuilders.get(RenderLayer.getCutout()));
        object2ObjectLinkedOpenHashMap.put((Object)TexturedRenderLayers.getBannerPatterns(), (Object)this.blockBuilders.get(RenderLayer.getCutoutMipped()));
        object2ObjectLinkedOpenHashMap.put((Object)TexturedRenderLayers.getEntityTranslucentCull(), (Object)this.blockBuilders.get(RenderLayer.getTranslucent()));
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getShieldPatterns());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getBeds());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getShulkerBoxes());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getSign());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getChest());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, RenderLayer.getTranslucentNoCrumbling());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, RenderLayer.getArmorGlint());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, RenderLayer.getArmorEntityGlint());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, RenderLayer.getGlint());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, RenderLayer.getGlintDirect());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, RenderLayer.getEntityGlint());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, RenderLayer.getEntityGlintDirect());
        BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, RenderLayer.getWaterMask());
        ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach(arg -> BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>)object2ObjectLinkedOpenHashMap, arg));
    });
    private final VertexConsumerProvider.Immediate entityVertexConsumers = VertexConsumerProvider.immediate(this.entityBuilders, new BufferBuilder(256));
    private final VertexConsumerProvider.Immediate effectVertexConsumers = VertexConsumerProvider.immediate(new BufferBuilder(256));
    private final OutlineVertexConsumerProvider outlineVertexConsumers = new OutlineVertexConsumerProvider(this.entityVertexConsumers);

    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> object2ObjectLinkedOpenHashMap, RenderLayer arg) {
        object2ObjectLinkedOpenHashMap.put((Object)arg, (Object)new BufferBuilder(arg.getExpectedBufferSize()));
    }

    public BlockBufferBuilderStorage getBlockBufferBuilders() {
        return this.blockBuilders;
    }

    public VertexConsumerProvider.Immediate getEntityVertexConsumers() {
        return this.entityVertexConsumers;
    }

    public VertexConsumerProvider.Immediate getEffectVertexConsumers() {
        return this.effectVertexConsumers;
    }

    public OutlineVertexConsumerProvider getOutlineVertexConsumers() {
        return this.outlineVertexConsumers;
    }
}

