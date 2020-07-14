/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItem
extends Item {
    @Deprecated
    private final Block block;

    public BlockItem(Block block, Item.Settings settings) {
        super(settings);
        this.block = block;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult lv = this.place(new ItemPlacementContext(context));
        if (!lv.isAccepted() && this.isFood()) {
            return this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
        }
        return lv;
    }

    public ActionResult place(ItemPlacementContext context) {
        if (!context.canPlace()) {
            return ActionResult.FAIL;
        }
        ItemPlacementContext lv = this.getPlacementContext(context);
        if (lv == null) {
            return ActionResult.FAIL;
        }
        BlockState lv2 = this.getPlacementState(lv);
        if (lv2 == null) {
            return ActionResult.FAIL;
        }
        if (!this.place(lv, lv2)) {
            return ActionResult.FAIL;
        }
        BlockPos lv3 = lv.getBlockPos();
        World lv4 = lv.getWorld();
        PlayerEntity lv5 = lv.getPlayer();
        ItemStack lv6 = lv.getStack();
        BlockState lv7 = lv4.getBlockState(lv3);
        Block lv8 = lv7.getBlock();
        if (lv8 == lv2.getBlock()) {
            lv7 = this.placeFromTag(lv3, lv4, lv6, lv7);
            this.postPlacement(lv3, lv4, lv5, lv6, lv7);
            lv8.onPlaced(lv4, lv3, lv7, lv5, lv6);
            if (lv5 instanceof ServerPlayerEntity) {
                Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)lv5, lv3, lv6);
            }
        }
        BlockSoundGroup lv9 = lv7.getSoundGroup();
        lv4.playSound(lv5, lv3, this.getPlaceSound(lv7), SoundCategory.BLOCKS, (lv9.getVolume() + 1.0f) / 2.0f, lv9.getPitch() * 0.8f);
        if (lv5 == null || !lv5.abilities.creativeMode) {
            lv6.decrement(1);
        }
        return ActionResult.success(lv4.isClient);
    }

    protected SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundGroup().getPlaceSound();
    }

    @Nullable
    public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
        return context;
    }

    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        return BlockItem.writeTagToBlockEntity(world, player, pos, stack);
    }

    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext context) {
        BlockState lv = this.getBlock().getPlacementState(context);
        return lv != null && this.canPlace(context, lv) ? lv : null;
    }

    private BlockState placeFromTag(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockState lv = state;
        CompoundTag lv2 = stack.getTag();
        if (lv2 != null) {
            CompoundTag lv3 = lv2.getCompound("BlockStateTag");
            StateManager<Block, BlockState> lv4 = lv.getBlock().getStateManager();
            for (String string : lv3.getKeys()) {
                Property<?> lv5 = lv4.getProperty(string);
                if (lv5 == null) continue;
                String string2 = lv3.get(string).asString();
                lv = BlockItem.with(lv, lv5, string2);
            }
        }
        if (lv != state) {
            world.setBlockState(pos, lv, 2);
        }
        return lv;
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return property.parse(name).map(value -> (BlockState)state.with(property, value)).orElse(state);
    }

    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        PlayerEntity lv = context.getPlayer();
        ShapeContext lv2 = lv == null ? ShapeContext.absent() : ShapeContext.of(lv);
        return (!this.checkStatePlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos())) && context.getWorld().canPlace(state, context.getBlockPos(), lv2);
    }

    protected boolean checkStatePlacement() {
        return true;
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 11);
    }

    public static boolean writeTagToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
        BlockEntity lv2;
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer == null) {
            return false;
        }
        CompoundTag lv = stack.getSubTag("BlockEntityTag");
        if (lv != null && (lv2 = world.getBlockEntity(pos)) != null) {
            if (!(world.isClient || !lv2.copyItemDataRequiresOperator() || player != null && player.isCreativeLevelTwoOp())) {
                return false;
            }
            CompoundTag lv3 = lv2.toTag(new CompoundTag());
            CompoundTag lv4 = lv3.copy();
            lv3.copyFrom(lv);
            lv3.putInt("x", pos.getX());
            lv3.putInt("y", pos.getY());
            lv3.putInt("z", pos.getZ());
            if (!lv3.equals(lv4)) {
                lv2.fromTag(world.getBlockState(pos), lv3);
                lv2.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTranslationKey() {
        return this.getBlock().getTranslationKey();
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            this.getBlock().addStacksForDisplay(group, stacks);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        this.getBlock().appendTooltip(stack, world, tooltip, context);
    }

    public Block getBlock() {
        return this.block;
    }

    public void appendBlocks(Map<Block, Item> map, Item item) {
        map.put(this.getBlock(), item);
    }
}

