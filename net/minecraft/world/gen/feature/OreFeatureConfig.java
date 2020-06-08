/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Target.field_24898.fieldOf("target").forGetter(arg -> arg.target), (App)BlockState.CODEC.fieldOf("state").forGetter(arg -> arg.state), (App)Codec.INT.fieldOf("size").withDefault((Object)0).forGetter(arg -> arg.size)).apply((Applicative)instance, OreFeatureConfig::new));
    public final Target target;
    public final int size;
    public final BlockState state;

    public OreFeatureConfig(Target arg, BlockState arg2, int i) {
        this.size = i;
        this.state = arg2;
        this.target = arg;
    }

    public static enum Target implements StringIdentifiable
    {
        NATURAL_STONE("natural_stone", arg -> {
            if (arg != null) {
                return arg.isOf(Blocks.STONE) || arg.isOf(Blocks.GRANITE) || arg.isOf(Blocks.DIORITE) || arg.isOf(Blocks.ANDESITE);
            }
            return false;
        }),
        NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK)),
        NETHER_ORE_REPLACEABLES("nether_ore_replaceables", arg -> {
            if (arg != null) {
                return arg.isOf(Blocks.NETHERRACK) || arg.isOf(Blocks.BASALT) || arg.isOf(Blocks.BLACKSTONE);
            }
            return false;
        });

        public static final Codec<Target> field_24898;
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

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24898 = StringIdentifiable.method_28140(Target::values, Target::byName);
            nameMap = Arrays.stream(Target.values()).collect(Collectors.toMap(Target::getName, arg -> arg));
        }
    }
}

