/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.JigsawBlock;
import net.minecraft.class_5455;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePoolBasedGenerator {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void method_30419(class_5455 arg, StructurePoolFeatureConfig arg2, PieceFactory arg3, ChunkGenerator arg4, StructureManager arg5, BlockPos arg6, List<? super PoolStructurePiece> list, Random random, boolean bl, boolean bl2) {
        int l;
        StructureFeature.method_28664();
        MutableRegistry<StructurePool> lv = arg.method_30530(Registry.TEMPLATE_POOL_WORLDGEN);
        BlockRotation lv2 = BlockRotation.random(random);
        StructurePool lv3 = arg2.getStartPool().get();
        StructurePoolElement lv4 = lv3.getRandomElement(random);
        PoolStructurePiece lv5 = arg3.create(arg5, lv4, arg6, lv4.getGroundLevelDelta(), lv2, lv4.getBoundingBox(arg5, arg6, lv2));
        BlockBox lv6 = lv5.getBoundingBox();
        int i = (lv6.maxX + lv6.minX) / 2;
        int j = (lv6.maxZ + lv6.minZ) / 2;
        if (bl2) {
            int k = arg6.getY() + arg4.getHeightOnGround(i, j, Heightmap.Type.WORLD_SURFACE_WG);
        } else {
            l = arg6.getY();
        }
        int m = lv6.minY + lv5.getGroundLevelDelta();
        lv5.translate(0, l - m, 0);
        list.add(lv5);
        if (arg2.getSize() <= 0) {
            return;
        }
        int n = 80;
        Box lv7 = new Box(i - 80, l - 80, j - 80, i + 80 + 1, l + 80 + 1, j + 80 + 1);
        StructurePoolGenerator lv8 = new StructurePoolGenerator(lv, arg2.getSize(), arg3, arg4, arg5, list, random);
        lv8.structurePieces.addLast(new ShapedPoolStructurePiece(lv5, new MutableObject((Object)VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(lv7), VoxelShapes.cuboid(Box.from(lv6)), BooleanBiFunction.ONLY_FIRST)), l + 80, 0));
        while (!lv8.structurePieces.isEmpty()) {
            ShapedPoolStructurePiece lv9 = (ShapedPoolStructurePiece)lv8.structurePieces.removeFirst();
            lv8.generatePiece(lv9.piece, (MutableObject<VoxelShape>)lv9.pieceShape, lv9.minY, lv9.currentSize, bl);
        }
    }

    public static void method_27230(class_5455 arg, PoolStructurePiece arg2, int i, PieceFactory arg3, ChunkGenerator arg4, StructureManager arg5, List<? super PoolStructurePiece> list, Random random) {
        MutableRegistry<StructurePool> lv = arg.method_30530(Registry.TEMPLATE_POOL_WORLDGEN);
        StructurePoolGenerator lv2 = new StructurePoolGenerator(lv, i, arg3, arg4, arg5, list, random);
        lv2.structurePieces.addLast(new ShapedPoolStructurePiece(arg2, new MutableObject((Object)VoxelShapes.UNBOUNDED), 0, 0));
        while (!lv2.structurePieces.isEmpty()) {
            ShapedPoolStructurePiece lv3 = (ShapedPoolStructurePiece)lv2.structurePieces.removeFirst();
            lv2.generatePiece(lv3.piece, (MutableObject<VoxelShape>)lv3.pieceShape, lv3.minY, lv3.currentSize, false);
        }
    }

    public static interface PieceFactory {
        public PoolStructurePiece create(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, BlockRotation var5, BlockBox var6);
    }

    static final class StructurePoolGenerator {
        private final Registry<StructurePool> field_25852;
        private final int maxSize;
        private final PieceFactory pieceFactory;
        private final ChunkGenerator chunkGenerator;
        private final StructureManager structureManager;
        private final List<? super PoolStructurePiece> children;
        private final Random random;
        private final Deque<ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

        private StructurePoolGenerator(Registry<StructurePool> arg, int i, PieceFactory arg2, ChunkGenerator arg3, StructureManager arg4, List<? super PoolStructurePiece> list, Random random) {
            this.field_25852 = arg;
            this.maxSize = i;
            this.pieceFactory = arg2;
            this.chunkGenerator = arg3;
            this.structureManager = arg4;
            this.children = list;
            this.random = random;
        }

        private void generatePiece(PoolStructurePiece piece, MutableObject<VoxelShape> mutableObject, int minY, int currentSize, boolean bl) {
            StructurePoolElement lv = piece.getPoolElement();
            BlockPos lv2 = piece.getPos();
            BlockRotation lv3 = piece.getRotation();
            StructurePool.Projection lv4 = lv.getProjection();
            boolean bl2 = lv4 == StructurePool.Projection.RIGID;
            MutableObject mutableObject2 = new MutableObject();
            BlockBox lv5 = piece.getBoundingBox();
            int k = lv5.minY;
            block0: for (Structure.StructureBlockInfo lv6 : lv.getStructureBlockInfos(this.structureManager, lv2, lv3, this.random)) {
                StructurePoolElement lv12;
                int o;
                MutableObject<VoxelShape> mutableObject4;
                Direction lv7 = JigsawBlock.getFacing(lv6.state);
                BlockPos lv8 = lv6.pos;
                BlockPos lv9 = lv8.offset(lv7);
                int l = lv8.getY() - k;
                int m = -1;
                StructurePool lv10 = StructurePoolGenerator.method_30420(this.field_25852, new Identifier(lv6.tag.getString("pool")));
                StructurePool lv11 = StructurePoolGenerator.method_30420(this.field_25852, lv10.getTerminatorsId());
                if (lv10 == TemplatePools.INVALID || lv10.getElementCount() == 0 && lv10 != TemplatePools.EMPTY) {
                    LOGGER.warn("Empty or none existent pool: {}", (Object)lv6.tag.getString("pool"));
                    continue;
                }
                boolean bl3 = lv5.contains(lv9);
                if (bl3) {
                    MutableObject mutableObject3 = mutableObject2;
                    int n = k;
                    if (mutableObject2.getValue() == null) {
                        mutableObject2.setValue((Object)VoxelShapes.cuboid(Box.from(lv5)));
                    }
                } else {
                    mutableObject4 = mutableObject;
                    o = minY;
                }
                ArrayList list = Lists.newArrayList();
                if (currentSize != this.maxSize) {
                    list.addAll(lv10.getElementIndicesInRandomOrder(this.random));
                }
                list.addAll(lv11.getElementIndicesInRandomOrder(this.random));
                Iterator iterator = list.iterator();
                while (iterator.hasNext() && (lv12 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                    for (BlockRotation lv13 : BlockRotation.randomRotationOrder(this.random)) {
                        int q;
                        List<Structure.StructureBlockInfo> list2 = lv12.getStructureBlockInfos(this.structureManager, BlockPos.ORIGIN, lv13, this.random);
                        BlockBox lv14 = lv12.getBoundingBox(this.structureManager, BlockPos.ORIGIN, lv13);
                        if (!bl || lv14.getBlockCountY() > 16) {
                            boolean p = false;
                        } else {
                            q = list2.stream().mapToInt(arg2 -> {
                                if (!lv14.contains(arg2.pos.offset(JigsawBlock.getFacing(arg2.state)))) {
                                    return 0;
                                }
                                Identifier lv = new Identifier(arg2.tag.getString("pool"));
                                StructurePool lv2 = StructurePoolGenerator.method_30420(this.field_25852, lv);
                                StructurePool lv3 = StructurePoolGenerator.method_30420(this.field_25852, lv2.getTerminatorsId());
                                return Math.max(lv2.getHighestY(this.structureManager), lv3.getHighestY(this.structureManager));
                            }).max().orElse(0);
                        }
                        for (Structure.StructureBlockInfo lv15 : list2) {
                            int ad;
                            int aa;
                            int v;
                            if (!JigsawBlock.attachmentMatches(lv6, lv15)) continue;
                            BlockPos lv16 = lv15.pos;
                            BlockPos lv17 = new BlockPos(lv9.getX() - lv16.getX(), lv9.getY() - lv16.getY(), lv9.getZ() - lv16.getZ());
                            BlockBox lv18 = lv12.getBoundingBox(this.structureManager, lv17, lv13);
                            int r = lv18.minY;
                            StructurePool.Projection lv19 = lv12.getProjection();
                            boolean bl4 = lv19 == StructurePool.Projection.RIGID;
                            int s = lv16.getY();
                            int t = l - s + JigsawBlock.getFacing(lv6.state).getOffsetY();
                            if (bl2 && bl4) {
                                int u = k + t;
                            } else {
                                if (m == -1) {
                                    m = this.chunkGenerator.getHeightOnGround(lv8.getX(), lv8.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                }
                                v = m - s;
                            }
                            int w = v - r;
                            BlockBox lv20 = lv18.translated(0, w, 0);
                            BlockPos lv21 = lv17.add(0, w, 0);
                            if (q > 0) {
                                int x = Math.max(q + 1, lv20.maxY - lv20.minY);
                                lv20.maxY = lv20.minY + x;
                            }
                            if (VoxelShapes.matchesAnywhere((VoxelShape)mutableObject4.getValue(), VoxelShapes.cuboid(Box.from(lv20).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) continue;
                            mutableObject4.setValue((Object)VoxelShapes.combine((VoxelShape)mutableObject4.getValue(), VoxelShapes.cuboid(Box.from(lv20)), BooleanBiFunction.ONLY_FIRST));
                            int y = piece.getGroundLevelDelta();
                            if (bl4) {
                                int z = y - t;
                            } else {
                                aa = lv12.getGroundLevelDelta();
                            }
                            PoolStructurePiece lv22 = this.pieceFactory.create(this.structureManager, lv12, lv21, aa, lv13, lv20);
                            if (bl2) {
                                int ab = k + l;
                            } else if (bl4) {
                                int ac = v + s;
                            } else {
                                if (m == -1) {
                                    m = this.chunkGenerator.getHeightOnGround(lv8.getX(), lv8.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                }
                                ad = m + t / 2;
                            }
                            piece.addJunction(new JigsawJunction(lv9.getX(), (int)(ad - l + y), lv9.getZ(), t, lv19));
                            lv22.addJunction(new JigsawJunction(lv8.getX(), ad - s + aa, lv8.getZ(), -t, lv4));
                            this.children.add(lv22);
                            if (currentSize + 1 > this.maxSize) continue block0;
                            this.structurePieces.addLast(new ShapedPoolStructurePiece(lv22, mutableObject4, o, currentSize + 1));
                            continue block0;
                        }
                    }
                }
            }
        }

        private static StructurePool method_30420(Registry<StructurePool> arg, Identifier arg2) {
            return Optional.ofNullable(arg.get(arg2)).orElse(TemplatePools.INVALID);
        }
    }

    static final class ShapedPoolStructurePiece {
        private final PoolStructurePiece piece;
        private final MutableObject<VoxelShape> pieceShape;
        private final int minY;
        private final int currentSize;

        private ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject<VoxelShape> mutableObject, int minY, int currentSize) {
            this.piece = piece;
            this.pieceShape = mutableObject;
            this.minY = minY;
            this.currentSize = currentSize;
        }
    }
}

