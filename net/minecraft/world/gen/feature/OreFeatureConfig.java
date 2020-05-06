/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public final Target target;
    public final int size;
    public final BlockState state;

    public OreFeatureConfig(Target arg, BlockState arg2, int i) {
        this.size = i;
        this.state = arg2;
        this.target = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("size"), (Object)dynamicOps.createInt(this.size), (Object)dynamicOps.createString("target"), (Object)dynamicOps.createString(this.target.getName()), (Object)dynamicOps.createString("state"), (Object)BlockState.serialize(dynamicOps, this.state).getValue())));
    }

    public static OreFeatureConfig deserialize(Dynamic<?> dynamic) {
        int i = dynamic.get("size").asInt(0);
        Target lv = Target.byName(dynamic.get("target").asString(""));
        BlockState lv2 = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new OreFeatureConfig(lv, lv2, i);
    }

    public static enum Target {
        NATURAL_STONE("natural_stone", arg -> {
            if (arg != null) {
                return arg.isOf(Blocks.STONE) || arg.isOf(Blocks.GRANITE) || arg.isOf(Blocks.DIORITE) || arg.isOf(Blocks.ANDESITE);
            }
            return false;
        }),
        NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK));

        private static final Map<String, Target> nameMap;
        private final String name;
        private final Predicate<BlockState> predicate;

        private Target(String string2, Predicate<BlockState> predicate) {
            this.name = string2;
            this.predicate = predicate;
        }

        public String getName() {
            return this.name;
        }

        public static Target byName(String string) {
            return nameMap.get(string);
        }

        public Predicate<BlockState> getCondition() {
            return this.predicate;
        }

        static {
            nameMap = Arrays.stream(Target.values()).collect(Collectors.toMap(Target::getName, arg -> arg));
        }
    }
}

