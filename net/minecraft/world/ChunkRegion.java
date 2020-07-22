/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MultiTickScheduler;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegion
implements ServerWorldAccess {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Chunk> chunks;
    private final int centerChunkX;
    private final int centerChunkZ;
    private final int width;
    private final ServerWorld world;
    private final long seed;
    private final WorldProperties levelProperties;
    private final Random random;
    private final DimensionType dimension;
    private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<Block>(pos -> this.getChunk((BlockPos)pos).getBlockTickScheduler());
    private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<Fluid>(pos -> this.getChunk((BlockPos)pos).getFluidTickScheduler());
    private final BiomeAccess biomeAccess;
    private final ChunkPos lowerCorner;
    private final ChunkPos upperCorner;

    public ChunkRegion(ServerWorld world, List<Chunk> chunks) {
        int i = MathHelper.floor(Math.sqrt(chunks.size()));
        if (i * i != chunks.size()) {
            throw Util.throwOrPause(new IllegalStateException("Cache size is not a square."));
        }
        ChunkPos lv = chunks.get(chunks.size() / 2).getPos();
        this.chunks = chunks;
        this.centerChunkX = lv.x;
        this.centerChunkZ = lv.z;
        this.width = i;
        this.world = world;
        this.seed = world.getSeed();
        this.levelProperties = world.getLevelProperties();
        this.random = world.getRandom();
        this.dimension = world.getDimension();
        this.biomeAccess = new BiomeAccess(this, BiomeAccess.hashSeed(this.seed), world.getDimension().getBiomeAccessType());
        this.lowerCorner = chunks.get(0).getPos();
        this.upperCorner = chunks.get(chunks.size() - 1).getPos();
    }

    public int getCenterChunkX() {
        return this.centerChunkX;
    }

    public int getCenterChunkZ() {
        return this.centerChunkZ;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
    }

    @Override
    @Nullable
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        Chunk lv2;
        if (this.isChunkLoaded(chunkX, chunkZ)) {
            int k = chunkX - this.lowerCorner.x;
            int l = chunkZ - this.lowerCorner.z;
            Chunk lv = this.chunks.get(k + l * this.width);
            if (lv.getStatus().isAtLeast(leastStatus)) {
                return lv;
            }
        } else {
            lv2 = null;
        }
        if (!create) {
            return null;
        }
        LOGGER.error("Requested chunk : {} {}", (Object)chunkX, (Object)chunkZ);
        LOGGER.error("Region bounds : {} {} | {} {}", (Object)this.lowerCorner.x, (Object)this.lowerCorner.z, (Object)this.upperCorner.x, (Object)this.upperCorner.z);
        if (lv2 != null) {
            throw Util.throwOrPause(new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", leastStatus, lv2.getStatus(), chunkX, chunkZ)));
        }
        throw Util.throwOrPause(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", chunkX, chunkZ)));
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return chunkX >= this.lowerCorner.x && chunkX <= this.upperCorner.x && chunkZ >= this.lowerCorner.z && chunkZ <= this.upperCorner.z;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4).getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.getChunk(pos).getFluidState(pos);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, Predicate<Entity> targetPredicate) {
        return null;
    }

    @Override
    public int getAmbientDarkness() {
        return 0;
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return this.biomeAccess;
    }

    @Override
    public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return this.world.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getBrightness(Direction direction, boolean shaded) {
        return 1.0f;
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        BlockState lv = this.getBlockState(pos);
        if (lv.isAir()) {
            return false;
        }
        if (drop) {
            BlockEntity lv2 = lv.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
            Block.dropStacks(lv, this.world, pos, lv2, breakingEntity, ItemStack.EMPTY);
        }
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3, maxUpdateDepth);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        Chunk lv = this.getChunk(pos);
        BlockEntity lv2 = lv.getBlockEntity(pos);
        if (lv2 != null) {
            return lv2;
        }
        CompoundTag lv3 = lv.getBlockEntityTag(pos);
        BlockState lv4 = lv.getBlockState(pos);
        if (lv3 != null) {
            if ("DUMMY".equals(lv3.getString("id"))) {
                Block lv5 = lv4.getBlock();
                if (!(lv5 instanceof BlockEntityProvider)) {
                    return null;
                }
                lv2 = ((BlockEntityProvider)((Object)lv5)).createBlockEntity(this.world);
            } else {
                lv2 = BlockEntity.createFromTag(lv4, lv3);
            }
            if (lv2 != null) {
                lv.setBlockEntity(pos, lv2);
                return lv2;
            }
        }
        if (lv4.getBlock() instanceof BlockEntityProvider) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", (Object)pos);
        }
        return null;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        Block lv3;
        Chunk lv = this.getChunk(pos);
        BlockState lv2 = lv.setBlockState(pos, state, false);
        if (lv2 != null) {
            this.world.onBlockChanged(pos, lv2, state);
        }
        if ((lv3 = state.getBlock()).hasBlockEntity()) {
            if (lv.getStatus().getChunkType() == ChunkStatus.ChunkType.field_12807) {
                lv.setBlockEntity(pos, ((BlockEntityProvider)((Object)lv3)).createBlockEntity(this));
            } else {
                CompoundTag lv4 = new CompoundTag();
                lv4.putInt("x", pos.getX());
                lv4.putInt("y", pos.getY());
                lv4.putInt("z", pos.getZ());
                lv4.putString("id", "DUMMY");
                lv.addPendingBlockEntityTag(lv4);
            }
        } else if (lv2 != null && lv2.getBlock().hasBlockEntity()) {
            lv.removeBlockEntity(pos);
        }
        if (state.shouldPostProcess(this, pos)) {
            this.markBlockForPostProcessing(pos);
        }
        return true;
    }

    private void markBlockForPostProcessing(BlockPos pos) {
        this.getChunk(pos).markBlockForPostProcessing(pos);
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        int i = MathHelper.floor(entity.getX() / 16.0);
        int j = MathHelper.floor(entity.getZ() / 16.0);
        this.getChunk(i, j).addEntity(entity);
        return true;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    @Deprecated
    public ServerWorld getWorld() {
        return this.world;
    }

    @Override
    public WorldProperties getLevelProperties() {
        return this.levelProperties;
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        if (!this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        }
        return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.method_30272());
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.world.getChunkManager();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        return this.getChunk(x >> 4, z >> 4).sampleHeightmap(heightmap, x & 0xF, z & 0xF) + 1;
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
    }

    @Override
    public DimensionType getDimension() {
        return this.dimension;
    }

    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        return state.test(this.getBlockState(pos));
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByClass(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getOtherEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    public List<PlayerEntity> getPlayers() {
        return Collections.emptyList();
    }

    @Override
    public Stream<? extends StructureStart<?>> method_30275(ChunkSectionPos arg, StructureFeature<?> arg2) {
        return this.world.method_30275(arg, arg2);
    }

    @Override
    @Deprecated
    public /* synthetic */ World getWorld() {
        return this.getWorld();
    }
}

