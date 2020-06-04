/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.Option;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.FpsSmoother;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.TransformingVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldRenderer
implements SynchronousResourceReloadListener,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
    private static final Identifier SUN = new Identifier("textures/environment/sun.png");
    private static final Identifier CLOUDS = new Identifier("textures/environment/clouds.png");
    private static final Identifier END_SKY = new Identifier("textures/environment/end_sky.png");
    private static final Identifier FORCEFIELD = new Identifier("textures/misc/forcefield.png");
    private static final Identifier RAIN = new Identifier("textures/environment/rain.png");
    private static final Identifier SNOW = new Identifier("textures/environment/snow.png");
    public static final Direction[] DIRECTIONS = Direction.values();
    private final MinecraftClient client;
    private final TextureManager textureManager;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final BufferBuilderStorage bufferBuilders;
    private ClientWorld world;
    private Set<ChunkBuilder.BuiltChunk> chunksToRebuild = Sets.newLinkedHashSet();
    private final ObjectList<ChunkInfo> visibleChunks = new ObjectArrayList(69696);
    private final Set<BlockEntity> noCullingBlockEntities = Sets.newHashSet();
    private BuiltChunkStorage chunks;
    private final VertexFormat skyVertexFormat = VertexFormats.POSITION;
    @Nullable
    private VertexBuffer starsBuffer;
    @Nullable
    private VertexBuffer lightSkyBuffer;
    @Nullable
    private VertexBuffer darkSkyBuffer;
    private boolean cloudsDirty = true;
    @Nullable
    private VertexBuffer cloudsBuffer;
    private final FpsSmoother chunkUpdateSmoother = new FpsSmoother(100);
    private int ticks;
    private final Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos = new Int2ObjectOpenHashMap();
    private final Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions = new Long2ObjectOpenHashMap();
    private final Map<BlockPos, SoundInstance> playingSongs = Maps.newHashMap();
    @Nullable
    private Framebuffer entityOutlinesFramebuffer;
    @Nullable
    private ShaderEffect entityOutlineShader;
    @Nullable
    private Framebuffer translucentFramebuffer;
    @Nullable
    private Framebuffer entityFramebuffer;
    @Nullable
    private Framebuffer particlesFramebuffer;
    @Nullable
    private Framebuffer weatherFramebuffer;
    @Nullable
    private Framebuffer cloudsFramebuffer;
    @Nullable
    private ShaderEffect transparencyShader;
    private double lastCameraChunkUpdateX = Double.MIN_VALUE;
    private double lastCameraChunkUpdateY = Double.MIN_VALUE;
    private double lastCameraChunkUpdateZ = Double.MIN_VALUE;
    private int cameraChunkX = Integer.MIN_VALUE;
    private int cameraChunkY = Integer.MIN_VALUE;
    private int cameraChunkZ = Integer.MIN_VALUE;
    private double lastCameraX = Double.MIN_VALUE;
    private double lastCameraY = Double.MIN_VALUE;
    private double lastCameraZ = Double.MIN_VALUE;
    private double lastCameraPitch = Double.MIN_VALUE;
    private double lastCameraYaw = Double.MIN_VALUE;
    private int lastCloudsBlockX = Integer.MIN_VALUE;
    private int lastCloudsBlockY = Integer.MIN_VALUE;
    private int lastCloudsBlockZ = Integer.MIN_VALUE;
    private Vec3d lastCloudsColor = Vec3d.ZERO;
    private CloudRenderMode lastCloudsRenderMode;
    private ChunkBuilder chunkBuilder;
    private final VertexFormat vertexFormat = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
    private int renderDistance = -1;
    private int regularEntityCount;
    private int blockEntityCount;
    private boolean shouldCaptureFrustum;
    @Nullable
    private Frustum capturedFrustum;
    private final Vector4f[] capturedFrustrumOrientation = new Vector4f[8];
    private final Vector3d capturedFrustumPosition = new Vector3d(0.0, 0.0, 0.0);
    private double lastTranslucentSortX;
    private double lastTranslucentSortY;
    private double lastTranslucentSortZ;
    private boolean needsTerrainUpdate = true;
    private int frame;
    private int field_20793;
    private final float[] field_20794 = new float[1024];
    private final float[] field_20795 = new float[1024];

    public WorldRenderer(MinecraftClient arg, BufferBuilderStorage arg2) {
        this.client = arg;
        this.entityRenderDispatcher = arg.getEntityRenderManager();
        this.bufferBuilders = arg2;
        this.textureManager = arg.getTextureManager();
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f = j - 16;
                float g = i - 16;
                float h = MathHelper.sqrt(f * f + g * g);
                this.field_20794[i << 5 | j] = -g / h;
                this.field_20795[i << 5 | j] = f / h;
            }
        }
        this.renderStars();
        this.renderLightSky();
        this.renderDarkSky();
    }

    private void renderWeather(LightmapTextureManager arg, float f, double d, double e, double g) {
        float h = this.client.world.getRainGradient(f);
        if (h <= 0.0f) {
            return;
        }
        arg.enable();
        ClientWorld lv = this.client.world;
        int i = MathHelper.floor(d);
        int j = MathHelper.floor(e);
        int k = MathHelper.floor(g);
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.normal3f(0.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        int l = 5;
        if (this.client.options.fancyGraphics) {
            l = 10;
        }
        int m = -1;
        float n = (float)this.ticks + f;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        BlockPos.Mutable lv4 = new BlockPos.Mutable();
        for (int o = k - l; o <= k + l; ++o) {
            for (int p = i - l; p <= i + l; ++p) {
                int w;
                int q = (o - k + 16) * 32 + p - i + 16;
                double r = (double)this.field_20794[q] * 0.5;
                double s = (double)this.field_20795[q] * 0.5;
                lv4.set(p, 0, o);
                Biome lv5 = lv.getBiome(lv4);
                if (lv5.getPrecipitation() == Biome.Precipitation.NONE) continue;
                int t = lv.getTopPosition(Heightmap.Type.MOTION_BLOCKING, lv4).getY();
                int u = j - l;
                int v = j + l;
                if (u < t) {
                    u = t;
                }
                if (v < t) {
                    v = t;
                }
                if ((w = t) < j) {
                    w = j;
                }
                if (u == v) continue;
                Random random = new Random(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761);
                lv4.set(p, u, o);
                float x = lv5.getTemperature(lv4);
                if (x >= 0.15f) {
                    if (m != 0) {
                        if (m >= 0) {
                            lv2.draw();
                        }
                        m = 0;
                        this.client.getTextureManager().bindTexture(RAIN);
                        lv3.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                    }
                    int y = this.ticks + p * p * 3121 + p * 45238971 + o * o * 418711 + o * 13761 & 0x1F;
                    float z = -((float)y + f) / 32.0f * (3.0f + random.nextFloat());
                    double aa = (double)((float)p + 0.5f) - d;
                    double ab = (double)((float)o + 0.5f) - g;
                    float ac = MathHelper.sqrt(aa * aa + ab * ab) / (float)l;
                    float ad = ((1.0f - ac * ac) * 0.5f + 0.5f) * h;
                    lv4.set(p, w, o);
                    int ae = WorldRenderer.getLightmapCoordinates(lv, lv4);
                    lv3.vertex((double)p - d - r + 0.5, (double)v - e, (double)o - g - s + 0.5).texture(0.0f, (float)u * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    lv3.vertex((double)p - d + r + 0.5, (double)v - e, (double)o - g + s + 0.5).texture(1.0f, (float)u * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    lv3.vertex((double)p - d + r + 0.5, (double)u - e, (double)o - g + s + 0.5).texture(1.0f, (float)v * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    lv3.vertex((double)p - d - r + 0.5, (double)u - e, (double)o - g - s + 0.5).texture(0.0f, (float)v * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    continue;
                }
                if (m != 1) {
                    if (m >= 0) {
                        lv2.draw();
                    }
                    m = 1;
                    this.client.getTextureManager().bindTexture(SNOW);
                    lv3.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                }
                float af = -((float)(this.ticks & 0x1FF) + f) / 512.0f;
                float ag = (float)(random.nextDouble() + (double)n * 0.01 * (double)((float)random.nextGaussian()));
                float ah = (float)(random.nextDouble() + (double)(n * (float)random.nextGaussian()) * 0.001);
                double ai = (double)((float)p + 0.5f) - d;
                double aj = (double)((float)o + 0.5f) - g;
                float ak = MathHelper.sqrt(ai * ai + aj * aj) / (float)l;
                float al = ((1.0f - ak * ak) * 0.3f + 0.5f) * h;
                lv4.set(p, w, o);
                int am = WorldRenderer.getLightmapCoordinates(lv, lv4);
                int an = am >> 16 & 0xFFFF;
                int ao = (am & 0xFFFF) * 3;
                int ap = (an * 3 + 240) / 4;
                int aq = (ao * 3 + 240) / 4;
                lv3.vertex((double)p - d - r + 0.5, (double)v - e, (double)o - g - s + 0.5).texture(0.0f + ag, (float)u * 0.25f + af + ah).color(1.0f, 1.0f, 1.0f, al).light(aq, ap).next();
                lv3.vertex((double)p - d + r + 0.5, (double)v - e, (double)o - g + s + 0.5).texture(1.0f + ag, (float)u * 0.25f + af + ah).color(1.0f, 1.0f, 1.0f, al).light(aq, ap).next();
                lv3.vertex((double)p - d + r + 0.5, (double)u - e, (double)o - g + s + 0.5).texture(1.0f + ag, (float)v * 0.25f + af + ah).color(1.0f, 1.0f, 1.0f, al).light(aq, ap).next();
                lv3.vertex((double)p - d - r + 0.5, (double)u - e, (double)o - g - s + 0.5).texture(0.0f + ag, (float)v * 0.25f + af + ah).color(1.0f, 1.0f, 1.0f, al).light(aq, ap).next();
            }
        }
        if (m >= 0) {
            lv2.draw();
        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableAlphaTest();
        arg.disable();
    }

    public void method_22713(Camera arg) {
        float f = this.client.world.getRainGradient(1.0f) / (this.client.options.fancyGraphics ? 1.0f : 2.0f);
        if (f <= 0.0f) {
            return;
        }
        Random random = new Random((long)this.ticks * 312987231L);
        ClientWorld lv = this.client.world;
        BlockPos lv2 = new BlockPos(arg.getPos());
        Vec3i lv3 = null;
        int i = (int)(100.0f * f * f) / (this.client.options.particles == ParticlesOption.DECREASED ? 2 : 1);
        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(21) - 10;
            int l = random.nextInt(21) - 10;
            BlockPos lv4 = lv.getTopPosition(Heightmap.Type.MOTION_BLOCKING, lv2.add(k, 0, l)).down();
            Biome lv5 = lv.getBiome(lv4);
            if (lv4.getY() <= 0 || lv4.getY() > lv2.getY() + 10 || lv4.getY() < lv2.getY() - 10 || lv5.getPrecipitation() != Biome.Precipitation.RAIN || !(lv5.getTemperature(lv4) >= 0.15f)) continue;
            lv3 = lv4;
            if (this.client.options.particles == ParticlesOption.MINIMAL) break;
            float g = random.nextFloat();
            float h = random.nextFloat();
            BlockState lv6 = lv.getBlockState((BlockPos)lv3);
            FluidState lv7 = lv.getFluidState((BlockPos)lv3);
            VoxelShape lv8 = lv6.getCollisionShape(lv, (BlockPos)lv3);
            float m = (float)lv8.getEndingCoord(Direction.Axis.Y, g, h);
            float n = lv7.getHeight(lv, (BlockPos)lv3);
            float o = Math.max(m, n);
            DefaultParticleType lv9 = lv7.matches(FluidTags.LAVA) || lv6.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(lv6) ? ParticleTypes.SMOKE : ParticleTypes.RAIN;
            this.client.world.addParticle(lv9, (float)lv3.getX() + g, (float)lv3.getY() + o, (float)lv3.getZ() + h, 0.0, 0.0, 0.0);
        }
        if (lv3 != null && random.nextInt(3) < this.field_20793++) {
            this.field_20793 = 0;
            if (lv3.getY() > lv2.getY() + 1 && lv.getTopPosition(Heightmap.Type.MOTION_BLOCKING, lv2).getY() > MathHelper.floor(lv2.getY())) {
                this.client.world.playSound((BlockPos)lv3, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1f, 0.5f, false);
            } else {
                this.client.world.playSound((BlockPos)lv3, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    @Override
    public void close() {
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.close();
        }
        if (this.transparencyShader != null) {
            this.transparencyShader.close();
        }
    }

    @Override
    public void apply(ResourceManager arg) {
        this.textureManager.bindTexture(FORCEFIELD);
        RenderSystem.texParameter(3553, 10242, 10497);
        RenderSystem.texParameter(3553, 10243, 10497);
        RenderSystem.bindTexture(0);
        this.loadEntityOutlineShader();
        this.loadTransparencyShader();
    }

    public void loadEntityOutlineShader() {
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.close();
        }
        Identifier lv = new Identifier("shaders/post/entity_outline.json");
        try {
            this.entityOutlineShader = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), lv);
            this.entityOutlineShader.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
            this.entityOutlinesFramebuffer = this.entityOutlineShader.getSecondaryTarget("final");
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", (Object)lv, (Object)iOException);
            this.entityOutlineShader = null;
            this.entityOutlinesFramebuffer = null;
        }
        catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to parse shader: {}", (Object)lv, (Object)jsonSyntaxException);
            this.entityOutlineShader = null;
            this.entityOutlinesFramebuffer = null;
        }
    }

    private void loadTransparencyShader() {
        if (this.transparencyShader != null) {
            this.transparencyShader.close();
        }
        Identifier lv = new Identifier("shaders/post/transparency.json");
        try {
            this.transparencyShader = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), lv);
            this.transparencyShader.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
            this.translucentFramebuffer = this.transparencyShader.getSecondaryTarget("translucent");
            this.entityFramebuffer = this.transparencyShader.getSecondaryTarget("itemEntity");
            this.particlesFramebuffer = this.transparencyShader.getSecondaryTarget("particles");
            this.weatherFramebuffer = this.transparencyShader.getSecondaryTarget("weather");
            this.cloudsFramebuffer = this.transparencyShader.getSecondaryTarget("clouds");
        }
        catch (IOException iOException) {
            throw new class_5347("Failed to load shader: " + lv, iOException);
        }
        catch (JsonSyntaxException jsonSyntaxException) {
            throw new class_5347("Failed to parse shader: " + lv, jsonSyntaxException);
        }
    }

    public void drawEntityOutlinesFramebuffer() {
        if (this.canDrawEntityOutlines()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            this.entityOutlinesFramebuffer.draw(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), false);
            RenderSystem.disableBlend();
        }
    }

    protected boolean canDrawEntityOutlines() {
        return this.entityOutlinesFramebuffer != null && this.entityOutlineShader != null && this.client.player != null;
    }

    private void renderDarkSky() {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        if (this.darkSkyBuffer != null) {
            this.darkSkyBuffer.close();
        }
        this.darkSkyBuffer = new VertexBuffer(this.skyVertexFormat);
        this.renderSkyHalf(lv2, -16.0f, true);
        lv2.end();
        this.darkSkyBuffer.upload(lv2);
    }

    private void renderLightSky() {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        if (this.lightSkyBuffer != null) {
            this.lightSkyBuffer.close();
        }
        this.lightSkyBuffer = new VertexBuffer(this.skyVertexFormat);
        this.renderSkyHalf(lv2, 16.0f, false);
        lv2.end();
        this.lightSkyBuffer.upload(lv2);
    }

    private void renderSkyHalf(BufferBuilder arg, float f, boolean bl) {
        int i = 64;
        int j = 6;
        arg.begin(7, VertexFormats.POSITION);
        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {
                float g = k;
                float h = k + 64;
                if (bl) {
                    h = k;
                    g = k + 64;
                }
                arg.vertex(g, f, l).next();
                arg.vertex(h, f, l).next();
                arg.vertex(h, f, l + 64).next();
                arg.vertex(g, f, l + 64).next();
            }
        }
    }

    private void renderStars() {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        if (this.starsBuffer != null) {
            this.starsBuffer.close();
        }
        this.starsBuffer = new VertexBuffer(this.skyVertexFormat);
        this.renderStars(lv2);
        lv2.end();
        this.starsBuffer.upload(lv2);
    }

    private void renderStars(BufferBuilder arg) {
        Random random = new Random(10842L);
        arg.begin(7, VertexFormats.POSITION);
        for (int i = 0; i < 1500; ++i) {
            double d = random.nextFloat() * 2.0f - 1.0f;
            double e = random.nextFloat() * 2.0f - 1.0f;
            double f = random.nextFloat() * 2.0f - 1.0f;
            double g = 0.15f + random.nextFloat() * 0.1f;
            double h = d * d + e * e + f * f;
            if (!(h < 1.0) || !(h > 0.01)) continue;
            h = 1.0 / Math.sqrt(h);
            double j = (d *= h) * 100.0;
            double k = (e *= h) * 100.0;
            double l = (f *= h) * 100.0;
            double m = Math.atan2(d, f);
            double n = Math.sin(m);
            double o = Math.cos(m);
            double p = Math.atan2(Math.sqrt(d * d + f * f), e);
            double q = Math.sin(p);
            double r = Math.cos(p);
            double s = random.nextDouble() * Math.PI * 2.0;
            double t = Math.sin(s);
            double u = Math.cos(s);
            for (int v = 0; v < 4; ++v) {
                double ab;
                double w = 0.0;
                double x = (double)((v & 2) - 1) * g;
                double y = (double)((v + 1 & 2) - 1) * g;
                double z = 0.0;
                double aa = x * u - y * t;
                double ac = ab = y * u + x * t;
                double ad = aa * q + 0.0 * r;
                double ae = 0.0 * q - aa * r;
                double af = ae * n - ac * o;
                double ag = ad;
                double ah = ac * n + ae * o;
                arg.vertex(j + af, k + ag, l + ah).next();
            }
        }
    }

    public void setWorld(@Nullable ClientWorld arg) {
        this.lastCameraChunkUpdateX = Double.MIN_VALUE;
        this.lastCameraChunkUpdateY = Double.MIN_VALUE;
        this.lastCameraChunkUpdateZ = Double.MIN_VALUE;
        this.cameraChunkX = Integer.MIN_VALUE;
        this.cameraChunkY = Integer.MIN_VALUE;
        this.cameraChunkZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setWorld(arg);
        this.world = arg;
        if (arg != null) {
            this.reload();
        } else {
            this.chunksToRebuild.clear();
            this.visibleChunks.clear();
            if (this.chunks != null) {
                this.chunks.clear();
                this.chunks = null;
            }
            if (this.chunkBuilder != null) {
                this.chunkBuilder.stop();
            }
            this.chunkBuilder = null;
            this.noCullingBlockEntities.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        Entity lv;
        if (this.world == null) {
            return;
        }
        this.world.reloadColor();
        if (this.chunkBuilder == null) {
            this.chunkBuilder = new ChunkBuilder(this.world, this, Util.getServerWorkerExecutor(), this.client.is64Bit(), this.bufferBuilders.getBlockBufferBuilders());
        } else {
            this.chunkBuilder.setWorld(this.world);
        }
        this.needsTerrainUpdate = true;
        this.cloudsDirty = true;
        RenderLayers.setFancyGraphics(this.client.options.fancyGraphics);
        this.renderDistance = this.client.options.viewDistance;
        if (this.chunks != null) {
            this.chunks.clear();
        }
        this.clearChunkRenderers();
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.clear();
        }
        this.chunks = new BuiltChunkStorage(this.chunkBuilder, this.world, this.client.options.viewDistance, this);
        if (this.world != null && (lv = this.client.getCameraEntity()) != null) {
            this.chunks.updateCameraPosition(lv.getX(), lv.getZ());
        }
    }

    protected void clearChunkRenderers() {
        this.chunksToRebuild.clear();
        this.chunkBuilder.reset();
    }

    public void onResized(int i, int j) {
        this.scheduleTerrainUpdate();
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.setupDimensions(i, j);
        }
        if (this.transparencyShader != null) {
            this.transparencyShader.setupDimensions(i, j);
        }
    }

    public String getChunksDebugString() {
        int i = this.chunks.chunks.length;
        int j = this.getCompletedChunkCount();
        return String.format("C: %d/%d %sD: %d, %s", j, i, this.client.chunkCullingEnabled ? "(s) " : "", this.renderDistance, this.chunkBuilder == null ? "null" : this.chunkBuilder.getDebugString());
    }

    protected int getCompletedChunkCount() {
        int i = 0;
        for (ChunkInfo lv : this.visibleChunks) {
            if (lv.chunk.getData().isEmpty()) continue;
            ++i;
        }
        return i;
    }

    public String getEntitiesDebugString() {
        return "E: " + this.regularEntityCount + "/" + this.world.getRegularEntityCount() + ", B: " + this.blockEntityCount;
    }

    private void setupTerrain(Camera arg, Frustum arg22, boolean bl, int i, boolean bl2) {
        Vec3d lv = arg.getPos();
        if (this.client.options.viewDistance != this.renderDistance) {
            this.reload();
        }
        this.world.getProfiler().push("camera");
        double d = this.client.player.getX() - this.lastCameraChunkUpdateX;
        double e = this.client.player.getY() - this.lastCameraChunkUpdateY;
        double f = this.client.player.getZ() - this.lastCameraChunkUpdateZ;
        if (this.cameraChunkX != this.client.player.chunkX || this.cameraChunkY != this.client.player.chunkY || this.cameraChunkZ != this.client.player.chunkZ || d * d + e * e + f * f > 16.0) {
            this.lastCameraChunkUpdateX = this.client.player.getX();
            this.lastCameraChunkUpdateY = this.client.player.getY();
            this.lastCameraChunkUpdateZ = this.client.player.getZ();
            this.cameraChunkX = this.client.player.chunkX;
            this.cameraChunkY = this.client.player.chunkY;
            this.cameraChunkZ = this.client.player.chunkZ;
            this.chunks.updateCameraPosition(this.client.player.getX(), this.client.player.getZ());
        }
        this.chunkBuilder.setCameraPosition(lv);
        this.world.getProfiler().swap("cull");
        this.client.getProfiler().swap("culling");
        BlockPos lv2 = arg.getBlockPos();
        ChunkBuilder.BuiltChunk lv3 = this.chunks.getRenderedChunk(lv2);
        int j = 16;
        BlockPos lv4 = new BlockPos(MathHelper.floor(lv.x / 16.0) * 16, MathHelper.floor(lv.y / 16.0) * 16, MathHelper.floor(lv.z / 16.0) * 16);
        float g = arg.getPitch();
        float h = arg.getYaw();
        this.needsTerrainUpdate = this.needsTerrainUpdate || !this.chunksToRebuild.isEmpty() || lv.x != this.lastCameraX || lv.y != this.lastCameraY || lv.z != this.lastCameraZ || (double)g != this.lastCameraPitch || (double)h != this.lastCameraYaw;
        this.lastCameraX = lv.x;
        this.lastCameraY = lv.y;
        this.lastCameraZ = lv.z;
        this.lastCameraPitch = g;
        this.lastCameraYaw = h;
        this.client.getProfiler().swap("update");
        if (!bl && this.needsTerrainUpdate) {
            this.needsTerrainUpdate = false;
            this.visibleChunks.clear();
            ArrayDeque queue = Queues.newArrayDeque();
            Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.viewDistance / 8.0, 1.0, 2.5) * (double)this.client.options.entityDistanceScaling);
            boolean bl3 = this.client.chunkCullingEnabled;
            if (lv3 == null) {
                int k = lv2.getY() > 0 ? 248 : 8;
                int l = MathHelper.floor(lv.x / 16.0) * 16;
                int m = MathHelper.floor(lv.z / 16.0) * 16;
                Direction[] list = Lists.newArrayList();
                for (int n = -this.renderDistance; n <= this.renderDistance; ++n) {
                    for (int o = -this.renderDistance; o <= this.renderDistance; ++o) {
                        ChunkBuilder.BuiltChunk lv5 = this.chunks.getRenderedChunk(new BlockPos(l + (n << 4) + 8, k, m + (o << 4) + 8));
                        if (lv5 == null || !arg22.isVisible(lv5.boundingBox)) continue;
                        lv5.setRebuildFrame(i);
                        list.add(new ChunkInfo(lv5, null, 0));
                    }
                }
                list.sort(Comparator.comparingDouble(arg2 -> lv2.getSquaredDistance(((ChunkInfo)arg2).chunk.getOrigin().add(8, 8, 8))));
                queue.addAll(list);
            } else {
                if (bl2 && this.world.getBlockState(lv2).isOpaqueFullCube(this.world, lv2)) {
                    bl3 = false;
                }
                lv3.setRebuildFrame(i);
                queue.add(new ChunkInfo(lv3, null, 0));
            }
            this.client.getProfiler().push("iteration");
            while (!queue.isEmpty()) {
                ChunkInfo lv6 = (ChunkInfo)queue.poll();
                ChunkBuilder.BuiltChunk lv7 = lv6.chunk;
                Direction lv8 = lv6.direction;
                this.visibleChunks.add((Object)lv6);
                for (Direction lv9 : DIRECTIONS) {
                    ChunkBuilder.BuiltChunk lv10 = this.getAdjacentChunk(lv4, lv7, lv9);
                    if (bl3 && lv6.canCull(lv9.getOpposite()) || bl3 && lv8 != null && !lv7.getData().isVisibleThrough(lv8.getOpposite(), lv9) || lv10 == null || !lv10.shouldBuild() || !lv10.setRebuildFrame(i) || !arg22.isVisible(lv10.boundingBox)) continue;
                    ChunkInfo lv11 = new ChunkInfo(lv10, lv9, lv6.propagationLevel + 1);
                    lv11.updateCullingState(lv6.cullingState, lv9);
                    queue.add(lv11);
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().swap("rebuildNear");
        Set<ChunkBuilder.BuiltChunk> set = this.chunksToRebuild;
        this.chunksToRebuild = Sets.newLinkedHashSet();
        for (ChunkInfo lv12 : this.visibleChunks) {
            boolean bl4;
            ChunkBuilder.BuiltChunk lv13 = lv12.chunk;
            if (!lv13.needsRebuild() && !set.contains(lv13)) continue;
            this.needsTerrainUpdate = true;
            BlockPos lv14 = lv13.getOrigin().add(8, 8, 8);
            boolean bl3 = bl4 = lv14.getSquaredDistance(lv2) < 768.0;
            if (lv13.needsImportantRebuild() || bl4) {
                this.client.getProfiler().push("build near");
                this.chunkBuilder.rebuild(lv13);
                lv13.cancelRebuild();
                this.client.getProfiler().pop();
                continue;
            }
            this.chunksToRebuild.add(lv13);
        }
        this.chunksToRebuild.addAll(set);
        this.client.getProfiler().pop();
    }

    @Nullable
    private ChunkBuilder.BuiltChunk getAdjacentChunk(BlockPos arg, ChunkBuilder.BuiltChunk arg2, Direction arg3) {
        BlockPos lv = arg2.getNeighborPosition(arg3);
        if (MathHelper.abs(arg.getX() - lv.getX()) > this.renderDistance * 16) {
            return null;
        }
        if (lv.getY() < 0 || lv.getY() >= 256) {
            return null;
        }
        if (MathHelper.abs(arg.getZ() - lv.getZ()) > this.renderDistance * 16) {
            return null;
        }
        return this.chunks.getRenderedChunk(lv);
    }

    private void captureFrustum(Matrix4f arg, Matrix4f arg2, double d, double e, double f, Frustum arg3) {
        this.capturedFrustum = arg3;
        Matrix4f lv = arg2.copy();
        lv.multiply(arg);
        lv.invert();
        this.capturedFrustumPosition.x = d;
        this.capturedFrustumPosition.y = e;
        this.capturedFrustumPosition.z = f;
        this.capturedFrustrumOrientation[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.capturedFrustrumOrientation[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.capturedFrustrumOrientation[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.capturedFrustrumOrientation[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.capturedFrustrumOrientation[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.capturedFrustrumOrientation[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.capturedFrustrumOrientation[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.capturedFrustrumOrientation[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 8; ++i) {
            this.capturedFrustrumOrientation[i].transform(lv);
            this.capturedFrustrumOrientation[i].normalizeProjectiveCoordinates();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(MatrixStack arg, float f, long l, boolean bl, Camera arg2, GameRenderer arg32, LightmapTextureManager arg4, Matrix4f arg5) {
        long o;
        boolean bl3;
        Frustum lv5;
        boolean bl2;
        BlockEntityRenderDispatcher.INSTANCE.configure(this.world, this.client.getTextureManager(), this.client.textRenderer, arg2, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, arg2, this.client.targetedEntity);
        Profiler lv = this.world.getProfiler();
        lv.swap("light_updates");
        this.client.world.getChunkManager().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
        Vec3d lv2 = arg2.getPos();
        double d = lv2.getX();
        double e = lv2.getY();
        double g = lv2.getZ();
        Matrix4f lv3 = arg.peek().getModel();
        lv.swap("culling");
        boolean bl4 = bl2 = this.capturedFrustum != null;
        if (bl2) {
            Frustum lv4 = this.capturedFrustum;
            lv4.setPosition(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
        } else {
            lv5 = new Frustum(lv3, arg5);
            lv5.setPosition(d, e, g);
        }
        this.client.getProfiler().swap("captureFrustum");
        if (this.shouldCaptureFrustum) {
            this.captureFrustum(lv3, arg5, lv2.x, lv2.y, lv2.z, bl2 ? new Frustum(lv3, arg5) : lv5);
            this.shouldCaptureFrustum = false;
        }
        lv.swap("clear");
        BackgroundRenderer.render(arg2, f, this.client.world, this.client.options.viewDistance, arg32.getSkyDarkness(f));
        RenderSystem.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
        float h = arg32.getViewDistance();
        boolean bl5 = bl3 = this.client.world.getSkyProperties().useThickFog(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();
        if (this.client.options.viewDistance >= 4) {
            BackgroundRenderer.applyFog(arg2, BackgroundRenderer.FogType.FOG_SKY, h, bl3);
            lv.swap("sky");
            this.renderSky(arg, f);
        }
        lv.swap("fog");
        BackgroundRenderer.applyFog(arg2, BackgroundRenderer.FogType.FOG_TERRAIN, Math.max(h - 16.0f, 32.0f), bl3);
        lv.swap("terrain_setup");
        this.setupTerrain(arg2, lv5, bl2, this.frame++, this.client.player.isSpectator());
        lv.swap("updatechunks");
        int i = 30;
        int j = this.client.options.maxFps;
        long m = 33333333L;
        if ((double)j == Option.FRAMERATE_LIMIT.getMax()) {
            long n = 0L;
        } else {
            o = 1000000000 / j;
        }
        long p = Util.getMeasuringTimeNano() - l;
        long q = this.chunkUpdateSmoother.getTargetUsedTime(p);
        long r = q * 3L / 2L;
        long s = MathHelper.clamp(r, o, 33333333L);
        this.updateChunks(l + s);
        lv.swap("terrain");
        this.renderLayer(RenderLayer.getSolid(), arg, d, e, g);
        this.renderLayer(RenderLayer.getCutoutMipped(), arg, d, e, g);
        this.renderLayer(RenderLayer.getCutout(), arg, d, e, g);
        if (this.world.getDimension().isNether()) {
            DiffuseLighting.enableForLevel(arg.peek().getModel());
        } else {
            DiffuseLighting.method_27869(arg.peek().getModel());
        }
        lv.swap("entities");
        lv.push("prepare");
        this.regularEntityCount = 0;
        this.blockEntityCount = 0;
        lv.swap("entities");
        if (this.entityFramebuffer != null) {
            this.entityFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.entityFramebuffer.copyDepthFrom(this.client.getFramebuffer());
            this.client.getFramebuffer().beginWrite(false);
        }
        if (this.canDrawEntityOutlines()) {
            this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.client.getFramebuffer().beginWrite(false);
        }
        boolean bl42 = false;
        VertexConsumerProvider.Immediate lv6 = this.bufferBuilders.getEntityVertexConsumers();
        for (Entity lv7 : this.world.getEntities()) {
            VertexConsumerProvider.Immediate lv10;
            if (!this.entityRenderDispatcher.shouldRender(lv7, lv5, d, e, g) && !lv7.hasPassengerDeep(this.client.player) || lv7 == arg2.getFocusedEntity() && !arg2.isThirdPerson() && (!(arg2.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)arg2.getFocusedEntity()).isSleeping()) || lv7 instanceof ClientPlayerEntity && arg2.getFocusedEntity() != lv7) continue;
            ++this.regularEntityCount;
            if (lv7.age == 0) {
                lv7.lastRenderX = lv7.getX();
                lv7.lastRenderY = lv7.getY();
                lv7.lastRenderZ = lv7.getZ();
            }
            if (this.canDrawEntityOutlines() && this.client.method_27022(lv7)) {
                bl42 = true;
                OutlineVertexConsumerProvider lv8 = this.bufferBuilders.getOutlineVertexConsumers();
                OutlineVertexConsumerProvider lv9 = lv8;
                int k = lv7.getTeamColorValue();
                int t = 255;
                int u = k >> 16 & 0xFF;
                int v = k >> 8 & 0xFF;
                int w = k & 0xFF;
                lv8.setColor(u, v, w, 255);
            } else {
                lv10 = lv6;
            }
            this.renderEntity(lv7, d, e, g, f, arg, lv10);
        }
        this.checkEmpty(arg);
        lv6.draw(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        lv6.draw(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        lv6.draw(RenderLayer.getEntityCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        lv6.draw(RenderLayer.getEntitySmoothCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        lv.swap("blockentities");
        for (Object lv11 : this.visibleChunks) {
            List<BlockEntity> list = ((ChunkInfo)lv11).chunk.getData().getBlockEntities();
            if (list.isEmpty()) continue;
            for (BlockEntity lv12 : list) {
                int x;
                BlockPos lv13 = lv12.getPos();
                VertexConsumerProvider lv14 = lv6;
                arg.push();
                arg.translate((double)lv13.getX() - d, (double)lv13.getY() - e, (double)lv13.getZ() - g);
                SortedSet sortedSet = (SortedSet)this.blockBreakingProgressions.get(lv13.asLong());
                if (sortedSet != null && !sortedSet.isEmpty() && (x = ((BlockBreakingInfo)sortedSet.last()).getStage()) >= 0) {
                    TransformingVertexConsumer lv15 = new TransformingVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(x)), arg.peek());
                    lv14 = arg3 -> {
                        VertexConsumer lv = lv6.getBuffer(arg3);
                        if (arg3.hasCrumbling()) {
                            return VertexConsumers.dual(lv15, lv);
                        }
                        return lv;
                    };
                }
                BlockEntityRenderDispatcher.INSTANCE.render(lv12, f, arg, lv14);
                arg.pop();
            }
        }
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            for (BlockEntity lv16 : this.noCullingBlockEntities) {
                BlockPos lv17 = lv16.getPos();
                arg.push();
                arg.translate((double)lv17.getX() - d, (double)lv17.getY() - e, (double)lv17.getZ() - g);
                BlockEntityRenderDispatcher.INSTANCE.render(lv16, f, arg, lv6);
                arg.pop();
            }
        }
        this.checkEmpty(arg);
        lv6.draw(RenderLayer.getSolid());
        lv6.draw(TexturedRenderLayers.getEntitySolid());
        lv6.draw(TexturedRenderLayers.getEntityCutout());
        lv6.draw(TexturedRenderLayers.getBeds());
        lv6.draw(TexturedRenderLayers.getShulkerBoxes());
        lv6.draw(TexturedRenderLayers.getSign());
        lv6.draw(TexturedRenderLayers.getChest());
        this.bufferBuilders.getOutlineVertexConsumers().draw();
        if (bl42) {
            this.entityOutlineShader.render(f);
            this.client.getFramebuffer().beginWrite(false);
        }
        lv.swap("destroyProgress");
        for (Long2ObjectMap.Entry entry : this.blockBreakingProgressions.long2ObjectEntrySet()) {
            SortedSet sortedSet2;
            double aa;
            double z;
            BlockPos lv18 = BlockPos.fromLong(entry.getLongKey());
            double y = (double)lv18.getX() - d;
            if (y * y + (z = (double)lv18.getY() - e) * z + (aa = (double)lv18.getZ() - g) * aa > 1024.0 || (sortedSet2 = (SortedSet)entry.getValue()) == null || sortedSet2.isEmpty()) continue;
            int ab = ((BlockBreakingInfo)sortedSet2.last()).getStage();
            arg.push();
            arg.translate((double)lv18.getX() - d, (double)lv18.getY() - e, (double)lv18.getZ() - g);
            TransformingVertexConsumer lv19 = new TransformingVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(ab)), arg.peek());
            this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(lv18), lv18, this.world, arg, lv19);
            arg.pop();
        }
        this.checkEmpty(arg);
        lv.pop();
        HitResult lv20 = this.client.crosshairTarget;
        if (bl && lv20 != null && lv20.getType() == HitResult.Type.BLOCK) {
            lv.swap("outline");
            BlockPos lv21 = ((BlockHitResult)lv20).getBlockPos();
            BlockState lv22 = this.world.getBlockState(lv21);
            if (!lv22.isAir() && this.world.getWorldBorder().contains(lv21)) {
                VertexConsumer lv23 = lv6.getBuffer(RenderLayer.getLines());
                this.drawBlockOutline(arg, lv23, arg2.getFocusedEntity(), d, e, g, lv21, lv22);
            }
        }
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(arg.peek().getModel());
        this.client.debugRenderer.render(arg, lv6, d, e, g);
        RenderSystem.popMatrix();
        lv6.draw(TexturedRenderLayers.getEntityTranslucentCull());
        lv6.draw(TexturedRenderLayers.getBannerPatterns());
        lv6.draw(TexturedRenderLayers.getShieldPatterns());
        lv6.draw(RenderLayer.method_27948());
        lv6.draw(RenderLayer.method_27949());
        lv6.draw(RenderLayer.getGlint());
        lv6.draw(RenderLayer.getEntityGlint());
        lv6.draw(RenderLayer.getWaterMask());
        this.bufferBuilders.getEffectVertexConsumers().draw();
        lv6.draw(RenderLayer.getLines());
        lv6.draw();
        lv.swap("translucent");
        if (this.translucentFramebuffer != null) {
            this.translucentFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.translucentFramebuffer.copyDepthFrom(this.client.getFramebuffer());
        }
        this.renderLayer(RenderLayer.getTranslucent(), arg, d, e, g);
        lv.swap("particles");
        if (this.particlesFramebuffer != null) {
            this.particlesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.particlesFramebuffer.copyDepthFrom(this.client.getFramebuffer());
            RenderPhase.PARTICLES_TARGET.startDrawing();
        }
        this.client.particleManager.renderParticles(arg, lv6, arg4, arg2, f);
        if (this.particlesFramebuffer != null) {
            RenderPhase.PARTICLES_TARGET.endDrawing();
        }
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(arg.peek().getModel());
        lv.swap("cloudsLayers");
        if (this.client.options.getCloudRenderMode() != CloudRenderMode.OFF) {
            lv.swap("clouds");
            if (this.cloudsFramebuffer != null) {
                this.cloudsFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
                RenderPhase.CLOUDS_TARGET.startDrawing();
            }
            this.renderClouds(arg, f, d, e, g);
            if (this.cloudsFramebuffer != null) {
                RenderPhase.CLOUDS_TARGET.endDrawing();
            }
        }
        lv.swap("weather");
        if (this.weatherFramebuffer != null) {
            this.weatherFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            RenderPhase.WEATHER_TARGET.startDrawing();
        } else {
            RenderSystem.depthMask(false);
        }
        this.renderWeather(arg4, f, d, e, g);
        this.renderWorldBorder(arg2);
        if (this.weatherFramebuffer != null) {
            RenderPhase.WEATHER_TARGET.endDrawing();
        } else {
            RenderSystem.depthMask(true);
        }
        if (this.transparencyShader != null) {
            this.transparencyShader.render(f);
            this.client.getFramebuffer().beginWrite(false);
        }
        this.renderChunkDebugInfo(arg2);
        RenderSystem.shadeModel(7424);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        BackgroundRenderer.method_23792();
    }

    private void checkEmpty(MatrixStack arg) {
        if (!arg.isEmpty()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity arg, double d, double e, double f, float g, MatrixStack arg2, VertexConsumerProvider arg3) {
        double h = MathHelper.lerp((double)g, arg.lastRenderX, arg.getX());
        double i = MathHelper.lerp((double)g, arg.lastRenderY, arg.getY());
        double j = MathHelper.lerp((double)g, arg.lastRenderZ, arg.getZ());
        float k = MathHelper.lerp(g, arg.prevYaw, arg.yaw);
        this.entityRenderDispatcher.render(arg, h - d, i - e, j - f, k, g, arg2, arg3, this.entityRenderDispatcher.getLight(arg, g));
    }

    private void renderLayer(RenderLayer arg, MatrixStack arg2, double d, double e, double f) {
        arg.startDrawing();
        if (arg == RenderLayer.getTranslucent()) {
            this.client.getProfiler().push("translucent_sort");
            double g = d - this.lastTranslucentSortX;
            double h = e - this.lastTranslucentSortY;
            double i = f - this.lastTranslucentSortZ;
            if (g * g + h * h + i * i > 1.0) {
                this.lastTranslucentSortX = d;
                this.lastTranslucentSortY = e;
                this.lastTranslucentSortZ = f;
                int j = 0;
                for (ChunkInfo lv : this.visibleChunks) {
                    if (j >= 15 || !lv.chunk.scheduleSort(arg, this.chunkBuilder)) continue;
                    ++j;
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().push("filterempty");
        this.client.getProfiler().swap(() -> "render_" + arg);
        boolean bl = arg != RenderLayer.getTranslucent();
        ObjectListIterator objectListIterator = this.visibleChunks.listIterator(bl ? 0 : this.visibleChunks.size());
        while (bl ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
            ChunkInfo lv2 = bl ? (ChunkInfo)objectListIterator.next() : (ChunkInfo)objectListIterator.previous();
            ChunkBuilder.BuiltChunk lv3 = lv2.chunk;
            if (lv3.getData().isEmpty(arg)) continue;
            VertexBuffer lv4 = lv3.getBuffer(arg);
            arg2.push();
            BlockPos lv5 = lv3.getOrigin();
            arg2.translate((double)lv5.getX() - d, (double)lv5.getY() - e, (double)lv5.getZ() - f);
            lv4.bind();
            this.vertexFormat.startDrawing(0L);
            lv4.draw(arg2.peek().getModel(), 7);
            arg2.pop();
        }
        VertexBuffer.unbind();
        RenderSystem.clearCurrentColor();
        this.vertexFormat.endDrawing();
        this.client.getProfiler().pop();
        arg.endDrawing();
    }

    private void renderChunkDebugInfo(Camera arg) {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        if (this.client.debugChunkInfo || this.client.debugChunkOcculsion) {
            double d = arg.getPos().getX();
            double e = arg.getPos().getY();
            double f = arg.getPos().getZ();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableTexture();
            for (ChunkInfo lv3 : this.visibleChunks) {
                ChunkBuilder.BuiltChunk lv4 = lv3.chunk;
                RenderSystem.pushMatrix();
                BlockPos lv5 = lv4.getOrigin();
                RenderSystem.translated((double)lv5.getX() - d, (double)lv5.getY() - e, (double)lv5.getZ() - f);
                if (this.client.debugChunkInfo) {
                    lv2.begin(1, VertexFormats.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0f);
                    int i = lv3.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)lv3.propagationLevel / 50.0f, 0.9f, 0.9f);
                    int j = i >> 16 & 0xFF;
                    int k = i >> 8 & 0xFF;
                    int l = i & 0xFF;
                    Direction lv6 = lv3.direction;
                    if (lv6 != null) {
                        lv2.vertex(8.0, 8.0, 8.0).color(j, k, l, 255).next();
                        lv2.vertex(8 - 16 * lv6.getOffsetX(), 8 - 16 * lv6.getOffsetY(), 8 - 16 * lv6.getOffsetZ()).color(j, k, l, 255).next();
                    }
                    lv.draw();
                    RenderSystem.lineWidth(1.0f);
                }
                if (this.client.debugChunkOcculsion && !lv4.getData().isEmpty()) {
                    lv2.begin(1, VertexFormats.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0f);
                    int m = 0;
                    for (Direction lv7 : DIRECTIONS) {
                        for (Direction lv8 : DIRECTIONS) {
                            boolean bl = lv4.getData().isVisibleThrough(lv7, lv8);
                            if (bl) continue;
                            ++m;
                            lv2.vertex(8 + 8 * lv7.getOffsetX(), 8 + 8 * lv7.getOffsetY(), 8 + 8 * lv7.getOffsetZ()).color(1, 0, 0, 1).next();
                            lv2.vertex(8 + 8 * lv8.getOffsetX(), 8 + 8 * lv8.getOffsetY(), 8 + 8 * lv8.getOffsetZ()).color(1, 0, 0, 1).next();
                        }
                    }
                    lv.draw();
                    RenderSystem.lineWidth(1.0f);
                    if (m > 0) {
                        lv2.begin(7, VertexFormats.POSITION_COLOR);
                        float g = 0.5f;
                        float h = 0.2f;
                        lv2.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv2.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        lv.draw();
                    }
                }
                RenderSystem.popMatrix();
            }
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
        }
        if (this.capturedFrustum != null) {
            RenderSystem.disableCull();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(10.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.capturedFrustumPosition.x - arg.getPos().x), (float)(this.capturedFrustumPosition.y - arg.getPos().y), (float)(this.capturedFrustumPosition.z - arg.getPos().z));
            RenderSystem.depthMask(true);
            lv2.begin(7, VertexFormats.POSITION_COLOR);
            this.method_22985(lv2, 0, 1, 2, 3, 0, 1, 1);
            this.method_22985(lv2, 4, 5, 6, 7, 1, 0, 0);
            this.method_22985(lv2, 0, 1, 5, 4, 1, 1, 0);
            this.method_22985(lv2, 2, 3, 7, 6, 0, 0, 1);
            this.method_22985(lv2, 0, 4, 7, 3, 0, 1, 0);
            this.method_22985(lv2, 1, 5, 6, 2, 1, 0, 1);
            lv.draw();
            RenderSystem.depthMask(false);
            lv2.begin(1, VertexFormats.POSITION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.method_22984(lv2, 0);
            this.method_22984(lv2, 1);
            this.method_22984(lv2, 1);
            this.method_22984(lv2, 2);
            this.method_22984(lv2, 2);
            this.method_22984(lv2, 3);
            this.method_22984(lv2, 3);
            this.method_22984(lv2, 0);
            this.method_22984(lv2, 4);
            this.method_22984(lv2, 5);
            this.method_22984(lv2, 5);
            this.method_22984(lv2, 6);
            this.method_22984(lv2, 6);
            this.method_22984(lv2, 7);
            this.method_22984(lv2, 7);
            this.method_22984(lv2, 4);
            this.method_22984(lv2, 0);
            this.method_22984(lv2, 4);
            this.method_22984(lv2, 1);
            this.method_22984(lv2, 5);
            this.method_22984(lv2, 2);
            this.method_22984(lv2, 6);
            this.method_22984(lv2, 3);
            this.method_22984(lv2, 7);
            lv.draw();
            RenderSystem.popMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
            RenderSystem.lineWidth(1.0f);
        }
    }

    private void method_22984(VertexConsumer arg, int i) {
        arg.vertex(this.capturedFrustrumOrientation[i].getX(), this.capturedFrustrumOrientation[i].getY(), this.capturedFrustrumOrientation[i].getZ()).next();
    }

    private void method_22985(VertexConsumer arg, int i, int j, int k, int l, int m, int n, int o) {
        float f = 0.25f;
        arg.vertex(this.capturedFrustrumOrientation[i].getX(), this.capturedFrustrumOrientation[i].getY(), this.capturedFrustrumOrientation[i].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        arg.vertex(this.capturedFrustrumOrientation[j].getX(), this.capturedFrustrumOrientation[j].getY(), this.capturedFrustrumOrientation[j].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        arg.vertex(this.capturedFrustrumOrientation[k].getX(), this.capturedFrustrumOrientation[k].getY(), this.capturedFrustrumOrientation[k].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        arg.vertex(this.capturedFrustrumOrientation[l].getX(), this.capturedFrustrumOrientation[l].getY(), this.capturedFrustrumOrientation[l].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks % 20 != 0) {
            return;
        }
        ObjectIterator iterator = this.blockBreakingInfos.values().iterator();
        while (iterator.hasNext()) {
            BlockBreakingInfo lv = (BlockBreakingInfo)iterator.next();
            int i = lv.getLastUpdateTick();
            if (this.ticks - i <= 400) continue;
            iterator.remove();
            this.removeBlockBreakingInfo(lv);
        }
    }

    private void removeBlockBreakingInfo(BlockBreakingInfo arg) {
        long l = arg.getPos().asLong();
        Set set = (Set)this.blockBreakingProgressions.get(l);
        set.remove(arg);
        if (set.isEmpty()) {
            this.blockBreakingProgressions.remove(l);
        }
    }

    private void renderEndSky(MatrixStack arg) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        this.textureManager.bindTexture(END_SKY);
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        for (int i = 0; i < 6; ++i) {
            arg.push();
            if (i == 1) {
                arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            }
            if (i == 2) {
                arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
            }
            if (i == 3) {
                arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
            }
            if (i == 4) {
                arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
            }
            if (i == 5) {
                arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0f));
            }
            Matrix4f lv3 = arg.peek().getModel();
            lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            lv2.vertex(lv3, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(40, 40, 40, 255).next();
            lv2.vertex(lv3, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(40, 40, 40, 255).next();
            lv2.vertex(lv3, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(40, 40, 40, 255).next();
            lv2.vertex(lv3, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(40, 40, 40, 255).next();
            lv.draw();
            arg.pop();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    public void renderSky(MatrixStack arg, float f) {
        if (this.client.world.getDimension().isEnd()) {
            this.renderEndSky(arg);
            return;
        }
        if (!this.client.world.getSkyProperties().shouldRenderSky()) {
            return;
        }
        RenderSystem.disableTexture();
        Vec3d lv = this.world.method_23777(this.client.gameRenderer.getCamera().getBlockPos(), f);
        float g = (float)lv.x;
        float h = (float)lv.y;
        float i = (float)lv.z;
        BackgroundRenderer.setFogBlack();
        BufferBuilder lv2 = Tessellator.getInstance().getBuffer();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f(g, h, i);
        this.lightSkyBuffer.bind();
        this.skyVertexFormat.startDrawing(0L);
        this.lightSkyBuffer.draw(arg.peek().getModel(), 7);
        VertexBuffer.unbind();
        this.skyVertexFormat.endDrawing();
        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] fs = this.world.getSkyProperties().getSkyColor(this.world.getSkyAngle(f), f);
        if (fs != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            arg.push();
            arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            float j = MathHelper.sin(this.world.getSkyAngleRadians(f)) < 0.0f ? 180.0f : 0.0f;
            arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(j));
            arg.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
            float k = fs[0];
            float l = fs[1];
            float m = fs[2];
            Matrix4f lv3 = arg.peek().getModel();
            lv2.begin(6, VertexFormats.POSITION_COLOR);
            lv2.vertex(lv3, 0.0f, 100.0f, 0.0f).color(k, l, m, fs[3]).next();
            int n = 16;
            for (int o = 0; o <= 16; ++o) {
                float p = (float)o * ((float)Math.PI * 2) / 16.0f;
                float q = MathHelper.sin(p);
                float r = MathHelper.cos(p);
                lv2.vertex(lv3, q * 120.0f, r * 120.0f, -r * 40.0f * fs[3]).color(fs[0], fs[1], fs[2], 0.0f).next();
            }
            lv2.end();
            BufferRenderer.draw(lv2);
            arg.pop();
            RenderSystem.shadeModel(7424);
        }
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        arg.push();
        float s = 1.0f - this.world.getRainGradient(f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, s);
        arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));
        arg.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(f) * 360.0f));
        Matrix4f lv4 = arg.peek().getModel();
        float t = 30.0f;
        this.textureManager.bindTexture(SUN);
        lv2.begin(7, VertexFormats.POSITION_TEXTURE);
        lv2.vertex(lv4, -t, 100.0f, -t).texture(0.0f, 0.0f).next();
        lv2.vertex(lv4, t, 100.0f, -t).texture(1.0f, 0.0f).next();
        lv2.vertex(lv4, t, 100.0f, t).texture(1.0f, 1.0f).next();
        lv2.vertex(lv4, -t, 100.0f, t).texture(0.0f, 1.0f).next();
        lv2.end();
        BufferRenderer.draw(lv2);
        t = 20.0f;
        this.textureManager.bindTexture(MOON_PHASES);
        int u = this.world.getMoonPhase();
        int v = u % 4;
        int w = u / 4 % 2;
        float x = (float)(v + 0) / 4.0f;
        float y = (float)(w + 0) / 2.0f;
        float z = (float)(v + 1) / 4.0f;
        float aa = (float)(w + 1) / 2.0f;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE);
        lv2.vertex(lv4, -t, -100.0f, t).texture(z, aa).next();
        lv2.vertex(lv4, t, -100.0f, t).texture(x, aa).next();
        lv2.vertex(lv4, t, -100.0f, -t).texture(x, y).next();
        lv2.vertex(lv4, -t, -100.0f, -t).texture(z, y).next();
        lv2.end();
        BufferRenderer.draw(lv2);
        RenderSystem.disableTexture();
        float ab = this.world.method_23787(f) * s;
        if (ab > 0.0f) {
            RenderSystem.color4f(ab, ab, ab, ab);
            this.starsBuffer.bind();
            this.skyVertexFormat.startDrawing(0L);
            this.starsBuffer.draw(arg.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.skyVertexFormat.endDrawing();
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        arg.pop();
        RenderSystem.disableTexture();
        RenderSystem.color3f(0.0f, 0.0f, 0.0f);
        double d = this.client.player.getCameraPosVec((float)f).y - this.world.getLevelProperties().getSkyDarknessHeight();
        if (d < 0.0) {
            arg.push();
            arg.translate(0.0, 12.0, 0.0);
            this.darkSkyBuffer.bind();
            this.skyVertexFormat.startDrawing(0L);
            this.darkSkyBuffer.draw(arg.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.skyVertexFormat.endDrawing();
            arg.pop();
        }
        if (this.world.getSkyProperties().isAlternateSkyColor()) {
            RenderSystem.color3f(g * 0.2f + 0.04f, h * 0.2f + 0.04f, i * 0.6f + 0.1f);
        } else {
            RenderSystem.color3f(g, h, i);
        }
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }

    public void renderClouds(MatrixStack arg, float f, double d, double e, double g) {
        float h = this.world.getSkyProperties().getCloudsHeight();
        if (Float.isNaN(h)) {
            return;
        }
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableFog();
        float i = 12.0f;
        float j = 4.0f;
        double k = 2.0E-4;
        double l = ((float)this.ticks + f) * 0.03f;
        double m = (d + l) / 12.0;
        double n = h - (float)e + 0.33f;
        double o = g / 12.0 + (double)0.33f;
        m -= (double)(MathHelper.floor(m / 2048.0) * 2048);
        o -= (double)(MathHelper.floor(o / 2048.0) * 2048);
        float p = (float)(m - (double)MathHelper.floor(m));
        float q = (float)(n / 4.0 - (double)MathHelper.floor(n / 4.0)) * 4.0f;
        float r = (float)(o - (double)MathHelper.floor(o));
        Vec3d lv = this.world.getCloudsColor(f);
        int s = (int)Math.floor(m);
        int t = (int)Math.floor(n / 4.0);
        int u = (int)Math.floor(o);
        if (s != this.lastCloudsBlockX || t != this.lastCloudsBlockY || u != this.lastCloudsBlockZ || this.client.options.getCloudRenderMode() != this.lastCloudsRenderMode || this.lastCloudsColor.squaredDistanceTo(lv) > 2.0E-4) {
            this.lastCloudsBlockX = s;
            this.lastCloudsBlockY = t;
            this.lastCloudsBlockZ = u;
            this.lastCloudsColor = lv;
            this.lastCloudsRenderMode = this.client.options.getCloudRenderMode();
            this.cloudsDirty = true;
        }
        if (this.cloudsDirty) {
            this.cloudsDirty = false;
            BufferBuilder lv2 = Tessellator.getInstance().getBuffer();
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.close();
            }
            this.cloudsBuffer = new VertexBuffer(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            this.renderClouds(lv2, m, n, o, lv);
            lv2.end();
            this.cloudsBuffer.upload(lv2);
        }
        this.textureManager.bindTexture(CLOUDS);
        arg.push();
        arg.scale(12.0f, 1.0f, 12.0f);
        arg.translate(-p, q, -r);
        if (this.cloudsBuffer != null) {
            int v;
            this.cloudsBuffer.bind();
            VertexFormats.POSITION_TEXTURE_COLOR_NORMAL.startDrawing(0L);
            for (int w = v = this.lastCloudsRenderMode == CloudRenderMode.FANCY ? 0 : 1; w < 2; ++w) {
                if (w == 0) {
                    RenderSystem.colorMask(false, false, false, false);
                } else {
                    RenderSystem.colorMask(true, true, true, true);
                }
                this.cloudsBuffer.draw(arg.peek().getModel(), 7);
            }
            VertexBuffer.unbind();
            VertexFormats.POSITION_TEXTURE_COLOR_NORMAL.endDrawing();
        }
        arg.pop();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableFog();
    }

    private void renderClouds(BufferBuilder arg, double d, double e, double f, Vec3d arg2) {
        float g = 4.0f;
        float h = 0.00390625f;
        int i = 8;
        int j = 4;
        float k = 9.765625E-4f;
        float l = (float)MathHelper.floor(d) * 0.00390625f;
        float m = (float)MathHelper.floor(f) * 0.00390625f;
        float n = (float)arg2.x;
        float o = (float)arg2.y;
        float p = (float)arg2.z;
        float q = n * 0.9f;
        float r = o * 0.9f;
        float s = p * 0.9f;
        float t = n * 0.7f;
        float u = o * 0.7f;
        float v = p * 0.7f;
        float w = n * 0.8f;
        float x = o * 0.8f;
        float y = p * 0.8f;
        arg.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float z = (float)Math.floor(e / 4.0) * 4.0f;
        if (this.lastCloudsRenderMode == CloudRenderMode.FANCY) {
            for (int aa = -3; aa <= 4; ++aa) {
                for (int ab = -3; ab <= 4; ++ab) {
                    float ac = aa * 8;
                    float ad = ab * 8;
                    if (z > -5.0f) {
                        arg.vertex(ac + 0.0f, z + 0.0f, ad + 8.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(t, u, v, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        arg.vertex(ac + 8.0f, z + 0.0f, ad + 8.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(t, u, v, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        arg.vertex(ac + 8.0f, z + 0.0f, ad + 0.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(t, u, v, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        arg.vertex(ac + 0.0f, z + 0.0f, ad + 0.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(t, u, v, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    }
                    if (z <= 5.0f) {
                        arg.vertex(ac + 0.0f, z + 4.0f - 9.765625E-4f, ad + 8.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        arg.vertex(ac + 8.0f, z + 4.0f - 9.765625E-4f, ad + 8.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        arg.vertex(ac + 8.0f, z + 4.0f - 9.765625E-4f, ad + 0.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        arg.vertex(ac + 0.0f, z + 4.0f - 9.765625E-4f, ad + 0.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                    }
                    if (aa > -1) {
                        for (int ae = 0; ae < 8; ++ae) {
                            arg.vertex(ac + (float)ae + 0.0f, z + 0.0f, ad + 8.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            arg.vertex(ac + (float)ae + 0.0f, z + 4.0f, ad + 8.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            arg.vertex(ac + (float)ae + 0.0f, z + 4.0f, ad + 0.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            arg.vertex(ac + (float)ae + 0.0f, z + 0.0f, ad + 0.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (aa <= 1) {
                        for (int af = 0; af < 8; ++af) {
                            arg.vertex(ac + (float)af + 1.0f - 9.765625E-4f, z + 0.0f, ad + 8.0f).texture((ac + (float)af + 0.5f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            arg.vertex(ac + (float)af + 1.0f - 9.765625E-4f, z + 4.0f, ad + 8.0f).texture((ac + (float)af + 0.5f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            arg.vertex(ac + (float)af + 1.0f - 9.765625E-4f, z + 4.0f, ad + 0.0f).texture((ac + (float)af + 0.5f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            arg.vertex(ac + (float)af + 1.0f - 9.765625E-4f, z + 0.0f, ad + 0.0f).texture((ac + (float)af + 0.5f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (ab > -1) {
                        for (int ag = 0; ag < 8; ++ag) {
                            arg.vertex(ac + 0.0f, z + 4.0f, ad + (float)ag + 0.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + (float)ag + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            arg.vertex(ac + 8.0f, z + 4.0f, ad + (float)ag + 0.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + (float)ag + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            arg.vertex(ac + 8.0f, z + 0.0f, ad + (float)ag + 0.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + (float)ag + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            arg.vertex(ac + 0.0f, z + 0.0f, ad + (float)ag + 0.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + (float)ag + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                        }
                    }
                    if (ab > 1) continue;
                    for (int ah = 0; ah < 8; ++ah) {
                        arg.vertex(ac + 0.0f, z + 4.0f, ad + (float)ah + 1.0f - 9.765625E-4f).texture((ac + 0.0f) * 0.00390625f + l, (ad + (float)ah + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        arg.vertex(ac + 8.0f, z + 4.0f, ad + (float)ah + 1.0f - 9.765625E-4f).texture((ac + 8.0f) * 0.00390625f + l, (ad + (float)ah + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        arg.vertex(ac + 8.0f, z + 0.0f, ad + (float)ah + 1.0f - 9.765625E-4f).texture((ac + 8.0f) * 0.00390625f + l, (ad + (float)ah + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        arg.vertex(ac + 0.0f, z + 0.0f, ad + (float)ah + 1.0f - 9.765625E-4f).texture((ac + 0.0f) * 0.00390625f + l, (ad + (float)ah + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                    }
                }
            }
        } else {
            boolean ai = true;
            int aj = 32;
            for (int ak = -32; ak < 32; ak += 32) {
                for (int al = -32; al < 32; al += 32) {
                    arg.vertex(ak + 0, z, al + 32).texture((float)(ak + 0) * 0.00390625f + l, (float)(al + 32) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    arg.vertex(ak + 32, z, al + 32).texture((float)(ak + 32) * 0.00390625f + l, (float)(al + 32) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    arg.vertex(ak + 32, z, al + 0).texture((float)(ak + 32) * 0.00390625f + l, (float)(al + 0) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    arg.vertex(ak + 0, z, al + 0).texture((float)(ak + 0) * 0.00390625f + l, (float)(al + 0) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                }
            }
        }
    }

    private void updateChunks(long l) {
        this.needsTerrainUpdate |= this.chunkBuilder.upload();
        long m = Util.getMeasuringTimeNano();
        int i = 0;
        if (!this.chunksToRebuild.isEmpty()) {
            Iterator<ChunkBuilder.BuiltChunk> iterator = this.chunksToRebuild.iterator();
            while (iterator.hasNext()) {
                long o;
                long p;
                ChunkBuilder.BuiltChunk lv = iterator.next();
                if (lv.needsImportantRebuild()) {
                    this.chunkBuilder.rebuild(lv);
                } else {
                    lv.scheduleRebuild(this.chunkBuilder);
                }
                lv.cancelRebuild();
                iterator.remove();
                long n = Util.getMeasuringTimeNano();
                long q = l - n;
                if (q >= (p = (o = n - m) / (long)(++i))) continue;
                break;
            }
        }
    }

    private void renderWorldBorder(Camera arg) {
        BufferBuilder lv = Tessellator.getInstance().getBuffer();
        WorldBorder lv2 = this.world.getWorldBorder();
        double d = this.client.options.viewDistance * 16;
        if (arg.getPos().x < lv2.getBoundEast() - d && arg.getPos().x > lv2.getBoundWest() + d && arg.getPos().z < lv2.getBoundSouth() - d && arg.getPos().z > lv2.getBoundNorth() + d) {
            return;
        }
        double e = 1.0 - lv2.getDistanceInsideBorder(arg.getPos().x, arg.getPos().z) / d;
        e = Math.pow(e, 4.0);
        double f = arg.getPos().x;
        double g = arg.getPos().y;
        double h = arg.getPos().z;
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        this.textureManager.bindTexture(FORCEFIELD);
        RenderSystem.depthMask(true);
        RenderSystem.pushMatrix();
        int i = lv2.getStage().getColor();
        float j = (float)(i >> 16 & 0xFF) / 255.0f;
        float k = (float)(i >> 8 & 0xFF) / 255.0f;
        float l = (float)(i & 0xFF) / 255.0f;
        RenderSystem.color4f(j, k, l, (float)e);
        RenderSystem.polygonOffset(-3.0f, -3.0f);
        RenderSystem.enablePolygonOffset();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableCull();
        float m = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0f;
        float n = 0.0f;
        float o = 0.0f;
        float p = 128.0f;
        lv.begin(7, VertexFormats.POSITION_TEXTURE);
        double q = Math.max((double)MathHelper.floor(h - d), lv2.getBoundNorth());
        double r = Math.min((double)MathHelper.ceil(h + d), lv2.getBoundSouth());
        if (f > lv2.getBoundEast() - d) {
            float s = 0.0f;
            double t = q;
            while (t < r) {
                double u = Math.min(1.0, r - t);
                float v = (float)u * 0.5f;
                this.method_22978(lv, f, g, h, lv2.getBoundEast(), 256, t, m + s, m + 0.0f);
                this.method_22978(lv, f, g, h, lv2.getBoundEast(), 256, t + u, m + v + s, m + 0.0f);
                this.method_22978(lv, f, g, h, lv2.getBoundEast(), 0, t + u, m + v + s, m + 128.0f);
                this.method_22978(lv, f, g, h, lv2.getBoundEast(), 0, t, m + s, m + 128.0f);
                t += 1.0;
                s += 0.5f;
            }
        }
        if (f < lv2.getBoundWest() + d) {
            float w = 0.0f;
            double x = q;
            while (x < r) {
                double y = Math.min(1.0, r - x);
                float z = (float)y * 0.5f;
                this.method_22978(lv, f, g, h, lv2.getBoundWest(), 256, x, m + w, m + 0.0f);
                this.method_22978(lv, f, g, h, lv2.getBoundWest(), 256, x + y, m + z + w, m + 0.0f);
                this.method_22978(lv, f, g, h, lv2.getBoundWest(), 0, x + y, m + z + w, m + 128.0f);
                this.method_22978(lv, f, g, h, lv2.getBoundWest(), 0, x, m + w, m + 128.0f);
                x += 1.0;
                w += 0.5f;
            }
        }
        q = Math.max((double)MathHelper.floor(f - d), lv2.getBoundWest());
        r = Math.min((double)MathHelper.ceil(f + d), lv2.getBoundEast());
        if (h > lv2.getBoundSouth() - d) {
            float aa = 0.0f;
            double ab = q;
            while (ab < r) {
                double ac = Math.min(1.0, r - ab);
                float ad = (float)ac * 0.5f;
                this.method_22978(lv, f, g, h, ab, 256, lv2.getBoundSouth(), m + aa, m + 0.0f);
                this.method_22978(lv, f, g, h, ab + ac, 256, lv2.getBoundSouth(), m + ad + aa, m + 0.0f);
                this.method_22978(lv, f, g, h, ab + ac, 0, lv2.getBoundSouth(), m + ad + aa, m + 128.0f);
                this.method_22978(lv, f, g, h, ab, 0, lv2.getBoundSouth(), m + aa, m + 128.0f);
                ab += 1.0;
                aa += 0.5f;
            }
        }
        if (h < lv2.getBoundNorth() + d) {
            float ae = 0.0f;
            double af = q;
            while (af < r) {
                double ag = Math.min(1.0, r - af);
                float ah = (float)ag * 0.5f;
                this.method_22978(lv, f, g, h, af, 256, lv2.getBoundNorth(), m + ae, m + 0.0f);
                this.method_22978(lv, f, g, h, af + ag, 256, lv2.getBoundNorth(), m + ah + ae, m + 0.0f);
                this.method_22978(lv, f, g, h, af + ag, 0, lv2.getBoundNorth(), m + ah + ae, m + 128.0f);
                this.method_22978(lv, f, g, h, af, 0, lv2.getBoundNorth(), m + ae, m + 128.0f);
                af += 1.0;
                ae += 0.5f;
            }
        }
        lv.end();
        BufferRenderer.draw(lv);
        RenderSystem.enableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
    }

    private void method_22978(BufferBuilder arg, double d, double e, double f, double g, int i, double h, float j, float k) {
        arg.vertex(g - d, (double)i - e, h - f).texture(j, k).next();
    }

    private void drawBlockOutline(MatrixStack arg, VertexConsumer arg2, Entity arg3, double d, double e, double f, BlockPos arg4, BlockState arg5) {
        WorldRenderer.drawShapeOutline(arg, arg2, arg5.getOutlineShape(this.world, arg4, ShapeContext.of(arg3)), (double)arg4.getX() - d, (double)arg4.getY() - e, (double)arg4.getZ() - f, 0.0f, 0.0f, 0.0f, 0.4f);
    }

    public static void method_22983(MatrixStack arg, VertexConsumer arg2, VoxelShape arg3, double d, double e, double f, float g, float h, float i, float j) {
        List<Box> list = arg3.getBoundingBoxes();
        int k = MathHelper.ceil((double)list.size() / 3.0);
        for (int l = 0; l < list.size(); ++l) {
            Box lv = list.get(l);
            float m = ((float)l % (float)k + 1.0f) / (float)k;
            float n = l / k;
            float o = m * (float)(n == 0.0f ? 1 : 0);
            float p = m * (float)(n == 1.0f ? 1 : 0);
            float q = m * (float)(n == 2.0f ? 1 : 0);
            WorldRenderer.drawShapeOutline(arg, arg2, VoxelShapes.cuboid(lv.offset(0.0, 0.0, 0.0)), d, e, f, o, p, q, 1.0f);
        }
    }

    private static void drawShapeOutline(MatrixStack arg, VertexConsumer arg2, VoxelShape arg3, double d, double e, double f, float g, float h, float i, float j) {
        Matrix4f lv = arg.peek().getModel();
        arg3.forEachEdge((k, l, m, n, o, p) -> {
            arg2.vertex(lv, (float)(k + d), (float)(l + e), (float)(m + f)).color(g, h, i, j).next();
            arg2.vertex(lv, (float)(n + d), (float)(o + e), (float)(p + f)).color(g, h, i, j).next();
        });
    }

    public static void drawBox(MatrixStack arg, VertexConsumer arg2, Box arg3, float f, float g, float h, float i) {
        WorldRenderer.drawBox(arg, arg2, arg3.minX, arg3.minY, arg3.minZ, arg3.maxX, arg3.maxY, arg3.maxZ, f, g, h, i, f, g, h);
    }

    public static void drawBox(MatrixStack arg, VertexConsumer arg2, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        WorldRenderer.drawBox(arg, arg2, d, e, f, g, h, i, j, k, l, m, j, k, l);
    }

    public static void drawBox(MatrixStack arg, VertexConsumer arg2, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m, float n, float o, float p) {
        Matrix4f lv = arg.peek().getModel();
        float q = (float)d;
        float r = (float)e;
        float s = (float)f;
        float t = (float)g;
        float u = (float)h;
        float v = (float)i;
        arg2.vertex(lv, q, r, s).color(j, o, p, m).next();
        arg2.vertex(lv, t, r, s).color(j, o, p, m).next();
        arg2.vertex(lv, q, r, s).color(n, k, p, m).next();
        arg2.vertex(lv, q, u, s).color(n, k, p, m).next();
        arg2.vertex(lv, q, r, s).color(n, o, l, m).next();
        arg2.vertex(lv, q, r, v).color(n, o, l, m).next();
        arg2.vertex(lv, t, r, s).color(j, k, l, m).next();
        arg2.vertex(lv, t, u, s).color(j, k, l, m).next();
        arg2.vertex(lv, t, u, s).color(j, k, l, m).next();
        arg2.vertex(lv, q, u, s).color(j, k, l, m).next();
        arg2.vertex(lv, q, u, s).color(j, k, l, m).next();
        arg2.vertex(lv, q, u, v).color(j, k, l, m).next();
        arg2.vertex(lv, q, u, v).color(j, k, l, m).next();
        arg2.vertex(lv, q, r, v).color(j, k, l, m).next();
        arg2.vertex(lv, q, r, v).color(j, k, l, m).next();
        arg2.vertex(lv, t, r, v).color(j, k, l, m).next();
        arg2.vertex(lv, t, r, v).color(j, k, l, m).next();
        arg2.vertex(lv, t, r, s).color(j, k, l, m).next();
        arg2.vertex(lv, q, u, v).color(j, k, l, m).next();
        arg2.vertex(lv, t, u, v).color(j, k, l, m).next();
        arg2.vertex(lv, t, r, v).color(j, k, l, m).next();
        arg2.vertex(lv, t, u, v).color(j, k, l, m).next();
        arg2.vertex(lv, t, u, s).color(j, k, l, m).next();
        arg2.vertex(lv, t, u, v).color(j, k, l, m).next();
    }

    public static void drawBox(BufferBuilder arg, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        arg.vertex(d, e, f).color(j, k, l, m).next();
        arg.vertex(d, e, f).color(j, k, l, m).next();
        arg.vertex(d, e, f).color(j, k, l, m).next();
        arg.vertex(d, e, i).color(j, k, l, m).next();
        arg.vertex(d, h, f).color(j, k, l, m).next();
        arg.vertex(d, h, i).color(j, k, l, m).next();
        arg.vertex(d, h, i).color(j, k, l, m).next();
        arg.vertex(d, e, i).color(j, k, l, m).next();
        arg.vertex(g, h, i).color(j, k, l, m).next();
        arg.vertex(g, e, i).color(j, k, l, m).next();
        arg.vertex(g, e, i).color(j, k, l, m).next();
        arg.vertex(g, e, f).color(j, k, l, m).next();
        arg.vertex(g, h, i).color(j, k, l, m).next();
        arg.vertex(g, h, f).color(j, k, l, m).next();
        arg.vertex(g, h, f).color(j, k, l, m).next();
        arg.vertex(g, e, f).color(j, k, l, m).next();
        arg.vertex(d, h, f).color(j, k, l, m).next();
        arg.vertex(d, e, f).color(j, k, l, m).next();
        arg.vertex(d, e, f).color(j, k, l, m).next();
        arg.vertex(g, e, f).color(j, k, l, m).next();
        arg.vertex(d, e, i).color(j, k, l, m).next();
        arg.vertex(g, e, i).color(j, k, l, m).next();
        arg.vertex(g, e, i).color(j, k, l, m).next();
        arg.vertex(d, h, f).color(j, k, l, m).next();
        arg.vertex(d, h, f).color(j, k, l, m).next();
        arg.vertex(d, h, i).color(j, k, l, m).next();
        arg.vertex(g, h, f).color(j, k, l, m).next();
        arg.vertex(g, h, i).color(j, k, l, m).next();
        arg.vertex(g, h, i).color(j, k, l, m).next();
        arg.vertex(g, h, i).color(j, k, l, m).next();
    }

    public void updateBlock(BlockView arg, BlockPos arg2, BlockState arg3, BlockState arg4, int i) {
        this.scheduleSectionRender(arg2, (i & 8) != 0);
    }

    private void scheduleSectionRender(BlockPos arg, boolean bl) {
        for (int i = arg.getZ() - 1; i <= arg.getZ() + 1; ++i) {
            for (int j = arg.getX() - 1; j <= arg.getX() + 1; ++j) {
                for (int k = arg.getY() - 1; k <= arg.getY() + 1; ++k) {
                    this.scheduleChunkRender(j >> 4, k >> 4, i >> 4, bl);
                }
            }
        }
    }

    public void scheduleBlockRenders(int i, int j, int k, int l, int m, int n) {
        for (int o = k - 1; o <= n + 1; ++o) {
            for (int p = i - 1; p <= l + 1; ++p) {
                for (int q = j - 1; q <= m + 1; ++q) {
                    this.scheduleBlockRender(p >> 4, q >> 4, o >> 4);
                }
            }
        }
    }

    public void scheduleBlockRerenderIfNeeded(BlockPos arg, BlockState arg2, BlockState arg3) {
        if (this.client.getBakedModelManager().shouldRerender(arg2, arg3)) {
            this.scheduleBlockRenders(arg.getX(), arg.getY(), arg.getZ(), arg.getX(), arg.getY(), arg.getZ());
        }
    }

    public void scheduleBlockRenders(int i, int j, int k) {
        for (int l = k - 1; l <= k + 1; ++l) {
            for (int m = i - 1; m <= i + 1; ++m) {
                for (int n = j - 1; n <= j + 1; ++n) {
                    this.scheduleBlockRender(m, n, l);
                }
            }
        }
    }

    public void scheduleBlockRender(int i, int j, int k) {
        this.scheduleChunkRender(i, j, k, false);
    }

    private void scheduleChunkRender(int i, int j, int k, boolean bl) {
        this.chunks.scheduleRebuild(i, j, k, bl);
    }

    public void playSong(@Nullable SoundEvent arg, BlockPos arg2) {
        SoundInstance lv = this.playingSongs.get(arg2);
        if (lv != null) {
            this.client.getSoundManager().stop(lv);
            this.playingSongs.remove(arg2);
        }
        if (arg != null) {
            MusicDiscItem lv2 = MusicDiscItem.bySound(arg);
            if (lv2 != null) {
                this.client.inGameHud.setRecordPlayingOverlay(lv2.getDescription());
            }
            lv = PositionedSoundInstance.record(arg, arg2.getX(), arg2.getY(), arg2.getZ());
            this.playingSongs.put(arg2, lv);
            this.client.getSoundManager().play(lv);
        }
        this.updateEntitiesForSong(this.world, arg2, arg != null);
    }

    private void updateEntitiesForSong(World arg, BlockPos arg2, boolean bl) {
        List<LivingEntity> list = arg.getNonSpectatingEntities(LivingEntity.class, new Box(arg2).expand(3.0));
        for (LivingEntity lv : list) {
            lv.setNearbySongPlaying(arg2, bl);
        }
    }

    public void addParticle(ParticleEffect arg, boolean bl, double d, double e, double f, double g, double h, double i) {
        this.addParticle(arg, bl, false, d, e, f, g, h, i);
    }

    public void addParticle(ParticleEffect arg, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
        try {
            this.spawnParticle(arg, bl, bl2, d, e, f, g, h, i);
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Exception while adding particle");
            CrashReportSection lv2 = lv.addElement("Particle being added");
            lv2.add("ID", Registry.PARTICLE_TYPE.getId(arg.getType()));
            lv2.add("Parameters", arg.asString());
            lv2.add("Position", () -> CrashReportSection.createPositionString(d, e, f));
            throw new CrashException(lv);
        }
    }

    private <T extends ParticleEffect> void addParticle(T arg, double d, double e, double f, double g, double h, double i) {
        this.addParticle(arg, arg.getType().shouldAlwaysSpawn(), d, e, f, g, h, i);
    }

    @Nullable
    private Particle spawnParticle(ParticleEffect arg, boolean bl, double d, double e, double f, double g, double h, double i) {
        return this.spawnParticle(arg, bl, false, d, e, f, g, h, i);
    }

    @Nullable
    private Particle spawnParticle(ParticleEffect arg, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
        Camera lv = this.client.gameRenderer.getCamera();
        if (this.client == null || !lv.isReady() || this.client.particleManager == null) {
            return null;
        }
        ParticlesOption lv2 = this.getRandomParticleSpawnChance(bl2);
        if (bl) {
            return this.client.particleManager.addParticle(arg, d, e, f, g, h, i);
        }
        if (lv.getPos().squaredDistanceTo(d, e, f) > 1024.0) {
            return null;
        }
        if (lv2 == ParticlesOption.MINIMAL) {
            return null;
        }
        return this.client.particleManager.addParticle(arg, d, e, f, g, h, i);
    }

    private ParticlesOption getRandomParticleSpawnChance(boolean bl) {
        ParticlesOption lv = this.client.options.particles;
        if (bl && lv == ParticlesOption.MINIMAL && this.world.random.nextInt(10) == 0) {
            lv = ParticlesOption.DECREASED;
        }
        if (lv == ParticlesOption.DECREASED && this.world.random.nextInt(3) == 0) {
            lv = ParticlesOption.MINIMAL;
        }
        return lv;
    }

    public void method_3267() {
    }

    public void processGlobalEvent(int i, BlockPos arg, int j) {
        switch (i) {
            case 1023: 
            case 1028: 
            case 1038: {
                Camera lv = this.client.gameRenderer.getCamera();
                if (!lv.isReady()) break;
                double d = (double)arg.getX() - lv.getPos().x;
                double e = (double)arg.getY() - lv.getPos().y;
                double f = (double)arg.getZ() - lv.getPos().z;
                double g = Math.sqrt(d * d + e * e + f * f);
                double h = lv.getPos().x;
                double k = lv.getPos().y;
                double l = lv.getPos().z;
                if (g > 0.0) {
                    h += d / g * 2.0;
                    k += e / g * 2.0;
                    l += f / g * 2.0;
                }
                if (i == 1023) {
                    this.world.playSound(h, k, l, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (i == 1038) {
                    this.world.playSound(h, k, l, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.world.playSound(h, k, l, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void processWorldEvent(PlayerEntity arg, int i, BlockPos arg2, int j) {
        Random random = this.world.random;
        switch (i) {
            case 1035: {
                this.world.playSound(arg2, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.world.playSound(arg2, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.world.playSound(arg2, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4f + 0.8f, 0.25f));
                break;
            }
            case 1001: {
                this.world.playSound(arg2, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.world.playSound(arg2, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.world.playSound(arg2, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.world.playSound(arg2, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                Direction lv = Direction.byId(j);
                int k = lv.getOffsetX();
                int l = lv.getOffsetY();
                int m = lv.getOffsetZ();
                double d = (double)arg2.getX() + (double)k * 0.6 + 0.5;
                double e = (double)arg2.getY() + (double)l * 0.6 + 0.5;
                double f = (double)arg2.getZ() + (double)m * 0.6 + 0.5;
                for (int n = 0; n < 10; ++n) {
                    double g = random.nextDouble() * 0.2 + 0.01;
                    double h = d + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)m * 0.5;
                    double o = e + (double)l * 0.01 + (random.nextDouble() - 0.5) * (double)l * 0.5;
                    double p = f + (double)m * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
                    double q = (double)k * g + random.nextGaussian() * 0.01;
                    double r = (double)l * g + random.nextGaussian() * 0.01;
                    double s = (double)m * g + random.nextGaussian() * 0.01;
                    this.addParticle(ParticleTypes.SMOKE, h, o, p, q, r, s);
                }
                break;
            }
            case 2003: {
                double t = (double)arg2.getX() + 0.5;
                double u = arg2.getY();
                double v = (double)arg2.getZ() + 0.5;
                for (int w = 0; w < 8; ++w) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), t, u, v, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                for (double x = 0.0; x < Math.PI * 2; x += 0.15707963267948966) {
                    this.addParticle(ParticleTypes.PORTAL, t + Math.cos(x) * 5.0, u - 0.4, v + Math.sin(x) * 5.0, Math.cos(x) * -5.0, 0.0, Math.sin(x) * -5.0);
                    this.addParticle(ParticleTypes.PORTAL, t + Math.cos(x) * 5.0, u - 0.4, v + Math.sin(x) * 5.0, Math.cos(x) * -7.0, 0.0, Math.sin(x) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                double y = arg2.getX();
                double z = arg2.getY();
                double aa = arg2.getZ();
                for (int ab = 0; ab < 8; ++ab) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), y, z, aa, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                float ac = (float)(j >> 16 & 0xFF) / 255.0f;
                float ad = (float)(j >> 8 & 0xFF) / 255.0f;
                float ae = (float)(j >> 0 & 0xFF) / 255.0f;
                DefaultParticleType lv2 = i == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int af = 0; af < 100; ++af) {
                    double ag = random.nextDouble() * 4.0;
                    double ah = random.nextDouble() * Math.PI * 2.0;
                    double ai = Math.cos(ah) * ag;
                    double aj = 0.01 + random.nextDouble() * 0.5;
                    double ak = Math.sin(ah) * ag;
                    Particle lv3 = this.spawnParticle(lv2, lv2.getType().shouldAlwaysSpawn(), y + ai * 0.1, z + 0.3, aa + ak * 0.1, ai, aj, ak);
                    if (lv3 == null) continue;
                    float al = 0.75f + random.nextFloat() * 0.25f;
                    lv3.setColor(ac * al, ad * al, ae * al);
                    lv3.move((float)ag);
                }
                this.world.playSound(arg2, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState lv4 = Block.getStateFromRawId(j);
                if (!lv4.isAir()) {
                    BlockSoundGroup lv5 = lv4.getSoundGroup();
                    this.world.playSound(arg2, lv5.getBreakSound(), SoundCategory.BLOCKS, (lv5.getVolume() + 1.0f) / 2.0f, lv5.getPitch() * 0.8f, false);
                }
                this.client.particleManager.addBlockBreakParticles(arg2, lv4);
                break;
            }
            case 2004: {
                for (int am = 0; am < 20; ++am) {
                    double an = (double)arg2.getX() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    double ao = (double)arg2.getY() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    double ap = (double)arg2.getZ() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    this.world.addParticle(ParticleTypes.SMOKE, an, ao, ap, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.FLAME, an, ao, ap, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2005: {
                BoneMealItem.createParticles(this.world, arg2, j);
                break;
            }
            case 2008: {
                this.world.addParticle(ParticleTypes.EXPLOSION, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.playEffects(this.world, arg2, j > 0);
                break;
            }
            case 1501: {
                this.world.playSound(arg2, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (this.world.getRandom().nextFloat() - this.world.getRandom().nextFloat()) * 0.8f, false);
                for (int aq = 0; aq < 8; ++aq) {
                    this.world.addParticle(ParticleTypes.LARGE_SMOKE, (double)arg2.getX() + Math.random(), (double)arg2.getY() + 1.2, (double)arg2.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.world.playSound(arg2, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5f, 2.6f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.8f, false);
                for (int ar = 0; ar < 5; ++ar) {
                    double as = (double)arg2.getX() + random.nextDouble() * 0.6 + 0.2;
                    double at = (double)arg2.getY() + random.nextDouble() * 0.6 + 0.2;
                    double au = (double)arg2.getZ() + random.nextDouble() * 0.6 + 0.2;
                    this.world.addParticle(ParticleTypes.SMOKE, as, at, au, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.world.playSound(arg2, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                for (int av = 0; av < 16; ++av) {
                    double aw = (float)arg2.getX() + (5.0f + random.nextFloat() * 6.0f) / 16.0f;
                    double ax = (float)arg2.getY() + 0.8125f;
                    double ay = (float)arg2.getZ() + (5.0f + random.nextFloat() * 6.0f) / 16.0f;
                    double az = 0.0;
                    double ba = 0.0;
                    double bb = 0.0;
                    this.world.addParticle(ParticleTypes.SMOKE, aw, ax, ay, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int bc = 0; bc < 200; ++bc) {
                    float bd = random.nextFloat() * 4.0f;
                    float be = random.nextFloat() * ((float)Math.PI * 2);
                    double bf = MathHelper.cos(be) * bd;
                    double bg = 0.01 + random.nextDouble() * 0.5;
                    double bh = MathHelper.sin(be) * bd;
                    Particle lv6 = this.spawnParticle(ParticleTypes.DRAGON_BREATH, false, (double)arg2.getX() + bf * 0.1, (double)arg2.getY() + 0.3, (double)arg2.getZ() + bh * 0.1, bf, bg, bh);
                    if (lv6 == null) continue;
                    lv6.move(bd);
                }
                if (j != 1) break;
                this.world.playSound(arg2, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2009: {
                for (int bi = 0; bi < 8; ++bi) {
                    this.world.addParticle(ParticleTypes.CLOUD, (double)arg2.getX() + Math.random(), (double)arg2.getY() + 1.2, (double)arg2.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1012: {
                this.world.playSound(arg2, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1036: {
                this.world.playSound(arg2, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.world.playSound(arg2, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.world.playSound(arg2, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1011: {
                this.world.playSound(arg2, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.world.playSound(arg2, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.world.playSound(arg2, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.world.playSound(arg2, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.world.playSound(arg2, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1005: {
                this.world.playSound(arg2, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.world.playSound(arg2, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                break;
            }
            case 1029: {
                this.world.playSound(arg2, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.world.playSound(arg2, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1044: {
                this.world.playSound(arg2, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.world.playSound(arg2, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.world.playSound(arg2, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.byRawId(j) instanceof MusicDiscItem) {
                    this.playSong(((MusicDiscItem)Item.byRawId(j)).getSound(), arg2);
                    break;
                }
                this.playSong(null, arg2);
                break;
            }
            case 1015: {
                this.world.playSound(arg2, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.world.playSound(arg2, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.world.playSound(arg2, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.world.playSound(arg2, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.world.playSound(arg2, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.world.playSound(arg2, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.world.playSound(arg2, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.world.playSound(arg2, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.world.playSound(arg2, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.world.playSound(arg2, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.world.playSound(arg2, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0f, 0.8f + this.world.random.nextFloat() * 0.3f, false);
            }
        }
    }

    public void setBlockBreakingInfo(int i, BlockPos arg, int j) {
        if (j < 0 || j >= 10) {
            BlockBreakingInfo lv = (BlockBreakingInfo)this.blockBreakingInfos.remove(i);
            if (lv != null) {
                this.removeBlockBreakingInfo(lv);
            }
        } else {
            BlockBreakingInfo lv2 = (BlockBreakingInfo)this.blockBreakingInfos.get(i);
            if (lv2 != null) {
                this.removeBlockBreakingInfo(lv2);
            }
            if (lv2 == null || lv2.getPos().getX() != arg.getX() || lv2.getPos().getY() != arg.getY() || lv2.getPos().getZ() != arg.getZ()) {
                lv2 = new BlockBreakingInfo(i, arg);
                this.blockBreakingInfos.put(i, (Object)lv2);
            }
            lv2.setStage(j);
            lv2.setLastUpdateTick(this.ticks);
            ((SortedSet)this.blockBreakingProgressions.computeIfAbsent(lv2.getPos().asLong(), l -> Sets.newTreeSet())).add(lv2);
        }
    }

    public boolean isTerrainRenderComplete() {
        return this.chunksToRebuild.isEmpty() && this.chunkBuilder.isEmpty();
    }

    public void scheduleTerrainUpdate() {
        this.needsTerrainUpdate = true;
        this.cloudsDirty = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateNoCullingBlockEntities(Collection<BlockEntity> collection, Collection<BlockEntity> collection2) {
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.removeAll(collection);
            this.noCullingBlockEntities.addAll(collection2);
        }
    }

    public static int getLightmapCoordinates(BlockRenderView arg, BlockPos arg2) {
        return WorldRenderer.getLightmapCoordinates(arg, arg.getBlockState(arg2), arg2);
    }

    public static int getLightmapCoordinates(BlockRenderView arg, BlockState arg2, BlockPos arg3) {
        int k;
        if (arg2.hasEmissiveLighting(arg, arg3)) {
            return 0xF000F0;
        }
        int i = arg.getLightLevel(LightType.SKY, arg3);
        int j = arg.getLightLevel(LightType.BLOCK, arg3);
        if (j < (k = arg2.getLuminance())) {
            j = k;
        }
        return i << 20 | j << 4;
    }

    @Nullable
    public Framebuffer getEntityOutlinesFramebuffer() {
        return this.entityOutlinesFramebuffer;
    }

    @Nullable
    public Framebuffer method_29360() {
        return this.translucentFramebuffer;
    }

    @Nullable
    public Framebuffer method_29361() {
        return this.entityFramebuffer;
    }

    @Nullable
    public Framebuffer getParticlesFramebuffer() {
        return this.particlesFramebuffer;
    }

    @Nullable
    public Framebuffer method_29363() {
        return this.weatherFramebuffer;
    }

    @Nullable
    public Framebuffer method_29364() {
        return this.cloudsFramebuffer;
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5347
    extends RuntimeException {
        public class_5347(String string, Throwable throwable) {
            super(string, throwable);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ChunkInfo {
        private final ChunkBuilder.BuiltChunk chunk;
        private final Direction direction;
        private byte cullingState;
        private final int propagationLevel;

        private ChunkInfo(ChunkBuilder.BuiltChunk arg2, @Nullable Direction arg3, int i) {
            this.chunk = arg2;
            this.direction = arg3;
            this.propagationLevel = i;
        }

        public void updateCullingState(byte b, Direction arg) {
            this.cullingState = (byte)(this.cullingState | (b | 1 << arg.ordinal()));
        }

        public boolean canCull(Direction arg) {
            return (this.cullingState & 1 << arg.ordinal()) > 0;
        }
    }
}

