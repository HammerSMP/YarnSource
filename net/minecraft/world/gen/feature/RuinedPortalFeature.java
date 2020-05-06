/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeaturePiece;
import net.minecraft.world.gen.feature.StructureFeature;

public class RuinedPortalFeature
extends AbstractTempleFeature<RuinedPortalFeatureConfig> {
    private static final String[] COMMON_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] RARE_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};

    public RuinedPortalFeature(Function<Dynamic<?>, ? extends RuinedPortalFeatureConfig> function) {
        super(function);
    }

    @Override
    public String getName() {
        return "Ruined_Portal";
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    protected int getSpacing(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getRuinedPortalSpacing(arg == DimensionType.THE_NETHER);
    }

    @Override
    protected int getSeparation(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getRuinedPortalSeparation(arg == DimensionType.THE_NETHER);
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 34222645;
    }

    private static boolean method_27209(BlockPos arg, Biome arg2) {
        return arg2.getTemperature(arg) < 0.15f;
    }

    private static int method_27211(Random random, ChunkGenerator<?> arg, RuinedPortalFeaturePiece.VerticalPlacement arg22, boolean bl, int i, int j, BlockBox arg3) {
        int t;
        if (arg22 == RuinedPortalFeaturePiece.VerticalPlacement.IN_NETHER) {
            if (bl) {
                int k = RuinedPortalFeature.choose(random, 32, 100);
            } else if (random.nextFloat() < 0.5f) {
                int l = RuinedPortalFeature.choose(random, 27, 29);
            } else {
                int m = RuinedPortalFeature.choose(random, 29, 100);
            }
        } else if (arg22 == RuinedPortalFeaturePiece.VerticalPlacement.IN_MOUNTAIN) {
            int n = i - j;
            int o = RuinedPortalFeature.choosePlacementHeight(random, 70, n);
        } else if (arg22 == RuinedPortalFeaturePiece.VerticalPlacement.UNDERGROUND) {
            int p = i - j;
            int q = RuinedPortalFeature.choosePlacementHeight(random, 15, p);
        } else if (arg22 == RuinedPortalFeaturePiece.VerticalPlacement.PARTLY_BURIED) {
            int r = i - j + RuinedPortalFeature.choose(random, 2, 8);
        } else {
            int s = i;
        }
        ImmutableList list = ImmutableList.of((Object)new BlockPos(arg3.minX, 0, arg3.minZ), (Object)new BlockPos(arg3.maxX, 0, arg3.minZ), (Object)new BlockPos(arg3.minX, 0, arg3.maxZ), (Object)new BlockPos(arg3.maxX, 0, arg3.maxZ));
        List list2 = list.stream().map(arg2 -> arg.getColumnSample(arg2.getX(), arg2.getZ())).collect(Collectors.toList());
        Heightmap.Type lv = arg22 == RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
        block0: for (t = s; t > 15; --t) {
            int u = 0;
            for (BlockView lv2 : list2) {
                if (!lv.getBlockPredicate().test(lv2.getBlockState(new BlockPos(0, t, 0))) || ++u != 3) continue;
                break block0;
            }
        }
        return t;
    }

    private static int choose(Random random, int i, int j) {
        return random.nextInt(j - i + 1) + i;
    }

    private static int choosePlacementHeight(Random random, int i, int j) {
        if (i < j) {
            return RuinedPortalFeature.choose(random, i, j);
        }
        return j;
    }

    public static enum Type {
        STANDARD("standard"),
        DESERT("desert"),
        JUNGLE("jungle"),
        SWAMP("swamp"),
        MOUNTAIN("mountain"),
        OCEAN("ocean"),
        NETHER("nether");

        private static final Map<String, Type> BY_NAME;
        private final String name;

        private Type(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Type byName(String string) {
            return BY_NAME.get(string);
        }

        static {
            BY_NAME = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, arg -> arg));
        }
    }

    public static class Start
    extends StructureStart {
        protected Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            Identifier lv11;
            RuinedPortalFeaturePiece.VerticalPlacement lv9;
            RuinedPortalFeatureConfig lv = arg.getStructureConfig(arg3, Feature.RUINED_PORTAL);
            if (lv == null) {
                return;
            }
            RuinedPortalFeaturePiece.Properties lv2 = new RuinedPortalFeaturePiece.Properties();
            if (lv.portalType == Type.DESERT) {
                RuinedPortalFeaturePiece.VerticalPlacement lv3 = RuinedPortalFeaturePiece.VerticalPlacement.PARTLY_BURIED;
                lv2.airPocket = false;
                lv2.mossiness = 0.0f;
            } else if (lv.portalType == Type.JUNGLE) {
                RuinedPortalFeaturePiece.VerticalPlacement lv4 = RuinedPortalFeaturePiece.VerticalPlacement.ON_LAND_SURFACE;
                lv2.airPocket = this.random.nextFloat() < 0.5f;
                lv2.mossiness = 0.8f;
                lv2.overgrown = true;
                lv2.vines = true;
            } else if (lv.portalType == Type.SWAMP) {
                RuinedPortalFeaturePiece.VerticalPlacement lv5 = RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR;
                lv2.airPocket = false;
                lv2.mossiness = 0.5f;
                lv2.vines = true;
            } else if (lv.portalType == Type.MOUNTAIN) {
                boolean bl = this.random.nextFloat() < 0.5f;
                RuinedPortalFeaturePiece.VerticalPlacement lv6 = bl ? RuinedPortalFeaturePiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalFeaturePiece.VerticalPlacement.ON_LAND_SURFACE;
                lv2.airPocket = bl || this.random.nextFloat() < 0.5f;
            } else if (lv.portalType == Type.OCEAN) {
                RuinedPortalFeaturePiece.VerticalPlacement lv7 = RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR;
                lv2.airPocket = false;
                lv2.mossiness = 0.8f;
            } else if (lv.portalType == Type.NETHER) {
                RuinedPortalFeaturePiece.VerticalPlacement lv8 = RuinedPortalFeaturePiece.VerticalPlacement.IN_NETHER;
                lv2.airPocket = this.random.nextFloat() < 0.5f;
                lv2.mossiness = 0.0f;
                lv2.replaceWithBlackstone = true;
            } else {
                boolean bl2 = this.random.nextFloat() < 0.5f;
                lv9 = bl2 ? RuinedPortalFeaturePiece.VerticalPlacement.UNDERGROUND : RuinedPortalFeaturePiece.VerticalPlacement.ON_LAND_SURFACE;
                boolean bl = lv2.airPocket = bl2 || this.random.nextFloat() < 0.5f;
            }
            if (this.random.nextFloat() < 0.05f) {
                Identifier lv10 = new Identifier(RARE_PORTAL_STRUCTURE_IDS[this.random.nextInt(RARE_PORTAL_STRUCTURE_IDS.length)]);
            } else {
                lv11 = new Identifier(COMMON_PORTAL_STRUCTURE_IDS[this.random.nextInt(COMMON_PORTAL_STRUCTURE_IDS.length)]);
            }
            Structure lv12 = arg2.getStructureOrBlank(lv11);
            BlockRotation lv13 = Util.getRandom(BlockRotation.values(), (Random)this.random);
            BlockMirror lv14 = this.random.nextFloat() < 0.5f ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
            BlockPos lv15 = new BlockPos(lv12.getSize().getX() / 2, 0, lv12.getSize().getZ() / 2);
            BlockPos lv16 = new ChunkPos(i, j).getCenterBlockPos();
            BlockBox lv17 = lv12.method_27267(lv16, lv13, lv15, lv14);
            Vec3i lv18 = lv17.getCenter();
            int k = lv18.getX();
            int l = lv18.getZ();
            int m = arg.getHeight(k, l, RuinedPortalFeaturePiece.getHeightmapType(lv9)) - 1;
            int n = RuinedPortalFeature.method_27211(this.random, arg, lv9, lv2.airPocket, m, lv17.getBlockCountY(), lv17);
            BlockPos lv19 = new BlockPos(lv16.getX(), n, lv16.getZ());
            if (lv.portalType == Type.MOUNTAIN || lv.portalType == Type.OCEAN || lv.portalType == Type.STANDARD) {
                lv2.cold = RuinedPortalFeature.method_27209(lv19, arg3);
            }
            this.children.add(new RuinedPortalFeaturePiece(lv19, lv9, lv2, lv11, lv12, lv13, lv14, lv15));
            this.setBoundingBoxFromChildren();
        }
    }
}

