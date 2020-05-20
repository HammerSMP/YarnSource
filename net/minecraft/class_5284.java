/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5308;
import net.minecraft.class_5309;
import net.minecraft.class_5310;
import net.minecraft.class_5311;
import net.minecraft.class_5314;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.StructureFeature;

public final class class_5284 {
    public static final Codec<class_5284> field_24780 = RecordCodecBuilder.create(instance -> instance.group((App)class_5311.field_24821.fieldOf("structures").forGetter(class_5284::getConfig), (App)class_5309.field_24804.fieldOf("noise").forGetter(class_5284::method_28559), (App)BlockState.field_24734.fieldOf("default_block").forGetter(class_5284::getDefaultBlock), (App)BlockState.field_24734.fieldOf("default_fluid").forGetter(class_5284::getDefaultFluid), (App)Codec.INT.stable().fieldOf("bedrock_roof_position").forGetter(class_5284::getBedrockCeilingY), (App)Codec.INT.stable().fieldOf("bedrock_floor_position").forGetter(class_5284::getBedrockFloorY), (App)Codec.INT.stable().fieldOf("sea_level").forGetter(class_5284::method_28561), (App)Codec.BOOL.stable().fieldOf("disable_mob_generation").forGetter(class_5284::method_28562)).apply((Applicative)instance, class_5284::new));
    public static final Codec<class_5284> field_24781 = Codec.either(class_5307.field_24788, field_24780).xmap(either -> (class_5284)either.map(class_5307::method_28568, Function.identity()), arg -> arg.field_24787.map(Either::left).orElseGet(() -> Either.right((Object)arg)));
    private final class_5311 config;
    private final class_5309 field_24782;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final int field_24783;
    private final int field_24784;
    private final int field_24785;
    private final boolean field_24786;
    private final Optional<class_5307> field_24787;

    private class_5284(class_5311 arg, class_5309 arg2, BlockState arg3, BlockState arg4, int i, int j, int k, boolean bl) {
        this(arg, arg2, arg3, arg4, i, j, k, bl, Optional.empty());
    }

    private class_5284(class_5311 arg, class_5309 arg2, BlockState arg3, BlockState arg4, int i, int j, int k, boolean bl, Optional<class_5307> optional) {
        this.config = arg;
        this.field_24782 = arg2;
        this.defaultBlock = arg3;
        this.defaultFluid = arg4;
        this.field_24783 = i;
        this.field_24784 = j;
        this.field_24785 = k;
        this.field_24786 = bl;
        this.field_24787 = optional;
    }

    public class_5311 getConfig() {
        return this.config;
    }

    public class_5309 method_28559() {
        return this.field_24782;
    }

    public BlockState getDefaultBlock() {
        return this.defaultBlock;
    }

    public BlockState getDefaultFluid() {
        return this.defaultFluid;
    }

    public int getBedrockCeilingY() {
        return this.field_24783;
    }

    public int getBedrockFloorY() {
        return this.field_24784;
    }

    public int method_28561() {
        return this.field_24785;
    }

    @Deprecated
    protected boolean method_28562() {
        return this.field_24786;
    }

    public boolean method_28555(class_5307 arg) {
        return Objects.equals(this.field_24787, Optional.of(arg));
    }

    public static class class_5307 {
        private static final Map<Identifier, class_5307> field_24795 = Maps.newHashMap();
        public static final Codec<class_5307> field_24788 = Identifier.field_25139.flatXmap(arg -> Optional.ofNullable(field_24795.get(arg)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + arg))), arg -> DataResult.success((Object)arg.field_24797)).stable();
        public static final class_5307 OVERWORLD = new class_5307("overworld", arg -> class_5307.method_28566(new class_5311(true), false, arg));
        public static final class_5307 AMPLIFIED = new class_5307("amplified", arg -> class_5307.method_28566(new class_5311(true), true, arg));
        public static final class_5307 NETHER = new class_5307("nether", arg -> class_5307.method_28564(new class_5311(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), arg));
        public static final class_5307 END = new class_5307("end", arg -> class_5307.method_28565(new class_5311(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), arg, true));
        public static final class_5307 CAVES = new class_5307("caves", arg -> class_5307.method_28564(new class_5311(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), arg));
        public static final class_5307 FLOATING_ISLANDS = new class_5307("floating_islands", arg -> class_5307.method_28565(new class_5311(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), arg, false));
        private final Text field_24796;
        private final Identifier field_24797;
        private final class_5284 field_24798;

        public class_5307(String string, Function<class_5307, class_5284> function) {
            this.field_24797 = new Identifier(string);
            this.field_24796 = new TranslatableText("generator.noise." + string);
            this.field_24798 = function.apply(this);
            field_24795.put(this.field_24797, this);
        }

        public class_5284 method_28568() {
            return this.field_24798;
        }

        private static class_5284 method_28565(class_5311 arg, BlockState arg2, BlockState arg3, class_5307 arg4, boolean bl) {
            return new class_5284(arg, new class_5309(128, new class_5308(2.0, 1.0, 80.0, 160.0), new class_5310(-3000, 64, -46), new class_5310(-30, 7, 1), 2, 1, 0.0, 0.0, true, false, bl, false), arg2, arg3, -10, -10, 0, true, Optional.of(arg4));
        }

        private static class_5284 method_28564(class_5311 arg, BlockState arg2, BlockState arg3, class_5307 arg4) {
            HashMap map = Maps.newHashMap(class_5311.field_24822);
            map.put(StructureFeature.RUINED_PORTAL, new class_5314(25, 10, 34222645));
            return new class_5284(new class_5311(Optional.ofNullable(arg.method_28602()), map), new class_5309(128, new class_5308(1.0, 3.0, 80.0, 60.0), new class_5310(120, 3, 0), new class_5310(320, 4, -1), 1, 2, 0.0, 0.019921875, false, false, false, false), arg2, arg3, 0, 0, 32, true, Optional.of(arg4));
        }

        private static class_5284 method_28566(class_5311 arg, boolean bl, class_5307 arg2) {
            double d = 0.9999999814507745;
            return new class_5284(arg, new class_5309(256, new class_5308(0.9999999814507745, 0.9999999814507745, 80.0, 160.0), new class_5310(-10, 3, 0), new class_5310(-30, 0, 0), 1, 2, 1.0, -0.46875, true, true, false, bl), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), -10, 0, 63, false, Optional.of(arg2));
        }
    }
}

