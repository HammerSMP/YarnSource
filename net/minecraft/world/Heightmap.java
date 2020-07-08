/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class Heightmap {
    private static final Predicate<BlockState> ALWAYS_TRUE = arg -> !arg.isAir();
    private static final Predicate<BlockState> SUFFOCATES = arg -> arg.getMaterial().blocksMovement();
    private final PackedIntegerArray storage = new PackedIntegerArray(9, 256);
    private final Predicate<BlockState> blockPredicate;
    private final Chunk chunk;

    public Heightmap(Chunk arg, Type arg2) {
        this.blockPredicate = arg2.getBlockPredicate();
        this.chunk = arg;
    }

    public static void populateHeightmaps(Chunk arg, Set<Type> set) {
        int i = set.size();
        ObjectArrayList objectList = new ObjectArrayList(i);
        ObjectListIterator objectListIterator = objectList.iterator();
        int j = arg.getHighestNonEmptySectionYOffset() + 16;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int k = 0; k < 16; ++k) {
            block1: for (int l = 0; l < 16; ++l) {
                for (Type lv2 : set) {
                    objectList.add((Object)arg.getHeightmap(lv2));
                }
                for (int m = j - 1; m >= 0; --m) {
                    lv.set(k, m, l);
                    BlockState lv3 = arg.getBlockState(lv);
                    if (lv3.isOf(Blocks.AIR)) continue;
                    while (objectListIterator.hasNext()) {
                        Heightmap lv4 = (Heightmap)objectListIterator.next();
                        if (!lv4.blockPredicate.test(lv3)) continue;
                        lv4.set(k, l, m + 1);
                        objectListIterator.remove();
                    }
                    if (objectList.isEmpty()) continue block1;
                    objectListIterator.back(i);
                }
            }
        }
    }

    public boolean trackUpdate(int i, int j, int k, BlockState arg) {
        int l = this.get(i, k);
        if (j <= l - 2) {
            return false;
        }
        if (this.blockPredicate.test(arg)) {
            if (j >= l) {
                this.set(i, k, j + 1);
                return true;
            }
        } else if (l - 1 == j) {
            BlockPos.Mutable lv = new BlockPos.Mutable();
            for (int m = j - 1; m >= 0; --m) {
                lv.set(i, m, k);
                if (!this.blockPredicate.test(this.chunk.getBlockState(lv))) continue;
                this.set(i, k, m + 1);
                return true;
            }
            this.set(i, k, 0);
            return true;
        }
        return false;
    }

    public int get(int i, int j) {
        return this.get(Heightmap.toIndex(i, j));
    }

    private int get(int i) {
        return this.storage.get(i);
    }

    private void set(int i, int j, int k) {
        this.storage.set(Heightmap.toIndex(i, j), k);
    }

    public void setTo(long[] ls) {
        System.arraycopy(ls, 0, this.storage.getStorage(), 0, ls.length);
    }

    public long[] asLongArray() {
        return this.storage.getStorage();
    }

    private static int toIndex(int i, int j) {
        return i + j * 16;
    }

    static /* synthetic */ Predicate method_16683() {
        return ALWAYS_TRUE;
    }

    static /* synthetic */ Predicate method_16681() {
        return SUFFOCATES;
    }

    public static enum Type implements StringIdentifiable
    {
        WORLD_SURFACE_WG("WORLD_SURFACE_WG", Purpose.WORLDGEN, Heightmap.method_16683()),
        WORLD_SURFACE("WORLD_SURFACE", Purpose.CLIENT, Heightmap.method_16683()),
        OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Purpose.WORLDGEN, Heightmap.method_16681()),
        OCEAN_FLOOR("OCEAN_FLOOR", Purpose.LIVE_WORLD, Heightmap.method_16681()),
        MOTION_BLOCKING("MOTION_BLOCKING", Purpose.CLIENT, arg -> arg.getMaterial().blocksMovement() || !arg.getFluidState().isEmpty()),
        MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Purpose.LIVE_WORLD, arg -> (arg.getMaterial().blocksMovement() || !arg.getFluidState().isEmpty()) && !(arg.getBlock() instanceof LeavesBlock));

        public static final Codec<Type> field_24772;
        private final String name;
        private final Purpose purpose;
        private final Predicate<BlockState> blockPredicate;
        private static final Map<String, Type> BY_NAME;

        private Type(String string2, Purpose arg, Predicate<BlockState> predicate) {
            this.name = string2;
            this.purpose = arg;
            this.blockPredicate = predicate;
        }

        public String getName() {
            return this.name;
        }

        public boolean shouldSendToClient() {
            return this.purpose == Purpose.CLIENT;
        }

        @Environment(value=EnvType.CLIENT)
        public boolean isStoredServerSide() {
            return this.purpose != Purpose.WORLDGEN;
        }

        @Nullable
        public static Type byName(String string) {
            return BY_NAME.get(string);
        }

        public Predicate<BlockState> getBlockPredicate() {
            return this.blockPredicate;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24772 = StringIdentifiable.createCodec(Type::values, Type::byName);
            BY_NAME = Util.make(Maps.newHashMap(), hashMap -> {
                for (Type lv : Type.values()) {
                    hashMap.put(lv.name, lv);
                }
            });
        }
    }

    public static enum Purpose {
        WORLDGEN,
        LIVE_WORLD,
        CLIENT;

    }
}

