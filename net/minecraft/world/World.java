/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World
implements WorldAccess,
AutoCloseable {
    protected static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<RegistryKey<World>> CODEC = Identifier.CODEC.xmap(RegistryKey.createKeyFactory(Registry.DIMENSION), RegistryKey::getValue);
    public static final RegistryKey<World> OVERWORLD = RegistryKey.of(Registry.DIMENSION, new Identifier("overworld"));
    public static final RegistryKey<World> NETHER = RegistryKey.of(Registry.DIMENSION, new Identifier("the_nether"));
    public static final RegistryKey<World> END = RegistryKey.of(Registry.DIMENSION, new Identifier("the_end"));
    private static final Direction[] DIRECTIONS = Direction.values();
    public final List<BlockEntity> blockEntities = Lists.newArrayList();
    public final List<BlockEntity> tickingBlockEntities = Lists.newArrayList();
    protected final List<BlockEntity> pendingBlockEntities = Lists.newArrayList();
    protected final List<BlockEntity> unloadedBlockEntities = Lists.newArrayList();
    private final Thread thread;
    private final boolean debugWorld;
    private int ambientDarkness;
    protected int lcgBlockSeed = new Random().nextInt();
    protected final int unusedIncrement = 1013904223;
    protected float rainGradientPrev;
    protected float rainGradient;
    protected float thunderGradientPrev;
    protected float thunderGradient;
    public final Random random = new Random();
    private final DimensionType dimension;
    protected final MutableWorldProperties properties;
    private final Supplier<Profiler> profiler;
    public final boolean isClient;
    protected boolean iteratingTickingBlockEntities;
    private final WorldBorder border;
    private final BiomeAccess biomeAccess;
    private final RegistryKey<World> registryKey;
    private final RegistryKey<DimensionType> dimensionRegistryKey;

    protected World(MutableWorldProperties properties, RegistryKey<World> registryKey, RegistryKey<DimensionType> dimensionRegistryKey, DimensionType dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        this.profiler = profiler;
        this.properties = properties;
        this.dimension = dimension;
        this.registryKey = registryKey;
        this.dimensionRegistryKey = dimensionRegistryKey;
        this.isClient = isClient;
        this.border = dimension.isShrunk() ? new WorldBorder(){

            @Override
            public double getCenterX() {
                return super.getCenterX() / 8.0;
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / 8.0;
            }
        } : new WorldBorder();
        this.thread = Thread.currentThread();
        this.biomeAccess = new BiomeAccess(this, seed, dimension.getBiomeAccessType());
        this.debugWorld = debugWorld;
    }

    @Override
    public boolean isClient() {
        return this.isClient;
    }

    @Nullable
    public MinecraftServer getServer() {
        return null;
    }

    public static boolean method_24794(BlockPos arg) {
        return !World.isHeightInvalid(arg) && World.isValid(arg);
    }

    public static boolean method_25953(BlockPos arg) {
        return !World.method_25952(arg.getY()) && World.isValid(arg);
    }

    private static boolean isValid(BlockPos pos) {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
    }

    private static boolean method_25952(int i) {
        return i < -20000000 || i >= 20000000;
    }

    public static boolean isHeightInvalid(BlockPos pos) {
        return World.isHeightInvalid(pos.getY());
    }

    public static boolean isHeightInvalid(int y) {
        return y < 0 || y >= 256;
    }

    public WorldChunk getWorldChunk(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public WorldChunk getChunk(int i, int j) {
        return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        Chunk lv = this.getChunkManager().getChunk(chunkX, chunkZ, leastStatus, create);
        if (lv == null && create) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return lv;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        return this.setBlockState(pos, state, flags, 512);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        if (World.isHeightInvalid(pos)) {
            return false;
        }
        if (!this.isClient && this.isDebugWorld()) {
            return false;
        }
        WorldChunk lv = this.getWorldChunk(pos);
        Block lv2 = state.getBlock();
        BlockState lv3 = lv.setBlockState(pos, state, (flags & 0x40) != 0);
        if (lv3 != null) {
            BlockState lv4 = this.getBlockState(pos);
            if (lv4 != lv3 && (lv4.getOpacity(this, pos) != lv3.getOpacity(this, pos) || lv4.getLuminance() != lv3.getLuminance() || lv4.hasSidedTransparency() || lv3.hasSidedTransparency())) {
                this.getProfiler().push("queueCheckLight");
                this.getChunkManager().getLightingProvider().checkBlock(pos);
                this.getProfiler().pop();
            }
            if (lv4 == state) {
                if (lv3 != lv4) {
                    this.scheduleBlockRerenderIfNeeded(pos, lv3, lv4);
                }
                if ((flags & 2) != 0 && (!this.isClient || (flags & 4) == 0) && (this.isClient || lv.getLevelType() != null && lv.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
                    this.updateListeners(pos, lv3, state, flags);
                }
                if ((flags & 1) != 0) {
                    this.updateNeighbors(pos, lv3.getBlock());
                    if (!this.isClient && state.hasComparatorOutput()) {
                        this.updateComparators(pos, lv2);
                    }
                }
                if ((flags & 0x10) == 0 && maxUpdateDepth > 0) {
                    int k = flags & 0xFFFFFFDE;
                    lv3.prepare(this, pos, k, maxUpdateDepth - 1);
                    state.updateNeighbors(this, pos, k, maxUpdateDepth - 1);
                    state.prepare(this, pos, k, maxUpdateDepth - 1);
                }
                this.onBlockChanged(pos, lv3, lv4);
            }
            return true;
        }
        return false;
    }

    public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        FluidState lv = this.getFluidState(pos);
        return this.setBlockState(pos, lv.getBlockState(), 3 | (move ? 64 : 0));
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        BlockState lv = this.getBlockState(pos);
        if (lv.isAir()) {
            return false;
        }
        FluidState lv2 = this.getFluidState(pos);
        if (!(lv.getBlock() instanceof AbstractFireBlock)) {
            this.syncWorldEvent(2001, pos, Block.getRawIdFromState(lv));
        }
        if (drop) {
            BlockEntity lv3 = lv.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
            Block.dropStacks(lv, this, pos, lv3, breakingEntity, ItemStack.EMPTY);
        }
        return this.setBlockState(pos, lv2.getBlockState(), 3, maxUpdateDepth);
    }

    public boolean setBlockState(BlockPos pos, BlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    public abstract void updateListeners(BlockPos var1, BlockState var2, BlockState var3, int var4);

    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
    }

    public void updateNeighborsAlways(BlockPos pos, Block block) {
        this.updateNeighbor(pos.west(), block, pos);
        this.updateNeighbor(pos.east(), block, pos);
        this.updateNeighbor(pos.down(), block, pos);
        this.updateNeighbor(pos.up(), block, pos);
        this.updateNeighbor(pos.north(), block, pos);
        this.updateNeighbor(pos.south(), block, pos);
    }

    public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction direction) {
        if (direction != Direction.WEST) {
            this.updateNeighbor(pos.west(), sourceBlock, pos);
        }
        if (direction != Direction.EAST) {
            this.updateNeighbor(pos.east(), sourceBlock, pos);
        }
        if (direction != Direction.DOWN) {
            this.updateNeighbor(pos.down(), sourceBlock, pos);
        }
        if (direction != Direction.UP) {
            this.updateNeighbor(pos.up(), sourceBlock, pos);
        }
        if (direction != Direction.NORTH) {
            this.updateNeighbor(pos.north(), sourceBlock, pos);
        }
        if (direction != Direction.SOUTH) {
            this.updateNeighbor(pos.south(), sourceBlock, pos);
        }
    }

    public void updateNeighbor(BlockPos sourcePos, Block sourceBlock, BlockPos neighborPos) {
        if (this.isClient) {
            return;
        }
        BlockState lv = this.getBlockState(sourcePos);
        try {
            lv.neighborUpdate(this, sourcePos, sourceBlock, neighborPos, false);
        }
        catch (Throwable throwable) {
            CrashReport lv2 = CrashReport.create(throwable, "Exception while updating neighbours");
            CrashReportSection lv3 = lv2.addElement("Block being updated");
            lv3.add("Source block type", () -> {
                try {
                    return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
                }
                catch (Throwable throwable) {
                    return "ID #" + Registry.BLOCK.getId(sourceBlock);
                }
            });
            CrashReportSection.addBlockInfo(lv3, sourcePos, lv);
            throw new CrashException(lv2);
        }
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        boolean m;
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) {
            int k = this.getSeaLevel() + 1;
        } else if (this.isChunkLoaded(x >> 4, z >> 4)) {
            int l = this.getChunk(x >> 4, z >> 4).sampleHeightmap(heightmap, x & 0xF, z & 0xF) + 1;
        } else {
            m = false;
        }
        return (int)m;
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.getChunkManager().getLightingProvider();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (World.isHeightInvalid(pos)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        WorldChunk lv = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        return lv.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (World.isHeightInvalid(pos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        WorldChunk lv = this.getWorldChunk(pos);
        return lv.getFluidState(pos);
    }

    public boolean isDay() {
        return !this.getDimension().hasFixedTime() && this.ambientDarkness < 4;
    }

    public boolean isNight() {
        return !this.getDimension().hasFixedTime() && !this.isDay();
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.playSound(player, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch);
    }

    public abstract void playSound(@Nullable PlayerEntity var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9, float var10, float var11);

    public abstract void playSoundFromEntity(@Nullable PlayerEntity var1, Entity var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean bl) {
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Environment(value=EnvType.CLIENT)
    public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public void addImportantParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public float getSkyAngleRadians(float tickDelta) {
        float g = this.method_30274(tickDelta);
        return g * ((float)Math.PI * 2);
    }

    public boolean addBlockEntity(BlockEntity blockEntity) {
        boolean bl;
        if (this.iteratingTickingBlockEntities) {
            org.apache.logging.log4j.util.Supplier[] arrsupplier = new org.apache.logging.log4j.util.Supplier[2];
            arrsupplier[0] = () -> Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType());
            arrsupplier[1] = blockEntity::getPos;
            LOGGER.error("Adding block entity while ticking: {} @ {}", arrsupplier);
        }
        if ((bl = this.blockEntities.add(blockEntity)) && blockEntity instanceof Tickable) {
            this.tickingBlockEntities.add(blockEntity);
        }
        if (this.isClient) {
            BlockPos lv = blockEntity.getPos();
            BlockState lv2 = this.getBlockState(lv);
            this.updateListeners(lv, lv2, lv2, 2);
        }
        return bl;
    }

    public void addBlockEntities(Collection<BlockEntity> blockEntities) {
        if (this.iteratingTickingBlockEntities) {
            this.pendingBlockEntities.addAll(blockEntities);
        } else {
            for (BlockEntity lv : blockEntities) {
                this.addBlockEntity(lv);
            }
        }
    }

    public void tickBlockEntities() {
        Profiler lv = this.getProfiler();
        lv.push("blockEntities");
        if (!this.unloadedBlockEntities.isEmpty()) {
            this.tickingBlockEntities.removeAll(this.unloadedBlockEntities);
            this.blockEntities.removeAll(this.unloadedBlockEntities);
            this.unloadedBlockEntities.clear();
        }
        this.iteratingTickingBlockEntities = true;
        Iterator<BlockEntity> iterator = this.tickingBlockEntities.iterator();
        while (iterator.hasNext()) {
            BlockEntity lv2 = iterator.next();
            if (!lv2.isRemoved() && lv2.hasWorld()) {
                BlockPos lv3 = lv2.getPos();
                if (this.getChunkManager().shouldTickBlock(lv3) && this.getWorldBorder().contains(lv3)) {
                    try {
                        lv.push(() -> String.valueOf(BlockEntityType.getId(lv2.getType())));
                        if (lv2.getType().supports(this.getBlockState(lv3).getBlock())) {
                            ((Tickable)((Object)lv2)).tick();
                        } else {
                            lv2.markInvalid();
                        }
                        lv.pop();
                    }
                    catch (Throwable throwable) {
                        CrashReport lv4 = CrashReport.create(throwable, "Ticking block entity");
                        CrashReportSection lv5 = lv4.addElement("Block entity being ticked");
                        lv2.populateCrashReport(lv5);
                        throw new CrashException(lv4);
                    }
                }
            }
            if (!lv2.isRemoved()) continue;
            iterator.remove();
            this.blockEntities.remove(lv2);
            if (!this.isChunkLoaded(lv2.getPos())) continue;
            this.getWorldChunk(lv2.getPos()).removeBlockEntity(lv2.getPos());
        }
        this.iteratingTickingBlockEntities = false;
        lv.swap("pendingBlockEntities");
        if (!this.pendingBlockEntities.isEmpty()) {
            for (int i = 0; i < this.pendingBlockEntities.size(); ++i) {
                BlockEntity lv6 = this.pendingBlockEntities.get(i);
                if (lv6.isRemoved()) continue;
                if (!this.blockEntities.contains(lv6)) {
                    this.addBlockEntity(lv6);
                }
                if (!this.isChunkLoaded(lv6.getPos())) continue;
                WorldChunk lv7 = this.getWorldChunk(lv6.getPos());
                BlockState lv8 = lv7.getBlockState(lv6.getPos());
                lv7.setBlockEntity(lv6.getPos(), lv6);
                this.updateListeners(lv6.getPos(), lv8, lv8, 3);
            }
            this.pendingBlockEntities.clear();
        }
        lv.pop();
    }

    public void tickEntity(Consumer<Entity> tickConsumer, Entity entity) {
        try {
            tickConsumer.accept(entity);
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Ticking entity");
            CrashReportSection lv2 = lv.addElement("Entity being ticked");
            entity.populateCrashReport(lv2);
            throw new CrashException(lv);
        }
    }

    public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, Explosion.DestructionType destructionType) {
        return this.createExplosion(entity, null, null, x, y, z, power, false, destructionType);
    }

    public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType) {
        return this.createExplosion(entity, null, null, x, y, z, power, createFire, destructionType);
    }

    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior arg3, double d, double e, double f, float g, boolean bl, Explosion.DestructionType arg4) {
        Explosion lv = new Explosion(this, entity, damageSource, arg3, d, e, f, g, bl, arg4);
        lv.collectBlocksAndDamageEntities();
        lv.affectWorld(true);
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public String getDebugString() {
        return this.getChunkManager().getDebugString();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        if (World.isHeightInvalid(pos)) {
            return null;
        }
        if (!this.isClient && Thread.currentThread() != this.thread) {
            return null;
        }
        BlockEntity lv = null;
        if (this.iteratingTickingBlockEntities) {
            lv = this.getPendingBlockEntity(pos);
        }
        if (lv == null) {
            lv = this.getWorldChunk(pos).getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
        }
        if (lv == null) {
            lv = this.getPendingBlockEntity(pos);
        }
        return lv;
    }

    @Nullable
    private BlockEntity getPendingBlockEntity(BlockPos pos) {
        for (int i = 0; i < this.pendingBlockEntities.size(); ++i) {
            BlockEntity lv = this.pendingBlockEntities.get(i);
            if (lv.isRemoved() || !lv.getPos().equals(pos)) continue;
            return lv;
        }
        return null;
    }

    public void setBlockEntity(BlockPos pos, @Nullable BlockEntity blockEntity) {
        if (World.isHeightInvalid(pos)) {
            return;
        }
        if (blockEntity != null && !blockEntity.isRemoved()) {
            if (this.iteratingTickingBlockEntities) {
                blockEntity.setLocation(this, pos);
                Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();
                while (iterator.hasNext()) {
                    BlockEntity lv = iterator.next();
                    if (!lv.getPos().equals(pos)) continue;
                    lv.markRemoved();
                    iterator.remove();
                }
                this.pendingBlockEntities.add(blockEntity);
            } else {
                this.getWorldChunk(pos).setBlockEntity(pos, blockEntity);
                this.addBlockEntity(blockEntity);
            }
        }
    }

    public void removeBlockEntity(BlockPos pos) {
        BlockEntity lv = this.getBlockEntity(pos);
        if (lv != null && this.iteratingTickingBlockEntities) {
            lv.markRemoved();
            this.pendingBlockEntities.remove(lv);
        } else {
            if (lv != null) {
                this.pendingBlockEntities.remove(lv);
                this.blockEntities.remove(lv);
                this.tickingBlockEntities.remove(lv);
            }
            this.getWorldChunk(pos).removeBlockEntity(pos);
        }
    }

    public boolean canSetBlock(BlockPos pos) {
        if (World.isHeightInvalid(pos)) {
            return false;
        }
        return this.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction) {
        if (World.isHeightInvalid(pos)) {
            return false;
        }
        Chunk lv = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if (lv == null) {
            return false;
        }
        return lv.getBlockState(pos).hasSolidTopSurface(this, pos, entity, direction);
    }

    public boolean isTopSolid(BlockPos pos, Entity entity) {
        return this.isDirectionSolid(pos, entity, Direction.UP);
    }

    public void calculateAmbientDarkness() {
        double d = 1.0 - (double)(this.getRainGradient(1.0f) * 5.0f) / 16.0;
        double e = 1.0 - (double)(this.getThunderGradient(1.0f) * 5.0f) / 16.0;
        double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.method_30274(1.0f) * ((float)Math.PI * 2)), -0.25, 0.25);
        this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
    }

    public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
        this.getChunkManager().setMobSpawnOptions(spawnMonsters, spawnAnimals);
    }

    protected void initWeatherGradients() {
        if (this.properties.isRaining()) {
            this.rainGradient = 1.0f;
            if (this.properties.isThundering()) {
                this.thunderGradient = 1.0f;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.getChunkManager().close();
    }

    @Override
    @Nullable
    public BlockView getExistingChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
    }

    @Override
    public List<Entity> getOtherEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
        this.getProfiler().visit("getEntities");
        ArrayList list = Lists.newArrayList();
        int i = MathHelper.floor((box.minX - 2.0) / 16.0);
        int j = MathHelper.floor((box.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
        int l = MathHelper.floor((box.maxZ + 2.0) / 16.0);
        ChunkManager lv = this.getChunkManager();
        for (int m = i; m <= j; ++m) {
            for (int n = k; n <= l; ++n) {
                WorldChunk lv2 = lv.getWorldChunk(m, n, false);
                if (lv2 == null) continue;
                lv2.collectOtherEntities(except, box, list, predicate);
            }
        }
        return list;
    }

    public <T extends Entity> List<T> getEntitiesByType(@Nullable EntityType<T> type, Box box, Predicate<? super T> predicate) {
        this.getProfiler().visit("getEntities");
        int i = MathHelper.floor((box.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk lv = this.getChunkManager().getWorldChunk(m, n, false);
                if (lv == null) continue;
                lv.collectEntities(type, box, list, predicate);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByClass(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        this.getProfiler().visit("getEntities");
        int i = MathHelper.floor((box.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        ChunkManager lv = this.getChunkManager();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk lv2 = lv.getWorldChunk(m, n, false);
                if (lv2 == null) continue;
                lv2.collectEntitiesByClass(entityClass, box, list, predicate);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        this.getProfiler().visit("getLoadedEntities");
        int i = MathHelper.floor((box.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        ChunkManager lv = this.getChunkManager();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk lv2 = lv.getWorldChunk(m, n);
                if (lv2 == null) continue;
                lv2.collectEntitiesByClass(entityClass, box, list, predicate);
            }
        }
        return list;
    }

    @Nullable
    public abstract Entity getEntityById(int var1);

    public void markDirty(BlockPos pos, BlockEntity blockEntity) {
        if (this.isChunkLoaded(pos)) {
            this.getWorldChunk(pos).markDirty();
        }
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    public int getReceivedStrongRedstonePower(BlockPos pos) {
        int i = 0;
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.down(), Direction.DOWN))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.up(), Direction.UP))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.north(), Direction.NORTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.south(), Direction.SOUTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.west(), Direction.WEST))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.east(), Direction.EAST))) >= 15) {
            return i;
        }
        return i;
    }

    public boolean isEmittingRedstonePower(BlockPos pos, Direction direction) {
        return this.getEmittedRedstonePower(pos, direction) > 0;
    }

    public int getEmittedRedstonePower(BlockPos pos, Direction direction) {
        BlockState lv = this.getBlockState(pos);
        int i = lv.getWeakRedstonePower(this, pos, direction);
        if (lv.isSolidBlock(this, pos)) {
            return Math.max(i, this.getReceivedStrongRedstonePower(pos));
        }
        return i;
    }

    public boolean isReceivingRedstonePower(BlockPos pos) {
        if (this.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.up(), Direction.UP) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.north(), Direction.NORTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.south(), Direction.SOUTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.west(), Direction.WEST) > 0) {
            return true;
        }
        return this.getEmittedRedstonePower(pos.east(), Direction.EAST) > 0;
    }

    public int getReceivedRedstonePower(BlockPos pos) {
        int i = 0;
        for (Direction lv : DIRECTIONS) {
            int j = this.getEmittedRedstonePower(pos.offset(lv), lv);
            if (j >= 15) {
                return 15;
            }
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public void disconnect() {
    }

    public long getTime() {
        return this.properties.getTime();
    }

    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
        return true;
    }

    public void sendEntityStatus(Entity entity, byte status) {
    }

    public void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data) {
        this.getBlockState(pos).onSyncedBlockEvent(this, pos, type, data);
    }

    @Override
    public WorldProperties getLevelProperties() {
        return this.properties;
    }

    public GameRules getGameRules() {
        return this.properties.getGameRules();
    }

    public float getThunderGradient(float delta) {
        return MathHelper.lerp(delta, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(delta);
    }

    @Environment(value=EnvType.CLIENT)
    public void setThunderGradient(float thunderGradient) {
        this.thunderGradientPrev = thunderGradient;
        this.thunderGradient = thunderGradient;
    }

    public float getRainGradient(float delta) {
        return MathHelper.lerp(delta, this.rainGradientPrev, this.rainGradient);
    }

    @Environment(value=EnvType.CLIENT)
    public void setRainGradient(float rainGradient) {
        this.rainGradientPrev = rainGradient;
        this.rainGradient = rainGradient;
    }

    public boolean isThundering() {
        if (!this.getDimension().hasSkyLight() || this.getDimension().hasCeiling()) {
            return false;
        }
        return (double)this.getThunderGradient(1.0f) > 0.9;
    }

    public boolean isRaining() {
        return (double)this.getRainGradient(1.0f) > 0.2;
    }

    public boolean hasRain(BlockPos pos) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.isSkyVisible(pos)) {
            return false;
        }
        if (this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        Biome lv = this.getBiome(pos);
        return lv.getPrecipitation() == Biome.Precipitation.RAIN && lv.getTemperature(pos) >= 0.15f;
    }

    public boolean hasHighHumidity(BlockPos pos) {
        Biome lv = this.getBiome(pos);
        return lv.hasHighHumidity();
    }

    @Nullable
    public abstract MapState getMapState(String var1);

    public abstract void putMapState(MapState var1);

    public abstract int getNextMapId();

    public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
    }

    public CrashReportSection addDetailsToCrashReport(CrashReport report) {
        CrashReportSection lv = report.addElement("Affected level", 1);
        lv.add("All players", () -> this.getPlayers().size() + " total; " + this.getPlayers());
        lv.add("Chunk stats", this.getChunkManager()::getDebugString);
        lv.add("Level dimension", () -> this.getRegistryKey().getValue().toString());
        try {
            this.properties.populateCrashReport(lv);
        }
        catch (Throwable throwable) {
            lv.add("Level Data Unobtainable", throwable);
        }
        return lv;
    }

    public abstract void setBlockBreakingInfo(int var1, BlockPos var2, int var3);

    @Environment(value=EnvType.CLIENT)
    public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable CompoundTag tag) {
    }

    public abstract Scoreboard getScoreboard();

    public void updateComparators(BlockPos pos, Block block) {
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BlockPos lv2 = pos.offset(lv);
            if (!this.isChunkLoaded(lv2)) continue;
            BlockState lv3 = this.getBlockState(lv2);
            if (lv3.isOf(Blocks.COMPARATOR)) {
                lv3.neighborUpdate(this, lv2, block, pos, false);
                continue;
            }
            if (!lv3.isSolidBlock(this, lv2) || !(lv3 = this.getBlockState(lv2 = lv2.offset(lv))).isOf(Blocks.COMPARATOR)) continue;
            lv3.neighborUpdate(this, lv2, block, pos, false);
        }
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        long l = 0L;
        float f = 0.0f;
        if (this.isChunkLoaded(pos)) {
            f = this.method_30272();
            l = this.getWorldChunk(pos).getInhabitedTime();
        }
        return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
    }

    @Override
    public int getAmbientDarkness() {
        return this.ambientDarkness;
    }

    public void setLightningTicksLeft(int lightningTicksLeft) {
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.border;
    }

    public void sendPacket(Packet<?> packet) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    @Override
    public DimensionType getDimension() {
        return this.dimension;
    }

    public RegistryKey<DimensionType> getDimensionRegistryKey() {
        return this.dimensionRegistryKey;
    }

    public RegistryKey<World> getRegistryKey() {
        return this.registryKey;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        return state.test(this.getBlockState(pos));
    }

    public abstract RecipeManager getRecipeManager();

    public abstract TagManager getTagManager();

    public BlockPos getRandomPosInChunk(int x, int y, int z, int l) {
        this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
        int m = this.lcgBlockSeed >> 2;
        return new BlockPos(x + (m & 0xF), y + (m >> 16 & l), z + (m >> 8 & 0xF));
    }

    public boolean isSavingDisabled() {
        return false;
    }

    public Profiler getProfiler() {
        return this.profiler.get();
    }

    public Supplier<Profiler> getProfilerSupplier() {
        return this.profiler;
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return this.biomeAccess;
    }

    public final boolean isDebugWorld() {
        return this.debugWorld;
    }

    public abstract DynamicRegistryManager getRegistryManager();

    @Override
    public /* synthetic */ Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ);
    }
}

