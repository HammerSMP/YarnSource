/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class CatSitOnBlockGoal
extends MoveToTargetPosGoal {
    private final CatEntity cat;

    public CatSitOnBlockGoal(CatEntity arg, double d) {
        super(arg, d, 8);
        this.cat = arg;
    }

    @Override
    public boolean canStart() {
        return this.cat.isTamed() && !this.cat.isSitting() && super.canStart();
    }

    @Override
    public void start() {
        super.start();
        this.cat.setInSittingPose(false);
    }

    @Override
    public void stop() {
        super.stop();
        this.cat.setInSittingPose(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.cat.setInSittingPose(this.hasReached());
    }

    @Override
    protected boolean isTargetPos(WorldView arg, BlockPos arg22) {
        if (!arg.isAir(arg22.up())) {
            return false;
        }
        BlockState lv = arg.getBlockState(arg22);
        if (lv.isOf(Blocks.CHEST)) {
            return ChestBlockEntity.getPlayersLookingInChestCount(arg, arg22) < 1;
        }
        if (lv.isOf(Blocks.FURNACE) && lv.get(FurnaceBlock.LIT).booleanValue()) {
            return true;
        }
        return lv.method_27851(BlockTags.BEDS, arg2 -> arg2.method_28500(BedBlock.PART).map(arg -> arg != BedPart.HEAD).orElse(true));
    }
}

