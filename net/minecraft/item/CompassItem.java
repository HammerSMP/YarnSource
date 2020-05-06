/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterestType;

public class CompassItem
extends Item
implements Vanishable {
    public CompassItem(Item.Settings arg) {
        super(arg);
    }

    public static boolean hasLodestone(ItemStack arg) {
        CompoundTag lv = arg.getTag();
        return lv != null && (lv.contains("LodestoneDimension") || lv.contains("LodestonePos"));
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack arg) {
        return CompassItem.hasLodestone(arg) || super.hasEnchantmentGlint(arg);
    }

    public static Optional<DimensionType> getLodestoneDimension(CompoundTag arg) {
        Identifier lv = Identifier.tryParse(arg.getString("LodestoneDimension"));
        if (lv != null) {
            return Registry.DIMENSION_TYPE.getOrEmpty(lv);
        }
        return Optional.empty();
    }

    @Override
    public void inventoryTick(ItemStack arg, World arg2, Entity arg3, int i, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        if (CompassItem.hasLodestone(arg)) {
            CompoundTag lv = arg.getOrCreateTag();
            if (lv.contains("LodestoneTracked") && !lv.getBoolean("LodestoneTracked")) {
                return;
            }
            Optional<DimensionType> optional = CompassItem.getLodestoneDimension(lv);
            if (optional.isPresent() && optional.get().equals(arg2.dimension.getType()) && lv.contains("LodestonePos") && !((ServerWorld)arg2).getPointOfInterestStorage().method_26339(PointOfInterestType.LODESTONE, NbtHelper.toBlockPos((CompoundTag)lv.get("LodestonePos")))) {
                lv.remove("LodestonePos");
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        BlockPos lv = arg.hit.getBlockPos();
        if (arg.world.getBlockState(lv).isOf(Blocks.LODESTONE)) {
            boolean bl;
            arg.world.playSound(null, lv, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            boolean bl2 = bl = !arg.player.abilities.creativeMode && arg.stack.getCount() == 1;
            if (bl) {
                this.method_27315(arg.world.dimension, lv, arg.stack.getOrCreateTag());
            } else {
                ItemStack lv2 = new ItemStack(Items.COMPASS, 1);
                CompoundTag lv3 = arg.stack.hasTag() ? arg.stack.getTag().copy() : new CompoundTag();
                lv2.setTag(lv3);
                if (!arg.player.abilities.creativeMode) {
                    arg.stack.decrement(1);
                }
                this.method_27315(arg.world.dimension, lv, lv3);
                if (!arg.player.inventory.insertStack(lv2)) {
                    arg.player.dropItem(lv2, false);
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(arg);
    }

    private void method_27315(Dimension arg, BlockPos arg2, CompoundTag arg3) {
        arg3.put("LodestonePos", NbtHelper.fromBlockPos(arg2));
        arg3.putString("LodestoneDimension", DimensionType.getId(arg.getType()).toString());
        arg3.putBoolean("LodestoneTracked", true);
    }

    @Override
    public String getTranslationKey(ItemStack arg) {
        return CompassItem.hasLodestone(arg) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(arg);
    }
}

