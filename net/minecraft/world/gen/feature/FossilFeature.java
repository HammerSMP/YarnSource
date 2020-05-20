/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class FossilFeature
extends Feature<DefaultFeatureConfig> {
    private static final Identifier SPINE_1 = new Identifier("fossil/spine_1");
    private static final Identifier SPINE_2 = new Identifier("fossil/spine_2");
    private static final Identifier SPINE_3 = new Identifier("fossil/spine_3");
    private static final Identifier SPINE_4 = new Identifier("fossil/spine_4");
    private static final Identifier SPINE_1_COAL = new Identifier("fossil/spine_1_coal");
    private static final Identifier SPINE_2_COAL = new Identifier("fossil/spine_2_coal");
    private static final Identifier SPINE_3_COAL = new Identifier("fossil/spine_3_coal");
    private static final Identifier SPINE_4_COAL = new Identifier("fossil/spine_4_coal");
    private static final Identifier SKULL_1 = new Identifier("fossil/skull_1");
    private static final Identifier SKULL_2 = new Identifier("fossil/skull_2");
    private static final Identifier SKULL_3 = new Identifier("fossil/skull_3");
    private static final Identifier SKULL_4 = new Identifier("fossil/skull_4");
    private static final Identifier SKULL_1_COAL = new Identifier("fossil/skull_1_coal");
    private static final Identifier SKULL_2_COAL = new Identifier("fossil/skull_2_coal");
    private static final Identifier SKULL_3_COAL = new Identifier("fossil/skull_3_coal");
    private static final Identifier SKULL_4_COAL = new Identifier("fossil/skull_4_coal");
    private static final Identifier[] FOSSILS = new Identifier[]{SPINE_1, SPINE_2, SPINE_3, SPINE_4, SKULL_1, SKULL_2, SKULL_3, SKULL_4};
    private static final Identifier[] COAL_FOSSILS = new Identifier[]{SPINE_1_COAL, SPINE_2_COAL, SPINE_3_COAL, SPINE_4_COAL, SKULL_1_COAL, SKULL_2_COAL, SKULL_3_COAL, SKULL_4_COAL};

    public FossilFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        Random random2 = arg.getRandom();
        BlockRotation lv = BlockRotation.random(random2);
        int i = random2.nextInt(FOSSILS.length);
        StructureManager lv2 = ((ServerWorld)arg.getWorld()).getServer().getStructureManager();
        Structure lv3 = lv2.getStructureOrBlank(FOSSILS[i]);
        Structure lv4 = lv2.getStructureOrBlank(COAL_FOSSILS[i]);
        ChunkPos lv5 = new ChunkPos(arg4);
        BlockBox lv6 = new BlockBox(lv5.getStartX(), 0, lv5.getStartZ(), lv5.getEndX(), 256, lv5.getEndZ());
        StructurePlacementData lv7 = new StructurePlacementData().setRotation(lv).setBoundingBox(lv6).setRandom(random2).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        BlockPos lv8 = lv3.getRotatedSize(lv);
        int j = random2.nextInt(16 - lv8.getX());
        int k = random2.nextInt(16 - lv8.getZ());
        int l = 256;
        for (int m = 0; m < lv8.getX(); ++m) {
            for (int n = 0; n < lv8.getZ(); ++n) {
                l = Math.min(l, arg.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, arg4.getX() + m + j, arg4.getZ() + n + k));
            }
        }
        int o = Math.max(l - 15 - random2.nextInt(10), 10);
        BlockPos lv9 = lv3.offsetByTransformedSize(arg4.add(j, o, k), BlockMirror.NONE, lv);
        BlockRotStructureProcessor lv10 = new BlockRotStructureProcessor(0.9f);
        lv7.clearProcessors().addProcessor(lv10);
        lv3.place(arg, lv9, lv9, lv7, 4);
        lv7.removeProcessor(lv10);
        BlockRotStructureProcessor lv11 = new BlockRotStructureProcessor(0.1f);
        lv7.clearProcessors().addProcessor(lv11);
        lv4.place(arg, lv9, lv9, lv7, 4);
        return true;
    }
}

