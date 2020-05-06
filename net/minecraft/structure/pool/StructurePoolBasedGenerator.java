/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
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
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.JigsawBlock;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolRegistry;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePoolBasedGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructurePoolRegistry REGISTRY = new StructurePoolRegistry();

    public static void init() {
        BastionRemnantGenerator.init();
        VillageGenerator.init();
        PillagerOutpostGenerator.init();
    }

    public static void addPieces(Identifier arg, int i, PieceFactory arg2, ChunkGenerator<?> arg3, StructureManager arg4, BlockPos arg5, List<? super PoolStructurePiece> list, Random random, boolean bl, boolean bl2) {
        int m;
        StructureFeatures.initialize();
        BlockRotation lv = BlockRotation.random(random);
        StructurePool lv2 = REGISTRY.get(arg);
        StructurePoolElement lv3 = lv2.getRandomElement(random);
        PoolStructurePiece lv4 = arg2.create(arg4, lv3, arg5, lv3.getGroundLevelDelta(), lv, lv3.getBoundingBox(arg4, arg5, lv));
        BlockBox lv5 = lv4.getBoundingBox();
        int j = (lv5.maxX + lv5.minX) / 2;
        int k = (lv5.maxZ + lv5.minZ) / 2;
        if (bl2) {
            int l = arg5.getY() + arg3.getHeightOnGround(j, k, Heightmap.Type.WORLD_SURFACE_WG);
        } else {
            m = arg5.getY();
        }
        int n = lv5.minY + lv4.getGroundLevelDelta();
        lv4.translate(0, m - n, 0);
        list.add(lv4);
        if (i <= 0) {
            return;
        }
        int o = 80;
        Box lv6 = new Box(j - 80, m - 80, k - 80, j + 80 + 1, m + 80 + 1, k + 80 + 1);
        StructurePoolGenerator lv7 = new StructurePoolGenerator(i, arg2, arg3, arg4, list, random);
        lv7.structurePieces.addLast(new ShapedPoolStructurePiece(lv4, new AtomicReference<VoxelShape>(VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(lv6), VoxelShapes.cuboid(Box.from(lv5)), BooleanBiFunction.ONLY_FIRST)), m + 80, 0));
        while (!lv7.structurePieces.isEmpty()) {
            ShapedPoolStructurePiece lv8 = (ShapedPoolStructurePiece)lv7.structurePieces.removeFirst();
            lv7.generatePiece(lv8.piece, lv8.pieceShape, lv8.minY, lv8.currentSize, bl);
        }
    }

    public static void method_27230(PoolStructurePiece arg, int i, PieceFactory arg2, ChunkGenerator<?> arg3, StructureManager arg4, List<? super PoolStructurePiece> list, Random random) {
        StructurePoolBasedGenerator.init();
        StructurePoolGenerator lv = new StructurePoolGenerator(i, arg2, arg3, arg4, list, random);
        lv.structurePieces.addLast(new ShapedPoolStructurePiece(arg, new AtomicReference<VoxelShape>(VoxelShapes.UNBOUNDED), 0, 0));
        while (!lv.structurePieces.isEmpty()) {
            ShapedPoolStructurePiece lv2 = (ShapedPoolStructurePiece)lv.structurePieces.removeFirst();
            lv.generatePiece(lv2.piece, lv2.pieceShape, lv2.minY, lv2.currentSize, false);
        }
    }

    static {
        REGISTRY.add(StructurePool.EMPTY);
    }

    public static interface PieceFactory {
        public PoolStructurePiece create(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, BlockRotation var5, BlockBox var6);
    }

    static final class StructurePoolGenerator {
        private final int maxSize;
        private final PieceFactory pieceFactory;
        private final ChunkGenerator<?> chunkGenerator;
        private final StructureManager structureManager;
        private final List<? super PoolStructurePiece> children;
        private final Random random;
        private final Deque<ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

        private StructurePoolGenerator(int i, PieceFactory arg, ChunkGenerator<?> arg2, StructureManager arg3, List<? super PoolStructurePiece> list, Random random) {
            this.maxSize = i;
            this.pieceFactory = arg;
            this.chunkGenerator = arg2;
            this.structureManager = arg3;
            this.children = list;
            this.random = random;
        }

        private void generatePiece(PoolStructurePiece arg, AtomicReference<VoxelShape> atomicReference, int i, int j, boolean bl) {
            StructurePoolElement lv = arg.getPoolElement();
            BlockPos lv2 = arg.getPos();
            BlockRotation lv3 = arg.getRotation();
            StructurePool.Projection lv4 = lv.getProjection();
            boolean bl2 = lv4 == StructurePool.Projection.RIGID;
            AtomicReference<VoxelShape> atomicReference2 = new AtomicReference<VoxelShape>();
            BlockBox lv5 = arg.getBoundingBox();
            int k = lv5.minY;
            block0: for (Structure.StructureBlockInfo lv6 : lv.getStructureBlockInfos(this.structureManager, lv2, lv3, this.random)) {
                StructurePoolElement lv12;
                int o;
                AtomicReference<VoxelShape> atomicReference4;
                Direction lv7 = JigsawBlock.method_26378(lv6.state);
                BlockPos lv8 = lv6.pos;
                BlockPos lv9 = lv8.offset(lv7);
                int l = lv8.getY() - k;
                int m = -1;
                StructurePool lv10 = REGISTRY.get(new Identifier(lv6.tag.getString("pool")));
                StructurePool lv11 = REGISTRY.get(lv10.getTerminatorsId());
                if (lv10 == StructurePool.INVALID || lv10.getElementCount() == 0 && lv10 != StructurePool.EMPTY) {
                    LOGGER.warn("Empty or none existent pool: {}", (Object)lv6.tag.getString("pool"));
                    continue;
                }
                boolean bl3 = lv5.contains(lv9);
                if (bl3) {
                    AtomicReference<VoxelShape> atomicReference3 = atomicReference2;
                    int n = k;
                    if (atomicReference2.get() == null) {
                        atomicReference2.set(VoxelShapes.cuboid(Box.from(lv5)));
                    }
                } else {
                    atomicReference4 = atomicReference;
                    o = i;
                }
                ArrayList list = Lists.newArrayList();
                if (j != this.maxSize) {
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
                                if (!lv14.contains(arg2.pos.offset(JigsawBlock.method_26378(arg2.state)))) {
                                    return 0;
                                }
                                Identifier lv = new Identifier(arg2.tag.getString("pool"));
                                StructurePool lv2 = REGISTRY.get(lv);
                                StructurePool lv3 = REGISTRY.get(lv2.getTerminatorsId());
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
                            int t = l - s + JigsawBlock.method_26378(lv6.state).getOffsetY();
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
                            if (VoxelShapes.matchesAnywhere(atomicReference4.get(), VoxelShapes.cuboid(Box.from(lv20).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) continue;
                            atomicReference4.set(VoxelShapes.combine(atomicReference4.get(), VoxelShapes.cuboid(Box.from(lv20)), BooleanBiFunction.ONLY_FIRST));
                            int y = arg.getGroundLevelDelta();
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
                            arg.addJunction(new JigsawJunction(lv9.getX(), (int)(ad - l + y), lv9.getZ(), t, lv19));
                            lv22.addJunction(new JigsawJunction(lv8.getX(), ad - s + aa, lv8.getZ(), -t, lv4));
                            this.children.add(lv22);
                            if (j + 1 > this.maxSize) continue block0;
                            this.structurePieces.addLast(new ShapedPoolStructurePiece(lv22, atomicReference4, o, j + 1));
                            continue block0;
                        }
                    }
                }
            }
        }
    }

    static final class ShapedPoolStructurePiece {
        private final PoolStructurePiece piece;
        private final AtomicReference<VoxelShape> pieceShape;
        private final int minY;
        private final int currentSize;

        private ShapedPoolStructurePiece(PoolStructurePiece arg, AtomicReference<VoxelShape> atomicReference, int i, int j) {
            this.piece = arg;
            this.pieceShape = atomicReference;
            this.minY = i;
            this.currentSize = j;
        }
    }
}

