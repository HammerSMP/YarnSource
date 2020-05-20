/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class AlterGroundTreeDecorator
extends TreeDecorator {
    private final BlockStateProvider field_21316;

    public AlterGroundTreeDecorator(BlockStateProvider arg) {
        super(TreeDecoratorType.ALTER_GROUND);
        this.field_21316 = arg;
    }

    public <T> AlterGroundTreeDecorator(Dynamic<T> dynamic) {
        this((BlockStateProvider)Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier((String)dynamic.get("provider").get("type").asString().orElseThrow(RuntimeException::new))).deserialize(dynamic.get("provider").orElseEmptyMap()));
    }

    @Override
    public void generate(WorldAccess arg3, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox arg22) {
        int i = list.get(0).getY();
        list.stream().filter(arg -> arg.getY() == i).forEach(arg2 -> {
            this.method_23462(arg3, random, arg2.west().north());
            this.method_23462(arg3, random, arg2.east(2).north());
            this.method_23462(arg3, random, arg2.west().south(2));
            this.method_23462(arg3, random, arg2.east(2).south(2));
            for (int i = 0; i < 5; ++i) {
                int j = random.nextInt(64);
                int k = j % 8;
                int l = j / 8;
                if (k != 0 && k != 7 && l != 0 && l != 7) continue;
                this.method_23462(arg3, random, arg2.add(-3 + k, 0, -3 + l));
            }
        });
    }

    private void method_23462(ModifiableTestableWorld arg, Random random, BlockPos arg2) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) == 2 && Math.abs(j) == 2) continue;
                this.method_23463(arg, random, arg2.add(i, 0, j));
            }
        }
    }

    private void method_23463(ModifiableTestableWorld arg, Random random, BlockPos arg2) {
        for (int i = 2; i >= -3; --i) {
            BlockPos lv = arg2.up(i);
            if (Feature.method_27368(arg, lv)) {
                arg.setBlockState(lv, this.field_21316.getBlockState(random, arg2), 19);
                break;
            }
            if (!Feature.method_27370(arg, lv) && i < 0) break;
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString()), (Object)dynamicOps.createString("provider"), this.field_21316.serialize(dynamicOps)))).getValue();
    }
}

