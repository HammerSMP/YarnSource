/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.WordPackedArray;

public class LeavesFix
extends DataFix {
    private static final int[][] field_5687 = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
    private static final Object2IntMap<String> LEAVES_MAP = (Object2IntMap)DataFixUtils.make((Object)new Object2IntOpenHashMap(), object2IntOpenHashMap -> {
        object2IntOpenHashMap.put((Object)"minecraft:acacia_leaves", 0);
        object2IntOpenHashMap.put((Object)"minecraft:birch_leaves", 1);
        object2IntOpenHashMap.put((Object)"minecraft:dark_oak_leaves", 2);
        object2IntOpenHashMap.put((Object)"minecraft:jungle_leaves", 3);
        object2IntOpenHashMap.put((Object)"minecraft:oak_leaves", 4);
        object2IntOpenHashMap.put((Object)"minecraft:spruce_leaves", 5);
    });
    private static final Set<String> LOGS_MAP = ImmutableSet.of((Object)"minecraft:acacia_bark", (Object)"minecraft:birch_bark", (Object)"minecraft:dark_oak_bark", (Object)"minecraft:jungle_bark", (Object)"minecraft:oak_bark", (Object)"minecraft:spruce_bark", (Object[])new String[]{"minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

    public LeavesFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder opticFinder = type.findField("Level");
        OpticFinder opticFinder2 = opticFinder.type().findField("Sections");
        Type type2 = opticFinder2.type();
        if (!(type2 instanceof List.ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
        }
        Type type3 = ((List.ListType)type2).getElement();
        OpticFinder opticFinder3 = DSL.typeFinder((Type)type3);
        return this.fixTypeEverywhereTyped("Leaves fix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> {
            int[] is = new int[]{0};
            Typed typed22 = typed.updateTyped(opticFinder2, typed2 -> {
                Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap(typed2.getAllTyped(opticFinder3).stream().map(typed -> new LeavesLogFixer((Typed<?>)typed, this.getInputSchema())).collect(Collectors.toMap(ListFixer::method_5077, arg -> arg)));
                if (int2ObjectMap.values().stream().allMatch(ListFixer::isFixed)) {
                    return typed2;
                }
                ArrayList list = Lists.newArrayList();
                for (int i = 0; i < 7; ++i) {
                    list.add(new IntOpenHashSet());
                }
                for (LeavesLogFixer lv : int2ObjectMap.values()) {
                    if (lv.isFixed()) continue;
                    for (int j = 0; j < 4096; ++j) {
                        int k = lv.needsFix(j);
                        if (lv.isLog(k)) {
                            ((IntSet)list.get(0)).add(lv.method_5077() << 12 | j);
                            continue;
                        }
                        if (!lv.isLeaf(k)) continue;
                        int l = this.method_5052(j);
                        int m = this.method_5050(j);
                        is[0] = is[0] | LeavesFix.method_5061(l == 0, l == 15, m == 0, m == 15);
                    }
                }
                for (int n = 1; n < 7; ++n) {
                    IntSet intSet = (IntSet)list.get(n - 1);
                    IntSet intSet2 = (IntSet)list.get(n);
                    IntIterator intIterator = intSet.iterator();
                    while (intIterator.hasNext()) {
                        int o = intIterator.nextInt();
                        int p = this.method_5052(o);
                        int q = this.method_5062(o);
                        int r = this.method_5050(o);
                        for (int[] js : field_5687) {
                            int x;
                            int v;
                            int w;
                            LeavesLogFixer lv2;
                            int s = p + js[0];
                            int t = q + js[1];
                            int u = r + js[2];
                            if (s < 0 || s > 15 || u < 0 || u > 15 || t < 0 || t > 255 || (lv2 = (LeavesLogFixer)int2ObjectMap.get(t >> 4)) == null || lv2.isFixed() || !lv2.isLeaf(w = lv2.needsFix(v = LeavesFix.method_5051(s, t & 0xF, u))) || (x = lv2.getDistanceToLog(w)) <= n) continue;
                            lv2.computeLeafStates(v, w, n);
                            intSet2.add(LeavesFix.method_5051(s, t, u));
                        }
                    }
                }
                return typed2.updateTyped(opticFinder3, arg_0 -> LeavesFix.method_5058((Int2ObjectMap)int2ObjectMap, arg_0));
            });
            if (is[0] != 0) {
                typed22 = typed22.update(DSL.remainderFinder(), dynamic -> {
                    Dynamic dynamic2 = (Dynamic)DataFixUtils.orElse((Optional)dynamic.get("UpgradeData").result(), (Object)dynamic.emptyMap());
                    return dynamic.set("UpgradeData", dynamic2.set("Sides", dynamic.createByte((byte)(dynamic2.get("Sides").asByte((byte)0) | is[0]))));
                });
            }
            return typed22;
        }));
    }

    public static int method_5051(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private int method_5052(int i) {
        return i & 0xF;
    }

    private int method_5062(int i) {
        return i >> 8 & 0xFF;
    }

    private int method_5050(int i) {
        return i >> 4 & 0xF;
    }

    public static int method_5061(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int i = 0;
        if (bl3) {
            i = bl2 ? (i |= 2) : (bl ? (i |= 0x80) : (i |= 1));
        } else if (bl4) {
            i = bl ? (i |= 0x20) : (bl2 ? (i |= 8) : (i |= 0x10));
        } else if (bl2) {
            i |= 4;
        } else if (bl) {
            i |= 0x40;
        }
        return i;
    }

    private static /* synthetic */ Typed method_5058(Int2ObjectMap int2ObjectMap, Typed typed) {
        return ((LeavesLogFixer)int2ObjectMap.get(((Dynamic)typed.get(DSL.remainderFinder())).get("Y").asInt(0))).method_5083(typed);
    }

    public static final class LeavesLogFixer
    extends ListFixer {
        @Nullable
        private IntSet leafIndices;
        @Nullable
        private IntSet logIndices;
        @Nullable
        private Int2IntMap leafStates;

        public LeavesLogFixer(Typed<?> typed, Schema schema) {
            super(typed, schema);
        }

        @Override
        protected boolean needsFix() {
            this.leafIndices = new IntOpenHashSet();
            this.logIndices = new IntOpenHashSet();
            this.leafStates = new Int2IntOpenHashMap();
            for (int i = 0; i < this.properties.size(); ++i) {
                Dynamic dynamic = (Dynamic)this.properties.get(i);
                String string = dynamic.get("Name").asString("");
                if (LEAVES_MAP.containsKey((Object)string)) {
                    boolean bl = Objects.equals(dynamic.get("Properties").get("decayable").asString(""), "false");
                    this.leafIndices.add(i);
                    this.leafStates.put(this.computeFlags(string, bl, 7), i);
                    this.properties.set(i, this.createLeafProperties(dynamic, string, bl, 7));
                }
                if (!LOGS_MAP.contains(string)) continue;
                this.logIndices.add(i);
            }
            return this.leafIndices.isEmpty() && this.logIndices.isEmpty();
        }

        private Dynamic<?> createLeafProperties(Dynamic<?> dynamic, String string, boolean bl, int i) {
            Dynamic dynamic2 = dynamic.emptyMap();
            dynamic2 = dynamic2.set("persistent", dynamic2.createString(bl ? "true" : "false"));
            dynamic2 = dynamic2.set("distance", dynamic2.createString(Integer.toString(i)));
            Dynamic dynamic3 = dynamic.emptyMap();
            dynamic3 = dynamic3.set("Properties", dynamic2);
            dynamic3 = dynamic3.set("Name", dynamic3.createString(string));
            return dynamic3;
        }

        public boolean isLog(int i) {
            return this.logIndices.contains(i);
        }

        public boolean isLeaf(int i) {
            return this.leafIndices.contains(i);
        }

        private int getDistanceToLog(int i) {
            if (this.isLog(i)) {
                return 0;
            }
            return Integer.parseInt(((Dynamic)this.properties.get(i)).get("Properties").get("distance").asString(""));
        }

        private void computeLeafStates(int i, int j, int k) {
            boolean bl;
            Dynamic dynamic = (Dynamic)this.properties.get(j);
            String string = dynamic.get("Name").asString("");
            int l = this.computeFlags(string, bl = Objects.equals(dynamic.get("Properties").get("persistent").asString(""), "true"), k);
            if (!this.leafStates.containsKey(l)) {
                int m = this.properties.size();
                this.leafIndices.add(m);
                this.leafStates.put(l, m);
                this.properties.add(this.createLeafProperties(dynamic, string, bl, k));
            }
            int n = this.leafStates.get(l);
            if (1 << this.blockStateMap.getUnitSize() <= n) {
                WordPackedArray lv = new WordPackedArray(this.blockStateMap.getUnitSize() + 1, 4096);
                for (int o = 0; o < 4096; ++o) {
                    lv.set(o, this.blockStateMap.get(o));
                }
                this.blockStateMap = lv;
            }
            this.blockStateMap.set(i, n);
        }
    }

    public static abstract class ListFixer {
        private final Type<Pair<String, Dynamic<?>>> field_5695 = DSL.named((String)TypeReferences.BLOCK_STATE.typeName(), (Type)DSL.remainderType());
        protected final OpticFinder<List<Pair<String, Dynamic<?>>>> field_5693 = DSL.fieldFinder((String)"Palette", (Type)DSL.list(this.field_5695));
        protected final List<Dynamic<?>> properties;
        protected final int field_5694;
        @Nullable
        protected WordPackedArray blockStateMap;

        public ListFixer(Typed<?> typed, Schema schema) {
            if (!Objects.equals((Object)schema.getType(TypeReferences.BLOCK_STATE), this.field_5695)) {
                throw new IllegalStateException("Block state type is not what was expected.");
            }
            Optional optional = typed.getOptional(this.field_5693);
            this.properties = optional.map(list -> list.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse((List)ImmutableList.of());
            Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
            this.field_5694 = dynamic.get("Y").asInt(0);
            this.computeFixableBlockStates(dynamic);
        }

        protected void computeFixableBlockStates(Dynamic<?> dynamic) {
            if (this.needsFix()) {
                this.blockStateMap = null;
            } else {
                long[] ls = dynamic.get("BlockStates").asLongStream().toArray();
                int i = Math.max(4, DataFixUtils.ceillog2((int)this.properties.size()));
                this.blockStateMap = new WordPackedArray(i, 4096, ls);
            }
        }

        public Typed<?> method_5083(Typed<?> typed) {
            if (this.isFixed()) {
                return typed;
            }
            return typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.blockStateMap.getAlignedArray())))).set(this.field_5693, this.properties.stream().map(dynamic -> Pair.of((Object)TypeReferences.BLOCK_STATE.typeName(), (Object)dynamic)).collect(Collectors.toList()));
        }

        public boolean isFixed() {
            return this.blockStateMap == null;
        }

        public int needsFix(int i) {
            return this.blockStateMap.get(i);
        }

        protected int computeFlags(String string, boolean bl, int i) {
            return LEAVES_MAP.get((Object)string) << 5 | (bl ? 16 : 0) | i;
        }

        int method_5077() {
            return this.field_5694;
        }

        protected abstract boolean needsFix();
    }
}

