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
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FishBucketItem
extends BucketItem {
    private final EntityType<?> fishType;

    public FishBucketItem(EntityType<?> type, Fluid fluid, Item.Settings settings) {
        super(fluid, settings);
        this.fishType = type;
    }

    @Override
    public void onEmptied(World world, ItemStack stack, BlockPos pos) {
        if (world instanceof ServerWorld) {
            this.spawnFish((ServerWorld)world, stack, pos);
        }
    }

    @Override
    protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }

    private void spawnFish(ServerWorld arg, ItemStack stack, BlockPos pos) {
        Entity lv = this.fishType.spawnFromItemStack(arg, stack, null, pos, SpawnReason.BUCKET, true, false);
        if (lv != null) {
            ((FishEntity)lv).setFromBucket(true);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CompoundTag lv;
        if (this.fishType == EntityType.TROPICAL_FISH && (lv = stack.getTag()) != null && lv.contains("BucketVariantTag", 3)) {
            int i = lv.getInt("BucketVariantTag");
            Formatting[] lvs = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};
            String string = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(i);
            String string2 = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(i);
            for (int j = 0; j < TropicalFishEntity.COMMON_VARIANTS.length; ++j) {
                if (i != TropicalFishEntity.COMMON_VARIANTS[j]) continue;
                tooltip.add(new TranslatableText(TropicalFishEntity.getToolTipForVariant(j)).formatted(lvs));
                return;
            }
            tooltip.add(new TranslatableText(TropicalFishEntity.getTranslationKey(i)).formatted(lvs));
            TranslatableText lv2 = new TranslatableText(string);
            if (!string.equals(string2)) {
                lv2.append(", ").append(new TranslatableText(string2));
            }
            lv2.formatted(lvs);
            tooltip.add(lv2);
        }
    }
}

