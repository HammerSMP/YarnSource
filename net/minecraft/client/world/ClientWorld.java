/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ColorResolver;

@Environment(value=EnvType.CLIENT)
public class ClientWorld
extends World {
    private final Int2ObjectMap<Entity> regularEntities = new Int2ObjectOpenHashMap();
    private final ClientPlayNetworkHandler netHandler;
    private final WorldRenderer worldRenderer;
    private final Properties clientWorldProperties;
    private final SkyProperties skyProperties;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<AbstractClientPlayerEntity> players = Lists.newArrayList();
    private Scoreboard scoreboard = new Scoreboard();
    private final Map<String, MapState> mapStates = Maps.newHashMap();
    private int lightningTicksLeft;
    private final Object2ObjectArrayMap<ColorResolver, BiomeColorCache> colorCache = Util.make(new Object2ObjectArrayMap(3), object2ObjectArrayMap -> {
        object2ObjectArrayMap.put((Object)BiomeColors.GRASS_COLOR, (Object)new BiomeColorCache());
        object2ObjectArrayMap.put((Object)BiomeColors.FOLIAGE_COLOR, (Object)new BiomeColorCache());
        object2ObjectArrayMap.put((Object)BiomeColors.WATER_COLOR, (Object)new BiomeColorCache());
    });
    private final ClientChunkManager chunkManager;

    public ClientWorld(ClientPlayNetworkHandler arg, Properties arg2, RegistryKey<World> arg3, RegistryKey<DimensionType> arg4, DimensionType arg5, int i, Supplier<Profiler> supplier, WorldRenderer arg6, boolean bl, long l) {
        super(arg2, arg3, arg4, arg5, supplier, true, bl, l);
        this.chunkManager = new ClientChunkManager(this, i);
        this.clientWorldProperties = arg2;
        this.netHandler = arg;
        this.worldRenderer = arg6;
        this.skyProperties = SkyProperties.byDimensionType(arg.method_29091().getDimensionTypeRegistry().getKey(arg5));
        this.setSpawnPos(new BlockPos(8, 64, 8));
        this.calculateAmbientDarkness();
        this.initWeatherGradients();
    }

    public SkyProperties getSkyProperties() {
        return this.skyProperties;
    }

    public void tick(BooleanSupplier booleanSupplier) {
        this.getWorldBorder().tick();
        this.tickTime();
        this.getProfiler().push("blocks");
        this.chunkManager.tick(booleanSupplier);
        this.getProfiler().pop();
    }

    private void tickTime() {
        this.method_29089(this.properties.getTime() + 1L);
        if (this.properties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            this.setTimeOfDay(this.properties.getTimeOfDay() + 1L);
        }
    }

    public void method_29089(long l) {
        this.clientWorldProperties.setTime(l);
    }

    public void setTimeOfDay(long l) {
        if (l < 0L) {
            l = -l;
            this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
        } else {
            this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, null);
        }
        this.clientWorldProperties.setTimeOfDay(l);
    }

    public Iterable<Entity> getEntities() {
        return this.regularEntities.values();
    }

    public void tickEntities() {
        Profiler lv = this.getProfiler();
        lv.push("entities");
        ObjectIterator objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
            Entity lv2 = (Entity)entry.getValue();
            if (lv2.hasVehicle()) continue;
            lv.push("tick");
            if (!lv2.removed) {
                this.tickEntity(this::tickEntity, lv2);
            }
            lv.pop();
            lv.push("remove");
            if (lv2.removed) {
                objectIterator.remove();
                this.finishRemovingEntity(lv2);
            }
            lv.pop();
        }
        this.tickBlockEntities();
        lv.pop();
    }

    public void tickEntity(Entity arg) {
        if (!(arg instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(arg)) {
            this.checkChunk(arg);
            return;
        }
        arg.resetPosition(arg.getX(), arg.getY(), arg.getZ());
        arg.prevYaw = arg.yaw;
        arg.prevPitch = arg.pitch;
        if (arg.updateNeeded || arg.isSpectator()) {
            ++arg.age;
            this.getProfiler().push(() -> Registry.ENTITY_TYPE.getId(arg.getType()).toString());
            arg.tick();
            this.getProfiler().pop();
        }
        this.checkChunk(arg);
        if (arg.updateNeeded) {
            for (Entity lv : arg.getPassengerList()) {
                this.tickPassenger(arg, lv);
            }
        }
    }

    public void tickPassenger(Entity arg, Entity arg2) {
        if (arg2.removed || arg2.getVehicle() != arg) {
            arg2.stopRiding();
            return;
        }
        if (!(arg2 instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(arg2)) {
            return;
        }
        arg2.resetPosition(arg2.getX(), arg2.getY(), arg2.getZ());
        arg2.prevYaw = arg2.yaw;
        arg2.prevPitch = arg2.pitch;
        if (arg2.updateNeeded) {
            ++arg2.age;
            arg2.tickRiding();
        }
        this.checkChunk(arg2);
        if (arg2.updateNeeded) {
            for (Entity lv : arg2.getPassengerList()) {
                this.tickPassenger(arg2, lv);
            }
        }
    }

    private void checkChunk(Entity arg) {
        if (!arg.method_29240()) {
            return;
        }
        this.getProfiler().push("chunkCheck");
        int i = MathHelper.floor(arg.getX() / 16.0);
        int j = MathHelper.floor(arg.getY() / 16.0);
        int k = MathHelper.floor(arg.getZ() / 16.0);
        if (!arg.updateNeeded || arg.chunkX != i || arg.chunkY != j || arg.chunkZ != k) {
            if (arg.updateNeeded && this.isChunkLoaded(arg.chunkX, arg.chunkZ)) {
                this.getChunk(arg.chunkX, arg.chunkZ).remove(arg, arg.chunkY);
            }
            if (arg.teleportRequested() || this.isChunkLoaded(i, k)) {
                this.getChunk(i, k).addEntity(arg);
            } else {
                if (arg.updateNeeded) {
                    LOGGER.warn("Entity {} left loaded chunk area", (Object)arg);
                }
                arg.updateNeeded = false;
            }
        }
        this.getProfiler().pop();
    }

    public void unloadBlockEntities(WorldChunk arg) {
        this.unloadedBlockEntities.addAll(arg.getBlockEntities().values());
        this.chunkManager.getLightingProvider().setLightEnabled(arg.getPos(), false);
    }

    public void resetChunkColor(int i, int j) {
        this.colorCache.forEach((colorResolver, arg) -> arg.reset(i, j));
    }

    public void reloadColor() {
        this.colorCache.forEach((colorResolver, arg) -> arg.reset());
    }

    @Override
    public boolean isChunkLoaded(int i, int j) {
        return true;
    }

    public int getRegularEntityCount() {
        return this.regularEntities.size();
    }

    public void addPlayer(int i, AbstractClientPlayerEntity arg) {
        this.addEntityPrivate(i, arg);
        this.players.add(arg);
    }

    public void addEntity(int i, Entity arg) {
        this.addEntityPrivate(i, arg);
    }

    private void addEntityPrivate(int i, Entity arg) {
        this.removeEntity(i);
        this.regularEntities.put(i, (Object)arg);
        this.getChunkManager().getChunk(MathHelper.floor(arg.getX() / 16.0), MathHelper.floor(arg.getZ() / 16.0), ChunkStatus.FULL, true).addEntity(arg);
    }

    public void removeEntity(int i) {
        Entity lv = (Entity)this.regularEntities.remove(i);
        if (lv != null) {
            lv.remove();
            this.finishRemovingEntity(lv);
        }
    }

    private void finishRemovingEntity(Entity arg) {
        arg.detach();
        if (arg.updateNeeded) {
            this.getChunk(arg.chunkX, arg.chunkZ).remove(arg);
        }
        this.players.remove(arg);
    }

    public void addEntitiesToChunk(WorldChunk arg) {
        for (Int2ObjectMap.Entry entry : this.regularEntities.int2ObjectEntrySet()) {
            Entity lv = (Entity)entry.getValue();
            int i = MathHelper.floor(lv.getX() / 16.0);
            int j = MathHelper.floor(lv.getZ() / 16.0);
            if (i != arg.getPos().x || j != arg.getPos().z) continue;
            arg.addEntity(lv);
        }
    }

    @Override
    @Nullable
    public Entity getEntityById(int i) {
        return (Entity)this.regularEntities.get(i);
    }

    public void setBlockStateWithoutNeighborUpdates(BlockPos arg, BlockState arg2) {
        this.setBlockState(arg, arg2, 19);
    }

    @Override
    public void disconnect() {
        this.netHandler.getConnection().disconnect(new TranslatableText("multiplayer.status.quitting"));
    }

    public void doRandomBlockDisplayTicks(int i, int j, int k) {
        int l = 32;
        Random random = new Random();
        boolean bl = false;
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
            for (ItemStack lv : this.client.player.getItemsHand()) {
                if (lv.getItem() != Blocks.BARRIER.asItem()) continue;
                bl = true;
                break;
            }
        }
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (int m = 0; m < 667; ++m) {
            this.randomBlockDisplayTick(i, j, k, 16, random, bl, lv2);
            this.randomBlockDisplayTick(i, j, k, 32, random, bl, lv2);
        }
    }

    public void randomBlockDisplayTick(int i, int j, int k, int l, Random random, boolean bl, BlockPos.Mutable arg) {
        int m = i + this.random.nextInt(l) - this.random.nextInt(l);
        int n = j + this.random.nextInt(l) - this.random.nextInt(l);
        int o = k + this.random.nextInt(l) - this.random.nextInt(l);
        arg.set(m, n, o);
        BlockState lv = this.getBlockState(arg);
        lv.getBlock().randomDisplayTick(lv, this, arg, random);
        FluidState lv2 = this.getFluidState(arg);
        if (!lv2.isEmpty()) {
            lv2.randomDisplayTick(this, arg, random);
            ParticleEffect lv3 = lv2.getParticle();
            if (lv3 != null && this.random.nextInt(10) == 0) {
                boolean bl2 = lv.isSideSolidFullSquare(this, arg, Direction.DOWN);
                Vec3i lv4 = arg.down();
                this.addParticle((BlockPos)lv4, this.getBlockState((BlockPos)lv4), lv3, bl2);
            }
        }
        if (bl && lv.isOf(Blocks.BARRIER)) {
            this.addParticle(ParticleTypes.BARRIER, (double)m + 0.5, (double)n + 0.5, (double)o + 0.5, 0.0, 0.0, 0.0);
        }
        if (!lv.isFullCube(this, arg)) {
            this.getBiome(arg).getParticleConfig().ifPresent(arg2 -> {
                if (arg2.shouldAddParticle(this.random)) {
                    this.addParticle(arg2.getParticleType(), (double)arg.getX() + this.random.nextDouble(), (double)arg.getY() + this.random.nextDouble(), (double)arg.getZ() + this.random.nextDouble(), 0.0, 0.0, 0.0);
                }
            });
        }
    }

    private void addParticle(BlockPos arg, BlockState arg2, ParticleEffect arg3, boolean bl) {
        if (!arg2.getFluidState().isEmpty()) {
            return;
        }
        VoxelShape lv = arg2.getCollisionShape(this, arg);
        double d = lv.getMax(Direction.Axis.Y);
        if (d < 1.0) {
            if (bl) {
                this.addParticle(arg.getX(), arg.getX() + 1, arg.getZ(), arg.getZ() + 1, (double)(arg.getY() + 1) - 0.05, arg3);
            }
        } else if (!arg2.isIn(BlockTags.IMPERMEABLE)) {
            double e = lv.getMin(Direction.Axis.Y);
            if (e > 0.0) {
                this.addParticle(arg, arg3, lv, (double)arg.getY() + e - 0.05);
            } else {
                BlockPos lv2 = arg.down();
                BlockState lv3 = this.getBlockState(lv2);
                VoxelShape lv4 = lv3.getCollisionShape(this, lv2);
                double f = lv4.getMax(Direction.Axis.Y);
                if (f < 1.0 && lv3.getFluidState().isEmpty()) {
                    this.addParticle(arg, arg3, lv, (double)arg.getY() - 0.05);
                }
            }
        }
    }

    private void addParticle(BlockPos arg, ParticleEffect arg2, VoxelShape arg3, double d) {
        this.addParticle((double)arg.getX() + arg3.getMin(Direction.Axis.X), (double)arg.getX() + arg3.getMax(Direction.Axis.X), (double)arg.getZ() + arg3.getMin(Direction.Axis.Z), (double)arg.getZ() + arg3.getMax(Direction.Axis.Z), d, arg2);
    }

    private void addParticle(double d, double e, double f, double g, double h, ParticleEffect arg) {
        this.addParticle(arg, MathHelper.lerp(this.random.nextDouble(), d, e), h, MathHelper.lerp(this.random.nextDouble(), f, g), 0.0, 0.0, 0.0);
    }

    public void finishRemovingEntities() {
        ObjectIterator objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
            Entity lv = (Entity)entry.getValue();
            if (!lv.removed) continue;
            objectIterator.remove();
            this.finishRemovingEntity(lv);
        }
    }

    @Override
    public CrashReportSection addDetailsToCrashReport(CrashReport arg) {
        CrashReportSection lv = super.addDetailsToCrashReport(arg);
        lv.add("Server brand", () -> this.client.player.getServerBrand());
        lv.add("Server type", () -> this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
        return lv;
    }

    @Override
    public void playSound(@Nullable PlayerEntity arg, double d, double e, double f, SoundEvent arg2, SoundCategory arg3, float g, float h) {
        if (arg == this.client.player) {
            this.playSound(d, e, f, arg2, arg3, g, h, false);
        }
    }

    @Override
    public void playSoundFromEntity(@Nullable PlayerEntity arg, Entity arg2, SoundEvent arg3, SoundCategory arg4, float f, float g) {
        if (arg == this.client.player) {
            this.client.getSoundManager().play(new EntityTrackingSoundInstance(arg3, arg4, arg2));
        }
    }

    public void playSound(BlockPos arg, SoundEvent arg2, SoundCategory arg3, float f, float g, boolean bl) {
        this.playSound((double)arg.getX() + 0.5, (double)arg.getY() + 0.5, (double)arg.getZ() + 0.5, arg2, arg3, f, g, bl);
    }

    @Override
    public void playSound(double d, double e, double f, SoundEvent arg, SoundCategory arg2, float g, float h, boolean bl) {
        double i = this.client.gameRenderer.getCamera().getPos().squaredDistanceTo(d, e, f);
        PositionedSoundInstance lv = new PositionedSoundInstance(arg, arg2, g, h, d, e, f);
        if (bl && i > 100.0) {
            double j = Math.sqrt(i) / 40.0;
            this.client.getSoundManager().play(lv, (int)(j * 20.0));
        } else {
            this.client.getSoundManager().play(lv);
        }
    }

    @Override
    public void addFireworkParticle(double d, double e, double f, double g, double h, double i, @Nullable CompoundTag arg) {
        this.client.particleManager.addParticle(new FireworksSparkParticle.FireworkParticle(this, d, e, f, g, h, i, this.client.particleManager, arg));
    }

    @Override
    public void sendPacket(Packet<?> arg) {
        this.netHandler.sendPacket(arg);
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.netHandler.getRecipeManager();
    }

    public void setScoreboard(Scoreboard arg) {
        this.scoreboard = arg;
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return DummyClientTickScheduler.get();
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return DummyClientTickScheduler.get();
    }

    @Override
    public ClientChunkManager getChunkManager() {
        return this.chunkManager;
    }

    @Override
    @Nullable
    public MapState getMapState(String string) {
        return this.mapStates.get(string);
    }

    @Override
    public void putMapState(MapState arg) {
        this.mapStates.put(arg.getId(), arg);
    }

    @Override
    public int getNextMapId() {
        return 0;
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public RegistryTagManager getTagManager() {
        return this.netHandler.getTagManager();
    }

    @Override
    public void updateListeners(BlockPos arg, BlockState arg2, BlockState arg3, int i) {
        this.worldRenderer.updateBlock(this, arg, arg2, arg3, i);
    }

    @Override
    public void scheduleBlockRerenderIfNeeded(BlockPos arg, BlockState arg2, BlockState arg3) {
        this.worldRenderer.scheduleBlockRerenderIfNeeded(arg, arg2, arg3);
    }

    public void scheduleBlockRenders(int i, int j, int k) {
        this.worldRenderer.scheduleBlockRenders(i, j, k);
    }

    @Override
    public void setBlockBreakingInfo(int i, BlockPos arg, int j) {
        this.worldRenderer.setBlockBreakingInfo(i, arg, j);
    }

    @Override
    public void syncGlobalEvent(int i, BlockPos arg, int j) {
        this.worldRenderer.processGlobalEvent(i, arg, j);
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity arg, int i, BlockPos arg2, int j) {
        try {
            this.worldRenderer.processWorldEvent(arg, i, arg2, j);
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Playing level event");
            CrashReportSection lv2 = lv.addElement("Level event being played");
            lv2.add("Block coordinates", CrashReportSection.createPositionString(arg2));
            lv2.add("Event source", arg);
            lv2.add("Event type", i);
            lv2.add("Event data", j);
            throw new CrashException(lv);
        }
    }

    @Override
    public void addParticle(ParticleEffect arg, double d, double e, double f, double g, double h, double i) {
        this.worldRenderer.addParticle(arg, arg.getType().shouldAlwaysSpawn(), d, e, f, g, h, i);
    }

    @Override
    public void addParticle(ParticleEffect arg, boolean bl, double d, double e, double f, double g, double h, double i) {
        this.worldRenderer.addParticle(arg, arg.getType().shouldAlwaysSpawn() || bl, d, e, f, g, h, i);
    }

    @Override
    public void addImportantParticle(ParticleEffect arg, double d, double e, double f, double g, double h, double i) {
        this.worldRenderer.addParticle(arg, false, true, d, e, f, g, h, i);
    }

    @Override
    public void addImportantParticle(ParticleEffect arg, boolean bl, double d, double e, double f, double g, double h, double i) {
        this.worldRenderer.addParticle(arg, arg.getType().shouldAlwaysSpawn() || bl, true, d, e, f, g, h, i);
    }

    public List<AbstractClientPlayerEntity> getPlayers() {
        return this.players;
    }

    @Override
    public Biome getGeneratorStoredBiome(int i, int j, int k) {
        return Biomes.PLAINS;
    }

    public float method_23783(float f) {
        float g = this.getSkyAngle(f);
        float h = 1.0f - (MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.2f);
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        h = 1.0f - h;
        h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0f) / 16.0));
        h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0f) / 16.0));
        return h * 0.8f + 0.2f;
    }

    public Vec3d method_23777(BlockPos arg, float f) {
        float p;
        float g = this.getSkyAngle(f);
        float h = MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        Biome lv = this.getBiome(arg);
        int i = lv.getSkyColor();
        float j = (float)(i >> 16 & 0xFF) / 255.0f;
        float k = (float)(i >> 8 & 0xFF) / 255.0f;
        float l = (float)(i & 0xFF) / 255.0f;
        j *= h;
        k *= h;
        l *= h;
        float m = this.getRainGradient(f);
        if (m > 0.0f) {
            float n = (j * 0.3f + k * 0.59f + l * 0.11f) * 0.6f;
            float o = 1.0f - m * 0.75f;
            j = j * o + n * (1.0f - o);
            k = k * o + n * (1.0f - o);
            l = l * o + n * (1.0f - o);
        }
        if ((p = this.getThunderGradient(f)) > 0.0f) {
            float q = (j * 0.3f + k * 0.59f + l * 0.11f) * 0.2f;
            float r = 1.0f - p * 0.75f;
            j = j * r + q * (1.0f - r);
            k = k * r + q * (1.0f - r);
            l = l * r + q * (1.0f - r);
        }
        if (this.lightningTicksLeft > 0) {
            float s = (float)this.lightningTicksLeft - f;
            if (s > 1.0f) {
                s = 1.0f;
            }
            j = j * (1.0f - (s *= 0.45f)) + 0.8f * s;
            k = k * (1.0f - s) + 0.8f * s;
            l = l * (1.0f - s) + 1.0f * s;
        }
        return new Vec3d(j, k, l);
    }

    public Vec3d getCloudsColor(float f) {
        float g = this.getSkyAngle(f);
        float h = MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        float i = 1.0f;
        float j = 1.0f;
        float k = 1.0f;
        float l = this.getRainGradient(f);
        if (l > 0.0f) {
            float m = (i * 0.3f + j * 0.59f + k * 0.11f) * 0.6f;
            float n = 1.0f - l * 0.95f;
            i = i * n + m * (1.0f - n);
            j = j * n + m * (1.0f - n);
            k = k * n + m * (1.0f - n);
        }
        i *= h * 0.9f + 0.1f;
        j *= h * 0.9f + 0.1f;
        k *= h * 0.85f + 0.15f;
        float o = this.getThunderGradient(f);
        if (o > 0.0f) {
            float p = (i * 0.3f + j * 0.59f + k * 0.11f) * 0.2f;
            float q = 1.0f - o * 0.95f;
            i = i * q + p * (1.0f - q);
            j = j * q + p * (1.0f - q);
            k = k * q + p * (1.0f - q);
        }
        return new Vec3d(i, j, k);
    }

    public float method_23787(float f) {
        float g = this.getSkyAngle(f);
        float h = 1.0f - (MathHelper.cos(g * ((float)Math.PI * 2)) * 2.0f + 0.25f);
        h = MathHelper.clamp(h, 0.0f, 1.0f);
        return h * h * 0.5f;
    }

    public int getLightningTicksLeft() {
        return this.lightningTicksLeft;
    }

    @Override
    public void setLightningTicksLeft(int i) {
        this.lightningTicksLeft = i;
    }

    @Override
    public float getBrightness(Direction arg, boolean bl) {
        boolean bl2 = this.getDimension().isNether();
        if (!bl) {
            return bl2 ? 0.9f : 1.0f;
        }
        switch (arg) {
            case DOWN: {
                return bl2 ? 0.9f : 0.5f;
            }
            case UP: {
                return bl2 ? 0.9f : 1.0f;
            }
            case NORTH: 
            case SOUTH: {
                return 0.8f;
            }
            case WEST: 
            case EAST: {
                return 0.6f;
            }
        }
        return 1.0f;
    }

    @Override
    public int getColor(BlockPos arg, ColorResolver colorResolver) {
        BiomeColorCache lv = (BiomeColorCache)this.colorCache.get((Object)colorResolver);
        return lv.getBiomeColor(arg, () -> this.calculateColor(arg, colorResolver));
    }

    public int calculateColor(BlockPos arg, ColorResolver colorResolver) {
        int i = MinecraftClient.getInstance().options.biomeBlendRadius;
        if (i == 0) {
            return colorResolver.getColor(this.getBiome(arg), arg.getX(), arg.getZ());
        }
        int j = (i * 2 + 1) * (i * 2 + 1);
        int k = 0;
        int l = 0;
        int m = 0;
        CuboidBlockIterator lv = new CuboidBlockIterator(arg.getX() - i, arg.getY(), arg.getZ() - i, arg.getX() + i, arg.getY(), arg.getZ() + i);
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        while (lv.step()) {
            lv2.set(lv.getX(), lv.getY(), lv.getZ());
            int n = colorResolver.getColor(this.getBiome(lv2), lv2.getX(), lv2.getZ());
            k += (n & 0xFF0000) >> 16;
            l += (n & 0xFF00) >> 8;
            m += n & 0xFF;
        }
        return (k / j & 0xFF) << 16 | (l / j & 0xFF) << 8 | m / j & 0xFF;
    }

    public BlockPos getSpawnPos() {
        BlockPos lv = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
        if (!this.getWorldBorder().contains(lv)) {
            lv = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return lv;
    }

    public void setSpawnPos(BlockPos arg) {
        this.properties.setSpawnPos(arg);
    }

    public String toString() {
        return "ClientLevel";
    }

    @Override
    public Properties getLevelProperties() {
        return this.clientWorldProperties;
    }

    @Override
    public /* synthetic */ WorldProperties getLevelProperties() {
        return this.getLevelProperties();
    }

    @Override
    public /* synthetic */ ChunkManager getChunkManager() {
        return this.getChunkManager();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Properties
    implements MutableWorldProperties {
        private final boolean hardcore;
        private final GameRules gameRules;
        private final boolean flatWorld;
        private int spawnX;
        private int spawnY;
        private int spawnZ;
        private long time;
        private long timeOfDay;
        private boolean raining;
        private Difficulty difficulty;
        private boolean difficultyLocked;

        public Properties(Difficulty arg, boolean bl, boolean bl2) {
            this.difficulty = arg;
            this.hardcore = bl;
            this.flatWorld = bl2;
            this.gameRules = new GameRules();
        }

        @Override
        public int getSpawnX() {
            return this.spawnX;
        }

        @Override
        public int getSpawnY() {
            return this.spawnY;
        }

        @Override
        public int getSpawnZ() {
            return this.spawnZ;
        }

        @Override
        public long getTime() {
            return this.time;
        }

        @Override
        public long getTimeOfDay() {
            return this.timeOfDay;
        }

        @Override
        public void setSpawnX(int i) {
            this.spawnX = i;
        }

        @Override
        public void setSpawnY(int i) {
            this.spawnY = i;
        }

        @Override
        public void setSpawnZ(int i) {
            this.spawnZ = i;
        }

        public void setTime(long l) {
            this.time = l;
        }

        public void setTimeOfDay(long l) {
            this.timeOfDay = l;
        }

        @Override
        public void setSpawnPos(BlockPos arg) {
            this.spawnX = arg.getX();
            this.spawnY = arg.getY();
            this.spawnZ = arg.getZ();
        }

        @Override
        public boolean isThundering() {
            return false;
        }

        @Override
        public boolean isRaining() {
            return this.raining;
        }

        @Override
        public void setRaining(boolean bl) {
            this.raining = bl;
        }

        @Override
        public boolean isHardcore() {
            return this.hardcore;
        }

        @Override
        public GameRules getGameRules() {
            return this.gameRules;
        }

        @Override
        public Difficulty getDifficulty() {
            return this.difficulty;
        }

        @Override
        public boolean isDifficultyLocked() {
            return this.difficultyLocked;
        }

        @Override
        public void populateCrashReport(CrashReportSection arg) {
            MutableWorldProperties.super.populateCrashReport(arg);
        }

        public void setDifficulty(Difficulty arg) {
            this.difficulty = arg;
        }

        public void setDifficultyLocked(boolean bl) {
            this.difficultyLocked = bl;
        }

        public double getSkyDarknessHeight() {
            if (this.flatWorld) {
                return 0.0;
            }
            return 63.0;
        }

        public double getHorizonShadingRatio() {
            if (this.flatWorld) {
                return 1.0;
            }
            return 0.03125;
        }
    }
}

