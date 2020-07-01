/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.class_5425;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WoodlandMansionGenerator {
    public static void addPieces(StructureManager arg, BlockPos arg2, BlockRotation arg3, List<Piece> list, Random random) {
        MansionParameters lv = new MansionParameters(random);
        LayoutGenerator lv2 = new LayoutGenerator(arg, random);
        lv2.generate(arg2, arg3, list, lv);
    }

    static class ThirdFloorRoomPool
    extends SecondFloorRoomPool {
        private ThirdFloorRoomPool() {
        }
    }

    static class SecondFloorRoomPool
    extends RoomPool {
        private SecondFloorRoomPool() {
        }

        @Override
        public String getSmallRoom(Random random) {
            return "1x1_b" + (random.nextInt(4) + 1);
        }

        @Override
        public String getSmallSecretRoom(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumFunctionalRoom(Random random, boolean bl) {
            if (bl) {
                return "1x2_c_stairs";
            }
            return "1x2_c" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumGenericRoom(Random random, boolean bl) {
            if (bl) {
                return "1x2_d_stairs";
            }
            return "1x2_d" + (random.nextInt(5) + 1);
        }

        @Override
        public String getMediumSecretRoom(Random random) {
            return "1x2_se" + (random.nextInt(1) + 1);
        }

        @Override
        public String getBigRoom(Random random) {
            return "2x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String getBigSecretRoom(Random random) {
            return "2x2_s1";
        }
    }

    static class FirstFloorRoomPool
    extends RoomPool {
        private FirstFloorRoomPool() {
        }

        @Override
        public String getSmallRoom(Random random) {
            return "1x1_a" + (random.nextInt(5) + 1);
        }

        @Override
        public String getSmallSecretRoom(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumFunctionalRoom(Random random, boolean bl) {
            return "1x2_a" + (random.nextInt(9) + 1);
        }

        @Override
        public String getMediumGenericRoom(Random random, boolean bl) {
            return "1x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String getMediumSecretRoom(Random random) {
            return "1x2_s" + (random.nextInt(2) + 1);
        }

        @Override
        public String getBigRoom(Random random) {
            return "2x2_a" + (random.nextInt(4) + 1);
        }

        @Override
        public String getBigSecretRoom(Random random) {
            return "2x2_s1";
        }
    }

    static abstract class RoomPool {
        private RoomPool() {
        }

        public abstract String getSmallRoom(Random var1);

        public abstract String getSmallSecretRoom(Random var1);

        public abstract String getMediumFunctionalRoom(Random var1, boolean var2);

        public abstract String getMediumGenericRoom(Random var1, boolean var2);

        public abstract String getMediumSecretRoom(Random var1);

        public abstract String getBigRoom(Random var1);

        public abstract String getBigSecretRoom(Random var1);
    }

    static class FlagMatrix {
        private final int[][] array;
        private final int n;
        private final int m;
        private final int fallback;

        public FlagMatrix(int i, int j, int k) {
            this.n = i;
            this.m = j;
            this.fallback = k;
            this.array = new int[i][j];
        }

        public void set(int i, int j, int k) {
            if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
                this.array[i][j] = k;
            }
        }

        public void fill(int i, int j, int k, int l, int m) {
            for (int n = j; n <= l; ++n) {
                for (int o = i; o <= k; ++o) {
                    this.set(o, n, m);
                }
            }
        }

        public int get(int i, int j) {
            if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
                return this.array[i][j];
            }
            return this.fallback;
        }

        public void update(int i, int j, int k, int l) {
            if (this.get(i, j) == k) {
                this.set(i, j, l);
            }
        }

        public boolean anyMatchAround(int i, int j, int k) {
            return this.get(i - 1, j) == k || this.get(i + 1, j) == k || this.get(i, j + 1) == k || this.get(i, j - 1) == k;
        }
    }

    static class MansionParameters {
        private final Random random;
        private final FlagMatrix field_15440;
        private final FlagMatrix field_15439;
        private final FlagMatrix[] field_15443;
        private final int field_15442;
        private final int field_15441;

        public MansionParameters(Random random) {
            this.random = random;
            int i = 11;
            this.field_15442 = 7;
            this.field_15441 = 4;
            this.field_15440 = new FlagMatrix(11, 11, 5);
            this.field_15440.fill(this.field_15442, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 3);
            this.field_15440.fill(this.field_15442 - 1, this.field_15441, this.field_15442 - 1, this.field_15441 + 1, 2);
            this.field_15440.fill(this.field_15442 + 2, this.field_15441 - 2, this.field_15442 + 3, this.field_15441 + 3, 5);
            this.field_15440.fill(this.field_15442 + 1, this.field_15441 - 2, this.field_15442 + 1, this.field_15441 - 1, 1);
            this.field_15440.fill(this.field_15442 + 1, this.field_15441 + 2, this.field_15442 + 1, this.field_15441 + 3, 1);
            this.field_15440.set(this.field_15442 - 1, this.field_15441 - 1, 1);
            this.field_15440.set(this.field_15442 - 1, this.field_15441 + 2, 1);
            this.field_15440.fill(0, 0, 11, 1, 5);
            this.field_15440.fill(0, 9, 11, 11, 5);
            this.method_15045(this.field_15440, this.field_15442, this.field_15441 - 2, Direction.WEST, 6);
            this.method_15045(this.field_15440, this.field_15442, this.field_15441 + 3, Direction.WEST, 6);
            this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 - 1, Direction.WEST, 3);
            this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 + 2, Direction.WEST, 3);
            while (this.method_15046(this.field_15440)) {
            }
            this.field_15443 = new FlagMatrix[3];
            this.field_15443[0] = new FlagMatrix(11, 11, 5);
            this.field_15443[1] = new FlagMatrix(11, 11, 5);
            this.field_15443[2] = new FlagMatrix(11, 11, 5);
            this.method_15042(this.field_15440, this.field_15443[0]);
            this.method_15042(this.field_15440, this.field_15443[1]);
            this.field_15443[0].fill(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 0x800000);
            this.field_15443[1].fill(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 0x800000);
            this.field_15439 = new FlagMatrix(this.field_15440.n, this.field_15440.m, 5);
            this.method_15048();
            this.method_15042(this.field_15439, this.field_15443[2]);
        }

        public static boolean method_15047(FlagMatrix arg, int i, int j) {
            int k = arg.get(i, j);
            return k == 1 || k == 2 || k == 3 || k == 4;
        }

        public boolean method_15039(FlagMatrix arg, int i, int j, int k, int l) {
            return (this.field_15443[k].get(i, j) & 0xFFFF) == l;
        }

        @Nullable
        public Direction method_15040(FlagMatrix arg, int i, int j, int k, int l) {
            for (Direction lv : Direction.Type.HORIZONTAL) {
                if (!this.method_15039(arg, i + lv.getOffsetX(), j + lv.getOffsetZ(), k, l)) continue;
                return lv;
            }
            return null;
        }

        private void method_15045(FlagMatrix arg, int i, int j, Direction arg2, int k) {
            if (k <= 0) {
                return;
            }
            arg.set(i, j, 1);
            arg.update(i + arg2.getOffsetX(), j + arg2.getOffsetZ(), 0, 1);
            for (int l = 0; l < 8; ++l) {
                Direction lv = Direction.fromHorizontal(this.random.nextInt(4));
                if (lv == arg2.getOpposite() || lv == Direction.EAST && this.random.nextBoolean()) continue;
                int m = i + arg2.getOffsetX();
                int n = j + arg2.getOffsetZ();
                if (arg.get(m + lv.getOffsetX(), n + lv.getOffsetZ()) != 0 || arg.get(m + lv.getOffsetX() * 2, n + lv.getOffsetZ() * 2) != 0) continue;
                this.method_15045(arg, i + arg2.getOffsetX() + lv.getOffsetX(), j + arg2.getOffsetZ() + lv.getOffsetZ(), lv, k - 1);
                break;
            }
            Direction lv2 = arg2.rotateYClockwise();
            Direction lv3 = arg2.rotateYCounterclockwise();
            arg.update(i + lv2.getOffsetX(), j + lv2.getOffsetZ(), 0, 2);
            arg.update(i + lv3.getOffsetX(), j + lv3.getOffsetZ(), 0, 2);
            arg.update(i + arg2.getOffsetX() + lv2.getOffsetX(), j + arg2.getOffsetZ() + lv2.getOffsetZ(), 0, 2);
            arg.update(i + arg2.getOffsetX() + lv3.getOffsetX(), j + arg2.getOffsetZ() + lv3.getOffsetZ(), 0, 2);
            arg.update(i + arg2.getOffsetX() * 2, j + arg2.getOffsetZ() * 2, 0, 2);
            arg.update(i + lv2.getOffsetX() * 2, j + lv2.getOffsetZ() * 2, 0, 2);
            arg.update(i + lv3.getOffsetX() * 2, j + lv3.getOffsetZ() * 2, 0, 2);
        }

        private boolean method_15046(FlagMatrix arg) {
            boolean bl = false;
            for (int i = 0; i < arg.m; ++i) {
                for (int j = 0; j < arg.n; ++j) {
                    if (arg.get(j, i) != 0) continue;
                    int k = 0;
                    k += MansionParameters.method_15047(arg, j + 1, i) ? 1 : 0;
                    k += MansionParameters.method_15047(arg, j - 1, i) ? 1 : 0;
                    k += MansionParameters.method_15047(arg, j, i + 1) ? 1 : 0;
                    if ((k += MansionParameters.method_15047(arg, j, i - 1) ? 1 : 0) >= 3) {
                        arg.set(j, i, 2);
                        bl = true;
                        continue;
                    }
                    if (k != 2) continue;
                    int l = 0;
                    l += MansionParameters.method_15047(arg, j + 1, i + 1) ? 1 : 0;
                    l += MansionParameters.method_15047(arg, j - 1, i + 1) ? 1 : 0;
                    l += MansionParameters.method_15047(arg, j + 1, i - 1) ? 1 : 0;
                    if ((l += MansionParameters.method_15047(arg, j - 1, i - 1) ? 1 : 0) > 1) continue;
                    arg.set(j, i, 2);
                    bl = true;
                }
            }
            return bl;
        }

        private void method_15048() {
            ArrayList list = Lists.newArrayList();
            FlagMatrix lv = this.field_15443[1];
            for (int i = 0; i < this.field_15439.m; ++i) {
                for (int j = 0; j < this.field_15439.n; ++j) {
                    int k = lv.get(j, i);
                    int l = k & 0xF0000;
                    if (l != 131072 || (k & 0x200000) != 0x200000) continue;
                    list.add(new Pair<Integer, Integer>(j, i));
                }
            }
            if (list.isEmpty()) {
                this.field_15439.fill(0, 0, this.field_15439.n, this.field_15439.m, 5);
                return;
            }
            Pair lv2 = (Pair)list.get(this.random.nextInt(list.size()));
            int m = lv.get((Integer)lv2.getLeft(), (Integer)lv2.getRight());
            lv.set((Integer)lv2.getLeft(), (Integer)lv2.getRight(), m | 0x400000);
            Direction lv3 = this.method_15040(this.field_15440, (Integer)lv2.getLeft(), (Integer)lv2.getRight(), 1, m & 0xFFFF);
            int n = (Integer)lv2.getLeft() + lv3.getOffsetX();
            int o = (Integer)lv2.getRight() + lv3.getOffsetZ();
            for (int p = 0; p < this.field_15439.m; ++p) {
                for (int q = 0; q < this.field_15439.n; ++q) {
                    if (!MansionParameters.method_15047(this.field_15440, q, p)) {
                        this.field_15439.set(q, p, 5);
                        continue;
                    }
                    if (q == (Integer)lv2.getLeft() && p == (Integer)lv2.getRight()) {
                        this.field_15439.set(q, p, 3);
                        continue;
                    }
                    if (q != n || p != o) continue;
                    this.field_15439.set(q, p, 3);
                    this.field_15443[2].set(q, p, 0x800000);
                }
            }
            ArrayList list2 = Lists.newArrayList();
            for (Direction lv4 : Direction.Type.HORIZONTAL) {
                if (this.field_15439.get(n + lv4.getOffsetX(), o + lv4.getOffsetZ()) != 0) continue;
                list2.add(lv4);
            }
            if (list2.isEmpty()) {
                this.field_15439.fill(0, 0, this.field_15439.n, this.field_15439.m, 5);
                lv.set((Integer)lv2.getLeft(), (Integer)lv2.getRight(), m);
                return;
            }
            Direction lv5 = (Direction)list2.get(this.random.nextInt(list2.size()));
            this.method_15045(this.field_15439, n + lv5.getOffsetX(), o + lv5.getOffsetZ(), lv5, 4);
            while (this.method_15046(this.field_15439)) {
            }
        }

        private void method_15042(FlagMatrix arg, FlagMatrix arg2) {
            ArrayList list = Lists.newArrayList();
            for (int i = 0; i < arg.m; ++i) {
                for (int j = 0; j < arg.n; ++j) {
                    if (arg.get(j, i) != 2) continue;
                    list.add(new Pair<Integer, Integer>(j, i));
                }
            }
            Collections.shuffle(list, this.random);
            int k = 10;
            for (Pair lv : list) {
                int m;
                int l = (Integer)lv.getLeft();
                if (arg2.get(l, m = ((Integer)lv.getRight()).intValue()) != 0) continue;
                int n = l;
                int o = l;
                int p = m;
                int q = m;
                int r = 65536;
                if (arg2.get(l + 1, m) == 0 && arg2.get(l, m + 1) == 0 && arg2.get(l + 1, m + 1) == 0 && arg.get(l + 1, m) == 2 && arg.get(l, m + 1) == 2 && arg.get(l + 1, m + 1) == 2) {
                    ++o;
                    ++q;
                    r = 262144;
                } else if (arg2.get(l - 1, m) == 0 && arg2.get(l, m + 1) == 0 && arg2.get(l - 1, m + 1) == 0 && arg.get(l - 1, m) == 2 && arg.get(l, m + 1) == 2 && arg.get(l - 1, m + 1) == 2) {
                    --n;
                    ++q;
                    r = 262144;
                } else if (arg2.get(l - 1, m) == 0 && arg2.get(l, m - 1) == 0 && arg2.get(l - 1, m - 1) == 0 && arg.get(l - 1, m) == 2 && arg.get(l, m - 1) == 2 && arg.get(l - 1, m - 1) == 2) {
                    --n;
                    --p;
                    r = 262144;
                } else if (arg2.get(l + 1, m) == 0 && arg.get(l + 1, m) == 2) {
                    ++o;
                    r = 131072;
                } else if (arg2.get(l, m + 1) == 0 && arg.get(l, m + 1) == 2) {
                    ++q;
                    r = 131072;
                } else if (arg2.get(l - 1, m) == 0 && arg.get(l - 1, m) == 2) {
                    --n;
                    r = 131072;
                } else if (arg2.get(l, m - 1) == 0 && arg.get(l, m - 1) == 2) {
                    --p;
                    r = 131072;
                }
                int s = this.random.nextBoolean() ? n : o;
                int t = this.random.nextBoolean() ? p : q;
                int u = 0x200000;
                if (!arg.anyMatchAround(s, t, 1)) {
                    s = s == n ? o : n;
                    int n2 = t = t == p ? q : p;
                    if (!arg.anyMatchAround(s, t, 1)) {
                        int n3 = t = t == p ? q : p;
                        if (!arg.anyMatchAround(s, t, 1)) {
                            s = s == n ? o : n;
                            int n4 = t = t == p ? q : p;
                            if (!arg.anyMatchAround(s, t, 1)) {
                                u = 0;
                                s = n;
                                t = p;
                            }
                        }
                    }
                }
                for (int v = p; v <= q; ++v) {
                    for (int w = n; w <= o; ++w) {
                        if (w == s && v == t) {
                            arg2.set(w, v, 0x100000 | u | r | k);
                            continue;
                        }
                        arg2.set(w, v, r | k);
                    }
                }
                ++k;
            }
        }
    }

    static class LayoutGenerator {
        private final StructureManager manager;
        private final Random random;
        private int field_15446;
        private int field_15445;

        public LayoutGenerator(StructureManager arg, Random random) {
            this.manager = arg;
            this.random = random;
        }

        public void generate(BlockPos arg, BlockRotation arg2, List<Piece> list, MansionParameters arg3) {
            GenerationPiece lv = new GenerationPiece();
            lv.position = arg;
            lv.rotation = arg2;
            lv.template = "wall_flat";
            GenerationPiece lv2 = new GenerationPiece();
            this.addEntrance(list, lv);
            lv2.position = lv.position.up(8);
            lv2.rotation = lv.rotation;
            lv2.template = "wall_window";
            if (!list.isEmpty()) {
                // empty if block
            }
            FlagMatrix lv3 = arg3.field_15440;
            FlagMatrix lv4 = arg3.field_15439;
            this.field_15446 = arg3.field_15442 + 1;
            this.field_15445 = arg3.field_15441 + 1;
            int i = arg3.field_15442 + 1;
            int j = arg3.field_15441;
            this.addRoof(list, lv, lv3, Direction.SOUTH, this.field_15446, this.field_15445, i, j);
            this.addRoof(list, lv2, lv3, Direction.SOUTH, this.field_15446, this.field_15445, i, j);
            GenerationPiece lv5 = new GenerationPiece();
            lv5.position = lv.position.up(19);
            lv5.rotation = lv.rotation;
            lv5.template = "wall_window";
            boolean bl = false;
            for (int k = 0; k < lv4.m && !bl; ++k) {
                for (int l = lv4.n - 1; l >= 0 && !bl; --l) {
                    if (!MansionParameters.method_15047(lv4, l, k)) continue;
                    lv5.position = lv5.position.offset(arg2.rotate(Direction.SOUTH), 8 + (k - this.field_15445) * 8);
                    lv5.position = lv5.position.offset(arg2.rotate(Direction.EAST), (l - this.field_15446) * 8);
                    this.method_15052(list, lv5);
                    this.addRoof(list, lv5, lv4, Direction.SOUTH, l, k, l, k);
                    bl = true;
                }
            }
            this.method_15055(list, arg.up(16), arg2, lv3, lv4);
            this.method_15055(list, arg.up(27), arg2, lv4, null);
            if (!list.isEmpty()) {
                // empty if block
            }
            RoomPool[] lvs = new RoomPool[]{new FirstFloorRoomPool(), new SecondFloorRoomPool(), new ThirdFloorRoomPool()};
            for (int m = 0; m < 3; ++m) {
                BlockPos lv6 = arg.up(8 * m + (m == 2 ? 3 : 0));
                FlagMatrix lv7 = arg3.field_15443[m];
                FlagMatrix lv8 = m == 2 ? lv4 : lv3;
                String string = m == 0 ? "carpet_south_1" : "carpet_south_2";
                String string2 = m == 0 ? "carpet_west_1" : "carpet_west_2";
                for (int n = 0; n < lv8.m; ++n) {
                    for (int o = 0; o < lv8.n; ++o) {
                        if (lv8.get(o, n) != 1) continue;
                        BlockPos lv9 = lv6.offset(arg2.rotate(Direction.SOUTH), 8 + (n - this.field_15445) * 8);
                        lv9 = lv9.offset(arg2.rotate(Direction.EAST), (o - this.field_15446) * 8);
                        list.add(new Piece(this.manager, "corridor_floor", lv9, arg2));
                        if (lv8.get(o, n - 1) == 1 || (lv7.get(o, n - 1) & 0x800000) == 0x800000) {
                            list.add(new Piece(this.manager, "carpet_north", lv9.offset(arg2.rotate(Direction.EAST), 1).up(), arg2));
                        }
                        if (lv8.get(o + 1, n) == 1 || (lv7.get(o + 1, n) & 0x800000) == 0x800000) {
                            list.add(new Piece(this.manager, "carpet_east", lv9.offset(arg2.rotate(Direction.SOUTH), 1).offset(arg2.rotate(Direction.EAST), 5).up(), arg2));
                        }
                        if (lv8.get(o, n + 1) == 1 || (lv7.get(o, n + 1) & 0x800000) == 0x800000) {
                            list.add(new Piece(this.manager, string, lv9.offset(arg2.rotate(Direction.SOUTH), 5).offset(arg2.rotate(Direction.WEST), 1), arg2));
                        }
                        if (lv8.get(o - 1, n) != 1 && (lv7.get(o - 1, n) & 0x800000) != 0x800000) continue;
                        list.add(new Piece(this.manager, string2, lv9.offset(arg2.rotate(Direction.WEST), 1).offset(arg2.rotate(Direction.NORTH), 1), arg2));
                    }
                }
                String string3 = m == 0 ? "indoors_wall_1" : "indoors_wall_2";
                String string4 = m == 0 ? "indoors_door_1" : "indoors_door_2";
                ArrayList list2 = Lists.newArrayList();
                for (int p = 0; p < lv8.m; ++p) {
                    for (int q = 0; q < lv8.n; ++q) {
                        boolean bl2;
                        boolean bl3 = bl2 = m == 2 && lv8.get(q, p) == 3;
                        if (lv8.get(q, p) != 2 && !bl2) continue;
                        int r = lv7.get(q, p);
                        int s = r & 0xF0000;
                        int t = r & 0xFFFF;
                        bl2 = bl2 && (r & 0x800000) == 0x800000;
                        list2.clear();
                        if ((r & 0x200000) == 0x200000) {
                            for (Direction lv10 : Direction.Type.HORIZONTAL) {
                                if (lv8.get(q + lv10.getOffsetX(), p + lv10.getOffsetZ()) != 1) continue;
                                list2.add(lv10);
                            }
                        }
                        Direction lv11 = null;
                        if (!list2.isEmpty()) {
                            lv11 = (Direction)list2.get(this.random.nextInt(list2.size()));
                        } else if ((r & 0x100000) == 0x100000) {
                            lv11 = Direction.UP;
                        }
                        BlockPos lv12 = lv6.offset(arg2.rotate(Direction.SOUTH), 8 + (p - this.field_15445) * 8);
                        lv12 = lv12.offset(arg2.rotate(Direction.EAST), -1 + (q - this.field_15446) * 8);
                        if (MansionParameters.method_15047(lv8, q - 1, p) && !arg3.method_15039(lv8, q - 1, p, m, t)) {
                            list.add(new Piece(this.manager, lv11 == Direction.WEST ? string4 : string3, lv12, arg2));
                        }
                        if (lv8.get(q + 1, p) == 1 && !bl2) {
                            BlockPos lv13 = lv12.offset(arg2.rotate(Direction.EAST), 8);
                            list.add(new Piece(this.manager, lv11 == Direction.EAST ? string4 : string3, lv13, arg2));
                        }
                        if (MansionParameters.method_15047(lv8, q, p + 1) && !arg3.method_15039(lv8, q, p + 1, m, t)) {
                            BlockPos lv14 = lv12.offset(arg2.rotate(Direction.SOUTH), 7);
                            lv14 = lv14.offset(arg2.rotate(Direction.EAST), 7);
                            list.add(new Piece(this.manager, lv11 == Direction.SOUTH ? string4 : string3, lv14, arg2.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (lv8.get(q, p - 1) == 1 && !bl2) {
                            BlockPos lv15 = lv12.offset(arg2.rotate(Direction.NORTH), 1);
                            lv15 = lv15.offset(arg2.rotate(Direction.EAST), 7);
                            list.add(new Piece(this.manager, lv11 == Direction.NORTH ? string4 : string3, lv15, arg2.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (s == 65536) {
                            this.addSmallRoom(list, lv12, arg2, lv11, lvs[m]);
                            continue;
                        }
                        if (s == 131072 && lv11 != null) {
                            Direction lv16 = arg3.method_15040(lv8, q, p, m, t);
                            boolean bl32 = (r & 0x400000) == 0x400000;
                            this.addMediumRoom(list, lv12, arg2, lv16, lv11, lvs[m], bl32);
                            continue;
                        }
                        if (s == 262144 && lv11 != null && lv11 != Direction.UP) {
                            Direction lv17 = lv11.rotateYClockwise();
                            if (!arg3.method_15039(lv8, q + lv17.getOffsetX(), p + lv17.getOffsetZ(), m, t)) {
                                lv17 = lv17.getOpposite();
                            }
                            this.addBigRoom(list, lv12, arg2, lv17, lv11, lvs[m]);
                            continue;
                        }
                        if (s != 262144 || lv11 != Direction.UP) continue;
                        this.addBigSecretRoom(list, lv12, arg2, lvs[m]);
                    }
                }
            }
        }

        private void addRoof(List<Piece> list, GenerationPiece arg, FlagMatrix arg2, Direction arg3, int i, int j, int k, int l) {
            int m = i;
            int n = j;
            Direction lv = arg3;
            do {
                if (!MansionParameters.method_15047(arg2, m + arg3.getOffsetX(), n + arg3.getOffsetZ())) {
                    this.method_15058(list, arg);
                    arg3 = arg3.rotateYClockwise();
                    if (m == k && n == l && lv == arg3) continue;
                    this.method_15052(list, arg);
                    continue;
                }
                if (MansionParameters.method_15047(arg2, m + arg3.getOffsetX(), n + arg3.getOffsetZ()) && MansionParameters.method_15047(arg2, m + arg3.getOffsetX() + arg3.rotateYCounterclockwise().getOffsetX(), n + arg3.getOffsetZ() + arg3.rotateYCounterclockwise().getOffsetZ())) {
                    this.method_15060(list, arg);
                    m += arg3.getOffsetX();
                    n += arg3.getOffsetZ();
                    arg3 = arg3.rotateYCounterclockwise();
                    continue;
                }
                if ((m += arg3.getOffsetX()) == k && (n += arg3.getOffsetZ()) == l && lv == arg3) continue;
                this.method_15052(list, arg);
            } while (m != k || n != l || lv != arg3);
        }

        private void method_15055(List<Piece> list, BlockPos arg, BlockRotation arg2, FlagMatrix arg3, @Nullable FlagMatrix arg4) {
            for (int i = 0; i < arg3.m; ++i) {
                for (int j = 0; j < arg3.n; ++j) {
                    boolean bl;
                    BlockPos lv = arg;
                    lv = lv.offset(arg2.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
                    lv = lv.offset(arg2.rotate(Direction.EAST), (j - this.field_15446) * 8);
                    boolean bl2 = bl = arg4 != null && MansionParameters.method_15047(arg4, j, i);
                    if (!MansionParameters.method_15047(arg3, j, i) || bl) continue;
                    list.add(new Piece(this.manager, "roof", lv.up(3), arg2));
                    if (!MansionParameters.method_15047(arg3, j + 1, i)) {
                        BlockPos lv2 = lv.offset(arg2.rotate(Direction.EAST), 6);
                        list.add(new Piece(this.manager, "roof_front", lv2, arg2));
                    }
                    if (!MansionParameters.method_15047(arg3, j - 1, i)) {
                        BlockPos lv3 = lv.offset(arg2.rotate(Direction.EAST), 0);
                        lv3 = lv3.offset(arg2.rotate(Direction.SOUTH), 7);
                        list.add(new Piece(this.manager, "roof_front", lv3, arg2.rotate(BlockRotation.CLOCKWISE_180)));
                    }
                    if (!MansionParameters.method_15047(arg3, j, i - 1)) {
                        BlockPos lv4 = lv.offset(arg2.rotate(Direction.WEST), 1);
                        list.add(new Piece(this.manager, "roof_front", lv4, arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    }
                    if (MansionParameters.method_15047(arg3, j, i + 1)) continue;
                    BlockPos lv5 = lv.offset(arg2.rotate(Direction.EAST), 6);
                    lv5 = lv5.offset(arg2.rotate(Direction.SOUTH), 6);
                    list.add(new Piece(this.manager, "roof_front", lv5, arg2.rotate(BlockRotation.CLOCKWISE_90)));
                }
            }
            if (arg4 != null) {
                for (int k = 0; k < arg3.m; ++k) {
                    for (int l = 0; l < arg3.n; ++l) {
                        BlockPos lv6 = arg;
                        lv6 = lv6.offset(arg2.rotate(Direction.SOUTH), 8 + (k - this.field_15445) * 8);
                        lv6 = lv6.offset(arg2.rotate(Direction.EAST), (l - this.field_15446) * 8);
                        boolean bl2 = MansionParameters.method_15047(arg4, l, k);
                        if (!MansionParameters.method_15047(arg3, l, k) || !bl2) continue;
                        if (!MansionParameters.method_15047(arg3, l + 1, k)) {
                            BlockPos lv7 = lv6.offset(arg2.rotate(Direction.EAST), 7);
                            list.add(new Piece(this.manager, "small_wall", lv7, arg2));
                        }
                        if (!MansionParameters.method_15047(arg3, l - 1, k)) {
                            BlockPos lv8 = lv6.offset(arg2.rotate(Direction.WEST), 1);
                            lv8 = lv8.offset(arg2.rotate(Direction.SOUTH), 6);
                            list.add(new Piece(this.manager, "small_wall", lv8, arg2.rotate(BlockRotation.CLOCKWISE_180)));
                        }
                        if (!MansionParameters.method_15047(arg3, l, k - 1)) {
                            BlockPos lv9 = lv6.offset(arg2.rotate(Direction.WEST), 0);
                            lv9 = lv9.offset(arg2.rotate(Direction.NORTH), 1);
                            list.add(new Piece(this.manager, "small_wall", lv9, arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        }
                        if (!MansionParameters.method_15047(arg3, l, k + 1)) {
                            BlockPos lv10 = lv6.offset(arg2.rotate(Direction.EAST), 6);
                            lv10 = lv10.offset(arg2.rotate(Direction.SOUTH), 7);
                            list.add(new Piece(this.manager, "small_wall", lv10, arg2.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (!MansionParameters.method_15047(arg3, l + 1, k)) {
                            if (!MansionParameters.method_15047(arg3, l, k - 1)) {
                                BlockPos lv11 = lv6.offset(arg2.rotate(Direction.EAST), 7);
                                lv11 = lv11.offset(arg2.rotate(Direction.NORTH), 2);
                                list.add(new Piece(this.manager, "small_wall_corner", lv11, arg2));
                            }
                            if (!MansionParameters.method_15047(arg3, l, k + 1)) {
                                BlockPos lv12 = lv6.offset(arg2.rotate(Direction.EAST), 8);
                                lv12 = lv12.offset(arg2.rotate(Direction.SOUTH), 7);
                                list.add(new Piece(this.manager, "small_wall_corner", lv12, arg2.rotate(BlockRotation.CLOCKWISE_90)));
                            }
                        }
                        if (MansionParameters.method_15047(arg3, l - 1, k)) continue;
                        if (!MansionParameters.method_15047(arg3, l, k - 1)) {
                            BlockPos lv13 = lv6.offset(arg2.rotate(Direction.WEST), 2);
                            lv13 = lv13.offset(arg2.rotate(Direction.NORTH), 1);
                            list.add(new Piece(this.manager, "small_wall_corner", lv13, arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        }
                        if (MansionParameters.method_15047(arg3, l, k + 1)) continue;
                        BlockPos lv14 = lv6.offset(arg2.rotate(Direction.WEST), 1);
                        lv14 = lv14.offset(arg2.rotate(Direction.SOUTH), 8);
                        list.add(new Piece(this.manager, "small_wall_corner", lv14, arg2.rotate(BlockRotation.CLOCKWISE_180)));
                    }
                }
            }
            for (int m = 0; m < arg3.m; ++m) {
                for (int n = 0; n < arg3.n; ++n) {
                    boolean bl3;
                    BlockPos lv15 = arg;
                    lv15 = lv15.offset(arg2.rotate(Direction.SOUTH), 8 + (m - this.field_15445) * 8);
                    lv15 = lv15.offset(arg2.rotate(Direction.EAST), (n - this.field_15446) * 8);
                    boolean bl = bl3 = arg4 != null && MansionParameters.method_15047(arg4, n, m);
                    if (!MansionParameters.method_15047(arg3, n, m) || bl3) continue;
                    if (!MansionParameters.method_15047(arg3, n + 1, m)) {
                        BlockPos lv16 = lv15.offset(arg2.rotate(Direction.EAST), 6);
                        if (!MansionParameters.method_15047(arg3, n, m + 1)) {
                            BlockPos lv17 = lv16.offset(arg2.rotate(Direction.SOUTH), 6);
                            list.add(new Piece(this.manager, "roof_corner", lv17, arg2));
                        } else if (MansionParameters.method_15047(arg3, n + 1, m + 1)) {
                            BlockPos lv18 = lv16.offset(arg2.rotate(Direction.SOUTH), 5);
                            list.add(new Piece(this.manager, "roof_inner_corner", lv18, arg2));
                        }
                        if (!MansionParameters.method_15047(arg3, n, m - 1)) {
                            list.add(new Piece(this.manager, "roof_corner", lv16, arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        } else if (MansionParameters.method_15047(arg3, n + 1, m - 1)) {
                            BlockPos lv19 = lv15.offset(arg2.rotate(Direction.EAST), 9);
                            lv19 = lv19.offset(arg2.rotate(Direction.NORTH), 2);
                            list.add(new Piece(this.manager, "roof_inner_corner", lv19, arg2.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                    }
                    if (MansionParameters.method_15047(arg3, n - 1, m)) continue;
                    BlockPos lv20 = lv15.offset(arg2.rotate(Direction.EAST), 0);
                    lv20 = lv20.offset(arg2.rotate(Direction.SOUTH), 0);
                    if (!MansionParameters.method_15047(arg3, n, m + 1)) {
                        BlockPos lv21 = lv20.offset(arg2.rotate(Direction.SOUTH), 6);
                        list.add(new Piece(this.manager, "roof_corner", lv21, arg2.rotate(BlockRotation.CLOCKWISE_90)));
                    } else if (MansionParameters.method_15047(arg3, n - 1, m + 1)) {
                        BlockPos lv22 = lv20.offset(arg2.rotate(Direction.SOUTH), 8);
                        lv22 = lv22.offset(arg2.rotate(Direction.WEST), 3);
                        list.add(new Piece(this.manager, "roof_inner_corner", lv22, arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    }
                    if (!MansionParameters.method_15047(arg3, n, m - 1)) {
                        list.add(new Piece(this.manager, "roof_corner", lv20, arg2.rotate(BlockRotation.CLOCKWISE_180)));
                        continue;
                    }
                    if (!MansionParameters.method_15047(arg3, n - 1, m - 1)) continue;
                    BlockPos lv23 = lv20.offset(arg2.rotate(Direction.SOUTH), 1);
                    list.add(new Piece(this.manager, "roof_inner_corner", lv23, arg2.rotate(BlockRotation.CLOCKWISE_180)));
                }
            }
        }

        private void addEntrance(List<Piece> list, GenerationPiece arg) {
            Direction lv = arg.rotation.rotate(Direction.WEST);
            list.add(new Piece(this.manager, "entrance", arg.position.offset(lv, 9), arg.rotation));
            arg.position = arg.position.offset(arg.rotation.rotate(Direction.SOUTH), 16);
        }

        private void method_15052(List<Piece> list, GenerationPiece arg) {
            list.add(new Piece(this.manager, arg.template, arg.position.offset(arg.rotation.rotate(Direction.EAST), 7), arg.rotation));
            arg.position = arg.position.offset(arg.rotation.rotate(Direction.SOUTH), 8);
        }

        private void method_15058(List<Piece> list, GenerationPiece arg) {
            arg.position = arg.position.offset(arg.rotation.rotate(Direction.SOUTH), -1);
            list.add(new Piece(this.manager, "wall_corner", arg.position, arg.rotation));
            arg.position = arg.position.offset(arg.rotation.rotate(Direction.SOUTH), -7);
            arg.position = arg.position.offset(arg.rotation.rotate(Direction.WEST), -6);
            arg.rotation = arg.rotation.rotate(BlockRotation.CLOCKWISE_90);
        }

        private void method_15060(List<Piece> list, GenerationPiece arg) {
            arg.position = arg.position.offset(arg.rotation.rotate(Direction.SOUTH), 6);
            arg.position = arg.position.offset(arg.rotation.rotate(Direction.EAST), 8);
            arg.rotation = arg.rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
        }

        private void addSmallRoom(List<Piece> list, BlockPos arg, BlockRotation arg2, Direction arg3, RoomPool arg4) {
            BlockRotation lv = BlockRotation.NONE;
            String string = arg4.getSmallRoom(this.random);
            if (arg3 != Direction.EAST) {
                if (arg3 == Direction.NORTH) {
                    lv = lv.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                } else if (arg3 == Direction.WEST) {
                    lv = lv.rotate(BlockRotation.CLOCKWISE_180);
                } else if (arg3 == Direction.SOUTH) {
                    lv = lv.rotate(BlockRotation.CLOCKWISE_90);
                } else {
                    string = arg4.getSmallSecretRoom(this.random);
                }
            }
            BlockPos lv2 = Structure.applyTransformedOffset(new BlockPos(1, 0, 0), BlockMirror.NONE, lv, 7, 7);
            lv = lv.rotate(arg2);
            lv2 = lv2.rotate(arg2);
            BlockPos lv3 = arg.add(lv2.getX(), 0, lv2.getZ());
            list.add(new Piece(this.manager, string, lv3, lv));
        }

        private void addMediumRoom(List<Piece> list, BlockPos arg, BlockRotation arg2, Direction arg3, Direction arg4, RoomPool arg5, boolean bl) {
            if (arg4 == Direction.EAST && arg3 == Direction.SOUTH) {
                BlockPos lv = arg.offset(arg2.rotate(Direction.EAST), 1);
                list.add(new Piece(this.manager, arg5.getMediumFunctionalRoom(this.random, bl), lv, arg2));
            } else if (arg4 == Direction.EAST && arg3 == Direction.NORTH) {
                BlockPos lv2 = arg.offset(arg2.rotate(Direction.EAST), 1);
                lv2 = lv2.offset(arg2.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, arg5.getMediumFunctionalRoom(this.random, bl), lv2, arg2, BlockMirror.LEFT_RIGHT));
            } else if (arg4 == Direction.WEST && arg3 == Direction.NORTH) {
                BlockPos lv3 = arg.offset(arg2.rotate(Direction.EAST), 7);
                lv3 = lv3.offset(arg2.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, arg5.getMediumFunctionalRoom(this.random, bl), lv3, arg2.rotate(BlockRotation.CLOCKWISE_180)));
            } else if (arg4 == Direction.WEST && arg3 == Direction.SOUTH) {
                BlockPos lv4 = arg.offset(arg2.rotate(Direction.EAST), 7);
                list.add(new Piece(this.manager, arg5.getMediumFunctionalRoom(this.random, bl), lv4, arg2, BlockMirror.FRONT_BACK));
            } else if (arg4 == Direction.SOUTH && arg3 == Direction.EAST) {
                BlockPos lv5 = arg.offset(arg2.rotate(Direction.EAST), 1);
                list.add(new Piece(this.manager, arg5.getMediumFunctionalRoom(this.random, bl), lv5, arg2.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.LEFT_RIGHT));
            } else if (arg4 == Direction.SOUTH && arg3 == Direction.WEST) {
                BlockPos lv6 = arg.offset(arg2.rotate(Direction.EAST), 7);
                list.add(new Piece(this.manager, arg5.getMediumFunctionalRoom(this.random, bl), lv6, arg2.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (arg4 == Direction.NORTH && arg3 == Direction.WEST) {
                BlockPos lv7 = arg.offset(arg2.rotate(Direction.EAST), 7);
                lv7 = lv7.offset(arg2.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, arg5.getMediumFunctionalRoom(this.random, bl), lv7, arg2.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.FRONT_BACK));
            } else if (arg4 == Direction.NORTH && arg3 == Direction.EAST) {
                BlockPos lv8 = arg.offset(arg2.rotate(Direction.EAST), 1);
                lv8 = lv8.offset(arg2.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, arg5.getMediumFunctionalRoom(this.random, bl), lv8, arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
            } else if (arg4 == Direction.SOUTH && arg3 == Direction.NORTH) {
                BlockPos lv9 = arg.offset(arg2.rotate(Direction.EAST), 1);
                lv9 = lv9.offset(arg2.rotate(Direction.NORTH), 8);
                list.add(new Piece(this.manager, arg5.getMediumGenericRoom(this.random, bl), lv9, arg2));
            } else if (arg4 == Direction.NORTH && arg3 == Direction.SOUTH) {
                BlockPos lv10 = arg.offset(arg2.rotate(Direction.EAST), 7);
                lv10 = lv10.offset(arg2.rotate(Direction.SOUTH), 14);
                list.add(new Piece(this.manager, arg5.getMediumGenericRoom(this.random, bl), lv10, arg2.rotate(BlockRotation.CLOCKWISE_180)));
            } else if (arg4 == Direction.WEST && arg3 == Direction.EAST) {
                BlockPos lv11 = arg.offset(arg2.rotate(Direction.EAST), 15);
                list.add(new Piece(this.manager, arg5.getMediumGenericRoom(this.random, bl), lv11, arg2.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (arg4 == Direction.EAST && arg3 == Direction.WEST) {
                BlockPos lv12 = arg.offset(arg2.rotate(Direction.WEST), 7);
                lv12 = lv12.offset(arg2.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, arg5.getMediumGenericRoom(this.random, bl), lv12, arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
            } else if (arg4 == Direction.UP && arg3 == Direction.EAST) {
                BlockPos lv13 = arg.offset(arg2.rotate(Direction.EAST), 15);
                list.add(new Piece(this.manager, arg5.getMediumSecretRoom(this.random), lv13, arg2.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (arg4 == Direction.UP && arg3 == Direction.SOUTH) {
                BlockPos lv14 = arg.offset(arg2.rotate(Direction.EAST), 1);
                lv14 = lv14.offset(arg2.rotate(Direction.NORTH), 0);
                list.add(new Piece(this.manager, arg5.getMediumSecretRoom(this.random), lv14, arg2));
            }
        }

        private void addBigRoom(List<Piece> list, BlockPos arg, BlockRotation arg2, Direction arg3, Direction arg4, RoomPool arg5) {
            int i = 0;
            int j = 0;
            BlockRotation lv = arg2;
            BlockMirror lv2 = BlockMirror.NONE;
            if (arg4 == Direction.EAST && arg3 == Direction.SOUTH) {
                i = -7;
            } else if (arg4 == Direction.EAST && arg3 == Direction.NORTH) {
                i = -7;
                j = 6;
                lv2 = BlockMirror.LEFT_RIGHT;
            } else if (arg4 == Direction.NORTH && arg3 == Direction.EAST) {
                i = 1;
                j = 14;
                lv = arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90);
            } else if (arg4 == Direction.NORTH && arg3 == Direction.WEST) {
                i = 7;
                j = 14;
                lv = arg2.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                lv2 = BlockMirror.LEFT_RIGHT;
            } else if (arg4 == Direction.SOUTH && arg3 == Direction.WEST) {
                i = 7;
                j = -8;
                lv = arg2.rotate(BlockRotation.CLOCKWISE_90);
            } else if (arg4 == Direction.SOUTH && arg3 == Direction.EAST) {
                i = 1;
                j = -8;
                lv = arg2.rotate(BlockRotation.CLOCKWISE_90);
                lv2 = BlockMirror.LEFT_RIGHT;
            } else if (arg4 == Direction.WEST && arg3 == Direction.NORTH) {
                i = 15;
                j = 6;
                lv = arg2.rotate(BlockRotation.CLOCKWISE_180);
            } else if (arg4 == Direction.WEST && arg3 == Direction.SOUTH) {
                i = 15;
                lv2 = BlockMirror.FRONT_BACK;
            }
            BlockPos lv3 = arg.offset(arg2.rotate(Direction.EAST), i);
            lv3 = lv3.offset(arg2.rotate(Direction.SOUTH), j);
            list.add(new Piece(this.manager, arg5.getBigRoom(this.random), lv3, lv, lv2));
        }

        private void addBigSecretRoom(List<Piece> list, BlockPos arg, BlockRotation arg2, RoomPool arg3) {
            BlockPos lv = arg.offset(arg2.rotate(Direction.EAST), 1);
            list.add(new Piece(this.manager, arg3.getBigSecretRoom(this.random), lv, arg2, BlockMirror.NONE));
        }
    }

    static class GenerationPiece {
        public BlockRotation rotation;
        public BlockPos position;
        public String template;

        private GenerationPiece() {
        }
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final String template;
        private final BlockRotation rotation;
        private final BlockMirror mirror;

        public Piece(StructureManager arg, String string, BlockPos arg2, BlockRotation arg3) {
            this(arg, string, arg2, arg3, BlockMirror.NONE);
        }

        public Piece(StructureManager arg, String string, BlockPos arg2, BlockRotation arg3, BlockMirror arg4) {
            super(StructurePieceType.WOODLAND_MANSION, 0);
            this.template = string;
            this.pos = arg2;
            this.rotation = arg3;
            this.mirror = arg4;
            this.setupPlacement(arg);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.WOODLAND_MANSION, arg2);
            this.template = arg2.getString("Template");
            this.rotation = BlockRotation.valueOf(arg2.getString("Rot"));
            this.mirror = BlockMirror.valueOf(arg2.getString("Mi"));
            this.setupPlacement(arg);
        }

        private void setupPlacement(StructureManager arg) {
            Structure lv = arg.getStructureOrBlank(new Identifier("woodland_mansion/" + this.template));
            StructurePlacementData lv2 = new StructurePlacementData().setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putString("Template", this.template);
            arg.putString("Rot", this.placementData.getRotation().name());
            arg.putString("Mi", this.placementData.getMirror().name());
        }

        /*
         * WARNING - void declaration
         */
        @Override
        protected void handleMetadata(String string, BlockPos arg, class_5425 arg2, Random random, BlockBox arg3) {
            if (string.startsWith("Chest")) {
                BlockRotation lv = this.placementData.getRotation();
                BlockState lv2 = Blocks.CHEST.getDefaultState();
                if ("ChestWest".equals(string)) {
                    lv2 = (BlockState)lv2.with(ChestBlock.FACING, lv.rotate(Direction.WEST));
                } else if ("ChestEast".equals(string)) {
                    lv2 = (BlockState)lv2.with(ChestBlock.FACING, lv.rotate(Direction.EAST));
                } else if ("ChestSouth".equals(string)) {
                    lv2 = (BlockState)lv2.with(ChestBlock.FACING, lv.rotate(Direction.SOUTH));
                } else if ("ChestNorth".equals(string)) {
                    lv2 = (BlockState)lv2.with(ChestBlock.FACING, lv.rotate(Direction.NORTH));
                }
                this.addChest(arg2, arg3, random, arg, LootTables.WOODLAND_MANSION_CHEST, lv2);
            } else {
                void lv5;
                switch (string) {
                    case "Mage": {
                        IllagerEntity lv3 = EntityType.EVOKER.create(arg2.getWorld());
                        break;
                    }
                    case "Warrior": {
                        IllagerEntity lv4 = EntityType.VINDICATOR.create(arg2.getWorld());
                        break;
                    }
                    default: {
                        return;
                    }
                }
                lv5.setPersistent();
                lv5.refreshPositionAndAngles(arg, 0.0f, 0.0f);
                lv5.initialize(arg2, arg2.getLocalDifficulty(lv5.getBlockPos()), SpawnReason.STRUCTURE, null, null);
                arg2.spawnEntity((Entity)lv5);
                arg2.setBlockState(arg, Blocks.AIR.getDefaultState(), 2);
            }
        }
    }
}

