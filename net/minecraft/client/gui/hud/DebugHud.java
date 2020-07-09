/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DataFixUtils
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.longs.LongSets$EmptySet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

@Environment(value=EnvType.CLIENT)
public class DebugHud
extends DrawableHelper {
    private static final Map<Heightmap.Type, String> HEIGHT_MAP_TYPES = Util.make(new EnumMap(Heightmap.Type.class), enumMap -> {
        enumMap.put(Heightmap.Type.WORLD_SURFACE_WG, "SW");
        enumMap.put(Heightmap.Type.WORLD_SURFACE, "S");
        enumMap.put(Heightmap.Type.OCEAN_FLOOR_WG, "OW");
        enumMap.put(Heightmap.Type.OCEAN_FLOOR, "O");
        enumMap.put(Heightmap.Type.MOTION_BLOCKING, "M");
        enumMap.put(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML");
    });
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;
    private HitResult blockHit;
    private HitResult fluidHit;
    @Nullable
    private ChunkPos pos;
    @Nullable
    private WorldChunk chunk;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;

    public DebugHud(MinecraftClient arg) {
        this.client = arg;
        this.fontRenderer = arg.textRenderer;
    }

    public void resetChunk() {
        this.chunkFuture = null;
        this.chunk = null;
    }

    public void render(MatrixStack arg) {
        this.client.getProfiler().push("debug");
        RenderSystem.pushMatrix();
        Entity lv = this.client.getCameraEntity();
        this.blockHit = lv.rayTrace(20.0, 0.0f, false);
        this.fluidHit = lv.rayTrace(20.0, 0.0f, true);
        this.renderLeftText(arg);
        this.renderRightText(arg);
        RenderSystem.popMatrix();
        if (this.client.options.debugTpsEnabled) {
            int i = this.client.getWindow().getScaledWidth();
            this.drawMetricsData(arg, this.client.getMetricsData(), 0, i / 2, true);
            IntegratedServer lv2 = this.client.getServer();
            if (lv2 != null) {
                this.drawMetricsData(arg, lv2.getMetricsData(), i - Math.min(i / 2, 240), i / 2, false);
            }
        }
        this.client.getProfiler().pop();
    }

    protected void renderLeftText(MatrixStack arg) {
        List<String> list = this.getLeftText();
        list.add("");
        boolean bl = this.client.getServer() != null;
        list.add("Debug: Pie [shift]: " + (this.client.options.debugProfilerEnabled ? "visible" : "hidden") + (bl ? " FPS + TPS" : " FPS") + " [alt]: " + (this.client.options.debugTpsEnabled ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty((String)string)) continue;
            this.fontRenderer.getClass();
            int j = 9;
            int k = this.fontRenderer.getWidth(string);
            int l = 2;
            int m = 2 + j * i;
            DebugHud.fill(arg, 1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
            this.fontRenderer.draw(arg, string, 2.0f, (float)m, 0xE0E0E0);
        }
    }

    protected void renderRightText(MatrixStack arg) {
        List<String> list = this.getRightText();
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty((String)string)) continue;
            this.fontRenderer.getClass();
            int j = 9;
            int k = this.fontRenderer.getWidth(string);
            int l = this.client.getWindow().getScaledWidth() - 2 - k;
            int m = 2 + j * i;
            DebugHud.fill(arg, l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
            this.fontRenderer.draw(arg, string, (float)l, (float)m, 0xE0E0E0);
        }
    }

    protected List<String> getLeftText() {
        ShaderEffect lv16;
        World lv7;
        String string7;
        String string2;
        IntegratedServer lv = this.client.getServer();
        ClientConnection lv2 = this.client.getNetworkHandler().getConnection();
        float f = lv2.getAveragePacketsSent();
        float g = lv2.getAveragePacketsReceived();
        if (lv != null) {
            String string = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", Float.valueOf(lv.getTickTime()), Float.valueOf(f), Float.valueOf(g));
        } else {
            string2 = String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.getServerBrand(), Float.valueOf(f), Float.valueOf(g));
        }
        BlockPos lv3 = this.client.getCameraEntity().getBlockPos();
        if (this.client.hasReducedDebugInfo()) {
            return Lists.newArrayList((Object[])new String[]{"Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.client.fpsDebugString, string2, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.getDebugString(), "", String.format("Chunk-relative: %d %d %d", lv3.getX() & 0xF, lv3.getY() & 0xF, lv3.getZ() & 0xF)});
        }
        Entity lv4 = this.client.getCameraEntity();
        Direction lv5 = lv4.getHorizontalFacing();
        switch (lv5) {
            case NORTH: {
                String string3 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                String string4 = "Towards positive Z";
                break;
            }
            case WEST: {
                String string5 = "Towards negative X";
                break;
            }
            case EAST: {
                String string6 = "Towards positive X";
                break;
            }
            default: {
                string7 = "Invalid";
            }
        }
        ChunkPos lv6 = new ChunkPos(lv3);
        if (!Objects.equals(this.pos, lv6)) {
            this.pos = lv6;
            this.resetChunk();
        }
        LongSets.EmptySet longSet = (lv7 = this.getWorld()) instanceof ServerWorld ? ((ServerWorld)lv7).getForcedChunks() : LongSets.EMPTY_SET;
        ArrayList list = Lists.newArrayList((Object[])new String[]{"Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType()) + ")", this.client.fpsDebugString, string2, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.getDebugString()});
        String string8 = this.method_27871();
        if (string8 != null) {
            list.add(string8);
        }
        list.add(this.client.world.getRegistryKey().getValue() + " FC: " + longSet.size());
        list.add("");
        list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().getX(), this.client.getCameraEntity().getY(), this.client.getCameraEntity().getZ()));
        list.add(String.format("Block: %d %d %d", lv3.getX(), lv3.getY(), lv3.getZ()));
        list.add(String.format("Chunk: %d %d %d in %d %d %d", lv3.getX() & 0xF, lv3.getY() & 0xF, lv3.getZ() & 0xF, lv3.getX() >> 4, lv3.getY() >> 4, lv3.getZ() >> 4));
        list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", lv5, string7, Float.valueOf(MathHelper.wrapDegrees(lv4.yaw)), Float.valueOf(MathHelper.wrapDegrees(lv4.pitch))));
        if (this.client.world != null) {
            if (this.client.world.isChunkLoaded(lv3)) {
                WorldChunk lv8 = this.getClientChunk();
                if (lv8.isEmpty()) {
                    list.add("Waiting for chunk...");
                } else {
                    int i = this.client.world.getChunkManager().getLightingProvider().getLight(lv3, 0);
                    int j = this.client.world.getLightLevel(LightType.SKY, lv3);
                    int k = this.client.world.getLightLevel(LightType.BLOCK, lv3);
                    list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
                    WorldChunk lv9 = this.getChunk();
                    if (lv9 != null) {
                        LightingProvider lv10 = lv7.getChunkManager().getLightingProvider();
                        list.add("Server Light: (" + lv10.get(LightType.SKY).getLightLevel(lv3) + " sky, " + lv10.get(LightType.BLOCK).getLightLevel(lv3) + " block)");
                    } else {
                        list.add("Server Light: (?? sky, ?? block)");
                    }
                    StringBuilder stringBuilder = new StringBuilder("CH");
                    for (Heightmap.Type lv11 : Heightmap.Type.values()) {
                        if (!lv11.shouldSendToClient()) continue;
                        stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get(lv11)).append(": ").append(lv8.sampleHeightmap(lv11, lv3.getX(), lv3.getZ()));
                    }
                    list.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    stringBuilder.append("SH");
                    for (Heightmap.Type lv12 : Heightmap.Type.values()) {
                        if (!lv12.isStoredServerSide()) continue;
                        stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get(lv12)).append(": ");
                        if (lv9 != null) {
                            stringBuilder.append(lv9.sampleHeightmap(lv12, lv3.getX(), lv3.getZ()));
                            continue;
                        }
                        stringBuilder.append("??");
                    }
                    list.add(stringBuilder.toString());
                    if (lv3.getY() >= 0 && lv3.getY() < 256) {
                        list.add("Biome: " + this.client.world.method_30349().method_30530(Registry.BIOME_KEY).getId(this.client.world.getBiome(lv3)));
                        long l = 0L;
                        float h = 0.0f;
                        if (lv9 != null) {
                            h = lv7.method_30272();
                            l = lv9.getInhabitedTime();
                        }
                        LocalDifficulty lv13 = new LocalDifficulty(lv7.getDifficulty(), lv7.getTimeOfDay(), l, h);
                        list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", Float.valueOf(lv13.getLocalDifficulty()), Float.valueOf(lv13.getClampedLocalDifficulty()), this.client.world.getTimeOfDay() / 24000L));
                    }
                }
            } else {
                list.add("Outside of world...");
            }
        } else {
            list.add("Outside of world...");
        }
        ServerWorld lv14 = this.getServerWorld();
        if (lv14 != null) {
            SpawnHelper.Info lv15 = lv14.getChunkManager().getSpawnInfo();
            if (lv15 != null) {
                Object2IntMap<SpawnGroup> object2IntMap = lv15.getGroupToCount();
                int m = lv15.getSpawningChunkCount();
                list.add("SC: " + m + ", " + Stream.of(SpawnGroup.values()).map(arg -> Character.toUpperCase(arg.getName().charAt(0)) + ": " + object2IntMap.getInt(arg)).collect(Collectors.joining(", ")));
            } else {
                list.add("SC: N/A");
            }
        }
        if ((lv16 = this.client.gameRenderer.getShader()) != null) {
            list.add("Shader: " + lv16.getName());
        }
        list.add(this.client.getSoundManager().getDebugString() + String.format(" (Mood %d%%)", Math.round(this.client.player.getMoodPercentage() * 100.0f)));
        return list;
    }

    @Nullable
    private ServerWorld getServerWorld() {
        IntegratedServer lv = this.client.getServer();
        if (lv != null) {
            return lv.getWorld(this.client.world.getRegistryKey());
        }
        return null;
    }

    @Nullable
    private String method_27871() {
        ServerWorld lv = this.getServerWorld();
        if (lv != null) {
            return lv.getDebugString();
        }
        return null;
    }

    private World getWorld() {
        return (World)DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).flatMap(arg -> Optional.ofNullable(arg.getWorld(this.client.world.getRegistryKey()))), (Object)this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            ServerWorld lv = this.getServerWorld();
            if (lv != null) {
                this.chunkFuture = lv.getChunkManager().getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, ChunkStatus.FULL, false).thenApply(either -> (WorldChunk)either.map(arg -> (WorldChunk)arg, arg -> null));
            }
            if (this.chunkFuture == null) {
                this.chunkFuture = CompletableFuture.completedFuture(this.getClientChunk());
            }
        }
        return this.chunkFuture.getNow(null);
    }

    private WorldChunk getClientChunk() {
        if (this.chunk == null) {
            this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
        }
        return this.chunk;
    }

    protected List<String> getRightText() {
        Entity lv7;
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        ArrayList list = Lists.newArrayList((Object[])new String[]{String.format("Java: %s %dbit", System.getProperty("java.version"), this.client.is64Bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", o * 100L / l, DebugHud.toMiB(o), DebugHud.toMiB(l)), String.format("Allocated: % 2d%% %03dMB", m * 100L / l, DebugHud.toMiB(m)), "", String.format("CPU: %s", GlDebugInfo.getCpuInfo()), "", String.format("Display: %dx%d (%s)", MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), GlDebugInfo.getVendor()), GlDebugInfo.getRenderer(), GlDebugInfo.getVersion()});
        if (this.client.hasReducedDebugInfo()) {
            return list;
        }
        if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos lv = ((BlockHitResult)this.blockHit).getBlockPos();
            BlockState lv2 = this.client.world.getBlockState(lv);
            list.add("");
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Block: " + lv.getX() + ", " + lv.getY() + ", " + lv.getZ());
            list.add(String.valueOf(Registry.BLOCK.getId(lv2.getBlock())));
            for (Map.Entry entry : lv2.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            for (Identifier lv3 : this.client.getNetworkHandler().getTagManager().getBlocks().getTagsFor(lv2.getBlock())) {
                list.add("#" + lv3);
            }
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            BlockPos lv4 = ((BlockHitResult)this.fluidHit).getBlockPos();
            FluidState lv5 = this.client.world.getFluidState(lv4);
            list.add("");
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Fluid: " + lv4.getX() + ", " + lv4.getY() + ", " + lv4.getZ());
            list.add(String.valueOf(Registry.FLUID.getId(lv5.getFluid())));
            for (Map.Entry entry2 : lv5.getEntries().entrySet()) {
                list.add(this.propertyToString(entry2));
            }
            for (Identifier lv6 : this.client.getNetworkHandler().getTagManager().getFluids().getTagsFor(lv5.getFluid())) {
                list.add("#" + lv6);
            }
        }
        if ((lv7 = this.client.targetedEntity) != null) {
            list.add("");
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Entity");
            list.add(String.valueOf(Registry.ENTITY_TYPE.getId(lv7.getType())));
        }
        return list;
    }

    private String propertyToString(Map.Entry<Property<?>, Comparable<?>> entry) {
        Property<?> lv = entry.getKey();
        Comparable<?> comparable = entry.getValue();
        String string = Util.getValueAsString(lv, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            string = (Object)((Object)Formatting.GREEN) + string;
        } else if (Boolean.FALSE.equals(comparable)) {
            string = (Object)((Object)Formatting.RED) + string;
        }
        return lv.getName() + ": " + string;
    }

    private void drawMetricsData(MatrixStack arg, MetricsData arg2, int i, int j, boolean bl) {
        RenderSystem.disableDepthTest();
        int k = arg2.getStartIndex();
        int l = arg2.getCurrentIndex();
        long[] ls = arg2.getSamples();
        int m = k;
        int n = i;
        int o = Math.max(0, ls.length - j);
        int p = ls.length - o;
        m = arg2.wrapIndex(m + o);
        long q = 0L;
        int r = Integer.MAX_VALUE;
        int s = Integer.MIN_VALUE;
        for (int t = 0; t < p; ++t) {
            int u = (int)(ls[arg2.wrapIndex(m + t)] / 1000000L);
            r = Math.min(r, u);
            s = Math.max(s, u);
            q += (long)u;
        }
        int v = this.client.getWindow().getScaledHeight();
        DebugHud.fill(arg, i, v - 60, i + p, v, -1873784752);
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        lv.begin(7, VertexFormats.POSITION_COLOR);
        Matrix4f lv2 = AffineTransformation.identity().getMatrix();
        while (m != l) {
            int w = arg2.method_15248(ls[m], bl ? 30 : 60, bl ? 60 : 20);
            int x = bl ? 100 : 60;
            int y = this.getMetricsLineColor(MathHelper.clamp(w, 0, x), 0, x / 2, x);
            int z = y >> 24 & 0xFF;
            int aa = y >> 16 & 0xFF;
            int ab = y >> 8 & 0xFF;
            int ac = y & 0xFF;
            lv.vertex(lv2, n + 1, v, 0.0f).color(aa, ab, ac, z).next();
            lv.vertex(lv2, n + 1, v - w + 1, 0.0f).color(aa, ab, ac, z).next();
            lv.vertex(lv2, n, v - w + 1, 0.0f).color(aa, ab, ac, z).next();
            lv.vertex(lv2, n, v, 0.0f).color(aa, ab, ac, z).next();
            ++n;
            m = arg2.wrapIndex(m + 1);
        }
        lv.end();
        BufferRenderer.draw(lv);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        if (bl) {
            DebugHud.fill(arg, i + 1, v - 30 + 1, i + 14, v - 30 + 10, -1873784752);
            this.fontRenderer.draw(arg, "60 FPS", (float)(i + 2), (float)(v - 30 + 2), 0xE0E0E0);
            this.drawHorizontalLine(arg, i, i + p - 1, v - 30, -1);
            DebugHud.fill(arg, i + 1, v - 60 + 1, i + 14, v - 60 + 10, -1873784752);
            this.fontRenderer.draw(arg, "30 FPS", (float)(i + 2), (float)(v - 60 + 2), 0xE0E0E0);
            this.drawHorizontalLine(arg, i, i + p - 1, v - 60, -1);
        } else {
            DebugHud.fill(arg, i + 1, v - 60 + 1, i + 14, v - 60 + 10, -1873784752);
            this.fontRenderer.draw(arg, "20 TPS", (float)(i + 2), (float)(v - 60 + 2), 0xE0E0E0);
            this.drawHorizontalLine(arg, i, i + p - 1, v - 60, -1);
        }
        this.drawHorizontalLine(arg, i, i + p - 1, v - 1, -1);
        this.drawVerticalLine(arg, i, v - 60, v, -1);
        this.drawVerticalLine(arg, i + p - 1, v - 60, v, -1);
        if (bl && this.client.options.maxFps > 0 && this.client.options.maxFps <= 250) {
            this.drawHorizontalLine(arg, i, i + p - 1, v - 1 - (int)(1800.0 / (double)this.client.options.maxFps), -16711681);
        }
        String string = r + " ms min";
        String string2 = q / (long)p + " ms avg";
        String string3 = s + " ms max";
        this.fontRenderer.getClass();
        this.fontRenderer.drawWithShadow(arg, string, (float)(i + 2), (float)(v - 60 - 9), 0xE0E0E0);
        this.fontRenderer.getClass();
        this.fontRenderer.drawWithShadow(arg, string2, (float)(i + p / 2 - this.fontRenderer.getWidth(string2) / 2), (float)(v - 60 - 9), 0xE0E0E0);
        this.fontRenderer.getClass();
        this.fontRenderer.drawWithShadow(arg, string3, (float)(i + p - this.fontRenderer.getWidth(string3)), (float)(v - 60 - 9), 0xE0E0E0);
        RenderSystem.enableDepthTest();
    }

    private int getMetricsLineColor(int i, int j, int k, int l) {
        if (i < k) {
            return this.interpolateColor(-16711936, -256, (float)i / (float)k);
        }
        return this.interpolateColor(-256, -65536, (float)(i - k) / (float)(l - k));
    }

    private int interpolateColor(int i, int j, float f) {
        int k = i >> 24 & 0xFF;
        int l = i >> 16 & 0xFF;
        int m = i >> 8 & 0xFF;
        int n = i & 0xFF;
        int o = j >> 24 & 0xFF;
        int p = j >> 16 & 0xFF;
        int q = j >> 8 & 0xFF;
        int r = j & 0xFF;
        int s = MathHelper.clamp((int)MathHelper.lerp(f, k, o), 0, 255);
        int t = MathHelper.clamp((int)MathHelper.lerp(f, l, p), 0, 255);
        int u = MathHelper.clamp((int)MathHelper.lerp(f, m, q), 0, 255);
        int v = MathHelper.clamp((int)MathHelper.lerp(f, n, r), 0, 255);
        return s << 24 | t << 16 | u << 8 | v;
    }

    private static long toMiB(long l) {
        return l / 1024L / 1024L;
    }
}

