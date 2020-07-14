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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
        return RAY_TRACE_SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        int i = state.get(LEVEL);
        float f = (float)pos.getY() + (6.0f + (float)(3 * i)) / 16.0f;
        if (!world.isClient && entity.isOnFire() && i > 0 && entity.getY() <= (double)f) {
            entity.extinguish();
            this.setLevel(world, pos, state, i - 1);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        DyeableItem lv5;
        ItemStack lv = player.getStackInHand(hand);
        if (lv.isEmpty()) {
            return ActionResult.PASS;
        }
        int i = state.get(LEVEL);
        Item lv2 = lv.getItem();
        if (lv2 == Items.WATER_BUCKET) {
            if (i < 3 && !world.isClient) {
                if (!player.abilities.creativeMode) {
                    player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                }
                player.incrementStat(Stats.FILL_CAULDRON);
                this.setLevel(world, pos, state, 3);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.success(world.isClient);
        }
        if (lv2 == Items.BUCKET) {
            if (i == 3 && !world.isClient) {
                if (!player.abilities.creativeMode) {
                    lv.decrement(1);
                    if (lv.isEmpty()) {
                        player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
                    } else if (!player.inventory.insertStack(new ItemStack(Items.WATER_BUCKET))) {
                        player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                    }
                }
                player.incrementStat(Stats.USE_CAULDRON);
                this.setLevel(world, pos, state, 0);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.success(world.isClient);
        }
        if (lv2 == Items.GLASS_BOTTLE) {
            if (i > 0 && !world.isClient) {
                if (!player.abilities.creativeMode) {
                    ItemStack lv3 = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                    player.incrementStat(Stats.USE_CAULDRON);
                    lv.decrement(1);
                    if (lv.isEmpty()) {
                        player.setStackInHand(hand, lv3);
                    } else if (!player.inventory.insertStack(lv3)) {
                        player.dropItem(lv3, false);
                    } else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).openHandledScreen(player.playerScreenHandler);
                    }
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setLevel(world, pos, state, i - 1);
            }
            return ActionResult.success(world.isClient);
        }
        if (lv2 == Items.POTION && PotionUtil.getPotion(lv) == Potions.WATER) {
            if (i < 3 && !world.isClient) {
                if (!player.abilities.creativeMode) {
                    ItemStack lv4 = new ItemStack(Items.GLASS_BOTTLE);
                    player.incrementStat(Stats.USE_CAULDRON);
                    player.setStackInHand(hand, lv4);
                    if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).openHandledScreen(player.playerScreenHandler);
                    }
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setLevel(world, pos, state, i + 1);
            }
            return ActionResult.success(world.isClient);
        }
        if (i > 0 && lv2 instanceof DyeableItem && (lv5 = (DyeableItem)((Object)lv2)).hasColor(lv) && !world.isClient) {
            lv5.removeColor(lv);
            this.setLevel(world, pos, state, i - 1);
            player.incrementStat(Stats.CLEAN_ARMOR);
            return ActionResult.SUCCESS;
        }
        if (i > 0 && lv2 instanceof BannerItem) {
            if (BannerBlockEntity.getPatternCount(lv) > 0 && !world.isClient) {
                ItemStack lv6 = lv.copy();
                lv6.setCount(1);
                BannerBlockEntity.loadFromItemStack(lv6);
                player.incrementStat(Stats.CLEAN_BANNER);
                if (!player.abilities.creativeMode) {
                    lv.decrement(1);
                    this.setLevel(world, pos, state, i - 1);
                }
                if (lv.isEmpty()) {
                    player.setStackInHand(hand, lv6);
                } else if (!player.inventory.insertStack(lv6)) {
                    player.dropItem(lv6, false);
                } else if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)player).openHandledScreen(player.playerScreenHandler);
                }
            }
            return ActionResult.success(world.isClient);
        }
        if (i > 0 && lv2 instanceof BlockItem) {
            Block lv7 = ((BlockItem)lv2).getBlock();
            if (lv7 instanceof ShulkerBoxBlock && !world.isClient()) {
                ItemStack lv8 = new ItemStack(Blocks.SHULKER_BOX, 1);
                if (lv.hasTag()) {
                    lv8.setTag(lv.getTag().copy());
                }
                player.setStackInHand(hand, lv8);
                this.setLevel(world, pos, state, i - 1);
                player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    public void setLevel(World world, BlockPos pos, BlockState state, int level) {
        world.setBlockState(pos, (BlockState)state.with(LEVEL, MathHelper.clamp(level, 0, 3)), 2);
        world.updateComparators(pos, this);
    }

    @Override
    public void rainTick(World world, BlockPos pos) {
        if (world.random.nextInt(20) != 1) {
            return;
        }
        float f = world.getBiome(pos).getTemperature(pos);
        if (f < 0.15f) {
            return;
        }
        BlockState lv = world.getBlockState(pos);
        if (lv.get(LEVEL) < 3) {
            world.setBlockState(pos, (BlockState)lv.cycle(LEVEL), 2);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

