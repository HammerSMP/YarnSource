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
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class AirBlockItem
extends Item {
    private final Block block;

    public AirBlockItem(Block arg, Item.Settings arg2) {
        super(arg2);
        this.block = arg;
    }

    @Override
    public String getTranslationKey() {
        return this.block.getTranslationKey();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        super.appendTooltip(arg, arg2, list, arg3);
        this.block.appendTooltip(arg, arg2, list, arg3);
    }
}

