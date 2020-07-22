/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.item;

import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompassItem
extends Item
implements Vanishable {
    private static final Logger field_24670 = LogManager.getLogger();

    public CompassItem(Item.Settings arg) {
        super(arg);
    }

    public static boolean hasLodestone(ItemStack stack) {
        CompoundTag lv = stack.getTag();
        return lv != null && (lv.contains("LodestoneDimension") || lv.contains("LodestonePos"));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return CompassItem.hasLodestone(stack) || super.hasGlint(stack);
    }

    public static Optional<RegistryKey<World>> getLodestoneDimension(CompoundTag tag) {
        return World.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)tag.get("LodestoneDimension")).result();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        if (CompassItem.hasLodestone(stack)) {
            CompoundTag lv = stack.getOrCreateTag();
            if (lv.contains("LodestoneTracked") && !lv.getBoolean("LodestoneTracked")) {
                return;
            }
            Optional<RegistryKey<World>> optional = CompassItem.getLodestoneDimension(lv);
            if (optional.isPresent() && optional.get() == world.getRegistryKey() && lv.contains("LodestonePos") && !((ServerWorld)world).getPointOfInterestStorage().method_26339(PointOfInterestType.LODESTONE, NbtHelper.toBlockPos(lv.getCompound("LodestonePos")))) {
                lv.remove("LodestonePos");
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos lv = context.getBlockPos();
        World lv2 = context.getWorld();
        if (lv2.getBlockState(lv).isOf(Blocks.LODESTONE)) {
            boolean bl;
            lv2.playSound(null, lv, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            PlayerEntity lv3 = context.getPlayer();
            ItemStack lv4 = context.getStack();
            boolean bl2 = bl = !lv3.abilities.creativeMode && lv4.getCount() == 1;
            if (bl) {
                this.method_27315(lv2.getRegistryKey(), lv, lv4.getOrCreateTag());
            } else {
                ItemStack lv5 = new ItemStack(Items.COMPASS, 1);
                CompoundTag lv6 = lv4.hasTag() ? lv4.getTag().copy() : new CompoundTag();
                lv5.setTag(lv6);
                if (!lv3.abilities.creativeMode) {
                    lv4.decrement(1);
                }
                this.method_27315(lv2.getRegistryKey(), lv, lv6);
                if (!lv3.inventory.insertStack(lv5)) {
                    lv3.dropItem(lv5, false);
                }
            }
            return ActionResult.success(lv2.isClient);
        }
        return super.useOnBlock(context);
    }

    private void method_27315(RegistryKey<World> arg, BlockPos arg22, CompoundTag arg3) {
        arg3.put("LodestonePos", NbtHelper.fromBlockPos(arg22));
        World.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, arg).resultOrPartial(((Logger)field_24670)::error).ifPresent(arg2 -> arg3.put("LodestoneDimension", (Tag)arg2));
        arg3.putBoolean("LodestoneTracked", true);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return CompassItem.hasLodestone(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
    }
}

