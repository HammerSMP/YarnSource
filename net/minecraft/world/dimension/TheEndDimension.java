/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.class_5217;
import net.minecraft.class_5268;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;

public class TheEndDimension
extends Dimension {
    public static final BlockPos SPAWN_POINT = new BlockPos(100, 50, 0);
    private final EnderDragonFight enderDragonFight;

    public TheEndDimension(World arg, DimensionType arg2) {
        super(arg, arg2, 0.0f);
        if (arg instanceof ServerWorld) {
            ServerWorld lv = (ServerWorld)arg;
            class_5217 lv2 = lv.getLevelProperties();
            if (lv2 instanceof class_5268) {
                CompoundTag lv3 = ((class_5268)lv2).getWorldData();
                this.enderDragonFight = new EnderDragonFight(lv, lv3.getCompound("DragonFight"));
            } else {
                this.enderDragonFight = null;
            }
        } else {
            this.enderDragonFight = null;
        }
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        FloatingIslandsChunkGeneratorConfig lv = ChunkGeneratorType.FLOATING_ISLANDS.createConfig();
        lv.setDefaultBlock(Blocks.END_STONE.getDefaultState());
        lv.setDefaultFluid(Blocks.AIR.getDefaultState());
        lv.withCenter(this.getForcedSpawnPoint());
        return ChunkGeneratorType.FLOATING_ISLANDS.create(this.world, BiomeSourceType.THE_END.applyConfig(BiomeSourceType.THE_END.getConfig(this.world.getSeed())), lv);
    }

    @Override
    public float getSkyAngle(long l, float f) {
        return 0.0f;
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public float[] getBackgroundColor(float f, float g) {
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d modifyFogColor(Vec3d arg, float f) {
        return arg.multiply(0.15f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasGround() {
        return false;
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    public boolean hasVisibleSky() {
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getCloudHeight() {
        return 8.0f;
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(ChunkPos arg, boolean bl) {
        Random random = new Random(this.world.getSeed());
        BlockPos lv = new BlockPos(arg.getStartX() + random.nextInt(15), 0, arg.getEndZ() + random.nextInt(15));
        return this.world.getTopNonAirState(lv).getMaterial().blocksMovement() ? lv : null;
    }

    @Override
    public BlockPos getForcedSpawnPoint() {
        return SPAWN_POINT;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
        return this.getSpawningBlockInChunk(new ChunkPos(i >> 4, j >> 4), bl);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isFogThick(int i, int j) {
        return false;
    }

    @Override
    public DimensionType getType() {
        return DimensionType.THE_END;
    }

    @Override
    public void saveWorldData(class_5268 arg) {
        CompoundTag lv = new CompoundTag();
        if (this.enderDragonFight != null) {
            lv.put("DragonFight", this.enderDragonFight.toTag());
        }
        arg.setWorldData(lv);
    }

    @Override
    public void update() {
        if (this.enderDragonFight != null) {
            this.enderDragonFight.tick();
        }
    }

    @Nullable
    public EnderDragonFight getEnderDragonFight() {
        return this.enderDragonFight;
    }
}

