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
    public static Immediate immediate(BufferBuilder arg) {
        return VertexConsumerProvider.immediate((Map<RenderLayer, BufferBuilder>)ImmutableMap.of(), arg);
    }

    public static Immediate immediate(Map<RenderLayer, BufferBuilder> map, BufferBuilder arg) {
        return new Immediate(arg, map);
    }

    public VertexConsumer getBuffer(RenderLayer var1);

    @Environment(value=EnvType.CLIENT)
    public static class Immediate
    implements VertexConsumerProvider {
        protected final BufferBuilder fallbackBuffer;
        protected final Map<RenderLayer, BufferBuilder> layerBuffers;
        protected Optional<RenderLayer> currentLayer = Optional.empty();
        protected final Set<BufferBuilder> activeConsumers = Sets.newHashSet();

        protected Immediate(BufferBuilder arg, Map<RenderLayer, BufferBuilder> map) {
            this.fallbackBuffer = arg;
            this.layerBuffers = map;
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

        private BufferBuilder getBufferInternal(RenderLayer arg) {
            return this.layerBuffers.getOrDefault(arg, this.fallbackBuffer);
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

        public void draw(RenderLayer arg) {
            BufferBuilder lv = this.getBufferInternal(arg);
            boolean bl = Objects.equals(this.currentLayer, arg.asOptional());
            if (!bl && lv == this.fallbackBuffer) {
                return;
            }
            if (!this.activeConsumers.remove(lv)) {
                return;
            }
            arg.draw(lv, 0, 0, 0);
            if (bl) {
                this.currentLayer = Optional.empty();
            }
        }
    }
}

