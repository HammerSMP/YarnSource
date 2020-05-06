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
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.class_5217;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MultiTickScheduler;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegion
implements IWorld {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Chunk> chunks;
    private final int centerChunkX;
    private final int centerChunkZ;
    private final int width;
    private final ServerWorld world;
    private final long seed;
    private final class_5217 levelProperties;
    private final Random random;
    private final Dimension dimension;
    private final ChunkGeneratorConfig generatorSettings;
    private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<Block>(arg -> this.getChunk((BlockPos)arg).getBlockTickScheduler());
    private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<Fluid>(arg -> this.getChunk((BlockPos)arg).getFluidTickScheduler());
    private final BiomeAccess biomeAccess;
    private final ChunkPos field_23788;
    private final ChunkPos field_23789;

    public ChunkRegion(ServerWorld arg2, List<Chunk> list) {
        int i = MathHelper.floor(Math.sqrt(list.size()));
        if (i * i != list.size()) {
            throw Util.throwOrPause(new IllegalStateException("Cache size is not a square."));
        }
        ChunkPos lv = list.get(list.size() / 2).getPos();
        this.chunks = list;
        this.centerChunkX = lv.x;
        this.centerChunkZ = lv.z;
        this.width = i;
        this.world = arg2;
        this.seed = arg2.getSeed();
        this.generatorSettings = arg2.getChunkManager().getChunkGenerator().getConfig();
        this.levelProperties = arg2.getLevelProperties();
        this.random = arg2.getRandom();
        this.dimension = arg2.getDimension();
        this.biomeAccess = new BiomeAccess(this, class_5217.method_27418(this.seed), this.dimension.getType().getBiomeAccessType());
        this.field_23788 = list.get(0).getPos();
        this.field_23789 = list.get(list.size() - 1).getPos();
    }

    public int getCenterChunkX() {
        return this.centerChunkX;
    }

    public int getCenterChunkZ() {
        return this.centerChunkZ;
    }

    @Override
    public Chunk getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.EMPTY);
    }

    @Override
    @Nullable
    public Chunk getChunk(int i, int j, ChunkStatus arg, boolean bl) {
        Chunk lv2;
        if (this.isChunkLoaded(i, j)) {
            int k = i - this.field_23788.x;
            int l = j - this.field_23788.z;
            Chunk lv = this.chunks.get(k + l * this.width);
            if (lv.getStatus().isAtLeast(arg)) {
                return lv;
            }
        } else {
            lv2 = null;
        }
        if (!bl) {
            return null;
        }
        LOGGER.error("Requested chunk : {} {}", (Object)i, (Object)j);
        LOGGER.error("Region bounds : {} {} | {} {}", (Object)this.field_23788.x, (Object)this.field_23788.z, (Object)this.field_23789.x, (Object)this.field_23789.z);
        if (lv2 != null) {
            throw Util.throwOrPause(new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", arg, lv2.getStatus(), i, j)));
        }
        throw Util.throwOrPause(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j)));
    }

    @Override
    public boolean isChunkLoaded(int i, int j) {
        return i >= this.field_23788.x && i <= this.field_23789.x && j >= this.field_23788.z && j <= this.field_23789.z;
    }

    @Override
    public BlockState getBlockState(BlockPos arg) {
        return this.getChunk(arg.getX() >> 4, arg.getZ() >> 4).getBlockState(arg);
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        return this.getChunk(arg).getFluidState(arg);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(double d, double e, double f, double g, Predicate<Entity> predicate) {
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
    public Biome getGeneratorStoredBiome(int i, int j, int k) {
        return this.world.getGeneratorStoredBiome(i, j, k);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getBrightness(Direction arg, boolean bl) {
        return 1.0f;
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    public boolean breakBlock(BlockPos arg, boolean bl, @Nullable Entity arg2) {
        BlockState lv = this.getBlockState(arg);
        if (lv.isAir()) {
            return false;
        }
        if (bl) {
            BlockEntity lv2 = lv.getBlock().hasBlockEntity() ? this.getBlockEntity(arg) : null;
            Block.dropStacks(lv, this.world, arg, lv2, arg2, ItemStack.EMPTY);
        }
        return this.setBlockState(arg, Blocks.AIR.getDefaultState(), 3);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg) {
        Chunk lv = this.getChunk(arg);
        BlockEntity lv2 = lv.getBlockEntity(arg);
        if (lv2 != null) {
            return lv2;
        }
        CompoundTag lv3 = lv.getBlockEntityTagAt(arg);
        BlockState lv4 = lv.getBlockState(arg);
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
                lv.setBlockEntity(arg, lv2);
                return lv2;
            }
        }
        if (lv4.getBlock() instanceof BlockEntityProvider) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", (Object)arg);
        }
        return null;
    }

    @Override
    public boolean setBlockState(BlockPos arg, BlockState arg2, int i) {
        Block lv3;
        Chunk lv = this.getChunk(arg);
        BlockState lv2 = lv.setBlockState(arg, arg2, false);
        if (lv2 != null) {
            this.world.onBlockChanged(arg, lv2, arg2);
        }
        if ((lv3 = arg2.getBlock()).hasBlockEntity()) {
            if (lv.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
                lv.setBlockEntity(arg, ((BlockEntityProvider)((Object)lv3)).createBlockEntity(this));
            } else {
                CompoundTag lv4 = new CompoundTag();
                lv4.putInt("x", arg.getX());
                lv4.putInt("y", arg.getY());
                lv4.putInt("z", arg.getZ());
                lv4.putString("id", "DUMMY");
                lv.addPendingBlockEntityTag(lv4);
            }
        } else if (lv2 != null && lv2.getBlock().hasBlockEntity()) {
            lv.removeBlockEntity(arg);
        }
        if (arg2.shouldPostProcess(this, arg)) {
            this.markBlockForPostProcessing(arg);
        }
        return true;
    }

    private void markBlockForPostProcessing(BlockPos arg) {
        this.getChunk(arg).markBlockForPostProcessing(arg);
    }

    @Override
    public boolean spawnEntity(Entity arg) {
        int i = MathHelper.floor(arg.getX() / 16.0);
        int j = MathHelper.floor(arg.getZ() / 16.0);
        this.getChunk(i, j).addEntity(arg);
        return true;
    }

    @Override
    public boolean removeBlock(BlockPos arg, boolean bl) {
        return this.setBlockState(arg, Blocks.AIR.getDefaultState(), 3);
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
    public class_5217 getLevelProperties() {
        return this.levelProperties;
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos arg) {
        if (!this.isChunkLoaded(arg.getX() >> 4, arg.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        }
        return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.getMoonSize());
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
    public int getTopY(Heightmap.Type arg, int i, int j) {
        return this.getChunk(i >> 4, j >> 4).sampleHeightmap(arg, i & 0xF, j & 0xF) + 1;
    }

    @Override
    public void playSound(@Nullable PlayerEntity arg, BlockPos arg2, SoundEvent arg3, SoundCategory arg4, float f, float g) {
    }

    @Override
    public void addParticle(ParticleEffect arg, double d, double e, double f, double g, double h, double i) {
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity arg, int i, BlockPos arg2, int j) {
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    @Override
    public boolean testBlockState(BlockPos arg, Predicate<BlockState> predicate) {
        return predicate.test(this.getBlockState(arg));
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> arg, Box arg2, @Nullable Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity arg, Box arg2, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    public List<PlayerEntity> getPlayers() {
        return Collections.emptyList();
    }

    @Override
    @Deprecated
    public /* synthetic */ World getWorld() {
        return this.getWorld();
    }
}

