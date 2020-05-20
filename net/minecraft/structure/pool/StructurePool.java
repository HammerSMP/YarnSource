/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrays
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class StructurePool {
    public static final StructurePool EMPTY = new StructurePool(new Identifier("empty"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(), Projection.RIGID);
    public static final StructurePool INVALID = new StructurePool(new Identifier("invalid"), new Identifier("invalid"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of(), Projection.RIGID);
    private final Identifier id;
    private final ImmutableList<Pair<StructurePoolElement, Integer>> elementCounts;
    private final List<StructurePoolElement> elements;
    private final Identifier terminatorsId;
    private final Projection projection;
    private int highestY = Integer.MIN_VALUE;

    public StructurePool(Identifier arg, Identifier arg2, List<Pair<StructurePoolElement, Integer>> list, Projection arg3) {
        this.id = arg;
        this.elementCounts = ImmutableList.copyOf(list);
        this.elements = Lists.newArrayList();
        for (Pair<StructurePoolElement, Integer> pair : list) {
            Integer integer = 0;
            while (integer < (Integer)pair.getSecond()) {
                this.elements.add(((StructurePoolElement)pair.getFirst()).setProjection(arg3));
                Integer n = integer;
                Integer n2 = integer = Integer.valueOf(integer + 1);
            }
        }
        this.terminatorsId = arg2;
        this.projection = arg3;
    }

    public int getHighestY(StructureManager arg) {
        if (this.highestY == Integer.MIN_VALUE) {
            this.highestY = this.elements.stream().mapToInt(arg2 -> arg2.getBoundingBox(arg, BlockPos.ORIGIN, BlockRotation.NONE).getBlockCountY()).max().orElse(0);
        }
        return this.highestY;
    }

    public Identifier getTerminatorsId() {
        return this.terminatorsId;
    }

    public StructurePoolElement getRandomElement(Random random) {
        return this.elements.get(random.nextInt(this.elements.size()));
    }

    public List<StructurePoolElement> getElementIndicesInRandomOrder(Random random) {
        return ImmutableList.copyOf((Object[])ObjectArrays.shuffle((Object[])this.elements.toArray(new StructurePoolElement[0]), (Random)random));
    }

    public Identifier getId() {
        return this.id;
    }

    public int getElementCount() {
        return this.elements.size();
    }

    public static enum Projection {
        TERRAIN_MATCHING("terrain_matching", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
        RIGID("rigid", (ImmutableList<StructureProcessor>)ImmutableList.of());

        private static final Map<String, Projection> PROJECTIONS_BY_ID;
        private final String id;
        private final ImmutableList<StructureProcessor> processors;

        private Projection(String string2, ImmutableList<StructureProcessor> immutableList) {
            this.id = string2;
            this.processors = immutableList;
        }

        public String getId() {
            return this.id;
        }

        public static Projection getById(String string) {
            return PROJECTIONS_BY_ID.get(string);
        }

        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }

        static {
            PROJECTIONS_BY_ID = Arrays.stream(Projection.values()).collect(Collectors.toMap(Projection::getId, arg -> arg));
        }
    }
}
