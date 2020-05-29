/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrays
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePool {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<StructurePool> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.field_25139.fieldOf("name").forGetter(StructurePool::getId), (App)Identifier.field_25139.fieldOf("fallback").forGetter(StructurePool::getTerminatorsId), (App)Codec.mapPair((MapCodec)StructurePoolElement.field_24953.fieldOf("element"), (MapCodec)Codec.INT.fieldOf("weight")).codec().listOf().promotePartial(Util.method_29188("Pool element: ", ((Logger)LOGGER)::error)).fieldOf("elements").forGetter(arg -> arg.elementCounts), (App)Projection.field_24956.fieldOf("projection").forGetter(arg -> arg.projection)).apply((Applicative)instance, StructurePool::new));
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
            for (int i = 0; i < (Integer)pair.getSecond(); ++i) {
                this.elements.add(((StructurePoolElement)pair.getFirst()).setProjection(arg3));
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

    public static enum Projection implements StringIdentifiable
    {
        TERRAIN_MATCHING("terrain_matching", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
        RIGID("rigid", (ImmutableList<StructureProcessor>)ImmutableList.of());

        public static final Codec<Projection> field_24956;
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

        @Override
        public String asString() {
            return this.id;
        }

        static {
            field_24956 = StringIdentifiable.method_28140(Projection::values, Projection::getById);
            PROJECTIONS_BY_ID = Arrays.stream(Projection.values()).collect(Collectors.toMap(Projection::getId, arg -> arg));
        }
    }
}

