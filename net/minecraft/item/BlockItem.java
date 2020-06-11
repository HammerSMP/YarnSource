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

    public BlockItem(Block arg, Item.Settings arg2) {
        super(arg2);
        this.block = arg;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        ActionResult lv = this.place(new ItemPlacementContext(arg));
        if (!lv.isAccepted() && this.isFood()) {
            return this.use(arg.world, arg.player, arg.hand).getResult();
        }
        return lv;
    }

    public ActionResult place(ItemPlacementContext arg) {
        if (!arg.canPlace()) {
            return ActionResult.FAIL;
        }
        ItemPlacementContext lv = this.getPlacementContext(arg);
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

    protected SoundEvent getPlaceSound(BlockState arg) {
        return arg.getSoundGroup().getPlaceSound();
    }

    @Nullable
    public ItemPlacementContext getPlacementContext(ItemPlacementContext arg) {
        return arg;
    }

    protected boolean postPlacement(BlockPos arg, World arg2, @Nullable PlayerEntity arg3, ItemStack arg4, BlockState arg5) {
        return BlockItem.writeTagToBlockEntity(arg2, arg3, arg, arg4);
    }

    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = this.getBlock().getPlacementState(arg);
        return lv != null && this.canPlace(arg, lv) ? lv : null;
    }

    private BlockState placeFromTag(BlockPos arg, World arg2, ItemStack arg3, BlockState arg4) {
        BlockState lv = arg4;
        CompoundTag lv2 = arg3.getTag();
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
        if (lv != arg4) {
            arg2.setBlockState(arg, lv, 2);
        }
        return lv;
    }

    private static <T extends Comparable<T>> BlockState with(BlockState arg, Property<T> arg2, String string) {
        return arg2.parse(string).map(comparable -> (BlockState)arg.with(arg2, comparable)).orElse(arg);
    }

    protected boolean canPlace(ItemPlacementContext arg, BlockState arg2) {
        PlayerEntity lv = arg.getPlayer();
        ShapeContext lv2 = lv == null ? ShapeContext.absent() : ShapeContext.of(lv);
        return (!this.checkStatePlacement() || arg2.canPlaceAt(arg.getWorld(), arg.getBlockPos())) && arg.getWorld().canPlace(arg2, arg.getBlockPos(), lv2);
    }

    protected boolean checkStatePlacement() {
        return true;
    }

    protected boolean place(ItemPlacementContext arg, BlockState arg2) {
        return arg.getWorld().setBlockState(arg.getBlockPos(), arg2, 11);
    }

    public static boolean writeTagToBlockEntity(World arg, @Nullable PlayerEntity arg2, BlockPos arg3, ItemStack arg4) {
        BlockEntity lv2;
        MinecraftServer minecraftServer = arg.getServer();
        if (minecraftServer == null) {
            return false;
        }
        CompoundTag lv = arg4.getSubTag("BlockEntityTag");
        if (lv != null && (lv2 = arg.getBlockEntity(arg3)) != null) {
            if (!(arg.isClient || !lv2.copyItemDataRequiresOperator() || arg2 != null && arg2.isCreativeLevelTwoOp())) {
                return false;
            }
            CompoundTag lv3 = lv2.toTag(new CompoundTag());
            CompoundTag lv4 = lv3.copy();
            lv3.copyFrom(lv);
            lv3.putInt("x", arg3.getX());
            lv3.putInt("y", arg3.getY());
            lv3.putInt("z", arg3.getZ());
            if (!lv3.equals(lv4)) {
                lv2.fromTag(arg.getBlockState(arg3), lv3);
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
    public void appendStacks(ItemGroup arg, DefaultedList<ItemStack> arg2) {
        if (this.isIn(arg)) {
            this.getBlock().addStacksForDisplay(arg, arg2);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        super.appendTooltip(arg, arg2, list, arg3);
        this.getBlock().buildTooltip(arg, arg2, list, arg3);
    }

    public Block getBlock() {
        return this.block;
    }

    public void appendBlocks(Map<Block, Item> map, Item arg) {
        map.put(this.getBlock(), arg);
    }
}

