/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public class BannerItem
extends WallStandingBlockItem {
    public BannerItem(Block arg, Block arg2, Item.Settings arg3) {
        super(arg, arg2, arg3);
        Validate.isInstanceOf(AbstractBannerBlock.class, (Object)arg);
        Validate.isInstanceOf(AbstractBannerBlock.class, (Object)arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendBannerTooltip(ItemStack arg, List<Text> list) {
        CompoundTag lv = arg.getSubTag("BlockEntityTag");
        if (lv == null || !lv.contains("Patterns")) {
            return;
        }
        ListTag lv2 = lv.getList("Patterns", 10);
        for (int i = 0; i < lv2.size() && i < 6; ++i) {
            CompoundTag lv3 = lv2.getCompound(i);
            DyeColor lv4 = DyeColor.byId(lv3.getInt("Color"));
            BannerPattern lv5 = BannerPattern.byId(lv3.getString("Pattern"));
            if (lv5 == null) continue;
            list.add(new TranslatableText("block.minecraft.banner." + lv5.getName() + '.' + lv4.getName()).formatted(Formatting.GRAY));
        }
    }

    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        BannerItem.appendBannerTooltip(arg, list);
    }
}

