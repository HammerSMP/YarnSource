/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class RuinedPortalFeature
extends StructureFeature<RuinedPortalFeatureConfig> {
    private static final String[] COMMON_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] RARE_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};

    public RuinedPortalFeature(Codec<RuinedPortalFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<RuinedPortalFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    private static boolean method_27209(BlockPos arg, Biome arg2) {
        return arg2.getTemperature(arg) < 0.15f;
    }

    private static int method_27211(Random random, ChunkGenerator arg, RuinedPortalStructurePiece.VerticalPlacement arg22, boolean bl, int i, int j, BlockBox arg3) {
        int t;
        if (arg22 == RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER) {
            if (bl) {
                int k = RuinedPortalFeature.choose(random, 32, 100);
            } else if (random.nextFloat() < 0.5f) {
                int l = RuinedPortalFeature.choose(random, 27, 29);
            } else {
                int m = RuinedPortalFeature.choose(random, 29, 100);
            }
        } else if (arg22 == RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN) {
            int n = i - j;
            int o = RuinedPortalFeature.choosePlacementHeight(random, 70, n);
        } else if (arg22 == RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND) {
            int p = i - j;
            int q = RuinedPortalFeature.choosePlacementHeight(random, 15, p);
        } else if (arg22 == RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED) {
            int r = i - j + RuinedPortalFeature.choose(random, 2, 8);
        } else {
            int s = i;
        }
        ImmutableList list = ImmutableList.of((Object)new BlockPos(arg3.minX, 0, arg3.minZ), (Object)new BlockPos(arg3.maxX, 0, arg3.minZ), (Object)new BlockPos(arg3.minX, 0, arg3.maxZ), (Object)new BlockPos(arg3.maxX, 0, arg3.maxZ));
        List list2 = list.stream().map(arg2 -> arg.getColumnSample(arg2.getX(), arg2.getZ())).collect(Collectors.toList());
        Heightmap.Type lv = arg22 == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        block0: for (t = s; t > 15; --t) {
            int u = 0;
            lv2.set(0, t, 0);
            for (BlockView lv3 : list2) {
                BlockState lv4 = lv3.getBlockState(lv2);
                if (lv4 == null || !lv.getBlockPredicate().test(lv4) || ++u != 3) continue;
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

    public static enum Type implements StringIdentifiable
    {
        STANDARD("standard"),
        DESERT("desert"),
        JUNGLE("jungle"),
        SWAMP("swamp"),
        MOUNTAIN("mountain"),
        OCEAN("ocean"),
        NETHER("nether");

        public static final Codec<Type> field_24840;
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

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24840 = StringIdentifiable.createCodec(Type::values, Type::byName);
            BY_NAME = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, arg -> arg));
        }
    }

    public static class Start
    extends StructureStart<RuinedPortalFeatureConfig> {
        protected Start(StructureFeature<RuinedPortalFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator arg, StructureManager arg2, int i, int j, Biome arg3, RuinedPortalFeatureConfig arg4) {
            Identifier lv10;
            RuinedPortalStructurePiece.VerticalPlacement lv8;
            RuinedPortalStructurePiece.Properties lv = new RuinedPortalStructurePiece.Properties();
            if (arg4.portalType == Type.DESERT) {
                RuinedPortalStructurePiece.VerticalPlacement lv2 = RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED;
                lv.airPocket = false;
                lv.mossiness = 0.0f;
            } else if (arg4.portalType == Type.JUNGLE) {
                RuinedPortalStructurePiece.VerticalPlacement lv3 = RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
                lv.airPocket = this.random.nextFloat() < 0.5f;
                lv.mossiness = 0.8f;
                lv.overgrown = true;
                lv.vines = true;
            } else if (arg4.portalType == Type.SWAMP) {
                RuinedPortalStructurePiece.VerticalPlacement lv4 = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
                lv.airPocket = false;
                lv.mossiness = 0.5f;
                lv.vines = true;
            } else if (arg4.portalType == Type.MOUNTAIN) {
                boolean bl = this.random.nextFloat() < 0.5f;
                RuinedPortalStructurePiece.VerticalPlacement lv5 = bl ? RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
                lv.airPocket = bl || this.random.nextFloat() < 0.5f;
            } else if (arg4.portalType == Type.OCEAN) {
                RuinedPortalStructurePiece.VerticalPlacement lv6 = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
                lv.airPocket = false;
                lv.mossiness = 0.8f;
            } else if (arg4.portalType == Type.NETHER) {
                RuinedPortalStructurePiece.VerticalPlacement lv7 = RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER;
                lv.airPocket = this.random.nextFloat() < 0.5f;
                lv.mossiness = 0.0f;
                lv.replaceWithBlackstone = true;
            } else {
                boolean bl2 = this.random.nextFloat() < 0.5f;
                lv8 = bl2 ? RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
                boolean bl = lv.airPocket = bl2 || this.random.nextFloat() < 0.5f;
            }
            if (this.random.nextFloat() < 0.05f) {
                Identifier lv9 = new Identifier(RARE_PORTAL_STRUCTURE_IDS[this.random.nextInt(RARE_PORTAL_STRUCTURE_IDS.length)]);
            } else {
                lv10 = new Identifier(COMMON_PORTAL_STRUCTURE_IDS[this.random.nextInt(COMMON_PORTAL_STRUCTURE_IDS.length)]);
            }
            Structure lv11 = arg2.getStructureOrBlank(lv10);
            BlockRotation lv12 = Util.getRandom(BlockRotation.values(), (Random)this.random);
            BlockMirror lv13 = this.random.nextFloat() < 0.5f ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
            BlockPos lv14 = new BlockPos(lv11.getSize().getX() / 2, 0, lv11.getSize().getZ() / 2);
            BlockPos lv15 = new ChunkPos(i, j).getCenterBlockPos();
            BlockBox lv16 = lv11.method_27267(lv15, lv12, lv14, lv13);
            Vec3i lv17 = lv16.getCenter();
            int k = lv17.getX();
            int l = lv17.getZ();
            int m = arg.getHeight(k, l, RuinedPortalStructurePiece.getHeightmapType(lv8)) - 1;
            int n = RuinedPortalFeature.method_27211(this.random, arg, lv8, lv.airPocket, m, lv16.getBlockCountY(), lv16);
            BlockPos lv18 = new BlockPos(lv15.getX(), n, lv15.getZ());
            if (arg4.portalType == Type.MOUNTAIN || arg4.portalType == Type.OCEAN || arg4.portalType == Type.STANDARD) {
                lv.cold = RuinedPortalFeature.method_27209(lv18, arg3);
            }
            this.children.add(new RuinedPortalStructurePiece(lv18, lv8, lv, lv10, lv11, lv12, lv13, lv14));
            this.setBoundingBoxFromChildren();
        }
    }
}

