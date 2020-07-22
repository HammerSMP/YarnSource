/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;

@Environment(value=EnvType.CLIENT)
public class BlockBufferBuilderStorage {
    private final Map<RenderLayer, BufferBuilder> builders = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(arg -> arg, arg -> new BufferBuilder(arg.getExpectedBufferSize())));

    public BufferBuilder get(RenderLayer layer) {
        return this.builders.get(layer);
    }

    public void clear() {
        this.builders.values().forEach(BufferBuilder::clear);
    }

    public void reset() {
        this.builders.values().forEach(BufferBuilder::reset);
    }
}

