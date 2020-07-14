/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerLidCollisions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ShulkerBoxBlock
extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
    public static final Identifier CONTENTS = new Identifier("contents");
    @Nullable
    private final DyeColor color;

    public ShulkerBoxBlock(@Nullable DyeColor color, AbstractBlock.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new ShulkerBoxBlockEntity(this.color);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (player.isSpectator()) {
            return ActionResult.CONSUME;
        }
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof ShulkerBoxBlockEntity) {
            boolean bl2;
            ShulkerBoxBlockEntity lv2 = (ShulkerBoxBlockEntity)lv;
            if (lv2.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                Direction lv3 = state.get(FACING);
                boolean bl = world.doesNotCollide(ShulkerLidCollisions.getLidCollisionBox(pos, lv3));
            } else {
                bl2 = true;
            }
            if (bl2) {
                player.openHandledScreen(lv2);
                player.incrementStat(Stats.OPEN_SHULKER_BOX);
                PiglinBrain.onGuardedBlockBroken(player, true);
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity lv2 = (ShulkerBoxBlockEntity)lv;
            if (!world.isClient && player.isCreative() && !lv2.isEmpty()) {
                ItemStack lv3 = ShulkerBoxBlock.getItemStack(this.getColor());
                CompoundTag lv4 = lv2.serializeInventory(new CompoundTag());
                if (!lv4.isEmpty()) {
                    lv3.putSubTag("BlockEntityTag", lv4);
                }
                if (lv2.hasCustomName()) {
                    lv3.setCustomName(lv2.getCustomName());
                }
                ItemEntity lv5 = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, lv3);
                lv5.setToDefaultPickupDelay();
                world.spawnEntity(lv5);
            } else {
                lv2.checkLootInteraction(player);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity lv = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (lv instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity lv2 = (ShulkerBoxBlockEntity)lv;
            builder = builder.putDrop(CONTENTS, (arg2, consumer) -> {
                for (int i = 0; i < lv2.size(); ++i) {
                    consumer.accept(lv2.getStack(i));
                }
            });
        }
        return super.getDroppedStacks(state, builder);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity lv;
        if (itemStack.hasCustomName() && (lv = world.getBlockEntity(pos)) instanceof ShulkerBoxBlockEntity) {
            ((ShulkerBoxBlockEntity)lv).setCustomName(itemStack.getName());
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof ShulkerBoxBlockEntity) {
            world.updateComparators(pos, state.getBlock());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        CompoundTag lv = stack.getSubTag("BlockEntityTag");
        if (lv != null) {
            if (lv.contains("LootTable", 8)) {
                tooltip.add(new LiteralText("???????"));
            }
            if (lv.contains("Items", 9)) {
                DefaultedList<ItemStack> lv2 = DefaultedList.ofSize(27, ItemStack.EMPTY);
                Inventories.fromTag(lv, lv2);
                int i = 0;
                int j = 0;
                for (ItemStack lv3 : lv2) {
                    if (lv3.isEmpty()) continue;
                    ++j;
                    if (i > 4) continue;
                    ++i;
                    MutableText lv4 = lv3.getName().shallowCopy();
                    lv4.append(" x").append(String.valueOf(lv3.getCount()));
                    tooltip.add(lv4);
                }
                if (j - i > 0) {
                    tooltip.add(new TranslatableText("container.shulkerBox.more", j - i).formatted(Formatting.ITALIC));
                }
            }
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof ShulkerBoxBlockEntity) {
            return VoxelShapes.cuboid(((ShulkerBoxBlockEntity)lv).getBoundingBox(state));
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput((Inventory)((Object)world.getBlockEntity(pos)));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack lv = super.getPickStack(world, pos, state);
        ShulkerBoxBlockEntity lv2 = (ShulkerBoxBlockEntity)world.getBlockEntity(pos);
        CompoundTag lv3 = lv2.serializeInventory(new CompoundTag());
        if (!lv3.isEmpty()) {
            lv.putSubTag("BlockEntityTag", lv3);
        }
        return lv;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static DyeColor getColor(Item item) {
        return ShulkerBoxBlock.getColor(Block.getBlockFromItem(item));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static DyeColor getColor(Block block) {
        if (block instanceof ShulkerBoxBlock) {
            return ((ShulkerBoxBlock)block).getColor();
        }
        return null;
    }

    public static Block get(@Nullable DyeColor dyeColor) {
        if (dyeColor == null) {
            return Blocks.SHULKER_BOX;
        }
        switch (dyeColor) {
            case WHITE: {
                return Blocks.WHITE_SHULKER_BOX;
            }
            case ORANGE: {
                return Blocks.ORANGE_SHULKER_BOX;
            }
            case MAGENTA: {
                return Blocks.MAGENTA_SHULKER_BOX;
            }
            case LIGHT_BLUE: {
                return Blocks.LIGHT_BLUE_SHULKER_BOX;
            }
            case YELLOW: {
                return Blocks.YELLOW_SHULKER_BOX;
            }
            case LIME: {
                return Blocks.LIME_SHULKER_BOX;
            }
            case PINK: {
                return Blocks.PINK_SHULKER_BOX;
            }
            case GRAY: {
                return Blocks.GRAY_SHULKER_BOX;
            }
            case LIGHT_GRAY: {
                return Blocks.LIGHT_GRAY_SHULKER_BOX;
            }
            case CYAN: {
                return Blocks.CYAN_SHULKER_BOX;
            }
            default: {
                return Blocks.PURPLE_SHULKER_BOX;
            }
            case BLUE: {
                return Blocks.BLUE_SHULKER_BOX;
            }
            case BROWN: {
                return Blocks.BROWN_SHULKER_BOX;
            }
            case GREEN: {
                return Blocks.GREEN_SHULKER_BOX;
            }
            case RED: {
                return Blocks.RED_SHULKER_BOX;
            }
            case BLACK: 
        }
        return Blocks.BLACK_SHULKER_BOX;
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getItemStack(@Nullable DyeColor color) {
        return new ItemStack(ShulkerBoxBlock.get(color));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}

