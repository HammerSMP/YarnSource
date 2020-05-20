/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ScaffoldingItem
extends BlockItem {
    public ScaffoldingItem(Block arg, Item.Settings arg2) {
        super(arg, arg2);
    }

    @Override
    @Nullable
    public ItemPlacementContext getPlacementContext(ItemPlacementContext arg) {
        Block lv4;
        BlockPos lv = arg.getBlockPos();
        World lv2 = arg.getWorld();
        BlockState lv3 = lv2.getBlockState(lv);
        if (lv3.isOf(lv4 = this.getBlock())) {
            Direction lv6;
            if (arg.shouldCancelInteraction()) {
                Direction lv5 = arg.hitsInsideBlock() ? arg.getSide().getOpposite() : arg.getSide();
            } else {
                lv6 = arg.getSide() == Direction.UP ? arg.getPlayerFacing() : Direction.UP;
            }
            int i = 0;
            BlockPos.Mutable lv7 = lv.mutableCopy().move(lv6);
            while (i < 7) {
                if (!lv2.isClient && !World.method_24794(lv7)) {
                    PlayerEntity lv8 = arg.getPlayer();
                    int j = lv2.getHeight();
                    if (!(lv8 instanceof ServerPlayerEntity) || lv7.getY() < j) break;
                    GameMessageS2CPacket lv9 = new GameMessageS2CPacket(new TranslatableText("build.tooHigh", j).formatted(Formatting.RED), MessageType.GAME_INFO, Util.field_25140);
                    ((ServerPlayerEntity)lv8).networkHandler.sendPacket(lv9);
                    break;
                }
                lv3 = lv2.getBlockState(lv7);
                if (!lv3.isOf(this.getBlock())) {
                    if (!lv3.canReplace(arg)) break;
                    return ItemPlacementContext.offset(arg, lv7, lv6);
                }
                lv7.move(lv6);
                if (!lv6.getAxis().isHorizontal()) continue;
                ++i;
            }
            return null;
        }
        if (ScaffoldingBlock.calculateDistance(lv2, lv) == 7) {
            return null;
        }
        return arg;
    }

    @Override
    protected boolean checkStatePlacement() {
        return false;
    }
}

