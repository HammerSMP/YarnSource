/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public class ChunkLoadingDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private double lastUpdateTime = Double.MIN_VALUE;
    private final int field_4511 = 12;
    @Nullable
    private ChunkLoadingStatus loadingData;

    public ChunkLoadingDebugRenderer(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        double g = Util.getMeasuringTimeNano();
        if (g - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = g;
            IntegratedServer lv = this.client.getServer();
            this.loadingData = lv != null ? new ChunkLoadingStatus(lv, d, f) : null;
        }
        if (this.loadingData != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(2.0f);
            RenderSystem.disableTexture();
            RenderSystem.depthMask(false);
            Map map = this.loadingData.serverStates.getNow(null);
            double h = this.client.gameRenderer.getCamera().getPos().y * 0.85;
            for (Map.Entry entry : this.loadingData.clientStates.entrySet()) {
                ChunkPos lv2 = (ChunkPos)entry.getKey();
                String string = (String)entry.getValue();
                if (map != null) {
                    string = string + (String)map.get(lv2);
                }
                String[] strings = string.split("\n");
                int i = 0;
                for (String string2 : strings) {
                    DebugRenderer.drawString(string2, (lv2.x << 4) + 8, h + (double)i, (lv2.z << 4) + 8, -1, 0.15f);
                    i -= 2;
                }
            }
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    @Environment(value=EnvType.CLIENT)
    final class ChunkLoadingStatus {
        private final Map<ChunkPos, String> clientStates;
        private final CompletableFuture<Map<ChunkPos, String>> serverStates;

        private ChunkLoadingStatus(IntegratedServer arg2, double d, double e) {
            ServerWorld lv4;
            ClientWorld lv = ((ChunkLoadingDebugRenderer)ChunkLoadingDebugRenderer.this).client.world;
            DimensionType lv2 = ((ChunkLoadingDebugRenderer)ChunkLoadingDebugRenderer.this).client.world.method_27983();
            if (arg2.getWorld(lv2) != null) {
                ServerWorld lv3 = arg2.getWorld(lv2);
            } else {
                lv4 = null;
            }
            int i = (int)d >> 4;
            int j = (int)e >> 4;
            ImmutableMap.Builder builder = ImmutableMap.builder();
            ClientChunkManager lv5 = lv.getChunkManager();
            for (int k = i - 12; k <= i + 12; ++k) {
                for (int l = j - 12; l <= j + 12; ++l) {
                    ChunkPos lv6 = new ChunkPos(k, l);
                    String string = "";
                    WorldChunk lv7 = lv5.getWorldChunk(k, l, false);
                    string = string + "Client: ";
                    if (lv7 == null) {
                        string = string + "0n/a\n";
                    } else {
                        string = string + (lv7.isEmpty() ? " E" : "");
                        string = string + "\n";
                    }
                    builder.put((Object)lv6, (Object)string);
                }
            }
            this.clientStates = builder.build();
            this.serverStates = arg2.submit(() -> {
                ImmutableMap.Builder builder = ImmutableMap.builder();
                ServerChunkManager lv = lv4.getChunkManager();
                for (int k = i - 12; k <= i + 12; ++k) {
                    for (int l = j - 12; l <= j + 12; ++l) {
                        ChunkPos lv2 = new ChunkPos(k, l);
                        builder.put((Object)lv2, (Object)("Server: " + lv.method_23273(lv2)));
                    }
                }
                return builder.build();
            });
        }
    }
}

