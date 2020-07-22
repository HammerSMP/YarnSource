/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;

@Environment(value=EnvType.CLIENT)
public interface VertexConsumerProvider {
    public static Immediate immediate(BufferBuilder buffer) {
        return VertexConsumerProvider.immediate((Map<RenderLayer, BufferBuilder>)ImmutableMap.of(), buffer);
    }

    public static Immediate immediate(Map<RenderLayer, BufferBuilder> layerBuffers, BufferBuilder fallbackBuffer) {
        return new Immediate(fallbackBuffer, layerBuffers);
    }

    public VertexConsumer getBuffer(RenderLayer var1);

    @Environment(value=EnvType.CLIENT)
    public static class Immediate
    implements VertexConsumerProvider {
        protected final BufferBuilder fallbackBuffer;
        protected final Map<RenderLayer, BufferBuilder> layerBuffers;
        protected Optional<RenderLayer> currentLayer = Optional.empty();
        protected final Set<BufferBuilder> activeConsumers = Sets.newHashSet();

        protected Immediate(BufferBuilder fallbackBuffer, Map<RenderLayer, BufferBuilder> layerBuffers) {
            this.fallbackBuffer = fallbackBuffer;
            this.layerBuffers = layerBuffers;
        }

        @Override
        public VertexConsumer getBuffer(RenderLayer arg) {
            Optional<RenderLayer> optional = arg.asOptional();
            BufferBuilder lv = this.getBufferInternal(arg);
            if (!Objects.equals(this.currentLayer, optional)) {
                RenderLayer lv2;
                if (this.currentLayer.isPresent() && !this.layerBuffers.containsKey(lv2 = this.currentLayer.get())) {
                    this.draw(lv2);
                }
                if (this.activeConsumers.add(lv)) {
                    lv.begin(arg.getDrawMode(), arg.getVertexFormat());
                }
                this.currentLayer = optional;
            }
            return lv;
        }

        private BufferBuilder getBufferInternal(RenderLayer layer) {
            return this.layerBuffers.getOrDefault(layer, this.fallbackBuffer);
        }

        public void draw() {
            this.currentLayer.ifPresent(arg -> {
                VertexConsumer lv = this.getBuffer((RenderLayer)arg);
                if (lv == this.fallbackBuffer) {
                    this.draw((RenderLayer)arg);
                }
            });
            for (RenderLayer lv : this.layerBuffers.keySet()) {
                this.draw(lv);
            }
        }

        public void draw(RenderLayer layer) {
            BufferBuilder lv = this.getBufferInternal(layer);
            boolean bl = Objects.equals(this.currentLayer, layer.asOptional());
            if (!bl && lv == this.fallbackBuffer) {
                return;
            }
            if (!this.activeConsumers.remove(lv)) {
                return;
            }
            layer.draw(lv, 0, 0, 0);
            if (bl) {
                this.currentLayer = Optional.empty();
            }
        }
    }
}

