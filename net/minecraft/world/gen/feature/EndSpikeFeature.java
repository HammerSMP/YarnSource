/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EndSpikeFeature
extends Feature<EndSpikeFeatureConfig> {
    private static final LoadingCache<Long, List<Spike>> CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build((CacheLoader)new SpikeCache());

    public EndSpikeFeature(Function<Dynamic<?>, ? extends EndSpikeFeatureConfig> function) {
        super(function);
    }

    public static List<Spike> getSpikes(IWorld arg) {
        Random random = new Random(arg.getSeed());
        long l = random.nextLong() & 0xFFFFL;
        return (List)CACHE.getUnchecked((Object)l);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, EndSpikeFeatureConfig arg5) {
        List<Spike> list = arg5.getSpikes();
        if (list.isEmpty()) {
            list = EndSpikeFeature.getSpikes(arg);
        }
        for (Spike lv : list) {
            if (!lv.isInChunk(arg4)) continue;
            this.generateSpike(arg, random, arg5, lv);
        }
        return true;
    }

    private void generateSpike(IWorld arg, Random random, EndSpikeFeatureConfig arg2, Spike arg3) {
        int i = arg3.getRadius();
        for (BlockPos lv : BlockPos.iterate(new BlockPos(arg3.getCenterX() - i, 0, arg3.getCenterZ() - i), new BlockPos(arg3.getCenterX() + i, arg3.getHeight() + 10, arg3.getCenterZ() + i))) {
            if (lv.getSquaredDistance(arg3.getCenterX(), lv.getY(), arg3.getCenterZ(), false) <= (double)(i * i + 1) && lv.getY() < arg3.getHeight()) {
                this.setBlockState(arg, lv, Blocks.OBSIDIAN.getDefaultState());
                continue;
            }
            if (lv.getY() <= 65) continue;
            this.setBlockState(arg, lv, Blocks.AIR.getDefaultState());
        }
        if (arg3.isGuarded()) {
            int j = -2;
            int k = 2;
            int l = 3;
            BlockPos.Mutable lv2 = new BlockPos.Mutable();
            for (int m = -2; m <= 2; ++m) {
                for (int n = -2; n <= 2; ++n) {
                    for (int o = 0; o <= 3; ++o) {
                        boolean bl3;
                        boolean bl = MathHelper.abs(m) == 2;
                        boolean bl2 = MathHelper.abs(n) == 2;
                        boolean bl4 = bl3 = o == 3;
                        if (!bl && !bl2 && !bl3) continue;
                        boolean bl42 = m == -2 || m == 2 || bl3;
                        boolean bl5 = n == -2 || n == 2 || bl3;
                        BlockState lv3 = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, bl42 && n != -2)).with(PaneBlock.SOUTH, bl42 && n != 2)).with(PaneBlock.WEST, bl5 && m != -2)).with(PaneBlock.EAST, bl5 && m != 2);
                        this.setBlockState(arg, lv2.set(arg3.getCenterX() + m, arg3.getHeight() + o, arg3.getCenterZ() + n), lv3);
                    }
                }
            }
        }
        EndCrystalEntity lv4 = EntityType.END_CRYSTAL.create(arg.getWorld());
        lv4.setBeamTarget(arg2.getPos());
        lv4.setInvulnerable(arg2.isCrystalInvulerable());
        lv4.refreshPositionAndAngles((float)arg3.getCenterX() + 0.5f, arg3.getHeight() + 1, (float)arg3.getCenterZ() + 0.5f, random.nextFloat() * 360.0f, 0.0f);
        arg.spawnEntity(lv4);
        this.setBlockState(arg, new BlockPos(arg3.getCenterX(), arg3.getHeight(), arg3.getCenterZ()), Blocks.BEDROCK.getDefaultState());
    }

    static class SpikeCache
    extends CacheLoader<Long, List<Spike>> {
        private SpikeCache() {
        }

        public List<Spike> load(Long arg) {
            List list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
            Collections.shuffle(list, new Random(arg));
            ArrayList list2 = Lists.newArrayList();
            for (int i = 0; i < 10; ++i) {
                int j = MathHelper.floor(42.0 * Math.cos(2.0 * (-Math.PI + 0.3141592653589793 * (double)i)));
                int k = MathHelper.floor(42.0 * Math.sin(2.0 * (-Math.PI + 0.3141592653589793 * (double)i)));
                int l = (Integer)list.get(i);
                int m = 2 + l / 3;
                int n = 76 + l * 3;
                boolean bl = l == 1 || l == 2;
                list2.add(new Spike(j, k, m, n, bl));
            }
            return list2;
        }

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((Long)object);
        }
    }

    public static class Spike {
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final Box boundingBox;

        public Spike(int i, int j, int k, int l, boolean bl) {
            this.centerX = i;
            this.centerZ = j;
            this.radius = k;
            this.height = l;
            this.guarded = bl;
            this.boundingBox = new Box(i - k, 0.0, j - k, i + k, 256.0, j + k);
        }

        public boolean isInChunk(BlockPos arg) {
            return arg.getX() >> 4 == this.centerX >> 4 && arg.getZ() >> 4 == this.centerZ >> 4;
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        public Box getBoundingBox() {
            return this.boundingBox;
        }

        public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.put(dynamicOps.createString("centerX"), dynamicOps.createInt(this.centerX));
            builder.put(dynamicOps.createString("centerZ"), dynamicOps.createInt(this.centerZ));
            builder.put(dynamicOps.createString("radius"), dynamicOps.createInt(this.radius));
            builder.put(dynamicOps.createString("height"), dynamicOps.createInt(this.height));
            builder.put(dynamicOps.createString("guarded"), dynamicOps.createBoolean(this.guarded));
            return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
        }

        public static <T> Spike deserialize(Dynamic<T> dynamic) {
            return new Spike(dynamic.get("centerX").asInt(0), dynamic.get("centerZ").asInt(0), dynamic.get("radius").asInt(0), dynamic.get("height").asInt(0), dynamic.get("guarded").asBoolean(false));
        }
    }
}

