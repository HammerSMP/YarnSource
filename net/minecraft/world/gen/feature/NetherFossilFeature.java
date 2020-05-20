/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class NetherFossilFeature
extends AbstractTempleFeature<DefaultFeatureConfig> {
    public NetherFossilFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 14357921;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Nether_Fossil";
    }

    @Override
    protected int getSpacing(ChunkGeneratorConfig arg) {
        return 2;
    }

    @Override
    protected int getSeparation(ChunkGeneratorConfig arg) {
        return 1;
    }

    @Override
    public int getRadius() {
        return 3;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator arg, StructureManager arg2, int i, int j, Biome arg3) {
            int n;
            ChunkPos lv = new ChunkPos(i, j);
            int k = lv.getStartX() + this.random.nextInt(16);
            int l = lv.getStartZ() + this.random.nextInt(16);
            int m = arg.getSeaLevel();
            BlockView lv2 = arg.getColumnSample(k, l);
            BlockPos.Mutable lv3 = new BlockPos.Mutable(k, n, l);
            for (n = m + this.random.nextInt(arg.getMaxY() - 2 - m); n > m; --n) {
                BlockState lv4 = lv2.getBlockState(lv3);
                lv3.move(Direction.DOWN);
                BlockState lv5 = lv2.getBlockState(lv3);
                if (lv4.isAir() && (lv5.isOf(Blocks.SOUL_SAND) || lv5.isSideSolidFullSquare(lv2, lv3, Direction.UP))) break;
            }
            if (n <= m) {
                return;
            }
            NetherFossilGenerator.addPieces(arg2, this.children, this.random, new BlockPos(k, n, l));
            this.setBoundingBoxFromChildren();
        }
    }
}

