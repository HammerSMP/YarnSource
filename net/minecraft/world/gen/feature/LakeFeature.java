/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

public class LakeFeature
extends Feature<SingleStateFeatureConfig> {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

    public LakeFeature(Function<Dynamic<?>, ? extends SingleStateFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, SingleStateFeatureConfig arg5) {
        while (arg4.getY() > 5 && arg.isAir(arg4)) {
            arg4 = arg4.down();
        }
        if (arg4.getY() <= 4) {
            return false;
        }
        if (arg2.getStructuresWithChildren(ChunkSectionPos.from(arg4 = arg4.down(4)), Feature.VILLAGE).findAny().isPresent()) {
            return false;
        }
        boolean[] bls = new boolean[2048];
        int i = random.nextInt(4) + 4;
        for (int j = 0; j < i; ++j) {
            double d = random.nextDouble() * 6.0 + 3.0;
            double e = random.nextDouble() * 4.0 + 2.0;
            double f = random.nextDouble() * 6.0 + 3.0;
            double g = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
            double h = random.nextDouble() * (8.0 - e - 4.0) + 2.0 + e / 2.0;
            double k = random.nextDouble() * (16.0 - f - 2.0) + 1.0 + f / 2.0;
            for (int l = 1; l < 15; ++l) {
                for (int m = 1; m < 15; ++m) {
                    for (int n = 1; n < 7; ++n) {
                        double o = ((double)l - g) / (d / 2.0);
                        double p = ((double)n - h) / (e / 2.0);
                        double q = ((double)m - k) / (f / 2.0);
                        double r = o * o + p * p + q * q;
                        if (!(r < 1.0)) continue;
                        bls[(l * 16 + m) * 8 + n] = true;
                    }
                }
            }
        }
        for (int s = 0; s < 16; ++s) {
            for (int t = 0; t < 16; ++t) {
                for (int u = 0; u < 8; ++u) {
                    boolean bl;
                    boolean bl2 = bl = !bls[(s * 16 + t) * 8 + u] && (s < 15 && bls[((s + 1) * 16 + t) * 8 + u] || s > 0 && bls[((s - 1) * 16 + t) * 8 + u] || t < 15 && bls[(s * 16 + t + 1) * 8 + u] || t > 0 && bls[(s * 16 + (t - 1)) * 8 + u] || u < 7 && bls[(s * 16 + t) * 8 + u + 1] || u > 0 && bls[(s * 16 + t) * 8 + (u - 1)]);
                    if (!bl) continue;
                    Material lv = arg.getBlockState(arg4.add(s, u, t)).getMaterial();
                    if (u >= 4 && lv.isLiquid()) {
                        return false;
                    }
                    if (u >= 4 || lv.isSolid() || arg.getBlockState(arg4.add(s, u, t)) == arg5.state) continue;
                    return false;
                }
            }
        }
        for (int v = 0; v < 16; ++v) {
            for (int w = 0; w < 16; ++w) {
                for (int x = 0; x < 8; ++x) {
                    if (!bls[(v * 16 + w) * 8 + x]) continue;
                    arg.setBlockState(arg4.add(v, x, w), x >= 4 ? CAVE_AIR : arg5.state, 2);
                }
            }
        }
        for (int y = 0; y < 16; ++y) {
            for (int z = 0; z < 16; ++z) {
                for (int aa = 4; aa < 8; ++aa) {
                    BlockPos lv2;
                    if (!bls[(y * 16 + z) * 8 + aa] || !LakeFeature.isDirt(arg.getBlockState(lv2 = arg4.add(y, aa - 1, z)).getBlock()) || arg.getLightLevel(LightType.SKY, arg4.add(y, aa, z)) <= 0) continue;
                    Biome lv3 = arg.getBiome(lv2);
                    if (lv3.getSurfaceConfig().getTopMaterial().isOf(Blocks.MYCELIUM)) {
                        arg.setBlockState(lv2, Blocks.MYCELIUM.getDefaultState(), 2);
                        continue;
                    }
                    arg.setBlockState(lv2, Blocks.GRASS_BLOCK.getDefaultState(), 2);
                }
            }
        }
        if (arg5.state.getMaterial() == Material.LAVA) {
            for (int ab = 0; ab < 16; ++ab) {
                for (int ac = 0; ac < 16; ++ac) {
                    for (int ad = 0; ad < 8; ++ad) {
                        boolean bl2;
                        boolean bl = bl2 = !bls[(ab * 16 + ac) * 8 + ad] && (ab < 15 && bls[((ab + 1) * 16 + ac) * 8 + ad] || ab > 0 && bls[((ab - 1) * 16 + ac) * 8 + ad] || ac < 15 && bls[(ab * 16 + ac + 1) * 8 + ad] || ac > 0 && bls[(ab * 16 + (ac - 1)) * 8 + ad] || ad < 7 && bls[(ab * 16 + ac) * 8 + ad + 1] || ad > 0 && bls[(ab * 16 + ac) * 8 + (ad - 1)]);
                        if (!bl2 || ad >= 4 && random.nextInt(2) == 0 || !arg.getBlockState(arg4.add(ab, ad, ac)).getMaterial().isSolid()) continue;
                        arg.setBlockState(arg4.add(ab, ad, ac), Blocks.STONE.getDefaultState(), 2);
                    }
                }
            }
        }
        if (arg5.state.getMaterial() == Material.WATER) {
            for (int ae = 0; ae < 16; ++ae) {
                for (int af = 0; af < 16; ++af) {
                    int ag = 4;
                    BlockPos lv4 = arg4.add(ae, 4, af);
                    if (!arg.getBiome(lv4).canSetIce(arg, lv4, false)) continue;
                    arg.setBlockState(lv4, Blocks.ICE.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}

