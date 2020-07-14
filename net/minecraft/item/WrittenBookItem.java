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
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WrittenBookItem
extends Item {
    public WrittenBookItem(Item.Settings arg) {
        super(arg);
    }

    public static boolean isValid(@Nullable CompoundTag tag) {
        if (!WritableBookItem.isValid(tag)) {
            return false;
        }
        if (!tag.contains("title", 8)) {
            return false;
        }
        String string = tag.getString("title");
        if (string.length() > 32) {
            return false;
        }
        return tag.contains("author", 8);
    }

    public static int getGeneration(ItemStack stack) {
        return stack.getTag().getInt("generation");
    }

    public static int getPageCount(ItemStack stack) {
        CompoundTag lv = stack.getTag();
        return lv != null ? lv.getList("pages", 8).size() : 0;
    }

    @Override
    public Text getName(ItemStack stack) {
        CompoundTag lv;
        String string;
        if (stack.hasTag() && !ChatUtil.isEmpty(string = (lv = stack.getTag()).getString("title"))) {
            return new LiteralText(string);
        }
        return super.getName(stack);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasTag()) {
            CompoundTag lv = stack.getTag();
            String string = lv.getString("author");
            if (!ChatUtil.isEmpty(string)) {
                tooltip.add(new TranslatableText("book.byAuthor", string).formatted(Formatting.GRAY));
            }
            tooltip.add(new TranslatableText("book.generation." + lv.getInt("generation")).formatted(Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos lv2;
        World lv = context.getWorld();
        BlockState lv3 = lv.getBlockState(lv2 = context.getBlockPos());
        if (lv3.isOf(Blocks.LECTERN)) {
            return LecternBlock.putBookIfAbsent(lv, lv2, lv3, context.getStack()) ? ActionResult.success(lv.isClient) : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        user.openEditBookScreen(lv, hand);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.method_29237(lv, world.isClient());
    }

    public static boolean resolve(ItemStack book, @Nullable ServerCommandSource commandSource, @Nullable PlayerEntity player) {
        CompoundTag lv = book.getTag();
        if (lv == null || lv.getBoolean("resolved")) {
            return false;
        }
        lv.putBoolean("resolved", true);
        if (!WrittenBookItem.isValid(lv)) {
            return false;
        }
        ListTag lv2 = lv.getList("pages", 8);
        for (int i = 0; i < lv2.size(); ++i) {
            LiteralText lv4;
            String string = lv2.getString(i);
            try {
                MutableText lv3 = Text.Serializer.fromLenientJson(string);
                lv3 = Texts.parse(commandSource, lv3, player, 0);
            }
            catch (Exception exception) {
                lv4 = new LiteralText(string);
            }
            lv2.set(i, StringTag.of(Text.Serializer.toJson(lv4)));
        }
        lv.put("pages", lv2);
        return true;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}

