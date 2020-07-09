/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RenderLayers {
    private static final Map<Block, RenderLayer> BLOCKS = Util.make(Maps.newHashMap(), hashMap -> {
        RenderLayer lv = RenderLayer.getTripwire();
        hashMap.put(Blocks.TRIPWIRE, lv);
        RenderLayer lv2 = RenderLayer.getCutoutMipped();
        hashMap.put(Blocks.GRASS_BLOCK, lv2);
        hashMap.put(Blocks.IRON_BARS, lv2);
        hashMap.put(Blocks.GLASS_PANE, lv2);
        hashMap.put(Blocks.TRIPWIRE_HOOK, lv2);
        hashMap.put(Blocks.HOPPER, lv2);
        hashMap.put(Blocks.CHAIN, lv2);
        hashMap.put(Blocks.JUNGLE_LEAVES, lv2);
        hashMap.put(Blocks.OAK_LEAVES, lv2);
        hashMap.put(Blocks.SPRUCE_LEAVES, lv2);
        hashMap.put(Blocks.ACACIA_LEAVES, lv2);
        hashMap.put(Blocks.BIRCH_LEAVES, lv2);
        hashMap.put(Blocks.DARK_OAK_LEAVES, lv2);
        RenderLayer lv3 = RenderLayer.getCutout();
        hashMap.put(Blocks.OAK_SAPLING, lv3);
        hashMap.put(Blocks.SPRUCE_SAPLING, lv3);
        hashMap.put(Blocks.BIRCH_SAPLING, lv3);
        hashMap.put(Blocks.JUNGLE_SAPLING, lv3);
        hashMap.put(Blocks.ACACIA_SAPLING, lv3);
        hashMap.put(Blocks.DARK_OAK_SAPLING, lv3);
        hashMap.put(Blocks.GLASS, lv3);
        hashMap.put(Blocks.WHITE_BED, lv3);
        hashMap.put(Blocks.ORANGE_BED, lv3);
        hashMap.put(Blocks.MAGENTA_BED, lv3);
        hashMap.put(Blocks.LIGHT_BLUE_BED, lv3);
        hashMap.put(Blocks.YELLOW_BED, lv3);
        hashMap.put(Blocks.LIME_BED, lv3);
        hashMap.put(Blocks.PINK_BED, lv3);
        hashMap.put(Blocks.GRAY_BED, lv3);
        hashMap.put(Blocks.LIGHT_GRAY_BED, lv3);
        hashMap.put(Blocks.CYAN_BED, lv3);
        hashMap.put(Blocks.PURPLE_BED, lv3);
        hashMap.put(Blocks.BLUE_BED, lv3);
        hashMap.put(Blocks.BROWN_BED, lv3);
        hashMap.put(Blocks.GREEN_BED, lv3);
        hashMap.put(Blocks.RED_BED, lv3);
        hashMap.put(Blocks.BLACK_BED, lv3);
        hashMap.put(Blocks.POWERED_RAIL, lv3);
        hashMap.put(Blocks.DETECTOR_RAIL, lv3);
        hashMap.put(Blocks.COBWEB, lv3);
        hashMap.put(Blocks.GRASS, lv3);
        hashMap.put(Blocks.FERN, lv3);
        hashMap.put(Blocks.DEAD_BUSH, lv3);
        hashMap.put(Blocks.SEAGRASS, lv3);
        hashMap.put(Blocks.TALL_SEAGRASS, lv3);
        hashMap.put(Blocks.DANDELION, lv3);
        hashMap.put(Blocks.POPPY, lv3);
        hashMap.put(Blocks.BLUE_ORCHID, lv3);
        hashMap.put(Blocks.ALLIUM, lv3);
        hashMap.put(Blocks.AZURE_BLUET, lv3);
        hashMap.put(Blocks.RED_TULIP, lv3);
        hashMap.put(Blocks.ORANGE_TULIP, lv3);
        hashMap.put(Blocks.WHITE_TULIP, lv3);
        hashMap.put(Blocks.PINK_TULIP, lv3);
        hashMap.put(Blocks.OXEYE_DAISY, lv3);
        hashMap.put(Blocks.CORNFLOWER, lv3);
        hashMap.put(Blocks.WITHER_ROSE, lv3);
        hashMap.put(Blocks.LILY_OF_THE_VALLEY, lv3);
        hashMap.put(Blocks.BROWN_MUSHROOM, lv3);
        hashMap.put(Blocks.RED_MUSHROOM, lv3);
        hashMap.put(Blocks.TORCH, lv3);
        hashMap.put(Blocks.WALL_TORCH, lv3);
        hashMap.put(Blocks.SOUL_TORCH, lv3);
        hashMap.put(Blocks.SOUL_WALL_TORCH, lv3);
        hashMap.put(Blocks.FIRE, lv3);
        hashMap.put(Blocks.SOUL_FIRE, lv3);
        hashMap.put(Blocks.SPAWNER, lv3);
        hashMap.put(Blocks.REDSTONE_WIRE, lv3);
        hashMap.put(Blocks.WHEAT, lv3);
        hashMap.put(Blocks.OAK_DOOR, lv3);
        hashMap.put(Blocks.LADDER, lv3);
        hashMap.put(Blocks.RAIL, lv3);
        hashMap.put(Blocks.IRON_DOOR, lv3);
        hashMap.put(Blocks.REDSTONE_TORCH, lv3);
        hashMap.put(Blocks.REDSTONE_WALL_TORCH, lv3);
        hashMap.put(Blocks.CACTUS, lv3);
        hashMap.put(Blocks.SUGAR_CANE, lv3);
        hashMap.put(Blocks.REPEATER, lv3);
        hashMap.put(Blocks.OAK_TRAPDOOR, lv3);
        hashMap.put(Blocks.SPRUCE_TRAPDOOR, lv3);
        hashMap.put(Blocks.BIRCH_TRAPDOOR, lv3);
        hashMap.put(Blocks.JUNGLE_TRAPDOOR, lv3);
        hashMap.put(Blocks.ACACIA_TRAPDOOR, lv3);
        hashMap.put(Blocks.DARK_OAK_TRAPDOOR, lv3);
        hashMap.put(Blocks.CRIMSON_TRAPDOOR, lv3);
        hashMap.put(Blocks.WARPED_TRAPDOOR, lv3);
        hashMap.put(Blocks.ATTACHED_PUMPKIN_STEM, lv3);
        hashMap.put(Blocks.ATTACHED_MELON_STEM, lv3);
        hashMap.put(Blocks.PUMPKIN_STEM, lv3);
        hashMap.put(Blocks.MELON_STEM, lv3);
        hashMap.put(Blocks.VINE, lv3);
        hashMap.put(Blocks.LILY_PAD, lv3);
        hashMap.put(Blocks.NETHER_WART, lv3);
        hashMap.put(Blocks.BREWING_STAND, lv3);
        hashMap.put(Blocks.COCOA, lv3);
        hashMap.put(Blocks.BEACON, lv3);
        hashMap.put(Blocks.FLOWER_POT, lv3);
        hashMap.put(Blocks.POTTED_OAK_SAPLING, lv3);
        hashMap.put(Blocks.POTTED_SPRUCE_SAPLING, lv3);
        hashMap.put(Blocks.POTTED_BIRCH_SAPLING, lv3);
        hashMap.put(Blocks.POTTED_JUNGLE_SAPLING, lv3);
        hashMap.put(Blocks.POTTED_ACACIA_SAPLING, lv3);
        hashMap.put(Blocks.POTTED_DARK_OAK_SAPLING, lv3);
        hashMap.put(Blocks.POTTED_FERN, lv3);
        hashMap.put(Blocks.POTTED_DANDELION, lv3);
        hashMap.put(Blocks.POTTED_POPPY, lv3);
        hashMap.put(Blocks.POTTED_BLUE_ORCHID, lv3);
        hashMap.put(Blocks.POTTED_ALLIUM, lv3);
        hashMap.put(Blocks.POTTED_AZURE_BLUET, lv3);
        hashMap.put(Blocks.POTTED_RED_TULIP, lv3);
        hashMap.put(Blocks.POTTED_ORANGE_TULIP, lv3);
        hashMap.put(Blocks.POTTED_WHITE_TULIP, lv3);
        hashMap.put(Blocks.POTTED_PINK_TULIP, lv3);
        hashMap.put(Blocks.POTTED_OXEYE_DAISY, lv3);
        hashMap.put(Blocks.POTTED_CORNFLOWER, lv3);
        hashMap.put(Blocks.POTTED_LILY_OF_THE_VALLEY, lv3);
        hashMap.put(Blocks.POTTED_WITHER_ROSE, lv3);
        hashMap.put(Blocks.POTTED_RED_MUSHROOM, lv3);
        hashMap.put(Blocks.POTTED_BROWN_MUSHROOM, lv3);
        hashMap.put(Blocks.POTTED_DEAD_BUSH, lv3);
        hashMap.put(Blocks.POTTED_CACTUS, lv3);
        hashMap.put(Blocks.CARROTS, lv3);
        hashMap.put(Blocks.POTATOES, lv3);
        hashMap.put(Blocks.COMPARATOR, lv3);
        hashMap.put(Blocks.ACTIVATOR_RAIL, lv3);
        hashMap.put(Blocks.IRON_TRAPDOOR, lv3);
        hashMap.put(Blocks.SUNFLOWER, lv3);
        hashMap.put(Blocks.LILAC, lv3);
        hashMap.put(Blocks.ROSE_BUSH, lv3);
        hashMap.put(Blocks.PEONY, lv3);
        hashMap.put(Blocks.TALL_GRASS, lv3);
        hashMap.put(Blocks.LARGE_FERN, lv3);
        hashMap.put(Blocks.SPRUCE_DOOR, lv3);
        hashMap.put(Blocks.BIRCH_DOOR, lv3);
        hashMap.put(Blocks.JUNGLE_DOOR, lv3);
        hashMap.put(Blocks.ACACIA_DOOR, lv3);
        hashMap.put(Blocks.DARK_OAK_DOOR, lv3);
        hashMap.put(Blocks.END_ROD, lv3);
        hashMap.put(Blocks.CHORUS_PLANT, lv3);
        hashMap.put(Blocks.CHORUS_FLOWER, lv3);
        hashMap.put(Blocks.BEETROOTS, lv3);
        hashMap.put(Blocks.KELP, lv3);
        hashMap.put(Blocks.KELP_PLANT, lv3);
        hashMap.put(Blocks.TURTLE_EGG, lv3);
        hashMap.put(Blocks.DEAD_TUBE_CORAL, lv3);
        hashMap.put(Blocks.DEAD_BRAIN_CORAL, lv3);
        hashMap.put(Blocks.DEAD_BUBBLE_CORAL, lv3);
        hashMap.put(Blocks.DEAD_FIRE_CORAL, lv3);
        hashMap.put(Blocks.DEAD_HORN_CORAL, lv3);
        hashMap.put(Blocks.TUBE_CORAL, lv3);
        hashMap.put(Blocks.BRAIN_CORAL, lv3);
        hashMap.put(Blocks.BUBBLE_CORAL, lv3);
        hashMap.put(Blocks.FIRE_CORAL, lv3);
        hashMap.put(Blocks.HORN_CORAL, lv3);
        hashMap.put(Blocks.DEAD_TUBE_CORAL_FAN, lv3);
        hashMap.put(Blocks.DEAD_BRAIN_CORAL_FAN, lv3);
        hashMap.put(Blocks.DEAD_BUBBLE_CORAL_FAN, lv3);
        hashMap.put(Blocks.DEAD_FIRE_CORAL_FAN, lv3);
        hashMap.put(Blocks.DEAD_HORN_CORAL_FAN, lv3);
        hashMap.put(Blocks.TUBE_CORAL_FAN, lv3);
        hashMap.put(Blocks.BRAIN_CORAL_FAN, lv3);
        hashMap.put(Blocks.BUBBLE_CORAL_FAN, lv3);
        hashMap.put(Blocks.FIRE_CORAL_FAN, lv3);
        hashMap.put(Blocks.HORN_CORAL_FAN, lv3);
        hashMap.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.TUBE_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.BRAIN_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.BUBBLE_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.FIRE_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.HORN_CORAL_WALL_FAN, lv3);
        hashMap.put(Blocks.SEA_PICKLE, lv3);
        hashMap.put(Blocks.CONDUIT, lv3);
        hashMap.put(Blocks.BAMBOO_SAPLING, lv3);
        hashMap.put(Blocks.BAMBOO, lv3);
        hashMap.put(Blocks.POTTED_BAMBOO, lv3);
        hashMap.put(Blocks.SCAFFOLDING, lv3);
        hashMap.put(Blocks.STONECUTTER, lv3);
        hashMap.put(Blocks.LANTERN, lv3);
        hashMap.put(Blocks.SOUL_LANTERN, lv3);
        hashMap.put(Blocks.CAMPFIRE, lv3);
        hashMap.put(Blocks.SOUL_CAMPFIRE, lv3);
        hashMap.put(Blocks.SWEET_BERRY_BUSH, lv3);
        hashMap.put(Blocks.WEEPING_VINES, lv3);
        hashMap.put(Blocks.WEEPING_VINES_PLANT, lv3);
        hashMap.put(Blocks.TWISTING_VINES, lv3);
        hashMap.put(Blocks.TWISTING_VINES_PLANT, lv3);
        hashMap.put(Blocks.NETHER_SPROUTS, lv3);
        hashMap.put(Blocks.CRIMSON_FUNGUS, lv3);
        hashMap.put(Blocks.WARPED_FUNGUS, lv3);
        hashMap.put(Blocks.CRIMSON_ROOTS, lv3);
        hashMap.put(Blocks.WARPED_ROOTS, lv3);
        hashMap.put(Blocks.POTTED_CRIMSON_FUNGUS, lv3);
        hashMap.put(Blocks.POTTED_WARPED_FUNGUS, lv3);
        hashMap.put(Blocks.POTTED_CRIMSON_ROOTS, lv3);
        hashMap.put(Blocks.POTTED_WARPED_ROOTS, lv3);
        hashMap.put(Blocks.CRIMSON_DOOR, lv3);
        hashMap.put(Blocks.WARPED_DOOR, lv3);
        RenderLayer lv4 = RenderLayer.getTranslucent();
        hashMap.put(Blocks.ICE, lv4);
        hashMap.put(Blocks.NETHER_PORTAL, lv4);
        hashMap.put(Blocks.WHITE_STAINED_GLASS, lv4);
        hashMap.put(Blocks.ORANGE_STAINED_GLASS, lv4);
        hashMap.put(Blocks.MAGENTA_STAINED_GLASS, lv4);
        hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS, lv4);
        hashMap.put(Blocks.YELLOW_STAINED_GLASS, lv4);
        hashMap.put(Blocks.LIME_STAINED_GLASS, lv4);
        hashMap.put(Blocks.PINK_STAINED_GLASS, lv4);
        hashMap.put(Blocks.GRAY_STAINED_GLASS, lv4);
        hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS, lv4);
        hashMap.put(Blocks.CYAN_STAINED_GLASS, lv4);
        hashMap.put(Blocks.PURPLE_STAINED_GLASS, lv4);
        hashMap.put(Blocks.BLUE_STAINED_GLASS, lv4);
        hashMap.put(Blocks.BROWN_STAINED_GLASS, lv4);
        hashMap.put(Blocks.GREEN_STAINED_GLASS, lv4);
        hashMap.put(Blocks.RED_STAINED_GLASS, lv4);
        hashMap.put(Blocks.BLACK_STAINED_GLASS, lv4);
        hashMap.put(Blocks.WHITE_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.ORANGE_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.MAGENTA_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.YELLOW_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.LIME_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.PINK_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.GRAY_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.CYAN_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.PURPLE_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.BLUE_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.BROWN_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.GREEN_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.RED_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.BLACK_STAINED_GLASS_PANE, lv4);
        hashMap.put(Blocks.SLIME_BLOCK, lv4);
        hashMap.put(Blocks.HONEY_BLOCK, lv4);
        hashMap.put(Blocks.FROSTED_ICE, lv4);
        hashMap.put(Blocks.BUBBLE_COLUMN, lv4);
    });
    private static final Map<Fluid, RenderLayer> FLUIDS = Util.make(Maps.newHashMap(), hashMap -> {
        RenderLayer lv = RenderLayer.getTranslucent();
        hashMap.put(Fluids.FLOWING_WATER, lv);
        hashMap.put(Fluids.WATER, lv);
    });
    private static boolean fancyGraphicsOrBetter;

    public static RenderLayer getBlockLayer(BlockState arg) {
        Block lv = arg.getBlock();
        if (lv instanceof LeavesBlock) {
            return fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
        }
        RenderLayer lv2 = BLOCKS.get(lv);
        if (lv2 != null) {
            return lv2;
        }
        return RenderLayer.getSolid();
    }

    public static RenderLayer getMovingBlockLayer(BlockState arg) {
        Block lv = arg.getBlock();
        if (lv instanceof LeavesBlock) {
            return fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
        }
        RenderLayer lv2 = BLOCKS.get(lv);
        if (lv2 != null) {
            if (lv2 == RenderLayer.getTranslucent()) {
                return RenderLayer.getTranslucentMovingBlock();
            }
            return lv2;
        }
        return RenderLayer.getSolid();
    }

    public static RenderLayer getEntityBlockLayer(BlockState arg, boolean bl) {
        RenderLayer lv = RenderLayers.getBlockLayer(arg);
        if (lv == RenderLayer.getTranslucent()) {
            if (!MinecraftClient.isFabulousGraphicsOrBetter()) {
                return TexturedRenderLayers.getEntityTranslucentCull();
            }
            return bl ? TexturedRenderLayers.getEntityTranslucentCull() : TexturedRenderLayers.getItemEntityTranslucentCull();
        }
        return TexturedRenderLayers.getEntityCutout();
    }

    public static RenderLayer getItemLayer(ItemStack arg, boolean bl) {
        Item lv = arg.getItem();
        if (lv instanceof BlockItem) {
            Block lv2 = ((BlockItem)lv).getBlock();
            return RenderLayers.getEntityBlockLayer(lv2.getDefaultState(), bl);
        }
        return bl ? TexturedRenderLayers.getEntityTranslucentCull() : TexturedRenderLayers.getItemEntityTranslucentCull();
    }

    public static RenderLayer getFluidLayer(FluidState arg) {
        RenderLayer lv = FLUIDS.get(arg.getFluid());
        if (lv != null) {
            return lv;
        }
        return RenderLayer.getSolid();
    }

    public static void setFancyGraphicsOrBetter(boolean bl) {
        fancyGraphicsOrBetter = bl;
    }
}

