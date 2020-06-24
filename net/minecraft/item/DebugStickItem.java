/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DebugStickItem
extends Item {
    public DebugStickItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public boolean hasGlint(ItemStack arg) {
        return true;
    }

    @Override
    public boolean canMine(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
        if (!arg2.isClient) {
            this.use(arg4, arg, arg2, arg3, false, arg4.getStackInHand(Hand.MAIN_HAND));
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        PlayerEntity lv = arg.getPlayer();
        World lv2 = arg.getWorld();
        if (!lv2.isClient && lv != null) {
            BlockPos lv3 = arg.getBlockPos();
            this.use(lv, lv2.getBlockState(lv3), lv2, lv3, true, arg.getStack());
        }
        return ActionResult.success(lv2.isClient);
    }

    private void use(PlayerEntity arg, BlockState arg2, WorldAccess arg3, BlockPos arg4, boolean bl, ItemStack arg5) {
        if (!arg.isCreativeLevelTwoOp()) {
            return;
        }
        Block lv = arg2.getBlock();
        StateManager<Block, BlockState> lv2 = lv.getStateManager();
        Collection<Property<?>> collection = lv2.getProperties();
        String string = Registry.BLOCK.getId(lv).toString();
        if (collection.isEmpty()) {
            DebugStickItem.sendMessage(arg, new TranslatableText(this.getTranslationKey() + ".empty", string));
            return;
        }
        CompoundTag lv3 = arg5.getOrCreateSubTag("DebugProperty");
        String string2 = lv3.getString(string);
        Property<?> lv4 = lv2.getProperty(string2);
        if (bl) {
            if (lv4 == null) {
                lv4 = collection.iterator().next();
            }
            BlockState lv5 = DebugStickItem.cycle(arg2, lv4, arg.shouldCancelInteraction());
            arg3.setBlockState(arg4, lv5, 18);
            DebugStickItem.sendMessage(arg, new TranslatableText(this.getTranslationKey() + ".update", lv4.getName(), DebugStickItem.getValueString(lv5, lv4)));
        } else {
            lv4 = DebugStickItem.cycle(collection, lv4, arg.shouldCancelInteraction());
            String string3 = lv4.getName();
            lv3.putString(string, string3);
            DebugStickItem.sendMessage(arg, new TranslatableText(this.getTranslationKey() + ".select", string3, DebugStickItem.getValueString(arg2, lv4)));
        }
    }

    private static <T extends Comparable<T>> BlockState cycle(BlockState arg, Property<T> arg2, boolean bl) {
        return (BlockState)arg.with(arg2, (Comparable)DebugStickItem.cycle(arg2.getValues(), arg.get(arg2), bl));
    }

    private static <T> T cycle(Iterable<T> iterable, @Nullable T object, boolean bl) {
        return bl ? Util.previous(iterable, object) : Util.next(iterable, object);
    }

    private static void sendMessage(PlayerEntity arg, Text arg2) {
        ((ServerPlayerEntity)arg).sendMessage(arg2, MessageType.GAME_INFO, Util.NIL_UUID);
    }

    private static <T extends Comparable<T>> String getValueString(BlockState arg, Property<T> arg2) {
        return arg2.name(arg.get(arg2));
    }
}

