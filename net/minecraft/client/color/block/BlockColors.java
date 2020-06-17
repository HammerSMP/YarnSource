/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class BlockColors {
    private final IdList<BlockColorProvider> providers = new IdList(32);
    private final Map<Block, Set<Property<?>>> properties = Maps.newHashMap();

    public static BlockColors create() {
        BlockColors lv = new BlockColors();
        lv.registerColorProvider((arg, arg2, arg3, i) -> {
            if (arg2 == null || arg3 == null) {
                return -1;
            }
            return BiomeColors.getGrassColor(arg2, arg.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER ? arg3.down() : arg3);
        }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        lv.registerColorProperty(TallPlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        lv.registerColorProvider((arg, arg2, arg3, i) -> {
            if (arg2 == null || arg3 == null) {
                return GrassColors.getColor(0.5, 1.0);
            }
            return BiomeColors.getGrassColor(arg2, arg3);
        }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
        lv.registerColorProvider((arg, arg2, arg3, i) -> FoliageColors.getSpruceColor(), Blocks.SPRUCE_LEAVES);
        lv.registerColorProvider((arg, arg2, arg3, i) -> FoliageColors.getBirchColor(), Blocks.BIRCH_LEAVES);
        lv.registerColorProvider((arg, arg2, arg3, i) -> {
            if (arg2 == null || arg3 == null) {
                return FoliageColors.getDefaultColor();
            }
            return BiomeColors.getFoliageColor(arg2, arg3);
        }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
        lv.registerColorProvider((arg, arg2, arg3, i) -> {
            if (arg2 == null || arg3 == null) {
                return -1;
            }
            return BiomeColors.getWaterColor(arg2, arg3);
        }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
        lv.registerColorProvider((arg, arg2, arg3, i) -> RedstoneWireBlock.getWireColor(arg.get(RedstoneWireBlock.POWER)), Blocks.REDSTONE_WIRE);
        lv.registerColorProperty(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
        lv.registerColorProvider((arg, arg2, arg3, i) -> {
            if (arg2 == null || arg3 == null) {
                return -1;
            }
            return BiomeColors.getGrassColor(arg2, arg3);
        }, Blocks.SUGAR_CANE);
        lv.registerColorProvider((arg, arg2, arg3, i) -> 14731036, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        lv.registerColorProvider((arg, arg2, arg3, i) -> {
            int j = arg.get(StemBlock.AGE);
            int k = j * 32;
            int l = 255 - j * 8;
            int m = j * 4;
            return k << 16 | l << 8 | m;
        }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        lv.registerColorProperty(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        lv.registerColorProvider((arg, arg2, arg3, i) -> {
            if (arg2 == null || arg3 == null) {
                return 7455580;
            }
            return 2129968;
        }, Blocks.LILY_PAD);
        return lv;
    }

    public int getColor(BlockState arg, World arg2, BlockPos arg3) {
        BlockColorProvider lv = this.providers.get(Registry.BLOCK.getRawId(arg.getBlock()));
        if (lv != null) {
            return lv.getColor(arg, null, null, 0);
        }
        MaterialColor lv2 = arg.getTopMaterialColor(arg2, arg3);
        return lv2 != null ? lv2.color : -1;
    }

    public int getColor(BlockState arg, @Nullable BlockRenderView arg2, @Nullable BlockPos arg3, int i) {
        BlockColorProvider lv = this.providers.get(Registry.BLOCK.getRawId(arg.getBlock()));
        return lv == null ? -1 : lv.getColor(arg, arg2, arg3, i);
    }

    public void registerColorProvider(BlockColorProvider arg, Block ... args) {
        for (Block lv : args) {
            this.providers.set(arg, Registry.BLOCK.getRawId(lv));
        }
    }

    private void registerColorProperties(Set<Property<?>> set, Block ... args) {
        for (Block lv : args) {
            this.properties.put(lv, set);
        }
    }

    private void registerColorProperty(Property<?> arg, Block ... args) {
        this.registerColorProperties((Set<Property<?>>)ImmutableSet.of(arg), args);
    }

    public Set<Property<?>> getProperties(Block arg) {
        return (Set)this.properties.getOrDefault(arg, (Set<Property<?>>)ImmutableSet.of());
    }
}

