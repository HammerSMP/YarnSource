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
        lv.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -1;
            }
            return BiomeColors.getGrassColor(world, state.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos);
        }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        lv.registerColorProperty(TallPlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        lv.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return GrassColors.getColor(0.5, 1.0);
            }
            return BiomeColors.getGrassColor(world, pos);
        }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
        lv.registerColorProvider((state, world, pos, tintIndex) -> FoliageColors.getSpruceColor(), Blocks.SPRUCE_LEAVES);
        lv.registerColorProvider((state, world, pos, tintIndex) -> FoliageColors.getBirchColor(), Blocks.BIRCH_LEAVES);
        lv.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return FoliageColors.getDefaultColor();
            }
            return BiomeColors.getFoliageColor(world, pos);
        }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
        lv.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -1;
            }
            return BiomeColors.getWaterColor(world, pos);
        }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
        lv.registerColorProvider((state, world, pos, tintIndex) -> RedstoneWireBlock.getWireColor(state.get(RedstoneWireBlock.POWER)), Blocks.REDSTONE_WIRE);
        lv.registerColorProperty(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
        lv.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -1;
            }
            return BiomeColors.getGrassColor(world, pos);
        }, Blocks.SUGAR_CANE);
        lv.registerColorProvider((state, world, pos, tintIndex) -> 14731036, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        lv.registerColorProvider((state, world, pos, tintIndex) -> {
            int j = state.get(StemBlock.AGE);
            int k = j * 32;
            int l = 255 - j * 8;
            int m = j * 4;
            return k << 16 | l << 8 | m;
        }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        lv.registerColorProperty(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        lv.registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return 7455580;
            }
            return 2129968;
        }, Blocks.LILY_PAD);
        return lv;
    }

    public int getColor(BlockState state, World world, BlockPos pos) {
        BlockColorProvider lv = this.providers.get(Registry.BLOCK.getRawId(state.getBlock()));
        if (lv != null) {
            return lv.getColor(state, null, null, 0);
        }
        MaterialColor lv2 = state.getTopMaterialColor(world, pos);
        return lv2 != null ? lv2.color : -1;
    }

    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tint) {
        BlockColorProvider lv = this.providers.get(Registry.BLOCK.getRawId(state.getBlock()));
        return lv == null ? -1 : lv.getColor(state, world, pos, tint);
    }

    public void registerColorProvider(BlockColorProvider provider, Block ... blocks) {
        for (Block lv : blocks) {
            this.providers.set(provider, Registry.BLOCK.getRawId(lv));
        }
    }

    private void registerColorProperties(Set<Property<?>> properties, Block ... blocks) {
        for (Block lv : blocks) {
            this.properties.put(lv, properties);
        }
    }

    private void registerColorProperty(Property<?> property, Block ... blocks) {
        this.registerColorProperties((Set<Property<?>>)ImmutableSet.of(property), blocks);
    }

    public Set<Property<?>> getProperties(Block block) {
        return (Set)this.properties.getOrDefault(block, (Set<Property<?>>)ImmutableSet.of());
    }
}

