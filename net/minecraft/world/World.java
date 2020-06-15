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
import net.minecraft.tag.RegistryTagManager;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
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

    protected World(MutableWorldProperties arg, RegistryKey<World> arg2, RegistryKey<DimensionType> arg3, DimensionType arg4, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l) {
        this.profiler = supplier;
        this.properties = arg;
        this.dimension = arg4;
        this.registryKey = arg2;
        this.dimensionRegistryKey = arg3;
        this.isClient = bl;
        this.border = arg4.isShrunk() ? new WorldBorder(){

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
        this.biomeAccess = new BiomeAccess(this, l, arg4.getBiomeAccessType());
        this.debugWorld = bl2;
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

    private static boolean isValid(BlockPos arg) {
        return arg.getX() >= -30000000 && arg.getZ() >= -30000000 && arg.getX() < 30000000 && arg.getZ() < 30000000;
    }

    private static boolean method_25952(int i) {
        return i < -20000000 || i >= 20000000;
    }

    public static boolean isHeightInvalid(BlockPos arg) {
        return World.isHeightInvalid(arg.getY());
    }

    public static boolean isHeightInvalid(int i) {
        return i < 0 || i >= 256;
    }

    public double getCollisionHeightAt(BlockPos arg2) {
        return this.getCollisionHeightAt(arg2, arg -> false);
    }

    public double getCollisionHeightAt(BlockPos arg, Predicate<BlockState> predicate) {
        VoxelShape lv2;
        BlockState lv = this.getBlockState(arg);
        VoxelShape voxelShape = lv2 = predicate.test(lv) ? VoxelShapes.empty() : lv.getCollisionShape(this, arg);
        if (lv2.isEmpty()) {
            BlockPos lv3 = arg.down();
            BlockState lv4 = this.getBlockState(lv3);
            VoxelShape lv5 = predicate.test(lv4) ? VoxelShapes.empty() : lv4.getCollisionShape(this, lv3);
            double d = lv5.getMax(Direction.Axis.Y);
            if (d >= 1.0) {
                return d - 1.0;
            }
            return Double.NEGATIVE_INFINITY;
        }
        return lv2.getMax(Direction.Axis.Y);
    }

    public double method_26096(BlockPos arg, double d) {
        BlockPos.Mutable lv = arg.mutableCopy();
        int i = MathHelper.ceil(d);
        for (int j = 0; j < i; ++j) {
            VoxelShape lv2 = this.getBlockState(lv).getCollisionShape(this, lv);
            if (!lv2.isEmpty()) {
                return (double)j + lv2.getMin(Direction.Axis.Y);
            }
            lv.move(Direction.UP);
        }
        return Double.POSITIVE_INFINITY;
    }

    public WorldChunk getWorldChunk(BlockPos arg) {
        return this.getChunk(arg.getX() >> 4, arg.getZ() >> 4);
    }

    @Override
    public WorldChunk getChunk(int i, int j) {
        return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
    }

    @Override
    public Chunk getChunk(int i, int j, ChunkStatus arg, boolean bl) {
        Chunk lv = this.getChunkManager().getChunk(i, j, arg, bl);
        if (lv == null && bl) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return lv;
    }

    @Override
    public boolean setBlockState(BlockPos arg, BlockState arg2, int i) {
        return this.method_30092(arg, arg2, i, 512);
    }

    @Override
    public boolean method_30092(BlockPos arg, BlockState arg2, int i, int j) {
        if (World.isHeightInvalid(arg)) {
            return false;
        }
        if (!this.isClient && this.isDebugWorld()) {
            return false;
        }
        WorldChunk lv = this.getWorldChunk(arg);
        Block lv2 = arg2.getBlock();
        BlockState lv3 = lv.setBlockState(arg, arg2, (i & 0x40) != 0);
        if (lv3 != null) {
            BlockState lv4 = this.getBlockState(arg);
            if (lv4 != lv3 && (lv4.getOpacity(this, arg) != lv3.getOpacity(this, arg) || lv4.getLuminance() != lv3.getLuminance() || lv4.hasSidedTransparency() || lv3.hasSidedTransparency())) {
                this.getProfiler().push("queueCheckLight");
                this.getChunkManager().getLightingProvider().checkBlock(arg);
                this.getProfiler().pop();
            }
            if (lv4 == arg2) {
                if (lv3 != lv4) {
                    this.scheduleBlockRerenderIfNeeded(arg, lv3, lv4);
                }
                if ((i & 2) != 0 && (!this.isClient || (i & 4) == 0) && (this.isClient || lv.getLevelType() != null && lv.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
                    this.updateListeners(arg, lv3, arg2, i);
                }
                if ((i & 1) != 0) {
                    this.updateNeighbors(arg, lv3.getBlock());
                    if (!this.isClient && arg2.hasComparatorOutput()) {
                        this.updateComparators(arg, lv2);
                    }
                }
                if ((i & 0x10) == 0 && j > 0) {
                    int k = i & 0xFFFFFFDE;
                    lv3.prepare(this, arg, k, j - 1);
                    arg2.updateNeighbors(this, arg, k, j - 1);
                    arg2.prepare(this, arg, k, j - 1);
                }
                this.onBlockChanged(arg, lv3, lv4);
            }
            return true;
        }
        return false;
    }

    public void onBlockChanged(BlockPos arg, BlockState arg2, BlockState arg3) {
    }

    @Override
    public boolean removeBlock(BlockPos arg, boolean bl) {
        FluidState lv = this.getFluidState(arg);
        return this.setBlockState(arg, lv.getBlockState(), 3 | (bl ? 64 : 0));
    }

    @Override
    public boolean method_30093(BlockPos arg, boolean bl, @Nullable Entity arg2, int i) {
        BlockState lv = this.getBlockState(arg);
        if (lv.isAir()) {
            return false;
        }
        FluidState lv2 = this.getFluidState(arg);
        if (!(lv.getBlock() instanceof AbstractFireBlock)) {
            this.syncWorldEvent(2001, arg, Block.getRawIdFromState(lv));
        }
        if (bl) {
            BlockEntity lv3 = lv.getBlock().hasBlockEntity() ? this.getBlockEntity(arg) : null;
            Block.dropStacks(lv, this, arg, lv3, arg2, ItemStack.EMPTY);
        }
        return this.method_30092(arg, lv2.getBlockState(), 3, i);
    }

    public boolean setBlockState(BlockPos arg, BlockState arg2) {
        return this.setBlockState(arg, arg2, 3);
    }

    public abstract void updateListeners(BlockPos var1, BlockState var2, BlockState var3, int var4);

    public void scheduleBlockRerenderIfNeeded(BlockPos arg, BlockState arg2, BlockState arg3) {
    }

    public void updateNeighborsAlways(BlockPos arg, Block arg2) {
        this.updateNeighbor(arg.west(), arg2, arg);
        this.updateNeighbor(arg.east(), arg2, arg);
        this.updateNeighbor(arg.down(), arg2, arg);
        this.updateNeighbor(arg.up(), arg2, arg);
        this.updateNeighbor(arg.north(), arg2, arg);
        this.updateNeighbor(arg.south(), arg2, arg);
    }

    public void updateNeighborsExcept(BlockPos arg, Block arg2, Direction arg3) {
        if (arg3 != Direction.WEST) {
            this.updateNeighbor(arg.west(), arg2, arg);
        }
        if (arg3 != Direction.EAST) {
            this.updateNeighbor(arg.east(), arg2, arg);
        }
        if (arg3 != Direction.DOWN) {
            this.updateNeighbor(arg.down(), arg2, arg);
        }
        if (arg3 != Direction.UP) {
            this.updateNeighbor(arg.up(), arg2, arg);
        }
        if (arg3 != Direction.NORTH) {
            this.updateNeighbor(arg.north(), arg2, arg);
        }
        if (arg3 != Direction.SOUTH) {
            this.updateNeighbor(arg.south(), arg2, arg);
        }
    }

    public void updateNeighbor(BlockPos arg, Block arg2, BlockPos arg3) {
        if (this.isClient) {
            return;
        }
        BlockState lv = this.getBlockState(arg);
        try {
            lv.neighborUpdate(this, arg, arg2, arg3, false);
        }
        catch (Throwable throwable) {
            CrashReport lv2 = CrashReport.create(throwable, "Exception while updating neighbours");
            CrashReportSection lv3 = lv2.addElement("Block being updated");
            lv3.add("Source block type", () -> {
                try {
                    return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(arg2), arg2.getTranslationKey(), arg2.getClass().getCanonicalName());
                }
                catch (Throwable throwable) {
                    return "ID #" + Registry.BLOCK.getId(arg2);
                }
            });
            CrashReportSection.addBlockInfo(lv3, arg, lv);
            throw new CrashException(lv2);
        }
    }

    @Override
    public int getTopY(Heightmap.Type arg, int i, int j) {
        boolean m;
        if (i < -30000000 || j < -30000000 || i >= 30000000 || j >= 30000000) {
            int k = this.getSeaLevel() + 1;
        } else if (this.isChunkLoaded(i >> 4, j >> 4)) {
            int l = this.getChunk(i >> 4, j >> 4).sampleHeightmap(arg, i & 0xF, j & 0xF) + 1;
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
    public BlockState getBlockState(BlockPos arg) {
        if (World.isHeightInvalid(arg)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        WorldChunk lv = this.getChunk(arg.getX() >> 4, arg.getZ() >> 4);
        return lv.getBlockState(arg);
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        if (World.isHeightInvalid(arg)) {
            return Fluids.EMPTY.getDefaultState();
        }
        WorldChunk lv = this.getWorldChunk(arg);
        return lv.getFluidState(arg);
    }

    public boolean isDay() {
        return !this.getDimension().hasFixedTime() && this.ambientDarkness < 4;
    }

    public boolean isNight() {
        return !this.getDimension().hasFixedTime() && !this.isDay();
    }

    @Override
    public void playSound(@Nullable PlayerEntity arg, BlockPos arg2, SoundEvent arg3, SoundCategory arg4, float f, float g) {
        this.playSound(arg, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5, arg3, arg4, f, g);
    }

    public abstract void playSound(@Nullable PlayerEntity var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9, float var10, float var11);

    public abstract void playSoundFromEntity(@Nullable PlayerEntity var1, Entity var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void playSound(double d, double e, double f, SoundEvent arg, SoundCategory arg2, float g, float h, boolean bl) {
    }

    @Override
    public void addParticle(ParticleEffect arg, double d, double e, double f, double g, double h, double i) {
    }

    @Environment(value=EnvType.CLIENT)
    public void addParticle(ParticleEffect arg, boolean bl, double d, double e, double f, double g, double h, double i) {
    }

    public void addImportantParticle(ParticleEffect arg, double d, double e, double f, double g, double h, double i) {
    }

    public void addImportantParticle(ParticleEffect arg, boolean bl, double d, double e, double f, double g, double h, double i) {
    }

    public float getSkyAngleRadians(float f) {
        float g = this.getSkyAngle(f);
        return g * ((float)Math.PI * 2);
    }

    public boolean addBlockEntity(BlockEntity arg) {
        boolean bl;
        if (this.iteratingTickingBlockEntities) {
            org.apache.logging.log4j.util.Supplier[] arrsupplier = new org.apache.logging.log4j.util.Supplier[2];
            arrsupplier[0] = () -> Registry.BLOCK_ENTITY_TYPE.getId(arg.getType());
            arrsupplier[1] = arg::getPos;
            LOGGER.error("Adding block entity while ticking: {} @ {}", arrsupplier);
        }
        if ((bl = this.blockEntities.add(arg)) && arg instanceof Tickable) {
            this.tickingBlockEntities.add(arg);
        }
        if (this.isClient) {
            BlockPos lv = arg.getPos();
            BlockState lv2 = this.getBlockState(lv);
            this.updateListeners(lv, lv2, lv2, 2);
        }
        return bl;
    }

    public void addBlockEntities(Collection<BlockEntity> collection) {
        if (this.iteratingTickingBlockEntities) {
            this.pendingBlockEntities.addAll(collection);
        } else {
            for (BlockEntity lv : collection) {
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

    public void tickEntity(Consumer<Entity> consumer, Entity arg) {
        try {
            consumer.accept(arg);
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Ticking entity");
            CrashReportSection lv2 = lv.addElement("Entity being ticked");
            arg.populateCrashReport(lv2);
            throw new CrashException(lv);
        }
    }

    public Explosion createExplosion(@Nullable Entity arg, double d, double e, double f, float g, Explosion.DestructionType arg2) {
        return this.createExplosion(arg, null, null, d, e, f, g, false, arg2);
    }

    public Explosion createExplosion(@Nullable Entity arg, double d, double e, double f, float g, boolean bl, Explosion.DestructionType arg2) {
        return this.createExplosion(arg, null, null, d, e, f, g, bl, arg2);
    }

    public Explosion createExplosion(@Nullable Entity arg, @Nullable DamageSource arg2, @Nullable ExplosionBehavior arg3, double d, double e, double f, float g, boolean bl, Explosion.DestructionType arg4) {
        Explosion lv = new Explosion(this, arg, arg2, arg3, d, e, f, g, bl, arg4);
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
    public BlockEntity getBlockEntity(BlockPos arg) {
        if (World.isHeightInvalid(arg)) {
            return null;
        }
        if (!this.isClient && Thread.currentThread() != this.thread) {
            return null;
        }
        BlockEntity lv = null;
        if (this.iteratingTickingBlockEntities) {
            lv = this.getPendingBlockEntity(arg);
        }
        if (lv == null) {
            lv = this.getWorldChunk(arg).getBlockEntity(arg, WorldChunk.CreationType.IMMEDIATE);
        }
        if (lv == null) {
            lv = this.getPendingBlockEntity(arg);
        }
        return lv;
    }

    @Nullable
    private BlockEntity getPendingBlockEntity(BlockPos arg) {
        for (int i = 0; i < this.pendingBlockEntities.size(); ++i) {
            BlockEntity lv = this.pendingBlockEntities.get(i);
            if (lv.isRemoved() || !lv.getPos().equals(arg)) continue;
            return lv;
        }
        return null;
    }

    public void setBlockEntity(BlockPos arg, @Nullable BlockEntity arg2) {
        if (World.isHeightInvalid(arg)) {
            return;
        }
        if (arg2 != null && !arg2.isRemoved()) {
            if (this.iteratingTickingBlockEntities) {
                arg2.setLocation(this, arg);
                Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();
                while (iterator.hasNext()) {
                    BlockEntity lv = iterator.next();
                    if (!lv.getPos().equals(arg)) continue;
                    lv.markRemoved();
                    iterator.remove();
                }
                this.pendingBlockEntities.add(arg2);
            } else {
                this.getWorldChunk(arg).setBlockEntity(arg, arg2);
                this.addBlockEntity(arg2);
            }
        }
    }

    public void removeBlockEntity(BlockPos arg) {
        BlockEntity lv = this.getBlockEntity(arg);
        if (lv != null && this.iteratingTickingBlockEntities) {
            lv.markRemoved();
            this.pendingBlockEntities.remove(lv);
        } else {
            if (lv != null) {
                this.pendingBlockEntities.remove(lv);
                this.blockEntities.remove(lv);
                this.tickingBlockEntities.remove(lv);
            }
            this.getWorldChunk(arg).removeBlockEntity(arg);
        }
    }

    public boolean canSetBlock(BlockPos arg) {
        if (World.isHeightInvalid(arg)) {
            return false;
        }
        return this.getChunkManager().isChunkLoaded(arg.getX() >> 4, arg.getZ() >> 4);
    }

    public boolean isDirectionSolid(BlockPos arg, Entity arg2, Direction arg3) {
        if (World.isHeightInvalid(arg)) {
            return false;
        }
        Chunk lv = this.getChunk(arg.getX() >> 4, arg.getZ() >> 4, ChunkStatus.FULL, false);
        if (lv == null) {
            return false;
        }
        return lv.getBlockState(arg).hasSolidTopSurface(this, arg, arg2, arg3);
    }

    public boolean isTopSolid(BlockPos arg, Entity arg2) {
        return this.isDirectionSolid(arg, arg2, Direction.UP);
    }

    public void calculateAmbientDarkness() {
        double d = 1.0 - (double)(this.getRainGradient(1.0f) * 5.0f) / 16.0;
        double e = 1.0 - (double)(this.getThunderGradient(1.0f) * 5.0f) / 16.0;
        double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.getSkyAngle(1.0f) * ((float)Math.PI * 2)), -0.25, 0.25);
        this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
    }

    public void setMobSpawnOptions(boolean bl, boolean bl2) {
        this.getChunkManager().setMobSpawnOptions(bl, bl2);
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
    public BlockView getExistingChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, false);
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity arg, Box arg2, @Nullable Predicate<? super Entity> predicate) {
        this.getProfiler().visit("getEntities");
        ArrayList list = Lists.newArrayList();
        int i = MathHelper.floor((arg2.minX - 2.0) / 16.0);
        int j = MathHelper.floor((arg2.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((arg2.minZ - 2.0) / 16.0);
        int l = MathHelper.floor((arg2.maxZ + 2.0) / 16.0);
        ChunkManager lv = this.getChunkManager();
        for (int m = i; m <= j; ++m) {
            for (int n = k; n <= l; ++n) {
                WorldChunk lv2 = lv.getWorldChunk(m, n, false);
                if (lv2 == null) continue;
                lv2.getEntities(arg, arg2, list, predicate);
            }
        }
        return list;
    }

    public <T extends Entity> List<T> getEntities(@Nullable EntityType<T> arg, Box arg2, Predicate<? super T> predicate) {
        this.getProfiler().visit("getEntities");
        int i = MathHelper.floor((arg2.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((arg2.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((arg2.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((arg2.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk lv = this.getChunkManager().getWorldChunk(m, n, false);
                if (lv == null) continue;
                lv.getEntities(arg, arg2, list, predicate);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> class_, Box arg, @Nullable Predicate<? super T> predicate) {
        this.getProfiler().visit("getEntities");
        int i = MathHelper.floor((arg.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((arg.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((arg.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((arg.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        ChunkManager lv = this.getChunkManager();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk lv2 = lv.getWorldChunk(m, n, false);
                if (lv2 == null) continue;
                lv2.getEntities(class_, arg, list, predicate);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> class_, Box arg, @Nullable Predicate<? super T> predicate) {
        this.getProfiler().visit("getLoadedEntities");
        int i = MathHelper.floor((arg.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((arg.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((arg.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((arg.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        ChunkManager lv = this.getChunkManager();
        for (int m = i; m < j; ++m) {
            for (int n = k; n < l; ++n) {
                WorldChunk lv2 = lv.getWorldChunk(m, n);
                if (lv2 == null) continue;
                lv2.getEntities(class_, arg, list, predicate);
            }
        }
        return list;
    }

    @Nullable
    public abstract Entity getEntityById(int var1);

    public void markDirty(BlockPos arg, BlockEntity arg2) {
        if (this.isChunkLoaded(arg)) {
            this.getWorldChunk(arg).markDirty();
        }
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public World getWorld() {
        return this;
    }

    public int getReceivedStrongRedstonePower(BlockPos arg) {
        int i = 0;
        if ((i = Math.max(i, this.getStrongRedstonePower(arg.down(), Direction.DOWN))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(arg.up(), Direction.UP))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(arg.north(), Direction.NORTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(arg.south(), Direction.SOUTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(arg.west(), Direction.WEST))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(arg.east(), Direction.EAST))) >= 15) {
            return i;
        }
        return i;
    }

    public boolean isEmittingRedstonePower(BlockPos arg, Direction arg2) {
        return this.getEmittedRedstonePower(arg, arg2) > 0;
    }

    public int getEmittedRedstonePower(BlockPos arg, Direction arg2) {
        BlockState lv = this.getBlockState(arg);
        int i = lv.getWeakRedstonePower(this, arg, arg2);
        if (lv.isSolidBlock(this, arg)) {
            return Math.max(i, this.getReceivedStrongRedstonePower(arg));
        }
        return i;
    }

    public boolean isReceivingRedstonePower(BlockPos arg) {
        if (this.getEmittedRedstonePower(arg.down(), Direction.DOWN) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(arg.up(), Direction.UP) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(arg.north(), Direction.NORTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(arg.south(), Direction.SOUTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(arg.west(), Direction.WEST) > 0) {
            return true;
        }
        return this.getEmittedRedstonePower(arg.east(), Direction.EAST) > 0;
    }

    public int getReceivedRedstonePower(BlockPos arg) {
        int i = 0;
        for (Direction lv : DIRECTIONS) {
            int j = this.getEmittedRedstonePower(arg.offset(lv), lv);
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

    public boolean canPlayerModifyAt(PlayerEntity arg, BlockPos arg2) {
        return true;
    }

    public void sendEntityStatus(Entity arg, byte b) {
    }

    public void addSyncedBlockEvent(BlockPos arg, Block arg2, int i, int j) {
        this.getBlockState(arg).onSyncedBlockEvent(this, arg, i, j);
    }

    @Override
    public WorldProperties getLevelProperties() {
        return this.properties;
    }

    public GameRules getGameRules() {
        return this.properties.getGameRules();
    }

    public float getThunderGradient(float f) {
        return MathHelper.lerp(f, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(f);
    }

    @Environment(value=EnvType.CLIENT)
    public void setThunderGradient(float f) {
        this.thunderGradientPrev = f;
        this.thunderGradient = f;
    }

    public float getRainGradient(float f) {
        return MathHelper.lerp(f, this.rainGradientPrev, this.rainGradient);
    }

    @Environment(value=EnvType.CLIENT)
    public void setRainGradient(float f) {
        this.rainGradientPrev = f;
        this.rainGradient = f;
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

    public boolean hasRain(BlockPos arg) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.isSkyVisible(arg)) {
            return false;
        }
        if (this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, arg).getY() > arg.getY()) {
            return false;
        }
        Biome lv = this.getBiome(arg);
        return lv.getPrecipitation() == Biome.Precipitation.RAIN && lv.getTemperature(arg) >= 0.15f;
    }

    public boolean hasHighHumidity(BlockPos arg) {
        Biome lv = this.getBiome(arg);
        return lv.hasHighHumidity();
    }

    @Nullable
    public abstract MapState getMapState(String var1);

    public abstract void putMapState(MapState var1);

    public abstract int getNextMapId();

    public void syncGlobalEvent(int i, BlockPos arg, int j) {
    }

    public CrashReportSection addDetailsToCrashReport(CrashReport arg) {
        CrashReportSection lv = arg.addElement("Affected level", 1);
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
    public void addFireworkParticle(double d, double e, double f, double g, double h, double i, @Nullable CompoundTag arg) {
    }

    public abstract Scoreboard getScoreboard();

    public void updateComparators(BlockPos arg, Block arg2) {
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BlockPos lv2 = arg.offset(lv);
            if (!this.isChunkLoaded(lv2)) continue;
            BlockState lv3 = this.getBlockState(lv2);
            if (lv3.isOf(Blocks.COMPARATOR)) {
                lv3.neighborUpdate(this, lv2, arg2, arg, false);
                continue;
            }
            if (!lv3.isSolidBlock(this, lv2) || !(lv3 = this.getBlockState(lv2 = lv2.offset(lv))).isOf(Blocks.COMPARATOR)) continue;
            lv3.neighborUpdate(this, lv2, arg2, arg, false);
        }
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos arg) {
        long l = 0L;
        float f = 0.0f;
        if (this.isChunkLoaded(arg)) {
            f = this.getMoonSize();
            l = this.getWorldChunk(arg).getInhabitedTime();
        }
        return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
    }

    @Override
    public int getAmbientDarkness() {
        return this.ambientDarkness;
    }

    public void setLightningTicksLeft(int i) {
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.border;
    }

    public void sendPacket(Packet<?> arg) {
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
    public boolean testBlockState(BlockPos arg, Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(arg));
    }

    public abstract RecipeManager getRecipeManager();

    public abstract RegistryTagManager getTagManager();

    public BlockPos getRandomPosInChunk(int i, int j, int k, int l) {
        this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
        int m = this.lcgBlockSeed >> 2;
        return new BlockPos(i + (m & 0xF), j + (m >> 16 & l), k + (m >> 8 & 0xF));
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

    @Override
    public /* synthetic */ Chunk getChunk(int i, int j) {
        return this.getChunk(i, j);
    }
}

