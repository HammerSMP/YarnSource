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
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!world.isClient) {
            this.use(miner, state, world, pos, false, miner.getStackInHand(Hand.MAIN_HAND));
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity lv = context.getPlayer();
        World lv2 = context.getWorld();
        if (!lv2.isClient && lv != null) {
            BlockPos lv3 = context.getBlockPos();
            this.use(lv, lv2.getBlockState(lv3), lv2, lv3, true, context.getStack());
        }
        return ActionResult.success(lv2.isClient);
    }

    private void use(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack) {
        if (!player.isCreativeLevelTwoOp()) {
            return;
        }
        Block lv = state.getBlock();
        StateManager<Block, BlockState> lv2 = lv.getStateManager();
        Collection<Property<?>> collection = lv2.getProperties();
        String string = Registry.BLOCK.getId(lv).toString();
        if (collection.isEmpty()) {
            DebugStickItem.sendMessage(player, new TranslatableText(this.getTranslationKey() + ".empty", string));
            return;
        }
        CompoundTag lv3 = stack.getOrCreateSubTag("DebugProperty");
        String string2 = lv3.getString(string);
        Property<?> lv4 = lv2.getProperty(string2);
        if (update) {
            if (lv4 == null) {
                lv4 = collection.iterator().next();
            }
            BlockState lv5 = DebugStickItem.cycle(state, lv4, player.shouldCancelInteraction());
            world.setBlockState(pos, lv5, 18);
            DebugStickItem.sendMessage(player, new TranslatableText(this.getTranslationKey() + ".update", lv4.getName(), DebugStickItem.getValueString(lv5, lv4)));
        } else {
            lv4 = DebugStickItem.cycle(collection, lv4, player.shouldCancelInteraction());
            String string3 = lv4.getName();
            lv3.putString(string, string3);
            DebugStickItem.sendMessage(player, new TranslatableText(this.getTranslationKey() + ".select", string3, DebugStickItem.getValueString(state, lv4)));
        }
    }

    private static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property, boolean inverse) {
        return (BlockState)state.with(property, (Comparable)DebugStickItem.cycle(property.getValues(), state.get(property), inverse));
    }

    private static <T> T cycle(Iterable<T> elements, @Nullable T current, boolean inverse) {
        return inverse ? Util.previous(elements, current) : Util.next(elements, current);
    }

    private static void sendMessage(PlayerEntity player, Text message) {
        ((ServerPlayerEntity)player).sendMessage(message, MessageType.GAME_INFO, Util.NIL_UUID);
    }

    private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
        return property.name(state.get(property));
    }
}

