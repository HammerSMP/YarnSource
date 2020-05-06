/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpkinBlock
extends GourdBlock {
    protected PumpkinBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg22, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        ItemStack lv = arg4.getStackInHand(arg5);
        if (lv.getItem() == Items.SHEARS) {
            if (!arg22.isClient) {
                Direction lv2 = arg6.getSide();
                Direction lv3 = lv2.getAxis() == Direction.Axis.Y ? arg4.getHorizontalFacing().getOpposite() : lv2;
                arg22.playSound(null, arg3, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                arg22.setBlockState(arg3, (BlockState)Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, lv3), 11);
                ItemEntity lv4 = new ItemEntity(arg22, (double)arg3.getX() + 0.5 + (double)lv3.getOffsetX() * 0.65, (double)arg3.getY() + 0.1, (double)arg3.getZ() + 0.5 + (double)lv3.getOffsetZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
                lv4.setVelocity(0.05 * (double)lv3.getOffsetX() + arg22.random.nextDouble() * 0.02, 0.05, 0.05 * (double)lv3.getOffsetZ() + arg22.random.nextDouble() * 0.02);
                arg22.spawnEntity(lv4);
                lv.damage(1, arg4, arg2 -> arg2.sendToolBreakStatus(arg5));
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(arg, arg22, arg3, arg4, arg5, arg6);
    }

    @Override
    public StemBlock getStem() {
        return (StemBlock)Blocks.PUMPKIN_STEM;
    }

    @Override
    public AttachedStemBlock getAttachedStem() {
        return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
    }
}

