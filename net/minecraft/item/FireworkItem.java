/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkItem
extends Item {
    public FireworkItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World lv = context.getWorld();
        if (!lv.isClient) {
            ItemStack lv2 = context.getStack();
            Vec3d lv3 = context.getHitPos();
            Direction lv4 = context.getSide();
            FireworkRocketEntity lv5 = new FireworkRocketEntity(lv, context.getPlayer(), lv3.x + (double)lv4.getOffsetX() * 0.15, lv3.y + (double)lv4.getOffsetY() * 0.15, lv3.z + (double)lv4.getOffsetZ() * 0.15, lv2);
            lv.spawnEntity(lv5);
            lv2.decrement(1);
        }
        return ActionResult.success(lv.isClient);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isFallFlying()) {
            ItemStack lv = user.getStackInHand(hand);
            if (!world.isClient) {
                world.spawnEntity(new FireworkRocketEntity(world, lv, user));
                if (!user.abilities.creativeMode) {
                    lv.decrement(1);
                }
            }
            return TypedActionResult.method_29237(user.getStackInHand(hand), world.isClient());
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ListTag lv2;
        CompoundTag lv = stack.getSubTag("Fireworks");
        if (lv == null) {
            return;
        }
        if (lv.contains("Flight", 99)) {
            tooltip.add(new TranslatableText("item.minecraft.firework_rocket.flight").append(" ").append(String.valueOf(lv.getByte("Flight"))).formatted(Formatting.GRAY));
        }
        if (!(lv2 = lv.getList("Explosions", 10)).isEmpty()) {
            for (int i = 0; i < lv2.size(); ++i) {
                CompoundTag lv3 = lv2.getCompound(i);
                ArrayList list2 = Lists.newArrayList();
                FireworkChargeItem.appendFireworkTooltip(lv3, list2);
                if (list2.isEmpty()) continue;
                for (int j = 1; j < list2.size(); ++j) {
                    list2.set(j, new LiteralText("  ").append((Text)list2.get(j)).formatted(Formatting.GRAY));
                }
                tooltip.addAll(list2);
            }
        }
    }

    public static enum Type {
        SMALL_BALL(0, "small_ball"),
        LARGE_BALL(1, "large_ball"),
        STAR(2, "star"),
        CREEPER(3, "creeper"),
        BURST(4, "burst");

        private static final Type[] TYPES;
        private final int id;
        private final String name;

        private Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        @Environment(value=EnvType.CLIENT)
        public String getName() {
            return this.name;
        }

        @Environment(value=EnvType.CLIENT)
        public static Type byId(int id) {
            if (id < 0 || id >= TYPES.length) {
                return SMALL_BALL;
            }
            return TYPES[id];
        }

        static {
            TYPES = (Type[])Arrays.stream(Type.values()).sorted(Comparator.comparingInt(type -> type.id)).toArray(Type[]::new);
        }
    }
}

