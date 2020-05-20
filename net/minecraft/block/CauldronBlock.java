/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CauldronBlock
extends Block {
    public static final IntProperty LEVEL = Properties.LEVEL_3;
    private static final VoxelShape RAY_TRACE_SHAPE = CauldronBlock.createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(CauldronBlock.createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), CauldronBlock.createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), CauldronBlock.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), RAY_TRACE_SHAPE), BooleanBiFunction.ONLY_FIRST);

    public CauldronBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return RAY_TRACE_SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        int i = arg.get(LEVEL);
        float f = (float)arg3.getY() + (6.0f + (float)(3 * i)) / 16.0f;
        if (!arg2.isClient && arg4.isOnFire() && i > 0 && arg4.getY() <= (double)f) {
            arg4.extinguish();
            this.setLevel(arg2, arg3, arg, i - 1);
        }
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        DyeableItem lv5;
        ItemStack lv = arg4.getStackInHand(arg5);
        if (lv.isEmpty()) {
            return ActionResult.PASS;
        }
        int i = arg.get(LEVEL);
        Item lv2 = lv.getItem();
        if (lv2 == Items.WATER_BUCKET) {
            if (i < 3 && !arg2.isClient) {
                if (!arg4.abilities.creativeMode) {
                    arg4.setStackInHand(arg5, new ItemStack(Items.BUCKET));
                }
                arg4.incrementStat(Stats.FILL_CAULDRON);
                this.setLevel(arg2, arg3, arg, 3);
                arg2.playSound(null, arg3, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }
        if (lv2 == Items.BUCKET) {
            if (i == 3 && !arg2.isClient) {
                if (!arg4.abilities.creativeMode) {
                    lv.decrement(1);
                    if (lv.isEmpty()) {
                        arg4.setStackInHand(arg5, new ItemStack(Items.WATER_BUCKET));
                    } else if (!arg4.inventory.insertStack(new ItemStack(Items.WATER_BUCKET))) {
                        arg4.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                    }
                }
                arg4.incrementStat(Stats.USE_CAULDRON);
                this.setLevel(arg2, arg3, arg, 0);
                arg2.playSound(null, arg3, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }
        if (lv2 == Items.GLASS_BOTTLE) {
            if (i > 0 && !arg2.isClient) {
                if (!arg4.abilities.creativeMode) {
                    ItemStack lv3 = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                    arg4.incrementStat(Stats.USE_CAULDRON);
                    lv.decrement(1);
                    if (lv.isEmpty()) {
                        arg4.setStackInHand(arg5, lv3);
                    } else if (!arg4.inventory.insertStack(lv3)) {
                        arg4.dropItem(lv3, false);
                    } else if (arg4 instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)arg4).openHandledScreen(arg4.playerScreenHandler);
                    }
                }
                arg2.playSound(null, arg3, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setLevel(arg2, arg3, arg, i - 1);
            }
            return ActionResult.SUCCESS;
        }
        if (lv2 == Items.POTION && PotionUtil.getPotion(lv) == Potions.WATER) {
            if (i < 3 && !arg2.isClient) {
                if (!arg4.abilities.creativeMode) {
                    ItemStack lv4 = new ItemStack(Items.GLASS_BOTTLE);
                    arg4.incrementStat(Stats.USE_CAULDRON);
                    arg4.setStackInHand(arg5, lv4);
                    if (arg4 instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)arg4).openHandledScreen(arg4.playerScreenHandler);
                    }
                }
                arg2.playSound(null, arg3, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setLevel(arg2, arg3, arg, i + 1);
            }
            return ActionResult.SUCCESS;
        }
        if (i > 0 && lv2 instanceof DyeableItem && (lv5 = (DyeableItem)((Object)lv2)).hasColor(lv) && !arg2.isClient) {
            lv5.removeColor(lv);
            this.setLevel(arg2, arg3, arg, i - 1);
            arg4.incrementStat(Stats.CLEAN_ARMOR);
            return ActionResult.SUCCESS;
        }
        if (i > 0 && lv2 instanceof BannerItem) {
            if (BannerBlockEntity.getPatternCount(lv) > 0 && !arg2.isClient) {
                ItemStack lv6 = lv.copy();
                lv6.setCount(1);
                BannerBlockEntity.loadFromItemStack(lv6);
                arg4.incrementStat(Stats.CLEAN_BANNER);
                if (!arg4.abilities.creativeMode) {
                    lv.decrement(1);
                    this.setLevel(arg2, arg3, arg, i - 1);
                }
                if (lv.isEmpty()) {
                    arg4.setStackInHand(arg5, lv6);
                } else if (!arg4.inventory.insertStack(lv6)) {
                    arg4.dropItem(lv6, false);
                } else if (arg4 instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)arg4).openHandledScreen(arg4.playerScreenHandler);
                }
            }
            return ActionResult.SUCCESS;
        }
        if (i > 0 && lv2 instanceof BlockItem) {
            Block lv7 = ((BlockItem)lv2).getBlock();
            if (lv7 instanceof ShulkerBoxBlock && !arg2.isClient()) {
                ItemStack lv8 = new ItemStack(Blocks.SHULKER_BOX, 1);
                if (lv.hasTag()) {
                    lv8.setTag(lv.getTag().copy());
                }
                arg4.setStackInHand(arg5, lv8);
                this.setLevel(arg2, arg3, arg, i - 1);
                arg4.incrementStat(Stats.CLEAN_SHULKER_BOX);
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    public void setLevel(World arg, BlockPos arg2, BlockState arg3, int i) {
        arg.setBlockState(arg2, (BlockState)arg3.with(LEVEL, MathHelper.clamp(i, 0, 3)), 2);
        arg.updateComparators(arg2, this);
    }

    @Override
    public void rainTick(World arg, BlockPos arg2) {
        if (arg.random.nextInt(20) != 1) {
            return;
        }
        float f = arg.getBiome(arg2).getTemperature(arg2);
        if (f < 0.15f) {
            return;
        }
        BlockState lv = arg.getBlockState(arg2);
        if (lv.get(LEVEL) < 3) {
            arg.setBlockState(arg2, (BlockState)lv.method_28493(LEVEL), 2);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        return arg.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(LEVEL);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

