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

    public ShulkerBoxBlock(@Nullable DyeColor arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.color = arg;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new ShulkerBoxBlockEntity(this.color);
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg2.isClient) {
            return ActionResult.SUCCESS;
        }
        if (arg4.isSpectator()) {
            return ActionResult.CONSUME;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof ShulkerBoxBlockEntity) {
            boolean bl2;
            ShulkerBoxBlockEntity lv2 = (ShulkerBoxBlockEntity)lv;
            if (lv2.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                Direction lv3 = arg.get(FACING);
                boolean bl = arg2.doesNotCollide(ShulkerLidCollisions.getLidCollisionBox(arg3, lv3));
            } else {
                bl2 = true;
            }
            if (bl2) {
                arg4.openHandledScreen(lv2);
                arg4.incrementStat(Stats.OPEN_SHULKER_BOX);
                PiglinBrain.onGuardedBlockBroken(arg4, true);
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(FACING, arg.getSide());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING);
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity lv2 = (ShulkerBoxBlockEntity)lv;
            if (!arg.isClient && arg4.isCreative() && !lv2.isEmpty()) {
                ItemStack lv3 = ShulkerBoxBlock.getItemStack(this.getColor());
                CompoundTag lv4 = lv2.serializeInventory(new CompoundTag());
                if (!lv4.isEmpty()) {
                    lv3.putSubTag("BlockEntityTag", lv4);
                }
                if (lv2.hasCustomName()) {
                    lv3.setCustomName(lv2.getCustomName());
                }
                ItemEntity lv5 = new ItemEntity(arg, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5, lv3);
                lv5.setToDefaultPickupDelay();
                arg.spawnEntity(lv5);
            } else {
                lv2.checkLootInteraction(arg4);
            }
        }
        super.onBreak(arg, arg2, arg3, arg4);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState arg, LootContext.Builder arg22) {
        BlockEntity lv = arg22.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (lv instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity lv2 = (ShulkerBoxBlockEntity)lv;
            arg22 = arg22.putDrop(CONTENTS, (arg2, consumer) -> {
                for (int i = 0; i < lv2.size(); ++i) {
                    consumer.accept(lv2.getStack(i));
                }
            });
        }
        return super.getDroppedStacks(arg, arg22);
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        BlockEntity lv;
        if (arg5.hasCustomName() && (lv = arg.getBlockEntity(arg2)) instanceof ShulkerBoxBlockEntity) {
            ((ShulkerBoxBlockEntity)lv).setCustomName(arg5.getName());
        }
    }

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof ShulkerBoxBlockEntity) {
            arg2.updateComparators(arg3, arg.getBlock());
        }
        super.onStateReplaced(arg, arg2, arg3, arg4, bl);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void buildTooltip(ItemStack arg, @Nullable BlockView arg2, List<Text> list, TooltipContext arg3) {
        super.buildTooltip(arg, arg2, list, arg3);
        CompoundTag lv = arg.getSubTag("BlockEntityTag");
        if (lv != null) {
            if (lv.contains("LootTable", 8)) {
                list.add(new LiteralText("???????"));
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
                    list.add(lv4);
                }
                if (j - i > 0) {
                    list.add(new TranslatableText("container.shulkerBox.more", j - i).formatted(Formatting.ITALIC));
                }
            }
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState arg) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof ShulkerBoxBlockEntity) {
            return VoxelShapes.cuboid(((ShulkerBoxBlockEntity)lv).getBoundingBox(arg));
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        return ScreenHandler.calculateComparatorOutput((Inventory)((Object)arg2.getBlockEntity(arg3)));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        ItemStack lv = super.getPickStack(arg, arg2, arg3);
        ShulkerBoxBlockEntity lv2 = (ShulkerBoxBlockEntity)arg.getBlockEntity(arg2);
        CompoundTag lv3 = lv2.serializeInventory(new CompoundTag());
        if (!lv3.isEmpty()) {
            lv.putSubTag("BlockEntityTag", lv3);
        }
        return lv;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static DyeColor getColor(Item arg) {
        return ShulkerBoxBlock.getColor(Block.getBlockFromItem(arg));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static DyeColor getColor(Block arg) {
        if (arg instanceof ShulkerBoxBlock) {
            return ((ShulkerBoxBlock)arg).getColor();
        }
        return null;
    }

    public static Block get(@Nullable DyeColor arg) {
        if (arg == null) {
            return Blocks.SHULKER_BOX;
        }
        switch (arg) {
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

    public static ItemStack getItemStack(@Nullable DyeColor arg) {
        return new ItemStack(ShulkerBoxBlock.get(arg));
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(FACING, arg2.rotate(arg.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return arg.rotate(arg2.getRotation(arg.get(FACING)));
    }
}

