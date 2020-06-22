/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.EvictingQueue
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.AshParticle;
import net.minecraft.client.particle.BarrierParticle;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.BlockFallingDustParticle;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.BubblePopParticle;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.CloudParticle;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.CurrentDownParticle;
import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.client.particle.EmitterParticle;
import net.minecraft.client.particle.EmotionParticle;
import net.minecraft.client.particle.EnchantGlyphParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.ExplosionEmitterParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.ExplosionSmokeParticle;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.FishingParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.LargeFireSmokeParticle;
import net.minecraft.client.particle.LavaEmberParticle;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.ReversePortalParticle;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.SuspendParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.particle.WhiteAshParticle;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;

@Environment(value=EnvType.CLIENT)
public class ParticleManager
implements ResourceReloadListener {
    private static final List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS = ImmutableList.of((Object)ParticleTextureSheet.TERRAIN_SHEET, (Object)ParticleTextureSheet.PARTICLE_SHEET_OPAQUE, (Object)ParticleTextureSheet.PARTICLE_SHEET_LIT, (Object)ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT, (Object)ParticleTextureSheet.CUSTOM);
    protected ClientWorld world;
    private final Map<ParticleTextureSheet, Queue<Particle>> particles = Maps.newIdentityHashMap();
    private final Queue<EmitterParticle> newEmitterParticles = Queues.newArrayDeque();
    private final TextureManager textureManager;
    private final Random random = new Random();
    private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap();
    private final Queue<Particle> newParticles = Queues.newArrayDeque();
    private final Map<Identifier, SimpleSpriteProvider> spriteAwareFactories = Maps.newHashMap();
    private final SpriteAtlasTexture particleAtlasTexture = new SpriteAtlasTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);

    public ParticleManager(ClientWorld arg, TextureManager arg2) {
        arg2.registerTexture(this.particleAtlasTexture.getId(), this.particleAtlasTexture);
        this.world = arg;
        this.textureManager = arg2;
        this.registerDefaultFactories();
    }

    private void registerDefaultFactories() {
        this.registerFactory(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.EntityAmbientFactory::new);
        this.registerFactory(ParticleTypes.ANGRY_VILLAGER, EmotionParticle.AngryVillagerFactory::new);
        this.registerFactory(ParticleTypes.BARRIER, new BarrierParticle.Factory());
        this.registerFactory(ParticleTypes.BLOCK, new BlockDustParticle.Factory());
        this.registerFactory(ParticleTypes.BUBBLE, WaterBubbleParticle.Factory::new);
        this.registerFactory(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Factory::new);
        this.registerFactory(ParticleTypes.BUBBLE_POP, BubblePopParticle.Factory::new);
        this.registerFactory(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosySmokeFactory::new);
        this.registerFactory(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalSmokeFactory::new);
        this.registerFactory(ParticleTypes.CLOUD, CloudParticle.CloudFactory::new);
        this.registerFactory(ParticleTypes.COMPOSTER, SuspendParticle.Factory::new);
        this.registerFactory(ParticleTypes.CRIT, DamageParticle.Factory::new);
        this.registerFactory(ParticleTypes.CURRENT_DOWN, CurrentDownParticle.Factory::new);
        this.registerFactory(ParticleTypes.DAMAGE_INDICATOR, DamageParticle.DefaultFactory::new);
        this.registerFactory(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Factory::new);
        this.registerFactory(ParticleTypes.DOLPHIN, SuspendParticle.DolphinFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_LAVA, BlockLeakParticle.DrippingLavaFactory::new);
        this.registerFactory(ParticleTypes.FALLING_LAVA, BlockLeakParticle.FallingLavaFactory::new);
        this.registerFactory(ParticleTypes.LANDING_LAVA, BlockLeakParticle.LandingLavaFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_WATER, BlockLeakParticle.DrippingWaterFactory::new);
        this.registerFactory(ParticleTypes.FALLING_WATER, BlockLeakParticle.FallingWaterFactory::new);
        this.registerFactory(ParticleTypes.DUST, RedDustParticle.Factory::new);
        this.registerFactory(ParticleTypes.EFFECT, SpellParticle.DefaultFactory::new);
        this.registerFactory(ParticleTypes.ELDER_GUARDIAN, new ElderGuardianAppearanceParticle.Factory());
        this.registerFactory(ParticleTypes.ENCHANTED_HIT, DamageParticle.EnchantedHitFactory::new);
        this.registerFactory(ParticleTypes.ENCHANT, EnchantGlyphParticle.EnchantFactory::new);
        this.registerFactory(ParticleTypes.END_ROD, EndRodParticle.Factory::new);
        this.registerFactory(ParticleTypes.ENTITY_EFFECT, SpellParticle.EntityFactory::new);
        this.registerFactory(ParticleTypes.EXPLOSION_EMITTER, new ExplosionEmitterParticle.Factory());
        this.registerFactory(ParticleTypes.EXPLOSION, ExplosionLargeParticle.Factory::new);
        this.registerFactory(ParticleTypes.FALLING_DUST, BlockFallingDustParticle.Factory::new);
        this.registerFactory(ParticleTypes.FIREWORK, FireworksSparkParticle.ExplosionFactory::new);
        this.registerFactory(ParticleTypes.FISHING, FishingParticle.Factory::new);
        this.registerFactory(ParticleTypes.FLAME, FlameParticle.Factory::new);
        this.registerFactory(ParticleTypes.SOUL, SoulParticle.Factory::new);
        this.registerFactory(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Factory::new);
        this.registerFactory(ParticleTypes.FLASH, FireworksSparkParticle.FlashFactory::new);
        this.registerFactory(ParticleTypes.HAPPY_VILLAGER, SuspendParticle.HappyVillagerFactory::new);
        this.registerFactory(ParticleTypes.HEART, EmotionParticle.HeartFactory::new);
        this.registerFactory(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantFactory::new);
        this.registerFactory(ParticleTypes.ITEM, new CrackParticle.ItemFactory());
        this.registerFactory(ParticleTypes.ITEM_SLIME, new CrackParticle.SlimeballFactory());
        this.registerFactory(ParticleTypes.ITEM_SNOWBALL, new CrackParticle.SnowballFactory());
        this.registerFactory(ParticleTypes.LARGE_SMOKE, LargeFireSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.LAVA, LavaEmberParticle.Factory::new);
        this.registerFactory(ParticleTypes.MYCELIUM, SuspendParticle.MyceliumFactory::new);
        this.registerFactory(ParticleTypes.NAUTILUS, EnchantGlyphParticle.NautilusFactory::new);
        this.registerFactory(ParticleTypes.NOTE, NoteParticle.Factory::new);
        this.registerFactory(ParticleTypes.POOF, ExplosionSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.PORTAL, PortalParticle.Factory::new);
        this.registerFactory(ParticleTypes.RAIN, RainSplashParticle.Factory::new);
        this.registerFactory(ParticleTypes.SMOKE, FireSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.SNEEZE, CloudParticle.SneezeFactory::new);
        this.registerFactory(ParticleTypes.SPIT, SpitParticle.Factory::new);
        this.registerFactory(ParticleTypes.SWEEP_ATTACK, SweepAttackParticle.Factory::new);
        this.registerFactory(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Factory::new);
        this.registerFactory(ParticleTypes.SQUID_INK, SquidInkParticle.Factory::new);
        this.registerFactory(ParticleTypes.UNDERWATER, WaterSuspendParticle.UnderwaterFactory::new);
        this.registerFactory(ParticleTypes.SPLASH, WaterSplashParticle.SplashFactory::new);
        this.registerFactory(ParticleTypes.WITCH, SpellParticle.WitchFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_HONEY, BlockLeakParticle.DrippingHoneyFactory::new);
        this.registerFactory(ParticleTypes.FALLING_HONEY, BlockLeakParticle.FallingHoneyFactory::new);
        this.registerFactory(ParticleTypes.LANDING_HONEY, BlockLeakParticle.LandingHoneyFactory::new);
        this.registerFactory(ParticleTypes.FALLING_NECTAR, BlockLeakParticle.FallingNectarFactory::new);
        this.registerFactory(ParticleTypes.ASH, AshParticle.Factory::new);
        this.registerFactory(ParticleTypes.CRIMSON_SPORE, WaterSuspendParticle.CrimsonSporeFactory::new);
        this.registerFactory(ParticleTypes.WARPED_SPORE, WaterSuspendParticle.WarpedSporeFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, BlockLeakParticle.DrippingObsidianTearFactory::new);
        this.registerFactory(ParticleTypes.FALLING_OBSIDIAN_TEAR, BlockLeakParticle.FallingObsidianTearFactory::new);
        this.registerFactory(ParticleTypes.LANDING_OBSIDIAN_TEAR, BlockLeakParticle.LandingObsidianTearFactory::new);
        this.registerFactory(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.Factory::new);
        this.registerFactory(ParticleTypes.WHITE_ASH, WhiteAshParticle.Factory::new);
    }

    private <T extends ParticleEffect> void registerFactory(ParticleType<T> arg, ParticleFactory<T> arg2) {
        this.factories.put(Registry.PARTICLE_TYPE.getRawId(arg), arg2);
    }

    private <T extends ParticleEffect> void registerFactory(ParticleType<T> arg, SpriteAwareFactory<T> arg2) {
        SimpleSpriteProvider lv = new SimpleSpriteProvider();
        this.spriteAwareFactories.put(Registry.PARTICLE_TYPE.getId(arg), lv);
        this.factories.put(Registry.PARTICLE_TYPE.getRawId(arg), arg2.create(lv));
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg, ResourceManager arg23, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        ConcurrentMap map = Maps.newConcurrentMap();
        CompletableFuture[] completableFutures = (CompletableFuture[])Registry.PARTICLE_TYPE.getIds().stream().map(arg2 -> CompletableFuture.runAsync(() -> this.loadTextureList(arg23, (Identifier)arg2, map), executor)).toArray(CompletableFuture[]::new);
        return ((CompletableFuture)((CompletableFuture)CompletableFuture.allOf(completableFutures).thenApplyAsync(void_ -> {
            arg3.startTick();
            arg3.push("stitching");
            SpriteAtlasTexture.Data lv = this.particleAtlasTexture.stitch(arg23, map.values().stream().flatMap(Collection::stream), arg3, 0);
            arg3.pop();
            arg3.endTick();
            return lv;
        }, executor)).thenCompose(arg::whenPrepared)).thenAcceptAsync(arg22 -> {
            this.particles.clear();
            arg4.startTick();
            arg4.push("upload");
            this.particleAtlasTexture.upload((SpriteAtlasTexture.Data)arg22);
            arg4.swap("bindSpriteSets");
            Sprite lv = this.particleAtlasTexture.getSprite(MissingSprite.getMissingSpriteId());
            map.forEach((arg2, list) -> {
                ImmutableList immutableList = list.isEmpty() ? ImmutableList.of((Object)lv) : (ImmutableList)list.stream().map(this.particleAtlasTexture::getSprite).collect(ImmutableList.toImmutableList());
                this.spriteAwareFactories.get(arg2).setSprites((List<Sprite>)immutableList);
            });
            arg4.pop();
            arg4.endTick();
        }, executor2);
    }

    public void clearAtlas() {
        this.particleAtlasTexture.clear();
    }

    private void loadTextureList(ResourceManager arg2, Identifier arg22, Map<Identifier, List<Identifier>> map) {
        Identifier lv = new Identifier(arg22.getNamespace(), "particles/" + arg22.getPath() + ".json");
        try (Resource lv2 = arg2.getResource(lv);
             InputStreamReader reader = new InputStreamReader(lv2.getInputStream(), Charsets.UTF_8);){
            ParticleTextureData lv3 = ParticleTextureData.load(JsonHelper.deserialize(reader));
            List<Identifier> list = lv3.getTextureList();
            boolean bl = this.spriteAwareFactories.containsKey(arg22);
            if (list == null) {
                if (bl) {
                    throw new IllegalStateException("Missing texture list for particle " + arg22);
                }
            } else {
                if (!bl) {
                    throw new IllegalStateException("Redundant texture list for particle " + arg22);
                }
                map.put(arg22, list.stream().map(arg -> new Identifier(arg.getNamespace(), "particle/" + arg.getPath())).collect(Collectors.toList()));
            }
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Failed to load description for particle " + arg22, iOException);
        }
    }

    public void addEmitter(Entity arg, ParticleEffect arg2) {
        this.newEmitterParticles.add(new EmitterParticle(this.world, arg, arg2));
    }

    public void addEmitter(Entity arg, ParticleEffect arg2, int i) {
        this.newEmitterParticles.add(new EmitterParticle(this.world, arg, arg2, i));
    }

    @Nullable
    public Particle addParticle(ParticleEffect arg, double d, double e, double f, double g, double h, double i) {
        Particle lv = this.createParticle(arg, d, e, f, g, h, i);
        if (lv != null) {
            this.addParticle(lv);
            return lv;
        }
        return null;
    }

    @Nullable
    private <T extends ParticleEffect> Particle createParticle(T arg, double d, double e, double f, double g, double h, double i) {
        ParticleFactory lv = (ParticleFactory)this.factories.get(Registry.PARTICLE_TYPE.getRawId(arg.getType()));
        if (lv == null) {
            return null;
        }
        return lv.createParticle(arg, this.world, d, e, f, g, h, i);
    }

    public void addParticle(Particle arg) {
        this.newParticles.add(arg);
    }

    public void tick() {
        this.particles.forEach((arg, queue) -> {
            this.world.getProfiler().push(arg.toString());
            this.tickParticles((Collection<Particle>)queue);
            this.world.getProfiler().pop();
        });
        if (!this.newEmitterParticles.isEmpty()) {
            ArrayList list = Lists.newArrayList();
            for (EmitterParticle lv : this.newEmitterParticles) {
                lv.tick();
                if (lv.isAlive()) continue;
                list.add(lv);
            }
            this.newEmitterParticles.removeAll(list);
        }
        if (!this.newParticles.isEmpty()) {
            Particle lv2;
            while ((lv2 = this.newParticles.poll()) != null) {
                this.particles.computeIfAbsent(lv2.getType(), arg -> EvictingQueue.create((int)16384)).add(lv2);
            }
        }
    }

    private void tickParticles(Collection<Particle> collection) {
        if (!collection.isEmpty()) {
            Iterator<Particle> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Particle lv = iterator.next();
                this.tickParticle(lv);
                if (lv.isAlive()) continue;
                iterator.remove();
            }
        }
    }

    private void tickParticle(Particle arg) {
        try {
            arg.tick();
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Ticking Particle");
            CrashReportSection lv2 = lv.addElement("Particle being ticked");
            lv2.add("Particle", arg::toString);
            lv2.add("Particle Type", arg.getType()::toString);
            throw new CrashException(lv);
        }
    }

    public void renderParticles(MatrixStack arg, VertexConsumerProvider.Immediate arg2, LightmapTextureManager arg3, Camera arg4, float f) {
        arg3.enable();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.enableFog();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(arg.peek().getModel());
        for (ParticleTextureSheet lv : PARTICLE_TEXTURE_SHEETS) {
            Iterable iterable = this.particles.get(lv);
            if (iterable == null) continue;
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            Tessellator lv2 = Tessellator.getInstance();
            BufferBuilder lv3 = lv2.getBuffer();
            lv.begin(lv3, this.textureManager);
            for (Particle lv4 : iterable) {
                try {
                    lv4.buildGeometry(lv3, arg4, f);
                }
                catch (Throwable throwable) {
                    CrashReport lv5 = CrashReport.create(throwable, "Rendering Particle");
                    CrashReportSection lv6 = lv5.addElement("Particle being rendered");
                    lv6.add("Particle", lv4::toString);
                    lv6.add("Particle Type", lv::toString);
                    throw new CrashException(lv5);
                }
            }
            lv.draw(lv2);
        }
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
        RenderSystem.disableBlend();
        RenderSystem.defaultAlphaFunc();
        arg3.disable();
        RenderSystem.disableFog();
    }

    public void setWorld(@Nullable ClientWorld arg) {
        this.world = arg;
        this.particles.clear();
        this.newEmitterParticles.clear();
    }

    public void addBlockBreakParticles(BlockPos arg, BlockState arg2) {
        if (arg2.isAir()) {
            return;
        }
        VoxelShape lv = arg2.getOutlineShape(this.world, arg);
        double d2 = 0.25;
        lv.forEachBox((d, e, f, g, h, i) -> {
            double j = Math.min(1.0, g - d);
            double k = Math.min(1.0, h - e);
            double l = Math.min(1.0, i - f);
            int m = Math.max(2, MathHelper.ceil(j / 0.25));
            int n = Math.max(2, MathHelper.ceil(k / 0.25));
            int o = Math.max(2, MathHelper.ceil(l / 0.25));
            for (int p = 0; p < m; ++p) {
                for (int q = 0; q < n; ++q) {
                    for (int r = 0; r < o; ++r) {
                        double s = ((double)p + 0.5) / (double)m;
                        double t = ((double)q + 0.5) / (double)n;
                        double u = ((double)r + 0.5) / (double)o;
                        double v = s * j + d;
                        double w = t * k + e;
                        double x = u * l + f;
                        this.addParticle(new BlockDustParticle(this.world, (double)arg.getX() + v, (double)arg.getY() + w, (double)arg.getZ() + x, s - 0.5, t - 0.5, u - 0.5, arg2).setBlockPos(arg));
                    }
                }
            }
        });
    }

    public void addBlockBreakingParticles(BlockPos arg, Direction arg2) {
        BlockState lv = this.world.getBlockState(arg);
        if (lv.getRenderType() == BlockRenderType.INVISIBLE) {
            return;
        }
        int i = arg.getX();
        int j = arg.getY();
        int k = arg.getZ();
        float f = 0.1f;
        Box lv2 = lv.getOutlineShape(this.world, arg).getBoundingBox();
        double d = (double)i + this.random.nextDouble() * (lv2.maxX - lv2.minX - (double)0.2f) + (double)0.1f + lv2.minX;
        double e = (double)j + this.random.nextDouble() * (lv2.maxY - lv2.minY - (double)0.2f) + (double)0.1f + lv2.minY;
        double g = (double)k + this.random.nextDouble() * (lv2.maxZ - lv2.minZ - (double)0.2f) + (double)0.1f + lv2.minZ;
        if (arg2 == Direction.DOWN) {
            e = (double)j + lv2.minY - (double)0.1f;
        }
        if (arg2 == Direction.UP) {
            e = (double)j + lv2.maxY + (double)0.1f;
        }
        if (arg2 == Direction.NORTH) {
            g = (double)k + lv2.minZ - (double)0.1f;
        }
        if (arg2 == Direction.SOUTH) {
            g = (double)k + lv2.maxZ + (double)0.1f;
        }
        if (arg2 == Direction.WEST) {
            d = (double)i + lv2.minX - (double)0.1f;
        }
        if (arg2 == Direction.EAST) {
            d = (double)i + lv2.maxX + (double)0.1f;
        }
        this.addParticle(new BlockDustParticle(this.world, d, e, g, 0.0, 0.0, 0.0, lv).setBlockPos(arg).move(0.2f).scale(0.6f));
    }

    public String getDebugString() {
        return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
    }

    @Environment(value=EnvType.CLIENT)
    class SimpleSpriteProvider
    implements SpriteProvider {
        private List<Sprite> sprites;

        private SimpleSpriteProvider() {
        }

        @Override
        public Sprite getSprite(int i, int j) {
            return this.sprites.get(i * (this.sprites.size() - 1) / j);
        }

        @Override
        public Sprite getSprite(Random random) {
            return this.sprites.get(random.nextInt(this.sprites.size()));
        }

        public void setSprites(List<Sprite> list) {
            this.sprites = ImmutableList.copyOf(list);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface SpriteAwareFactory<T extends ParticleEffect> {
        public ParticleFactory<T> create(SpriteProvider var1);
    }
}

