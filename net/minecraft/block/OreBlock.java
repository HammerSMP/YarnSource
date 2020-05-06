/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class OreBlock
extends Block {
    public OreBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    protected int getExperienceWhenMined(Random random) {
        if (this == Blocks.COAL_ORE) {
            return MathHelper.nextInt(random, 0, 2);
        }
        if (this == Blocks.DIAMOND_ORE) {
            return MathHelper.nextInt(random, 3, 7);
        }
        if (this == Blocks.EMERALD_ORE) {
            return MathHelper.nextInt(random, 3, 7);
        }
        if (this == Blocks.LAPIS_ORE) {
            return MathHelper.nextInt(random, 2, 5);
        }
        if (this == Blocks.NETHER_QUARTZ_ORE) {
            return MathHelper.nextInt(random, 2, 5);
        }
        if (this == Blocks.NETHER_GOLD_ORE) {
            return MathHelper.nextInt(random, 0, 1);
        }
        return 0;
    }

    @Override
    public void onStacksDropped(BlockState arg, World arg2, BlockPos arg3, ItemStack arg4) {
        int i;
        super.onStacksDropped(arg, arg2, arg3, arg4);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, arg4) == 0 && (i = this.getExperienceWhenMined(arg2.random)) > 0) {
            this.dropExperience(arg2, arg3, i);
        }
    }
}

