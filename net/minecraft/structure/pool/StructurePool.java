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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePool {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final MapCodec<StructurePool> field_25853 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("name").forGetter(StructurePool::getId), (App)Identifier.CODEC.fieldOf("fallback").forGetter(StructurePool::getTerminatorsId), (App)Codec.mapPair((MapCodec)StructurePoolElement.field_24953.fieldOf("element"), (MapCodec)Codec.INT.fieldOf("weight")).codec().listOf().promotePartial(Util.method_29188("Pool element: ", ((Logger)LOGGER)::error)).fieldOf("elements").forGetter(arg -> arg.elementCounts)).apply((Applicative)instance, StructurePool::new));
    public static final Codec<Supplier<StructurePool>> CODEC = RegistryElementCodec.of(Registry.TEMPLATE_POOL_WORLDGEN, field_25853);
    private final Identifier id;
    private final List<Pair<StructurePoolElement, Integer>> elementCounts;
    private final List<StructurePoolElement> elements;
    private final Identifier terminatorsId;
    private int highestY = Integer.MIN_VALUE;

    public StructurePool(Identifier arg, Identifier arg2, List<Pair<StructurePoolElement, Integer>> list) {
        this.id = arg;
        this.elementCounts = list;
        this.elements = Lists.newArrayList();
        for (Pair<StructurePoolElement, Integer> pair : list) {
            StructurePoolElement lv = (StructurePoolElement)pair.getFirst();
            for (int i = 0; i < (Integer)pair.getSecond(); ++i) {
                this.elements.add(lv);
            }
        }
        this.terminatorsId = arg2;
    }

    public StructurePool(Identifier arg, Identifier arg2, List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> list, Projection arg3) {
        this.id = arg;
        this.elementCounts = Lists.newArrayList();
        this.elements = Lists.newArrayList();
        for (Pair<Function<Projection, ? extends StructurePoolElement>, Integer> pair : list) {
            StructurePoolElement lv = (StructurePoolElement)((Function)pair.getFirst()).apply(arg3);
            this.elementCounts.add((Pair<StructurePoolElement, Integer>)Pair.of((Object)lv, (Object)pair.getSecond()));
            for (int i = 0; i < (Integer)pair.getSecond(); ++i) {
                this.elements.add(lv);
            }
        }
        this.terminatorsId = arg2;
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
            field_24956 = StringIdentifiable.createCodec(Projection::values, Projection::getById);
            PROJECTIONS_BY_ID = Arrays.stream(Projection.values()).collect(Collectors.toMap(Projection::getId, arg -> arg));
        }
    }
}

